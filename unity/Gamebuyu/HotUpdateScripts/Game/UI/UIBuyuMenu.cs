using UnityEngine;
using JEngine.Core;
using JEngine.UI;
using com.maple.common.login.proto;
using ProtoBuf;
using com.maple.network.proto;
using System.Security.Cryptography;
using NetLib;
using System;
using com.maple.game.osee.proto;
using UnityEngine.UI;
using CoreGame;
using com.maple.game.osee.proto.fishing;
using DG.Tweening;

namespace Game.UI
{
    public class UIBuyuMenu : MonoBehaviour 
    {
        private static bool IsFirst = true;

        //体验场
        private Button btn_tyc;

        private Button[] btn_rooms=new Button[5];
        
        private Button btn_zixuan;
        public Transform[] btn_other=new Transform[5];
        public Material[] mat_monsters;
        //public Text[] txt_tips=new Text[5];
        //public Text[] txt_beishu=new Text[5];
        public Text[] txt_PaoJstips=new Text[5];
        public Button btn_return;
        private  ScrollRect Scrollrect;
        public Transform Content;
        public float SL;
        int mPao = 0;
        int mMoney = 0;
        public void Awake()
        {
            btn_return = transform.Find("bg/btn_return").GetComponent<Button>();
            Scrollrect = transform.Find("bg/Scroll View").GetComponent<ScrollRect>();

            btn_tyc = transform.Find("bg/Scroll View/Viewport/Content/btn_tyc").GetComponent<Button>();

            btn_rooms[0] = transform.Find("bg/Scroll View/Viewport/Content/btn_chuji").GetComponent<Button>();
            btn_rooms[1] = transform.Find("bg/Scroll View/Viewport/Content/btn_zhongji").GetComponent<Button>();
            btn_rooms[2] = transform.Find("bg/Scroll View/Viewport/Content/btn_gaoji").GetComponent<Button>();
            btn_rooms[3] = transform.Find("bg/Scroll View/Viewport/Content/btn_gaogaoji").GetComponent<Button>();
            btn_rooms[4] = transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang").GetComponent<Button>();
        

            btn_zixuan = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang/other/btn_zixuan").GetComponent<Button>();
            btn_other[0] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_chuji/other");
            btn_other[1] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zhongji/other");
            btn_other[2] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaoji/other");
            btn_other[3] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaogaoji/other");
            btn_other[4] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang/other");
            //txt_tips[0] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_chuji/other/kuang/Text").GetComponent<Text>();
            //txt_tips[1] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zhongji/other/kuang/Text").GetComponent<Text>();
            //txt_tips[2] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaoji/other/kuang/Text").GetComponent<Text>();
            //txt_tips[3] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaogaoji/other/kuang/Text").GetComponent<Text>();
            //txt_tips[4] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang/other/kuang/Text").GetComponent<Text>();
           
            //txt_beishu[0] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_chuji/other/Text").GetComponent<Text>();
            //txt_beishu[1] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zhongji/other/Text").GetComponent<Text>();
            //txt_beishu[2] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaoji/other/Text").GetComponent<Text>();
            //txt_beishu[3] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaogaoji/other/Text").GetComponent<Text>();
            //txt_beishu[4] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang/other/Text").GetComponent<Text>();
            
            txt_PaoJstips[0] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_chuji/suo/Text").GetComponent<Text>();
            txt_PaoJstips[1] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zhongji/suo/Text").GetComponent<Text>();
            txt_PaoJstips[2] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaoji/suo/Text").GetComponent<Text>();
            txt_PaoJstips[3] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_gaogaoji/suo/Text").GetComponent<Text>();
            txt_PaoJstips[4] = this.transform.Find("bg/Scroll View/Viewport/Content/btn_zongjichang/suo/Text").GetComponent<Text>();
            Content = this.transform.Find("bg/Scroll View/Viewport/Content");

            UEventDispatcher.Instance.AddEventListener(UEventName.FishingGetFieldInfoResponse, On_FishingGetFieldInfoResponse);//获取捕鱼场次信息响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingJoinRoomResponse, On_FishingJoinRoomResponse);//捕鱼加入房间返回
        }
        //改变明暗和解锁
        public void ChangeLight()
        {
            //for (int i = 0; i < btn_rooms.Length; i++)
            //{
            //    if (i > 0)
            //    {
            //        if (PlayerData.PaoLevel < common.dicPaoFwConfig[i].Minlevel)
            //        {
            //            var allimage = btn_other[i].GetComponentsInChildren<Image>();
            //            for (int m = 0; m < allimage.Length; m++)
            //            {
            //                allimage[m].color = Color.gray;
            //            }
            //            btn_rooms[i].transform.Find("suo").gameObject.SetActive(true);
            //        }
            //        else
            //        {
            //            var allimage = btn_other[i].GetComponentsInChildren<Image>();
            //            for (int m = 0; m < allimage.Length; m++)
            //            {
            //                allimage[m].color = Color.white;
            //            }
            //            btn_rooms[i].transform.Find("suo").gameObject.SetActive(false);
            //        }

            //    }
            //}
            //if (common.fieldInfos != null && common.fieldInfos.Count > 0)
            //{
            //    for (int i = 0; i < 5; i++)
            //    {
            //        txt_PaoJstips[common.fieldInfos[i].index - 1].text = "立即解锁" + common.fieldInfos[i].batteryLevelLimit.ToString() + "倍炮";
            //    }
            //}
        }
        private void Start()
        {
            btn_return.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIMainMenu);
                UIMgr.CloseUI(UIPath.UIBuyuMenu);
            });
            btn_zixuan.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIChangeSeat);
            });
            Debug.Log(btn_rooms.Length);
            for (int i = 0; i < btn_rooms.Length; i++)
            {
                int n = i;
                btn_rooms[n].onClick.AddListener(() =>
                {
                    Debug.Log("sssss"+ n);
                    //  UIMgr.CloseAll();
                    //UIMgr.ShowUI(UIPath.UIByRoomMain);
                    // NetMessage.OseeFishing.Req_FishingJoinRoomRequest(n + 1);
                    NetMessage.Chanllenge.Req_FishingChallengeJoinRoomRequest(11+n,0,"");
                });
            }
            btn_tyc.onClick.AddListener(() =>
            {
                NetMessage.Chanllenge.Req_FishingChallengeJoinRoomRequest(51,0,"");
            });
        } 
        void OnEnable()
        {
            ChangeLight();
            //SoundMgr.Instance.SwitchMenuBgMusic();
            NetMessage.OseeFishing.Req_PlayerBatteryLevelRequest();
            EventManager.PaoLevelUpdate+= On_ChangePao;

            ChangeLight();

            CancelInvoke("normalCN");
            Invoke("normalCN", 1f);
            MoveCanJionRoom();
        }
        void OnDisable() {
            EventManager.PaoLevelUpdate -= On_ChangePao;
        }
        void OnDestory()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingGetFieldInfoResponse, On_FishingGetFieldInfoResponse);//获取捕鱼场次信息响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingJoinRoomResponse, On_FishingJoinRoomResponse);//捕鱼加入房间返回
        }
        void normalCN()
        {
            if (ByData.NewNormalnModule != 0)
            {
                // UIMgr.CloseUI(UIPath.UIBuyuMenu);
                NetMessage.OseeFishing.Req_FishingJoinRoomRequest(ByData.NewNormalnModule);
                ByData.NewNormalnModule = 0;
            }
        }
        void On_ChangePao(int v)
        {
            ChangeLight();
        }
        void MoveCanJionRoom()
        {
            if (PlayerData.DragonCrystal < 500000)//小于50万
            {
                mMoney = 1;
            }
            else if (PlayerData.DragonCrystal < 2000000)//小于200万
            {
                mMoney = 2;
            }
            else if (PlayerData.DragonCrystal < 20000000)//小于2000万
            {
                mMoney = 3;
            }
            else if (PlayerData.DragonCrystal < 50000000)//小于5000万
            {
                mMoney = 4;
            }
            else if (PlayerData.DragonCrystal >= 50000000)//大于5000万
            {
                mMoney = 5;
            }
            if (PlayerData.PaoLevel >= 20 && PlayerData.PaoLevel < 200)
            {
                mPao = 1;
            }
            if (PlayerData.PaoLevel >= 200 && PlayerData.PaoLevel < 2000)
            {
                mPao = 2;
            }
            if (PlayerData.PaoLevel >= 2000 && PlayerData.PaoLevel < 20000)
            {
                mPao = 3;
            }
            if (PlayerData.PaoLevel >= 20000 && PlayerData.PaoLevel < 200000)
            {
                mPao = 4;
            }
            if (PlayerData.PaoLevel >= 200000)
            {
                mPao = 5;

            }
            SL = mPao;
            //判断
            if (mMoney < mPao)
            {
                TowMovePos(mMoney);
            }
            else
            {
                TowMovePos(mPao);
            }
        }
        void TowMovePos(int toPos)
        {

            if (toPos == 1)
            {
                Scrollrect.horizontalNormalizedPosition = 0f;
            }
            else if (toPos == 2)
            {
                Scrollrect.horizontalNormalizedPosition = 0f;
            }
            else if (toPos == 3)
            {
                Scrollrect.horizontalNormalizedPosition = 0f;
                DOVirtual.Float(0f, 0.43f, 1f, (fNow) => {
                    Scrollrect.horizontalNormalizedPosition = fNow;
                });

            }
            else if (toPos == 4)
            {
                Scrollrect.horizontalNormalizedPosition = 0.36f;
                DOVirtual.Float(0.36f, 0.76f, 1f, (fNow) => {
                    Scrollrect.horizontalNormalizedPosition = fNow;
                });

            }
            else if (toPos == 5)
            {
                Scrollrect.horizontalNormalizedPosition = 0.6f;
                DOVirtual.Float(0.6f, 1f, 1f, (fNow) => {
                    Scrollrect.horizontalNormalizedPosition = fNow;
                });

            }
        }
       
        /// <summary>
        /// 捕鱼加入房间返回
        /// <summary>
        private void On_FishingJoinRoomResponse(UEventContext obj)
        {
            //var pack = obj.GetData<FishingJoinRoomResponse>();

        }
        /// <summary>
        /// 获取捕鱼场次信息响应
        /// <summary>
        private void On_FishingGetFieldInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingGetFieldInfoResponse>();

        }

    }
}