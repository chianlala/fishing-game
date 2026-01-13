using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/*
 public Button btn_ok;
 public Button btn_Close;

*/
public class FindCompent : MonoBehaviour
{
    public GameObject _ItemGo;
     
    public InputField _InputIn;
    public InputField _InputOut;
    public Button _Btn;
    private void Awake()
    {
        _Btn.onClick.AddListener(() =>
        {
            StartInput();

        });
    }
    private void Start()
    {
        StartInput();
    }

    string _outstr="";
    void StartInput()
    {
        if (_InputIn.text=="")
        {
            Debug.Log("_InputIn null");
            return;
        }
        _outstr = "";
        string _tstr = _InputIn.text;
        var _Allhang= _tstr.Split(new char[2] { '\r', '\n' });

        foreach (string item in _Allhang)
        {
            if (item=="")
            {
                continue;
            }
            var _str = item;
            _str = _str.Replace("public", "");
            _str = _str.Replace(";", "");
            string strTmp = _str.Trim();

            string[] allstr = strTmp.Split(" ".ToCharArray(), StringSplitOptions.RemoveEmptyEntries);

            string TransType = allstr[0];
            string TransName = allstr[1];



            Transform[] all = _ItemGo.transform.GetComponentsInChildren<Transform>(true);


            string strname = "";
            string varstr = "";
            foreach (var t in all)
            {
                if (TransName == t.name)
                {
                    print(t.name);

                    varstr = DiGuiChazhao(strname, t, _ItemGo.transform);
                    Debug.Log(varstr);
                    break;
                }
            }
            string OutPut = String.Format("{0}=this.transform.Find(\"{1}\").GetComponent<{2}>();", allstr[1], varstr, allstr[0]);
            if (TransType== "Transform")
            {
                OutPut = String.Format("{0}=this.transform.Find(\"{1}\");", allstr[1], varstr);
            }
            if (TransType == "GameObject")
            {
                OutPut = String.Format("{0}=this.transform.Find(\"{1}\").gameObject;", allstr[1], varstr);
            }
            Debug.Log(OutPut);
            _outstr= _outstr +"\n"+ OutPut;
        }
        _InputOut.text = _outstr;
        

    }


    /// <summary>
    /// 递归查找
    /// </summary>
    /// <param name="strname"></param>
    /// <param name="thisGo"></param>
    /// <param name="varparent"></param>
    /// <returns></returns>
    string DiGuiChazhao(string strname, Transform thisGo, Transform varparent)
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

}
