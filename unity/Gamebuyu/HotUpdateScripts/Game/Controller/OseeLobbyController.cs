using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;
using GameFramework;
using NetLib;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class OseeLobbyController : BaseController
{
    public OseeLobbyController()
    {
        Register((int)OseeMsgCode.S_C_OSEE_PLAYER_MONEY_RESPONSE, typeof(PlayerMoneyResponse), On_PlayerMoneyResponse);//获取玩家货币返回
        Register((int)OseeMsgCode.S_C_OSEE_VIP_LEVEL_RESPONSE, typeof(VipLevelResponse), On_VipLevelResponse);//获取玩家vip等级返回
        Register((int)OseeMsgCode.S_C_OSEE_NEXT_LOTTERY_DRAW_FEE_RESPONSE, typeof(NextLotteryDrawFeeResponse), On_NextLotteryDrawFeeResponse);//获取下次抽奖费用返回
        Register((int)OseeMsgCode.S_C_OSEE_LOTTERY_DRAW_RESPONSE, typeof(LotteryDrawResponse), On_LotteryDrawResponse);//转盘抽奖返回
        Register((int)OseeMsgCode.S_C_TURN_TABLE_ALL_RESPONSE, typeof(TurnTableAllResponse), On_TurnTableAllResponse);//转盘中奖全部记录返回
        Register((int)OseeMsgCode.S_C_TURN_TABLE_USER_RESPONSE, typeof(TurnTableUserResponse), On_TurnTableUserResponse);//转盘中奖用户记录返回
        Register((int)OseeMsgCode.S_C_OSEE_LOTTERY_INFO_RESPONSE, typeof(LotteryInfoResponse), On_LotteryInfoResponse);//转盘中奖用户记录返回s
        

        Register((int)OseeMsgCode.S_C_OSEE_SIGNED_TIMES_RESPONSE, typeof(SignedTimesResponse), On_SignedTimesResponse);//获取已签到次数返回

        Register((int)OseeMsgCode.S_C_OSEE_CHECK_BANK_PASSWORD_RESPONSE, typeof(CheckBankPasswordResponse), On_CheckBankPasswordResponse);//检查保险箱密码返回
        Register((int)OseeMsgCode.S_C_OSEE_SAVE_MONEY_RESPONSE, typeof(SaveMoneyResponse), On_SaveMoneyResponse);//存取金币返回
        Register((int)OseeMsgCode.S_C_OSEE_CHANGE_BANK_PASSWORD_RESPONSE, typeof(ChangeBankPasswordResponse), On_ChangeBankPasswordResponse);//修改保险箱密码返回

        Register((int)OseeMsgCode.S_C_OSEE_GET_RANKING_LIST_RESPONSE, typeof(GetRankingListResponse), On_GetRankingListResponse);//获取排行榜数据请求

        Register((int)OseeMsgCode.S_C_OSEE_GET_LOTTERY_SHOP_LIST_RESPONSE, typeof(GetLotteryShopListResponse), On_GetLotteryShopListResponse);//获取奖券商品列表返回
        Register((int)OseeMsgCode.S_C_OSEE_BUY_SHOP_ITEM_RESPONSE, typeof(BuyShopItemResponse), On_BuyShopItemResponse);//购买商城商品返回
        Register((int)OseeMsgCode.S_C_OSEE_SERVICE_WECHAT_RESPONSE, typeof(ServiceWechatResponse), On_ServiceWechatResponse);//客服微信返回
        Register((int)OseeMsgCode.S_C_OSEE_NOTICE_LIST_RESPONSE, typeof(NoticeListResponse), On_NoticeListResponse);//公告列表返回
        Register((int)OseeMsgCode.S_C_OSEE_USE_CDK_RESPONSE, typeof(UseCdkResponse), On_UseCdkResponse);//使用cdk返回
        Register((int)OseeMsgCode.S_C_OSEE_AUTHENTICATE_INFO_RESPONSE, typeof(AuthenticateInfoResponse), On_AuthenticateInfoResponse);//实名认证信息返回
        Register((int)OseeMsgCode.S_C_OSEE_AUTHENTICATE_PHONE_CHECK_RESPONSE, typeof(AuthenticatePhoneCheckResponse), On_AuthenticatePhoneCheckResponse);//实名认证手机验证请求
        Register((int)OseeMsgCode.S_C_OSEE_SUBMIT_AUTHENTICATE_RESPONSE, typeof(SubmitAuthenticateResponse), On_SubmitAuthenticateResponse);//提交实名认证信息返回

        Register((int)OseeMsgCode.S_C_OSEE_GET_RESET_PASSWORD_PHONE_NUM_RESPONSE, typeof(GetResetPasswordPhoneNumResponse), On_GetResetPasswordPhoneNumResponse);//获取重置密码手机号返回
        Register((int)OseeMsgCode.S_C_OSEE_RESET_PASSWORD_PHONE_CHECK_RESPONSE, typeof(ResetPasswordPhoneCheckResponse), On_ResetPasswordPhoneCheckResponse);//重置用户密码手机验证返回
        Register((int)OseeMsgCode.S_C_OSEE_RESET_PASSWORD_RESPONSE, typeof(ResetPasswordResponse), On_ResetPasswordResponse);//重置密码返回
        Register((int)OseeMsgCode.S_C_OSEE_WECHAT_SHARE_RESPONSE, typeof(WechatShareResponse), On_WechatShareResponse);//微信分享返回

        Register((int)OseeMsgCode.S_C_TTMY_ONCE_BAG_BUY_INFO_RESPONSE, typeof(OnceBagBuyInfoResponse), On_OnceBagBuyInfoResponse);//限次礼包购买信息响应
        Register((int)OseeMsgCode.S_C_TTMY_ONCE_BAG_BUY_SUCCESS_RESPONSE, typeof(OnceBagBuySuccessResponse), On_OnceBagBuySuccessResponse);//限次礼包购买成功响应
        Register((int)OseeMsgCode.S_C_TTMY_MONEY_CARD_BUY_INFO_RESPONSE, typeof(MoneyCardBuyInfoResponse), On_MoneyCardBuyInfoResponse);//金币卡购买信息响应
        Register((int)OseeMsgCode.S_C_TTMY_MONEY_CARD_BUY_SUCCESS_RESPONSE, typeof(MoneyCardBuySuccessResponse), On_MoneyCardBuySuccessResponse);//购买金币卡成功响应
        Register((int)OseeMsgCode.S_C_TTMY_RECEIVE_MONEY_RESPONSE, typeof(ReceiveMoneyResponse), On_ReceiveMoneyResponse);//领取金币卡金币响应

        Register((int)OseeMsgCode.S_C_TTMY_BUY_BATTERY_LEVEL_RESPONSE, typeof(BuyBatteryLevelResponse), On_BuyBatteryLevelResponse);//炮台直升响应

        Register((int)OseeMsgCode.S_C_FEED_BACK_RESPONSE, typeof(FeedBackResponse), On_FeedBackResponse);//添加用户反馈响应

        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_PROP_ONE_RESPONSE, typeof(PlayerPropOneResponse), On_PlayerPropOneResponse);//玩家单个道具信息返回


    }

    /// <summary>
    /// 微信分享返回
    /// <summary>
    private void On_WechatShareResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<WechatShareResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.WechatShareResponse, this, pack);
    }
    /// <summary>
    /// 获取重置密码手机号返回
    /// <summary>
    private void On_GetResetPasswordPhoneNumResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetResetPasswordPhoneNumResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetResetPasswordPhoneNumResponse, this, pack);
    }
    /// <summary>
    /// 重置用户密码手机验证返回
    /// <summary>
    private void On_ResetPasswordPhoneCheckResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ResetPasswordPhoneCheckResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ResetPasswordPhoneCheckResponse, this, pack);
    }
    /// <summary>
    /// 重置密码返回
    /// <summary>
    private void On_ResetPasswordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ResetPasswordResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ResetPasswordResponse, this, pack);
    }


    /// <summary>
    /// 提交实名认证信息返回
    /// <summary>
    private void On_SubmitAuthenticateResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<SubmitAuthenticateResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.SubmitAuthenticateResponse, this, pack);
    }

    /// <summary>
    /// 实名认证手机验证请求
    /// <summary>
    private void On_AuthenticatePhoneCheckResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<AuthenticatePhoneCheckResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.AuthenticatePhoneCheckResponse, this, pack);
    }

    /// <summary>
    /// 获取奖券商品列表返回
    /// <summary>
    private void On_GetLotteryShopListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetLotteryShopListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetLotteryShopListResponse, this, pack);
    }
    /// <summary>
    /// 购买商城商品返回
    /// <summary>
    private void On_BuyShopItemResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BuyShopItemResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.BuyShopItemResponse, this, pack);
    }
    /// <summary>
    /// 客服微信返回
    /// <summary>
    private void On_ServiceWechatResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ServiceWechatResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ServiceWechatResponse, this, pack);
    }
    /// <summary>
    /// 公告列表返回
    /// <summary>
    private void On_NoticeListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<NoticeListResponse>();

        string noticeStr = PlayerData.PlayerId.ToString();
        for (int j = 0; j < pack.notice.Count; j++)
        {
            noticeStr += common.MD5Encrypt(pack.notice[j].title + pack.notice[j].content);
        }
        string nMD5 = common.MD5Encrypt(noticeStr);
        string varstr = PlayerPrefs.GetString("gonggao");
        bool hasNew = false;
        if ((varstr == null || varstr == "") && pack.notice.Count>0)
        {
            hasNew = true;
        }
        else if(varstr != nMD5)
        {
            hasNew = true;
        }

        //if (hasNew)
        //{
        //    if (UIMainMenu.instance != null && UIMainMenu.instance.gameObject.activeSelf)
        //    {
        //        UIMainMenu.instance.img_ggtips.gameObject.SetActive(true);                
        //    }         
        //}
        //else
        //{
        //    if ( UIMainMenu.instance != null && UIMainMenu.instance.gameObject.activeSelf)
        //    {
        //        UIMainMenu.instance.img_ggtips.gameObject.SetActive(false);
        //    }
        //}
        
        UEventDispatcher.Instance.DispatchEvent(UEventName.NoticeListResponse, this, pack);
    }
    /// <summary>
    /// 使用cdk返回
    /// <summary>
    private void On_UseCdkResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseCdkResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseCdkResponse, this, pack);
    }
    /// <summary>
    /// 实名认证信息返回
    /// <summary>
    private void On_AuthenticateInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<AuthenticateInfoResponse>();
        if (pack.realName == "")//没认证
        {
           PlayerData.isShiMingZheng = false;
        }
        else//已认证
        {
            PlayerData.isShiMingZheng = true;
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.AuthenticateInfoResponse, this, pack);
    }


    /// <summary>
    /// 获取排行榜数据请求
    /// <summary>
    private void On_GetRankingListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetRankingListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetRankingListResponse, this, pack);
    }

    ///<summary>
    /// 检查保险箱密码返回
    /// <summary>
    private void On_CheckBankPasswordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<CheckBankPasswordResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.CheckBankPasswordResponse, this, pack);
    }
    /// <summary>
    /// 存取金币返回
    /// <summary>
    private void On_SaveMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<SaveMoneyResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.SaveMoneyResponse, this, pack);
    }
    /// <summary>
    /// 修改保险箱密码返回
    /// <summary>
    private void On_ChangeBankPasswordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChangeBankPasswordResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChangeBankPasswordResponse, this, pack);
    }


    /// <summary>
    /// 获取玩家货币返回
    /// <summary>
    private void On_PlayerMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerMoneyResponse>();
        PlayerData.Gold = pack.money;
        PlayerData.Jiangquan = pack.lottery;
        PlayerData.Diamond = pack.diamond;
        //PlayerData.BankGold = pack.bankMoney;
        PlayerData.DragonCrystal = pack.dragonCrystal;
      
        if (pack.isFirstJoin == 1)
        {
            NetMessage.Login.Req_FirstJoinRequest(PlayerData.PlayerId);
            //新手指引
            //common.firstLogin = pack.isFirstJoin;
            //UIMgr.ShowUI(UIPath.NewbiePrompt);
        }              
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerMoneyResponse, this, pack);
    }
    /// <summary>
    /// 获取玩家vip等级返回
    /// <summary>
    private void On_VipLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<VipLevelResponse>();

    

        PlayerData.vipLevel = pack.vipLevel;        
        PlayerData.vipNextMoney = pack.nextLevel;
        PlayerData.vipTotalMoney = pack.totalMoney;
        EventManager.VipUpdate?.Invoke();
        //if (UIShop.instance!=null&& UIShop.instance.gameObject.activeSelf)
        //{          
        //    UIShop.instance.Setprocess();
        //}
        //UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>().SetAddress();



        UEventDispatcher.Instance.DispatchEvent(UEventName.VipLevelResponse, this, pack);
    }
    /// <summary>
    /// 获取下次抽奖费用返回
    /// <summary>
    private void On_NextLotteryDrawFeeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<NextLotteryDrawFeeResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.NextLotteryDrawFeeResponse, this, pack);
    }
    /// <summary>
    /// 转盘抽奖返回
    /// <summary>
    private void On_LotteryDrawResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<LotteryDrawResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.LotteryDrawResponse, this, pack);
    }
    /// <summary>
    /// 转盘中奖全部记录返回
    /// <summary>
    private void On_TurnTableAllResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<TurnTableAllResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.TurnTableAllResponse, this, pack);
    }
    /// <summary>
    /// 转盘中奖用户记录返回
    /// <summary>
    private void On_TurnTableUserResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<TurnTableUserResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.TurnTableUserResponse, this, pack);
    }
    private void On_LotteryInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<TurnTableUserResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.LotteryInfoResponse, this, pack);
    }
     
    /// <summary>
    /// 获取已签到次数返回
    /// <summary>
    private void On_SignedTimesResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<SignedTimesResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.SignedTimesResponse, this, pack);
        UEventDispatcher.Instance.DispatchEvent(UEventName.SignedTimesResponseMenu, this, pack.signed);
    }

    /// <summary>
    /// 限次礼包购买信息响应
    /// <summary>
    private void On_OnceBagBuyInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<OnceBagBuyInfoResponse>();
        //if (PlayerData.SpecialPackage)
        //{
        //    PlayerData.SpecialPackage = false;
        //    //for (int i = 0; i < pack.buyItems.Count; i++)
        //    //{
        //    //    if (pack.buyItems[i] == 0)
        //    //    {
        //    //        //没购买
        //    //        UIMgr.ShowUI(UIPath.UISpecialPackage);
        //    //        return;
        //    //    }
        //    //}
        //    if (pack.buyItems.Count <18)
        //    {
        //        //没购买
        //        UIMgr.ShowUI(UIPath.UISpecialPackage);
        //        return;
        //    }
        //    //全买了
        //    //common.DailyBag = true;
        //    ////NetMessage.OseeLobby.Req_DailyBagBuyInfoRequest();
        //    //NetMessage.OseeLobby.Req_DailyBuyGiftInfoRequest();
        //    PlayerData.GoldCard = true;
        //    NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
        //}
        //if (PlayerData.byGetSpecialPackage)
        //{
        //    if (pack.buyItems.Count >= 30)
        //    {
        //        UIMgr.CloseUI(UIPath.UISpecialPackage);
        //        //全买了
        //        //common.DailyBag = true;
        //        ////NetMessage.OseeLobby.Req_DailyBagBuyInfoRequest();
        //        //NetMessage.OseeLobby.Req_DailyBuyGiftInfoRequest();

        //        PlayerData.GoldCard = true;
        //        NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
        //    }           
        //}                
        UEventDispatcher.Instance.DispatchEvent(UEventName.OnceBagBuyInfoResponse, this, pack);
    }
    /// <summary>
    /// 限次礼包购买成功响应
    /// <summary>
    private void On_OnceBagBuySuccessResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<OnceBagBuySuccessResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.OnceBagBuySuccessResponse, this, pack);
    }
    /// <summary>
    /// 金币卡购买信息响应
    /// <summary>
    private void On_MoneyCardBuyInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<MoneyCardBuyInfoResponse>();
        if (PlayerData.GoldCard)
        {
            PlayerData.GoldCard = false;
            if (pack.bought==true)
            {
                UIMgr.ShowUISynchronize(UIPath.UIShop);
            }
            else
            {
                UIMgr.ShowUI(UIPath.UIGoldCard);
            }
        }
        if (PlayerData.byGetSpecialPackage)
        {
            if (pack.bought == true)
            {
                UIMgr.CloseUI(UIPath.UIGoldCard);
                UIMgr.ShowUISynchronize(UIPath.UIShop);
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.MoneyCardBuyInfoResponse, this, pack);
    }
    /// <summary>
    /// 购买金币卡成功响应
    /// <summary>
    private void On_MoneyCardBuySuccessResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<MoneyCardBuySuccessResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.MoneyCardBuySuccessResponse, this, pack);
    }
    /// <summary>
    /// 领取金币卡金币响应
    /// <summary>
    private void On_ReceiveMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ReceiveMoneyResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ReceiveMoneyResponse, this, pack);
    }
    /// <summary>
    /// 炮台直升响应
    /// <summary>
    private void On_BuyBatteryLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BuyBatteryLevelResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.BuyBatteryLevelResponse, this, pack);
    }
    /// <summary>
    /// 添加用户反馈响应
    /// <summary>
    private void On_FeedBackResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FeedBackResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FeedBackResponse, this, pack);
    }

    /// <summary>
    /// 玩家单个道具信息返回
    /// <summary>
    private void On_PlayerPropOneResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerPropOneResponse>();
        if (common.myDicItem.ContainsKey(pack.itemId))
        {
            //using (zstring.Block())
            //{
            //    //(zstring.Format("fishState.routeId错误路径：{0}", fishState.routeId));
            //    Debug.LogError(zstring.Format("玩家单个道具信息返回:{0} {1}", pack.itemId,pack.itemNum));
            //}
            common.myDicItem[pack.itemId] = pack.itemNum;
        }
     
        if (pack.itemId == 1)
        {
            PlayerData.Gold = pack.itemNum;
        }
        else if (pack.itemId == 2)//银行金币
        {

        }
        else if (pack.itemId == 3)//奖券
        {
            PlayerData.Jiangquan = pack.itemNum;
        }
        else if (pack.itemId == 4)//钻石
        {
            PlayerData.Diamond = pack.itemNum;
        }
        else if (pack.itemId == 5)//核弹
        {
            common.myItem[0] = pack.itemNum;
        }
        else if (pack.itemId == 6)//核弹
        {
            common.myItem[1] = pack.itemNum;
            //if (UIDragonScale.instance != null && UIDragonScale.instance.gameObject.activeSelf)
            //{
            //    UIDragonScale.instance.ChangeMyNum();
            //}
        }
        else if (pack.itemId == 7)//黄金核弹
        {
            common.myItem[2] = pack.itemNum;
            PlayerData.GoldTorpedo = pack.itemNum;
        }
        else if (pack.itemId == 8)//锁定技能
        {
            common.myItem[3] = pack.itemNum;
        }
        else if (pack.itemId == 9)//冰冻技能
        {
            common.myItem[4] = pack.itemNum;
        }
        else if (pack.itemId == 10)//急速技能
        {
            common.myItem[5] = pack.itemNum;
        }
        else if (pack.itemId == 11)//暴击技能
        {
            common.myItem[6] = pack.itemNum;
        }
        else if (pack.itemId == 12)//月卡
        {
            common.monthCardOverDate = pack.itemNum;                 
        }
        else if (pack.itemId == 13)//BOSS号角
        {
            common.myItem[7] = pack.itemNum;
        }
        else if (pack.itemId == 14)//骑士之誓炮台外观
        {
            common.myCaiLiao[14] = pack.itemNum;
        }
        else if (pack.itemId == 15)//冰龙怒吼炮台外观
        {
            common.myCaiLiao[15] = pack.itemNum;
        }
        else if (pack.itemId == 16)//莲花童子炮台外观
        {
            common.myCaiLiao[16] = pack.itemNum;
        }
        else if (pack.itemId == 17)//死亡火炮炮台外观
        {
            common.myCaiLiao[17] = pack.itemNum;
        }
        else if (pack.itemId == 18)//龙晶
        {
            PlayerData.DragonCrystal = pack.itemNum;
        }
        else if (pack.itemId == 19)//分身炮
        {
            common.myItem[8] = pack.itemNum;
        }
        else if (pack.itemId == 20)//绑定黄金核弹
        {
            common.myItem[9] = pack.itemNum;
        }
        else if (pack.itemId == 21)//稀有核弹
        {
            common.myItem[11] = pack.itemNum;
        }
        else if (pack.itemId == 22)//绑定稀有核弹
        {
            common.myItem[12] = pack.itemNum;
        }
        if (pack.itemId>=23& pack.itemId <=37 )
        {
            common.myCaiLiao[(int)pack.itemId] = pack.itemNum;          
        }
        if (pack.itemId==50)
        {
            common.myItem[10] = pack.itemNum;
        }

        if (pack.itemId >50 & pack.itemId <= 64)
        {    
            common.myCaiLiao[(int)pack.itemId] = pack.itemNum;                
        }
        //事件触发
        EventManager.PropUpdate?.Invoke();
    }

}
