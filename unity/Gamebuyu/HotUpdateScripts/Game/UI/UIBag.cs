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
    public class UIBag : MonoBehaviour
    {

        public Button btn_close;

        //详情
        public Image detailPropIco;
        public Text detailName;
        public Text detailCount;
        public Text detailContent;


        //item
        public Button[] btn_item = new Button[8];

  
        private int _ngivePlayerId, _ngiveNum, _nSelItem = -1;
        private int _nClick;

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
        private Dictionary<int, BagItemInfo> list_item = new Dictionary<int, BagItemInfo>();
        void FindCompent()
        {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            detailPropIco = this.transform.Find("bg/Detail/Item/prop").GetComponent<Image>();
            detailName = this.transform.Find("bg/Detail/Item/prop/Name").GetComponent<Text>();
            detailCount = this.transform.Find("bg/Detail/Item/prop/Count").GetComponent<Text>();
            detailContent = this.transform.Find("bg/Detail/Item/Describe/Content").GetComponent<Text>();

  



            btn_item[0] = this.transform.Find("bg/itemview0/bg_item/7").GetComponent<Button>();
            btn_item[1] = this.transform.Find("bg/itemview0/bg_item/8").GetComponent<Button>();
            btn_item[2] = this.transform.Find("bg/itemview0/bg_item/9").GetComponent<Button>();
            btn_item[3] = this.transform.Find("bg/itemview0/bg_item/10").GetComponent<Button>();
            btn_item[4] = this.transform.Find("bg/itemview0/bg_item/11").GetComponent<Button>();
            btn_item[5] = this.transform.Find("bg/itemview0/bg_item/19").GetComponent<Button>();
            btn_item[6] = this.transform.Find("bg/itemview0/bg_item/38").GetComponent<Button>();
            btn_item[7] = this.transform.Find("bg/itemview0/bg_item/50").GetComponent<Button>();

            //TogAll[0]= this.transform.Find("bg/togChange/tog_daoju").GetComponent<Toggle>();
            //TogAll[1] = this.transform.Find("bg/togChange/tog_cailiao").GetComponent<Toggle>();
            //TogAll[2] = this.transform.Find("bg/togChange/tog_pao").GetComponent<Toggle>();
        }
        void Awake()
        {
            FindCompent();
            list_item.Add(7, new BagItemInfo("核弹", "海洋世界中，各部落研制出的最新武器，蕴藏着巨大的能量，魔晶战场中可与龙晶互相兑换，经典渔场击杀boss有概率掉落。"));
            list_item.Add(8, new BagItemInfo("锁定", "技能持续期间对点击目标鱼类进行无视其他鱼类的追踪攻击。"));
            list_item.Add(9, new BagItemInfo("冰冻", "冰冻界面中所有的鱼，被冰冻的鱼停在原地不可动弹，冰冻时间内暂停刷新其他鱼类。"));
            list_item.Add(10, new BagItemInfo("急速", "子弹速度加快。"));
            list_item.Add(11, new BagItemInfo("狂暴", "狂暴状态下呈X2、X4倍消耗，同时击杀威力也将提升。"));
            list_item.Add(19, new BagItemInfo("分身", "使用分身状态下 可同时发射三发子弹，可提升捕鱼威力。"));
            list_item.Add(38, new BagItemInfo("海螺", "传闻黄金鱼最爱的海螺声，使用后必定召唤1只适合该场次的黄金鱼。"));
            list_item.Add(50, new BagItemInfo("电磁炮", "电磁炮,同时击杀威力也将提升。"));


            btn_close.onClick.AddListener(() =>
            {
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
                UIMgr.CloseUI(UIPath.UIBag);
            });
            //道具页面
            for (int i = 0; i < btn_item.Length; i++)
            {
                int n = i;
                btn_item[n].onClick.AddListener(() =>
                {
                    _nSelItem = int.Parse(btn_item[n].name);
                    _nClick = n;

                
                    Sprite sp = btn_item[n].transform.GetComponent<Image>().sprite;
                    string sname = common4.BagItemConfig[_nSelItem].name;
                    string scount = btn_item[n].transform.Find("txt_count").GetComponent<Text>().text;
                    string content = common4.BagItemConfig[_nSelItem].info;
                    ChangeXiangQing(sp, sname, scount, content);
                });
            }

        
            UEventDispatcher.Instance.AddEventListener(UEventName.PlayerPropResponse, On_PlayerPropResponse);//玩家道具信息返回
        }
        void ChangeXiangQing(Sprite sp, string name, string count, string Content)
        {
            detailPropIco.sprite = sp;
            detailName.text = name;
            detailCount.text = count;
            detailContent.text = Content;
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
            EventManager.PropUpdate += ChangeProp;

            ChangeItemNum();
            _nSelItem = _ngiveNum = _ngivePlayerId = -1;
            NetMessage.Lobby.Req_PlayerLevelRequest();
            ShowDetil(0);
        }
        void ShowDetil(int n)
        {
            _nSelItem = int.Parse(btn_item[n].name);
            _nClick = n;
            Sprite sp = btn_item[n].transform.GetComponent<Image>().sprite;
            string sname = common4.BagItemConfig[_nSelItem].name;
            string scount = btn_item[n].transform.Find("txt_count").GetComponent<Text>().text;
            string content = common4.BagItemConfig[_nSelItem].info.ToString();
            ChangeXiangQing(sp, sname, scount, content);
        }
        void OnDisable()
        {
            EventManager.PropUpdate -= ChangeProp;

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
            ChangeItemNum();
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

        void ChangeItemNum()
        {
            for (int i = 0; i < btn_item.Length; i++)
            {
                if (int.Parse(btn_item[i].name)==7|| int.Parse(btn_item[i].name) == 9|| int.Parse(btn_item[i].name) == 38)
                {
                    if (common.myDicItem.ContainsKey(int.Parse(btn_item[i].name)))
                    {
                        btn_item[i].transform.Find("txt_count").GetComponent<Text>().text = common.myDicItem[int.Parse(btn_item[i].name)].ToString();
                    }
                }
            }
        }
        void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerPropResponse, On_PlayerPropResponse);//玩家道具信息返回

        }



    }
}