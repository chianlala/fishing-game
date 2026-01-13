using LitJson;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;
using UnityEngine.UI;

[Preserve]
public class Root3D : MonoBehaviour
{
    public Camera UICamera;
    public Camera cam3D;

    public static Root3D Instance;
   
    public Transform Trans_RootBG; 
    public Material IceMaterial;
    //public Transform ICE; 
    public Transform rootPath;
    public Transform rootPathRo;
    public Transform rootFish;
    public Animator AniShaker;
    //隐藏或打开3d对象
    //public Transform rootall;
    public Root3D()
    {
        if (Instance==null)
        {
            Instance = this;
        }    
    }
    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }       
    } 
    public void loadBuyuRoom(bool isOpen)
    {
        //ICE = GameObject.Find("BuyuRoom_ICE").transform;
        cam3D = GameObject.Find("3DCamera").GetComponent<Camera>();
        rootFish = GameObject.Find("rootFish").transform;
        rootPath = GameObject.Find("rootPath").transform;
        rootPathRo = GameObject.Find("rootPathRo").transform;
        AniShaker = cam3D.GetComponent<Animator>();
    }
    public void ShowRootBG(bool isOpen) 
    {
        Trans_RootBG.gameObject.SetActive(isOpen);
    }
    public void ShowAllObject(bool isOpen) 
    { 
        //rootall.gameObject.SetActive(isOpen);
    }
    public void DeleteRootFishChild()
    {
        for (int i = rootFish.childCount-1; i >= 0; i--)
        {
            DestroyImmediate(rootFish.GetChild(i).gameObject);
        }
    }
  
    public Vector2 GetClickPos()
    {
        Vector2 v2 = Input.mousePosition;
        Debug.Log(v2.x + "unity角1度" + v2.y);
        return v2;
    }
    //public bool GetClickKeyCodeO_DOWN() 
    //{
    //    if (Input.GetKeyDown(KeyCode.O))
    //    { 
    //        return true; 
    //    }
    //    return false; 
    //}
    static string StrID = "abcdefghijklmnoprstuvwxyz";
    static string sGetBiaoShiDebug = "";
    public void DebugString(string txt)
    {
        JsonData requestData = new JsonData();
        requestData["message"] = "英雄捕鱼" + GetBiaoShiDebug() + txt + Time();
        StartCoroutine(SendHttpPostRequest("http://10.1.2.201:7777/message/push", requestData.ToJson(), null));
    }
    public string GetBiaoShiDebug()
    {
        if (sGetBiaoShiDebug == null || sGetBiaoShiDebug == "")
        {
            sGetBiaoShiDebug = commonunity.GetString("varDeviceID");
            if (sGetBiaoShiDebug == "")
            {
                var zGetBiaoShiDebug = SystemInfo.deviceUniqueIdentifier;
                if (zGetBiaoShiDebug == SystemInfo.unsupportedIdentifier)
                {
                    zGetBiaoShiDebug = UnityEngine.Random.Range(1000, 99999999).ToString() + StrID[UnityEngine.Random.Range(0, 10)] + System.DateTime.Now.ToUniversalTime().Ticks + StrID[UnityEngine.Random.Range(0, 10)];
                    PlayerPrefs.SetString("varDeviceID", zGetBiaoShiDebug);
                    return " [" + zGetBiaoShiDebug + "] ";
                }
                else
                {
                    PlayerPrefs.SetString("varDeviceID", zGetBiaoShiDebug);
                    return " [" + zGetBiaoShiDebug + "] ";
                }
            }
            else
            {
                return " [" + sGetBiaoShiDebug + "] ";
            }
        }
        else
        {
            return " [" + sGetBiaoShiDebug + "] ";
        }
    }
    string Time()
    {
        return " [" + DateTime.Now.ToUniversalTime().Ticks + "]" + " [" + DateTime.Now + "]";
        // return " [" + DateTime.Now + "]";
    }
    public IEnumerator SendHttpPostRequest(string url, string content, System.Action<WWW> callback)
    {
        var header = new Dictionary<string, string>();
        header.Add("Content-Type", "application/json");
        WWW www = new WWW(url, System.Text.Encoding.UTF8.GetBytes(content), header);
        yield return www;
        if (callback != null)
            callback.Invoke(www);
    }
    public bool GetClickKeyCodeP_DOWN()
    {
        if (Input.GetKeyDown(KeyCode.P))
        {
            return true;
        }
        return false;
    }
    public void showShaker() {
        AniShaker.Play("cattle_shaker");
    }
}
