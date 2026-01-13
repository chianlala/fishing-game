using UnityEngine;
using JEngine.Core;
using JEngine.UI;
using ProtoBuf;
using com.maple.network.proto;
using System.Security.Cryptography;
using NetLib;
using System;
using com.maple.game.osee.proto;
using UnityEngine.UI;
using CoreGame;
using System.Collections.Generic;
using com.maple.game.osee.proto.fishing;
using System.Collections;
using DG.Tweening;
using GameFramework;
using System.Text;
using libx;
using UnityEngine.SceneManagement;
using System.Linq;

namespace Game.UI
{
    public class UIByChange : MonoBehaviour, UIFishingInterface
    {
        private static bool IsFirst = true; 

        public List<player> objPlayer = new List<player>();
        public player MyPlayer;

        private Transform[] trans_waitjion = new Transform[4];


        public Transform TipsPos;
        public Transform CatchTips;
        public Transform root_DropEffect;
        #region   
        //左侧打开 
        //public Button btn_leftOpen;
        //public Button btn_leftClose;
        //public Transform view_leftNormal;
        //public Transform view_leftHide;
        //public Button btn_exit;

        //public Button btn_yuzhong;
        #endregion
        public GameObject prefabBoomlongjin;
        //击杀鱼
        #region 
        public float f_comboCD;
        public ArtNumQuote num_combo;//杀鱼连击数
        public KillBoss KillBoss;
        public Transform GoldNormal;
        public Transform NowNormal;
        #endregion
        //对象池
        #region 
 
        //public GameObject prefab_piaofen;//飘分

        //private GameObjectPool itemPool_piaofen;
        #endregion 
        public GameObject prefabItemLight;//掉落的道具光
        public Amazing GO_Amazing;//掉落的道具 
       
        private int MaxPaoFw = 15;
        public GameObject maskReConnecting;
        //技能
        #region 
        //public Button btn_suoding;
        //public Button btn_bingdong;
        //public Button btn_shengdeng;//神灯
        //public Button btn_autofire;

        //public Image btn_suoding_mask;
        //public Image btn_bingdong_mask;
        //public Image btn_shengdeng_mask;//神灯
        //雷电枪
        public GameObject leidainPao;
        public Toggle tog_yulei;
  
        [HideInInspector]
        public bool b_SkillIce = false;//是否处于冰冻技能

        #endregion
        public Transform trans_fishTrigger;

        private GameObjectPool Object_DropIconFishPool;
        public GameObject Object_DropIconFish;


        public GameObject Object_bigWin;
        //房间任务
        public Image img_imgtarget;
        public Image img_imgAward;
        public Text txt_questProcess;
        public Text txt_questTarget;
        public Text txt_jobname;
        public Text txt_jobaward;
        public Button btn_linqu;

        Transform varZhuan;
        Dictionary<int, long> Dictmp = new Dictionary<int, long>();
        public Transform JobMuiltMoney;

        public Image prefabBoom;//核弹爆炸
        //刷新鱼处理 服务器时间
        private List<FishingChallengeFishInfoProto> list_fishInit = new List<FishingChallengeFishInfoProto>();
        public GameObject goldNum;//金币数字
        ////闪电链子
        //GameObjectPool ShandianPool;
        public Transform boosTips;

        //钻头效果临时储存
 
        public Button tog_ShowjiesuoLV;
        public Sprite spJiesuo;
        public Sprite spQianghua;
        public GameObject tyc;
        public Transform game_ui_skill;
        Dictionary<int, AudioClip> AllFishDie = new Dictionary<int, AudioClip>();
        void FindCompent() {
            tyc = transform.Find("tyc").gameObject;
            objPlayer.Clear();
            objPlayer.Add(transform.Find("rootPlayer/0").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/1").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/2").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/3").GetComponent<player>());

            GoldNormal = this.transform.Find("GoldNormal");
            maskReConnecting = transform.Find("maskReConnecting").gameObject;            
            //寻找加入或换座
            #region
            trans_waitjion[0] = transform.Find("rootPlayer/img0");
            trans_waitjion[1] = transform.Find("rootPlayer/img1");
            trans_waitjion[2] = transform.Find("rootPlayer/img2");
            trans_waitjion[3] = transform.Find("rootPlayer/img3");

            #endregion

            num_combo = transform.Find("obj_combo/ArtNumQuote").GetComponent<ArtNumQuote>();
            
            //btn_bingdong = transform.Find("game_ui_skill/Ice/Button").GetComponent<Button>();
            //btn_shengdeng = transform.Find("game_ui_skill/Summon/Button").GetComponent<Button>();

            //btn_suoding = transform.Find("game_ui_skill/Lock/Button").GetComponent<Button>();
         
            //btn_autofire = transform.Find("game_ui_skill/Auto/Button").GetComponent<Button>();

            //btn_bingdong_mask = transform.Find("game_ui_skill/Ice/Mask").GetComponent<Image>();
            //btn_shengdeng_mask = transform.Find("game_ui_skill/Summon/Mask").GetComponent<Image>();

            //btn_suoding_mask = transform.Find("game_ui_skill/Lock/Mask").GetComponent<Image>();

         
            CatchTips = transform.Find("CatchTips");
            TipsPos = transform.Find("rootPlayer/posTips");
            game_ui_skill = transform.Find("rootPlayer/game_ui_skill");
            boosTips = transform.Find("boosTips");
     
            img_imgtarget = this.transform.Find("topQuest/ImgTarget").GetComponent<Image>();
            img_imgAward = this.transform.Find("topQuest/bg_award/Image").GetComponent<Image>();
            txt_questProcess = this.transform.Find("topQuest/txt_process").GetComponent<Text>();
            txt_questTarget = this.transform.Find("topQuest/888").GetComponent<Text>();
            txt_jobname = this.transform.Find("topQuest/bg_target/txt_target").GetComponent<Text>();
            txt_jobaward = this.transform.Find("topQuest/bg_award/txt_award").GetComponent<Text>();
            btn_linqu = this.transform.Find("topQuest/guang/btn_linqu").GetComponent<Button>();
            JobMuiltMoney = this.transform.Find("JobMoney");
   
            //旋转影响的座位号
            objPlayer[0].n_RotateSeat = 0;
            objPlayer[1].n_RotateSeat = 1;
            objPlayer[2].n_RotateSeat = 2;
            objPlayer[3].n_RotateSeat = 3;

            Object_DropIconFish = common4.LoadPrefab("BuyuPrefabs/DropIconFish");
            Object_bigWin = common4.LoadPrefab("BuyuPrefabs/gold_bigwin");
            Object_DropIconFishPool = new GameObjectPool();
            Object_DropIconFishPool.SetTemplete(Object_DropIconFish);
        }
        public void Awake()
        {
            if (IsFirst)
            {
                IsFirst = false;
            }
            common3._UIFishingInterface = this;

            FindCompent();
          
            //btn_autofire.onClick.AddListener(() => {
            //    if (PlayerData._bZiDong == false)
            //    {
            //        PlayerData._bZiDong = true;
            //        PlayerData.SetRootbZiDong(true);
            //        btn_autofire.transform.parent.Find("frame").gameObject.SetActive(true);
            //    }
            //    else
            //    {
            //        PlayerData._bZiDong = false;
            //        PlayerData.SetRootbZiDong(false);
            //        btn_autofire.transform.parent.Find("frame" +
            //            "").gameObject.SetActive(false);
            //    }

            //});
            //技能点击事件
            #region

            btn_linqu.onClick.AddListener(() =>
            {
            });
            //btn_suoding.onClick.AddListener(() =>
            //{
            //    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)8);
            //});
         
            //btn_bingdong.onClick.AddListener(() =>
            //{
            //    if (ByData.nModule == 51)
            //    {
            //        MessageBox.ShowPopOneMessage("体验场无法使用该技能!");
            //        return;
            //    }
            //    if (btn_bingdong_mask.enabled)
            //    {
            //        MessageBox.ShowPopOneMessage("技能冷却中!");
            //        return;
            //    }
            //    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)9);
            //});
            //btn_shengdeng.onClick.AddListener(() =>
            //{
            //    if (ByData.nModule == 51)
            //    {
            //        MessageBox.ShowPopOneMessage("体验场无法使用该技能!");
            //        return;
            //    }
            //    if (btn_shengdeng_mask.enabled == true)
            //    {
            //        MessageBox.ShowPopOneMessage("技能冷却中");
            //        return;
            //    }
            //    if (IsCanUseSkill(btn_shengdeng.transform))
            //    {
            //        int NumMax = 0;
            //        for (int i = 2001; i <= 2015; i++)
            //        {
            //            if (GetPathISfish(i) == false)
            //            {
            //                NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)38, i);
            //                StartCoroutine(IESkill_Shengdneg(3f, PlayerData.PlayerId));
            //                return;
            //            }
            //            else
            //            {
            //                NumMax++;
            //                if (NumMax >= 5)
            //                {
            //                    MessageBox.ShowPopOneMessage("黄金鱼召唤已达上限!");
            //                    return;
            //                }
            //            }
            //        }
            //    }
            //});
            #endregion
        }
       //public void SetZiDong(bool state) {
       //     if (state)
       //     {
       //         PlayerData._bZiDong = true;
       //         PlayerData.SetRootbZiDong(true);
       //         btn_autofire.transform.parent.Find("effect").gameObject.SetActive(true);
       //     }
       //     else
       //     {
       //         PlayerData._bZiDong = false;
       //         PlayerData.SetRootbZiDong(false);
       //         btn_autofire.transform.parent.Find("effect").gameObject.SetActive(false);
       //     }
       // }
        //获取路径鱼 是否有鱼
        bool GetPathISfish(long fwqPathID)
        {
            foreach (var item in common.listFish)
            {
                if (item.Value == null)
                {
                    continue;
                }
                if (item.Value.fishState == null)
                {
                    continue;
                }
                //此路径已经有鱼
                if (item.Value.fishState.routeId == fwqPathID)
                {
                    return true;
                }
            }
            return false;
        }
        private void Start()
        {
        }
        float fJianGe;
        void Update()
        {
            //刷新鱼
            if (list_fishInit.Count > 0)
            {
                InitFish(list_fishInit[0]);
                list_fishInit.RemoveAt(0);
            }
            //连击
            if (f_comboCD > 0)
            {
                f_comboCD -= Time.deltaTime;
                if (f_comboCD <= 0)
                {
                    if (num_combo.Num >= 150)
                    {
                        NetMessage.OseeLobby.Req_GetFightNumRequest(PlayerData.PlayerId, num_combo.Num);
                    }
                    PlayerPrefs.SetInt("LianJiCiShu", (int)num_combo.Num);
                    num_combo.Num = 0;
                    num_combo.transform.parent.gameObject.SetActive(false);
                }
            }
            //已经登录的情况下
            if (common.IsAlreadyLogin)
            {
                fJianGe += Time.deltaTime;
                if (fJianGe > 5f)
                {
                    //归0 这样代表5s秒请求一次
                    fJianGe = 0f;
                    //请求
                    NetMessage.Login.Req_PlayerRoomStatusRequest();
                    if (common.listPlayer.Count <= 0)
                    {
                        UIMgr.ShowUI(UIPath.UIMainMenu);
                        UIMgr.CloseAllwithOut(UIPath.UIMainMenu);
                        //common.HaveSence = false;
                        //ByData.IsLoading = false;
                    }
                }
                if (NetMgr.Instance.nLoginState == 2)
                {
                    PlayerData.CanRechiveXiaoXi += Time.deltaTime;
                    if (PlayerData.CanRechiveXiaoXi > 5f)
                    {
                        //间隔超过五秒重新登录
                        PlayerData.CanRechiveXiaoXi = 0f;
                        if (common.IsOpenUILogin)
                        {
                        }
                        else
                        {
                            common.nLoinWay = 3;
                            NetMgr.Instance.OnSpecialLogin();
                        }
                    }
                }
            }

        }
        void RestRoomStatus()
        {
            fJianGe = 0f;
        }
        void ChangeBg(int BgID) {
            for (int i = common2.base_BG.childCount - 1; i >= 0; i--)
            {
                try
                {
                    DestroyImmediate(common2.base_BG.GetChild(i).gameObject);
                }
                catch
                {
                }
            }
            using (zstring.Block())
            {
                GameObject varUIGoBg = common4.LoadPrefab(zstring.Format("SceneBg/Bg/{0}", BgID));
                var mm = Instantiate(varUIGoBg, common2.base_BG);
                mm.transform.localScale = Vector3.one;
                mm.transform.localPosition = Vector3.zero;
            }
        }
        void OnEnable() {
            PlayerData.CanRechiveXiaoXi = 0f;
            IsBoDieSound = false;
            RestRoomStatus();
            RestAllSkill();
            JinZhu(false);//关闭金猪
            common3._UIFishingInterface = this;
            UIMgr.CloseAllwithOutTwo(UIPath.UIByChange,UIPath.UILoadingGame);

            SetMyItem();
            if (ByData.nModule==11)
            {
                SoundLoadPlay.ChangeBgMusic("GameBg0");
            }
            else if (ByData.nModule == 12)
            {
                SoundLoadPlay.ChangeBgMusic("GameBg1");
            }
            else if (ByData.nModule == 13)
            {
                SoundLoadPlay.ChangeBgMusic("GameBg2");
            }
            else
            {
                SoundLoadPlay.ChangeBgMusic("GameBg2");
            }
            EventManager.PropUpdate += SetMyItem;
            EventManager.ChangeAutoTarget += ChangeAutoTarget;
            EventManager.DragonCrystalUpdate += SetMyDragonCrystal;
            EventManager.DoFire += On_DoFire;
            EventManager.ClickDoFire += On_Req_DoFire;
            EventManager.ClickAuto += On_ClickAuto;
            EventManager.DiamondUpdate += On_DiamondUpdate;
            EventManager.ClickPaoTai += On_ClickPaoTai;
            EventManager.IsOnAuto += On_IsOnAuto; 
            if (ByData.nModule==51)
            {
                tyc.gameObject.SetActive(true);
            }
            else
            {
                tyc.gameObject.SetActive(false);
            }
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeRoomPlayerInfoResponse, On_FishingPlayerInfoResponse);//捕鱼玩家信息返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeRoomPlayerInfoListResponse, On_FishingPlayersInfoResponse);//捕鱼玩家列表信息返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeExitRoomResponse, On_FishingExitRoomResponse);//捕鱼退出房间返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeChangeBatteryViewResponse, On_FishingChangeBatteryViewResponse);//捕鱼改变炮台外观返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeChangeBatteryLevelResponse, On_FishingChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChangeBatteryMultResponse, On_FishingChangeBatteryMultResponse);//捕鱼改变炮台倍数返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeFireResponse, On_FishingFireResponse);//捕鱼发射子弹返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeFightFishResponse, On_FishingFightFishResponse);//捕鱼击中鱼类返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeRefreshFishesResponse, On_FishingRefreshFishesResponse);//捕鱼刷新房间鱼类返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeSynchroniseResponse, On_FishingSynchroniseResponse);//捕鱼同步返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeUseSkillResponse, On_FishingUseSkillResponse);//捕鱼使用技能返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeRobotFireResponse, On_FishingChallengeRobotFireResponse);//捕鱼机器人发射子弹返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeCatchBossFishResponse, On_FishingChallengeCatchBossFishResponse);//玩家捕获boss鱼响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeCatchSpecialFishResponse, On_CatchSpecialFishResponse);//捕捉特殊鱼响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeUseBossBugleResponse, On_UseBossBugleResponse);//使用boss号角响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeSyncLockResponse, On_FishingChallengeSyncLockResponse);//龙晶战场同步锁定响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeDoubleKillFishResponse, On_FishingChallengeDoubleKillFishResponse);//龙晶场二次伤害杀死鱼返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeDoubleKillResponse, On_FishingChallengeDoubleKillResponse);//龙晶场二次伤害鱼返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChallengeDoubleKillEndResponse, On_FishingChallengeDoubleKillEndResponse);//龙晶场二次伤害结束返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishBossMultipleResponse, On_FishBossMultipleResponse);//特殊BOSS鱼倍数信息
            UEventDispatcher.Instance.AddEventListener(UEventName.BackgroundSyncResponse, On_BackgroundSyncResponse);//背景同步响应
            NetMessage.OseeFishing.Req_BackgroundSyncRequest();
            //NetMessage.Chanllenge.Req_FishingChallengeSynchroniseRequest();
            Req_FishingReactiveRequest();
        }

