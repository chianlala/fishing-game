using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using System.Collections.Generic;

public class UIDuanZhaoItemBox : MonoBehaviour
{
    public Text txt_title;
    public Button btn_ok;
    public Button btn_close; 

    Action pressOkHandler;
    Action PressCloseHandler;
    GameObjectPool itmePool;
    public Transform itme;
    public Transform Grid;
    public Transform mask;
    private void Awake()
    {
        mask = transform.Find("mask");
        itmePool = new GameObjectPool();
        itmePool.SetTemplete(itme.gameObject);
        itmePool.Recycle(itme.gameObject);
    }
    public string Title
    {
        get { return txt_title.text; }
        set { txt_title.text = value; }
    }


    // Use this for initialization
    void Start()
    {
        btn_ok.onClick.AddListener(OnClickOk);
        btn_close.onClick.AddListener(OnClickClose);
    }

    public void InitItem(Dictionary<int, long> tmp, bool isAuotoClose = false, bool isClose = false, Action callbackok = null, AudioClip audioClip=null)
    {
     
       
        SetOkHandler(callbackok);
        foreach (Transform item in Grid) 
        {
            itmePool.Recycle(item.gameObject);
        }
        foreach (var item in tmp)
        {
           GameObject vartmp = itmePool.Get(Grid);
            vartmp.transform.Find("img").GetComponent<Image>().sprite = Resources.Load<Sprite>("item/" + item.Key);
            vartmp.transform.Find("img").GetComponent<Image>().SetNativeSize();
            vartmp.transform.Find("Text").GetComponent<Text>().text ="x"+ item.Value.ToString();
        }
        if (isAuotoClose)
        {      
            StopCoroutine("CloseThis");
            StartCoroutine("CloseThis", 2f);
        }
        else
        {
            StopCoroutine("CloseThis");
        }        
        mask.gameObject.SetActive(isClose);        
    }
    public void InitKuang(Dictionary<int, long> tmp, bool isAuotoClose = false, bool isClose = false, Action callbackok = null) 
    {  
        SetOkHandler(callbackok);
        foreach (Transform item in Grid)
        {
            itmePool.Recycle(item.gameObject);
        }
        foreach (var item in tmp)
        {
            GameObject vartmp = itmePool.Get(Grid);
            if (item.Key== 3)
            {
                vartmp.transform.Find("img").GetComponent<Image>().sprite = Resources.Load<Sprite>("item/jiangquan2");
            }
            else
            {
                vartmp.transform.Find("img").GetComponent<Image>().sprite = Resources.Load<Sprite>("item/" + item.Key);
            }            
            vartmp.transform.Find("img").GetComponent<Image>().SetNativeSize();
            vartmp.transform.Find("Text").GetComponent<Text>().text = "x" + item.Value.ToString();
        }
        if (isAuotoClose)
        {
            StopCoroutine("CloseThis");
            StartCoroutine("CloseThis", 2f);
        }
        else
        {
            StopCoroutine("CloseThis");
        }
        mask.gameObject.SetActive(isClose);
    }
    public void InitKuang2(Dictionary<int, long> tmp, bool isAuotoClose = false, bool isClose = false, Action callbackok = null)
    {

        SetOkHandler(callbackok);
        foreach (Transform item in Grid)
        { 
            itmePool.Recycle(item.gameObject);
        }
        foreach (var item in tmp)
        {
            GameObject vartmp = itmePool.Get(Grid);
            if (item.Key == 3)
            {
                vartmp.transform.Find("img").GetComponent<Image>().sprite = Resources.Load<Sprite>("item/jiangquan2");
            }
            else
            {
                vartmp.transform.Find("img").GetComponent<Image>().sprite = Resources.Load<Sprite>("item/" + item.Key);
            }
            vartmp.transform.Find("img").GetComponent<Image>().SetNativeSize();
            vartmp.transform.Find("Text").GetComponent<Text>().text = "x" + item.Value.ToString();
        }
        if (isAuotoClose)
        {
            StopCoroutine("CloseThis");
            StartCoroutine("CloseThis", 2f);
        }
        else
        {
            StopCoroutine("CloseThis");
        }
        if (isClose)
        {
            btn_close.enabled=false;
        }
        else
        {
            btn_close.enabled = true;
        }
        mask.gameObject.SetActive(isClose);
    }
    void OnEnable() 
    {
       
    }
    IEnumerator CloseThis() 
    {
        yield return new WaitForSeconds(2f);
        gameObject.SetActive(false);
    }
    void OnClickOk()
    {
        gameObject.SetActive(false);
        if (pressOkHandler != null)
            pressOkHandler();
    }
    private void OnClickClose()
    {        
        gameObject.SetActive(false);
        if (PressCloseHandler != null)
            PressCloseHandler();
    }

    public void SetOkHandler(Action callback)
    {
        pressOkHandler = callback;
    }
    public void SetCloseHandler(Action callback)
    {
        PressCloseHandler = callback;
    }
}
