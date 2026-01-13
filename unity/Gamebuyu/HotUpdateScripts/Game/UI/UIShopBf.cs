using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using LitJson;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public enum JieMainBf
    {
        钻石商城 = 0, 
        金币商城 = 1,
        道具商城 = 2,
        炮台商城 = 3, 
        奖励商城 = 4,
        核弹商城 = 5,
    } 
    public class UIShopBf : MonoBehaviour
    {
        public Button btn_close;
        public Toggle tog_diamond, tog_gold, tog_reward, tog_skill, tog_hedan, tog_paotai;
        public GameObject view_diamond, view_gold, view_reward, view_skill, view_hedan, view_paotai;
        public GameObject view_PayType;
        public Button btn_AliPay; 
        public Button btn_AliPaySao;
        public Button btn_WeiChat;
        public Button btn_WeiChatSao;

        public Button btn_PayClose;
        public Button btn_looktq;//特权
     
        public Text text_nowvip;
        public Text txt_nextMoney;
        public Image img_process;
        public Text txt_process;

        public ArtNumQuote ArtnextVipLv;

        //-------------------------view_diamond-------------------
        public Button[] btn_diamonds=new Button[4];
        //public Text[] btn_diamondsTips=new Text[4];
        //-------------------------view_gold----------------------
        public Button[] btn_golds=new Button[6];
        //public Text[] btn_goldsTips=new Text[8];
        //-------------------------view_Skill----------------------
        public Button[] btn_skill=new Button[10];

        public Button[] btn_hedans = new Button[6]; 
        //-------------------------view_reward--------------------
        //public Button[] btn_pao=new  Button[7];
        //-------------------------view_reward--------------------
        public GameObject rewarditem;
        public Transform Contentgrid;
        private GameObjectPool itemPool = new GameObjectPool();
     
        public JsonData reDataGold;
        public JsonData reDataDaimold;
        public JsonData reDataSkill;
        public Transform Mask;
        public long CanbuyMoney = 3000;
        //限购
        //public Text txt_XianGouhj;
        //兑换记录
        public Transform bg_giftRecording;
        public Button btn_OpenGiftRecording;
        public Button btn_CloseGiftRecording;
        public Button btn_leftRecording;
        public Button btn_RightRecording;
        public Text txt_RecordingNopage;
        public GameObjectPool MyGiftRecordPool = new GameObjectPool();
        public Transform RecordItem;
        public long AllPage = 0;
        public long NoPage = 0;
        //兑换详情
        public Transform bg_xiangqing;
        public Button bg_CloseXiangqing;
        public Text txt_acount;
        public Text txt_password;
        //设置地址
        public Transform bg_Receipt;
        public Button btn_adrees;
        public Button btn_Setadrees;
        public Button btn_adreesclose;
        public InputField Input_Name;
        public InputField Input_phone;
        public InputField Input_address;

        private void Awake()
        {
            InitFind();
            InitClickEvet();

            
            MyGiftRecordPool.SetTemplete(RecordItem.gameObject);
       
            UEventDispatcher.Instance.AddEventListener(UEventName.GetLotteryShopListResponse, On_GetLotteryShopListResponse);//获取奖券商品列表返回
            UEventDispatcher.Instance.AddEventListener(UEventName.BuyShopItemResponse, On_BuyShopItemResponse);//购买商城商品返回
            UEventDispatcher.Instance.AddEventListener(UEventName.RechargeLimitRestResponse, On_RechargeLimitRestResponse);//玩家今日充值剩余限制金额响应
            UEventDispatcher.Instance.AddEventListener(UEventName.BuyIsFirstResponse, On_BuyIsFirstResponse);//获取是否首充响应
            UEventDispatcher.Instance.AddEventListener(UEventName.BossBugleBuyLimitResponse, On_BossBugleBuyLimitResponse);//玩家今日boss号角限购次数信息
            UEventDispatcher.Instance.AddEventListener(UEventName.GetAddressResponse, On_GetAddressResponse);//获取收货地址响应
            UEventDispatcher.Instance.AddEventListener(UEventName.SetAddressResponse, On_SetAddressResponse);//设置收货地址响应
            UEventDispatcher.Instance.AddEventListener(UEventName.LotteryExchangeLogResponse, On_LotteryExchangeLogResponse);//获取兑换记录响应
            itemPool.SetTemplete(rewarditem);
            itemPool.Recycle(rewarditem);
        }
        public void SetAddress()
        {
            bg_Receipt.gameObject.SetActive(true);
        }
        Transform TransformFind(string str)
        {
            var tmp = this.transform.Find(str);
            if (tmp == null)
            {
                Debug.LogError("未找到路径：" + str);
                return null;
            }
            else
            {
                return tmp;
            }
        }
        void InitFind() {
          
            btn_close = TransformFind("bg/btn_close").GetComponent<Button>();
            tog_diamond = TransformFind("bg/root_tog/tog_diamond").GetComponent<Toggle>();
            tog_gold = TransformFind("bg/root_tog/tog_gold").GetComponent<Toggle>();
            tog_reward = TransformFind("bg/root_tog/tog_reward").GetComponent<Toggle>();
            tog_skill = TransformFind("bg/root_tog/tog_skill").GetComponent<Toggle>();
            tog_hedan = TransformFind("bg/root_tog/tog_hedan").GetComponent<Toggle>();
            tog_paotai = TransformFind("bg/root_tog/tog_paotai").GetComponent<Toggle>();

            view_diamond = TransformFind("bg/view_diamond").gameObject;
            view_gold = TransformFind("bg/view_gold").gameObject;
            view_reward = TransformFind("bg/view_reward").gameObject;
            view_skill = TransformFind("bg/view_skill").gameObject;
            view_hedan = TransformFind("bg/view_hedan").gameObject;
            view_paotai = TransformFind("bg/view_paotai").gameObject;

            view_PayType = TransformFind("view_PayType").gameObject;

            btn_PayClose =TransformFind("view_PayType/Background/btn_PayClose").GetComponent<Button>();

            btn_leftRecording = TransformFind("bg_giftRecording/btn_leftRecording").GetComponent<Button>();
            btn_RightRecording = TransformFind("bg_giftRecording/btn_RightRecording").GetComponent<Button>();
            txt_RecordingNopage = TransformFind("bg_giftRecording/txt_RecordingNopage").GetComponent<Text>();


            bg_giftRecording = TransformFind("bg_giftRecording");            
            btn_OpenGiftRecording = TransformFind("bg/view_reward/view_reward/btn_OpenGiftRecording").GetComponent<Button>();        
            btn_CloseGiftRecording = TransformFind("bg_giftRecording/btn_CloseGiftRecording").GetComponent<Button>();

            RecordItem = TransformFind("bg_giftRecording/Viewport/Content/RecordItem");
            rewarditem = TransformFind("bg/view_reward/view_reward/Viewport/Contentgrid/rewarditem").gameObject;

            Contentgrid = TransformFind("bg/view_reward/view_reward/Viewport/Contentgrid");
            bg_xiangqing = TransformFind("bg_xiangqing");
            bg_CloseXiangqing = TransformFind("bg_xiangqing/bg_CloseXiangqing").GetComponent<Button>();
            txt_acount = TransformFind("bg_xiangqing/txt_acount").GetComponent<Text>();
            txt_password = TransformFind("bg_xiangqing/txt_password").GetComponent<Text>();

            btn_adrees = TransformFind("bg/view_reward/view_reward/btn_adrees").GetComponent<Button>();
            btn_adreesclose = TransformFind("bg_Receipt/btn_adreesclose").GetComponent<Button>();
            bg_Receipt = TransformFind("bg_Receipt");                      
            btn_Setadrees = TransformFind("bg_Receipt/btn_Setadrees").GetComponent<Button>();
       
            Input_Name = TransformFind("bg_Receipt/Input_Name").GetComponent<InputField>();
            Input_phone = TransformFind("bg_Receipt/Input_phone").GetComponent<InputField>();
            Input_address = TransformFind("bg_Receipt/Input_address").GetComponent<InputField>();
            btn_looktq = TransformFind("bg/down/btn_looktq").GetComponent<Button>();



            ArtnextVipLv = TransformFind("bg/down/txt_nextMoney/bg_vip/ArtnextVipLv").GetComponent<ArtNumQuote>();
            text_nowvip = TransformFind("bg/down/bg_vip/text_nowvip").GetComponent<Text>();
            txt_nextMoney = TransformFind("bg/down/txt_nextMoney").GetComponent<Text>();
            img_process = TransformFind("bg/down/bg_process/img_process").GetComponent<Image>();
            txt_process = TransformFind("bg/down/bg_process/txt_process").GetComponent<Text>();

            var vargoGold = TransformFind("bg/view_gold/Viewport/view_gold");
           // btn_golds = new Button[vargoGold.childCount]; 
            for (int i = 0; i < btn_golds.Length; i++)
            {
                btn_golds[i] = vargoGold.GetChild(i).GetComponent<Button>();
                //btn_goldsTips[i] = vargoGold.GetChild(i).transform.Find("Text").GetComponent<Text>();
            }

            var vardiamond = TransformFind("bg/view_diamond/Viewport/view_diamond");
          //  btn_diamonds = new Button[vardiamond.childCount];
            for (int i = 0; i < btn_diamonds.Length; i++) 
            {
                btn_diamonds[i] = vardiamond.GetChild(i).GetComponent<Button>();
                //btn_diamondsTips[i] = vardiamond.GetChild(i).transform.Find("Text").GetComponent<Text>();
            }
            var varSkill = TransformFind("bg/view_skill/Viewport/view_skill");
           // btn_skill = new Button[varSkill.childCount];
            for (int i = 0; i < btn_skill.Length; i++)
            {
                btn_skill[i] = varSkill.GetChild(i).GetComponent<Button>();
            }

            var varhedan = TransformFind("bg/view_hedan/Viewport/view_hedan");
            // btn_skill = new Button[varSkill.childCount];
            for (int i = 0; i < btn_hedans.Length; i++)
            {
                btn_hedans[i] = varhedan.GetChild(i).GetComponent<Button>();
            }
            //var varpao = TransformFind("bg/view_paotai/Viewport/view_paotai");
            ////btn_pao = new Button[varpao.childCount];
            //for (int i = 0; i < btn_pao.Length; i++)
            //{
            //    btn_pao[i] = varpao.GetChild(i).GetComponent<Button>();
            //}
            
        }
        void InitClickEvet() {
            btn_close.onClick.AddListener(() => {
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close"); 
                UIMgr.CloseUI(UIPath.UIShop); 
            });
            btn_looktq.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIVipInfo);
                UIMgr.CloseUI(UIPath.UIShop);
            });
            btn_PayClose.onClick.AddListener(() =>
            {
                view_PayType.gameObject.SetActive(false);
            });
            for (int i = 0; i < btn_golds.Length; i++)
            {
                int n = i;
                btn_golds[n].onClick.AddListener(() =>
                {
                    MessageBox.Show("是否确定购买?", "提示", () => {
                        NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_golds[n].name));
                    });
                });
            }
            for (int i = 0; i < btn_hedans.Length; i++)
            {
                int n = i;
                btn_hedans[n].onClick.AddListener(() =>
                {
                    //NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_hedans[n].name));
                    MessageBox.Show("是否确定购买?", "提示", () => {
                        NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_hedans[n].name));
                    });
                });
            }
            for (int i = 0; i < btn_skill.Length; i++)
            {
                int n = i;
                btn_skill[n].onClick.AddListener(() =>
                {
                    MessageBox.Show("是否确定购买?", "提示", () => {
                        NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_skill[n].name));
                    });
                    //NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_skill[n].name));
                });
            }

            //for (int i = 0; i < btn_pao.Length; i++)
            //{
            //    int n = i;
            //    btn_pao[n].onClick.AddListener(() =>
            //    {

            //        NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_pao[n].name));
            //    });
            //}
            btn_adreesclose.onClick.AddListener(() =>
            {
                bg_Receipt.gameObject.SetActive(false);
            });
            btn_Setadrees.onClick.AddListener(() =>
            {
                //if (Input_Name.text=="")
                //{
                //    MessageBox.Show("姓名不能为空");
                //}
                //if (Input_phone.text == "")
                //{
                //    MessageBox.Show("手机号码不能为空");
                //}
                //if (Input_address.text == "")
                //{
                //    MessageBox.Show("地址不能为空");
                //}
                NetMessage.Lobby.Req_SetAddressRequest(Input_Name.text, Input_phone.text, Input_address.text);
            });
            btn_adrees.onClick.AddListener(() =>
            {
                NetMessage.Lobby.Req_GetAddressRequest();
                bg_Receipt.gameObject.SetActive(true);
            });
            btn_OpenGiftRecording.onClick.AddListener(() =>
            {
                foreach (Transform item in RecordItem.transform.parent)
                {
                    MyGiftRecordPool.Recycle(item.gameObject);
                }
                NetMessage.OseeFishing.Req_LotteryExchangeLogRequest(1, 5);
                bg_giftRecording.gameObject.SetActive(true);
            });
            btn_CloseGiftRecording.onClick.AddListener(() =>
            {
                bg_giftRecording.gameObject.SetActive(false);
            });
            bg_CloseXiangqing.onClick.AddListener(() =>
            {
                bg_xiangqing.gameObject.SetActive(false);
            });
            btn_leftRecording.onClick.AddListener(() =>
            {
                string[] vartmp = txt_RecordingNopage.text.Split('/');
                int tmp = int.Parse(vartmp[0]);
                if (tmp - 1 <= 0)
                {
                    return;
                }
                NetMessage.OseeFishing.Req_LotteryExchangeLogRequest(tmp - 1, 5);
            });
            btn_RightRecording.onClick.AddListener(() =>
            {
                string[] vartmp = txt_RecordingNopage.text.Split('/');
                int tmp = int.Parse(vartmp[0]);
                if (tmp + 1 > AllPage)
                {
                    return;
                }
                NetMessage.OseeFishing.Req_LotteryExchangeLogRequest(tmp + 1, 5);
            });
            for (int i = 0; i < btn_diamonds.Length; i++)
            {
                int n = i;
                btn_diamonds[n].onClick.AddListener(() =>
                {
                    MessageBox.Show("是否确定购买?", "提示", () => {
                        NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_diamonds[n].name));
                    });
                    //NetMessage.OseeLobby.Req_BuyShopItemRequest(int.Parse(btn_diamonds[n].name));            
                });
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
        int SongM = 0;
        public void Setpanel(JieMain jiemain)
        {
            switch (jiemain)
            {
                case JieMain.钻石商城:
                    tog_diamond.isOn = true;
                    tog_gold.isOn = false;
                    tog_reward.isOn = false;
                    tog_skill.isOn = false;
                    tog_paotai.isOn = false;

                    view_diamond.SetActive(true);

                    view_gold.SetActive(false);
                    view_reward.SetActive(false);
                    view_skill.SetActive(false);
                    view_paotai.SetActive(false);
                    break;
                case JieMain.金币商城:
                    tog_gold.isOn = true;
                    tog_diamond.isOn = false;
                    tog_reward.isOn = false;
                    tog_skill.isOn = false;
                    tog_paotai.isOn = false;

                    view_gold.SetActive(true);

                    view_diamond.SetActive(false);
                    view_reward.SetActive(false);
                    view_skill.SetActive(false);
                    view_paotai.SetActive(false);
                    break;
                case JieMain.道具商城:
                    tog_skill.isOn = true;
                    tog_diamond.isOn = false;
                    tog_gold.isOn = false;
                    tog_reward.isOn = false;
                    tog_paotai.isOn = false;

                    view_skill.SetActive(true);

                    view_diamond.SetActive(false);
                    view_gold.SetActive(false);
                    view_reward.SetActive(false);
                    view_paotai.SetActive(false);
                    break;
                case JieMain.炮台商城:
                    tog_paotai.isOn = true;
                    tog_diamond.isOn = false;
                    tog_gold.isOn = false;
                    tog_reward.isOn = false;
                    tog_skill.isOn = false;

                    view_paotai.SetActive(true);

                    view_diamond.SetActive(false);
                    view_gold.SetActive(false);
                    view_reward.SetActive(false);
                    view_skill.SetActive(false);
                    //if (PlayerData.vipLevel > 0)
                    //{
                    //    btn_skill[0].gameObject.SetActive(false);
                    //}
                    //else
                    //{
                    //    btn_skill[0].gameObject.SetActive(true);
                    //}
                    break;
                case JieMain.奖励商城:
                    tog_reward.isOn = true;
                    tog_diamond.isOn = false;
                    tog_gold.isOn = false;
                    tog_skill.isOn = false;
                    tog_paotai.isOn = false;

                    view_reward.SetActive(true);

                    view_diamond.SetActive(false);
                    view_gold.SetActive(false);
                    view_paotai.SetActive(false);
                    view_skill.SetActive(false);
                    break;
                case JieMain.核弹商城:
                    tog_reward.isOn = false;
                    tog_diamond.isOn = false;
                    tog_gold.isOn = false;
                    tog_skill.isOn = false;
                    tog_paotai.isOn = false;
                    tog_hedan.isOn = true;

                    view_reward.SetActive(false);
                    view_hedan.SetActive(true);

                    view_diamond.SetActive(false);
                    view_gold.SetActive(false);
                    view_paotai.SetActive(false);
                    view_skill.SetActive(false);
                    break;
                default:
                    break;
            }
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetLotteryShopListResponse, On_GetLotteryShopListResponse);//获取奖券商品列表返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BuyShopItemResponse, On_BuyShopItemResponse);//购买商城商品返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.RechargeLimitRestResponse, On_RechargeLimitRestResponse);//玩家今日充值剩余限制金额响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BuyIsFirstResponse, On_BuyIsFirstResponse);//获取是否首充响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BossBugleBuyLimitResponse, On_BossBugleBuyLimitResponse);//玩家今日boss号角限购次数信息
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetAddressResponse, On_GetAddressResponse);//获取收货地址响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.SetAddressResponse, On_SetAddressResponse);//设置收货地址响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.LotteryExchangeLogResponse, On_LotteryExchangeLogResponse);//获取兑换记录响应
                                                                                                                                //事件注册     
        }

        private void OnEnable()
        {
            EventManager.ChangeVipLevel+=ChangeVipLevel;
            NetMessage.OseeLobby.Req_GetLotteryShopListRequest();
            NetMessage.OseeLobby.Req_RechargeLimitRestRequest();
            NetMessage.OseeLobby.Req_GetPayWayRequest();
            NetMessage.OseeLobby.Req_BossBugleBuyLimitRequest();
            NetMessage.OseeLobby.Req_BuyIsFirstRequest();

            UEventDispatcher.Instance.AddEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取下次抽奖费用返回  
                                                                                                         //GetGoldOrderNew();

            //txt_money.text = PlayerData.Gold.ToString();
            //txt_jiangquan.text = PlayerData.Jiangquan.ToString();
            //txt_diamond.text = PlayerData.Diamond.ToString();
            SetVipProcess();
            Setpanel(JieMain.钻石商城);

            EventManager.VipUpdate+= SetVipProcess;
            PlayerData._isOpenShop = true;
            //if (PlayerData.vipLevel>0)
            //{
            //    btn_skill[0].gameObject.SetActive(false);
            //}
            //else
            //{
            //    btn_skill[0].gameObject.SetActive(true);
            //}
        }
        void ChangeVipLevel(int vip) {
            //if (vip>0)
            //{
            //    btn_skill[0].gameObject.SetActive(false);
            //}
            //else
            //{
            //    btn_skill[0].gameObject.SetActive(true);
            //}
                    
        }
        /// <summary>
        /// 获取收货地址响应
        /// <summary>
        private void On_GetAddressResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetAddressResponse>();
            Input_Name.text = pack.name;
            Input_phone.text = pack.phone;
            Input_address.text = pack.address;
            if (pack.address == "" || pack.name == "" || pack.phone == "")
            {
                Input_Name.interactable = true;
                Input_phone.interactable = true;
                Input_address.interactable = true;
                btn_Setadrees.gameObject.SetActive(true);
            }
            else
            {
                Input_Name.interactable = false;
                Input_phone.interactable = false;
                Input_address.interactable = false;
                btn_Setadrees.gameObject.SetActive(false);

            }

        }
        /// <summary>
        /// 设置收货地址响应
        /// <summary>
        private void On_SetAddressResponse(UEventContext obj)
        {
            var pack = obj.GetData<SetAddressResponse>();
            Input_Name.interactable = false;
            Input_phone.interactable = false;
            Input_address.interactable = false;
            NetMessage.Lobby.Req_GetAddressRequest();
            MessageBox.Show("设置成功");
            btn_Setadrees.gameObject.SetActive(false);
        }


        /// <summary>
        /// 获取兑换记录响应
        /// <summary>
        private void On_LotteryExchangeLogResponse(UEventContext obj)
        {
            var pack = obj.GetData<LotteryExchangeLogResponse>();
            foreach (Transform item in RecordItem.transform.parent)
            {
                MyGiftRecordPool.Recycle(item.gameObject);
            }
            foreach (var item in pack.log)
            {
                var go = MyGiftRecordPool.Get(RecordItem.transform.parent);
                // go.transform.Find("Id").GetComponent<Text>().text = item.state.ToString();
                go.transform.Find("name").GetComponent<Text>().text = item.shopName.ToString();
                //go.transform.Find("prop").GetComponent<Text>().text = item.info.ToString();//item.giftName + "x" + item.giftNum.ToString();
                go.transform.Find("time").GetComponent<Text>().text = item.date;
                if (item.state == 0)
                {
                    go.transform.Find("state").GetComponent<Text>().text = "待发货";
                }
                else if (item.state == 1)
                {
                    go.transform.Find("state").GetComponent<Text>().text = "已发货";
                }
                else
                {
                    go.transform.Find("state").GetComponent<Text>().text = "已拒绝";
                }

                if (item.info == null)
                {
                    go.transform.Find("Button").gameObject.SetActive(false);
                }
                else
                {
                    go.transform.Find("Button").gameObject.SetActive(true);
                    go.transform.Find("Button").GetComponent<Button>().onClick.RemoveAllListeners();
                    go.transform.Find("Button").GetComponent<Button>().onClick.AddListener(() =>
                    {
                        bg_xiangqing.gameObject.SetActive(true);
                        txt_acount.text = item.info.number;
                        txt_password.text = item.info.password;
                    });
                }


            }
            long m = pack.totalCount / 5;
            if (pack.totalCount % 5 > 0)
            {
                m = m + 1;
            }
            AllPage = m;
            NoPage = pack.pageNo;
            txt_RecordingNopage.text = pack.pageNo.ToString() + "/" + "" + m;
        }

        /// <summary>
        /// 玩家今日boss号角限购次数信息
        /// <summary>
        private void On_BossBugleBuyLimitResponse(UEventContext obj)
        {
            var pack = obj.GetData<BossBugleBuyLimitResponse>();
           // txt_XianGouhj.text = "每日购买" + pack.usedLimit + "/" + pack.buyLimit + "次";
        }

        public void SetVipProcess()
        {
            text_nowvip.text = PlayerData.vipLevel + "";
            txt_nextMoney.text = PlayerData.vipNextMoney.ToString();
            ArtnextVipLv.Init( PlayerData.vipLevel + 1);
            if (PlayerData.vipLevel + 1 < common.dicVipConfig.Count)
            {
                txt_process.text = string.Format("{0}/{1}", PlayerData.vipTotalMoney, common.dicVipConfig[PlayerData.vipLevel + 1].money);
                img_process.fillAmount = (float)PlayerData.vipTotalMoney / common.dicVipConfig[PlayerData.vipLevel + 1].money;
            }
            else
            {
                txt_process.text = string.Format("最高等级");
                img_process.fillAmount = 1f;
            }
        }
        private void On_VipLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<VipLevelResponse>();
            txt_nextMoney.text = PlayerData.vipNextMoney.ToString();
            text_nowvip.text = PlayerData.vipLevel.ToString();

            if (PlayerData.vipLevel < common.dicVipConfig.Count)
            {
                txt_process.text = string.Format("{0}/{1}", PlayerData.vipTotalMoney, common.dicVipConfig[PlayerData.vipLevel + 1].money);
                img_process.fillAmount = (float)PlayerData.vipTotalMoney / common.dicVipConfig[PlayerData.vipLevel + 1].money;
            }
            else
            {
                txt_process.text = string.Format("最高等级");
                img_process.fillAmount = 1f;
            }
        }
        private void OnDisable()
        {
            PlayerData._isOpenShop =false;
            EventManager.ChangeVipLevel -= ChangeVipLevel;
            view_diamond.gameObject.SetActive(true);
            view_gold.gameObject.SetActive(false);
            view_reward.gameObject.SetActive(false);
            view_skill.gameObject.SetActive(false);
            view_paotai.gameObject.SetActive(false);
            NetMessage.Agent.Req_GetUserRechargeMoneyRequest(PlayerData.PlayerId);

            UEventDispatcher.Instance.RemoveEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取下次抽奖费用返回  
            EventManager.VipUpdate -= SetVipProcess;
            EventManager.ShopPaoWingUpdate?.Invoke();
        }

   
 
        /// <summary>
        /// 获取奖券商品列表返回
        /// <summary>
        private void On_GetLotteryShopListResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetLotteryShopListResponse>();
            //清空
            for (int i = 0; i < Contentgrid.childCount; i++)
            {
                var go = Contentgrid.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }
            for (int i = 0; i < pack.shopItems.Count; i++)
            {
                int n = i;
                var data = pack.shopItems[n];
                var go = itemPool.Get();
                go.SetActive(true);
                go.transform.SetParent(Contentgrid, false);

                Image imgItem = go.transform.Find("img_item").GetComponent<Image>();
                Text txtLeft = imgItem.transform.Find("txt_left").GetComponent<Text>();
                ContentSizeFitter sizeFit = go.transform.Find("Image/group").GetComponent<ContentSizeFitter>();
                Text txtNum = sizeFit.transform.Find("txt_num").GetComponent<Text>();
                Text txtName = imgItem.transform.Find("txt_name").GetComponent<Text>();

                txtName.text = data.name;
                if (data.rest == -1)
                    txtLeft.text = "剩余数量:无限制";
                else
                    txtLeft.text = "剩余数量:" + data.rest;
                txtNum.text = data.lottery.ToString();
                sizeFit.enabled = false;
                sizeFit.enabled = true;
                Debug.Log(data.img.ToString());
                UIHelper.GetHttpImage(data.img, (sp) => { imgItem.sprite = sp; });
                imgItem.sprite = common4.LoadSprite("item/shopDiamond");
                Button btn = go.GetComponent<Button>();

                btn.onClick.RemoveAllListeners();
                btn.onClick.AddListener(() =>
                {
                    //MessageBox.Show("兑换奖品请联系客服微信");
                    Debug.Log(data.id);
                    NetMessage.OseeLobby.Req_BuyShopItemRequest(data.id);
                });

            }
        }
        /// <summary>
        /// 购买商城商品返回
        /// <summary>
        private void On_BuyShopItemResponse(UEventContext obj)
        {
            var pack = obj.GetData<BuyShopItemResponse>();
            NetMessage.OseeLobby.Req_RechargeLimitRestRequest();
            string str = pack.success ? "成功" : "失败";
            MessageBox.Show("购买商品" + str, "购买商品");
            NetMessage.OseeLobby.Req_BossBugleBuyLimitRequest();
            NetMessage.OseeLobby.Req_GetLotteryShopListRequest();
            NetMessage.OseeLobby.Req_BuyIsFirstRequest();
            NetMessage.OseeLobby.Req_VipLevelRequest();

            NetMessage.OseeFishing.Req_PlayerPropRequest();
            NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
        }
        /// <summary>
        /// 玩家今日充值剩余限制金额响应
        /// <summary>
        private void On_RechargeLimitRestResponse(UEventContext obj)
        {
            var pack = obj.GetData<RechargeLimitRestResponse>();
            CanbuyMoney = pack.limitRest;
        }
        /// <summary>
        /// 获取是否首充响应
        /// <summary>
        private void On_BuyIsFirstResponse(UEventContext obj)
        {
            var pack = obj.GetData<BuyIsFirstResponse>();
        }

        void GetSkillOrder(int nIndex, long ProductId, int type, bool isNei)
        {

       
        }
        void GetSkillOrderNew(int nIndex, long ProductId, int type, bool isNei)
        {
        
        }
        void GetGoldOrder(int nIndex, long ProductId, int type, bool isNei)
        {
      
        }
        void GetGoldOrderNEW(int nIndex, long ProductId, int type, bool isNei)
        {

          
        }
        void GetOrderDaimoldNew(int nIndex, long ProductId, int tpye, bool isNei)
        {
 
        }
        void GetOrder(int nIndex, long ProductId, int tpye, bool isNei)
        {
         
        }
    }
}