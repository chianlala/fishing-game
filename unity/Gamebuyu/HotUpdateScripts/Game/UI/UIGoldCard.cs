using com.maple.game.osee.proto;
using com.maple.game.osee.proto.goldenpig;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIGoldCard : MonoBehaviour
    {

        public Button btn_close;
        public Button btn_buy;
        public Button btn_AlreadyLq;
        public Button btn_Lq;
        public Transform AlreadyLingqu;
        void Awake()
        {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_buy = this.transform.Find("bg/Other/btn_buy").GetComponent<Button>();
            btn_AlreadyLq = this.transform.Find("bg/AlreadyLingqu/btn_AlreadyLq").GetComponent<Button>();
            btn_Lq = this.transform.Find("bg/AlreadyLingqu/btn_Lq").GetComponent<Button>();
            AlreadyLingqu = this.transform.Find("bg/AlreadyLingqu");

            // instance = this;
            btn_close.onClick.AddListener(CloseUI);
            btn_buy.onClick.AddListener(() =>
            {
                //NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
                NetMessage.Lobby.Req_BuyMoneyCardRequest();
            });
            btn_Lq.onClick.AddListener(() =>
            {
                NetMessage.Lobby.Req_ReceiveMoneyRequest();
            });
            UEventDispatcher.Instance.AddEventListener(UEventName.MoneyCardBuyInfoResponse, On_MoneyCardBuyInfoResponse);//金币卡购买信息响应
            UEventDispatcher.Instance.AddEventListener(UEventName.MoneyCardBuySuccessResponse, On_MoneyCardBuySuccessResponse);//购买金币卡成功响应
            UEventDispatcher.Instance.AddEventListener(UEventName.ReceiveMoneyResponse, On_ReceiveMoneyResponse);//领取金币卡金币响应
        }


        private void CloseUI()
        {
            UIMgr.CloseUI(UIPath.UIGoldCard);
            if (PlayerData.byGetSpecialPackage)
            {
                UIMgr.CloseUI(UIPath.UIGoldCard);
                UIMgr.ShowUI(UIPath.UIShop);
            }
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.MoneyCardBuyInfoResponse, On_MoneyCardBuyInfoResponse);//金币卡购买信息响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.MoneyCardBuySuccessResponse, On_MoneyCardBuySuccessResponse);//购买金币卡成功响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ReceiveMoneyResponse, On_ReceiveMoneyResponse);//领取金币卡金币响应
        }
        private void OnEnable()
        {
            NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
        }
        /// <summary>
        /// 金币卡购买信息响应
        /// <summary>
        private void On_MoneyCardBuyInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<MoneyCardBuyInfoResponse>();
            if (pack.bought)
            {
                btn_buy.gameObject.SetActive(false);
                AlreadyLingqu.gameObject.gameObject.SetActive(true);
            }
            else
            {
                btn_buy.gameObject.SetActive(true);
                AlreadyLingqu.gameObject.gameObject.SetActive(false);
            }
            if (pack.received)
            {
                btn_AlreadyLq.gameObject.SetActive(true);
                btn_Lq.gameObject.SetActive(false);
            }
            else
            {
                btn_AlreadyLq.gameObject.SetActive(false);
                btn_Lq.gameObject.SetActive(true);
            }
        }
        /// <summary>
        /// 购买金币卡成功响应
        /// <summary>
        private void On_MoneyCardBuySuccessResponse(UEventContext obj)
        {
            var pack = obj.GetData<MoneyCardBuySuccessResponse>();
            MessageBox.Show("购买永久金币卡成功，记得每天来领取哦");
            NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
        }
        /// <summary>
        /// 领取金币卡金币响应
        /// <summary>
        private void On_ReceiveMoneyResponse(UEventContext obj)
        {
            var pack = obj.GetData<ReceiveMoneyResponse>();
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            foreach (var item in pack.itemData)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            tmp.InitKuang2(Dictmp, false, true);
            NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
        }
    }
}