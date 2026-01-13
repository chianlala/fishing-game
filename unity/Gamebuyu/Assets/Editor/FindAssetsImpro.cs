using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.RegularExpressions;
using UnityEditor;
using UnityEngine;

public class FindAssetsImpro
{
    private static Dictionary<string, string> tempGUIDDic;
    [MenuItem("Assets/查找该资源预设引用位置", false, 10)]
    private static void FindAss()
    {
        //建立GUID字典
        if (tempGUIDDic == null)
        {
            tempGUIDDic = new Dictionary<string, string>();
        } 
        tempGUIDDic.Clear();
        //
        string curentGUID = "";
        System.Type type;
        Dictionary<string, string> spriteFileIDDic = null;
        //已经匹配
        List<MatchContent> AlreadyMatchList = new List<MatchContent>();

        Dictionary<string, Object> spriteNmeObjDic = new Dictionary<string, Object>();

        Dictionary<string, List<System.Action>> texturePrintAct = new Dictionary<string, List<System.Action>>();

        string spriteFileName = string.Empty;

        if (EditorSettings.serializationMode != SerializationMode.ForceText)
        {
            EditorSettings.serializationMode = SerializationMode.ForceText;
        }    
        //路径
        string path = AssetDatabase.GetAssetPath(Selection.activeObject);

        string selectionName = Selection.activeObject.name;

        type = Selection.activeObject.GetType();
        //判断类型
        if (type == typeof(UnityEngine.Texture2D) || type == typeof(UnityEngine.Sprite))
        {
            spriteFileIDDic = GetTexture2DSpritfileIDDic(path, out spriteNmeObjDic);
            //类型相等
            if (type == typeof(UnityEngine.Sprite))
            {
                spriteFileName = Selection.activeObject.name;
            }
        }
        //路径是否为null
        if (!string.IsNullOrEmpty(path))
        {
            string guid = AssetDatabase.AssetPathToGUID(path);
            string[] searchInFolders = { "Assets" };
            string[] GUIDS = AssetDatabase.FindAssets("t:Prefab", searchInFolders);
            List<string> fs = new List<string>();
            for (int i = 0; i < GUIDS.Length; i++)
            {
                fs.Add(AssetDatabase.GUIDToAssetPath(GUIDS[i]));
            }
            string[] files = fs.ToArray();
            int startIndex = 0;
            int matchCount = 0;
            EditorApplication.update = delegate ()
            {
                bool isCancel = EditorUtility.DisplayCancelableProgressBar("匹配资源中", files[startIndex], (float)startIndex / (float)files.Length);
                bool isHave = false;
                foreach (var item in AssetDatabase.GetDependencies(files[startIndex]))
                {
                    if (!tempGUIDDic.TryGetValue(item, out curentGUID))
                    {
                        curentGUID = AssetDatabase.AssetPathToGUID(item);
                        tempGUIDDic[item] = curentGUID;
                    }
                    if (string.Equals(guid, curentGUID))
                    {
                        isHave = true;
                        break;
                    }
                }
                if (!isHave)
                {
                    startIndex++;
                }
                else
                {
                    string objTxt = File.ReadAllText(files[startIndex]);
                    if (Regex.IsMatch(objTxt, guid))
                    {
                        string delimiter = string.Empty;
                        for (int i = 0; i < specialCharacters.Length; i++)
                        {
                            if (!Regex.IsMatch(objTxt, "\\" + specialCharacters[i]))
                            {
                                delimiter = specialCharacters[i];
                                break;
                            }
                        }
                        if (string.IsNullOrEmpty(delimiter))
                        {
                            UnityEngine.Debug.LogError("No special symbols to split text.Please add some special symbols to specialCharacters");
                            EditorUtility.ClearProgressBar();
                            EditorApplication.update = null;
                        }
                        string[] objTxtArry = SelectText(objTxt, "---", delimiter);
                        var matchContent = new MatchContent(guid) { type = type };
                        if (spriteFileIDDic != null)
                        {
                            matchContent.spriteFileIDDic = spriteFileIDDic;
                            if (type == typeof(UnityEngine.Sprite)) matchContent.spriteFileId = spriteFileIDDic.FirstOrDefault(q => q.Value == spriteFileName).Key;
                        }
                        var tempList = CheckOut(objTxtArry, matchContent, files[startIndex], ref matchCount, ref texturePrintAct, spriteNmeObjDic);
                        if (tempList != null) AlreadyMatchList.AddRange(tempList);
                    }
                    startIndex++;
                }
                if (isCancel || startIndex >= files.Length)
                {
                    EditorUtility.ClearProgressBar();
                    EditorApplication.update = null;
                    startIndex = 0;
                    if (texturePrintAct.Count > 0)
                    {
                        List<KeyValuePair<string, List<System.Action>>> lst = new List<KeyValuePair<string, List<System.Action>>>(texturePrintAct);
                        lst.Sort((KeyValuePair<string, List<System.Action>> s1, KeyValuePair<string, List<System.Action>> s2) => { return s1.Key.CompareTo(s2.Key); });
                        texturePrintAct.Clear();
                        foreach (var item in lst) foreach (var a in item.Value) a();
                    }
                    Debug.Log(string.Format("匹配结束--<color=red>{0}</color>---引用位置数为--><color=#00FFFFFF>{1}</color>", selectionName, matchCount.ToString()), Selection.activeObject);
                    if (type == typeof(UnityEngine.Texture2D))
                    {
                        Dictionary<string, MatchContent> alReadyDic = new Dictionary<string, MatchContent>();
                        for (int i = 0; i < AlreadyMatchList.Count; i++)
                        {
                            alReadyDic[AlreadyMatchList[i].spriteFileId] = AlreadyMatchList[i];
                        }
                        if (spriteFileIDDic != null)
                        {
                            string pngPath = "";
                            foreach (var item in spriteFileIDDic)
                            {
                                //Debug.Log($"item--->{item.Value}");
                                if (!alReadyDic.ContainsKey(item.Key))
                                {
                                    Debug.Log("未被引用的资源-----------><color=yellow>" + item.Value + "</color>", spriteNmeObjDic[item.Value]);
                                }
                            }
                            AssetDatabase.Refresh();
                        }
                    }
                }
            };
        }
    }

