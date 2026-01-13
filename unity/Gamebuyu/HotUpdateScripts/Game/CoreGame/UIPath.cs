using CoreGame;
using JEngine.Core;
using UnityEngine;

public enum UIType
{
    PopUpUICanvas = 0,
    FixedUICanvas = 1,
    NormalUICanvas = 2,
    BuyuUICanvas = 3,
    TwoAttackCanvas=4
}
public struct UIContext
{
    public UIContext(string varname, UIType ut)
    {
        name = varname;
        uiType = ut;
    }
    public string name;
    public UIType uiType;
}
public abstract class UIPath
{
    //-----公用
    public static readonly string MessageBox = "";

    public static readonly UIContext UILoadingGame = new UIContext("UILoadingGame", UIType.PopUpUICanvas);//600
    public static readonly UIContext UIExitGame = new UIContext("UIExitGame", UIType.PopUpUICanvas);//600
    
    public static readonly UIContext UIMessageItemBox = new UIContext("UIMessageItemBox", UIType.PopUpUICanvas);//480
    public static readonly UIContext UIPopOneMsg = new UIContext("Pop/UIPopOneMsg", UIType.PopUpUICanvas);//504
    public static readonly UIContext UIPopMessage = new UIContext("Pop/UIPopMessage", UIType.PopUpUICanvas);//503
    public static readonly UIContext UIMessageBoxEx = new UIContext("Pop/UIMessageBoxEx", UIType.PopUpUICanvas);//501
    public static readonly UIContext UIMessageBox = new UIContext("Pop/UIMessageBox", UIType.PopUpUICanvas);//502
    public static readonly UIContext UIMessageNetBox = new UIContext("Pop/UIMessageNetBox", UIType.PopUpUICanvas);//502

    public static readonly UIContext UIShop = new UIContext("UIShop", UIType.PopUpUICanvas); //400 提示必须在shop之上
    public static readonly UIContext UIUserInfo = new UIContext("UIUserInfo", UIType.PopUpUICanvas);//600
    public static readonly UIContext UILogin = new UIContext("UILogin", UIType.FixedUICanvas);
    public static readonly UIContext UIVipInfo = new UIContext("UIVipInfo", UIType.FixedUICanvas);
    public static readonly UIContext UIBag = new UIContext("UIBag", UIType.FixedUICanvas);

    public static readonly UIContext UIDragonScale = new UIContext("UIDragonScale", UIType.FixedUICanvas);
    public static readonly UIContext UIDuiHuanJq = new UIContext("UIDuiHuanJq", UIType.FixedUICanvas);
    public static readonly UIContext UISetAccount = new UIContext("UISetAccount", UIType.FixedUICanvas); 
    public static readonly UIContext UIHitPig = new UIContext("UIHitPig", UIType.NormalUICanvas);

    public static readonly UIContext UIShowGold = new UIContext("UIShowGold", UIType.FixedUICanvas);
    public static readonly UIContext UIYueKa = new UIContext("UIYueKa", UIType.FixedUICanvas);
    public static readonly UIContext UIAccount = new UIContext("UIAccount", UIType.FixedUICanvas);
    public static readonly UIContext UIBigAwardOver = new UIContext("UIBigAwardOver", UIType.FixedUICanvas); 
    public static readonly UIContext UI_ViewXuke = new UIContext("UI_ViewXuke", UIType.FixedUICanvas);
    public static readonly UIContext UIRenzheng = new UIContext("UIRenzheng", UIType.FixedUICanvas);

    public static readonly UIContext UISpecialPackage = new UIContext("UISpecialPackage", UIType.FixedUICanvas);

    public static readonly UIContext UIGoldCard = new UIContext("UIGoldCard", UIType.FixedUICanvas);
    public static readonly UIContext UIYuzhong = new UIContext("UIYuzhong", UIType.FixedUICanvas);
    public static readonly UIContext UIKefu = new UIContext("UIKefu", UIType.FixedUICanvas);
    public static readonly UIContext UIGuangbo = new UIContext("UIGuangbo", UIType.FixedUICanvas);

    public static readonly UIContext UIFishInfo = new UIContext("Info/UIFishInfo", UIType.FixedUICanvas);
    public static readonly UIContext UISkillInfo = new UIContext("Info/UISkillInfo", UIType.FixedUICanvas);
    public static readonly UIContext UISpecialInfo = new UIContext("Info/UISpecialInfo", UIType.FixedUICanvas);

