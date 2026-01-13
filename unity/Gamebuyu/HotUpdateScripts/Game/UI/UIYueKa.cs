using com.maple.game.osee.proto.lobby;
using CoreGame;
using LitJson;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIYueKa : MonoBehaviour
    {
        public Button btn_close;
        public Button btn_ok;
        public Transform Mask;
        public Transform view_PayType;
        public Text txt_Remaining;
        public Button btn_AliPay;
        public Button btn_AliPaySao;
        public Button btn_PayClose;
        public Button btn_WeiChat;
        public Button btn_WeiChatSao;
       // public static UIYueKa instance;
        void Awake()
        {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_ok = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            Mask = this.transform.Find("view_PayType/Mask");
            view_PayType = this.transform.Find("view_PayType");
            txt_Remaining = this.transform.Find("bg/txt_Remaining").GetComponent<Text>();
     
            btn_AliPay = this.transform.Find("view_PayType/Background/Content/btn_AliPay").GetComponent<Button>();
            btn_AliPaySao = this.transform.Find("view_PayType/Background/Content/btn_AliPaySao").GetComponent<Button>();
            btn_PayClose = this.transform.Find("view_PayType/Background/btn_PayClose").GetComponent<Button>();
            btn_WeiChat = this.transform.Find("view_PayType/Background/Content/btn_WeiChat").GetComponent<Button>();
            btn_WeiChatSao = this.transform.Find("view_PayType/Background/Content/btn_WeiChatSao").GetComponent<Button>();
            btn_close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIYueKa);
            });
            btn_ok.onClick.AddListener(() =>
            {
                NetMessage.OseeLobby.Req_BuyShopItemRequest(12);
              
            });
            btn_PayClose.onClick.AddListener(() =>
            {
                view_PayType.gameObject.SetActive(false);
            });

        }

        private void OnDestroy()
        {
        }
        private void OnEnable()
        {
            NetMessage.OseeLobby.Req_GetPayWayRequest();
            ChangeTime();
            EventManager.PropUpdate+= ChangeTime;
        }
        void OnDisable() {
            EventManager.PropUpdate -= ChangeTime;
        }
        DateTime vardate;
        private string timeURL = "http://cgi.im.qq.com/cgi-bin/cgi_svrtime";
        public void ChangeTime()
        {
            System.DateTime dtServer = GameHelper.ConvertJavaTime((double)common.monthCardOverDate);
            long varm = GameHelper.ConvertDataTimeToLong(DateTime.Now);

            if (varm > common.monthCardOverDate) //不是月卡会员
            {
                txt_Remaining.text = "";
            }
            else
            {
                txt_Remaining.text = "到期时间：" + dtServer.ToString("yyyy年MM月dd日");
            }
        }
        private void payState()
        {
            if (common.wxH5 == 0)
            {
                btn_WeiChat.gameObject.SetActive(false);
            }
            else
            {
                btn_WeiChat.gameObject.SetActive(true);
            }
            if (common.wxS == 0)
            {
                btn_WeiChatSao.gameObject.SetActive(false);
            }
            else
            {
                btn_WeiChatSao.gameObject.SetActive(true);
            }
            if (common.zfbH5 == 0)
            {
                btn_AliPay.gameObject.SetActive(false);
            }
            else
            {
                btn_AliPay.gameObject.SetActive(true);
            }
            if (common.zfbS == 0)
            {
                btn_AliPaySao.gameObject.SetActive(false);
            }
            else
            {
                btn_AliPaySao.gameObject.SetActive(true);
            }
        }
        void GetGoldOrderNEW(int type, bool isNei)
        {
        
        }
        //isNei是否为内嵌网页
        void GetGoldOrder(int type, bool isNei)
        {
           
        }
    }

}