    [MenuItem("Assets/查找该资源预设引用位置", true)]
    static private bool IsFind()
    {
        string path = AssetDatabase.GetAssetPath(Selection.activeObject);
        return string.IsNullOrEmpty(path) ? false : File.Exists(path);
    }
    private static string[] SelectText(string objTxt, string delimiter)
    {
        string tempTxt = objTxt.Replace("---", delimiter);
        return tempTxt.Split(delimiter.ToCharArray(), System.StringSplitOptions.RemoveEmptyEntries);
    }
    private static string[] SelectText(string objTxt, string oldValue, string delimiter)
    {
        string tempTxt = objTxt.Replace(oldValue, delimiter);
        return tempTxt.Split(delimiter.ToCharArray(), System.StringSplitOptions.RemoveEmptyEntries);
    }
    private static List<MatchContent> CheckOut(string[] objectTxtArry, MatchContent matchContent, string filePath, ref int matchCount, ref Dictionary<string, List<System.Action>> texturePrintAct, Dictionary<string, Object> spriteNmeObjDic)
    {
        Dictionary<string, string> keyObjcetVRectOrTrans = new Dictionary<string, string>();
        Dictionary<string, NodeMsg> rectTransOrTransDic = new Dictionary<string, NodeMsg>();
        List<MatchContent> matchList = null;
        foreach (var item in objectTxtArry)
        {
            bool isMatch = Regex.IsMatch(item, matchContent.guid);
            string[] compentTxtArry = item.Split("\n".ToCharArray(), System.StringSplitOptions.RemoveEmptyEntries);
            if (compentTxtArry.Length <= 3) continue;
            if (compentTxtArry[1].ToLower().Contains("Prefab".ToLower())) continue;
            if (compentTxtArry[1].ToLower().Contains("GameObject".ToLower()))
            {
                string gameObjectFileID = compentTxtArry[0].Substring(compentTxtArry[0].IndexOf('&') + 1).Trim();
                string rectTransOrTransFileID = "";
                string m_Name = "";
                for (int i = 2; i < compentTxtArry.Length; i++)
                {
                    if (Regex.IsMatch(compentTxtArry[i], "m_Component"))
                    {
                        string rectTransOrTransFileIDTxt = compentTxtArry[i + 1];
                        rectTransOrTransFileID = rectTransOrTransFileIDTxt.Substring(rectTransOrTransFileIDTxt.LastIndexOf(':') + 1).Trim(' ', '}', '\r', '\n');
                        keyObjcetVRectOrTrans[gameObjectFileID] = rectTransOrTransFileID;
                    }
                    else if (Regex.IsMatch(compentTxtArry[i], "m_Name"))
                    {
                        m_Name = compentTxtArry[i].Substring(compentTxtArry[i].IndexOf(':') + 1).Trim();
                    }
                }
                if (!rectTransOrTransDic.ContainsKey(rectTransOrTransFileID)) rectTransOrTransDic[rectTransOrTransFileID] = new NodeMsg(rectTransOrTransFileID);
                rectTransOrTransDic[rectTransOrTransFileID].gameObjectFileId = gameObjectFileID;
                rectTransOrTransDic[rectTransOrTransFileID].rectTransOrTransFileId = rectTransOrTransFileID;
                rectTransOrTransDic[rectTransOrTransFileID].m_Name = m_Name;
            }
            else if (compentTxtArry[1].ToLower().Contains("RectTransform".ToLower()) || compentTxtArry[1].ToLower().Contains("Transform".ToLower()))
            {
                string rectTransOrTransFileID = compentTxtArry[0].Substring(compentTxtArry[0].IndexOf('&') + 1).Trim();
                if (!rectTransOrTransDic.ContainsKey(rectTransOrTransFileID)) rectTransOrTransDic[rectTransOrTransFileID] = new NodeMsg(rectTransOrTransFileID);
                for (int i = 2; i < compentTxtArry.Length; i++)
                {
                    if (Regex.IsMatch(compentTxtArry[i], "m_Father")) rectTransOrTransDic[rectTransOrTransFileID].m_Father = compentTxtArry[i].Substring(compentTxtArry[i].LastIndexOf(':') + 1).Trim(' ', '}', '\r', '\n');
                }
            }

            if (isMatch)
            {
                if (matchContent.type == typeof(UnityEngine.Sprite))
                {
                    if (!Regex.IsMatch(item, matchContent.spriteFileId)) continue;
                }
                string matchFileId = string.Empty;
                string matchSpriteFileId = string.Empty;
                int matchIndex = 0;
                string matchTxt = "";
                for (int i = 0; i < compentTxtArry.Length; i++)
                {
                    if (Regex.IsMatch(compentTxtArry[i], "m_GameObject")) matchFileId = compentTxtArry[i].Substring(compentTxtArry[i].LastIndexOf(':') + 1).Trim(' ', '}', '\n', '\r');
                    if (Regex.IsMatch(compentTxtArry[i], matchContent.guid))
                    {
                        if (matchContent.type == typeof(UnityEngine.Sprite))
                        {
                            if (!Regex.IsMatch(compentTxtArry[i], matchContent.spriteFileId)) continue;
                        }
                        string str1 = compentTxtArry[i].Substring(compentTxtArry[i].IndexOf("fileID:"));
                        int si = str1.IndexOf(":");
                        int li = str1.IndexOf(",");
                        matchSpriteFileId = str1.Substring(str1.IndexOf(":") + 1, li - si - 1).Trim();
                        matchIndex = i;
                        if (string.IsNullOrEmpty(matchFileId) || matchFileId == "0") Debug.Log(filePath, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(filePath)));
                        else
                        {
                            if (matchList == null) matchList = new List<MatchContent>();
                            matchTxt = compentTxtArry[matchIndex].Trim();
                            matchTxt = matchTxt.Substring(0, matchTxt.IndexOf(":")).Replace("m_", "");
                            matchList.Add(new MatchContent(matchFileId) { spriteFileId = matchSpriteFileId, spriteFileIDDic = matchContent.spriteFileIDDic, matchTxt = matchTxt });
                        }
                    }
                }
                matchCount++;
            }
        }
        if (matchList != null)
        {
            string commonPath = Application.dataPath.Replace("Assets", "");
            for (int i = 0; i < matchList.Count; i++)
            {
                try
                {
                    string sp = GetNodePath(rectTransOrTransDic, keyObjcetVRectOrTrans[matchList[i].guid]).TrimEnd('/');
                    string spriteName = string.Empty;
                    if (!string.IsNullOrEmpty(matchList[i].spriteFileId) && matchList[i].spriteFileIDDic != null)
                    {
                        if (!matchList[i].spriteFileIDDic.ContainsKey(matchList[i].spriteFileId) || string.IsNullOrEmpty(matchList[i].spriteFileIDDic[matchList[i].spriteFileId]))
                        {
                            UnityEngine.Debug.LogWarning("引用的sprite资源丢失-->\n{filePath}<-->{sp}--组件->{matchList[i].matchTxt}", AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(filePath)));
                            continue;
                        }
                        else spriteName = matchList[i].spriteFileIDDic[matchList[i].spriteFileId];
                    }
                    if ((matchContent.type == typeof(UnityEngine.Texture2D) || matchContent.type == typeof(UnityEngine.Sprite)) && !string.IsNullOrEmpty(spriteName))
                    {
                        if (!texturePrintAct.ContainsKey(spriteName))
                        {
                            texturePrintAct[spriteName] = new List<System.Action>
                            {
                                () =>
                                {
                                    Debug.Log("<color=#00FF00>" + spriteName + "--->的引用</color>", spriteNmeObjDic[spriteName]);
                                }
                            };
                        }
                        texturePrintAct[spriteName].Add(() => { Debug.Log(filePath + "<---->" + sp, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(filePath))); });
                    }
                    else
                    {
                        Debug.Log(filePath + "<---->" + sp, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(filePath)));
                    }
                }
                catch (System.Exception e)
                {
                    UnityEngine.Debug.LogError(e.Message);
                    Debug.Log(filePath, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(filePath)));
                }
            }
            return matchList;
        }
        return null;
    }
    private static string GetRelativeAssetsPath(string path)
    {
        return "Assets" + Path.GetFullPath(path).Replace(Path.GetFullPath(Application.dataPath), "").Replace("\\", "/");
    }
    private static string GetNodePath(Dictionary<string, NodeMsg> rectTransOrTransDic, string fileId, string outString = "")
    {
        outString = rectTransOrTransDic[fileId].m_Name + "/" + outString;
        if (rectTransOrTransDic[fileId].m_Father == "0") return outString;
        return GetNodePath(rectTransOrTransDic, rectTransOrTransDic[fileId].m_Father, outString);
    }

    private static string[] specialCharacters = new string[] { "*", "~", "`" };
    static private Dictionary<string, string> GetTexture2DSpritfileIDDic(string path, out Dictionary<string, Object> spriteNmeObjDic)
    {
        Object[] objs = AssetDatabase.LoadAllAssetsAtPath(path);
        spriteNmeObjDic = new Dictionary<string, Object>();
        Dictionary<string, bool> spriteNameDic = new Dictionary<string, bool>();
        for (int i = 0; i < objs.Length; i++)
        {
            if (objs[i].GetType() == typeof(UnityEngine.Sprite))
            {
                spriteNameDic[objs[i].name] = true;
                spriteNmeObjDic[objs[i].name] = objs[i];
            }
        }
        path = path + ".meta";
        path = Path.Combine(Application.dataPath.Replace("/Assets", ""), path);
        path = path.Replace("\\", "/");
        if (!File.Exists(path))
        {
            UnityEngine.Debug.LogError("找不到该图片meta文件----->" + path);
            return null;
        }
        string[] txtArry = File.ReadAllLines(path, System.Text.Encoding.UTF8);
        List<string> txtList = new List<string>();
        txtList.AddRange(txtArry);
        bool isMatch = false;
        bool isOver = false;
        int n = 0;
        bool isMeatNeedReplace = false;
        Dictionary<string, string> spriteFileIDDic = new Dictionary<string, string>();
#if UNITY_2019
        string matchName = "internalIDToNameTable";
        int count = 0;
        string fid = "";
        string spriteName = "";
        List<int> needDeleteLine = new List<int>();
#else
        string matchName = "fileIDToRecycleName";
#endif
        for (int i = 0; i < txtArry.Length; i++)
        {
            if (Regex.IsMatch(txtArry[i], matchName))
            {
                isMatch = true;
                continue;
            }
            if (isMatch)
            {
#if UNITY_2019
                if (count == 0 && !txtArry[i].Contains("- first:"))
                {
                    break;
                }
                count++;
                if (count == 2)
                {
                    fid = txtArry[i].Trim().Split(":".ToCharArray())[1].Trim();
                }
                if (count == 3)
                {
                    count = 0;
                    spriteName = txtArry[i].Trim().Split(":".ToCharArray())[1].Trim();
                    if (spriteNameDic.ContainsKey(spriteName)) spriteFileIDDic[fid] = spriteName;
                    else
                    {
                        string _spriteName = Unicode2String(spriteName.Trim(new char[] { '\"', ' ' }));
                        if (spriteNameDic.ContainsKey(_spriteName)) spriteFileIDDic[fid] = _spriteName;
                        else
                        {
                            if (!isMeatNeedReplace) isMeatNeedReplace = true;
                            needDeleteLine.Add(i - 2);
                            needDeleteLine.Add(i - 1);
                            needDeleteLine.Add(i);
                        }
                    }
                }
#else
                string content = txtArry[i].Trim();
                string s = content.Substring(0, 1);
                if (int.TryParse(s, out n))
                {
                    string[] dicArry = content.Split(":".ToCharArray());
                    string spriteName = dicArry[1].Trim();
                    if (spriteNameDic.ContainsKey(spriteName)) spriteFileIDDic[dicArry[0].Trim()] = spriteName;
                    else
                    {
                        string _spriteName = Unicode2String(spriteName.Trim(new char[] { '\"', ' ' }));
                        if (spriteNameDic.ContainsKey(_spriteName)) spriteFileIDDic[dicArry[0].Trim()] = _spriteName;
                        else
                        {
                            if (!isMeatNeedReplace) isMeatNeedReplace = true;
                            txtList.Remove(txtArry[i]);
                        }
                    }
                }
                else isOver = true;
#endif
            }
            if (isOver) break;
        }
#if UNITY_2019
        if (isMeatNeedReplace)//如果 精灵实际已经没有了，但是在meta文件中还有记录，清理meta文件
        {
            needDeleteLine.Sort((x, y) => { return -x.CompareTo(y); });
            for (int i = 0; i < needDeleteLine.Count; i++)
            {
                txtList.RemoveAt(needDeleteLine[i]);
            }
        }
#endif
        if (isMeatNeedReplace) File.WriteAllLines(path, txtList.ToArray());
        return spriteFileIDDic.Count > 0 ? spriteFileIDDic : null;
    }

    /// <summary>
    /// <summary>
    /// 字符串转Unicode
    /// </summary>
    /// <param name="source">源字符串</param>
    /// <returns>Unicode编码后的字符串</returns>
    public static string String2Unicode(string source)
    {
        byte[] bytes = System.Text.Encoding.Unicode.GetBytes(source);
        System.Text.StringBuilder stringBuilder = new System.Text.StringBuilder();
        for (int i = 0; i < bytes.Length; i += 2)
        {
            stringBuilder.AppendFormat("\\u{0}{1}", bytes[i + 1].ToString("x").PadLeft(2, '0').ToUpper(), bytes[i].ToString("x").PadLeft(2, '0').ToUpper());
        }
        return stringBuilder.ToString();
    }

    /// <summary>
    /// Unicode转字符串
    /// </summary>
    /// <param name="source">经过Unicode编码的字符串</param>
    /// <returns>正常字符串</returns>
    public static string Unicode2String(string source)
    {
        return new Regex(@"\\u([0-9A-F]{4})", RegexOptions.IgnoreCase | RegexOptions.Compiled).Replace(
                     source, x => string.Empty + System.Convert.ToChar(System.Convert.ToUInt16(x.Result("$1"), 16)));
    }

}
public class NodeMsg
{
    public string selfFileId;
    public string gameObjectFileId;
    public string rectTransOrTransFileId;
    public string m_Father;
    public string m_Name;

    public NodeMsg(string selfFileId)
    {
        this.selfFileId = selfFileId;
    }
}
public class MatchContent
{
    public string guid;
    public System.Type type;
    public Dictionary<string, string> spriteFileIDDic;
    public string spriteFileId;
    public string matchTxt;
    public MatchContent(string matchFileId)
    {
        this.guid = matchFileId;
    }
}
