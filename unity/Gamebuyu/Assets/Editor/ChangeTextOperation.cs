using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

public class ChangeTextOperation : EditorWindow
{
    //窗口需要一个打开的方式
    //MenuItem属性会在编辑器菜单上创建对应的选项 
    //点击选项即可创建窗口 
    [MenuItem("Window/改Text文本操作")]
    public static void ShowWindow1()
    {
        //调用EditorWindow的静态函数GetWindow
        //创建对应的窗口
        //ps:该函数有多个重载
        EditorWindow.GetWindow(typeof(ChangeTextOperation));
    }
    //输入文字的内容
    private string changenametext1;


    private string xiahuaxianbefore;
    private string xiahuaxianbefore2;
    private string xiahuaxianbanck; 

    private void OnGUI()
    {
        //窗口绘制的实际代码在这里 
        //输入框控件
        changenametext1 = EditorGUILayout.TextField("(排序重命名)输入起始数字:", changenametext1);

        if (GUILayout.Button("确定改名", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
            tmpChangeCopyName();
        }


        //窗口绘制的实际代码在这里 
        //输入框控件
        xiahuaxianbefore = EditorGUILayout.TextField("输入起始字符串:", xiahuaxianbefore);
        xiahuaxianbefore2 = EditorGUILayout.TextField("输入起始中文字符串:", xiahuaxianbefore2);
        xiahuaxianbanck = EditorGUILayout.TextField("输入起始数字:", xiahuaxianbanck);

        if (GUILayout.Button("确定根据字符串改名", GUILayout.Width(200)))
        {
            //关闭通知栏 
            this.RemoveNotification(); 
            tmpChangeStringName();
        }
    }
    public  void tmpChangeCopyName()
    {
        var m = int.Parse(changenametext1);
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
    public void tmpChangeStringName()
    {
        var m = int.Parse(xiahuaxianbanck);
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
            all[i].name = xiahuaxianbefore + m.ToString();
            all[i].transform.GetChild(0).GetComponent<Text>().text = xiahuaxianbefore2 + m.ToString();
            m++;
        }
        AssetDatabase.Refresh();
    }
}