    public static readonly UIContext UIDaySpecialPackage = new UIContext("UIDaySpecialPackage", UIType.FixedUICanvas);
    public static readonly UIContext UILastWeekRank = new UIContext("UILastWeekRank", UIType.FixedUICanvas);
    public static readonly UIContext UIBuLuo = new UIContext("UIBuLuo", UIType.FixedUICanvas);
    public static readonly UIContext UIBuLuoWai = new UIContext("UIBuLuoWai", UIType.FixedUICanvas);
    public static readonly UIContext UIXinYunShiKe = new UIContext("UIXinYunShiKe", UIType.FixedUICanvas);

    
    public static readonly UIContext UISetting = new UIContext("UISetting", UIType.FixedUICanvas);
    
    public static readonly UIContext UISettingFish = new UIContext("UISettingFish", UIType.FixedUICanvas); 

    public static readonly UIContext UIHelp = new UIContext("UIHelp", UIType.FixedUICanvas);
    public static readonly UIContext UIDuihuan = new UIContext("UIDuihuan", UIType.FixedUICanvas);
    public static readonly UIContext UIGonggao = new UIContext("UIGonggao", UIType.FixedUICanvas);
    public static readonly UIContext UIDailyBag = new UIContext("UIDailyBag", UIType.FixedUICanvas);
    
    public static readonly UIContext UIBaoxianxiang = new UIContext("UIBaoxianxiang", UIType.FixedUICanvas);
    public static readonly UIContext UI_HuoDong = new UIContext("UI_HuoDong", UIType.FixedUICanvas);
    public static readonly UIContext UIZhuanpan = new UIContext("UIZhuanpan", UIType.FixedUICanvas);
    public static readonly UIContext UIRank = new UIContext("Rank/UIRank", UIType.FixedUICanvas);
    public static readonly UIContext UIZhanJi = new UIContext("Rank/UIZhanJi", UIType.FixedUICanvas); 

    public static readonly UIContext UIQuest = new UIContext("UIQuest", UIType.FixedUICanvas);
    public static readonly UIContext UIQianDao = new UIContext("UIQianDao", UIType.FixedUICanvas); 
    public static readonly UIContext UIXiaoxi = new UIContext("UIXiaoxi", UIType.FixedUICanvas);
    public static readonly UIContext UIFirstcharge = new UIContext("UIFirstcharge", UIType.FixedUICanvas);
    //public static readonly UIContext UI_BangDing = new UIContext("UI_BangDing", UIType.FixedUICanvas);
    public static readonly UIContext UI_Tuiguang = new UIContext("UI_Tuiguang", UIType.FixedUICanvas);



    public static readonly UIContext UIGift = new UIContext("UIGift", UIType.FixedUICanvas);
    public static readonly UIContext UICommunication = new UIContext("UICommunication", UIType.FixedUICanvas);
    public static readonly UIContext UIGameRule = new UIContext("UIGameRule", UIType.FixedUICanvas);
    public static readonly UIContext UIUpLevel = new UIContext("UIUpLevel", UIType.FixedUICanvas);
    public static readonly UIContext UIChangeSeat = new UIContext("UIChangeSeat", UIType.FixedUICanvas);
    public static readonly UIContext UIChangeSeatLJ = new UIContext("UIChangeSeatLJ", UIType.FixedUICanvas);
    
    public static readonly UIContext UIMainMenu = new UIContext("UIMainMenu", UIType.NormalUICanvas);
    public static readonly UIContext UIBuyuMenu = new UIContext("UIBuyuMenu", UIType.NormalUICanvas);

    public static readonly UIContext UIBOOSMoney = new UIContext("UIBOOSMoney", UIType.NormalUICanvas);
    public static readonly UIContext UIChangeHuan = new UIContext("UIChangeHuan", UIType.NormalUICanvas);
    public static readonly UIContext UIBaoFu = new UIContext("UIBaoFu", UIType.NormalUICanvas);
    public static readonly UIContext UIJiShaBeiShu = new UIContext("UIJiShaBeiShu", UIType.NormalUICanvas);
    public static readonly UIContext UIShowHhjj = new UIContext("UIShowHhjj", UIType.NormalUICanvas);
    public static readonly UIContext UILottery = new UIContext("UILottery", UIType.NormalUICanvas);

    public static readonly UIContext UIByRoomMain = new UIContext("UIByRoomMain", UIType.BuyuUICanvas);
    public static readonly UIContext UIByChange = new UIContext("UIByChange", UIType.BuyuUICanvas);
    public static readonly UIContext UIChangePaoTai = new UIContext("UIChangePaoTai", UIType.FixedUICanvas);
    public static readonly UIContext UIResetPasswd = new UIContext("UIResetPasswd", UIType.FixedUICanvas);

   
    public static readonly UIContext UICqzsTA = new UIContext("TwoAttack/UICqzsTA", UIType.BuyuUICanvas); 

  
}

