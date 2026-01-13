
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Events;


using System;

//public enum EventKey
//{
//    ChangeGold,
//    ChangeDiamond, 
//    ChangeJiangQuan, 
//    ChangePlayerName,
//    ChangeNackName, 
//    ChangeItem,
//    ChangePao,
//    ChangePaoViewIndex,
//    ChangeLinquLj,
//    VIPInfoLevel,
//    ChangeHeadUrl,
//    ChangeRank,
//}


public static class EventManager
{

    //玩家数据
    public static Action<long> DragonCrystalUpdate; //龙晶
    public static Action<long> DiamondUpdate; //钻石  
    public static Action<long> BankLjUpdate; //保险箱龙晶
    public static Action<long> JiangquanUpdate; //奖券
    public static Action<long> GoldTorpedoUpdate; //鱼雷 
    public static Action<long> GoldUpdate;  //金币

    public static Action<string> NackNameUpdate;//昵称 
    public static Action<string> StrHeadUrlUpdate;  //头像
    public static Action<int> intHeadUrlUpdate;  //头像
    public static Action<int> PaoIndexUpdate; //炮台ID  
    public static Action<int> WingIndexUpdate; //翅膀ID
    public static Action VipUpdate; //vip
    public static Action<long> PlayerLevelUpdate;  //玩家等级
    public static Action ChangeLinquLj; //活动的领取龙晶
    public static Action<int> XiaoNumUpdate;//邮件消息数量更新
    public static Action<float> SoundYinXiaoUpdate;//游戏音效

    public static Action<int> PaoViewIndexUpdate;//炮外观
    public static Action ShopPaoWingUpdate;//翅膀

    public static Action<int> ChangeVipLevel;  //更改玩家昵称
    public static Action PropUpdate;  //道具变更 


    public static Action ChangeRank;//获取玩家连击榜榜排名和奖励返回
    public static Action<bool> GonggaoTips;//公告提示
    public static Action<int> PaoLevelUpdate;//炮倍数等级

    //打开收货地址
    public static Action OpenAddress;

    //道具数变更
    public static Action<long> FastCount;  //急速
    public static Action<long> IceCount;  //冰冻
    public static Action<long> LockCount;  //锁定
    public static Action<long> SummonCount;  //神灯

  
    //使用道具成功更新 只有自己的会更新 float时间
    public static Action<float> UseFast;  //急速
    public static Action<float> UseIce;  //冰冻 
    public static Action<float> UseLock;  //锁定
    public static Action<float> UseFury;  //冰冻 
    public static Action<float> UseSummon;  //神灯
                                            //                                            
    public static Action ClickPaoTai;
    public static Action<bool> IsOnAuto;  //自动  
    //玩家操作 
    public static Action<string> ChangePlayerName;  //更改玩家昵称
    public static Action<Vector2> ChangeAutoTarget; //更改锁定目标
    public static Action<Vector2> DoFire;  //开炮
    public static Action<Vector2> ClickDoFire;  //手动点击开炮 
    public static Action ClickAuto;  //点击自动  
    public static Action SetAddress;  //设置地址
    //public static Action<int> Req_ContinueUseSkill; //继续使用技能
}