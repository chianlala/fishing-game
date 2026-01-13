using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
using Game.UI;
using libx;
using ProtoBuf;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;
using UnityEngine;

namespace Game.UI
{
    /// <summary>
    /// JEngine's UI
    /// </summary>
    public  class common 
    {
        /// <summary>
        /// 如何登录 0未操作  1点击登录 2注册登录 3重连登录 4获取验证码 5找回密码
        /// </summary>
        public static int nLoinWay;

        public static long nXhya; 
        public static long nCaisheng;

        public static bool isReciveWaitMessage=false;
        //未登录
        public static bool IsLoginState = false;

        public static bool IsAlreadyLogin = false;
        //开始Ping
        public static bool isPingStart = false;
        //Ping间隔
        public static float isPingStartTime = 0;
        //登陆
        #region
        public static int LoginState;
        public static int OpenType = 0;
        public static string OpenUsername = "";
        public static string OpenPassw = "";

        public static string Login_Zhanghao = "";
        public static string Login_Mima = "";
        public static int isOpen;
        #endregion
        public static int IsSavePwd;

        //网络
        #region
        public static int token;
        public static bool bWaiting;
        public static float HeartTime;
        public static bool IsOpenUILogin;
        #endregion
        public static string phoneNum;

        public static DateTime dt1_Start;
        public static DateTime dt2_end;

        ////全局Transform位置
        //#region
        //public static Transform TraUIRoot;
        //public static Transform PopUpUICanvas; 
        //public static Transform FixedUICanvas; 
        //public static Transform NormalUICanvas;


        //public static Transform BuyuUICanvas;
        //public static Transform BulletPos;
        //public static Transform base_BG;
        //public static Transform GoldNormal;
        //public static Transform ApperAnimation;
        //public static Transform transICE;
        //public static Transform RootBG;
        //#endregion
        public static Vector2 OldInput;
        public static float OldInputAngle;
        //public static float W=640f;
        //public static float H=360f;
        public static float W;
        public static float H;
        public static bool bAlwals=false;
        //全局所有道具
        public static List<Sprite> AllItemSp=new List<Sprite>();
        //支付
        #region
        public static long wxH5 = 0;
        public static long wxS = 0;
        public static long zfbH5 = 13;
        public static long zfbS = 0;
        #endregion
        //月卡时间
        public static long monthCardOverDate; 
        public static List<FishingFieldInfoProto> fieldInfos;

     
        //不同场次区分ID
        public static int IDRoomFish = 0;
        public static BY_SESSIONNAME bySessionName;  
        //public static Dictionary<long, int> dicBullet = new Dictionary<long, int>();//子弹

        //public static Dictionary<long, PlayerInfo> listPlayer = new Dictionary<long, PlayerInfo>();
        public static Dictionary<long, player> listPlayer = new Dictionary<long, player>();
        /// <summary>
        /// 0低阶龙珠 1中阶龙珠 2高阶龙珠 3锁定 4冰冻 5急速 6暴击 7号角 8分身 9绑定龙珠  10闪电炮 11稀有鱼雷 12绑定稀有鱼雷
        /// </summary>
        public static long[] myItem = new long[13];//自己的道具      
        /// <summary>
        /// 自己ID
        /// </summary>
        public static Dictionary<long, long> myDicItem = new Dictionary<long, long>() { { 7, 0 }, { 8, 0 }, { 9, 0 }, { 11, 0 }, { 38, 0 } };

        public static Dictionary<int, long> myCaiLiao = new Dictionary<int, long>();//自己的材料
        public static float ScreenBiLi;
        public static List<FightInfoMessage> LianJiMessage = new List<FightInfoMessage>();
        public static List<long> dicMoneyConfig = new List<long>() { 0, 100000, 2000000, 5000000 };

        //全局参数 这鱼只能在二次伤害结束 并且金币飞到玩家后 删除  或者自己游走
        //public static Dictionary<long, fishdata> listFishData = new Dictionary<long, fishdata>();
        //场景对象鱼
        public static Dictionary<long, fish> listFish = new Dictionary<long, fish>();
        //----读取的表格
        #region 
        public static Dictionary<int, PaoConfig> dicPaoConfig = new Dictionary<int, PaoConfig>();//炮台解锁配置信息
        public static Dictionary<int, PaoConfig> dicPaoConfigLj = new Dictionary<int, PaoConfig>();//炮台解锁配置信息 
        public static Dictionary<int, PaoRangeConfig> dicPaoFwConfig = new Dictionary<int, PaoRangeConfig>();//炮台解锁配置信息  
        public static Dictionary<long, FishMoveConfig> dicPathConfig = new Dictionary<long, FishMoveConfig>();//鱼游路径配置信息
        public static Dictionary<long, int> dicCfgforging = new Dictionary<long, int>();//炮台锻造时id  
        public static Dictionary<int, ZhuZaoConfig> dicDuanZaoInfoConfig = new Dictionary<int, ZhuZaoConfig>();//炮台锻造
        public static Dictionary<int, VipConfig> dicVipConfig = new Dictionary<int, VipConfig>();//Vip充值配置表
        #endregion
        public static void SendMessage(int cmd, IExtensible msg, bool bShowWait = false)
        {
            //if (cmd != (int)NetworkMsgCode.C_S_HEART_BEAT_REQUEST)
            //    Debug.Log(string.Format("发送消息0x{0:x}({1})", cmd, (com.maple.game.osee.proto.OseeMsgCode)cmd));
            NetMgr.Instance.Socket.SendMsg((int)cmd, msg);
        }
     
