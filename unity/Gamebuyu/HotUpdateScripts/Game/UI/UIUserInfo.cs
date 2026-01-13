//using com.lyh.protocol;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using com.maple.game.osee.proto.lobby;

using com.maple.game.osee.proto;
using System.Text.RegularExpressions;
using CoreGame;
using System;

namespace Game.UI
{
    public class UIUserInfo : MonoBehaviour
    {
        public Button btn_close;
        public Button btn_Copy;
        //public Button btn_smRenZen;

        public Button btn_SecurityCenter;
        //main
        public Image img_headsp;
        public Text txt_name;


        //public Text txt_explevel;
        //public Slider Slider_exp;

        //public Text txt_vipLevel;
        //public Text txt_vipEndDate;
        //public Text txt_vipLeftTime;
        //public Text txt_yaoqinma;

        public Button btn_closechangehead, btn_closeChangeName;

        public Button btn_ChangeName;
        public GameObject view_changeName;
        public GameObject view_changeHead;

        public Button btn_OkChangeName;
        //-------------------view_main---------------
        public Button btn_changeHead;
        public InputField input_name;
        public Text txt_playerID;
        public Text num_vip;
        //public Text num_vip2;
        //-------------------view_changeHead----------
        public GameObject itemhead;
        public Transform gridhead;
        private GameObjectPool itemPool = new GameObjectPool();

        //public Button btn_ShezhiNackName;
        //public Button btn_chongzhimima;
        public class BagItemInfo
        {
            public string strName;
            public string strInfo;
            public long costMoney = 0;
            //public int itemID = 0;
            public BagItemInfo(string a, string b, long c = 0)
            {
                //itemID = x;
                strName = a;
                strInfo = b;
                costMoney = c;
            }
        }

