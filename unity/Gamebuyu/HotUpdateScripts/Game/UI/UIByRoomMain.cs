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
    public class UIByRoomMain : MonoBehaviour, UIFishingInterface
    {
        private static bool IsFirst = true;

        public List<player> objPlayer = new List<player>();
        public player MyPlayer; 

        private Button[] btn_ChanegSet = new Button[4];
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
        //进场提示
        //public Transform fishTips;
        //public Transform fishTipsSpecial;
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

        //private Toggle toggle_jeisuo;
        //private Text txt_jeiSuoBei;   
        public int next_bei;//下一个倍数
        public long next_cost;//下一个所需花费
        private long CanChangeYugu = 0;

        //public Text txt_bei;
        //public Text txt_beiUp;//Up为升级页面参数
         
        //public Text txt_neednum;  
        //public Text txt_neednumUp;

        //public Transform tra_jiesuo;
        //public Transform tra_jiesuoUp;
        //public Button btn_view_jiesuo;
        //public Button btn_view_jiesuoUp;
        private int MaxPaoFw = 5;
        //public Button btn_UpThound;
        //public Button btn_getmoney;
        //技能
        #region 
        public Button btn_suoding;
        public Button btn_bingdong;
        public Button btn_baoji;
        public Button btn_shengdeng;//神灯
        public Button btn_autofire;
 
        [HideInInspector]
        public bool b_SkillIce = false;//是否处于冰冻技能

        #endregion
        public Animator pig_title;
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
        //public Transform JobMuiltMoney;
        public Image prefabBoom;//核弹爆炸
        //刷新鱼处理 服务器时间
        private List<FishingFishInfoProto> list_fishInit = new List<FishingFishInfoProto>();
        //public GameObject goldNum;//金币数字
        //public Transform boosTips;
        //public Transform BOOSTipsAnimation;
        //public AudioSource BOOSTipsAudio;
        public Toggle testToggle;
        public Transform testToggleTmp;  
        Dictionary<int, AudioClip> AllFishDie = new Dictionary<int, AudioClip>();
        void FindCompent() {

            objPlayer.Clear();
            objPlayer.Add(transform.Find("rootPlayer/0").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/1").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/2").GetComponent<player>());
            objPlayer.Add(transform.Find("rootPlayer/3").GetComponent<player>());

            //fishTipsSpecial = transform.Find("fishTipsSpecial");

            //寻找加入或换座
            #region
            trans_waitjion[0] = transform.Find("rootPlayer/img0");
            trans_waitjion[1] = transform.Find("rootPlayer/img1");
            trans_waitjion[2] = transform.Find("rootPlayer/img2");
            trans_waitjion[3] = transform.Find("rootPlayer/img3");

            btn_ChanegSet[0] = transform.Find("vipChangeSet/0").GetComponent<Button>();
            btn_ChanegSet[1] = transform.Find("vipChangeSet/1").GetComponent<Button>();
            btn_ChanegSet[2] = transform.Find("vipChangeSet/2").GetComponent<Button>();
            btn_ChanegSet[3] = transform.Find("vipChangeSet/3").GetComponent<Button>();
            #endregion

            num_combo = transform.Find("obj_combo/ArtNumQuote").GetComponent<ArtNumQuote>();
       

            testToggle = this.transform.Find("TestToggle").GetComponent<Toggle>(); 
            testToggleTmp = common2.NormalUICanvas.Find("TestFishTwoAttack/tmp");
            pig_title = transform.Find("pig_title").GetComponent<Animator>();
            btn_suoding = transform.Find("game_ui_skill/lock/Button").GetComponent<Button>();
            btn_bingdong = transform.Find("game_ui_skill/freeze/Button").GetComponent<Button>();
            btn_baoji = transform.Find("game_ui_skill/fury/Button").GetComponent<Button>();
            btn_shengdeng = transform.Find("game_ui_skill/summon/Button").GetComponent<Button>();

            btn_autofire = transform.Find("game_ui_skill/auto/Button").GetComponent<Button>();
    
            CatchTips = transform.Find("CatchTips");
            TipsPos = transform.Find("rootPlayer/posTips");

            //boosTips = transform.Find("boosTips");
            //BOOSTipsAnimation = transform.Find("BOOSTipsAnimation");
            //BOOSTipsAudio = this.transform.Find("BOOSTipsAnimation/Audio Source").GetComponent<AudioSource>();

            img_imgtarget = this.transform.Find("topQuest/ImgTarget").GetComponent<Image>();
            img_imgAward = this.transform.Find("topQuest/bg_award/Image").GetComponent<Image>();
            txt_questProcess = this.transform.Find("topQuest/txt_process").GetComponent<Text>();
            txt_questTarget = this.transform.Find("topQuest/888").GetComponent<Text>();
            txt_jobname = this.transform.Find("topQuest/bg_target/txt_target").GetComponent<Text>();
            txt_jobaward = this.transform.Find("topQuest/bg_award/txt_award").GetComponent<Text>();
            btn_linqu = this.transform.Find("topQuest/guang/btn_linqu").GetComponent<Button>();
            //JobMuiltMoney = this.transform.Find("JobMoney");
      
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
            FindCompent();
            testToggle.onValueChanged.AddListener((arg) => {
                testToggleTmp.gameObject.SetActive(arg);
            });
            btn_autofire.onClick.AddListener(() => {
                if (PlayerData._bZiDong == false)
                {
                    PlayerData._bZiDong = true;
                    PlayerData.SetRootbZiDong(true);
                    btn_autofire.transform.parent.Find("effect").gameObject.SetActive(true);
                }
                else
                {
                    PlayerData._bZiDong = false;
                    PlayerData.SetRootbZiDong(false);
                    btn_autofire.transform.parent.Find("effect").gameObject.SetActive(false);
                }
            });
            //技能点击事件
            #region
            btn_linqu.onClick.AddListener(() =>
            {
                NetMessage.OseeFishing.Req_FishingGetRoomTaskRewardRequest(int.Parse(btn_linqu.name));
            });
            btn_suoding.onClick.AddListener(() =>
            {
                if (IsCanUseAnIsEnoughGoldSkill(btn_suoding.transform))
                {
                    NetMessage.OseeFishing.Req_FishingUseSkillRequest((int)8);
                }
            });
            btn_bingdong.onClick.AddListener(() =>
            {
                if (IsCanUseSkill(btn_bingdong.transform))
                {
                    Debug.Log("btn_bingdong");
                    NetMessage.OseeFishing.Req_FishingUseSkillRequest((int)9);
                }
            });
            btn_baoji.onClick.AddListener(() =>
            {
                if (IsCanUseAnIsEnoughGoldSkill(btn_baoji.transform))
                {
                    NetMessage.OseeFishing.Req_FishingUseSkillRequest((int)BY_SKILL.FURY);
                }
            });
            btn_shengdeng.onClick.AddListener(() =>
            {
                if (IsCanUseSkill(btn_shengdeng.transform))
                {
                    //10453-10467
                    for (int i = 10453; i <= 10467; i++)
                    {
                        if (GetPathISfish(i) == false)//代表此路径没有鱼
                        {
                            Debug.Log(i);
                            NetMessage.OseeFishing.Req_FishingUseSkillRequest((int)38, i);
                            break;
                        }
                    }
                }
            });
            #endregion
        }
        //获取路径鱼 是否有鱼
        bool GetPathISfish(long fwqPathID) {
            foreach (var item in common.listFish)
            {
                if (item.Value==null)
                {
                    continue;
                }
                if (item.Value.fishState==null)
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
        }
        void OnEnable() {
            RestAllSkill();
            JinZhu(false);//关闭金猪
            common3._UIFishingInterface = this;
            UIMgr.CloseAllwithOut(UIPath.UIByRoomMain);
            NetMessage.OseeFishing.Req_UnlockBatteryLevelHintRequest();
            NetMessage.OseeFishing.Req_PlayerPropRequest();
            NetMessage.OseeFishing.Req_FishingRoomTaskListRequest();
            SetMyItem();

            EventManager.PropUpdate += SetMyItem;
            EventManager.ChangeAutoTarget += ChangeAutoTarget;
            EventManager.DoFire += On_DoFire;
            EventManager.ClickDoFire += On_Req_DoFire;
            SoundLoadPlay.ChangeBgMusic("sd_t5_game_background_music_normal");

            UEventDispatcher.Instance.AddEventListener(UEventName.FishingPlayerInfoResponse, On_FishingPlayerInfoResponse);//捕鱼玩家信息返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingRoomTaskListResponse, On_FishingRoomTaskListResponse);//捕鱼获取房间任务列表返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingGetRoomTaskRewardResponse, On_FishingGetRoomTaskRewardResponse);//捕鱼获取房间任务奖励返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingUseSkillResponse, On_FishingUseSkillResponse);//捕鱼使用技能返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingPlayersInfoResponse, On_FishingPlayersInfoResponse);//捕鱼玩家列表信息返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingExitRoomResponse, On_FishingExitRoomResponse);//捕鱼退出房间返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingRefreshFishesResponse, On_FishingRefreshFishesResponse);//捕鱼刷新房间鱼类返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingSynchroniseResponse, On_FishingSynchroniseResponse);//同步鱼返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingFireResponse, On_FishingFireResponse);//捕鱼发射子弹返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingFightFishResponse, On_FishingFightFishResponse);//捕鱼击中鱼类返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingRobotFireResponse, On_FishingRobotFireResponse);//捕鱼机器人发射子弹返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChangeBatteryLevelResponse, On_FishingChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingChangeCritMultResponse, On_FishingChangeCritMultResponse);//改变狂暴倍数响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingDoubleKillResponse, On_FishingDoubleKillResponse);//二次伤害鱼返回
            UEventDispatcher.Instance.AddEventListener(UEventName.UnlockBatteryLevelHintResponse, On_UnlockBatteryLevelHintResponse);//解锁炮台等级提示返回
            UEventDispatcher.Instance.AddEventListener(UEventName.UnlockBatteryLevelResponse, On_UnlockBatteryLevelResponse);//解锁炮台等级响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingFinishRoomGoalResponse, On_FishingFinishRoomGoalResponse);//捕鱼完成房间目标返回
            UEventDispatcher.Instance.AddEventListener(UEventName.UseEleResponse, On_UseEleResponse);//同步电磁炮响应
            UEventDispatcher.Instance.AddEventListener(UEventName.UseBlackResponse, On_UseBlackResponse);//同步黑洞炮响应
            UEventDispatcher.Instance.AddEventListener(UEventName.UseTroResponse, On_UseTroResponse);//同步鱼雷炮响应
            UEventDispatcher.Instance.AddEventListener(UEventName.BitFightFishResponse, On_BitFightFishResponse);//钻头击中鱼响应
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingDoubleKillFishResponse, On_FishingDoubleKillFishResponse);//钻头击中鱼响应


        }

        void OnDisable() {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingPlayerInfoResponse, On_FishingPlayerInfoResponse);//捕鱼玩家信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingRoomTaskListResponse, On_FishingRoomTaskListResponse);//捕鱼获取房间任务列表返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingGetRoomTaskRewardResponse, On_FishingGetRoomTaskRewardResponse);//捕鱼获取房间任务奖励返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingUseSkillResponse, On_FishingUseSkillResponse);//捕鱼使用技能返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingPlayersInfoResponse, On_FishingPlayersInfoResponse);//捕鱼玩家列表信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingExitRoomResponse, On_FishingExitRoomResponse);//捕鱼退出房间返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingRefreshFishesResponse, On_FishingRefreshFishesResponse);//捕鱼刷新房间鱼类返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingSynchroniseResponse, On_FishingSynchroniseResponse);//捕鱼同步返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingFireResponse, On_FishingFireResponse);//捕鱼发射子弹返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingFightFishResponse, On_FishingFightFishResponse);//捕鱼击中鱼类返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingRobotFireResponse, On_FishingRobotFireResponse);//捕鱼机器人发射子弹返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChangeBatteryLevelResponse, On_FishingChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingChangeCritMultResponse, On_FishingChangeCritMultResponse);//改变狂暴倍数响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingDoubleKillResponse, On_FishingDoubleKillResponse);//二次伤害鱼返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UnlockBatteryLevelHintResponse, On_UnlockBatteryLevelHintResponse);//解锁炮台等级提示返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UnlockBatteryLevelResponse, On_UnlockBatteryLevelResponse);//解锁炮台等级响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingFinishRoomGoalResponse, On_FishingFinishRoomGoalResponse);//捕鱼完成房间目标返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UseEleResponse, On_UseEleResponse);//同步电磁炮响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UseBlackResponse, On_UseBlackResponse);//同步黑洞炮响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UseTroResponse, On_UseTroResponse);//同步鱼雷炮响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BitFightFishResponse, On_BitFightFishResponse);//钻头击中鱼响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingDoubleKillFishResponse, On_FishingDoubleKillFishResponse);//钻头击中鱼响应

            common3._UIFishingInterface = null;
            EventManager.PropUpdate -= SetMyItem;
            EventManager.ChangeAutoTarget -= ChangeAutoTarget;
            EventManager.DoFire -= On_DoFire;
            EventManager.ClickDoFire -= On_Req_DoFire;
            
            UIMgr.DestroyCreateUI();
        }
        void ChangeAutoTarget(Vector2 v2) {
            
            if (MyPlayer!=null)
            {
                MyPlayer.ChangeAutoTarget(v2);
            }
            else
            {
                Debug.Log("MyPlayer==null");
            }
        }
        void OnDestory() {
        }
        bool IsCanUseAnIsEnoughGoldSkill(Transform Go)
        {  
            if (Go.transform.parent.Find("txtCount").GetComponent<Text>().text == "0")
            {
                UIShop tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                tmp.Setpanel(JieMain.道具商城);
                return false;
            }
            try
            {
                if (MyPlayer.IsEnoughGold(PlayerData.Gold) == false)
                {
                    //MessageBox.Show("你已破产,是否前往商城购买金币？", "", () =>
                    //{                    
                    //    var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                    //    tmp.Setpanel(JieMain.金币商城);
                    //}, () => { });
                    var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                    tmp.Setpanel(JieMain.钻石商城);
                    return false;
                }
                else
                {
                    return true;
                }
            }
            catch
            {
                return true;
            }
        }
        bool IsCanUseSkill(Transform Go)
        {
            if (Go.transform.parent.Find("txtCount").GetComponent<Text>().text == "0")
            {
                UIShop tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                tmp.Setpanel(JieMain.道具商城);
                return false;
            }
            return true;
        }
        void On_DoFire(Vector2 v2)
        {
            if (MyPlayer != null)
            {
                MyPlayer.Req_DoFire(v2);
            }
            else
            {
                Debug.Log("MyPlayer= null"); 
            }
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
            var pack = obj.GetData<FishingPlayerInfoResponse>();
            //判定旋转
            if (pack.playerInfo.playerId == PlayerData.PlayerId)
            {
                if (pack.playerInfo.seat > 1)
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
        /// <summary>
        /// 捕鱼获取房间任务列表返回
        /// <summary>
        private void On_FishingRoomTaskListResponse(UEventContext obj)
        {
            //var pack = obj.GetData<FishingRoomTaskListResponse>();
            //for (int i = 0; i < pack.taskInfos.Count; i++)
            //{
            //    txt_jobname.text = pack.taskInfos[i].name.ToString();
            //    if (pack.taskInfos[i].goalType == 1)
            //    {

            //    }
            //    string str1 = pack.taskInfos[i].goalId.Replace("[", "");
            //    string str = str1.Replace("]", "");
            //    if (str == "0")
            //    {

            //    }
            //    else if (str == "81" || str == "82")
            //    {
            //        //其它图标
            //        img_imgtarget.sprite = common4.LoadSprite("tubaio/" + str);// Resources.Load<Sprite>("autoFire/" + str);
            //    }
            //    else if (str == "8" || str == "9" || str == "10" || str == "11")
            //    {
            //        //技能
            //        img_imgtarget.sprite = common4.LoadSprite("item/" + str);
            //    }
            //    else
            //    {
            //        //鱼图标
            //        img_imgtarget.sprite = common4.LoadSprite("FishIco/" + str);
            //    }
            //    img_imgtarget.SetNativeSize();
            //    //img_imgAward.sprite = common4.LoadSprite("item/" + pack.taskInfos[i].rewards[i].itemId);
            //    img_imgAward.sprite = common4.LoadSprite("item/" + pack.taskInfos[i].rewards[i].itemId);
            //    txt_jobaward.text = "x" + pack.taskInfos[i].rewards[i].itemNum;
            //    txt_questProcess.text = pack.taskInfos[i].progress.ToString();                
            //    txt_questTarget.text = "/" + pack.taskInfos[i].target;
            //    if (pack.taskInfos[i].progress >= pack.taskInfos[i].target)
            //    {
            //        btn_linqu.transform.parent.gameObject.SetActive(true);
            //        btn_linqu.name = pack.taskInfos[i].taskId.ToString();
            //        btn_linqu.gameObject.SetActive(true);
            //    }
            //    else
            //    {
            //        btn_linqu.transform.parent.gameObject.SetActive(false);
            //        btn_linqu.gameObject.SetActive(false);
            //    }
            //}
        }
        /// <summary>
        /// 捕鱼获取房间任务奖励返回
        /// <summary>
        private void On_FishingGetRoomTaskRewardResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingGetRoomTaskRewardResponse>();
            //JobMuiltMoney.gameObject.SetActive(true);

            //List<int> Floatpoint = new List<int>() { 18, 54, 90, 126, 162, 198, 234, 270, 306, 342 };
            //int tmpAngle = Floatpoint[pack.rewardMulti];

            //varZhuan = JobMuiltMoney.Find("zhuan");
            //varZhuan.rotation = new Quaternion(0f, 0f, 0f, 0f);

            //Dictmp.Clear();
            //foreach (var item in pack.rewards)
            //{
            //    Dictmp.Add(item.itemId, item.itemNum);
            //}
            //varZhuan.DORotate(new Vector3(0f, 0f, -3600f + tmpAngle), 3f, RotateMode.FastBeyond360).SetEase(Ease.Linear).OnComplete(() =>
            //{

            //    CancelInvoke("closeJobMuiltMoney");
            //    Invoke("closeJobMuiltMoney", 1f);
            //    CancelInvoke("ShowDictmp");
            //    Invoke("ShowDictmp", 2f);
            //});
        }
  
        void ShowDictmp()
        {
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            tmp.InitItem(Dictmp, 2, false);
        }
        //重置所有技能状态
        public void RestAllSkill()
        {
            //冰冻
            common2.transICE.gameObject.SetActive(false);
            //自动
            PlayerData._bZiDong = false;
            PlayerData.SetRootbZiDong(false);
            btn_autofire.transform.parent.Find("effect").gameObject.SetActive(false);
            //技能计数
            timbaoji = 0;
            timBingDong = 0;
            OthertimBingDong = 0;
            timSuoding = 0;
            b_shengdeng = false;
            timShengdeng = 0;

            if (btn_suoding != null)
            {
                CloseSkillCD(btn_suoding.transform);
            }
            if (btn_bingdong != null)
            {
                CloseSkillCD(btn_bingdong.transform);
            }
            if (btn_baoji != null)
            {
                CloseSkillCD(btn_baoji.transform);
            }
            if (btn_shengdeng != null)
            {
                CloseSkillCD(btn_shengdeng.transform);
            }
        }
        int timShengdeng = 0;
        bool b_shengdeng = false;
        IEnumerator IESkill_Shengdneg(float ftime, long varPlayerId)
        {
            if (varPlayerId == PlayerData.PlayerId)  //技能CD图片 别人使用时不显示CD
            {
                b_shengdeng = true;
                timShengdeng++;

                Image img = btn_shengdeng.transform.GetChild(1).GetComponent<Image>();

                img.enabled = true;
                img.fillAmount = 1;
                //等待使用系统时间 以防切出去
                float bingdong_connter = Time.realtimeSinceStartup;
                //等待时间 和CD图片
                while (Time.realtimeSinceStartup - bingdong_connter < ftime)
                {
                    img.fillAmount = 1 - ((Time.realtimeSinceStartup - bingdong_connter) / ftime);
                    yield return new WaitForEndOfFrame();
                }
                //计数防止重复时提前关闭
                timShengdeng--;
                if (timShengdeng <= 0)
                {
                    img.enabled = false;
                    b_shengdeng = false;
                }
            }
        }
        void CloseSkillCD(Transform vargo)
        {
            Image img = vargo.parent.Find("Mask").GetComponent<Image>();
            img.enabled = false;
            img.DOKill();
            img.fillAmount = 1;
        }
        /// <summary>
        /// 捕鱼使用技能返回
        /// <summary
        private void On_FishingUseSkillResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingUseSkillResponse>();
            SoundLoadPlay.PlaySound("clickskill");
            //GameObject go;//= Instantiate<GameObject>(Resources.Load<GameObject>("fish/skill/" + pack.skillId));
            switch (pack.skillId)
            {
                case 8://锁定            
                    GetOnePlayer(pack.playerId).Skill_Juemingzhuiji(pack.duration, pack.playerId);
                    StartCoroutine(IESkill_Suoding(pack.duration, pack.playerId));
                    break;
                case 9://冰冻    
                    StartCoroutine(IESkill_BingDong(pack.duration, pack.playerId));
                    break;              
                case 11://能量暴击
                    GetOnePlayer(pack.playerId).Skill_Baoji(pack.duration);
                    if (pack.playerId == PlayerData.PlayerId)
                    {
                        StartCoroutine(IESkill_baoji(pack.duration));
                        On_SetMultResponse(pack.playerId, 2);
                    }
                    break;
                case 38://神灯
                    iTweenPath ipath = common.GetRootPath().Find(common.dicPathConfig[pack.skillFishId].pathId.ToString()).GetComponent<iTweenPath>();
                    if (ipath == null)
                    {
                        Debug.LogError("轨迹ID不存在" + common.dicPathConfig[pack.skillFishId].pathId.ToString());
                    }
                    //fly过去
                    var flyEffect = common4.LoadPrefab("BuyuPrefabs/zhaohuan_fly");
                    var vargofly = Instantiate(flyEffect, common2.BulletPos);
                    var playerPos = GetOnePlayer(pack.playerId).objPaotai.transform.position;
                    vargofly.transform.position = playerPos;
                    vargofly.transform.DOLocalMove(common.WordToUI(ipath.nodes[0]), 0.3f);
                    break;
                case 64://钻头炮         
                    break;
                case 101://局部爆炸鱼    连环炸弹蟹
                    Debug.Log("此工程没有局部爆炸鱼 但服务器依旧回包了" + pack.skillFishId);
                    break;
                case 102://闪电鱼      
                    break;
                case 103://黑洞鱼         
                    break;
            }
        }
        private int timSuoding = 0;
        IEnumerator IESkill_Suoding(float ftime, long varPlayerId)
        {
            //技能CD图片 只显示自己的别人使用时不显示
            if (varPlayerId == PlayerData.PlayerId)
            {
                timSuoding++;

                Image img = btn_suoding.transform.parent.Find("Mask").GetComponent<Image>();
      
                img.enabled = true;
                img.fillAmount = 1;
                //等待使用系统时间 以防切出去停止
                float suoding_connter = Time.realtimeSinceStartup;
                //等待时间 和CD图片
                while (Time.realtimeSinceStartup - suoding_connter < ftime)
                {
                    img.fillAmount = 1 - ((Time.realtimeSinceStartup - suoding_connter) / ftime);
                    yield return new WaitForEndOfFrame();
                }
                //计数防止重复时提前关闭
                timSuoding--;
                if (timSuoding <= 0)
                {
                    //结束
                    img.enabled = false;
                }
            }
        }
        private int timBingDong = 0;
        private int OthertimBingDong = 0;
        IEnumerator IESkill_BingDong(float ftime, long varPlayerId)
        {
            if (varPlayerId == PlayerData.PlayerId)  //技能CD图片 别人使用时不显示CD
            {
                SoundLoadPlay.PlaySound("frezon");
                //冰冻背景
                //ChangeSFX.instnace.ICE.gameObject.SetActive(true);
                common2.transICE.gameObject.SetActive(true);
                //所有鱼冰冻     
                foreach (var it in common.listFish)
                {
                    //不冻boss
                    it.Value.Skill_Ice(true);
                    //if (it.Value.isBoss == false)
                    //{
                    //    //不冻boss
                    //    it.Value.Skill_Ice(true);
                    //}
                }
                b_SkillIce = true;
                timBingDong++;

                Image img = btn_bingdong.transform.parent.Find("Mask").GetComponent<Image>();

                img.enabled = true;
                img.fillAmount = 1;
                //等待使用系统时间 以防切出去
                float bingdong_connter = Time.realtimeSinceStartup;
                //等待时间 和CD图片
                while (Time.realtimeSinceStartup - bingdong_connter < ftime)
                {
                    //所有鱼冰冻     
                    foreach (var it in common.listFish)
                    {
                        it.Value.Skill_Ice(true);
                        //if (it.Value.isBoss == false)
                        //{
                        //    //不冻boss
                        //    it.Value.Skill_Ice(true);
                        //}
                    }
                    img.fillAmount = 1 - ((Time.realtimeSinceStartup - bingdong_connter) / ftime);
                    yield return new WaitForEndOfFrame();
                }
                //计数防止重复时提前关闭
                timBingDong--;
                if (timBingDong <= 0)
                {
                    img.enabled = false;
                }
                if (timBingDong + OthertimBingDong <= 0)
                {
                    //结束
                    //ChangeSFX.instnace.ICE.gameObject.SetActive(false);
                    common2.transICE.gameObject.SetActive(false);
                    //所有鱼解冻
                    foreach (var it in common.listFish)
                    {
                        it.Value.Skill_Ice(false);
                    }
                    b_SkillIce = false;
                }
            }
            else
            {
                SoundLoadPlay.PlaySound("frezon");
                //冰冻背景
                common2.transICE.gameObject.SetActive(true);
                //所有鱼冰冻     
                foreach (var it in common.listFish)
                {
                    it.Value.Skill_Ice(true);
                    //if (it.Value.isBoss == false)
                    //{
                    //    //不冻boss
                    //    it.Value.Skill_Ice(true);
                    //}
                }
                b_SkillIce = true;
                OthertimBingDong++;
                //使用系统时间 以防切出去
                float bingdong_connter = Time.realtimeSinceStartup;
                //等待时间 和CD图片
                while (Time.realtimeSinceStartup - bingdong_connter < ftime)
                {
                    //所有鱼冰冻     
                    foreach (var it in common.listFish)
                    {
                        it.Value.Skill_Ice(true);
                        //if (it.Value.isBoss == false)
                        //{
                        //    //不冻boss
                        //    it.Value.Skill_Ice(true);
                        //}
                    }
                    yield return new WaitForEndOfFrame();
                }
                //计数防止重复时提前关闭
                OthertimBingDong--;
                if (timBingDong + OthertimBingDong <= 0)
                {
                    //结束
                    common2.transICE.gameObject.SetActive(false);
                    //所有鱼解冻
                    foreach (var it in common.listFish)
                    {
                        it.Value.Skill_Ice(false);
                    }
                    b_SkillIce = false;
                }
            }

        }
        private void On_SetMultResponse(long playerId, int mult)
        {
        }
        int timbaoji = 0;
        IEnumerator IESkill_baoji(float ftime)
        {
            timbaoji++;
            Image img = btn_baoji.transform.parent.Find("Mask").GetComponent<Image>();
            img.enabled = true;
            img.fillAmount = 1;
            float mtime = ftime;
            float kuangbao_connter = Time.realtimeSinceStartup;
            //等待时间 和CD图片
            while (Time.realtimeSinceStartup - kuangbao_connter < ftime)
            {
                img.fillAmount = 1 - ((Time.realtimeSinceStartup - kuangbao_connter) / ftime);
                yield return new WaitForEndOfFrame();
            }
            timbaoji--;
            if (timbaoji <= 0)//最后一个
            {
                img.enabled = false;
                //if (common.listPlayer.ContainsKey(PlayerData.PlayerId))
                //{
                //    GetOnePlayer(PlayerData.PlayerId).NKuangBaoMult = 1;
                //}
            }
        }
        void OnPlayerJoinRoom(FishingPlayerInfoProto data)
        {
            //Debug.Log(data.playerId+"加入房间");
            ////加入之前先移除之前相同的
            //if (common.listPlayer.ContainsKey(data.playerId))//存在此玩家
            //{
            //    if (common.listPlayer[data.playerId].name==data.seat.ToString())//座位号相同 更新
            //    {
            //        //座位号相等更新数据
            //        PlayerInfo pi = new PlayerInfo();
            //        pi.playerId = data.playerId;
            //        pi.name = data.name;
            //        pi.money = data.money;
            //        pi.diamond = data.diamond;
            //        pi.seat = data.seat;
            //        pi.sex = data.sex;
            //        pi.url = data.headUrl;
            //        if (data.headIndex > 0)
            //        {
            //            pi.url = data.headIndex.ToString();
            //        }
            //        pi.isOnline = data.online;
            //        pi.nHeadIndex = data.headIndex;
         
            //        pi.nRoleLevel = data.level;
            //        //赋值
            //        common.listPlayer[pi.playerId] = objPlayer[pi.seat];
            //        objPlayer[pi.seat]._pi = pi;
            //        //打开 
            //        //objPlayer[pi.seat].gameObject.SetActive(true);
            //        //炮台初始化
            //        objPlayer[pi.seat].InitPaoAndWin(data.viewIndex, data.wingIndex, data.batteryLevel);
             
            //        if (pi.playerId == PlayerData.PlayerId)
            //        {
            //            NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
            //            //NetMessage.OseeFishing.Req_FishingPlayerInfoRequest(pi.pos);
            //            MyPlayer = objPlayer[pi.seat];

            //            MyPlayer.bAutoFire = false;
            //            MyPlayer.ChangeGold(PlayerData.Gold);//
            //            MyPlayer.ChangeDiamond(PlayerData.Diamond);
            //            //翅膀
            //            PlayerData.PaoViewIndex = data.viewIndex;
            //            PlayerData.WingIndex = data.wingIndex;
            //            MyPlayer.ChangePaoView(PlayerData.PaoViewIndex);
            //            MyPlayer.ChangeWingView(PlayerData.WingIndex);
            //            //捕获提示
            //            CatchTips.gameObject.SetActive(false);
            //        }
            //    }
            //    else
            //    {
            //        common.listPlayer[data.playerId].gameObject.SetActive(false);
            //        //座位号不相等则移除
            //        common.listPlayer.Remove(data.playerId);
            
            //    }
            //}
            ////增加玩家
            //if (!common.listPlayer.ContainsKey(data.playerId))
            //{
            //    PlayerInfo pi = new PlayerInfo();
            //    pi.playerId = data.playerId;
            //    pi.name = data.name;
            //    pi.money = data.money;
            //    pi.diamond = data.diamond;
            //    pi.seat = data.seat;
            //    pi.sex = data.sex;
            //    pi.url = data.headUrl;
            //    if (data.headIndex > 0)
            //    {
            //        pi.url = data.headIndex.ToString();
            //    }
            //    pi.isOnline = data.online;
            //    pi.nHeadIndex = data.headIndex;
            //    pi.nMinLevel = 1000000;
            //    pi.nMaxLevel = 0;
            //    foreach (var it in common.dicPaoConfig.Values)
            //    {
            //        if (it.nModule == ByData.nModule)
            //        {
            //            if (it.nLevel < pi.nMinLevel)
            //            {
            //                pi.nMinLevel = it.nLevel;
            //            }
            //            if (it.nLevel > pi.nMaxLevel)
            //            {
            //                pi.nMaxLevel = it.nLevel;
            //            }
            //        }
            //    }
            //    pi.nRoleLevel = data.level;
            //    common.listPlayer.Add(pi.playerId, objPlayer[pi.seat]);
            //    objPlayer[pi.seat]._pi = pi;
            //    //objPlayer[pi.seat].gameObject.SetActive(true);
            //    objPlayer[pi.seat].InitPaoAndWin(data.viewIndex, data.wingIndex, data.batteryLevel);
       
            //    //自己数据
            //    if (pi.playerId == PlayerData.PlayerId)
            //    {
            //        //玩家状态请求
            //        NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);

            //        MyPlayer = objPlayer[pi.seat];
            //        MyPlayer.bAutoFire = false;
            //        MyPlayer.ChangeGold(PlayerData.Gold);//
            //        MyPlayer.ChangeDiamond(PlayerData.Diamond);
            //        //炮台
            //        PlayerData.PaoViewIndex = data.viewIndex;
            //        PlayerData.WingIndex = data.wingIndex;
            //        MyPlayer.ChangePaoView(PlayerData.PaoViewIndex);
            //        MyPlayer.ChangeWingView(PlayerData.WingIndex);
                    
            //        CatchTips.gameObject.SetActive(false);
            //        //座位提示
            //        TipsPos.gameObject.SetActive(true);
            //        TipsPos.transform.SetParent(MyPlayer.transform);
            //        TipsPos.SetAsFirstSibling();
            //        TipsPos.transform.localScale = Vector3.one;
            //        Vector3 pos = MyPlayer.transform.Find("imgDizuo").localPosition;
            //        TipsPos.transform.localPosition = pos;
            //        if (MyPlayer.name == "2" || MyPlayer.name == "3")
            //        {
            //            TipsPos.transform.localPosition = new Vector3(pos.x, pos.y - 50f, pos.z);
            //            TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            //        }
            //        else
            //        {
            //            TipsPos.transform.localPosition = new Vector3(pos.x, pos.y + 50f, pos.z);
            //            TipsPos.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            //        }
            //    }
            //}
            ////等待加入按钮更改
            //for (int i = 0; i < 4; i++)
            //{
            //    btn_ChanegSet[i].gameObject.SetActive(false);
            //    if (objPlayer[i].gameObject.activeSelf)
            //    {
            //        trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(false);
            //    }
            //    else
            //    {
            //        trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(true);
            //    }
            //}
      
        }
        void ChangePlayerInfo() {

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
            }
            else
                return false;
        }
      
    
        /// <summary>
        /// 捕鱼玩家列表信息返回
        /// <summary>
        private void On_FishingPlayersInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingPlayersInfoResponse>();
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
        }
        void RatatePlayer(bool isbool) {
            if (isbool)//旋转
            {
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
        private void InitFish(FishingFishInfoProto ms)
        {
            if (ms==null)
            {
                Debug.LogWarning("ms==null");
                return;
            }
            if (!common.listFish.ContainsKey(ms.id))
            {
                if (common4.dicFishConfig.ContainsKey(ms.fishId))
                {
                    //召唤的黄金鱼轨迹
                    if (ms.routeId >= 10453 && ms.routeId <= 10467)
                    {
                        if (ms.clientLifeTime <= 1)
                        {
                            StartCoroutine(HornInitFish(ms));
                            return;
                        }
                    }
                    var goFish = commonLoad.GetOneAynsFish(common4.GetFishModleID(common4.dicFishConfig[ms.fishId].name), Root3D.Instance.rootFish);//{
    
                    goFish.SetActive(true);

                    var mf = goFish.GetComponent<fish>();
                    if (mf!=null)
                    {
                        mf.Init(new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, 0, false));
                    }
                    else
                    {
                        Debug.Log("mf==null");
                        var mp = new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime,0, false);
                        StartCoroutine(StartInitFish(goFish, mp));
                    }
      
                }
                else
                {
                    Debug.LogError("鱼id不存在于dicFishConfig  " + ms.fishId);
                }
            }
            else
            {
            } 
        }
        public void SetZiDong(bool state)
        {
            if (state)
            {
                PlayerData._bZiDong = true;
                PlayerData.SetRootbZiDong(true);
                btn_autofire.transform.parent.Find("effect").gameObject.SetActive(true);
            }
            else
            {
                PlayerData._bZiDong = false;
                PlayerData.SetRootbZiDong(false);
                btn_autofire.transform.parent.Find("effect").gameObject.SetActive(false);
            }
        }
        IEnumerator StartInitFish(GameObject goFish, TmpFishingFishInfoProto pp)
        {
            yield return new WaitForSeconds(1f);
            var mf = goFish.GetComponent<fish>();
            if (mf != null)
            {
                mf.Init(pp);
            }
            else
            {
                Debug.Log("mf==null");
            }
        }

        IEnumerator HornInitFish(FishingFishInfoProto ms) 
        {
            //在技能回包里显示神灯飞过去的线
            iTweenPath ipath = common.GetRootPath().Find(common.dicPathConfig[ms.routeId].pathId.ToString()).GetComponent<iTweenPath>();
            if (ipath == null)
            {
                Debug.LogError("轨迹ID不存在" + common.dicPathConfig[ms.routeId].pathId.ToString());
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
                var mp = new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, ms.isBossBulge, false);
                mf.Init(mp);
            }
            else
            {
                Debug.Log("mf==null");
            }
        }
        public void DestroyThisRoomToLogin()
        {
            UIMgr.DestroyCreateUI();
            //如果是自己就退出房间并清空数据           
            if (common2.BulletPos != null)
            {
                //先删除所有子弹
                for (int i = common2.BulletPos.childCount - 1; i >= 0; i--)
                {
                    if (common2.BulletPos.GetChild(i) != null)
                    {
                        GameObject.Destroy(common2.BulletPos.GetChild(i));
                    }
                }
            }
            //卸载场景
            try
            {
                SceneManager.UnloadSceneAsync("ZBuyuRoom");
            }
            catch
            {
            }
            UIMgr.ShowUI(UIPath.UIMainMenu);
            UIMgr.CloseUI(UIPath.UIByRoomMain);
            Root3D.Instance.ShowAllObject(false);
            for (int i = 0; i < objPlayer.Count; i++)
            {
                objPlayer[i].bAutoFire = false;
                objPlayer[i].gameObject.SetActive(false);
            }
            ClearDestory();
        }
        /// <summary>
        /// 捕鱼退出房间返回
        /// <summary>
        private void On_FishingExitRoomResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingExitRoomResponse>();
            if (pack.playerId == PlayerData.PlayerId)
            {
                UIMgr.DestroyCreateUI();
                //如果是自己就退出房间并清空数据           
                if (common2.BulletPos!=null)
                {
                    //先删除所有子弹
                    for (int i = common2.BulletPos.childCount - 1; i >= 0; i--)
                    {
                        if (common2.BulletPos.GetChild(i) != null)
                        {
                            GameObject.Destroy(common2.BulletPos.GetChild(i));
                        }
                    }
                }
                //卸载场景
                try
                {
                    SceneManager.UnloadSceneAsync("ZBuyuRoom");
                }
                catch 
                {
                }
                UIMgr.ShowUI(UIPath.UIMainMenu);
                UIMgr.CloseUI(UIPath.UIByRoomMain);
                Root3D.Instance.ShowAllObject(false);
                for (int i = 0; i < objPlayer.Count; i++)
                {
                    objPlayer[i].bAutoFire = false;
                    objPlayer[i].gameObject.SetActive(false);
                }
                ClearDestory();
                //Resources.UnloadUnusedAssets();
                //System.GC.Collect();
            }
            else
            {
                //其他人退出，更新当前房间信息
                if (common.listPlayer.ContainsKey(pack.playerId))
                {
                    common.listPlayer[pack.playerId].gameObject.SetActive(false);

                    for (int i = 0; i < 4; i++)
                    {
                        if (ByData.nModule == 5)//普通场五号场可以换座位
                        {
                            if (objPlayer[i].gameObject.activeSelf)
                            {
                                btn_ChanegSet[int.Parse(objPlayer[i].name)].gameObject.SetActive(false);
                            }
                            else
                            {
                                btn_ChanegSet[int.Parse(objPlayer[i].name)].gameObject.SetActive(true);
                            }
                        }
                        else
                        {
                            if (objPlayer[i].gameObject.activeSelf)
                            {
                                trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(false);
                            }
                            else
                            {
                                trans_waitjion[int.Parse(objPlayer[i].name)].gameObject.SetActive(true);
                            }
                        }
                    }
                    common.listPlayer.Remove(pack.playerId);
                }
            }
        }
        /// <summary>
        /// 捕鱼同步返回
        /// <summary>
        private void On_FishingSynchroniseResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingSynchroniseResponse>();
            for (int i = 0; i < pack.fishInfos.Count; i++)
            {
                var it = pack.fishInfos[i];
                if (common4.dicFishConfig.ContainsKey(it.fishId))
                {
                    if (it.isBossBulge == 0)
                    {
                        // 0 代表不是boss号角召唤的鱼 所有会提前生成 有提示
                        StartCoroutine(tmpAdd(0f - it.clientLifeTime, it));                     
                    }
                    else
                    {
                        InitFish(it);
                        //Debug.Log("捕鱼同步返回" + it.clientLifeTime);
                    }
                }
                else
                {
                    Debug.LogWarning("配置文件没有找到此鱼ID" + it.fishId);
                }
            }
        }
        /// <summary>
        /// 捕鱼刷新房间鱼类返回
        /// <summary>
        private void On_FishingRefreshFishesResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingRefreshFishesResponse>();
            for (int i = 0; i < pack.fishInfos.Count; i++)
            {
                var it = pack.fishInfos[i];
               // Debug.Log(string.Format("fishId:{0},id:{1},routeId:{2},isBossBulge:{3},clientLifeTime:{4},createTime:{5}", it.fishId, it.id, it.routeId, it.isBossBulge, it.clientLifeTime, it.createTime));
                if (common4.dicFishConfig.ContainsKey(it.fishId))
                {

                    //InitFish(it);     
                    if (it.isBossBulge == 0)
                    {
                        StartCoroutine(tmpAdd(0f - it.clientLifeTime, it)); // 0 代表不是boss号角召唤的鱼 所有会提前生成 有提示
                    }
                    else
                    {
                        list_fishInit.Add(it);
                    }
                }
                else
                {
                    Debug.LogWarning(common4.dicFishConfig.Count);
                    Debug.LogWarning("配置文件没有找到此鱼ID" + it.fishId);
                }
            }
            obj = null;


        }
        /// <summary>
        /// 二次伤害击中鱼响应
        /// <summary>
        private void On_FishingDoubleKillFishResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingDoubleKillFishResponse>();
            for (int i = 0; i < pack.fishds.Count; i++)
            {
                if (common.listFish.ContainsKey(pack.fishds[i]))
                {
                    common.listFish[pack.fishds[i]].FishDie();
                }
      
            }
        }
        /// <summary>
        /// 钻头击中鱼响应
        /// <summary>
        private void On_BitFightFishResponse(UEventContext obj)
        {
            var pack = obj.GetData<BitFightFishResponse>();
            //判断是否存在鱼
            if (common.listFish.ContainsKey(pack.fishId))
            {
                //判断是否存在该玩家
                if (common.listPlayer.ContainsKey(pack.playerId))
                {
                    player tmpplayer = common.listPlayer[pack.playerId];
                    //更改金币数显示
                    GetOnePlayer(pack.playerId).ChangeGold(pack.restMoney);
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
                    }
                    //type大于0代表有核弹或绑定核弹
                    if (pack.type > 0)
                    {
                        //鱼坐标点
                        Vector2 varfishpos = common.WordToUI(common.listFish[pack.fishId].transform.position);
                        //本地配置表ID
                        var varDicfishId = common.listFish[pack.fishId].fishState.fishId;
                        long varhedan = 0;
                        long varhedanbang = 0;
                        foreach (var item in pack.dropItems)
                        {
                            if (item.itemId == 5 || item.itemId == 6 || item.itemId == 7)
                            {
                                varhedan += item.itemNum;
                            }
                            if (item.itemId == 20)
                            {
                                varhedanbang += item.itemNum;
                            }
                        }
                        Debug.LogError("varhedan"+ varhedan+"varhedanbang" + varhedanbang);
                        //StartCoroutine(Animal_HedanBei(common4.dicFishConfig[varDicfishId].monsterId, common4.dicFishConfig[varDicfishId].name, varhedan, varhedanbang, pack.dropMoney, varfishpos, pack.playerId, (int)pack.targetMult));
                        ////得先拿到一些参数才能死鱼
                        //common.listFish[pack.fishId].FishDie(pack.dropMoney, varPlayerId: pack.playerId);
                        return;
                    }
                    else
                    {
                        //没有核弹
                        Vector2 varfishpos = common.WordToUI(common.listFish[pack.fishId].transform.position);
                        //道具
                        foreach (var item in pack.dropItems)
                        {
                            if (item.itemId == 18)//是龙晶
                            {
                                if (item.itemNum > 0)
                                {
                                    //StartCoroutine(AnimalItemDropMult(varfishpos, pack.playerId, item.itemNum));
                                }
                            }
                            else
                            {
                                AnimalItemDrop(item.itemId, varfishpos, tmpplayer.objPaotai.transform.position, item.itemNum);
                            }
                        }
                        //金币绑定
                        if (pack.dropMoney > 0)
                        {
                            //金钱掉落动画
                            AnimalMoneyDrop(varfishpos, tmpplayer.objPaotai.transform.position, pack.dropMoney, tmpplayer.n_nameSeat, common4.dicFishConfig[common.listFish[pack.fishId].fishState.fishId].monsterId);
                        }
                        //销毁鱼
                        common.listFish[pack.fishId].FishDie(varPlayerId: pack.playerId); 
                    }
                }
                else
                {
                    Debug.LogWarning("pack.playerId" + pack.playerId + "已经不存在了");
                    common.listFish[pack.fishId].FishDie();
                }
            }
            else
            {
                Debug.LogWarning("鱼pack.fishId" + pack.fishId + "已经不存在了");
            }
        }
        /// <summary>
        /// 同步鱼雷炮响应
        /// <summary>
        private void On_UseTroResponse(UEventContext obj)
        {
        }
        /// <summary>
        /// 同步黑洞炮响应
        /// <summary>
        private void On_UseBlackResponse(UEventContext obj)
        {
          
        }
        /// <summary>
        /// 同步电磁炮响应
        /// <summary>
        private void On_UseEleResponse(UEventContext obj)
        {
            var pack = obj.GetData<UseEleResponse>();
          
        }
        //public enum ITEM : int//任务
        //{
        //    gold = 1,
        //    bankGold = 2,
        //    jiangquan = 3,
        //    diamond = 4,
        //}
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
        /// 解锁炮台等级响应
        /// <summary>
        private void On_UnlockBatteryLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<UnlockBatteryLevelResponse>();
            //if (common.listPlayer.ContainsKey(PlayerData.PlayerId))
            //{
            //    //common.listPlayer[PlayerData.PlayerId].nMaxLevel = pack.level;
            //    tog_ShowjiesuoLV.transform.Find("Text").GetComponent<Text>().text = pack.level.ToString() + "倍";
            //    NetMessage.OseeFishing.Req_UnlockBatteryLevelHintRequest();
            //    objPlayer[common.listPlayer[PlayerData.PlayerId].pos].ChangePaoMult();
            //}
            //if (ByData.nModule < 5)
            //{
            //    if (PlayerData.PaoLevel == common.dicPaoFwConfig[ByData.nModule].Minlevel)
            //    {
            //        MessageBox.ShowConfirm("你已经解锁到下一场次，是否立即进入下一场次", null, () => {
            //            if (PlayerData.Gold >= common.dicMoneyConfig[ByData.nModule])
            //            {
            //                ByData.NewNormalnModule = ByData.nModule + 1;
            //                NetMessage.OseeFishing.Req_FishingExitRoomRequest();
            //            }
            //            else
            //            {
            //                MessageBox.Show("金币不足", null, () => {
            //                    NetMessage.OseeFishing.Req_FishingExitRoomRequest();
            //                    var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
            //                    tmp.Setpanel(1);
            //                });
            //            }
            //        });
            //    }
            //}
        }
        /// <summary>
        /// 捕鱼发射子弹返回
        /// <summary>
        private void On_FishingFireResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingFireResponse>();
            if (common.listPlayer.ContainsKey(pack.playerId))
            {
               GetOnePlayer(pack.playerId).ChangeGold(pack.restMoney);//("fish-playerMoney", pack.restMoney);// = pack.gold.ToString();

                if (pack.playerId != PlayerData.PlayerId)
                {
                    //objPlayer[common.listPlayer[pack.playerId].pos].Other_Re_DoFire(pack);
                    //Debug.LogWarning("Other_Re_DoFire");
                    GetOnePlayer(pack.playerId).Other_Re_DoFire(pack.angle, pack.fireId);
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
            var pack = obj.GetData<FishingChangeBatteryLevelResponse>();
            if (objPlayer != null)
            {
                if (common.listPlayer.ContainsKey(pack.playerId))
                {
                    common.listPlayer[pack.playerId].nPower = pack.level;
                }
            }
        }
        /// <summary>
        /// 改变狂暴倍数响应
        /// <summary>
        private void On_FishingChangeCritMultResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingChangeCritMultResponse>();
            //if (pack.mult == 1)
            //{
            //    all_Slider[0].isOn = true;
            //    all_Slider[1].isOn = false;
            //    all_Slider[2].isOn = false;
            //}
            //if (pack.mult == 2)
            //{
            //    all_Slider[1].isOn = true;
            //    all_Slider[0].isOn = false;
            //    all_Slider[2].isOn = false;
            //}
            //if (pack.mult == 4)
            //{
            //    all_Slider[2].isOn = true;
            //    all_Slider[1].isOn = false;
            //    all_Slider[0].isOn = false;
            //}
            //GetOnePlayer(PlayerData.PlayerId).NKuangBaoMult = pack.mult;

        }

        /// <summary>
        /// 解锁炮台等级提示返回
        /// <summary>
        private void On_UnlockBatteryLevelHintResponse(UEventContext obj)
        {
            var pack = obj.GetData<UnlockBatteryLevelHintResponse>();
           // Debug.Log("UnlockBatteryLevelHintResponse"+ pack.nextLevel+"sss"+ pack.cost);
           // txt_jeiSuoBei.text = pack.nextLevel.ToString() + "倍";
           // txt_bei.text = "解锁  " + pack.nextLevel.ToString() + "  倍炮";
           // txt_beiUp.text = "解锁  " + pack.nextLevel.ToString() + "  倍炮";
           // if (common.myCaiLiao.ContainsKey(23))
           // {
           //     txt_neednum.text = common.myCaiLiao[23] + "/" + pack.cost.ToString();
           //     txt_neednumUp.text = common.myCaiLiao[23] + "/" + pack.cost.ToString();
           // }
           //// tra_jiesuoUp.transform.Find("money").GetComponent<Text>().text = pack.rewardGold.itemNum.ToString();
           // next_bei = pack.nextLevel;
           // next_cost = pack.cost;
           // if (pack.nextLevel >= common.dicPaoFwConfig[MaxPaoFw].Maxlevel)
           // {
           //     if (toggle_jeisuo.isOn)
           //     {
           //         tra_jiesuo.gameObject.SetActive(true);
           //         tra_jiesuoUp.gameObject.SetActive(false);
           //     }
           //     else
           //     {
           //         tra_jiesuo.gameObject.SetActive(false);
           //         tra_jiesuoUp.gameObject.SetActive(false);
           //     }
      
           //     txt_bei.text = "已达到最高等级炮台";
           //     txt_neednum.text = "暂无/暂无";
           // }
           // ChangeYuGu();
        }
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
                    //if (objPlayer[common.listPlayer[pack.robotId].pos].SD_diancipao)
                    //{

                    //    //objPlayer[common.listPlayer[pack.robotId].pos]._fwqShanDianAutoFish = pack.fishId;
                    //    objPlayer[common.listPlayer[pack.robotId].pos].S_C_NormalRobotDoFire(pack);
                    //}
                    //else
                    //{
                        common.listPlayer[pack.robotId].S_C_NormalRobotDoFire(pack);
                    //}

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
        /// 捕鱼击中鱼类返回  这个里包括其它玩家的返回
        /// <summary>
        private void On_FishingFightFishResponse(UEventContext obj)
        {
         
            var pack = obj.GetData<FishingFightFishResponse>();
            //判断是否存在鱼
            if (common.listFish.ContainsKey(pack.fishId))
            {
                player tmpplayer = GetOnePlayer(pack.playerId);
                //判断是否存在该玩家
                if (tmpplayer!=null)
                {
                    //因为金币飞到一半时 玩家有可能 已经离开房间  所以用座位号  或位置飞金币
                        string fishname = common4.dicFishConfig[common.listFish[pack.fishId].fishState.fishId].name;
                        if (fishname == "黄金水母")
                        {
                            GameObject m = common4.LoadPrefab("GoldTurntable/gold_turntable");
                            var go = Instantiate(m, common2.BuyuUICanvas);
                            go.transform.position = GetOnePlayer(pack.playerId).pos_caijin.transform.position;
                            go.transform.Find("root/ui_dazhuanpan/GameObject/ribbon/nameText").GetComponent<Text>().text = "黄金水母";
                            go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = pack.dropMoney.ToString();
                        }
                        else if (fishname == "黄金座头鲸")
                        {
                            GameObject m = common4.LoadPrefab("GoldTurntable/gold_turntable");
                            var go = Instantiate(m, common2.BuyuUICanvas);
                            go.transform.position = GetOnePlayer(pack.playerId).pos_caijin.transform.position;
                            go.transform.Find("root/ui_dazhuanpan/GameObject/ribbon/nameText").GetComponent<Text>().text = "黄金座头鲸";
                            go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = pack.dropMoney.ToString();
                        }
                        else if (fishname == "黄金锤头鲨")
                        {
                            GameObject m = common4.LoadPrefab("GoldTurntable/gold_turntable");
                            var go = Instantiate(m, common2.BuyuUICanvas);
                            go.transform.position = GetOnePlayer(pack.playerId).pos_caijin.transform.position;
                            go.transform.Find("root/ui_dazhuanpan/GameObject/ribbon/nameText").GetComponent<Text>().text = "黄金锤头鲨";
                            go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = pack.dropMoney.ToString();
                        }
                        else if (fishname == "黄金独角鲸")
                        {
                            GameObject m = common4.LoadPrefab("GoldTurntable/gold_turntable");
                            var go = Instantiate(m, common2.BuyuUICanvas);
                            go.transform.position = GetOnePlayer(pack.playerId).pos_caijin.transform.position;
                            go.transform.Find("root/ui_dazhuanpan/GameObject/ribbon/nameText").GetComponent<Text>().text = "黄金独角鲸";
                            go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = pack.dropMoney.ToString();
                        }
                        else if (fishname == "黄金海龟")
                        {
                            GameObject m = common4.LoadPrefab("GoldTurntable/gold_turntable");
                            var go = Instantiate(m, common2.BuyuUICanvas);
                            go.transform.position = GetOnePlayer(pack.playerId).pos_caijin.transform.position;
                            go.transform.Find("root/ui_dazhuanpan/GameObject/ribbon/nameText").GetComponent<Text>().text = "黄金海龟";
                            go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = pack.dropMoney.ToString();
                        }
                        //更改金币数显示
                        tmpplayer.ChangeGold(pack.restMoney);
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
                                //Root3D.Instance.cam3D.GetComponent<Shaker>().enabled = true;
                                common4.SetCamreRoomIN();
                            }
                        }

                        //type大于0代表有核弹或绑定核弹
                        if (pack.type > 0)
                        {
                            //鱼坐标点
                            Vector2 varfishpos = common.WordToUI(common.listFish[pack.fishId].transform.position);
                            //本地配置表ID
                            var varDicfishId = common.listFish[pack.fishId].fishState.fishId;
                            long varhedan = 0;
                            long varhedanbang = 0;
                            foreach (var item in pack.dropItems)
                            {
                                if (item.itemId == 5 || item.itemId == 6 || item.itemId == 7)
                                {
                                    varhedan += item.itemNum;
                                }
                                if (item.itemId == 20)
                                {
                                    varhedanbang += item.itemNum;
                                }
                            }
                            Debug.LogError("varhedan" + varhedan + "varhedanbang" + varhedanbang);
                            //StartCoroutine(Animal_HedanBei(common4.dicFishConfig[varDicfishId].monsterId, common4.dicFishConfig[varDicfishId].name, varhedan, varhedanbang, pack.dropMoney, varfishpos, pack.playerId, (int)pack.targetMult));
                            ////得先拿到一些参数才能死鱼
                            //common.listFish[pack.fishId].FishDie(pack.dropMoney, varPlayerId: pack.playerId);
                            return;
                        }
                        else
                        {
                            //没有核弹
                            Vector2 varfishpos = common.WordToUI(common.listFish[pack.fishId].transform.position);
                            //道具
                            foreach (var item in pack.dropItems)
                            {
                                if (item.itemId == 18)//是龙晶
                                {
                                    if (item.itemNum > 0)
                                    {
                                        Debug.LogError("是龙晶" + item.itemNum);
                                      //  StartCoroutine(AnimalItemDropMult(varfishpos, pack.playerId, item.itemNum));
                                    }
                                }
                                else
                                {
                                    AnimalItemDrop(item.itemId, varfishpos, tmpplayer.objPaotai.transform.position, item.itemNum);
                                }
                            }
                            //金币绑定
                            if (pack.dropMoney > 0)
                            {
                                //金钱掉落动画
                                AnimalMoneyDrop(varfishpos, tmpplayer.objPaotai.transform.position,  pack.dropMoney,int.Parse(tmpplayer.name), common4.dicFishConfig[common.listFish[pack.fishId].fishState.fishId].monsterId);
                            }
                            else
                            {
                                //二次伤害时 击杀的鱼没有金币
                                OnlyFishDieEffect(varfishpos);
                            }
                            //销毁鱼
                            common.listFish[pack.fishId].FishDie(varPlayerId: pack.playerId);
                        }
                    //}
                    //else
                    //{
                    //    Debug.LogWarning("playerPos" + varplayerPos + "位置不对");
                    //    common.listFish[pack.fishId].FishDie();
                    //}
                }
                else
                {
                    Debug.LogWarning("pack.playerId" + pack.playerId + "已经不存在了");
                    common.listFish[pack.fishId].FishDie();
                }
            }
            else
            {
                Debug.LogWarning("鱼pack.fishId" + pack.fishId + "已经不存在了");
            }
        }
        public void OpenReConnecting(bool state)
        {
           
        }
        IEnumerator HuiSHou(GameObject dieEffect, string str)
        {
            yield return new WaitForSeconds(1f);
            commonLoad.ReciveOneDic(str, dieEffect);
        }
        IEnumerator DisableSetCamreRoomIN() 
        { 
            yield return new WaitForSeconds(3f);
            common4.SetCamreRoomIN();
        }
        // 死亡掉落金币动画
        public void AnimalMoneyDrop(Vector3 fishpos, Vector3 paotaiPos, long gold, int namePos, long fishid)
        { 
            if (gold>0)
            {
                //显示金币文本
                var dieEffect = commonLoad.GetOneDicPool("BuyuPrefabs/TxtPaoFen", common2.GoldNormal);
                dieEffect.GetComponent<Text>().text= gold.ToString("N0");
                dieEffect.transform.localPosition = fishpos;
                dieEffect.transform.localScale = Vector3.zero;
                dieEffect.transform.DOLocalJump(fishpos,5,1,0.3f);
                dieEffect.transform.DOScale(0.8f, 0.3f);
                StartCoroutine(HuiSHou(dieEffect, "BuyuPrefabs/TxtPaoFen"));
            }
            if (common4.dicFishConfig.ContainsKey(fishid))
            {
                //根据鱼倍数显示不同的金币数  飞向玩家
                if (common4.dicFishConfig[fishid].fishType == 4)
                {
                    //黄金鱼
                    GoldFishDieEffect(fishpos, paotaiPos,  gold, namePos);
                }
                else
                {
                    if (common4.dicFishConfig[fishid].money < 40)
                    {
                        SmallFishDieEffect(fishpos, paotaiPos, gold, namePos);
                        return;
                    }
                    else if (common4.dicFishConfig[fishid].money < 100)
                    {
                        BigFishDieEffect(fishpos, paotaiPos,  gold, namePos);
                        return;
                    }
                    else
                    {
                        BigFishDieEffect(fishpos, paotaiPos, gold, namePos);
                        return;
                    }
                }
            }
        }
        
        void SmallFishDieEffect(Vector3 varfishpos, Vector3 endtargetpos,long gold, int playerPos)
        {
       
            int nNum= UnityEngine.Random.Range(1, 3);
          
            for (int i = 0; i < nNum; i++)
            {
                GameObject tmpgo; 
                bool bBigDrop = false;
                bBigDrop = true;
                int n = i;
                tmpgo = commonLoad.GetOneDieMoney( common2.BulletPos);
                try
                {
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(35f, 50f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw*i, 0f, 0f);                   
                    //跳跃
                    tmpgo.transform.DOLocalJump(tmpgo.transform.localPosition, 40f, 2, 1f).SetEase(Ease.Flash).OnComplete(() =>
                    {
                        if (n == nNum - 1)
                        {
                            tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() => {
                                PlayerPosPiaofen(gold,playerPos);
                                //回收
                                if (bBigDrop)
                                {
                                    commonLoad.ReciveDieMoney(0, tmpgo);
                                }
                                else
                                {
                                    commonLoad.ReciveDieMoney(0, tmpgo);
                                }
                            });
                        }
                        else
                        {
                            tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(()=> {
                                //回收
                                if (bBigDrop)
                                {
                                    commonLoad.ReciveDieMoney(0, tmpgo);
                                }
                                else
                                {
                                    commonLoad.ReciveDieMoney(0, tmpgo);
                                }
                            });
                        }
                    });
                }
                catch
                {
                }
            }
        }
        void OnlyFishDieEffect(Vector3 varfishpos)
        {
            //烟花特效
            //var dieEffect = commonLoad.GetOneDicPool("Effect/die_effect/firework_blue", common2.BulletPos);
            //dieEffect.transform.SetParent(common2.BulletPos, false);
            //dieEffect.SetActive(true);
            //dieEffect.transform.localPosition = varfishpos;
            //dieEffect.transform.Find("blue_line").gameObject.SetActive(true);
            //DOVirtual.DelayedCall(7, () =>
            //{
            //    dieEffect.transform.Find("yellow_line").gameObject.SetActive(false);
            //    commonLoad.ReciveOneDic("Effect/die_effect/firework_blue", dieEffect);
            //});
        }
        /// <summary>
        /// 黄金鱼死亡 target为炮台的UI世界坐标  varfishpos已经是UI坐标了
        /// </summary>
        /// <param name="endtargetpos"></param>
        void GoldFishDieEffect(Vector3 varfishpos, Vector3 endtargetpos,  long gold,int playerPos)
        {
            //金币爆炸特效
            var dieEffect = commonLoad.GetOneDicPool("Effect/die_effect/coin_explo_new_V3", common2.BulletPos);
            dieEffect.transform.SetParent(common2.BulletPos, false);
            dieEffect.SetActive(true);
            dieEffect.transform.localPosition = varfishpos;
            dieEffect.transform.localScale = Vector3.one * 3;
            //烟花特效
            var dieEffectyh = commonLoad.GetOneDicPool("Effect/die_effect/firework_yellow", common2.BulletPos);
            dieEffectyh.transform.SetParent(common2.BulletPos, false);
            dieEffectyh.SetActive(true);
            dieEffectyh.transform.localPosition = varfishpos;
            dieEffectyh.transform.localScale = Vector3.one;
            dieEffectyh.transform.Find("yellow_line").gameObject.SetActive(true);
            DOVirtual.DelayedCall(7, () =>
            {
                commonLoad.ReciveOneDic("Effect/die_effect/coin_explo_new_V3", dieEffect);
                commonLoad.ReciveOneDic("Effect/die_effect/firework_yellow", dieEffectyh);
            });
            //使用主路径 判断距离不使用相对路径   
            GameObject tmpgo1;
            bool bBigDrop1 = false;
            bBigDrop1 = true;
            tmpgo1 = commonLoad.GetOneDieMoney( common2.BulletPos);// common3._UIFishingInterface.GetitemPool_goldDrop().Get();
            try
            {
                tmpgo1.transform.SetParent(common2.BulletPos, false);
                tmpgo1.SetActive(true);
                tmpgo1.transform.localPosition = varfishpos;
                DOVirtual.DelayedCall(2f, () =>
                {
                    //移动
                    tmpgo1.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() => {
                        PlayerPosPiaofen(gold, playerPos);
                        //回收
                        if (bBigDrop1)
                        {
                            commonLoad.ReciveDieMoney(0, tmpgo1);
                        }
                        else
                        {
                            commonLoad.ReciveDieMoney(0, tmpgo1);
                        }
                    });
                });
              
            }
            catch
            {
            }
            //第一圈
            for (int i = 0; i < 5; i++)
            {
                GameObject tmpgo;
                bool bBigDrop = false;
                bBigDrop = true;
                tmpgo = commonLoad.GetOneDieMoney( common2.BulletPos);
                try
                {
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(45f, 90f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, 0f, 0f);
                    tmpgo.transform.RotateAround(tmpgo1.transform.position, tmpgo1.transform.forward, 72f * i + UnityEngine.Random.Range(-40f, 40f));
                    DOVirtual.DelayedCall(2f, () =>
                    {
                        //移动
                        tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() => {
                            //回收
                            if (bBigDrop)
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            }
                            else
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            }
                         });
                    });
                }
                catch
                {
                }
            }
            //第二圈
            for (int i = 0; i < 10; i++)
            {
                GameObject tmpgo;
                bool bBigDrop = false;
                bBigDrop = true;
                tmpgo = commonLoad.GetOneDieMoney( common2.BulletPos);
                try
                {
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(95f, 135f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, 0f, 0f);
                    tmpgo.transform.RotateAround(tmpgo1.transform.position, tmpgo1.transform.forward, 36 * i + UnityEngine.Random.Range(-40f, 40f));
                    DOVirtual.DelayedCall(2f, () =>
                    {
                        //移动
                        tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() =>
                        {
                            //回收
                            if (bBigDrop)
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            }
                            else
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo);
                            }
                        });
                    });
                }
                catch
                {
                }
            }
           
        }
        /// <summary>
        /// target为炮台的UI世界坐标  varfishpos已经是UI坐标了
        /// </summary>
        /// <param name="endtargetpos"></param>
        void BigFishDieEffect(Vector3 varfishpos, Vector3 endtargetpos, long gold, int playerPos)
        {
            //烟花特效
            var dieEffect = commonLoad.GetOneDicPool("Effect/die_effect/firework_blue", common2.BulletPos);
            dieEffect.transform.SetParent(common2.BulletPos, false);
            dieEffect.SetActive(true);
            dieEffect.transform.localPosition = varfishpos;
            dieEffect.transform.Find("blue_line").gameObject.SetActive(true);
            DOVirtual.DelayedCall(7, () =>
            {
                dieEffect.transform.Find("blue_line").gameObject.SetActive(false);
                commonLoad.ReciveOneDic("Effect/die_effect/firework_blue", dieEffect);
            });
            //使用主路径 判断距离不使用相对路径   
            GameObject tmpgo1;
            bool bBigDrop1 = false;
            bBigDrop1 = true;
            tmpgo1 = commonLoad.GetOneDieMoney( common2.BulletPos);// common3._UIFishingInterface.GetitemPool_goldDrop().Get();
            try
            {
                tmpgo1.transform.SetParent(common2.BulletPos, false);
                tmpgo1.SetActive(true);
                tmpgo1.transform.localPosition = varfishpos;
                float dilegthbase = Vector3.Distance(endtargetpos, tmpgo1.transform.position);
                DOVirtual.DelayedCall(2f, () =>
                {
                    tmpgo1.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() =>
                    {
                        PlayerPosPiaofen(gold, playerPos);
                        //回收
                        if (bBigDrop1)
                        {
                            commonLoad.ReciveDieMoney(0, tmpgo1);
                        }
                        else
                        {
                            commonLoad.ReciveDieMoney(0, tmpgo1);
                        }
                    });
                });
            }
            catch
            {
            }
            //第一圈
            for (int i = 0; i < 5; i++)
            {
                GameObject tmpgo;
                bool bBigDrop = false;
                bBigDrop = true;
                tmpgo = commonLoad.GetOneDieMoney( common2.BulletPos);
                try
                {
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(25f,70f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, 0f, 0f);
                    tmpgo.transform.RotateAround(tmpgo1.transform.position, tmpgo1.transform.forward, 72f * i + UnityEngine.Random.Range(-20f, 20f));
                
                    DOVirtual.DelayedCall(2f, () =>
                    {
                        tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() => {
                            //回收
                            if (bBigDrop1)
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo1);
                            }
                            else
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo1);
                            }
                        });
                    });
                 
                }
                catch
                {
                }
            }
            //第二圈
            for (int i = 0; i < 10; i++)
            {
                GameObject tmpgo;
                bool bBigDrop = false;
                bBigDrop = true;
                tmpgo = commonLoad.GetOneDieMoney( common2.BulletPos);
                try
                {
                    tmpgo.transform.SetParent(common2.BulletPos, false);
                    tmpgo.SetActive(true);
                    float fRaw = UnityEngine.Random.Range(75f, 105f);
                    tmpgo.transform.localPosition = varfishpos + new Vector3(fRaw, 0f, 0f);
                    tmpgo.transform.RotateAround(tmpgo1.transform.position, tmpgo1.transform.forward, 36 * i+ UnityEngine.Random.Range(-20f, 20f));

                    float dilegth = Vector3.Distance(endtargetpos, tmpgo.transform.position);
                    DOVirtual.DelayedCall(2f, () =>
                    {
                        tmpgo.transform.DOMove(endtargetpos, 20f).SetSpeedBased().OnComplete(() =>
                        {
                            //回收
                            if (bBigDrop1)
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo1);
                            }
                            else
                            {
                                commonLoad.ReciveDieMoney(0, tmpgo1);
                            }
                        });
                    });
                }
                catch
                {
                }
            }
     
        }
        public void AnimalMoneyDrop3(int nCount, Vector3 varfishpos, Vector3 endPos, long gold, int playerPos, long varfishId)
        {
            //bool bSelf = false;
            //if (common.listPlayer[PlayerData.PlayerId].pos == playerPos) 
            //{
            //    bSelf = true;
            //}
            ////飘分
            //GameObject numPiao = itemPool_piaofen.Get();

            //numPiao.transform.SetParent(common2.GoldNormal, false);
            //numPiao.gameObject.SetActive(true);
            //numPiao.transform.localPosition = varfishpos;
        
            //numPiao.GetComponent<Text>().text = gold.ToString();
            //numPiao.transform.DOBlendableLocalMoveBy(new Vector3(0, 0, 0), 2).OnComplete(() =>
            //{
            //    itemPool_piaofen.Recycle(numPiao.gameObject);
            //});
            ////金币飞行
            //StartCoroutine(Fly(nCount, varfishpos, endPos, playerPos, gold, bSelf));
        }
        // 道具掉落动画
        public void AnimalItemDrop(int itemId, Vector3 pos, Vector3 endPos, long itemCount)
        {
            if (itemCount > 50)
            {
                itemCount = 50;
            }
            for (int i = 0; i < itemCount; i++)
            {
                DOVirtual.DelayedCall(UnityEngine.Random.Range(0f, 0.5f), () =>
                {
                    using (zstring.Block())
                    {
                        GameObject goImage = common4.LoadPrefab(zstring.Format("DropItem/{0}", itemId));// Resources.Load<Sprite>(zstring.Format("item/{0}", itemId));
                        goImage.transform.localScale = Vector3.one;
                        GameObject gobject = Instantiate(goImage);
                        gobject.transform.SetParent(common2.GoldNormal, false);
                        gobject.transform.localScale = new Vector3(1f, 1f, 1f);
                        gobject.SetActive(true);
                        gobject.transform.localPosition = pos;

                        gobject.transform.DOLocalJump(gobject.transform.localPosition, 40f, 2, 1f).SetEase(Ease.Flash).OnComplete(() => {
                            gobject.transform.DOMove(endPos, 1.5f).OnComplete(() => {
                                Destroy(gobject);
                            });
                        });

                    }
                });
            }
        }
        ////稀有核弹
        //IEnumerator Animal_HedanBei(long fishid, string fishName, long varHedan, long varHedanbang, long varGold, Vector3 pos, long playerId, int beishu)
        //{

        //    if (playerId == PlayerData.PlayerId)
        //    {
        //        KillBoss.gameObject.SetActive(true);
        //        KillBoss.InitThis(fishid, beishu, varGold, varHedan, varHedanbang);
        //    }

        //    yield return new WaitForSeconds(7f);

        //    if (varHedan > 0)//核弹大于0
        //    {
        //        AnimalItemDrop(6, pos, GetOnePlayer(playerId).objPaotai.transform.position, varHedan + varHedanbang);
        //    }

        //    if (varGold > 0)//金币大于0
        //    {
        //        DropDragonScale(pos, GetOnePlayer(playerId).objPaotai.transform.position,  varGold, GetOnePlayer(playerId).n_nameSeat, 100);
        //    }
        //    yield return new WaitForSeconds(3f);

        //    if (varGold > 0)
        //    {
        //        //yield return new WaitForSeconds(1.5f);
        //        //GO_Amazing.transform.position = GetOnePlayer(playerId).pos_caijin.position;
        //        //GO_Amazing.gameObject.SetActive(true);
        //        //GO_Amazing.Init(varGold, common.listPlayer[playerId].pos);
        //        //yield return new WaitForSeconds(3f);
        //        //GO_Amazing.gameObject.SetActive(false);

        //        //其它人的也会显示
        //        ShowFishIconMoney(playerId, fishName, fishid, varGold);
        //    }
        //}

        void PlayerPosPiaofen(long gold,int ngdseat)
        {
            try
            {
                UIShowGold popMsg = UIMgr.ShowUISynchronize(UIPath.UIShowGold).GetComponent<UIShowGold>();
                popMsg.ShowMessage("+" + gold.ToString(),ngdseat);
            }
            catch
            {

            }

        }
        ///// <summary>
        ///// 掉落龙晶
        ///// </summary>
        //public void DropDragonScale(Vector3 pos, Vector3 endPos,long gold, int playerPos, int modelID)
        //{
        //    int nCount = 9;

        //    bool bSelf = false;
        //    if (common.listPlayer[PlayerData.PlayerId].name == playerPos.ToString())
        //    {
        //        bSelf = true;
        //    }

        //    GameObject tmpgo = Instantiate<GameObject>(prefabBoomlongjin, common2.GoldNormal);
        //    tmpgo.transform.position = new Vector3(pos.x, pos.y, -250f);
    
        //    for (int i = 0; i < nCount; i++)
        //    {
        //        float fDelay = 0.02f * i;
        //        GameObject go;
        //        if (bSelf)
        //        {
        //            go = commonLoad.GetOneDragonScale(1,common2.GoldNormal);// itemPool_DragonScale.Get();
        //        }
        //        else
        //        {
        //            go = commonLoad.GetOneDragonScale(2, common2.GoldNormal);
        //        }

        //        go.transform.SetParent(common2.GoldNormal, false);
        //        go.SetActive(true);
        //        go.transform.localScale = Vector3.one;
        //        go.transform.position = pos;

        //        int n = i % 3;
        //        int m = i / 3;
        //        float w = 40;
        //        float x, y;
        //        x = w * n - 80;
        //        y = -m * 50;
        //        go.transform.DOLocalJump(pos + new Vector3(x, y + n * 2, 0), 150, 1, 0.5f).SetDelay(fDelay).OnComplete(() =>
        //        {
        //            go.transform.DOLocalJump(pos + new Vector3(x, y + n * 2, 0), 50, 1, 0.3f).OnComplete(() =>
        //            {
        //                go.transform.DOLocalJump(pos + new Vector3(x, y + n * 2, 0), 10, 1, 0.15f).OnComplete(() =>
        //                {
        //                    go.transform.DORotate(new Vector3(0, 90, 0), 0.1f).SetDelay(1).SetLoops(4, LoopType.Yoyo);
        //                    go.transform.DOMove(endPos, 0.5f).SetDelay(1).OnComplete(() =>
        //                    {
        //                        //if (playerPos == common.listPlayer[PlayerData.PlayerId].seat)
        //                        //{
        //                        //    //if (i==9)
        //                        //    //{
        //                        //    //    int nIndex = modelID >= 30 ? 1 : 0;//BOSS播放1
        //                        //    //    SoundHelper.PlayMoneyDrop(nIndex);
        //                        //    //}                                
        //                        //}
        //                        if (fDelay == 0.02f * (nCount - 1))
        //                        {
        //                            PlayerPosPiaofen(gold,playerPos);
        //                        }
        //                        go.transform.DOScale(0, 0.5f).OnComplete(() =>
        //                        {
        //                            if (bSelf)
        //                            {
                
        //                                commonLoad.ReciveDragonScale(1, go);
        //                            }
        //                            else
        //                            {
                     
        //                                commonLoad.ReciveDragonScale(2, go);
        //                            }
        //                        });
        //                    });
        //                });
        //            });
        //        });
        //    }
        //}
        //IEnumerator AnimalItemDropMult(Vector3 pos, long playerId, long Num)
        //{
        //    //GO_Amazing.transform.position = GetOnePlayer(playerId).pos_caijin.position;
        //    //GO_Amazing.gameObject.SetActive(true);
        //    //GO_Amazing.Init(Num, int.Parse(common.listPlayer[playerId].name));
        //    //yield return new WaitForSeconds(3f);
        //    //GO_Amazing.gameObject.SetActive(false);
        //    //DropDragonScale(pos, GetOnePlayer(playerId).objPaotai.transform.position,  Num, GetOnePlayer(playerId).n_nameSeat, 100);
        //}
        //闪 
        IEnumerator tmpAdd(float time, FishingFishInfoProto it)
        {
            if (time >= 14)
            {
                tmpUIByBOOSAnimation(it.fishId);
            }
            yield return new WaitForSeconds(time);
            list_fishInit.Add(it);
        }
     

        public void ChangebSuoDingAutoFire()
        {
   

            long varm = GameHelper.ConvertDataTimeToLong(DateTime.Now);
        }
        ////得到
        //public GameObject ShandianGet()
        //{
        //    return ShandianPool.Get();
        //}
        //public void ChangebShanDianAutoFire()
        //{
        //    //if (objPlayer[common.listPlayer[PlayerData.PlayerId].pos].SD_diancipao == true)
        //    //{
        //    //    objPlayer[common.listPlayer[PlayerData.PlayerId].pos].SD_diancipao = false;
        //    //}
        //}





     

        public GameObject GetprefabYulei()
        {
     
            throw new NotImplementedException();
        }

   

        //public Transform Gettrans_fishTrigger()
        //{
        //    throw new NotImplementedException();
        //}

    

        //public List<player> GetobjPlayer()
        //{
        //    return objPlayer;
        //}

   

        //public GameObject GetleidainPao()
        //{
        //    throw new NotImplementedException();
        //}

        public void Req_XmlEndRequest(long userId, long reword, int type)
        {
            throw new NotImplementedException();
        }

        public void Req_UseEleRequest(long fishId)
        {
            NetMessage.OseeFishing.Req_UseEleRequest(fishId);
        }

        public void ChangeS_C_DoFire(FishingFireResponse vartmp, long fireId, long fishId, long fishId1, long fishId2, float angle)
        {
            MyPlayer.Re_DoFire(vartmp);
            //发射子弹
            NetMessage.OseeFishing.Req_FishingFireRequest(fireId, fishId, angle);
            //同步三管炮
            NetMessage.OseeFishing.Req_FishingSyncLockRequest(vartmp.playerId, fishId, fishId1, fishId2);
        }

        public void ShanDianPao_DoFire(FishingFireResponse vartmp, long fireId, long fishId, float angle)
        {
            //NetMessage.OseeFishing.Req_FishingFireRequest(fireId, fishId, angle);
        }

        public void ShanDianPaoSyncLockThree_DoFire(long playerId, long fishId, long fishId1, long fishId2)
        {
            //NetMessage.OseeFishing.Req_FishingSyncLockRequest(playerId, fishId, fishId1, fishId2);
        }

        //public void ZhuanTou_Fire(FishingFireResponse vartmp, long fireId, long fishId, float angle)
        //{
        //    //钻头请求击中
        //}

        public void SpecialFishRequest(List<long> fishIds, long specialFishId, long playerId)
        {
            NetMessage.OseeFishing.Req_CatchSpecialFishRequest(specialFishId, fishIds, playerId);
        }

        public void Req_FishingFightFishRequest(long fireId, long fishId)
        {
            NetMessage.OseeFishing.Req_FishingFightFishRequest(fireId, fishId);
        }

        public void Req_ChangePaoRequest(int paoIndex)
        {
            NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(paoIndex);
        }

        public void Req_FishingRobotFightFishRequest(long fireId, long fishId, long robotId)
        {
            NetMessage.OseeFishing.Req_FishingRobotFightFishRequest(fireId, fishId, robotId);
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
            NetMessage.OseeFishing.Req_FishingDoubleKillFishRequest(varListFish, PlayerID); 
        }
        public void Req_DoubleKillEndRequest(long AllGold, long PlayerID, long mult, string fishName)
        {
            NetMessage.OseeFishing.Req_FishingDoubleKillEndRequest(AllGold, PlayerID, mult, fishName);
        }

        public void ShowFishIconMoney(long playerId, string name, long fishid, long varGold)
        {
            //if (common.listFishData.ContainsKey(fishid))
            //{
            //    common.listFishData.Remove(fishid);
            //}
            if (name == "炸弹蟹")
            {
                GameObject m = common4.LoadPrefab("GoldTurntable/gold_bomb_turntable");
                var go = Instantiate(m, common2.BuyuUICanvas);
                go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
                go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = varGold.ToString();
                return;
            }
            else if (name == "钻头蟹")
            {
                var m = common4.LoadPrefab("GoldTurntable/gold_drill_turntable");
                var go = Instantiate(m, common2.BuyuUICanvas);
                go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
                go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = varGold.ToString();
                return;
            }
            else if (name == "雷神蟹")
            {
                var m = common4.LoadPrefab("GoldTurntable/gold_hammer_turntable");
                var go = Instantiate(m, common2.BuyuUICanvas);
                go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
                go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = varGold.ToString();
                return;
            }
            else if (name == "如来神掌")
            {
                var m = common4.LoadPrefab("GoldTurntable/gold_rulaishenzhangl_turntable");
                var go = Instantiate(m, common2.BuyuUICanvas);
                go.transform.position = common3._UIFishingInterface.GetOnePlayer(playerId).pos_caijin.transform.position;
                go.transform.Find("root/ui_dazhuanpan/GameObject/GameObject/Text").GetComponent<Text>().text = varGold.ToString();
                return;
            }
            //if (playerId==PlayerData.PlayerId)
            //{
            //    //Debug.LogError("ShowFishIconMoney");
            //    //只有自己显示 中大奖了
              
            //    var vargo = Instantiate(Object_bigWin, common2.BuyuUICanvas);
            //    vargo.transform.localScale = Vector3.one;
            //    vargo.transform.Find("Text/Text").GetComponent<Text>().text = varGold.ToString();
            //}
     
        }

        public Transform Getroot_DropEffect()
        {
            throw new NotImplementedException();
        }

        public Transform GetGoldNormal()
        {
            return GoldNormal;
        }

        //public GameObjectPool GetitemPool_goldDrop()
        //{
        //    return itemPool_goldDrop;
        //}

        //public GameObjectPool GetitemPool_goldDrop2()
        //{
        //    return itemPool_goldDrop2;
        //}

    

        //public void Req_BitFightFishRequest(List<long> varListFish)
        //{
        //    throw new NotImplementedException();
        //}

        //public Dictionary<int, GameObjectPool> GetitemPool_fishs()
        //{
        //    return itemPool_fishs;
        //}

        //public Transform GetfishPos()
        //{
        //    throw new NotImplementedException();
        //}

        public GameObjectPool GetitemPool_SkillDie()
        {
            throw new NotImplementedException();
        }
        public void SetMyItem()
        {
            btn_suoding.transform.parent.Find("txtCount").GetComponent<Text>().text = common.myItem[3].ToString();
            btn_bingdong.transform.parent.Find("txtCount").GetComponent<Text>().text = common.myItem[4].ToString();
            btn_baoji.transform.parent.Find("txtCount").GetComponent<Text>().text = common.myItem[6].ToString();
            btn_shengdeng.transform.parent.Find("txtCount").GetComponent<Text>().text = common.myDicItem[38].ToString();
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
            if (common2.BulletPos!=null)
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
            if (common2.GoldNormal != null)
            {
                if (common2.GoldNormal.childCount > 0)
                {
                    for (int i = common2.GoldNormal.childCount - 1; i >= 0; i--)
                    {
                        DestroyImmediate(common2.GoldNormal.GetChild(i).gameObject);
                    }
                }
            }
            //if (GoldNormal.childCount > 0)
            //{
            //    for (int i = GoldNormal.childCount - 1; i >= 0; i--)
            //    {
            //        DestroyImmediate(GoldNormal.GetChild(i).gameObject);
            //    }
            //}

            InitGame();
            Resources.UnloadUnusedAssets();
            System.GC.Collect();
        }
        void InitGame()
        {
            //num_combo.transform.parent.gameObject.SetActive(false);

            //common.dicBullet.Clear();
            //b_SkillIce = false;
            //if (common.listFish.Count > 0)
            //{
            //    foreach (var it in common.listFish.Values)
            //    {
            //        DestroyImmediate(it.gameObject);
            //    }
            //}
            //common.listFish.Clear();


            //if (rootFish != null)
            //{
            //    if (rootFish.childCount > 0)
            //    {
            //        for (int i = rootFish.childCount - 1; i >= 0; i--)
            //        {
            //            DestroyImmediate(rootFish.GetChild(i).gameObject);
            //        }
            //    }
            //}
       
            //if (root_DropEffect.childCount > 0)
            //{
            //    for (int i = root_DropEffect.childCount - 1; i >= 0; i--)
            //    {
            //        DestroyImmediate(root_DropEffect.GetChild(i).gameObject);
            //    }
            //}
            //if (NowNormal.childCount > 0)
            //{
            //    for (int i = NowNormal.childCount - 1; i >= 0; i--)
            //    {
            //        DestroyImmediate(NowNormal.GetChild(i).gameObject);
            //    }
            //}
        }
        public void ChangeGold()
        {
            throw new NotImplementedException();
        }

        public void AnimalYuleiBoom(int itemId, long money, Vector3 pos, long playerPlayerID, float range = 0, bool bPlaySound = true)
        {
            throw new NotImplementedException();
        }

        public void Req_FishingReactiveRequest()
        {
            NetMessage.OseeFishing.Req_FishingReactiveRequest();
            NetMessage.OseeFishing.Req_FishingSynchroniseRequest();
        }

        public GameObjectPool GetDropIconFish()
        {
            return Object_DropIconFishPool;
        }

        public void RedImageShan(bool tmp)
        {
            throw new NotImplementedException();
        }

        public void isPlayBossCommingSound(bool varPlay)
        {
            if (varPlay)
            {
                SoundLoadPlay.PlaySound("boosbg");
            }
            else
            {
                SoundLoadPlay.PlaySound("buyubg"); 
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
                    NetMessage.OseeFishing.Req_CatchSpecialFishRequest(-1 * (int)BY_SKILL.ZHUANTOU, varListFish1, playerID);
                }
            }
        }
        public void SyncLockRequest(long fishId)
        {
            NetMessage.OseeFishing.Req_FishingSyncLockRequest(PlayerData.PlayerId,fishId, 0, 0);
        }
        /// <summary>
        /// 二次伤害鱼返回
        /// <summary>
        private void On_FishingDoubleKillResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingDoubleKillResponse>();
            var monsterId = common4.dicFishConfig[common.listFish[pack.modelId].fishState.fishId].monsterId;
            var varname = pack.name;// common4.dicFishConfig[common.listFish[pack.modelId].fishState.fishId].name;
            var V3 = common.WordToUI(common.listFish[pack.modelId].transform.position);
            Debug.Log("pack.name"+pack.name);
            Debug.Log("pack.num:"+pack.num);
            Debug.Log("pack.modelId:" + pack.modelId);
            Debug.Log("pack.num1:" + pack.num1);
            Debug.Log("pack.num2:" + pack.num2);
            Debug.Log("pack.mult:" + pack.mult);
            Debug.Log("pack.winMoney:" + pack.winMoney);
            Debug.Log("pack.userId:" + pack.userId);
            
        }

        public void AnimalNet(int localPaoPrefab,int nPaoView, Vector3 pos, GameObject tmpBullet)
        {
            int mm = nPaoView;
            tmpBullet.SetActive(false);
            commonLoad.ReciveOneBullet(localPaoPrefab, tmpBullet);
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

        //public AudioClip[] Getm_listLitGoldDrop()
        //{
        //    throw new NotImplementedException();
        //}

        //public GameObjectPool GetitemPool_zhuanfanle()
        //{
        //    throw new NotImplementedException();
        //}

        public void TwoAttackFish(int Type, long playerID)
        {
            if (playerID == PlayerData.PlayerId)//是自己则请求
            {
                List<long> varListFish = new List<long>();
                foreach (var item in common.listFish)
                {
                    //类型
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

                    NetMessage.OseeFishing.Req_FishingDoubleKillFishRequest(varListFish, playerID);
                }
            }
        }
        public void TwoAttackNumFish(int Type, long playerID,int Num)
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
                            if (varListFish.Count>= Num)
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
                    NetMessage.OseeFishing.Req_FishingDoubleKillFishRequest(varListFish, playerID);
                }
            }
        }

        public player GetOnePlayer(long playerID)
        {
            if (!common.listPlayer.ContainsKey(playerID))
            {
                foreach (var item in common.listPlayer)
                {
                    Debug.LogWarning("全部" + item.Key);
                }
                Debug.LogWarning("错误"+ playerID);
                return null;
            }
            return common.listPlayer[playerID];
        }
        public void ReconnectionInRoom()
        {

        }

        public void JinZhu(bool state)
        {
            pig_title.gameObject.SetActive(state);
            Debug.Log("金猪状态"+state);
            if (state)
            {
                pig_title.transform.Find("pool2").GetComponent<StartJcUpdate>().SetStartText(122363411);
                pig_title.Play("pig_title_show");
            }
            else
            {
                pig_title.Play("pig_title_hide");
            }
        }
    }
}