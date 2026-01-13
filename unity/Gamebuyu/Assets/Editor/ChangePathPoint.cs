using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

public class ChangePathPoint : Editor
{
    //[MenuItem("ChangeFishPath")]

   
    
    //GameObject 会在视窗内显示
    //Assets 资源内显示

    [MenuItem("GameObject/ChangeFishPath", false, 10)]
    public static void tmpChangeFishPath()
    {
        GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                for (int j = 0; j < _mfishPath.AllPoint.Count; j++)
                {
                    if (_mfishPath.nodes.Count > j)
                    {
                        _mfishPath.nodes[j] = _mfishPath.AllPoint[j].position;
                    }
                    else
                    {
                        if (_mfishPath.AllPoint[j]==null)
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
    }


    [MenuItem("GameObject/SetiTweenPathpathVisibleFalse", false, 10)]
    public static void SetiTweenPathpathVisibleFalse()
    {
        GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                _mfishPath.pathVisible = false;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();
    }
    [MenuItem("GameObject/SetiTweenPathpathVisibleTrue", false, 10)]
    public static void SetiTweenPathpathVisibleTrue()
    {
        GameObject[] objs = Selection.gameObjects;
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
    }
    [MenuItem("GameObject/SetiTweenPathEnableTrue", false, 10)]
    public static void SetiTweenPathEnableTrue()
    {
        GameObject[] objs = Selection.gameObjects; 
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                _mfishPath.enabled = true;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();
    }
    [MenuItem("GameObject/SetiTweenPathEnableFalse", false, 10)]
    public static void SetiTweenPathEnableFalse()
    {
        GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++) 
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                _mfishPath.enabled = false;
            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();
    }
    [MenuItem("GameObject/DestoryAllChild", false, 10)]
    public static void DestoryAllChild() 
    {
        GameObject[] objs = Selection.gameObjects;
        for (int i = 0; i < objs.Length; i++)
        {
            iTweenPath _mfishPath = objs[i].GetComponent<iTweenPath>();
            if (_mfishPath != null)
            {

                //_mfishPath.pathVisible = false;
                for (int m = objs[i].transform.childCount-1; m >= 0; m--)
                {
                    DestroyImmediate(objs[i].transform.GetChild(m).gameObject);
                }

            }
            else
            {
                Debug.LogError("无效 因为没有iTweenPath");
            }
        }
        AssetDatabase.Refresh();
    }
}
