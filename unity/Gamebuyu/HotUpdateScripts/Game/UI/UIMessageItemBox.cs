using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using System.Collections.Generic;
using CoreGame;

namespace Game.UI { 
    public class UIMessageItemBox : MonoBehaviour
    {
        public Text txt_title;
        public Button btn_ok;
        public Button btn_close;

        Action pressOkHandler;
        Action PressCloseHandler;
        GameObjectPool itmePool;
        public Transform itemgo;
        public Transform GridContent;
        public Transform mask;
        private void Awake()
        {
            mask = transform.Find("mask");

            txt_title = this.transform.Find("bg/bg/txt_title").GetComponent<Text>();
            btn_ok = this.transform.Find("bg/bg/btn_ok").GetComponent<Button>();
            btn_close = this.transform.Find("btn_close").GetComponent<Button>();
         
            itemgo = this.transform.Find("bg/bg/Scroll View/Viewport/GridContent/itemgo");
            GridContent = this.transform.Find("bg/bg/Scroll View/Viewport/GridContent");
            mask = this.transform.Find("mask");

            itmePool = new GameObjectPool();
            itmePool.SetTemplete(itemgo.gameObject);
            itmePool.Recycle(itemgo.gameObject);
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


        public void InitItem(Dictionary<int, long> tmp, float isAuotoClose = -1, bool isClose = false, Action callbackok = null, AudioClip audioClip=null,string tittps="恭喜获得")
        {
            SetOkHandler(callbackok);
            foreach (Transform item in GridContent) 
            {
                itmePool.Recycle(item.gameObject);
            }
            foreach (var item in tmp)
            {
               GameObject vartmp = itmePool.Get(GridContent);
                vartmp.transform.Find("img").GetComponent<Image>().sprite = common4.LoadSprite("item/" + item.Key);
                vartmp.transform.Find("img").GetComponent<Image>().SetNativeSize();
                vartmp.transform.Find("Text").GetComponent<Text>().text ="x"+ item.Value.ToString();
            }
            if (isAuotoClose==-1)
            {
                StopCoroutine("CloseThis");            
            }
            else
            {
                StopCoroutine("CloseThis");
                StartCoroutine("CloseThis", isAuotoClose);
            }
            txt_title.text = tittps;
            mask.gameObject.SetActive(isClose);
            //SoundHelper.PlayGetitem();
        }
        public void InitKuang(Dictionary<int, long> tmp, bool isAuotoClose = false, bool isClose = false, Action callbackok = null) 
        {  
            SetOkHandler(callbackok);
            foreach (Transform item in GridContent)
            {
                itmePool.Recycle(item.gameObject.gameObject);
            }
            foreach (var item in tmp)
            {
                GameObject vartmp = itmePool.Get(GridContent);
                if (item.Key== 3)
                {
                    vartmp.transform.Find("img").GetComponent<Image>().sprite = common4.LoadSprite("item/jiangquan2");
                }
                else
                {
                    vartmp.transform.Find("img").GetComponent<Image>().sprite = common4.LoadSprite("item/" + item.Key);
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
            //SoundHelper.PlayGetitem();
        }
        public void InitKuang2(Dictionary<int, long> tmp, bool isAuotoClose = false, bool isClose = false, Action callbackok = null)
        {

            SetOkHandler(callbackok);
            foreach (Transform item in GridContent)
            { 
                itmePool.Recycle(item.gameObject);
            }
            foreach (var item in tmp)
            {
                GameObject vartmp = itmePool.Get(GridContent);
                if (item.Key == 3)
                {
                    vartmp.transform.Find("img").GetComponent<Image>().sprite = common4.LoadSprite("item/jiangquan2");
                }
                else
                {
                    vartmp.transform.Find("img").GetComponent<Image>().sprite = common4.LoadSprite("item/" + item.Key);
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
            //SoundHelper.PlayGetitem();
        }
        void OnEnable() 
        {
       
        }
        IEnumerator CloseThis() 
        {
            yield return new WaitForSeconds(2f);
            UIMgr.CloseUI(UIPath.UIMessageItemBox);
        }
        void OnClickOk()
        {
            UIMgr.CloseUI(UIPath.UIMessageItemBox);

            if (pressOkHandler != null)
                pressOkHandler();
        }
        private void OnClickClose()
        {
            UIMgr.CloseUI(UIPath.UIMessageItemBox);

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
}