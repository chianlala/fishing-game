using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

public class ChangShop : EditorWindow
{
    //窗口需要一个打开的方式
    //MenuItem属性会在编辑器菜单上创建对应的选项 
    //点击选项即可创建窗口
    [MenuItem("Window/改名商城参数操作")]
    public static void ShowWindow()
    {
        //调用EditorWindow的静态函数GetWindow
        //创建对应的窗口
        //ps:该函数有多个重载
        EditorWindow.GetWindow(typeof(ChangShop));
    }


    private void OnGUI()
    {

        if (GUILayout.Button("确定更改商城数据", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeShop();
        }

    }
    /// <summary>
    /// 商城配置信息
    /// </summary>
    public class ShopConfig
    {
        public long id;
        public int gudingId;//商城购买id						
        public string name;//名字
        public int num;//数量
        public int purchase;//购买方式 
        public int rich;//价格 
        public float firstGive;//首次购买额外赠送
        public float followUpGive;//后续赠送 
    }
    public void InitFishConfig()
    {
        dicShopConfig.Clear();
        LoadFile("Config", "cfg_shop");
        //LoadFileTest("cfg_monster.txt");
        for (int i = 2; i < m_ArrayData.Count; i++)
        {
            ShopConfig fc = new ShopConfig();
            fc.id = GetLong(i, "id");
            fc.name = GetString(i, "name");
            fc.gudingId = GetInt(i, "gudingId");
            fc.num = GetInt(i, "num");
            fc.purchase = GetInt(i, "purchase");         
            fc.rich = GetInt(i, "rich");
            fc.firstGive = GetFloat(i, "firstGive");
            fc.followUpGive = GetFloat(i, "followUpGive");
            if (dicShopConfig.ContainsKey(fc.gudingId))
            {
                dicShopConfig[fc.gudingId] =fc;

            }
            else
            {
                dicShopConfig.Add(fc.gudingId, fc);
            }
       
        }
    }
    public static Dictionary<long, ShopConfig> dicShopConfig = new Dictionary<long, ShopConfig>();//鱼摆摆的配置信息
    public void tmpChangeShop()
    {
   
        GameObject[] objs = Selection.gameObjects;
        if (objs.Length>1)
        {
            return;
        }
        InitFishConfig();
        if (objs[0].name =="UIShop")
        {
            var mAll = objs[0].transform.Find("bg/view_gold/Viewport/view_gold");
            for (int i = 0; i < mAll.childCount; i++)
            {
                Transform nowChosse= mAll.GetChild(i);
                foreach (var item in dicShopConfig)
                {
                    if (item.Key.ToString()== nowChosse.name)//id相等
                    {
                        nowChosse.Find("Image/txt_name").GetComponent<Text>().text = item.Value.name;
                        nowChosse.Find("Image/txt_goldnum").GetComponent<Text>().text = (item.Value.num /10000)+"万金币"; 
                        nowChosse.Find("transdown/txt_price").GetComponent<Text>().text = item.Value.rich.ToString();
                    }
                }
            }
            var mAlldiamond = objs[0].transform.Find("bg/view_diamond/Viewport/view_diamond");
            for (int i = 0; i < mAlldiamond.childCount; i++)
            {
                Transform nowChosse = mAlldiamond.GetChild(i);
                foreach (var item in dicShopConfig)
                {
                    if (item.Key.ToString() == nowChosse.name)//id相等
                    {
                        nowChosse.Find("Image/txt_name").GetComponent<Text>().text = item.Value.name;
                        nowChosse.Find("Image/txt_num").GetComponent<Text>().text = "x" + item.Value.num.ToString();
                        nowChosse.Find("transdown/txt_price").GetComponent<Text>().text = item.Value.rich.ToString();
                    }
                }
            }
            var mAllpaotai = objs[0].transform.Find("bg/view_paotai/Viewport/view_paotai");
            for (int i = 0; i < mAllpaotai.childCount; i++)
            {
                Transform nowChosse = mAllpaotai.GetChild(i);
                foreach (var item in dicShopConfig)
                {
                    if (item.Key.ToString() == nowChosse.name)//id相等
                    {
                        nowChosse.Find("Image/txt_name").GetComponent<Text>().text = item.Value.name;
                        nowChosse.Find("Image/txt_num").GetComponent<Text>().text = "x" + item.Value.num.ToString();
                        nowChosse.Find("transdown/txt_price").GetComponent<Text>().text = item.Value.rich.ToString();
                    }
                }
            }
            var mAllskill = objs[0].transform.Find("bg/view_skill/Viewport/view_skill");
            for (int i = 0; i < mAllskill.childCount; i++)
            {
                Transform nowChosse = mAllskill.GetChild(i);
                foreach (var item in dicShopConfig)
                {
                    if (item.Key.ToString() == nowChosse.name)//id相等
                    {
                        nowChosse.Find("Image/txt_name").GetComponent<Text>().text = item.Value.name;
                        nowChosse.Find("Image/txt_num").GetComponent<Text>().text = "x" + item.Value.num.ToString();
                        nowChosse.Find("transdown/txt_price").GetComponent<Text>().text =item.Value.rich.ToString();
                    }
                }
            }
          
        }
        AssetDatabase.Refresh();
    }
    //csv
    //static CSV csv;
    private string strFileName = "";//当前加载文档
    public  List<string[]> m_ArrayData=new List<string[]>();//文档数据
    //public static CSV GetInstance()
    //{
    //    if (csv == null)
    //    {
    //        csv = new CSV();
    //    }
    //    return csv;
    //}
    //private CSV() { m_ArrayData = new List<string[]>(); }
    public string GetString(int row, int col)
    {
        return m_ArrayData[row][col];
    }
    public string GetString(int row, string strType)
    {
        int nIndex = -1;
        for (int i = 0; i < m_ArrayData[0].Length; i++)
        {
            if (strType.ToLower() == m_ArrayData[0][i].ToLower())
            {
                nIndex = i;
                break;
            }
        }
        if (nIndex == -1)
        {
            Debug.Log("CSV配置" + strFileName + "中不包含" + strType);
            return "";
        }
        try
        {
            return m_ArrayData[row][nIndex];
        }
        catch
        {

            Debug.LogError("row" + row + "nIndex" + nIndex);
            return "";
        }

    }

    public int GetInt(int row, int col)
    {
        int nRe = 0;
        int.TryParse(GetString(row, col), out nRe);
        return nRe;
    }
    public int GetInt(int row, string strType)
    {
        int nRe = 0;
        int.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public long GetLong(int row, int col)
    {
        long nRe = 0;
        long.TryParse(GetString(row, col), out nRe);
        return nRe;
    }
    public long GetLong(int row, string strType)
    {
        long nRe = 0;
        long.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public float GetFloat(int row, int col)
    {
        float nRe = 0;
        float.TryParse(GetString(row, col), out nRe);
        return nRe;
    }
    public float GetFloat(int row, string strType)
    {
        float nRe = 0;
        float.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public void LoadFile(string path, string fileName)
    {
        m_ArrayData.Clear();
        strFileName = fileName;

         path = Application.dataPath + "/NotUseConfig/cfg_shop.txt";
        //读取每一行的内容  
        string[] lineArray = File.ReadAllLines(path);
        if (lineArray.Length > 2)
        {
            Debug.Log("读取配置文件" + fileName + "成功");
        }
        else
        {
            Debug.Log("读取配置文件" + fileName + "失败");
        }

        for (int i = 0; i < lineArray.Length - 1; i++)
        {
            m_ArrayData.Add(GetLineArray(lineArray[i]));
        }
    }

    /// <summary>
    /// 去掉," 引起的bug
    /// </summary>
    /// <param name="str"></param>
    /// <returns></returns>
    string[] GetLineArray(string str)
    {
        if (str.IndexOf("\"") == -1)
        {
            return str.Split(',');
        }

        List<string> listTemp = new List<string>();
        string[] tempArray = str.Split(',');
        string strTemp = "";
        bool bOne = false;
        for (int i = 0; i < tempArray.Length; i++)
        {
            if (tempArray[i].IndexOf("\"") >= 0)
            {
                if (!bOne)
                {
                    bOne = true;
                }
                else
                {
                    bOne = false;
                    strTemp += tempArray[i].Trim('\"');
                    listTemp.Add(strTemp);
                    strTemp = "";
                    continue;
                }
            }
            if (bOne)
            {
                strTemp += tempArray[i].Trim('\"') + ",";
            }
            else
            {
                listTemp.Add(tempArray[i]);
            }
        }

        string[] retArray = new string[listTemp.Count];
        //将listTemp转换为[]
        for (int i = 0; i < listTemp.Count; i++)
        {
            retArray[i] = listTemp[i];
        }
        return retArray;
    }

    /// <summary>
    /// 用于PC包测试配置文件 方便替换 streamAssets目录下 
    /// </summary>
    /// <param name="fileName"></param>
    public void LoadFileTest(string fileName)
    {
        m_ArrayData.Clear();
        strFileName = fileName;
        StreamReader sr = null;
        try
        {
            sr = File.OpenText(Application.streamingAssetsPath + "//" + fileName);
            Debug.Log("读取配置文件" + fileName + "成功");
        }
        catch
        {
            Debug.Log("读取配置文件" + fileName + "失败");
            return;
        }
        string line;
        while ((line = sr.ReadLine()) != null)
        {
            m_ArrayData.Add(line.Split(','));
        }
        sr.Close();
        sr.Dispose();
    }
}

