using UnityEngine;
using System.IO;
using System.Collections.Generic;

using System;

public static class CSV
{
  //  static CSV csv;
    private static string strFileName="";//当前加载文档
    public static List<string[]> m_ArrayData=new List<string[]>();//文档数据
    //public static CSV GetInstance()
    //{
    //    if (csv == null)
    //    {
    //        csv = new CSV();
    //    }
    //    return csv;
    //}
   // private CSV() { m_ArrayData = new List<string[]>(); }
    public static string GetString(int row, int col)
    {
        return m_ArrayData[row][col];
    }
    public static string GetString(int row,string strType)
    {
        int nIndex = -1;
        for(int i=0;i< m_ArrayData[0].Length;i++)
        {
            if(strType.ToLower() == m_ArrayData[0][i].ToLower())
            {
                nIndex = i;
                break;
            }
        }
        if(nIndex==-1)
        {
            Debug.Log("CSV配置"+strFileName + "中不包含" + strType);
            return "";
        }
        try
        {
            return m_ArrayData[row][nIndex];
        }
        catch
        {

            Debug.LogError("row"+ row+ "nIndex" + nIndex);
            return "";
        }
   
    }

    public static int GetInt(int row, int col)
    {
        int nRe = 0;
        int.TryParse(GetString(row,col), out nRe);
        return nRe;
    }
    public static int GetInt(int row, string strType)
    {
        int nRe = 0;
        int.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public static long GetLong(int row, int col)
    {
        long nRe = 0;
        long.TryParse(GetString(row, col), out nRe);
        return nRe;
    }
    public static long GetLong(int row, string strType)
    {
        long nRe = 0;
        long.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public static float GetFloat(int row, int col)
    {
        float nRe = 0;
        float.TryParse(GetString(row, col), out nRe);
        return nRe;
    }
    public static float GetFloat(int row, string strType)
    {
        float nRe = 0;
        float.TryParse(GetString(row, strType), out nRe);
        return nRe;
    }

    public static void LoadFile(string path, string fileName, TextAsset binAsset)
    {
        m_ArrayData.Clear();
        strFileName = fileName;
        //读取每一行的内容  
        string[] lineArray = binAsset.text.Split("\r"[0]);
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
    //public static void LoadFileAysn(string path, string fileName, TextAsset binAsset)
    //{
    //    m_ArrayData.Clear();
    //    strFileName = fileName;



    //    //读取每一行的内容  
    //    string[] lineArray = binAsset.text.Split("\r"[0]);
    //    if (lineArray.Length > 2)
    //    {
    //        Debug.Log("读取配置文件" + fileName + "成功");
    //    }
    //    else
    //    {
    //        Debug.Log("读取配置文件" + fileName + "失败");
    //    }

    //    for (int i = 0; i < lineArray.Length - 1; i++)
    //    {
    //        m_ArrayData.Add(GetLineArray(lineArray[i]));
    //    }
        

    //}
    /// <summary>
    /// 去掉," 引起的bug
    /// </summary>
    /// <param name="str"></param>
    /// <returns></returns>
    static string[] GetLineArray(string str)
    {
        if(str.IndexOf("\"")==-1)
        {
            return str.Split(',');
        }

        List<string> listTemp = new List<string>();
        string[] tempArray = str.Split(',');
        string strTemp = "";
        bool bOne = false;
        for(int i=0;i<tempArray.Length;i++)
        {
            if(tempArray[i].IndexOf("\"")>=0)
            {
                if(!bOne)
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
            if(bOne)
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
        for(int i=0;i<listTemp.Count;i++)
        {
            retArray[i] = listTemp[i];
        }
        return retArray;
    }

    /// <summary>
    /// 用于PC包测试配置文件 方便替换 streamAssets目录下 
    /// </summary>
    /// <param name="fileName"></param>
    public static void LoadFileTest(string fileName)
    {
        m_ArrayData.Clear();
        strFileName = fileName;
        StreamReader sr = null;
        try
        {
            sr = File.OpenText(Application.streamingAssetsPath+ "//" + fileName);
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