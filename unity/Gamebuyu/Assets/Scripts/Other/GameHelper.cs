using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public abstract class GameHelper
{
    public static void QuitApp()
    {
#if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
#else
        Application.Quit();   
#endif
    }

    /// <summary>
    /// 10进制转换成16进制0x的形式
    /// </summary>
    /// <param name="num"></param>
    /// <returns></returns>
    public static string Convert0x(int num)
    {
        return string.Format("0x{0}", Convert.ToString(num, 16));
    }
    /// <summary>
    /// 将java服务器的毫秒数转换为DateTime类型
    /// </summary>
    /// <param name="millSecond"></param>
    /// <returns></returns>
    public static DateTime ConvertJavaTime(double millSecond)
    {
        //用这种方式消除时区的影响
        DateTime dt = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
        dt = dt.AddMilliseconds(millSecond);

        return dt;
    }
    public static long ConvertDataTimeToLong(DateTime dt)
    {
        DateTime dtStart = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
        TimeSpan toNow = dt.Subtract(dtStart);
        long timeStamp = toNow.Ticks;
        timeStamp = long.Parse(timeStamp.ToString().Substring(0, timeStamp.ToString().Length - 4));
        return timeStamp;
    }
    //发送HTTP POST请求
    public static IEnumerator SendHttpPostRequest(string url, string content, Action<WWW> callback)
    {
        var header = new Dictionary<string, string>();
        header.Add("Content-Type", "application/json");
        WWW www = new WWW(url, System.Text.Encoding.UTF8.GetBytes(content), header);

        yield return www;

        if (callback != null)
            callback.Invoke(www);
    }
    //发送HTTP GET请求
    public static IEnumerator SendHttpGetRequest(string url, Action<WWW> callback)
    {
        WWW www = new WWW(url);

        yield return www;

        if (callback != null)
            callback.Invoke(www);
    }
    public static int GetLocalPos(int absPos, int myPos)
    {
        int localPos = -1;

        if (myPos == 0)
        {
            localPos = 5 + absPos;

            if (localPos > 4)
                localPos -= 5;
        }
        else if (myPos == 1)
        {
            localPos = 4 + absPos;

            if (localPos > 4)
                localPos -= 5;
        }
        else if (myPos == 2)
        {
            localPos = 3 + absPos;

            if (localPos > 4)
                localPos -= 5;
        }
        else if (myPos == 3)
        {
            localPos = 2 + absPos;

            if (localPos > 4)
                localPos -= 5;
        }
        else if (myPos == 4)
        {
            localPos = 1 + absPos;

            if (localPos > 4)
                localPos -= 5;
        } 
        return localPos;
    }
}