        void OnDisable() {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeRoomPlayerInfoResponse, On_FishingPlayerInfoResponse);//捕鱼玩家信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeRoomPlayerInfoListResponse, On_FishingPlayersInfoResponse);//捕鱼玩家列表信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeExitRoomResponse, On_FishingExitRoomResponse);//捕鱼退出房间返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeChangeBatteryViewResponse, On_FishingChangeBatteryViewResponse);//捕鱼改变炮台外观返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeChangeBatteryLevelResponse, On_FishingChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChangeBatteryMultResponse, On_FishingChangeBatteryMultResponse);//捕鱼改变炮台倍数返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeFireResponse, On_FishingFireResponse);//捕鱼发射子弹返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeFightFishResponse, On_FishingFightFishResponse);//捕鱼击中鱼类返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeRefreshFishesResponse, On_FishingRefreshFishesResponse);//捕鱼刷新房间鱼类返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeSynchroniseResponse, On_FishingSynchroniseResponse);//捕鱼同步返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeUseSkillResponse, On_FishingUseSkillResponse);//捕鱼使用技能返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeRobotFireResponse, On_FishingChallengeRobotFireResponse);//捕鱼机器人发射子弹返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeCatchBossFishResponse, On_FishingChallengeCatchBossFishResponse);//玩家捕获boss鱼响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeCatchSpecialFishResponse, On_CatchSpecialFishResponse);//捕捉特殊鱼响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeUseBossBugleResponse, On_UseBossBugleResponse);//使用boss号角响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeSyncLockResponse, On_FishingChallengeSyncLockResponse);//龙晶战场同步锁定响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeDoubleKillFishResponse, On_FishingChallengeDoubleKillFishResponse);//龙晶场二次伤害杀死鱼返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeDoubleKillResponse, On_FishingChallengeDoubleKillResponse);//龙晶场二次伤害鱼返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChallengeDoubleKillEndResponse, On_FishingChallengeDoubleKillEndResponse);//龙晶场二次伤害结束返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishBossMultipleResponse, On_FishBossMultipleResponse);//特殊BOSS鱼倍数信息
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BackgroundSyncResponse, On_BackgroundSyncResponse);//背景同步响应
            common3._UIFishingInterface = null;
            EventManager.PropUpdate -= SetMyItem;
            EventManager.ChangeAutoTarget -= ChangeAutoTarget;
            EventManager.DragonCrystalUpdate -= SetMyDragonCrystal;
            EventManager.DoFire -= On_DoFire;
            EventManager.ClickDoFire -= On_Req_DoFire;
            EventManager.ClickAuto -= On_ClickAuto;
            EventManager.ClickPaoTai -= On_ClickPaoTai;
            EventManager.DiamondUpdate -= On_DiamondUpdate;
            EventManager.IsOnAuto -= On_IsOnAuto;
            UIMgr.DestroyCreateUI();

            UIMgr.CloseUI(UIPath.UIShowGold);
        }
        void SetMyDragonCrystal(long v) {
            if (MyPlayer!=null)
            {
                MyPlayer.ChangeDragonScale(v);// v.ToString();
            }
        }
        void ChangeAutoTarget(Vector2 v2) {
            if (MyPlayer != null)
            {
                Debug.Log(v2.x + "  v2  " + v2.y);
                MyPlayer.ChangeAutoTarget(v2);
            }
        }
        void OnDestory() {
     
        }
        bool IsCanUseSkill(Transform Go)
        {
            if (Go.transform.parent.Find("txtCount").GetComponent<Text>().text == "0")
            {
                //MessageBox.ShowPopMessage("暂时无可用技能");
                UIShop tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                tmp.Setpanel(JieMain.道具商城);
                return false;
            }
            return true;
        }
        void On_DoFire(Vector2 v2)
        {

          //  Debug.Log(v2.x + "角度s" + v2.y);
            if (MyPlayer != null)
            {
                MyPlayer.Req_DoFire(v2);
            }

        }
        void On_ClickPaoTai()
        {
            if (game_ui_skill.gameObject.activeSelf)
            {
                game_ui_skill.gameObject.SetActive(false);
            }
            else
            {
                game_ui_skill.gameObject.SetActive(true);
            }
            
        }
        void On_IsOnAuto(bool isAuto) {
            PlayerData._bZiDong = isAuto;
            PlayerData.SetRootbZiDong(isAuto);
        }
        void On_DiamondUpdate(long diam) {
            if (MyPlayer != null)
            {
                MyPlayer.ChangeDiamond(diam);
            }
        }
        void On_ClickAuto() {
            TipsPos.gameObject.SetActive(false);
        }
        void On_Req_DoFire(Vector2 v2)
        {
            if (MyPlayer != null)
            {
                MyPlayer.Click_Req_DoFire(v2);
            }
        }
        /// <summary>
        /// 捕鱼玩家信息返回 
        /// <summary>
        private void On_FishingPlayerInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeRoomPlayerInfoResponse>();
            //判定旋转
            if (pack.playerInfo.playerId==PlayerData.PlayerId)
            {
                if (pack.playerInfo.seat>1)
                {
                    //自己在上方   就需要旋转
                    PlayerData.IsRotateGame = true;
                    RatatePlayer(true);
                }
                else
                {
                    //自己在下方不需要旋转
                    PlayerData.IsRotateGame = false;
                    RatatePlayer(false);
                }
            }
            //没转
            OnPlayerJoinRoom(pack.playerInfo);
        }
        void RatatePlayer(bool isbool)
        {
            if (isbool)//旋转
            {
                Debug.LogError("旋转");
                objPlayer[0] = transform.Find("rootPlayer/2").GetComponent<player>();
                objPlayer[1] = transform.Find("rootPlayer/3").GetComponent<player>();
                objPlayer[2] = transform.Find("rootPlayer/0").GetComponent<player>();
                objPlayer[3] = transform.Find("rootPlayer/1").GetComponent<player>();

                objPlayer[0].n_RotateSeat = 0;
                objPlayer[1].n_RotateSeat = 1;
                objPlayer[2].n_RotateSeat = 2;
                objPlayer[3].n_RotateSeat = 3;
            }
            else
            {
                objPlayer[0] = transform.Find("rootPlayer/0").GetComponent<player>();
                objPlayer[1] = transform.Find("rootPlayer/1").GetComponent<player>();
                objPlayer[2] = transform.Find("rootPlayer/2").GetComponent<player>();
                objPlayer[3] = transform.Find("rootPlayer/3").GetComponent<player>();
                objPlayer[0].n_RotateSeat = 0;
                objPlayer[1].n_RotateSeat = 1;
                objPlayer[2].n_RotateSeat = 2;
                objPlayer[3].n_RotateSeat = 3;
            }

        }
        /// <summary>
        /// 捕鱼获取房间任务列表返回
        /// <summary>
        private void On_FishingRoomTaskListResponse(UEventContext obj)
        {
        }
        /// <summary>
        /// 捕鱼获取房间任务奖励返回
        /// <summary>
        private void On_FishingGetRoomTaskRewardResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingGetRoomTaskRewardResponse>();
            JobMuiltMoney.gameObject.SetActive(true);

            List<int> Floatpoint = new List<int>() { 18, 54, 90, 126, 162, 198, 234, 270, 306, 342 };
            int tmpAngle = Floatpoint[pack.rewardMulti];

            varZhuan = JobMuiltMoney.Find("zhuan");
            varZhuan.rotation = new Quaternion(0f, 0f, 0f, 0f);

            Dictmp.Clear();
            foreach (var item in pack.rewards)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            varZhuan.DORotate(new Vector3(0f, 0f, -3600f + tmpAngle), 3f, RotateMode.FastBeyond360).SetEase(Ease.Linear).OnComplete(() =>
            {

                CancelInvoke("closeJobMuiltMoney");
                Invoke("closeJobMuiltMoney", 1f);
                CancelInvoke("ShowDictmp");
                Invoke("ShowDictmp", 2f);
            });
        }
        void closeJobMuiltMoney()
        {
            JobMuiltMoney.gameObject.SetActive(false);
        }
        void ShowDictmp()
        {
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            tmp.InitItem(Dictmp, 2, false);
        }
        //重置所有技能状态
        public void RestAllSkill()
        {
            //技能计数
            PlayerData._TwoAttackNum = 0;
            timBingDong = 0;
            timSuoding = 0;
            b_shengdeng = false;
            timShengdeng = 0;

            //自动
            PlayerData._bZiDong = false;
            PlayerData.SetRootbZiDong(false);
            //btn_autofire.transform.parent.Find("effect").gameObject.SetActive(false);
            //冰冻
            common2.transICE.gameObject.SetActive(false);
            //if (btn_suoding_mask != null)
            //{
            //    //CloseSkillCD(btn_suoding.transform);
            //    btn_suoding_mask.gameObject.SetActive(false);
            //}
            //if (btn_shengdeng_mask != null)
            //{
            //    btn_shengdeng_mask.enabled = false;
            //    btn_shengdeng_mask.DOKill();
            //    btn_shengdeng_mask.fillAmount = 1;
            //}
            //if (btn_bingdong_mask != null)
            //{
            //    btn_bingdong_mask.enabled = false;
            //    btn_bingdong_mask.DOKill();
            //    btn_bingdong_mask.fillAmount = 1;
            //}
        }
        int timShengdeng = 0;
        bool b_shengdeng = false;
        //IEnumerator IESkill_Shengdneg(float ftime, long varPlayerId)
        //{
        //    //if (varPlayerId == PlayerData.PlayerId)  //技能CD图片 别人使用时不显示CD
        //    //{
        //    //    b_shengdeng = true;
        //    //    timShengdeng++;
        //    //    btn_shengdeng_mask.enabled = true;
        //    //    btn_shengdeng_mask.fillAmount = 1;
        //    //    //等待使用系统时间 以防切出去
        //    //    float bingdong_connter = Time.realtimeSinceStartup;
        //    //    //等待时间 和CD图片
        //    //    while (Time.realtimeSinceStartup - bingdong_connter < ftime)
        //    //    {
        //    //        btn_shengdeng_mask.fillAmount = 1 - ((Time.realtimeSinceStartup - bingdong_connter) / ftime);
        //    //        yield return new WaitForEndOfFrame();
        //    //    }
        //    //    //计数防止重复时提前关闭
        //    //    timShengdeng--;
        //    //    if (timShengdeng <= 0)
        //    //    {
        //    //        btn_shengdeng_mask.enabled = false;
        //    //        b_shengdeng = false;
        //    //    }
        //    //}
        //}
        void CloseSkillCD(Transform vargo)
        {
            Image img = vargo.parent.Find("Mask").GetComponent<Image>();
            img.enabled = false;
            img.DOKill();
            img.fillAmount = 1;
        }
        private void On_FishingChallengeCatchBossFishResponse(UEventContext obj)
        {
            var data = obj.GetData<FishingChallengeCatchBossFishResponse>();
        }
        /// <summary>
        /// 使用boss号角响应
        /// <summary>
        private void On_UseBossBugleResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeUseBossBugleResponse>();
            if (pack.playerId == PlayerData.PlayerId)
            {
               // DoDaojuSkillCD(0, 60f);
            }
        }

        /// <summary>
        /// 龙晶战场同步锁定响应
        /// <summary>
        private void On_FishingChallengeSyncLockResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeSyncLockResponse>();

            if (pack.userId != PlayerData.PlayerId)
            {

                if (common.listPlayer.ContainsKey(pack.userId))
                {
                }
            }
        }
        /// <summary>
        /// 龙晶场二次伤害杀死鱼返回
        /// <summary>
        private void On_FishingChallengeDoubleKillFishResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeDoubleKillFishResponse>();
        }
        /// <summary>
        /// 龙晶场二次伤害结束返回
        /// <summary>
        private void On_FishingChallengeDoubleKillEndResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeDoubleKillEndResponse>();
        }
        /// <summary>
        /// 背景同步响应
        /// <summary>
        private void On_BackgroundSyncResponse(UEventContext obj)
        {
            var pack = obj.GetData<BackgroundSyncResponse>();
        }
        public float DestoryBgDieTime=0f;        
        /// <summary>
        /// 特殊BOSS鱼倍数信息
        /// <summary>
        private void On_FishBossMultipleResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishBossMultipleResponse>();
            Debug.Log("packtest.fishName" + pack.fishName);
            Debug.Log("packtest.mult" + pack.mult);
            Debug.Log("packtest.money" + pack.money);
            Debug.Log("packtest.fishId" + pack.fishId);
            Debug.Log("packtest.data" + pack.data);
            Debug.Log("packtest.playerId" + pack.playerId);
        
            //玩家已经不存在
            if (GetOnePlayer(pack.playerId) == null)
            {
                return;
            }
            if (pack.playerId==PlayerData.PlayerId)
            {
                PlayerData._TwoAttackNum++;
            }
        }

        /// <summary>
        /// 龙晶场二次伤害鱼返回
        /// <summary>
        private void On_FishingChallengeDoubleKillResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeDoubleKillResponse>();
            Debug.Log("pack.name" + pack.name);
            Debug.Log("pack.modelId:" + pack.modelId);
            Debug.Log("pack.num:" + pack.num);
            Debug.Log("pack.num1:" + pack.num1);
            Debug.Log("pack.num2:" + pack.num2);
            Debug.Log("pack.mult:" + pack.mult);
            Debug.Log("pack.winMoney:" + pack.winMoney);
            Debug.Log("pack.userId:" + pack.userId);
            common.listPlayer[pack.userId].AddWaitDragonScale(pack.modelId, pack.winMoney);
            //玩家已经不存在
            if (GetOnePlayer(pack.userId) == null)
            {
                return;
            }
            if (pack.userId == PlayerData.PlayerId)
            {
                PlayerData._TwoAttackNum++;
            }
            var varname = pack.name;
            var V3 = Vector3.zero;
            try
            {
                V3 = common.WordToUI(common.listFish[pack.modelId].transform.position);
            }
            catch 
            {
                Debug.Log("pack.modelId 不存在"+ pack.modelId);
            }
            //次数里的金币个数
            if (pack.num1>20)
            {
                pack.num1 = pack.num1 % 10+10;
            }
        }
        /// <summary>
        /// 捕捉特殊鱼响应
        /// <summary>
        private void On_CatchSpecialFishResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeCatchSpecialFishResponse>();
        }
        /// <summary>
        /// 捕鱼机器人发射子弹返回
        /// <summary>
        private void On_FishingChallengeRobotFireResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeRobotFireResponse>();
            if (this.gameObject.activeSelf)
            {
                if (common.listPlayer.ContainsKey(pack.robotId))
                {
                   GetOnePlayer(pack.robotId).S_C_ChangeRobotDoFire(pack);
                }
            }
        }
        /// <summary>
        /// 捕鱼使用技能返回
        /// <summary
        private void On_FishingUseSkillResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeUseSkillResponse>();
            Debug.Log("捕鱼使用技能返回"+pack.playerId);
            switch (pack.skillId)
            {
                case 8://锁定
                    if (common.listPlayer.ContainsKey(pack.playerId))
                    {
                        //技能CD图片 只显示自己的别人使用时不显示
                        if (pack.playerId == PlayerData.PlayerId)
                        {
                            common.listPlayer[pack.playerId].Skill_Juemingzhuiji(pack.duration, pack.playerId);
                            //timSuoding++;
                            //if (pack.duration > 0)
                            //{
                            //    SoundLoadPlay.PlaySound("sd_t2_use_suoding_item");
                            //    btn_suoding_mask.gameObject.SetActive(true);
                            //    btn_suoding_mask.fillAmount = 1;
                            //}
                            //else
                            //{
                            //    btn_suoding_mask.gameObject.SetActive(false);
                            //}
                            EventManager.UseLock?.Invoke(pack.duration);
                        }
                        else
                        {
                            common.listPlayer[pack.playerId].Skill_Juemingzhuiji(pack.duration, pack.playerId);
                        }
                    }
                    break;
                case 9://冰冻    
                    if (pack.duration>=15)
                    {
                        SoundLoadPlay.PlaySound("sd_t3_bingdong");
                    }
                    //var go=  Instantiate<GameObject>(common4.LoadPrefab("Effect/skill/9"), common2.BuyuUICanvas);
                    //go.transform.position = Vector3.zero;
                    //for (int i = 0; i < pack.fishIds.Count; i++)
                    //{
                    //    if (common.listFish.ContainsKey(pack.fishIds[i]))
                    //    {
                    //        if (common.listFish[pack.fishIds[i]]!=null)
                    //        {
                    //            if (i< pack.remainDurations.Count)
                    //            {
                    //                common.listFish[pack.fishIds[i]].IceTime = pack.remainDurations[i]/1000f;
                    //                if (common.listFish[pack.fishIds[i]].IceTime>0f)
                    //                {
                    //                    common.listFish[pack.fishIds[i]].Skill_Ice(true);

                    //                    go = Instantiate<GameObject>(common4.LoadPrefab("Effect/skill/icepoint"), common2.BulletPos);
                    //                    go.transform.localPosition =common.WordToUI(common.listFish[pack.fishIds[i]].transform.position);
                    //                    go.transform.localScale = Vector3.one;
                    //                }
                    //            }
                    //        }
                    //    }
                    //}
                    common2.transICE.gameObject.SetActive(true);
                    if (pack.playerId == PlayerData.PlayerId)  //技能CD图片 别人使用时不显示CD
                    {
                        //StartCoroutine(IESkill_BingDongMask(2, pack.playerId));
                    }
                    //StartCoroutine(IESkill_BingDong(pack.duration, pack.playerId));
                    break;              
                case 11://能量暴击
                    if (common.listPlayer.ContainsKey(pack.playerId))
                    {
                        common.listPlayer[pack.playerId].Skill_Baoji(pack.duration);

                        if (pack.playerId == PlayerData.PlayerId)
                        {
                            EventManager.UseFury?.Invoke(pack.duration);
                        }
                        //if (pack.playerId == PlayerData.PlayerId)
                        //{
                        //    if (pack.duration > 0)
                        //    {
                        //        btn_baoji_mask.gameObject.SetActive(true);
                        //        btn_baoji_mask.fillAmount = 1;

                        //    }
                        //    else
                        //    {
                        //        btn_baoji_mask.gameObject.SetActive(false);
                        //        btn_baoji_mask.fillAmount = 1;
                        //    }
                        //}
                    }
             
                    break;
                case 38://神灯
                    iTweenPath ipath = common.GetRootPath().Find(pack.skillFishId.ToString()).GetComponent<iTweenPath>();
                    if (ipath == null)
                    {
                        Debug.LogError("轨迹ID不存在" + pack.skillFishId.ToString());
                        return;
                    }
                    SoundLoadPlay.PlaySound("sd_t3_zhaohuan");
                    //fly过去
                    var flyEffect = common4.LoadPrefab("BuyuPrefabs/zhaohuan_fly");
                    var vargofly = Instantiate(flyEffect, common2.BulletPos);
                    var playerPos = GetOnePlayer(pack.playerId).objPaotai.transform.position;
                    vargofly.transform.position = playerPos;
                    vargofly.transform.DOLocalMove(common.WordToUI(ipath.nodes[0]), 0.3f);
                    break;
                case 50://闪电炮
                        //go = Instantiate<GameObject>(common4.LoadPrefab("Effect/skill/lock_violent"), root_DropEffect);
                    //if (common.listPlayer.ContainsKey(pack.playerId))
                    //{
                    //    common.listPlayer[pack.playerId].Skill_Diancipao(pack.duration);
                    //    if (pack.playerId == PlayerData.PlayerId)
                    //    {
                    //        SoundLoadPlay.PlaySound("sd_t2_guangling_kaishi");
                    //        if (pack.duration > 0)
                    //        {
                    //            btn_diancipao_mask.fillAmount = 1;
                    //            btn_diancipao_mask.gameObject.SetActive(true);
                    //        }
                    //        else
                    //        {
                    //            btn_diancipao_mask.fillAmount = 1;
                    //            btn_diancipao_mask.gameObject.SetActive(false);
                    //        }
                    //    }
                    //}
                    break;
                case 64://钻头炮         
                    Debug.Log("此工程没有钻头炮但服务器依旧回包了" + pack.skillFishId);
                    break;
                case 101://局部爆炸鱼    连环炸弹蟹     
                    Debug.Log("此工程没有局部爆炸鱼 但服务器依旧回包了" + pack.skillFishId);
                    break;
                case 102://闪电鱼      
                    Debug.Log("此工程没有闪电鱼但服务器依旧回包了" + pack.skillFishId);
                    break;
                case 103://黑洞鱼         
                    Debug.Log("此工程没有黑洞鱼但服务器依旧回包了"+ pack.skillFishId);
                    break;
            }
        }
        private int timSuoding = 0;
        //IEnumerator IESkill_Suoding(float ftime, long varPlayerId)
        //{
        //    //技能CD图片 只显示自己的别人使用时不显示
        //    if (varPlayerId == PlayerData.PlayerId)
        //    {
        //        timSuoding++;

        //        Image img = btn_suoding.transform.parent.Find("Mask").GetComponent<Image>();
        //        img.enabled = true;
        //        img.fillAmount = 1;
        //        //等待使用系统时间 以防切出去停止
        //        float suoding_connter = Time.realtimeSinceStartup;
        //        //等待时间 和CD图片
        //        while (Time.realtimeSinceStartup - suoding_connter < ftime)
        //        {
        //            img.fillAmount = 1 - ((Time.realtimeSinceStartup - suoding_connter) / ftime);
        //            yield return new WaitForEndOfFrame();
        //        }
        //        //计数防止重复时提前关闭
        //        timSuoding--;
        //        if (timSuoding <= 0)
        //        {
        //            //结束
        //            img.enabled = false;
        //        }
        //    }
        //}
        private int timBingDong = 0;
        //IEnumerator IESkill_BingDongMask(float ftime, long varPlayerId)
        //{
  
        //        SoundLoadPlay.PlaySound("frezon");
        //        //common2.transICE.gameObject.SetActive(true);
        //        timBingDong++;
        //        btn_bingdong_mask.enabled = true;
        //        btn_bingdong_mask.fillAmount = 1;
        //        //等待使用系统时间 以防切出去
        //        float bingdong_connter = Time.realtimeSinceStartup;
        //        //等待时间 和CD图片
        //        while (Time.realtimeSinceStartup - bingdong_connter < ftime)
        //        {    
        //            btn_bingdong_mask.fillAmount = 1 - (Time.realtimeSinceStartup - bingdong_connter) / ftime;
        //            yield return new WaitForEndOfFrame();
        //        }
        //        //计数防止重复时提前关闭
        //        timBingDong--;
        //        if (timBingDong <= 0)
        //        {
        //            btn_bingdong_mask.enabled = false;
        //        }
            
        //}
        //private int timBingDong = 0;
        //private int OthertimBingDong = 0;
        //IEnumerator IESkill_BingDong(float ftime, long varPlayerId)
        //{
        //    float LengQue = ftime + 5f;
        //    if (varPlayerId == PlayerData.PlayerId)  //技能CD图片 别人使用时不显示CD
        //    {
        //        SoundLoadPlay.PlaySound("frezon");

        //        common2.transICE.gameObject.SetActive(true);
        //        //所有鱼冰冻     
        //        foreach (var it in common.listFish)
        //        {
        //            it.Value.Skill_Ice(true);
        //            //if (it.Value.isBoss == false)
        //            //{
        //            //    //不冻boss
        //            //    it.Value.Skill_Ice(true);
        //            //}
        //        }
        //        b_SkillIce = true;
        //        timBingDong++;
        //        btn_bingdong_mask.enabled = true;
        //        btn_bingdong_mask.fillAmount = 1;
        //        //等待使用系统时间 以防切出去
        //        float bingdong_connter = Time.realtimeSinceStartup;
        //        //等待时间 和CD图片
        //        while (Time.realtimeSinceStartup - bingdong_connter < ftime)
        //        {     //所有鱼冰冻     
        //            foreach (var it in common.listFish)
        //            {
        //                it.Value.Skill_Ice(true);
        //            }
        //            //冰冻多5秒显示
        //            btn_bingdong_mask.fillAmount = 1 - (Time.realtimeSinceStartup - bingdong_connter) / LengQue;
        //            yield return new WaitForEndOfFrame();
        //        }

        //        //计数防止重复时提前关闭
        //        timBingDong--;
        //        if (timBingDong + OthertimBingDong <= 0)
        //        {
        //            //结束
        //            common2.transICE.gameObject.SetActive(false);
        //            //所有鱼解冻
        //            foreach (var it in common.listFish)
        //            {
        //                it.Value.Skill_Ice(false);
        //            }
        //            b_SkillIce = false;
        //        }
        //        if (timBingDong <= 0)
        //        {
        //            //等待时间 和CD图片
        //            while (Time.realtimeSinceStartup - bingdong_connter < LengQue)
        //            {    
        //                btn_bingdong_mask.fillAmount = 1 - ((Time.realtimeSinceStartup - bingdong_connter) / LengQue);
        //                yield return new WaitForEndOfFrame();
        //            }
        //            btn_bingdong_mask.enabled = false;
        //        }
        //    }
        //    else
        //    {
        //        SoundLoadPlay.PlaySound("frezon");
        //        //冰冻背景
        //        common2.transICE.gameObject.SetActive(true);
        //        //所有鱼冰冻     
        //        foreach (var it in common.listFish)
        //        {
        //            //不冻boss
        //            it.Value.Skill_Ice(true);
        //            //if (it.Value.isBoss == false)
        //            //{
        //            //    //不冻boss
        //            //    it.Value.Skill_Ice(true);
        //            //}
        //        }
        //        b_SkillIce = true;
        //        OthertimBingDong++;
        //        //使用系统时间 以防切出去
        //        float bingdong_connter = Time.realtimeSinceStartup;
        //        //等待时间 和CD图片
        //        while (Time.realtimeSinceStartup - bingdong_connter < ftime)
        //        {
        //            //所有鱼冰冻     
        //            foreach (var it in common.listFish)
        //            {
        //                it.Value.Skill_Ice(true);
        //                //if (it.Value.isBoss == false)
        //                //{
        //                //    //不冻boss
        //                //    it.Value.Skill_Ice(true);
        //                //}
        //            }
        //            yield return new WaitForEndOfFrame();
        //        }
        //        //计数防止重复时提前关闭
        //        OthertimBingDong--;
        //        if (timBingDong + OthertimBingDong <= 0)
        //        {
        //            //结束
        //            common2.transICE.gameObject.SetActive(false);
        //            //所有鱼解冻
        //            foreach (var it in common.listFish)
        //            {
        //                it.Value.Skill_Ice(false);
        //            }
        //            b_SkillIce = false;
        //        }
        //    }

        //}
        //int timdiancipao = 0;
        //IEnumerator IESkill_diancipao(float ftime)
        //{
        //    timdiancipao++;
        //    //all_Slider[1].isOn = true;
        //    //all_Slider[0].isOn = false;
        //    //all_Slider[2].isOn = false;
        //    //kuangbao.gameObject.SetActive(true);
        //    // Skillviewright.gameObject.SetActive(false);
        //    Image img = btn_diancipao.transform.parent.Find("Mask").GetComponent<Image>();
        //    img.enabled = true;
        //    img.fillAmount = 1;
        //    float mtime = ftime;
        //    float diancipao_connter = Time.realtimeSinceStartup;
        //    //等待时间 和CD图片
        //    while (Time.realtimeSinceStartup - diancipao_connter < ftime)
        //    {
        //        img.fillAmount = 1 - ((Time.realtimeSinceStartup - diancipao_connter) / ftime);
        //        yield return new WaitForEndOfFrame();
        //    }

        //    timdiancipao--;
        //    if (timdiancipao <= 0)//最后一个
        //    {
        //        //kuangbao.gameObject.SetActive(false);
        //        img.enabled = false;
        //        //Skillviewright.gameObject.SetActive(true);
        //        if (common.listPlayer.ContainsKey(PlayerData.PlayerId))
        //        {
        //            common.listPlayer[PlayerData.PlayerId].NKuangBaoMult = 1;
        //        }
        //    }
        //}
        //int timbaoji = 0;
        //IEnumerator IESkill_baoji(float ftime)
        //{
        //    timbaoji++;
        //    //all_Slider[1].isOn = true;
        //    //all_Slider[0].isOn = false;
        //    //all_Slider[2].isOn = false;
        //    //kuangbao.gameObject.SetActive(true);
        //    // Skillviewright.gameObject.SetActive(false);
        //    Image img = btn_baoji.transform.parent.Find("Mask").GetComponent<Image>();
        //    img.enabled = true;
        //    img.fillAmount = 1;
        //    float mtime = ftime;
        //    float kuangbao_connter = Time.realtimeSinceStartup;
        //    //等待时间 和CD图片
        //    while (Time.realtimeSinceStartup - kuangbao_connter < ftime)
        //    {
        //        img.fillAmount = 1 - ((Time.realtimeSinceStartup - kuangbao_connter) / ftime);
        //        yield return new WaitForEndOfFrame();
        //    }

        //    timbaoji--;
        //    if (timbaoji <= 0)//最后一个
        //    {
        //        //kuangbao.gameObject.SetActive(false);
        //        img.enabled = false;
        //        //Skillviewright.gameObject.SetActive(true);
        //        if (common.listPlayer.ContainsKey(PlayerData.PlayerId))
        //        {
        //            common.listPlayer[PlayerData.PlayerId].NKuangBaoMult = 1;
        //        }
        //    }
        //}
        //private void On_SetMultResponse(long playerId, int mult)
        //{
        //    if (common.listPlayer.ContainsKey(playerId))
        //    {
        //        GetOnePlayer(playerId).NKuangBaoMult = mult;
        //    }
        //}
        int JionRoomTimes = 0;
        void OnPlayerJoinRoom(FishingChallengePlayerInfoProto data)
        {
            if (common.listPlayer.ContainsKey(data.playerId))
            {
                //不为null且座位号相等
                if (common.listPlayer[data.playerId]._pi != null && common.listPlayer[data.playerId]._pi.seat == data.seat)
                {
                    PlayerInfo pi = new PlayerInfo();
                    pi.playerId = data.playerId;
                    pi.name = data.name;
                    pi.money = data.money;
                    pi.diamond = data.diamond;
                    pi.seat = data.seat;
                    pi.sex = data.sex;
                    pi.url = data.headUrl;
                    pi.lottery = data.lottery;
                    if (data.headIndex > 0)
                    {
                        pi.url = data.headIndex.ToString();
                    }
                    pi.isOnline = data.online;
                    pi.nHeadIndex = data.headIndex;
                    pi.nRoleLevel = data.level;

                    common.listPlayer[data.playerId]._pi = pi;

                    //自己数据
                    if (pi.playerId == PlayerData.PlayerId)
                    {
                        //玩家状态请求
                        NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
                        MyPlayer = common.listPlayer[data.playerId];
                        MyPlayer.bAutoFire = false;
                        //if (ByData.nModule == 51)//体验场
                        //{
                        //    MyPlayer.ChangeDragonScale(data.money);//
                        //}
                        //else
                        //{
                        //    MyPlayer.ChangeDragonScale(PlayerData.DragonCrystal);//
                        //}
                        MyPlayer.ChangeDragonScale(PlayerData.DragonCrystal);//
                        MyPlayer.ChangeDiamond(PlayerData.Diamond);
                        common.listPlayer[data.playerId].ChangeJiangQuan(data.lottery);
                        //炮台
                        PlayerData.PaoViewIndex = data.viewIndex;
                        PlayerData.WingIndex = data.wingIndex;
                        MyPlayer.InitPaoAndWinOther(data.viewIndex, data.wingIndex, data.batteryLevel);
                        CatchTips.gameObject.SetActive(false);

                        if (JionRoomTimes<=0)
                        {
                            //座位提示
                            TipsPos.gameObject.SetActive(true);
                            TipsPos.transform.SetParent(MyPlayer.transform);
                            game_ui_skill.transform.SetParent(MyPlayer.transform);
                            TipsPos.SetAsFirstSibling();
                            TipsPos.transform.localScale = Vector3.one;
                            game_ui_skill.transform.localScale = Vector3.one;
                            Vector3 pos = MyPlayer.transform.Find("imgDizuo").localPosition;
                            TipsPos.transform.localPosition = pos;

                            if (MyPlayer.name == "2" || MyPlayer.name == "3")
                            {
                                game_ui_skill.transform.localPosition = new Vector3(pos.x, pos.y - 200f, pos.z);
                                TipsPos.transform.localPosition = new Vector3(pos.x, pos.y - 50f, pos.z);
                                TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                            }
                            else
                            {
                                game_ui_skill.transform.localPosition = new Vector3(pos.x, pos.y + 200f, pos.z);
                                TipsPos.transform.localPosition = new Vector3(pos.x, pos.y + 50f, pos.z);
                                TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                            }
                            JionRoomTimes++;
                        }
                      
                    }
                    else
                    {
                        common.listPlayer[data.playerId].ChangeDragonScale(data.money);
                        common.listPlayer[data.playerId].ChangeDiamond(long.Parse(data.diamond));
                        common.listPlayer[data.playerId].ChangeJiangQuan(data.lottery); 
                        common.listPlayer[data.playerId].InitPaoAndWinOther(data.viewIndex, data.wingIndex, data.batteryLevel);
                    }
                }
                else
                {
                    common.listPlayer[data.playerId].ClearThis();
                    //座位号不相等则移除
                    common.listPlayer.Remove(data.playerId);
                }
            }

            //增加玩家
            if (!common.listPlayer.ContainsKey(data.playerId))
            {
                PlayerInfo pi = new PlayerInfo();
                pi.playerId = data.playerId;
                pi.name = data.name;
                pi.money = data.money;
                pi.diamond = data.diamond;
                pi.seat = data.seat;
                pi.sex = data.sex;
                pi.url = data.headUrl;
                pi.lottery = data.lottery;
                if (data.headIndex > 0)
                {
                    pi.url = data.headIndex.ToString();
                }
                pi.isOnline = data.online;
                pi.nHeadIndex = data.headIndex;
      
                pi.nRoleLevel = data.level;
                common.listPlayer.Add(pi.playerId, objPlayer[pi.seat]);
                objPlayer[pi.seat]._pi = pi;

                //自己数据
                if (pi.playerId == PlayerData.PlayerId)
                {
                    //玩家状态请求
                    NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
                    MyPlayer = objPlayer[pi.seat];
                    MyPlayer.bAutoFire = false;
                    //if (ByData.nModule==51)//体验场
                    //{
                    //    MyPlayer.ChangeDragonScale(data.money);//
                    //}
                    //else
                    //{
                    //    MyPlayer.ChangeDragonScale(PlayerData.DragonCrystal);//
                    //}
                    MyPlayer.ChangeDragonScale(PlayerData.DragonCrystal);//

                    MyPlayer.ChangeDiamond(PlayerData.Diamond);
                    MyPlayer.ChangeJiangQuan(data.lottery);
                    //翅膀
                    PlayerData.PaoViewIndex = data.viewIndex;
                    PlayerData.WingIndex = data.wingIndex;
                    //炮台
                    objPlayer[pi.seat].InitPaoAndWin(data.viewIndex, data.wingIndex, data.batteryLevel);
                    CatchTips.gameObject.SetActive(false);
                    //座位提示
                    TipsPos.gameObject.SetActive(true);
                    TipsPos.transform.SetParent(MyPlayer.transform);
                    game_ui_skill.transform.SetParent(MyPlayer.transform);
                    game_ui_skill.transform.localScale = Vector3.one;
                    TipsPos.SetAsFirstSibling();
                    TipsPos.transform.localScale = Vector3.one;
                    Vector3 pos = MyPlayer.transform.Find("imgDizuo").localPosition;
                    TipsPos.transform.localPosition = pos;
                    if (MyPlayer.name == "2" || MyPlayer.name == "3")
                    {
                        game_ui_skill.transform.localPosition = new Vector3(pos.x, pos.y - 200f, pos.z);
               
                        TipsPos.transform.localPosition = new Vector3(pos.x, pos.y - 50f, pos.z);
                        TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                    }
                    else
                    {
                        game_ui_skill.transform.localPosition = new Vector3(pos.x, pos.y + 200f, pos.z);
                        TipsPos.transform.localPosition = new Vector3(pos.x, pos.y + 50f, pos.z);
                        TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                    }
                }
                else
                {
                    objPlayer[pi.seat].ChangeDragonScale(data.money);
                    objPlayer[pi.seat].ChangeDiamond(long.Parse(data.diamond));
                    objPlayer[pi.seat].InitPaoAndWin(data.viewIndex, data.wingIndex, data.batteryLevel);
                    common.listPlayer[data.playerId].ChangeJiangQuan(data.lottery);
                }
            }
            for (int i = 0; i < 4; i++)
            {
                if (objPlayer[i].isInit)
                {
                    trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(false);
                }
                else
                {
                    trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(true);
                }
            }
           
        }
        /// <summary>
        /// 检查爆炸距离
        /// </summary>
        /// <returns>true 在</returns>
        bool CheckBooArea(long SkillFish, long NextFish)
        {
            if (SkillFish > 0)
            {
                if (common.listFish.ContainsKey(SkillFish))
                {
                    float m = Mathf.Abs(Mathf.Abs(common.listFish[SkillFish].transform.localPosition.x) - Mathf.Abs(common.listFish[NextFish].transform.localPosition.x));
                    float n = Mathf.Abs(Mathf.Abs(common.listFish[SkillFish].transform.localPosition.y) - Mathf.Abs(common.listFish[NextFish].transform.localPosition.y));
                    if (Mathf.Abs(m) < 200 && Mathf.Abs(n) < 200)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                return false;
            }
            else
                return false;
        }
        /// <summary>
        /// 检查爆炸距离
        /// </summary>
        /// <returns>true 在</returns>
        bool CheckBooArea(Vector3 vectorPoint, long NextFish)
        {
            if (common.listFish.ContainsKey(NextFish))
            {
                float m = Mathf.Abs(Mathf.Abs(vectorPoint.x) - Mathf.Abs(common.WordToUI(common.listFish[NextFish].transform.position).x));
                float n = Mathf.Abs(Mathf.Abs(vectorPoint.y) - Mathf.Abs(common.WordToUI(common.listFish[NextFish].transform.position).y));
                if (Mathf.Abs(m) < 200 && Mathf.Abs(n) < 200)
                {
                    return true;
                }
                else
                {
                    return false;
                }

                return false;
            }
            else
                return false;
        }
        /// <summary>
        /// 捕鱼玩家列表信息返回
        /// <summary>
        private void On_FishingPlayersInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeRoomPlayerInfoListResponse>();
            for (int i = 0; i < pack.playerInfos.Count; i++)
            {
                //判定旋转
                if (pack.playerInfos[i].playerId == PlayerData.PlayerId)
                {
                    if (pack.playerInfos[i].seat > 1)
                    {
                        //自己在上方   就需要旋转
                        PlayerData.IsRotateGame = true;
                        RatatePlayer(true);
                    }
                    else
                    {
                        //自己在下方不需要旋转
                        PlayerData.IsRotateGame = false;
                        RatatePlayer(false);
                    }
                }
            }
            for (int i = 0; i < pack.playerInfos.Count; i++)
            {
                var data = pack.playerInfos[i];
                OnPlayerJoinRoom(data);
            }
            Debug.Log("pack.playerInfos.Count"+ pack.playerInfos.Count);

            UIMgr.CloseUI(UIPath.UILoadingGame);
        }
        private void InitFish(FishingChallengeFishInfoProto ms)
        {
            if (!common.listFish.ContainsKey(ms.id))
            {
                if (common4.dicFishConfig.ContainsKey(ms.fishId))
                {
                    //召唤的黄金鱼
                    ms.clientLifeTime = ms.clientLifeTime / 1000f;
                    //生成鱼
                    commonLoad.GetOneAnysFish(common4.GetFishModleID(common4.dicFishConfig[ms.fishId].name), ms.id, Root3D.Instance.rootFish, new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, ms.lottery,ms.chuQiZhiShengFlag));// (go) =>
                    if (common4.GetFishCanShu(ms.fishId).isTwoAttack == true)
                    {
                        if (ms.durationRefreshFlag==false)
                        {
                            if (ms.clientLifeTime < 3) //时间小于1
                            {
                                using (zstring.Block())
                                {
                                    UIMgr.ShowAsynCreatePerfab(zstring.Format("BossComing/boss_coming_{0}", common4.GetFishModleID(ms.fishId)), common2.ApperAnimation);
                                }
                            }
                        }
                    }           
                }
                else
                {
                    using (zstring.Block())
                    {
                        Debug.LogError(zstring.Format("鱼id不存在于dicFishConfig:{0}", ms.fishId));
                    }
                }
            }
            else
            {
            }
        }
        IEnumerator HornInitFish(FishingChallengeFishInfoProto ms)
        {
            //在技能回包里显示神灯飞过去的线
            iTweenPath ipath = common.GetRootPath().Find(ms.routeId.ToString()).GetComponent<iTweenPath>();
            if (ipath == null)
            {
                Debug.LogError("轨迹ID不存在" + ms.routeId.ToString());
            }
            yield return new WaitForSeconds(0.3f);
            //是黄金鱼地方
            var sdEffect = common4.LoadPrefab("BuyuPrefabs/zhaohuan");
            var vargo = Instantiate(sdEffect);
            vargo.transform.position = ipath.nodes[0];
            yield return new WaitForSeconds(1f);
            var goFish = commonLoad.GetOneAynsFish(common4.GetFishModleID(common4.dicFishConfig[ms.fishId].name), Root3D.Instance.rootFish);
            var mf = goFish.GetComponent<fish>();
            if (mf != null)
            {
                var mp = new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, ms.lottery,ms.chuQiZhiShengFlag);
                mf.Init(mp);
            }
            else
            {
                Debug.Log("mf==null");
            }
        }
        public void ClearDestory()
        {
            //清空鱼
            Dictionary<long, fish>.Enumerator it = common.listFish.GetEnumerator();
            while (it.MoveNext())
            {
                try
                {
                    DestroyImmediate(it.Current.Value.gameObject);
                }
                catch
                {
                }
            }
            common.listFish.Clear();

            //清空子弹
            if (Root3D.Instance.rootFish != null)
            {
                if (Root3D.Instance.rootFish.childCount > 0)
                {
                    for (int i = Root3D.Instance.rootFish.childCount - 1; i >= 0; i--)
                    {
                        try
                        {
                            DestroyImmediate(Root3D.Instance.rootFish.GetChild(i).gameObject);
                        }
                        catch
                        {
                        }
                    }
                }
            }
            if (common2.BulletPos != null)
            {
                if (common2.BulletPos.childCount > 0)
                {
                    for (int i = common2.BulletPos.childCount - 1; i >= 0; i--)
                    {
                        try
                        {
                            DestroyImmediate(common2.BulletPos.GetChild(i).gameObject);
                        }
                        catch
                        {
                        }
                    }
                }
            }
            if (GoldNormal != null)
            {
                if (GoldNormal.childCount > 0)
                {
                    for (int i = GoldNormal.childCount - 1; i >= 0; i--)
                    {
                        DestroyImmediate(GoldNormal.GetChild(i).gameObject);
                    }
                }
            }
            InitGame();
            Resources.UnloadUnusedAssets();
            System.GC.Collect();
        }
        void InitGame()
        {
        }
    
        public void DestroyThisRoomToLogin() {
            //如果是自己就退出房间并清空数据            
            UIMgr.DestroyCreateUI();
            UIMgr.DestroyAllCreatePerfab();
            //先删除所有子弹
            for (int i = common2.BulletPos.childCount - 1; i > 0; i--)
            {
                GameObject.Destroy(common2.BulletPos.GetChild(i));
            }
            //卸载场景
            try
            {
                UIExitGame uiExitGame = UIMgr.ShowUISynchronize(UIPath.UIExitGame).GetComponent<UIExitGame>();
                uiExitGame.StartLoad("ZBuyuRoom", UIPath.UILogin);
            }
            catch
            {
            }
            UIMgr.ShowUI(UIPath.UILogin);
            Root3D.Instance.ShowAllObject(false);
            for (int i = 0; i < objPlayer.Count; i++)
            {
                objPlayer[i].ClearThis();
            }
            JionRoomTimes = 0;
            DestoryBgDieTime = 0;
            ClearDestory();
            UIMgr.CloseUI(UIPath.UIByChange);
        }
        /// <summary>
        /// 捕鱼退出房间返回
        /// <summary>
        private void On_FishingExitRoomResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeExitRoomResponse>();
            if (pack.playerId == PlayerData.PlayerId)
            {
                //如果是自己就退出房间并清空数据           
                UIMgr.DestroyCreateUI();
                UIMgr.DestroyAllCreatePerfab();
                //先删除所有子弹
                for (int i = common2.BulletPos.childCount-1; i > 0; i--)
                {
                    GameObject.Destroy(common2.BulletPos.GetChild(i)); 
                }
                //卸载场景
                try
                {
                    UIExitGame uiExitGame = UIMgr.ShowUISynchronize(UIPath.UIExitGame).GetComponent<UIExitGame>();
                    uiExitGame.StartLoad("ZBuyuRoom", UIPath.UIMainMenu);
                }
                catch
                {

                }
                
                UIMgr.ShowUI(UIPath.UIMainMenu);
                Root3D.Instance.ShowAllObject(false);

                for (int i = 0; i < objPlayer.Count; i++)
                {
                    objPlayer[i].ClearThis();
                }
                JionRoomTimes = 0;
                DestoryBgDieTime = 0;
                ClearDestory();
                UIMgr.CloseUI(UIPath.UIByChange);

                Root3D.Instance.DebugString(PlayerData.PlayerId + "FishingChallengeExitRoomResponse");
            }
            else
            {
                //其他人退出，更新当前房间信息
                if (common.listPlayer.ContainsKey(pack.playerId))
                {
                    common.listPlayer[pack.playerId].ClearThis();//.SetActive(false);
                    for (int i = 0; i < 4; i++)
                    {
                        //等待加入按钮更改
                        for (int m = 0; m < 4; m++)
                        {
                            //btn_ChanegSet[m].gameObject.SetActive(false);
                            if (objPlayer[m].isInit)
                            {
                                trans_waitjion[int.Parse(objPlayer[m].name)].gameObject.SetActive(false);
                            }
                            else
                            {
                                trans_waitjion[int.Parse(objPlayer[m].name)].gameObject.SetActive(true);
                            }
                        }
                    }
                    common.listPlayer.Remove(pack.playerId);
                }
            }
        }
        /// <summary>
        /// 捕鱼改变炮台外观返回
        /// <summary>
        private void On_FishingChangeBatteryViewResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeChangeBatteryViewResponse>();

        }
        public void ReconnectionInRoom() {
         
            if (common2.BulletPos != null)
            {
                common2.BulletPos.gameObject.SetActive(true);
            }
            //删除二次伤害界面
            UIMgr.DestroyCreateUI();
            //打开炮台(防止被激光炮什么的给关了)
            for (int i = 0; i < objPlayer.Count; i++)
            {
                objPlayer[i].objPaotai.gameObject.SetActive(true);
            }
            //删除数据
            ClearDestory();
            IsBoDieSound = false;
            
            RestAllSkill();
            //再激活一下  只需更新玩家信息  和鱼信息
            Req_FishingReactiveRequest();
        }
        public void OpenReConnecting(bool state)
        {
            maskReConnecting.gameObject.SetActive(state);
        }
        /// <summary>
        /// 捕鱼同步返回
        /// <summary>
        private void On_FishingSynchroniseResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeSynchroniseResponse>();
            for (int i = 0; i < pack.fishInfos.Count; i++)
            {
                var it = pack.fishInfos[i];
                if (common4.dicFishConfig.ContainsKey(it.fishId))
                {
                    InitFish(it);
                }
                else
                {
                    Debug.LogError("配置文件没有找到此鱼ID" + it.fishId);
                }
            }
            maskReConnecting.gameObject.SetActive(false);
        }
        /// <summary>
        /// 捕鱼刷新房间鱼类返回
        /// <summary>
        private void On_FishingRefreshFishesResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeRefreshFishesResponse>();
            for (int i = 0; i < pack.fishInfos.Count; i++)
            {
                //Debug.Log("pack.fishInfos" + pack.fishInfos[i].id);
                //Debug.Log("pack.fishInfos" + pack.fishInfos[i].fishId);
                //Debug.Log("pack.fishInfos" + pack.fishInfos[i].routeId);
                //Debug.Log("pack.fishInfos" + pack.fishInfos[i].createTime);
                //Debug.Log(string.Format("pack.fishInfos=name:{0}", common4.dicFishConfig[pack.fishInfos[i].fishId].name));
                var it = pack.fishInfos[i];

                //Debug.Log(string.Format("刷新=fishId:{0},id:{1},routeId:{2},clientLifeTime:{3},createTime:{4}", it.fishId, it.id, it.routeId,  it.clientLifeTime, it.createTime));
                if (common4.dicFishConfig.ContainsKey(it.fishId))
                {
                    list_fishInit.Add(it);
                } 
                else
                {
                    Debug.LogWarning(common4.dicFishConfig.Count);
                    Debug.LogWarning("配置文件没有找到此鱼ID" + it.fishId);
                }
            }
        }
        /// <summary>
        /// 捕鱼完成房间目标返回
        /// <summary>
        private void On_FishingFinishRoomGoalResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingFinishRoomGoalResponse>();
            string strItemName = "";
            switch (pack.rewardItem.itemId)
            {
                case 1:
                    strItemName = "金币";
                    break;
                case 2:
                    strItemName = "银行金币";
                    break;
                case 3:
                    strItemName = "奖券";
                    break;
                case 4:
                    strItemName = "钻石";
                    break;
            }
            MessageBox.ShowPopMessage(string.Format("恭喜您完成目标，获得{0}{1}", pack.rewardItem.itemNum, strItemName));
        }
      
        /// <summary>
        /// 捕鱼发射子弹返回
        /// <summary>
        private void On_FishingFireResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeFireResponse>();
            Debug.Log("发射子弹"+pack.playerId+pack.restMoney);
            if (common.listPlayer.ContainsKey(pack.playerId))
            {
                common.listPlayer[pack.playerId].ChangeDragonScale(pack.restMoney);//("fish-playerMoney", pack.restMoney);// = pack.gold.ToString();

                if (pack.playerId != PlayerData.PlayerId)
                {
                    //objPlayer[common.listPlayer[pack.playerId].pos].Other_Re_DoFire(pack);
                    Debug.LogWarning("Other_Re_DoFire");
                    common.listPlayer[pack.playerId].Other_Re_DoFire(pack.angle, pack.fireId);
                }
                else
                {
                    //自已经发了 就可以不同步了
                }
            }

        }
        /// <summary>
        /// 捕鱼改变炮台等级返回
        /// <summary>
        private void On_FishingChangeBatteryLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeChangeBatteryLevelResponse>();
            if (objPlayer != null)
            {
                if (common.listPlayer.ContainsKey(pack.playerId))
                {
                    //Debug.Log("pack.targetLevel 回包"+ pack.level);
                    common.listPlayer[pack.playerId].nPower = pack.level;
                }

            }
        }
        /// <summary>
        /// 捕鱼改变炮台倍数返回
        /// <summary>
        private void On_FishingChangeBatteryMultResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChangeBatteryMultResponse>();
        }
        ///// <summary>
        ///// 改变狂暴倍数响应
        ///// <summary>
        //private void On_FishingChangeCritMultResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<FishingChangeCritMultResponse>();
        //    if (pack.mult == 1)
        //    {
        //        all_Slider[0].isOn = true;
        //        all_Slider[1].isOn = false;
        //        all_Slider[2].isOn = false;
        //    }
        //    if (pack.mult == 2)
        //    {
        //        all_Slider[1].isOn = true;
        //        all_Slider[0].isOn = false;
        //        all_Slider[2].isOn = false;
        //    }
        //    if (pack.mult == 4)
        //    {
        //        all_Slider[2].isOn = true;
        //        all_Slider[1].isOn = false;
        //        all_Slider[0].isOn = false;
        //    }
        //    objPlayer[common.listPlayer[PlayerData.PlayerId].pos].NKuangBaoMult = pack.mult;
        //}

        ///// <summary>
        ///// 解锁炮台等级提示返回
        ///// <summary>
        //private void On_UnlockBatteryLevelHintResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<UnlockBatteryLevelHintResponse>();
        //    Debug.Log("UnlockBatteryLevelHintResponse"+ pack.nextLevel+"sss"+ pack.cost);
        //    txt_jeiSuoBei.text = pack.nextLevel.ToString() + "倍";
        //    txt_bei.text = "解锁  " + pack.nextLevel.ToString() + "  倍炮";
        //    txt_beiUp.text = "解锁  " + pack.nextLevel.ToString() + "  倍炮";
        //    if (common.myCaiLiao.ContainsKey(23))
        //    {
        //        txt_neednum.text = common.myCaiLiao[23] + "/" + pack.cost.ToString();
        //        txt_neednumUp.text = common.myCaiLiao[23] + "/" + pack.cost.ToString();
        //    }
        //   // tra_jiesuoUp.transform.Find("money").GetComponent<Text>().text = pack.rewardGold.itemNum.ToString();
        //    next_bei = pack.nextLevel;
        //    next_cost = pack.cost;
        //    if (pack.nextLevel >= common.dicPaoFwConfig[MaxPaoFw].Maxlevel)
        //    {
        //        if (toggle_jeisuo.isOn)
        //        {
        //            tra_jiesuo.gameObject.SetActive(true);
        //            tra_jiesuoUp.gameObject.SetActive(false);
        //        }
        //        else
        //        {
        //            tra_jiesuo.gameObject.SetActive(false);
        //            tra_jiesuoUp.gameObject.SetActive(false);
        //        }
      
        //        txt_bei.text = "已达到最高等级炮台";
        //        txt_neednum.text = "暂无/暂无";
        //    }
        //    ChangeYuGu();
        //}
        //public void ChangeYuGu()
        //{            
        //    if (common.myCaiLiao.ContainsKey(23) && common.myCaiLiao[23] >= CanChangeYugu)
        //    {
        //        CanChangeYugu = common.myCaiLiao[23];
        //    }
        //    else
        //    {
        //        return;
        //    }
        //    if (PlayerData.PaoLevel >= 1000 && PlayerData.PaoLevel < common.dicPaoFwConfig[MaxPaoFw].Maxlevel)//铸造不更新
        //    {
        //        return;
        //    }
        //    if (PlayerData.PaoLevel >= common.dicPaoFwConfig[MaxPaoFw].Maxlevel)//最高不更新
        //    {
        //        return;
        //    }

        //    if (PlayerData.PaoLevel < 1000)
        //    {
        //        if (toggle_jeisuo.isOn == true) //打开状态
        //        {
        //            if (common.myCaiLiao.ContainsKey(23))
        //            {
        //                if (common.myCaiLiao[23] < next_cost)
        //                {
        //                    //材料不够 打开提示
        //                    tra_jiesuo.gameObject.SetActive(true);
        //                    tra_jiesuoUp.gameObject.SetActive(false);
        //                    if (common.myCaiLiao.ContainsKey(23))
        //                    {
        //                        txt_neednum.text = new StringBuilder().Append(common.myCaiLiao[23].ToString()).Append("/").Append(next_cost).ToString();
        //                    }
        //                    else
        //                    {
        //                        //不存在则为0
        //                        txt_neednum.text = new StringBuilder().Append("0").Append("/").Append(next_cost).ToString();
        //                    }
        //                }
        //                else
        //                {
        //                    //材料足够 打开Up
        //                    tra_jiesuo.gameObject.SetActive(false);
        //                    tra_jiesuoUp.gameObject.SetActive(true);
        //                    if (common.myCaiLiao.ContainsKey(23))
        //                    {
        //                        txt_neednumUp.text = new StringBuilder().Append(common.myCaiLiao[23].ToString()).Append("/").Append(next_cost).ToString();
        //                    }
        //                    else
        //                    {
        //                        txt_neednumUp.text = new StringBuilder().Append("0").Append("/").Append(next_cost).ToString();
        //                    }
        //                }
        //            }
        //        }
        //        else
        //        {
        //            if (common.myCaiLiao.ContainsKey(23))
        //            {
        //                if (common.myCaiLiao[23] >= next_cost)
        //                {
        //                    if (waitTimeThe==null)
        //                    {
        //                        waitTimeThe = tmpWaitTimeThe();
        //                    }
        //                    else
        //                    {
        //                        waitTimeThe.Reset();
        //                    }
        //                    StartCoroutine(waitTimeThe);
        //                    tra_jiesuoUp.gameObject.SetActive(true);
        //                    tra_jiesuo.gameObject.SetActive(false);
        //                }
        //            }
        //        }
        //    }
        //}
        
        IEnumerator waitTimeThe;
        IEnumerator tmpWaitTimeThe() { 
            yield return new WaitForSeconds(3f);
        }
        /// <summary>
        /// 捕鱼机器人发射子弹返回
        /// <summary>
        private void On_FishingRobotFireResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingRobotFireResponse>();
            if (this.gameObject.activeSelf)
            {
                if (common.listPlayer.ContainsKey(pack.robotId))
                {
                    common.listPlayer[pack.robotId].S_C_NormalRobotDoFire(pack);
                }
            }
        }
        /// <summary>
        /// 检查被闪电攻击的鱼是否在屏幕内
        /// </summary>
        /// <returns>true 在</returns>
        bool CheckFlashFishInScreen(long nFlashFish)
        {
            if (nFlashFish > 0)
            {
                if (common.listFish.ContainsKey(nFlashFish)&& common.listFish[nFlashFish]!=null)
                {
                    float m = Mathf.Abs(common.WordToUI(common.listFish[nFlashFish].transform.position).x);
                    float n = Mathf.Abs(common.WordToUI(common.listFish[nFlashFish].transform.position).y);
                    if (Mathf.Abs(m) < 1280 && Mathf.Abs(n) < 720)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                return false;
            }
            else
                return false;
        }
        /// <summary>
        /// 捕鱼击中鱼类返回
        /// <summary>
        private void On_FishingFightFishResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChallengeFightFishResponse>();
            if (common.listPlayer.ContainsKey(pack.playerId)==false)
            {
                return;
            }
            var vPlayer = common.listPlayer[pack.playerId];
    
            //存在此鱼-----------------
            if (common.listFish.ContainsKey(pack.fishId))
            {
                //添加 等待的金币 且必须在前面----------
                if (pack.dropMoney > 0)
                {
                    common.listPlayer[pack.playerId].AddWaitDragonScale(pack.fishId, pack.dropMoney);
                }
                //更新龙晶
                common.listPlayer[pack.playerId].ChangeDragonScale(pack.restMoney);

                //获取鱼名
                string fishname = common4.dicFishConfig[common.listFish[pack.fishId].fishState.fishId].name;
                if (fishname == "全屏炸弹")
                {
                    ShowFishIconMoney(pack.playerId, fishname, pack.fishId, pack.dropMoney);
                }
        
                //鱼位置
                Vector2 pos = common.WordToUI(common.listFish[pack.fishId].transform.position);
                
                if (pack.chuQiZhiShengFlag)//出奇制胜
                {
                    UICqzsTA tmp1 = UIMgr.ShowCreateUI(UIPath.UICqzsTA).GetComponent<UICqzsTA>();
                    tmp1.transform.SetAsFirstSibling();

                    tmp1.Init(pos,pack.refFishList);

                    //显示出奇制胜
                    ShowFishIconMoneyCqzs(pack.playerId, fishname, pack.fishId, pack.dropMoney);
                }

                //销毁所有鱼-----------------
                if (pack.refFishList.Count > 0)
                {
                    for (int i = 0; i < pack.refFishList.Count; i++)
                    {
                        if (common.listFish.ContainsKey(pack.refFishList[i].fishId))
                        {
                            if (vPlayer != null)
                            {
                                //鱼位置 
                                Vector2 postmp = common.WordToUI(common.listFish[pack.refFishList[i].fishId].transform.position);

                                OnlyMoneyFly(postmp, pack.fishId, pack.playerId, vPlayer.objPaotai.transform.position, pack.dropMoney, vPlayer.n_nameSeat);
                            }
                            common.listFish[pack.refFishList[i].fishId].FishDieT();
                        }
                    }
                }

                FourTimePlaySound(fishname);

                if (common4.GetFishCanShu(fishname).isTwoAttack == true)//为二次伤害鱼 或技能鱼
                {
                    common.listFish[pack.fishId].FishDieT();
                    return;
                }
                //掉落金币
                if (pack.dropMoney > 0)
                {
                    if (common.listPlayer.ContainsKey(pack.playerId))
                    {
                        if (vPlayer != null)
                        {
                            AnimalMoneyDrop(pos, pack.fishId, pack.playerId, vPlayer.objPaotai.transform.position, pack.dropMoney, vPlayer.n_nameSeat, fishname);
                        }
                    }
                }
                if (pack.dropItems.Count > 0)
                {
                    //掉落道具
                    foreach (var item in pack.dropItems)
                    {
                        AnimalItemDropJQ(pack.playerId, item.itemId, pos, common.listPlayer[pack.playerId].objPaotai.transform.position, item.itemNum, item.originalNumber);
                    }
                }
                //连击数判断
                if (pack.playerId == PlayerData.PlayerId)//自己
                {
                    long nCombo = num_combo.Num + 1;
                    num_combo.Init(nCombo, isShowAni: true, bFlash: true);
                    Transform pa = num_combo.transform.parent;
                    if (nCombo > 1)
                    {
                        pa.transform.position = MyPlayer.objPaotai.transform.position;
                        pa.localScale = Vector3.zero;
                        pa.gameObject.SetActive(true);
                        pa.DOScale(1, 0.5f).SetEase(Ease.OutBack);
                        f_comboCD = 2;
                    }
                    long mfishId = common.listFish[pack.fishId].fishState.fishId;
                    if (common4.GetFishModleID(mfishId) >= 2012)
                    {
                        common4.SetCamreRoomIN();
                    }
                }
                //销毁鱼
                common.listFish[pack.fishId].FishDieT();
            }
            else
            {     
                //销毁所有鱼-----------------
                if (pack.refFishList.Count > 0)
                {
                    for (int i = 0; i < pack.refFishList.Count; i++)
                    {
                        if (common.listFish.ContainsKey(pack.refFishList[i].fishId))
                        {
                            if (vPlayer != null)
                            {
                                //鱼位置
                                Vector2 pos = common.WordToUI(common.listFish[pack.refFishList[i].fishId].transform.position);

                                OnlyMoneyFly(pos, pack.fishId, pack.playerId, vPlayer.objPaotai.transform.position, pack.dropMoney, vPlayer.n_nameSeat);
                            }
                            common.listFish[pack.refFishList[i].fishId].FishDieT();
                        }
                    }
                }
                Debug.LogWarning("pack.playerId" + pack.playerId + "不存在了");
                return;
            }
        }

        // 道具掉落动画
        public void AnimalItemDropJQ(long playerID,int itemId, Vector3 pos, Vector3 endPos, long itemCount,long originalNumber)
        {
            Debug.Log("道具掉落"+itemId);
            Debug.Log("道具掉落" + itemCount);
            Debug.Log("道具掉落" + originalNumber);
            long varitemCount;
            if (itemCount > 20)
            {
                varitemCount = 20;
            }
            else
            {
                varitemCount = itemCount;
            }
            for (int i = 0; i < varitemCount; i++)
            {
                DOVirtual.DelayedCall(UnityEngine.Random.Range(0f, 0.5f), () =>
                {
                    using (zstring.Block())
                    {
                        GameObject goImage = common4.LoadPrefab(zstring.Format("DropItem/{0}", itemId));// Resources.Load<Sprite>(zstring.Format("item/{0}", itemId));
                        goImage.transform.localScale = Vector3.one;
                        GameObject gobject = Instantiate(goImage);
                        gobject.transform.SetParent(GoldNormal, false);
                        gobject.transform.localScale = new Vector3(1f, 1f, 1f);
                        gobject.SetActive(true);
                        gobject.transform.localPosition = pos+new Vector3(UnityEngine.Random.Range(-100f, 10f), UnityEngine.Random.Range(-100f, 100f),0f);
                        gobject.transform.DOLocalJump(gobject.transform.localPosition, 40f, 2, 1f).SetEase(Ease.Linear).OnComplete(() => {
                            gobject.transform.DOMove(endPos, 1.5f).OnComplete(() => {
                                Destroy(gobject);
                            });
                        });
                    }
                });
            }
            DOVirtual.DelayedCall(3f, ()=> {
                common.listPlayer[playerID].ChangeJiangQuan(originalNumber + itemCount);
            });
        }
        bool IsBoDieSound=false;
        /// <summary>
        /// 四秒间隔播一次鱼死亡的音效
        /// </summary>
        /// <param name="fishname"></param>
        public void FourTimePlaySound(string fishname)
        {
            if (IsBoDieSound == false)
            {
                IsBoDieSound = true;
                if (common4.GetFishCanShu(fishname).isSmall|| common4.GetFishCanShu(fishname).isBig)
                {
                    SoundLoadPlay.PlaySound(UnityEngine.Random.Range(1,18).ToString());
                }
                else if (common4.GetFishCanShu(fishname).isGold)
                {
                    SoundLoadPlay.PlaySound(UnityEngine.Random.Range(110, 119).ToString());
                }
                DOVirtual.DelayedCall(4f, () => {
                    IsBoDieSound = false;
                });
            }
          //四秒钟只播一个
        }
        IEnumerator HuiSHou(GameObject dieEffect, string str) {
            yield return new WaitForSeconds(1f);
            commonLoad.ReciveOneDic(str, dieEffect);
        }

        public void OnlyMoneyFly(Vector3 varfishpos, long fishfwqId, long playerId, Vector3 endtargetpos, long gold, int nameSate) {
            if (common2.BulletPos == null)
            {
                return;
            }
            if (common2.BulletPos == null)
            { 
                //父物体不存在 因为可能回包时 场景已经没了
                return;
            }
            int nNum = UnityEngine.Random.Range(3, 8);
            for (int i = 0; i < nNum; i++)
            {
                int n = i;
                DOVirtual.DelayedCall(0.1f * i, () => {
                    GameObject tmpgo;
                    tmpgo = commonLoad.GetOneDieMoney(common2.BulletPos);
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(-80f, 80f);
                    float fList = UnityEngine.Random.Range(-20f, 80f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, fList, 0f);
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 60f, 2, 1f).SetEase(Ease.Linear).OnComplete(() =>
                    {
                        tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() => {
                            if (n == nNum - 1)
                            {
                                PlayerPosPiaofen(gold, nameSate);
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                                if (playerId == PlayerData.PlayerId)
                                {
                                    SoundLoadPlay.PlaySound("shop_coin");
                                }
                                try
                                {
                                    common.listPlayer[playerId].ReMoveWaitDragonScale(fishfwqId);
                                }
                                catch { }
                            }
                            //回收
                            commonLoad.ReciveDieMoney(0, tmpgo);
                        });
                    });
                });
            }
        }
        // 死亡掉落金币动画
        public void AnimalMoneyDrop(Vector3 fishpos,long fishfwqId, long Playerid, Vector3 endPos, long gold, int nameSate, string fishname)
        {
            if (gold > 0) 
            {
                //显示金币文本
                var dieEffect = commonLoad.GetOneDicPool("BuyuPrefabs/TxtPaoFen", GoldNormal);
                dieEffect.GetComponent<Text>().text = gold.ToString("N0");
                dieEffect.transform.localPosition = fishpos;
                dieEffect.transform.localScale = Vector3.zero;
                dieEffect.transform.DOLocalJump(fishpos, 5, 1, 0.3f);
                dieEffect.transform.DOScale(0.8f, 0.3f);
                StartCoroutine(HuiSHou(dieEffect, "BuyuPrefabs/TxtPaoFen"));
            }
            if (common4.dicMoldConfig.ContainsKey(fishname))
            {
                //根据鱼倍数显示不同的金币数  飞向玩家
                if (common4.dicMoldConfig[fishname].isGold)
                {
                    //黄金鱼                    
                    GoldFishDieEffect(fishfwqId, fishname, fishpos, Playerid, endPos, gold, nameSate);
                }
                else
                {
                    if (common4.dicMoldConfig[fishname].isSmall)
                    {
                        //小鱼 
                        //SoundLoadPlay.PlaySound("sd_t3_jisha_xiaoyu");
                        SmallFishDieEffect(fishfwqId, fishpos, Playerid, endPos, gold, nameSate);
                        return;
                    }
                    else if (common4.dicMoldConfig[fishname].isBig)
                    {
                        //大鱼       
                        BigFishDieEffect(fishfwqId, fishpos, Playerid, endPos, gold, nameSate);
                        return;
                    }
                    else
                    {
                        BigFishDieEffect(fishfwqId, fishpos, Playerid, endPos, gold, nameSate); 
                        return;
                    }
                }
            }
        }
        void SmallFishDieEffect(long fishid, Vector3 varfishpos, long playerId, Vector3 endtargetpos, long gold, int playerPos)
        {
            if (common2.BulletPos==null)
            {
                return;
            }
            int nNum = UnityEngine.Random.Range(1, 3);
  
            for (int i = 0; i < nNum; i++)
            {
                int n = i;
                DOVirtual.DelayedCall(0.1f*i,()=>{
                    GameObject tmpgo;
                    tmpgo = commonLoad.GetOneDieMoney(common2.BulletPos);
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(35f, 50f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw * i, 0f, 0f);
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 40f, 2, 1f).SetEase(Ease.Linear).OnComplete(() =>
                    {
                        if (n == nNum - 1)
                        {

                            tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() => {
                                if (playerId == PlayerData.PlayerId)
                                {
                                    SoundLoadPlay.PlaySound("shop_coin");
                                }
                                PlayerPosPiaofen(gold, playerPos);
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                                try
                                {
                                    common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
                                }
                                catch
                                {
                                }
                            });
                        }
                        else
                        {
                            tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() => {
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            });
                        }
                    });

                });
            }
        }
        /// <summary>
        /// target为炮台的UI世界坐标  varfishpos已经是UI坐标了
        /// </summary>
        /// <param name="endtargetpos"></param>
        void BigFishDieEffect(long fishid, Vector3 varfishpos, long playerId, Vector3 endtargetpos, long gold, int playerPos)
        {
            if (common2.BulletPos == null)
            {
                return;
            }
            if (common2.BulletPos == null)
            {
                //父物体不存在 因为可能回包时 场景已经没了
                return;
            }
            int nNum = UnityEngine.Random.Range(3, 8);
            for (int i = 0; i < nNum; i++)
            {
                int n = i;
                DOVirtual.DelayedCall(0.1f * i, () => {
                    GameObject tmpgo;
                    tmpgo = commonLoad.GetOneDieMoney(common2.BulletPos);
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(-80f, 80f);
                    float fList = UnityEngine.Random.Range(-20f, 80f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, fList, 0f);
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 60f, 2, 1f).SetEase(Ease.Linear).OnComplete(() =>
                    {
                        tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() => {
                            if (n == nNum - 1)
                            {
                                PlayerPosPiaofen(gold, playerPos);
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                                if (playerId == PlayerData.PlayerId)
                                {
                                    SoundLoadPlay.PlaySound("shop_coin");
                                }
                                try
                                {
                                    common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
                                }
                                catch { }
                            }
                            //回收
                            commonLoad.ReciveDieMoney(0, tmpgo);
                        });
                    });
                });
            }
            var dieEffect = commonLoad.GetOneDicPool("Effect/dieBoom/die1", common2.BulletPos);
            dieEffect.transform.SetParent(common2.BulletPos, false);
            dieEffect.SetActive(true);
            dieEffect.transform.localPosition = varfishpos;
            dieEffect.transform.localScale = Vector3.one;
            DOVirtual.DelayedCall(3, () =>
            {
                commonLoad.ReciveOneDic("Effect/dieBoom/die1", dieEffect);
            });
        }
        /// <summary>
        /// target为炮台的UI世界坐标  varfishpos已经是UI坐标了
        /// </summary>
        /// <param name="endtargetpos"></param>
        void BossFishDieEffect(long fishid, string fishname, Vector3 varfishpos, long playerId, Vector3 endtargetpos, long gold, int playerPos)
        {
            if (common2.BulletPos == null)
            {
                return;
            }
            int nNum = UnityEngine.Random.Range(12, 20);

            for (int i = 0; i < nNum; i++)
            {
                int n = i;
                DOVirtual.DelayedCall(0.1f * i, () => {
                    GameObject tmpgo;
                    tmpgo = commonLoad.GetOneDieMoney(common2.BulletPos);
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(-80f, 80f);
                    float fList = UnityEngine.Random.Range(-20f, 80f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, fList, 0f);
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 80f, 6, 2f).SetEase(Ease.Flash).OnComplete(() =>
                    { 
                        if (n == nNum - 1)
                        {
                            PlayerPosPiaofen(gold, playerPos);
                            //回收
                            commonLoad.ReciveDieMoney(0, tmpgo);
                            try
                            {
                                GameObject m = common4.LoadPrefab("GoldTurntable/fish_xbcaipan");
                                var go = Instantiate(m, GoldNormal);
                                go.transform.position = GetOnePlayer(playerId).pos_caijin.transform.position;
                                FishCaiPan fp = go.GetComponent<FishCaiPan>();
                                fp.fishImg.sprite = common4.LoadSprite("FishIcon/" + common4.GetFishModleID(fishname));
                                fp.fishImg.SetNativeSize();
                                fp.nameTxt.text = fishname;
                                //三次累加逻辑
                                long tmp1 = gold / 4L;
                                fp.beiTxt.text = tmp1.ToString();
                                fp.beiTxt.transform.DOScale(0.015f, 0.25f).OnComplete(() => { fp.beiTxt.transform.DOScale(0.01f, 0.25f); });
                                DOVirtual.Float(tmp1, tmp1 * 2, 0.5f, (tmpFloat) =>
                                {
                                    fp.beiTxt.text = ((int)tmpFloat).ToString();
                                }).SetDelay(1.2f).
                                OnComplete(() =>
                                {
                                    fp.beiTxt.transform.DOScale(0.015f, 0.25f).OnComplete(() => { fp.beiTxt.transform.DOScale(0.01f, 0.25f); });
                                    DOVirtual.Float(tmp1 * 2, tmp1 * 3, 0.5f, (tmpFloat2) =>
                                    {
                                        fp.beiTxt.text = ((int)tmpFloat2).ToString();
                                    }).SetDelay(1.2f).
                                    OnComplete(() =>
                                    {
                                        fp.beiTxt.transform.DOScale(0.015f, 0.25f).OnComplete(() => { fp.beiTxt.transform.DOScale(0.01f, 0.25f); });
                                        DOVirtual.Float(tmp1 * 3, gold, 0.5f, (tmpFloat3) =>
                                        {
                                            fp.beiTxt.text = ((int)tmpFloat3).ToString();
                                        }).SetDelay(1.2f).
                                         OnComplete(() =>
                                         {
                                             fp.beiTxt.text = gold.ToString();
                                             try
                                             {
                                                 common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
                                             }
                                             catch { }
                                         });
                                    });
                                });

                            }
                            catch
                            {
                                //tmpgo.gameObject.SetActive(false);
                                //Destroy(tmpgo);
                            }
                            if (playerId == PlayerData.PlayerId)
                            {
                                SoundLoadPlay.PlaySound("shop_coin");
                            }

                            tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() =>
                            {
                                if (playerId == PlayerData.PlayerId)
                                {
                                    SoundLoadPlay.PlaySound("shop_coin");
                                }
                                PlayerPosPiaofen(gold, playerPos);
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                                try
                                {
                                    common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
                                }
                                catch
                                {
                                }
                            });
                        }
                        else
                        {
                            tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().SetEase(Ease.Flash).OnComplete(() => {
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            });
                        }
                    });

                });
            }
        }
        void PlayerPosPiaofen(long gold, int ngdseat)
        {
            try
            {
                UIShowGold popMsg = UIMgr.ShowUISynchronize(UIPath.UIShowGold).GetComponent<UIShowGold>();
                popMsg.ShowMessage("+" + gold.ToString(), ngdseat);
            }
            catch
            {

            }

        }
        /// <summary>
        /// 黄金鱼死亡 target为炮台的UI世界坐标  varfishpos已经是UI坐标了
        /// </summary>
        /// <param name="endtargetpos"></param>
        void GoldFishDieEffect(long fishid, string fishname, Vector3 varfishpos, long playerId, Vector3 endtargetpos, long gold, int playerPos)
        {
            if (common2.BulletPos == null)
            {
                //父物体不存在 因为可能回包时 场景已经没了
                return;
            }
            int nNum = UnityEngine.Random.Range(3, 8);

            for (int i = 0; i < nNum; i++)
            {
                int n = i;
                DOVirtual.DelayedCall(0.1f * i, () => {
                    GameObject tmpgo;
                    tmpgo = commonLoad.GetOneDieMoney(common2.BulletPos);
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(-80f, 80f);
                    float fList = UnityEngine.Random.Range(-80f, 80f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, fList, 0f);
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 60f, 2, 1f).SetEase(Ease.Linear).OnComplete(() =>
                    {
                        if (n == nNum - 1)
                        {
                            PlayerPosPiaofen(gold, playerPos);
                            //回收
                            //commonLoad.ReciveDieMoney(0, tmpgo);
                            if (playerId == PlayerData.PlayerId)
                            {
                                SoundLoadPlay.PlaySound("shop_coin");
                            }
                            PlayerPosPiaofen(gold, playerPos);
                            try
                            {
                                common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
                                GameObject m = common4.LoadPrefab("GoldTurntable/fish_caipan");
                                var go = Instantiate(m, GoldNormal);
                                go.transform.position = GetOnePlayer(playerId).pos_caijin.transform.position;
                                FishCaiPan fp = go.GetComponent<FishCaiPan>();
                                fp.fishImg.sprite = common4.LoadSprite("FishIcon/" + common4.GetFishModleID(fishname));
                                fp.fishImg.SetNativeSize();
                                fp.nameTxt.text = fishname;
                                fp.beiTxt.text = gold.ToString();
                            }
                            catch
                            {
                                tmpgo.gameObject.SetActive(false);
                                Destroy(tmpgo);
                            }
                            //回收
                            commonLoad.ReciveDieMoney(0, tmpgo);
                        }
                        else
                        {
                            tmpgo.transform.DOMove(endtargetpos, 200f).SetSpeedBased().OnComplete(() => {
                                //回收
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            });
                        }
                    });
                });
            }          
        }
        public void Req_XmlEndRequest(long userId, long reword, int type)
        {
        }
        public void Req_UseEleRequest(long fishId)
        {
            NetMessage.Chanllenge.Req_ChallengeUseEleRequest(fishId);
        }

        public void ChangeS_C_DoFire(FishingFireResponse vartmp, long fireId, long fishId, long fishId1, long fishId2, float angle)
        {
            MyPlayer.Re_DoFire(vartmp); 
            NetMessage.Chanllenge.Req_FishingChallengeFireRequest(fireId, fishId, angle); 
            //同步三管炮
            NetMessage.Chanllenge.Req_FishingChallengeSyncLockRequest(vartmp.playerId, fishId, fishId1, fishId2);
        }

        public void ShanDianPao_DoFire(FishingFireResponse vartmp, long fireId, long fishId, float angle)
        {
        }

        public void ShanDianPaoSyncLockThree_DoFire(long playerId, long fishId, long fishId1, long fishId2)
        {
        }
        public void SpecialFishRequest(List<long> fishIds, long specialFishId, long playerId)
        {
            NetMessage.Chanllenge.Req_FishingChallengeCatchSpecialFishRequest(specialFishId, fishIds,playerId);
        }

        public void Req_FishingFightFishRequest(long fireId, long fishId)
        {
            NetMessage.Chanllenge.Req_FishingChallengeFightFishRequest(fireId, fishId);
        }

        public void Req_ChangePaoRequest(int paoIndex)
        {
            NetMessage.Chanllenge.Req_FishingChallengeChangeBatteryViewRequest(paoIndex);
        }

        public void Req_FishingRobotFightFishRequest(long fireId, long fishId, long robotId)
        {
            NetMessage.Chanllenge.Req_FishingChallengeRobotFightFishRequest(fireId, fishId, robotId);
        }
        public void tmpUIByBOOSAnimation(long fishId)
        {
            isPlayBossCommingSound(true);//播放音效
        }

        public bool Getb_SkillIce()
        {
            return b_SkillIce;
        }
        public void Req_DoubleKillRequest(long PlayerID, List<long> varListFish)
        {
            NetMessage.Chanllenge.Req_FishingChallengeDoubleKillFishRequest(varListFish, PlayerID); 
        }
        public void Req_DoubleKillEndRequest(long AllGold, long PlayerID, long mult, string fishName)
        {
            NetMessage.Chanllenge.Req_FishingChallengeDoubleKillEndRequest(AllGold, PlayerID, mult, fishName);
        }

        public void ShowFishIconMoney(long playerId, string name, long fishid, long varGold)
        {
            PlayerData._TwoAttackNum--;
            GameObject m = common4.LoadPrefab("GoldTurntable/fish_caipan");
            var go = Instantiate(m, GoldNormal);
            go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
            FishCaiPan fp = go.GetComponent<FishCaiPan>();
            fp.fishImg.sprite = common4.LoadSprite("FishIcon/" + common4.GetFishModleID(name));
            fp.fishImg.SetNativeSize();
            fp.nameTxt.GetComponent<Text>().text = name;
            fp.beiTxt.text = varGold.ToString();

            PlayerPosPiaofen(varGold, common.listPlayer[playerId].n_nameSeat);
            common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
        }
        public void ShowFishIconMoneyCqzs(long playerId, string name, long fishid, long varGold) 
        {
            PlayerData._TwoAttackNum--;
            GameObject m = common4.LoadPrefab("GoldTurntable/fish_caipanCqzs");
            var go = Instantiate(m, GoldNormal);
            go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
            FishCaiPan fp = go.GetComponent<FishCaiPan>();
            fp.fishImg.sprite = common4.LoadSprite("FishIcon/" + common4.GetFishModleID(name));
            fp.fishImg.SetNativeSize();
            fp.nameTxt.GetComponent<Text>().text = "出奇制胜";
            fp.beiTxt.text = varGold.ToString();

            PlayerPosPiaofen(varGold, common.listPlayer[playerId].n_nameSeat);
            common.listPlayer[playerId].ReMoveWaitDragonScale(fishid);
        }
        public void SetMyItem()
        {
        }
        public void Req_FishingReactiveRequest()
        {
            NetMessage.Chanllenge.Req_FishingChallengeReactiveRequest();
            NetMessage.Chanllenge.Req_FishingChallengeSynchroniseRequest();
        }

        public GameObjectPool GetDropIconFish()
        {
            return Object_DropIconFishPool;
        }
        public void isPlayBossCommingSound(bool varPlay)
        {
            if (varPlay)
            {
                SoundLoadPlay.ChangeBgMusic("boosbg");
            }
            else
            {
                SoundLoadPlay.ChangeBgMusic("sd_t5_game_background_music_normal");
            }
        }
        public void ZhuanTouBoom(long playerID, Vector2 input)
        {
            if (playerID == PlayerData.PlayerId)
            {
                List<long> varListFish1 = new List<long>();
                foreach (var item in common.listFish)
                {
                    if (CheckBooArea(input, item.Key))
                    {
                        if (item.Value.IsCanHit == true)
                        {
                            if (common4.dicFishConfig[item.Value.fishState.fishId].money < 100)
                            {
                                varListFish1.Add(item.Key);
                            }
                        }
                    }
                }
                if (playerID == PlayerData.PlayerId)
                {
                    
                    NetMessage.Chanllenge.Req_FishingChallengeCatchSpecialFishRequest(-1 * BY_SKILL.ZHUANTOU, varListFish1, playerID);
                }
            }
        }
        public void SyncLockRequest(long fishId)
        {
            
            NetMessage.Chanllenge.Req_FishingChallengeSyncLockRequest(PlayerData.PlayerId,fishId, 0, 0);
        }
 
        public void AnimalNet(int localPaoPrefab, int nPaoView, Vector3 pos, GameObject tmpBullet)
        {
            int mm = nPaoView; 
            commonLoad.ReciveOneBullet(localPaoPrefab, tmpBullet.gameObject);

            GameObject go = commonLoad.GetOneWang(localPaoPrefab, common2.BulletPos);
            //go.GetComponent<DragonBones.UnityArmatureComponent>().animation.Play("wang", 1);
            go.transform.position = pos;
            DOVirtual.DelayedCall(1f, () =>
            {
                commonLoad.ReciveOneWang(localPaoPrefab, go);
            });
        } 
        public  Dictionary<int, AudioClip>  Getm_listLitFishDie()
        {
            return AllFishDie;
        }
        public void TwoAttackNumFish(int Type, long playerID, int Num)
        {
            List<long> varListFish = new List<long>();
            foreach (var item in common.listFish)
            {
                if (common4.dicFishConfig[item.Value.fishState.fishId].money < 100)
                {
                    if (common.listFish[item.Key].IsCanHit == true)//能被攻击
                    {
                        if (CheckFlashFishInScreen(item.Key))//是否为在屏幕外面
                        {
                            if (varListFish.Count >= Num)
                            {
                                break;
                            }
                            else
                            {
                                varListFish.Add(item.Key);
                            }
                        }
                    }
                }
            }
            if (varListFish.Count > 0)
            {
                if (playerID == PlayerData.PlayerId)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeDoubleKillFishRequest(varListFish, playerID);
                }
            }
        }
        public player GetOnePlayer(long playerID)
        {
            if (common.listPlayer.ContainsKey(playerID))
            {
                return common.listPlayer[playerID];
            }
            Debug.LogWarning("错误" + playerID);
            return null;
        }
        public void TwoAttackFish(int Type, long playerID)
        {
            //是自己才请求
            if (playerID == PlayerData.PlayerId)
            {
                List<long> varListFish = new List<long>();
                foreach (var item in common.listFish)
                {
                    // if (common4.dicFishConfig[item.Value.fishState.fishId].money < 100)
                    if (common4.dicFishConfig[item.Value.fishState.fishId].fishType < 3)
                    {
                        if (common.listFish[item.Key].IsCanHit == true)//能被攻击
                        {
                            if (CheckFlashFishInScreen(item.Key))//是否为在屏幕外面
                            {
                                varListFish.Add(item.Key);
                            }
                        }
                    }
                }
                if (varListFish.Count > 0)
                {

                    NetMessage.Chanllenge.Req_FishingChallengeDoubleKillFishRequest(varListFish, playerID);
                }
            }
        }
        public void JinZhu(bool state)
        {
        }
   
    }
}