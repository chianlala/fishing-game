using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

public class replaceNameOperation : EditorWindow
{
    //窗口需要一个打开的方式
    //MenuItem属性会在编辑器菜单上创建对应的选项 
    //点击选项即可创建窗口 
    [MenuItem("Window/替换名字操作")]
    public static void ShowWindow1()
    {
        //调用EditorWindow的静态函数GetWindow
        //创建对应的窗口
        //ps:该函数有多个重载
        EditorWindow.GetWindow(typeof(replaceNameOperation));
    }

    private string xiahuaxianbefore;
    private string xiahuaxianbefore2;

    private void OnGUI()
    {
  

        //窗口绘制的实际代码在这里 
        //输入框控件
        xiahuaxianbefore = EditorGUILayout.TextField("输入需替换字符串:", xiahuaxianbefore);
        xiahuaxianbefore2 = EditorGUILayout.TextField("输入替换后字符串:", xiahuaxianbefore2);

        if (GUILayout.Button("确定替换对象名字符串", GUILayout.Width(200)))
        {
            //关闭通知栏 
            this.RemoveNotification(); 
            tmpChangeStringName();
        }
    }

    public void tmpChangeStringName()
    {
      
        GameObject[] objs = Selection.gameObjects;
      
        for (int i = 0; i < objs.Length; i++)
        {
            string str = objs[i].name;
            string resultA = str.Replace(xiahuaxianbefore, xiahuaxianbefore2);
            objs[i].name = resultA;
        }
        AssetDatabase.Refresh();
    }
}