        public static string MD5Encrypt(string strText)
        {
            string strRe = "";
            MD5 md5 = new MD5CryptoServiceProvider();
            byte[] result = md5.ComputeHash(System.Text.Encoding.UTF8.GetBytes(strText));
            for (int i = 0; i < result.Length; i++)
            {
                strRe += forDigit((result[i] & 0xF0) >> 4, 16);
                strRe += forDigit((result[i] & 0xF), 16);
            }
            return strRe;
        }
        public static char forDigit(int digit, int radix)
        {
            if (2 <= radix && radix <= 36)
            {
                if (digit >= 0 && digit < radix)
                {
                    return (char)(digit < 10 ? digit + '0' : digit + 'a' - 10);
                }
            }
            return (char)0;
        }
        //旋屏
        public static Transform GetRootPath() 
        {
            if (PlayerData.IsRotateGame)
            {
                return Root3D.Instance.rootPathRo;
            }
            else
            {
                return Root3D.Instance.rootPath;
            }
            
        }
        public static Vector3 WordToUI(Vector3 pos)
        {

            Vector3 viewportPos = Root3D.Instance.cam3D.WorldToViewportPoint(pos);
            Vector3 uiPos = new Vector3(viewportPos.x * common.W, viewportPos.y * common.H, 0);
            Vector3 myUiPos = new Vector3(uiPos.x, uiPos.y, 0);
            return myUiPos;
        }
        public static Vector3 WordToScreenPointUI(Vector3 pos) 
        {
            Vector3 viewportPos = Root3D.Instance.cam3D.WorldToScreenPoint(pos);
            return viewportPos;
        }
        public static Vector3 UIToScreenPointUI(Vector3 pos)
        {
            Vector2 viewportPos = RectTransformUtility.WorldToScreenPoint(Root3D.Instance.UICamera, pos);
            return viewportPos;
        }
        public static void GetMyHeadImage(Action<Sprite> callback)
        {
            string url = "";
            url = PlayerData.StrHeadUrl;
            UIHelper.GetHeadImage(url, callback);
        }
   
    }
    
    public class VipConfig
    {
        public int level;
        public long money;//需充值多少钱
        public string strInfo;//特权说明
        public string strAward;//特权奖励
    }
   
    public class FishMoveConfig
    {
        public long id;
        public int pathId;
        public float time;
        public string strAnimal;
        public string strStop;
    }
    /// <summary>
    /// 炮台
    /// </summary>
    public class ZhuZaoConfig
    {
        public int name;//使用场景 
        public string Info = "";//info     
    }
    /// <summary>
    /// 客户端桌面玩家数据
    /// </summary>
    public class PlayerInfo
    {
        public long playerId;
        public int seat;//本地座位号
        public string name;
        public long money;//龙晶场 这个money就为龙晶
        public long lottery; //奖券
        public int sex;
        public string url;
        public string diamond;
        
        //public long dragonScale;
        public bool isOnline;
        public int nMinLevel;
        public int nMaxLevel;
        public int nRoleLevel;//玩家等级
        public int nHeadIndex;
        public bool isRobot = false;//是否是机器人
    }
    /// <summary>
    /// 炮台配置文件
    /// </summary>
    public class PaoConfig
    {
        public int nModule;//使用场景
        public int nPrevLevel = 0;//上一等级
        public int nNextLevel = 9999;//下一等级
        public int nLevel = 0;
    }

    public class PaoRangeConfig
    {
        public int Minlevel;
        public int Maxlevel;
    }
    public class FishConfig
    {
        public long monsterId;
        //public int modelId;//模型id
        public string name;//鱼名称
        public int money;//金币倍数
        public int maxMoney;//金币倍数 
        public int skill;//技能 
        public int fishType;//鱼类型 100为Boos  
        public int scene;//场次类型 
    }
    public class FishJson 
    {
        public int modelID;  
        public int BgID;
        public bool isSmall;
        public bool isBig;
        public bool isBoss;
        public bool xiaoboss; 
        public bool isTwoAttack;
        public bool isSkill;
        public bool isGold;
       
        public string info; 
    }
    public class fishdata 
    {
        //鱼id
        public long id;
        public long fishId;
        public long routeId;
        public float clientLifeTime;
        public long createTime;
        public FishJson fishJson;
    }
     
    public class PaoWinJson 
    {
        public int modelID;
        public string name;          
        public string info;
    }

    public class BagItemJson
    { 
        public string name;
        public string info;
    }
    //public class FishModleConfig
    //{
    //    public int Id;  
    //    public string name;//鱼名称
    //    public int modelId;//模型id
    //    public int fishType;//鱼类型 100为Boos  
    //}
}