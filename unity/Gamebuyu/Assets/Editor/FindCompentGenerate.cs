using System;
using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;

public class FindCompentGenerate
{
    [MenuItem("Tools/FindCompent")] 
    
    public static void FindCompent()
    {
        GameObject[] objs = Selection.gameObjects;

        if (null == objs || objs.Length < 1)

        {

            Debug.LogError("没有选中prefab");

            return;

        }


        
        for (int i = 0; i < objs.Length; i++)
        {

            string _AllString = "";
            Button[] allbtn = objs[i].GetComponentsInChildren<Button>(true);
            if (allbtn.Length>0)
            {
                //声明
                for (int x = 0; x < allbtn.Length; x++)
                {
                    string str = "private Button " + allbtn[x].name +";";

                    _AllString = _AllString + "\n" + str;
                }
                for (int x = 0; x < allbtn.Length; x++)
                {
                    string strname = "";
                    string varstr = DiGuiChazhao(strname, allbtn[x].transform, objs[i].transform);
                    string str = allbtn[x].name + "= this.transform.Find(\""+varstr+"\").GetComponent<Button>();";
                    _AllString = _AllString + "\n" + str;
                }
                for (int x = 0; x < allbtn.Length; x++)
                {
                    allbtn[x].onClick.AddListener(() => { });
                    string str = allbtn[x].name+ ".onClick.AddListener(() => { });";
                    _AllString = _AllString + "\n" + str;
                }

          
            }

            Debug.Log(_AllString);
        }

    }

    /// <summary>
    /// 递归查找
    /// </summary>
    /// <param name="strname"></param>
    /// <param name="thisGo"></param>
    /// <param name="varparent"></param>
    /// <returns></returns>
    static  string DiGuiChazhao(string strname, Transform thisGo, Transform varparent)
    {
        if (thisGo.name == varparent.name)
        {
            return strname;
        }
        else
        {
            if (strname == "")
            {
                strname = thisGo.name;
            }
            else
            {
                strname = thisGo.name + "/" + strname;
            }
            return DiGuiChazhao(strname, thisGo.parent, varparent);
        }
    }


    /// <summary>
    /// 递归查找 
    /// </summary>
    /// <param name="strname"></param>
    /// <param name="thisGo"></param>
    /// <param name="varparent"></param>
    /// <returns></returns>
    static string DiGuiChazhao2(string strname, Transform thisGo)
    {
        var index = thisGo.name.IndexOf("UI");
        if (index>=0)
        {
            return strname;
        }
        else
        {
            if (strname == "")
            {
                strname = thisGo.name;
            }
            else
            {
                strname = thisGo.name + "/" + strname;
            }
            return DiGuiChazhao2(strname, thisGo.parent);
        }
    }
    private static string _AString;

  
    [MenuItem("GameObject/ChooseTwoGenerate/Button", priority = 0)]
    public static void CreatChoose001() {
        
        GameObject[] allobjs = Selection.gameObjects;
        for (int i = 0; i < allobjs.Length; i++)
        {
            _AString = "";
            GameObject objs = allobjs[i];
            if (objs==null)
            {
                Debug.LogError("为null");
                return;
            }

            //声明
            string str1 = "private Button " + objs.name + ";";
            _AString = _AString + "\n" + str1;


            //路径
            string strname = "";
            string varstr = DiGuiChazhao2(strname, objs.transform);
            string str2 = objs.name + "= this.transform.Find(\"" + varstr + "\").GetComponent<Button>();";
            _AString = _AString + "\n" + str2;


            string str = objs.name + ".onClick.AddListener(() => { });";
            _AString = _AString + "\n" + str;
            Debug.Log(_AString);
        }
      


    }
    [MenuItem("GameObject/ChooseTwoGenerate/Text", priority = 0)]
    public static void CreatChoose002()
    {
        _AString = "";
        GameObject[] objs = Selection.gameObjects;


        if (null == objs || objs.Length != 1)
        {

            Debug.LogError("只能选中一个物体");
            return;
        }
        //声明
        string str1 = "private Text " + objs[0].name +";";
        _AString = _AString + "\n" + str1;


        //路径
        string strname = "";
        string varstr = DiGuiChazhao2(strname, objs[0].transform);
        string str2 = objs[0].name + "= this.transform.Find(\"" + varstr + "\").GetComponent<Text>();";
        _AString = _AString + "\n" + str2;

        Debug.Log(_AString);

    }

    [MenuItem("GameObject/ChooseTwoGenerate/Animation", priority = 0)]
    public static void CreatChoose003()
    {
        _AString = "";
        GameObject[] objs = Selection.gameObjects;


        if (null == objs || objs.Length != 1)
        {

            Debug.LogError("只能选中一个物体");
            return;
        }
        //声明
        string str1 = "private Animation " + objs[0].name +";";
        _AString = _AString + "\n" + str1;


        //路径
        string strname = "";
        string varstr = DiGuiChazhao2(strname, objs[0].transform);
        string str2 = objs[0].name + "= this.transform.Find(\"" + varstr + "\").GetComponent<Animation>();";
        _AString = _AString + "\n" + str2;

        Debug.Log(_AString);


    }
    
    [MenuItem("GameObject/ChooseTwoGenerate/InputField", priority = 0)]
    public static void CreatChoose004()
    {
        _AString = "";
        GameObject[] objs = Selection.gameObjects;


        if (null == objs || objs.Length != 1)
        {

            Debug.LogError("只能选中一个物体");
            return;
        }
        //声明
        string str1 = "private InputField " + objs[0].name +";";
        _AString = _AString + "\n" + str1;


        //路径
        string strname = "";
        string varstr = DiGuiChazhao2(strname, objs[0].transform);
        string str2 = objs[0].name + "= this.transform.Find(\"" + varstr + "\").GetComponent<InputField>();";
        _AString = _AString + "\n" + str2;

        Debug.Log(_AString);
    }
    
    [MenuItem("GameObject/ChooseTwoGenerate/Transform", priority = 0)]
    public static void CreatChoose007()
    {
        _AString = "";
        GameObject[] objs = Selection.gameObjects;


        if (null == objs || objs.Length != 1)
        {

            Debug.LogError("只能选中一个物体");
            return;
        }
        //声明
        string str1 = "private Transform " + objs[0].name +";";
        _AString = _AString + "\n" + str1;


        //路径
        string strname = "";
        string varstr = DiGuiChazhao2(strname, objs[0].transform);
        string str2 = objs[0].name + "= this.transform.Find(\"" + varstr + "\");";
        _AString = _AString + "\n" + str2;

        Debug.Log(_AString);
    

    }
}
