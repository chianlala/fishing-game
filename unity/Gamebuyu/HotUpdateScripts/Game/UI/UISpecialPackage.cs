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
    public class UISpecialPackage : MonoBehaviour
    {

        public Button btnClose;
        //public Button left;
        //public Button right;
        public int NowCrue = 0;
        //public Transform[] TransNow;
        public Button[] btn_buy=new Button[6];
        public Transform[] Trans_YiGou=new Transform[6];
        void Awake()
        {
            btnClose = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_buy[0] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item0/Button").GetComponent<Button>();
            btn_buy[1] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item1/Button").GetComponent<Button>();
            btn_buy[2] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item2/Button").GetComponent<Button>();
            btn_buy[3] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item3/Button").GetComponent<Button>();
            btn_buy[4] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item4/Button").GetComponent<Button>();
            btn_buy[5] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item5/Button").GetComponent<Button>();

            Trans_YiGou[0] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item0/btn_yg");
            Trans_YiGou[1] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item1/btn_yg");
            Trans_YiGou[2] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item2/btn_yg");
            Trans_YiGou[3] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item3/btn_yg");
            Trans_YiGou[4] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item4/btn_yg");
            Trans_YiGou[5] = this.transform.Find("bg/itembg0/Scroll View/Viewport/Content/item5/btn_yg");
            btnClose.onClick.AddListener(CloseUI);

            for (int i = 0; i < btn_buy.Length; i++)
            {
                int varM = i;
                btn_buy[varM].onClick.AddListener(() =>
                {
                    //请求
                    NetMessage.Lobby.Req_BuyOnceBagRequest(1110 + varM);
                });
            }
            UEventDispatcher.Instance.AddEventListener(UEventName.OnceBagBuyInfoResponse, On_OnceBagBuyInfoResponse);//限次礼包购买信息响应
            UEventDispatcher.Instance.AddEventListener(UEventName.OnceBagBuySuccessResponse, On_OnceBagBuySuccessResponse);//限次礼包购买成功响应
        }
        void Start() {
         
        }

        private void OnDisable()
        {
            System.GC.Collect();
        }

        private void CloseUI()
        {
            UIMgr.CloseUI(UIPath.UISpecialPackage);
            //if (PlayerData.byGetSpecialPackage)
            //{
            //    //   //全买了
            //    //common.DailyBag = true;
            //    //NetMessage.OseeLobby.Req_DailyBagBuyInfoRequest();
            //    PlayerData.GoldCard = true;
            //    NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
            //}
        }
        private void OnEnable()
        {
            NetMessage.Lobby.Req_OnceBagBuyInfoRequest();
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.OnceBagBuyInfoResponse, On_OnceBagBuyInfoResponse);//限次礼包购买信息响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.OnceBagBuySuccessResponse, On_OnceBagBuySuccessResponse);//限次礼包购买成功响应
        }
        /// <summary>
        /// 限次礼包购买信息响应
        /// <summary>
        private void On_OnceBagBuyInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<OnceBagBuyInfoResponse>();
            for (int i = 0; i < Trans_YiGou.Length; i++)
            {
                Trans_YiGou[i].gameObject.SetActive(false);
            }
            for (int m = 0; m < 6; m++)
            {
                int varm = 0;
                for (int i = 0; i < pack.buyItems.Count; i++)
                {
                    int buyItem = pack.buyItems[i] - 1110;
                    if (buyItem >= 0 && buyItem <= 6)
                    {
                        if (m == buyItem)
                        {
                            varm++;
                        }
                    }
                }
                if (varm >= 5)
                {
                    Trans_YiGou[m].gameObject.SetActive(true);
                }
                else
                {
                    Trans_YiGou[m].gameObject.SetActive(false);
                }

            }

        }

        /// <summary>
        /// 限次礼包购买成功响应
        /// <summary>
        private void On_OnceBagBuySuccessResponse(UEventContext obj)
        {
            var pack = obj.GetData<OnceBagBuySuccessResponse>();
            //pack.itemData[0].itemId
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            foreach (var item in pack.itemData)
            {
                Dictmp.Add(item.itemId, item.itemNum);

            }
            tmp.InitItem(Dictmp, -1, true);
            //tmp.InitKuang2(Dictmp, false, true);
            NetMessage.Lobby.Req_OnceBagBuyInfoRequest();
        }
    }
}