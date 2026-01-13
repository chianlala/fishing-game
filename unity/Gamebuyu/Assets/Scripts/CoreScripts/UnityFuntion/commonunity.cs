using NetLib;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.Scripting;

[Preserve]
public static class commonunity 
{
    public static int nNowSocketID = 0;
    /// <summary>
    ///  连接错误码 
    /// </summary>
    public static int isConnectEorro = 0;
    /// <summary>
    /// 连接次数
    /// </summary>
    public static int nConnectTimes = 0;
    /// <summary>
    /// 是否已经自动登录
    /// </summary>
    public static bool isAlreadyLogin = false;

    public static bool ConnectReLogin = false;
    public static bool SpecialLogin = false;
    public static bool ConnectSuccess = false;

    /// <summary>
    /// 登录方式 1 不登录  2 点击登录等特殊登录  3 重连登录
    /// </summary>
    public static int nLoginWay = 0;

    public static bool bWaiting = false;

    public static List<string> ListLog = new List<string>();
    public static Dictionary<int, Type> msgDic = new Dictionary<int, Type>();
    public static Queue<NetMsgPack> _msgQueue = new Queue<NetMsgPack>();
    public static Vector2 GetClickPos()
    {
        //var ptTouch = Root3D.Instance.cam3D.ScreenToWorldPoint(Input.mousePosition);
        //var mGo = GameObject.CreatePrimitive(PrimitiveType.Capsule);
        //mGo.transform.position = new Vector3(ptTouch.x, 0, ptTouch.z);
        //mGo.transform.SetParent(Root3D.Instance.rootFish);
        //mGo.transform.localScale = Vector3.one * 10f;
        return Input.mousePosition;
    }
    public static String GetString(string str)
    {
        return PlayerPrefs.GetString(str, "");
    }
    public static void SetMainColor(Material Mat,Color color)  
    {
        Mat.SetColor("_Color", color);
    }
    public static long[] GetArryLong(Dictionary<long, long> myDic)
    {
        return myDic.Keys.ToArray();
    }
    public static int[] GetArryInt(Dictionary<int, long> myDic)
    {
        return myDic.Keys.ToArray();
    }
    //发送HTTP POST请求
    public static IEnumerator SendHttpPostRequest(string url, string content, Action<string,string> callback)
    {
        var header = new Dictionary<string, string>();
        header.Add("Content-Type", "application/json");
        WWW www = new WWW(url, System.Text.Encoding.UTF8.GetBytes(content), header);

  
        yield return www;

        Debug.Log("回调：" + www.error);
        Debug.Log("回调：" + www.text);
        if (callback != null)

            callback.Invoke(www.error,www.text);
    }

    private static string GetBasePath()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            return Application.streamingAssetsPath + "/";
        }

        if (Application.platform == RuntimePlatform.WindowsPlayer ||
            Application.platform == RuntimePlatform.WindowsEditor)
        {
            return "file:///" + Application.streamingAssetsPath + "/";
        }

        return "file://" + Application.streamingAssetsPath + "/";
    }
    public static string StartFile()
    {
        string localPath = GetBasePath() + "qudao.txt";
        WWW www = new WWW(localPath);
        if (www.error != null)
        {
            Debug.LogError("error while reading files : " + localPath);
            return ""; //读取文件出错
        }
        while (!www.isDone) { }
        Debug.Log("File content :  " + www.text);//www下面还有获取字节数组的属性
        return www.text;
    }
}
