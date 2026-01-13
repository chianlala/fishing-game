using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

public class ChangeCopyName : EditorWindow
{
    //窗口需要一个打开的方式
    //MenuItem属性会在编辑器菜单上创建对应的选项
    //点击选项即可创建窗口
    [MenuItem("Window/轨迹操作")]
    public static void ShowWindow()
    {
        //调用EditorWindow的静态函数GetWindow
        //创建对应的窗口
        //ps:该函数有多个重载
        EditorWindow.GetWindow(typeof(ChangeCopyName));
    }
    //输入文字的内容
    private string text;

    //输入文字的内容
    private string textRote;
    private string textJianJu;

    //深度
    private string textShenDu;
    private string textShenDuJianJu;
    
    Vector3 V3EulerAngles = Vector3.zero;
    Vector3 V3Positon = Vector3.zero;
    Vector3 V3Scale= Vector3.one;

    //位置
    private string textXiaoBiao; 

    private string textScXiaoBiao;
    
    private void OnGUI()
    {
        if (GUILayout.Button("打印深度不一致的轨迹", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            DebugShengDu();
        }

        //窗口绘制的实际代码在这里 
        //输入框控件
        text = EditorGUILayout.TextField("(排序重命名)输入起始数字:", text);

        if (GUILayout.Button("确定改名", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeCopyName();
        }

        textRote = EditorGUILayout.TextField("(旋转物体)输入旋转角:", textRote);
        textJianJu = EditorGUILayout.TextField("(旋转物体)输入旋转角间距:", textJianJu);
        if (GUILayout.Button("确定更改旋转", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeRote();
        }
        textShenDu = EditorGUILayout.TextField("输入第一个轨迹的深度值:", textShenDu);
        textShenDuJianJu = EditorGUILayout.TextField("输入深度间距:", textShenDuJianJu);
        if (GUILayout.Button("确定更改深度正序", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpShenDu();
        }
        if (GUILayout.Button("确定更改深度反序", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpShenDuFanXu();
        }
        if (GUILayout.Button("移除除首尾之外的点(没事别瞎点)", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpMoveShouWei();
        }

        if (GUILayout.Button("使所有轨迹高度值相等(没事别瞎点)", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeGaoDuShouWei(); 
        }
        V3EulerAngles = EditorGUILayout.Vector3Field("(旋转物体):", V3EulerAngles);
        V3Positon = EditorGUILayout.Vector3Field("(平移物体):", V3Positon);
        V3Scale = EditorGUILayout.Vector3Field("(缩放物体):", V3Scale);
        if (GUILayout.Button("确定旋转或平移(此操作不可逆)", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeGuiJi();
        }

        textXiaoBiao = EditorGUILayout.TextField("输入加入的下标:", textXiaoBiao);
        if (GUILayout.Button("轨迹加点(此操作不可逆)", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpPathAddPoint();
        }

        textScXiaoBiao = EditorGUILayout.TextField("输入删除的下标:", textScXiaoBiao);
        if (GUILayout.Button("轨迹删点(此操作不可逆)", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpPathDeletePoint(); 
        }
    }
    public void tmpPathAddPoint()  
    {
        int weiZhi = int.Parse(textXiaoBiao);
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < 1; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                _mfishPath.nodes.Insert(weiZhi, Vector3.zero);
                _mfishPath.nodeCount = _mfishPath.nodeCount+1;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            AssetDatabase.Refresh();
        }
        AssetDatabase.Refresh();
    }
    public void tmpPathDeletePoint()
    {
        int weiZhi = int.Parse(textScXiaoBiao);
        if (weiZhi==0)
        {
            return ;
        }
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < 1; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                if (_mfishPath.nodes.Count>= weiZhi)
                {
                    _mfishPath.nodes.RemoveAt(weiZhi-1);
                    _mfishPath.nodeCount = _mfishPath.nodeCount - 1;
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            AssetDatabase.Refresh();
        }
        AssetDatabase.Refresh();
    }
    public void DebugShengDu() 
    {
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                int jtmp = _mfishPath.nodes.Count - 1;
                float zZhou = 0;
                for (int j = _mfishPath.nodes.Count - 1; j >= 0; j--)
                {
                    if (zZhou==0)
                    {
                        zZhou = _mfishPath.nodes[j].z;
                    }
                    if (zZhou != _mfishPath.nodes[j].z)
                    {
                        Debug.Log("ID"+ objs[i].name);
                    }
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            AssetDatabase.Refresh();
        }
        AssetDatabase.Refresh();
    }
    public  void tmpChangeCopyName()
    {
        var m = int.Parse(text);
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            all.Add(objs[i]);
        }
        all.Sort((a, b) =>
        {
            return a.transform.GetSiblingIndex().CompareTo(b.transform.GetSiblingIndex());            
        });
        for (int i = 0; i < all.Count; i++)
        {           
            all[i].name = m.ToString();
            m++;
        }
        AssetDatabase.Refresh();
    }
    public void tmpChangeRote()
    { 
        var m = int.Parse(textRote); 
        var m2 = int.Parse(textJianJu);
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            all.Add(objs[i]);
        }
        all.Sort((a, b) =>
        {
            return a.transform.GetSiblingIndex().CompareTo(b.transform.GetSiblingIndex());
        });
        for (int i = 0; i < all.Count; i++)
        {
            all[i].transform.eulerAngles=new Vector3(0f,0f, m);
            m=m+ m2;
        }
        AssetDatabase.Refresh();
    }
    public void tmpShenDu() 
    {
        var ShenDu = int.Parse(textShenDu);
        var JianJu = int.Parse(textShenDuJianJu);
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            all.Add(objs[i]);
        }
        all.Sort((a, b) =>
        {
            return a.transform.GetSiblingIndex().CompareTo(b.transform.GetSiblingIndex());
        });
        for (int i = 0; i < all.Count; i++)
        {
            iTweenPath _mfishPath = all[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                for (int j = 0; j < _mfishPath.AllPoint.Count; j++)
                {
                    if (_mfishPath.nodes.Count > j)
                    {
                        _mfishPath.nodes[j] =new Vector3(_mfishPath.nodes[j].x, _mfishPath.nodes[j].y, ShenDu);
                    }
                    else
                    {
                        if (_mfishPath.AllPoint[j] == null)
                        {
                            _mfishPath.AllPoint.RemoveAt(j);
                        }
                    }
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            ShenDu = ShenDu + JianJu;
        }
        AssetDatabase.Refresh();
    }
    public void tmpShenDuFanXu()
    { 
        var ShenDu = int.Parse(textShenDu);
        var JianJu = int.Parse(textShenDuJianJu);
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            all.Add(objs[i]);
        }
        all.Sort((a, b) =>
        {
            return a.transform.GetSiblingIndex().CompareTo(b.transform.GetSiblingIndex());
        });
        //反序操作
        all.Reverse();
        for (int i = 0; i < all.Count; i++)
        {
            iTweenPath _mfishPath = all[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                for (int j = 0; j < _mfishPath.AllPoint.Count; j++)
                {
                    if (_mfishPath.nodes.Count > j)
                    {
                        _mfishPath.nodes[j] = new Vector3(_mfishPath.nodes[j].x, _mfishPath.nodes[j].y, ShenDu);
                    }
                    else
                    {
                        if (_mfishPath.AllPoint[j] == null)
                        {
                            _mfishPath.AllPoint.RemoveAt(j);
                        }
                    }
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            ShenDu = ShenDu + JianJu;
        }
        AssetDatabase.Refresh();
    }
    public void tmpMoveShouWei() 
    {
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                int jtmp = _mfishPath.nodes.Count - 1;
                for (int j = _mfishPath.nodes.Count-1; j >= 0; j--)
                {
                    if (j!=0&& j!= jtmp)
                    {
                        _mfishPath.nodes.RemoveAt(j);
                        _mfishPath.AllPoint.Clear();
                    }
                }
                _mfishPath.nodeCount = 2;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            AssetDatabase.Refresh();
        }
        AssetDatabase.Refresh();
    }
    public void tmpChangeGaoDuShouWei() 
    {
        GameObject[] objs = Selection.gameObjects;
        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                for (int j = _mfishPath.nodes.Count - 1; j >= 0; j--)
                {
                    _mfishPath.nodes[j] =new Vector3(_mfishPath.nodes[j].x, _mfishPath.nodes[0].y,_mfishPath.nodes[j].z);
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            AssetDatabase.Refresh();
        }
        AssetDatabase.Refresh();
    }


    //直接更改轨迹
    public void tmpChangeGuiJi()
    {
        //关闭画线
        ChangePathPoint.SetiTweenPathpathVisibleFalse();
        //生成子物体
        GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            List<Transform> AllPoint = _mfishPath.AllPoint;
             List<Vector3> nodes = _mfishPath.nodes;
            for (int m = 0; m < nodes.Count; m++)
            {
                if (AllPoint != null)
                {
                    if (AllPoint.Count > m)
                    {
                        if (AllPoint[m] == null)
                        {
                            GameObject tGO = GameObject.CreatePrimitive(PrimitiveType.Cube);// new GameObject(m.ToString());
                            tGO.name = m.ToString();
                            tGO.transform.SetParent(objs[i].transform);
                            AllPoint[m] = tGO.transform;
                            AllPoint[m].position = nodes[m];
                        }
                        else
                        {
                            AllPoint[m].position = nodes[m];
                        }
                    }
                    else
                    {
                        GameObject tGO = GameObject.CreatePrimitive(PrimitiveType.Cube);// new GameObject(m.ToString());
                        tGO.name = m.ToString();
                        tGO.transform.SetParent(objs[i].transform);
                        AllPoint.Add(tGO.transform);
                        AllPoint[m].position = nodes[m];
                    }
                }
                else
                {
                    AllPoint = new List<Transform>();
                    GameObject tGO = GameObject.CreatePrimitive(PrimitiveType.Cube);// new GameObject(m.ToString());
                    tGO.name = m.ToString();
                    tGO.transform.SetParent(objs[i].transform);
                    AllPoint.Add(tGO.transform);
                    AllPoint[m].position = nodes[m];
                }
            }

        }
         
        //父物体赋值
        for (int i = 0; i < objs.Length; i++)
        {
            objs[i].transform.localPosition = V3Positon;
            objs[i].transform.localEulerAngles = V3EulerAngles;
            objs[i].transform.localScale = V3Scale;
        }
        varChangeFishPath(objs);
    }
    //保存
    public static void varChangeFishPath(GameObject[] objs)
    {
        //GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                for (int j = 0; j < _mfishPath.AllPoint.Count; j++)
                {
                    if (_mfishPath.nodes.Count > j)
                    {
                        if (_mfishPath.AllPoint[j]!=null)
                        {
                            _mfishPath.nodes[j] = _mfishPath.AllPoint[j].position;
                        }
                    }
                    else
                    {
                        if (_mfishPath.AllPoint[j] == null)
                        {
                            _mfishPath.AllPoint.RemoveAt(j);
                        }

                    }
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();

        ////关闭脚本
        //SetiTweenPathEnableFalse(objs);
        SetiTweenPathpathVisibleTrue(objs);
    }
   
    public static void SetiTweenPathpathVisibleTrue(GameObject[] objs)
    {
        //开启画线
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                _mfishPath.pathVisible = true;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();

        //重置角度
        for (int i = 0; i < objs.Length; i++)
        {
            //删除子物体后重置角度
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {
                for (int m = objs[i].transform.childCount - 1; m >= 0; m--)
                {
                    DestroyImmediate(objs[i].transform.GetChild(m).gameObject);
                }
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
            objs[i].transform.localPosition = Vector3.zero;
            objs[i].transform.localEulerAngles = Vector3.zero;
            objs[i].transform.localScale = Vector3.one;
      
        }
        AssetDatabase.Refresh();
    }
}
