using com.maple.game.osee.proto;
using com.maple.network.proto;
using ProtoBuf;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace Game.UI
{
    public static class PlayerData
    {
        public static float CanRechiveXiaoXi = 0;
        //默认不旋转
        public static bool IsRotateGame=false;
        //账号密码
        public static string Acount = "";
        public static string PassWord = "";
        public static string inputCraetName_phone;
        public static string inputCraetName_Captcha;    

        public static string str_CraetName_phone;
        public static string str_input_forgetAcc;
        //等级和vip 
        public static int Lv;
        public static int _vipLevel=0;
        public static long vipNextMoney; 
        public static long vipTotalMoney;
        //二次伤害鱼的个数
        public static int _TwoAttackNum; 
        //个人游戏参数
        private static long _PlayerId;
        private static string _NickName = "";
        private static string _UserName = "";
        public static long _PlayerLevel;
        private static long _Gold;
        private static long _GoldTorpedo; 
        private static long _DragonCrystal;
        private static long _Diamond;
        private static long _Jiangquan;
        private static long _BankGold;
        private static string _StrHeadUrl;
        public static int _PaoLevel;
        public static int _next_bei;


        public static int LastLottery;
        
        //技能
        public static int _FastCount;
        public static int _IceCount; 
        public static int _LockCount;
        public static int _SummonCount;
        //初始化70 代表基础炮台 
        public static int _PaoViewIndex=70;
        //初始化80 代表没装
        public static int _WingIndex=80; 
        public static bool Shijiao;            
        public static bool firstoperating;

        //玩家账号认证等
        public static string AccountPhone;
        public static string PhoneNum;
        public static bool isShengFenZheng;
        public static bool isZhengSongPwd;
        public static bool isShiMingZheng; 
        public static bool isWxSheZhiNickName;

        //购买的礼包
        public static bool DailyBag;
        public static bool GoldCard;
        public static bool byGetSpecialPackage;
        public static bool SpecialPackage;
        //活动
        public static long Bangding;
        public static string BangdingName;
        public static long rewordNum;
        public static int dailiLevel;
        public static int oceanAgent;
        //部落
        public static long BuluoID;
        public static bool isJoinBuluo;
        public static List<long> Jion_BuluoList=new List<long>();
        //主控制  只用它控制自动开火
        public static bool _RootbZiDong;  
        public static bool _bZiDong; //自动
        public static bool _bSuoDing;  //锁定 ：有锁定必定自动
        public static int Changc;//场次

        public static int canfireLittleGame;

        //是否打开的商城 
        public static bool _isOpenShop=false;

        public static List<FunctionStateProto> OpenCloseStates;
        public static List<FightInfoMessage> LianJiMessage;
        public static void SetRootbZiDong(bool state) {
            if (state)
            {
                _RootbZiDong = true;
            }
            else
            {
                int m = 0;
                if (_bZiDong)
                {
                    m++;
                }
                if (_bSuoDing)
                {
                    m++;
                }
                if (m == 0)
                {
                    _RootbZiDong = false;
                }
            }
         
        }
        public static int sex { get; set; }//性别 1男2女
        /// <summary>
        /// 玩家ID
        /// </summary>
        public static long PlayerId
        {
            get { return _PlayerId; }
            set
            {
                _PlayerId = value;
            }
        }
        /// <summary>
        /// 昵称
        /// </summary>
        public static string NickName
        {
            get { return _NickName; }
            set
            {
                _NickName = value;
                EventManager.NackNameUpdate?.Invoke(_NickName);
            }
        }
        /// <summary>
        /// 用户名
        /// </summary>
        public static string UserName
        {
            get { return _UserName; }
            set
            {
                _UserName = value;
                EventManager.ChangePlayerName?.Invoke(_UserName);                          
            }
        }
        public static long PlayerLevel
        {
            get { return _PlayerLevel; }
            set
            {
                _PlayerLevel = value;
                EventManager.PlayerLevelUpdate?.Invoke(_PlayerLevel);
            }
        }
        public static int vipLevel
        {
            get { return _vipLevel; }
            set
            {
                _vipLevel = value;
                EventManager.ChangeVipLevel?.Invoke(_vipLevel);
            }
        }
        /// <summary>
        /// 金币
        /// </summary>
        public static long Gold
        {
            get { return _Gold; }
            set
            {
                _Gold = value;
                EventManager.GoldUpdate?.Invoke(_Gold);
            }
        }
        /// <summary>
        /// 龙晶
        /// </summary>
        public static long DragonCrystal
        {
            get {
                return _DragonCrystal; 
            }
            set
            {
                _DragonCrystal = value;
                EventManager.DragonCrystalUpdate?.Invoke(_DragonCrystal); 
            }
        }
        /// <summary>
        /// 鱼雷
        /// </summary>
        public static long GoldTorpedo
        {
            get { return _GoldTorpedo; } 
            set
            {
                _GoldTorpedo = value;
                EventManager.GoldTorpedoUpdate?.Invoke(_GoldTorpedo);
            }
        }
        /// <summary>
        /// 钻石
        /// </summary>
        public static long Diamond
        {
            get { return _Diamond; }
            set
            {
                _Diamond = value;
                EventManager.DiamondUpdate?.Invoke(_Diamond);
            }
        }
        /// <summary>
        /// 翅膀ID
        /// </summary>
        public static int WingIndex
        {
            get { return _WingIndex; }
            set
            {
                _WingIndex = value;
                EventManager.WingIndexUpdate?.Invoke(_WingIndex);
            }  
        }
        /// <summary>
        /// 炮台ID 以服务器为准 变自己的
        /// </summary>
        public static int PaoViewIndex
        {
            get { return _PaoViewIndex; }
            set
            {
                _PaoViewIndex = value;
                EventManager.PaoIndexUpdate?.Invoke(_PaoViewIndex);
            }
        }
        
        /// <summary>
        /// 保险箱金币
        /// </summary>
        public static long BankGold {

            get { return _BankGold; } 
            set
            {
                _BankGold = value;
                EventManager.BankLjUpdate?.Invoke(_BankGold);
            }
        }
        /// <summary>
        /// 奖券
        /// </summary>
        public static long Jiangquan
        {
            get { return _Jiangquan; }
            set
            {
                _Jiangquan = value;
                EventManager.JiangquanUpdate?.Invoke(_Jiangquan);
            }
        }
        /// <summary>
        /// 头像下标
        /// </summary>
        private static int _headIndex;

        public static int HeadIndex
        {
            get { return _headIndex; }
            set
            {
                _headIndex = value;
                Debug.Log("_headIndex:" + _headIndex);
                EventManager.intHeadUrlUpdate?.Invoke(_headIndex);
            }
        }
        /// <summary>
        /// 头像字符串
        /// </summary>
        public static string StrHeadUrl
        {
            get { return _StrHeadUrl; }
            set
            {
                _StrHeadUrl = value;
                Debug.Log("_StrHeadUrl:"+ _StrHeadUrl);
                EventManager.StrHeadUrlUpdate?.Invoke(_StrHeadUrl);
            }
        }
        /// <summary>
        /// 炮倍数等级
        /// </summary>
        public static int PaoLevel
        {
            get { return _PaoLevel; }
            set
            {
                _PaoLevel = value;
                EventManager.PaoLevelUpdate?.Invoke(_PaoLevel); 
            }
        }
        public static int next_bei
        {
            get { return _next_bei; }
            set
            {
                _next_bei = value;
            }
        }
        ///// <summary>
        ///// 对所有玩家生效
        ///// </summary>
        ///// <param name="playerID"></param>
        ///// <param name="View"></param>
        //public static void ChangePlayerPaoTai(long playerID, int View)
        //{
        //    var tmp = common3._UIFishingInterface.GetOnePlayer(playerID);
        //    if (tmp != null)
        //    {
        //        tmp.ChangePaoView(View);
        //    }
        //    //common3._UIFishingInterface.GetOnePlayer(playerID).ChangePaoView(View);
        //}
        /// <summary>
        /// 获取自己的头像
        /// </summary>
        /// <param name="callback"></param>
        public static void GetMyHeadImage(Action<Sprite> callback)
        {
            if (StrHeadUrl=="")
            {
                string url = PlayerData.HeadIndex.ToString("00");
                UIHelper.GetHeadImage(url, callback);
            }
            else
            {
                string url = StrHeadUrl;
                UIHelper.GetHeadImage(url, callback);
            }

        }
        //极速技能
        public static int FastCount
        {
            get { return _FastCount; }
            set 
            {
                _FastCount = value;
                EventManager.FastCount?.Invoke(_FastCount);
            }
        }
        //冰冻技能
        public static int IceCount 
        {
            get { return _IceCount; }
            set
            {
                _IceCount = value;
                EventManager.IceCount?.Invoke(_IceCount);
            }
        }
        public static int LockCount 
        {
            get { return _LockCount; }
            set
            {
                _LockCount = value;
                EventManager.LockCount?.Invoke(_LockCount);
            }
        }
        public static int SummonCount
        {
            get { return _SummonCount; }
            set
            {
                _SummonCount = value; 
                EventManager.LockCount?.Invoke(_SummonCount);
            }
        }
        
    }
}