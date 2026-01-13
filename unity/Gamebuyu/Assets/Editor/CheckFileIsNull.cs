using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

public class CheckFileIsNull : EditorWindow
{
    //窗口需要一个打开的方式
    //MenuItem属性会在编辑器菜单上创建对应的选项 
    //点击选项即可创建窗口
    [MenuItem("Window/检查文件操作")]
    public static void ShowWindow()
    {
        
        EditorWindow.GetWindow(typeof(CheckFileIsNull));
    }


    private void OnGUI()
    {

        if (GUILayout.Button("确定检查", GUILayout.Width(200)))
        {
            //关闭通知栏
            this.RemoveNotification();
        }
    }
}
