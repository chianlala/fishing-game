using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class UIHelper
{
    /// <summary>
    /// 将世界坐标转换为相对于Rectangle的坐标
    /// </summary>
    /// <param name="canvas"></param>
    /// <param name="point"></param>
    /// <returns></returns>
    public static Vector2 WorldPointToLocalPointInRectangle(Canvas canvas, Vector3 point)
    {
        Vector2 screenPos = RectTransformUtility.WorldToScreenPoint(canvas.worldCamera, point);
        Vector2 localPos;
        RectTransformUtility.ScreenPointToLocalPointInRectangle((RectTransform)canvas.transform, screenPos, canvas.worldCamera, out localPos);

        return localPos;
    }
    /// <summary>
    /// 获取带正负的数字字符串
    /// </summary>
    /// <param name="num"></param>
    /// <returns></returns>
    public static string GetSignedString(object num)
    {
        string str = string.Empty;

        if (num is int)
        {
            if ((int)num > 0)
                str = "+" + num.ToString();
            else
                str = num.ToString();
        }
        else if (num is long)
        {
            if ((long)num > 0)
                str = "+" + num.ToString();
            else
                str = num.ToString();
        }
        else if (num is float)
        {
            if ((float)num > 0)
                str = "+" + num.ToString();
            else
                str = num.ToString();
        }
        else if (num is double)
        {
            if ((double)num > 0)
                str = "+" + num.ToString();
            else
                str = num.ToString();
        }
        else
        {
            str = num.ToString();
        }

        return str;
    }

    public static void GetHttpImage(string url, Action<Sprite> callback)
    {
        if (url != "")
            Root3D.Instance.StartCoroutine(dd(url, callback));
    }
    static IEnumerator dd(string url, Action<Sprite> callback, int outTimes=3)
    {
        WWW www = new WWW(url);

        yield return www;

        if (!string.IsNullOrEmpty(www.error))
        {
            //MessageBox.ShowPopMessage(www.error);
            Debug.Log(www.error);
            if(outTimes>0)//超时次数
            {
                outTimes--;
                yield return new WaitForSeconds(3);//等待3秒后重新下载
                Root3D.Instance.StartCoroutine(dd(url, callback,outTimes));
            }
        }
        else
        {
            Sprite sp = GetSprite(www.texture);
            callback(sp);
        }
    }
    public static Sprite GetSprite(Texture2D tex)
    {
        Sprite sp = Sprite.Create(tex, new Rect(0, 0, tex.width, tex.height), Vector2.zero);
        return sp;
    }

    /// <summary>
    /// 获取头像
    /// </summary>
    /// <param name="url"></param>
    /// <param name="callback"></param>
    public static void GetHeadImage(string url, Action<Sprite> callback)
    {
        if (url == "")
            url = "01";

        int nHead = 0;
        int.TryParse(url, out nHead);//转换失败为0
        if(nHead==0)
        {
            Root3D.Instance.StartCoroutine(dd(url, callback));
        }
        else
        {
            Sprite sp = PerfabsMgr.Instance.AllHead[nHead];// Resources.Load<Sprite>(string.Format("head/{0}",nHead));
           // Sprite sp = Resources.Load<Sprite>(string.Format("head/{0}",nHead));
            callback(sp);
        }
    }

  
}