        void FindCompent()
        {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_Copy = this.transform.Find("bg/btn_Copy").GetComponent<Button>();
            txt_name = this.transform.Find("bg/Personal/NameFont").GetComponent<Text>();
            txt_playerID = this.transform.Find("bg/ID/fra/Text").GetComponent<Text>();

            btn_SecurityCenter = this.transform.Find("bg/Personal/SafeCenter").GetComponent<Button>();

            img_headsp = this.transform.Find("bg/Head/Image").GetComponent<Image>();// this.transform.Find("bg/img_head/mask/img_headsp").GetComponent<Image>();


            btn_ChangeName = this.transform.Find("bg/Personal/ModifyTheNameBtn").GetComponent<Button>();//this.transform.Find("bg/txt_name").GetComponent<Button>();

            //txt_explevel = this.transform.Find("bg/Level/Text").GetComponent<Text>();// = this.transform.Find("bg/bg_exp/txt_explevel").GetComponent<Text>();
            //Slider_exp = this.transform.Find("Content/fra01/Content/Level/Slider_exp").GetComponent<Slider>();// this.transform.Find("bg/bg_exp/img_exp").GetComponent<Slider>();


            btn_changeHead = this.transform.Find("bg/Head/but").GetComponent<Button>(); //this.transform.Find("bg/btn_changeHead").GetComponent<Button>();
            view_changeName = this.transform.Find("view_changeName").gameObject;
            view_changeHead = this.transform.Find("view_changeHead").gameObject;
            btn_OkChangeName = this.transform.Find("view_changeName/bg/btn_ok").GetComponent<Button>();
            input_name = this.transform.Find("view_changeName/bg/input_name").GetComponent<InputField>();
            gridhead = this.transform.Find("view_changeHead/Scroll View/Viewport/gridhead");
            itemhead = gridhead.Find("itemhead").gameObject;

            num_vip = this.transform.Find("bg/Personal/NameFont/VipFont").GetComponent<Text>();//= this.transform.Find("bg/bg_vip/num_vip").GetComponent<Text>();
            //num_vip2 = this.transform.Find("Content/fra01/Content/Title/fra/num_vip").GetComponent<Text>();
            btn_closechangehead = this.transform.Find("view_changeHead/btn_close").GetComponent<Button>();
            btn_closeChangeName = this.transform.Find("view_changeName/bg/btn_close").GetComponent<Button>();

            //btn_ShezhiNackName = this.transform.Find("bg/btn_ShezhiNackName").GetComponent<Button>();
            //btn_chongzhimima = this.transform.Find("bg/btn_chongzhimima").GetComponent<Button>();
        }
        void Awake()
        {
            FindCompent();



            btn_close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIUserInfo);
            });

            itemPool.SetTemplete(itemhead);
            itemPool.Recycle(itemhead);

            btn_changeHead.onClick.AddListener(() =>
            {
                view_changeHead.SetActive(true);
            });
            btn_OkChangeName.onClick.AddListener(() =>
            {
                if (input_name.text.Length < 2 || input_name.text.Length > 6)
                {
                    MessageBox.ShowPopOneMessage("请输入长度为2-6位的昵称");
                    return;
                }
                Regex rnum = new Regex("^[0-9]{1,}$"); //正则表达式 表示数字的范围 ^符号是开始，$是关闭   
                var resultnum = rnum.Match(input_name.text);
                if (resultnum.Success)
                {
                    MessageBox.ShowPopOneMessage("只能为汉字和字母");
                    return;
                }
                Regex rex = new Regex(@"^[\u4E00-\u9FA5A-Za-z0-9]+$");
                var result = rex.Match(input_name.text);
                if (!result.Success)
                {
                    MessageBox.ShowPopOneMessage("只能为汉字和字母");
                    return;
                }

                if (input_name.text == PlayerData.NickName)
                {
                    MessageBox.ShowPopOneMessage("和当前昵称相同");
                    return;
                }
                if (input_name != null && input_name.text != "")
                {

                    NetMessage.Lobby.Req_ChangeNicknameRequest(input_name.text);
                }
            });

            btn_Copy.onClick.AddListener(() =>
            {
                UnityEngine.GUIUtility.systemCopyBuffer = PlayerData.PlayerId.ToString();
                MessageBox.ShowPopOneMessage("复制成功！");

            });
            btn_SecurityCenter.onClick.AddListener(() =>
            {

                //if (common.LoginState == 2) //游客登陆
                //{
                //    MessageBox.Show("你还未设置昵称！点击确定立即开始设置", null, () =>
                //    {
                //        UIAccount varUIAccount = UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>();
                //        varUIAccount.SetPanel(UIAccountPanel.BangDingNackName);
                //    });
                //    return;
                //}
                //if (common.LoginState == 4) //微信登陆
                //{
                //    if (PlayerData.isWxSheZhiNickName)
                //    {

                //    }
                //    else
                //    {
                //        MessageBox.Show("你还未设置昵称！点击确定立即开始设置", null, () =>
                //        {
                //            UIAccount varUIAccount = UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>();
                //            varUIAccount.SetPanel(UIAccountPanel.BangDingNackName);
                //        });
                //        return;
                //    }
                //}
                //if (PlayerData.isShengFenZheng)
                //{
                //    UIAccount varUIAccount = UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>();
                //    varUIAccount.SetPanel(UIAccountPanel.SecurityCenter);
                //}
                //else
                //{

                //    UIMgr.ShowUI(UIPath.UI_ViewXuke);
                //}
                UIMgr.CloseUI(UIPath.UIUserInfo);
                UIMgr.ShowUI(UIPath.UIResetPasswd);
            });

            //btn_ShezhiNackName.onClick.AddListener(() =>
            //{
            //    view_changeName.SetActive(true);
            //});
            //btn_chongzhimima.onClick.AddListener(() =>
            //{
            //    UIMgr.ShowUI(UIPath.UIResetPasswd);
            //});

            btn_closechangehead.onClick.AddListener(() => { view_changeHead.SetActive(false); });
            btn_closeChangeName.onClick.AddListener(() => { view_changeName.SetActive(false); });

            for (int i = 1; i < PerfabsMgr.Instance.AllHead.Count; i++)
            {
                int n = i;
                var go = itemPool.Get();
                go.SetActive(true);
                if (go != null)
                {

                    Debug.Log("加载29次数");
                }
                go.transform.SetParent(gridhead, false);
                Image imgHead = go.transform.Find("Image").GetComponent<Image>();
                imgHead.sprite = PerfabsMgr.Instance.AllHead[n];// varSprit;// common4.LoadSprite("head/head_" + i);
                go.GetComponent<Button>().onClick.RemoveAllListeners();
                go.GetComponent<Button>().onClick.AddListener(() =>
                {
                    NetMessage.Lobby.Req_ChangeUserInfoRequest(null, n, PlayerData.StrHeadUrl);
                });
            }
            btn_changeHead.onClick.AddListener(() =>
            {
                view_changeHead.SetActive(true);
            });
            btn_ChangeName.onClick.AddListener(() =>
            {
                view_changeName.SetActive(true);
            });
            UEventDispatcher.Instance.AddEventListener(UEventName.PlayerPropResponse, On_PlayerPropResponse);//玩家道具信息返回
            UEventDispatcher.Instance.AddEventListener(UEventName.ChangeNicknameResponse, On_ChangeNicknameResponse);//更改昵称响应
            UEventDispatcher.Instance.AddEventListener(UEventName.PlayerLevelResponse, On_PlayerLevelResponse);//获取玩家等级请求
        }


        public long AllPage = 0;
        public long NoPage = 0;
        /// <summary>
        /// 玩家道具信息返回
        /// <summary>
        private void On_PlayerPropResponse(UEventContext obj)
        {
            var pack = obj.GetData<PlayerPropResponse>();
            //for (int i = 0; i < btn_item.Length - 3; i++)
            //{
            //    btn_item[i].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myItem[i + 2].ToString();
            //}

            //btn_item[btn_item.Length - 3].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[51].ToString();
            //btn_item[btn_item.Length - 2].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[52].ToString();
            //btn_item[btn_item.Length - 1].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[64].ToString();

            //if (PlayerData.DragonCrystal > 10000)
            //{
            //    btn_itemLongjin.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = (PlayerData.DragonCrystal / 10000) + "W";
            //}
            //else
            //{
            //    btn_itemLongjin.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = PlayerData.DragonCrystal.ToString();
            //}
            //if (PlayerData.Diamond > 10000)
            //{
            //    btn_itemDiamond.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = (PlayerData.Diamond / 10000) + "W";
            //}
            //else
            //{
            //    btn_itemDiamond.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = PlayerData.Diamond.ToString();
            //}
        }
        /// <summary>
        /// 玩家消息列表响应
        /// <summary>
        private void On_MessageListResponse(UEventContext obj)
        {
            var pack = obj.GetData<MessageListResponse>();
        }
        /// <summary>
        /// 玩家未读消息数量响应
        /// <summary>
        private void On_UnreadMessageCountResponse(UEventContext obj)
        {
            var pack = obj.GetData<UnreadMessageCountResponse>();
        }
        /// <summary>
        /// 读取消息响应
        /// <summary>
        private void On_ReadMessageResponse(UEventContext obj)
        {
            var pack = obj.GetData<ReadMessageResponse>();
        }
        /// <summary>
        /// 领取消息附件/删除响应
        /// <summary>
        private void On_ReceiveMessageItemsResponse(UEventContext obj)
        {
            var pack = obj.GetData<ReceiveMessageItemsResponse>();
        }
        void OnEnable()
        {

            EventManager.PaoViewIndexUpdate += On_ChangePaoView;
            EventManager.PropUpdate += ChangeProp;
            EventManager.intHeadUrlUpdate += On_intHeadUrlUpdate;
            EventManager.StrHeadUrlUpdate += On_StrHeadUrl;



            txt_playerID.text = "ID:" + PlayerData.PlayerId;

            NetMessage.Lobby.Req_PlayerLevelRequest();
            num_vip.text = "贵族" + PlayerData.vipLevel.ToString();
            //num_vip2.text = "贵族" + PlayerData.vipLevel.ToString();
            PlayerData.GetMyHeadImage((sp) =>
            {
                img_headsp.sprite = sp;
            });
            txt_name.text = PlayerData.NickName;


            //img_sex.sprite = Resources.Load<Sprite>("sex/" + (int)PlayerData.sex);
            //if (PlayerData.vipLevel <= 0)
            //{
            //    txt_vipLevel.text = "普通玩家";
            //}
            //else
            //{
            //    txt_vipLevel.text = PlayerData.vipLevel.ToString();
            //}



        }
        void OnDisable()
        {
            EventManager.PropUpdate -= ChangeProp;
            EventManager.PaoViewIndexUpdate -= On_ChangePaoView;
            EventManager.intHeadUrlUpdate -= On_intHeadUrlUpdate;
            EventManager.StrHeadUrlUpdate -= On_StrHeadUrl;
        }
        void On_StrHeadUrl(string v)
        {
            GetMyHeadImage((sp) =>
            {
                img_headsp.sprite = sp;
            });
        }
        void On_intHeadUrlUpdate(int m)
        {
            GetMyHeadImage((sp) =>
            {
                img_headsp.sprite = sp;
            });
        }
        /// <summary>
        /// 获取自己的头像
        /// </summary>
        /// <param name="callback"></param>
        public void GetMyHeadImage(Action<Sprite> callback)
        {

            if (PlayerData.StrHeadUrl == "")
            {
                string url = PlayerData.HeadIndex.ToString("00");

                UIHelper.GetHeadImage(url, callback);
            }
            else
            {
                string url = "";
                url = PlayerData.StrHeadUrl;
                UIHelper.GetHeadImage(url, callback);
            }

        }
        void ChangeProp()
        {

        }
        void On_ChangePaoView(int mess)
        {
            //for (int i = 0; i < btn_paotai.Length; i++)
            //{
            //    int n = i;
            //    if (btn_paotai[n].name == PlayerData.PaoViewIndex.ToString())
            //    {
            //        btn_paotai[n].transform.Find("tog_zhuangbei").GetComponent<Toggle>().isOn = true;
            //    }
            //    else
            //    {
            //        btn_paotai[n].transform.Find("tog_zhuangbei").GetComponent<Toggle>().isOn = false;
            //    }
            //}
        }


        /// <summary>
        /// 更改昵称响应
        /// <summary>
        private void On_ChangeNicknameResponse(UEventContext obj)
        {
            var pack = obj.GetData<ChangeNicknameResponse>();
            PlayerData.NickName = pack.nickname;
            view_changeName.gameObject.SetActive(false);
            UIMgr.CloseUI(UIPath.UIUserInfo);
            MessageBox.ShowPopOneMessage("修改成功");
        }

        /// <summary>
        /// 获取玩家等级请求返回
        /// <summary>
        private void On_PlayerLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<PlayerLevelResponse>();
            float need = pack.nowExperience / (pack.nextExperience * 1f);
            //txt_explevel.text = pack.level + "级";
            if (need <= 0.2f)
            {
                need = 0.2f;
            }
            //Slider_exp.value = need;
        }

        void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerPropResponse, On_PlayerPropResponse);//玩家道具信息返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.ChangeNicknameResponse, On_ChangeNicknameResponse);//更改昵称响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerLevelResponse, On_PlayerLevelResponse);//获取玩家等级请求
        }
    }
}