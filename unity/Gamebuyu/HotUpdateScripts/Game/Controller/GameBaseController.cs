using com.maple.game.osee.proto;
using com.maple.game.osee.proto.agent;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.goldenpig;
using com.maple.game.osee.proto.lobby;
using com.maple.gamebase.proto;
using CoreGame;
using DG.Tweening;
using Game.UI;
using LitJson;
using NetLib;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.SceneManagement;


public class ChangeItemNum 
{
}

public class GameBaseController : BaseController
{
    public GameBaseController()
    {
        
        Register((int)GameBaseMsgCode.S_C_CHAT_IN_ROOM_RESPONSE, typeof(ChatInRoomResponse), On_ChatInRoomResponse);//房间聊天返回
        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE, typeof(PlayerStatusResponse), On_PlayerStatusResponse);//玩家最新状态响应   
        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_PROP_RESPONSE, typeof(PlayerPropResponse), On_PlayerPropResponse);//玩家道具信息返回
        Register((int)OseeMsgCode.S_C_TTMY_MESSAGE_LIST_RESPONSE, typeof(MessageListResponse), On_MessageListResponse);//玩家消息列表响应
        Register((int)OseeMsgCode.S_C_TTMY_UNREAD_MESSAGE_COUNT_RESPONSE, typeof(UnreadMessageCountResponse), On_UnreadMessageCountResponse);//玩家未读消息数量响应
        Register((int)OseeMsgCode.S_C_TTMY_READ_MESSAGE_RESPONSE, typeof(ReadMessageResponse), On_ReadMessageResponse);//读取消息响应
        
        Register((int)OseeMsgCode.S_C_TTMY_DELETE_MESSAGE_RESPONSE, typeof(DeleteMessageResponse), On_DeleteMessageResponse);//
        Register((int)OseeMsgCode.S_C_TTMY_RECEIVE_MESSAGE_ITEMS_RESPONSE, typeof(ReceiveMessageItemsResponse), On_ReceiveMessageItemsResponse);//领取消息附件/删除响应
        Register((int)OseeMsgCode.S_C_TTMY_CHANGE_NICKNAME_RESPONSE, typeof(ChangeNicknameResponse), On_ChangeNicknameResponse);//更改昵称响应
        Register((int)OseeMsgCode.S_C_TTMY_FUNCTION_STATE_RESPONSE, typeof(FunctionStateResponse), On_FunctionStateResponse);//功能启用状态响应

        Register((int)OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE, typeof(FirstChargeRewardsResponse), On_FirstChargeRewardsResponse);//首充奖励响应
        Register((int)OseeMsgCode.S_C_TTMY_BUY_MONTH_CARD_REWARDS_RESPONSE, typeof(BuyMonthCardRewardsResponse), On_BuyMonthCardRewardsResponse);//购买月卡奖励

        Register((int)OseeMsgCode.S_C_TTMY_GOLDEN_PIG_BREAK_RESPONSE, typeof(GoldenPigBreakResponse), On_GoldenPigBreakResponse);//砸金猪响应
        Register((int)OseeMsgCode.S_C_TTMY_GOLDEN_PIG_FREE_TIMES_RESPONSE, typeof(GoldenPigFreeTimesResponse), On_GoldenPigFreeTimesResponse);//获取今日砸金猪免费次数响应
        Register((int)OseeMsgCode.S_C_TTMY_GOLDEN_PIG_HIT_LIMIT_RESPONSE, typeof(GoldenPigHitLimitResponse), On_GoldenPigHitLimitResponse);//获取今日VIP可砸的次数上限响应

        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_LEVEL_RESPONSE, typeof(PlayerLevelResponse), On_PlayerLevelResponse);//获取玩家等级请求
        Register((int)OseeMsgCode.S_C_TTMY_LOTTERY_EXCHANGE_LOG_RESPONSE, typeof(LotteryExchangeLogResponse), On_LotteryExchangeLogResponse);//获取兑换记录响应


        Register((int)OseeMsgCode.S_C_TTMY_DAILY_BAG_BUY_INFO_RESPONSE, typeof(DailyBagBuyInfoResponse), On_DailyBagBuyInfoResponse);//每日福袋购买信息响应
        Register((int)OseeMsgCode.S_C_TTMY_DAILY_BAG_BUY_SUCCESS_RESPONSE, typeof(DailyBagBuySuccessResponse), On_DailyBagBuySuccessResponse);//每日福袋购买成功响应
        Register((int)OseeMsgCode.S_C__FIRST_JOIN_RESPONSE, typeof(FirstJoinResponse), On_FirstJoinResponse);//第一次加入返回  
        Register((int)OseeMsgCode.S_C__FIRST_WEEK_LOGIN_RESPONSE, typeof(FirstWeekLoginResponse), On_FirstWeekLoginResponse);//第一次登陆的上周排行榜
        Register((int)OseeMsgCode.S_C_GET_PAY_WAY_RESPONSE, typeof(GetPayWayAllResponse), On_GetPayWayAllResponse);//支付方式

        Register((int)OseeMsgCode.S_C_USE_ELE_RESPONSE, typeof(UseEleResponse), On_UseEleResponse);//同步电磁炮响应

        Register((int)OseeMsgCode.S_C_CHANGE_BBATTERY_VIEW_RESPONSE, typeof(changeBatteryViewResponse), On_changeBatteryViewResponse);//切换炮台


        Register((int)OseeMsgCode.S_C_USE_BLACK_RESPONSE, typeof(UseBlackResponse), On_UseBlackResponse);//同步黑洞炮响应
        Register((int)OseeMsgCode.S_C_USE_TRO_RESPONSE, typeof(UseTroResponse), On_UseTroResponse);//同步鱼雷炮响应
        Register((int)OseeMsgCode.S_C_USE_BIT_RESPONSE, typeof(UseBitResponse), On_UseBitResponse);//同步钻头响应
                                                                                                   //  Register((int)OseeMsgCode.S_C_TTMY_DAILY_BAG_GIFT_SUCCESS_RESPONSE, typeof(DailyGiftBuySuccessResponse), On_DailyGiftBuySuccessResponse);//每日礼包购买成功响应
        Register((int)OseeMsgCode.S_C_TTMY_DAILY_BUY_GIFT_SUCCESS_RESPONSE, typeof(DailyBuyGiftSuccessResponse), On_DailyBuyGiftSuccessResponse);

        Register((int)OseeMsgCode.S_C_TTMY_DAILY_BUY_GIFT_INFO_RESPONSE, typeof(DailyBuyGiftInfoResponse), On_DailyBuyGiftInfoResponse);//每日礼包购买信息响应

        Register((int)OseeMsgCode.S_C_OSEE_TRIBE_ESTABLISH_RESPONSE, typeof(TribrEsTabLishResponse), On_TribrEsTabLishResponse);//创建部落返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_TRIBE_RESPONSE, typeof(GetTribeResponse), On_GetTribeResponse);//获取部落列表返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_TRIBE_ALL_USER_RESPONSE, typeof(GetTribeAllUserResponse), On_GetTribeAllUserResponse);//获取部落所有成员返回
        Register((int)OseeMsgCode.S_C_OSEE_APPLY_TRIBE_RESPONSE, typeof(ApplyTripeResponse), On_ApplyTripeResponse);//申请部落返回
        Register((int)OseeMsgCode.S_C_OSEE_DEAL_APPLY_TRIBE_RESPONSE, typeof(DealApplyTripeResponse), On_DealApplyTripeResponse);//处理申请部落返回

        Register((int)OseeMsgCode.S_C_IS_GET_TRIBE_GIFT_RESPONSE, typeof(IsTribeGetGiftResponse), On_IsTribeGetGiftResponse);//是否获取部落礼包返回

        Register((int)OseeMsgCode.S_C_OSEE_UPDATE_TRIBE_JURISDICTION_RESPONSE, typeof(UpdateTribeJurisDictionResponse), On_UpdateTribeJurisDictionResponse);//修改部落权限返回
        Register((int)OseeMsgCode.S_C_OSEE_DEPOSIT_TRIBE_WAREHOUSE_RESPONSE, typeof(DepositTribeWareHouseResponse), On_DepositTribeWareHouseResponse);//存入部落仓库返回
        Register((int)OseeMsgCode.S_C_OSEE_OUT_TRIBE_WAREHOUSE_RESPONSE, typeof(OutTribeWareHouseResponse), On_OutTribeWareHouseResponse);//取出部落仓库返回
        Register((int)OseeMsgCode.S_C_OSEE_UPDATE_TRIBE_NAME_RESPONSE, typeof(UpdateTribeNameResponse), On_UpdateTribeNameResponse);//修改部落名称返回
        Register((int)OseeMsgCode.S_C_OSEE_UPDATE_TRIBE_CONTEXT_RESPONSE, typeof(UpdateTribeContextResponse), On_UpdateTribeContextResponse);//修改部落简介返回
        Register((int)OseeMsgCode.S_C_OSEE_KICK_OUT_TRIBE_USER_RESPONSE, typeof(KickOutTribeUserResponse), On_KickOutTribeUserResponse);//踢人出部落返回
        Register((int)OseeMsgCode.S_C_OSEE_IS_JOIN_TRIBE_RESPONSE, typeof(IsJoinTribeResponse), On_IsJoinTribeResponse);//是否加入部落返回

        Register((int)OseeMsgCode.S_C_OSEE_GET_ALL_TRIBE_WAREHOUSE_RESPONSE, typeof(GetAllTribeWareHouseResponse), On_GetAllTribeWareHouseResponse);//获取所有宝箱返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_ALL_TRIBE_APPLY_RESPONSE, typeof(GetAllTribeApplyResponse), On_GetAllTribeApplyResponse);//获取所有部落申请返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_ONE_TRIBE_RESPONSE, typeof(GetOneTribeResponse), On_GetOneTribeResponse);//获取当前部落信息返回

        Register((int)OseeMsgCode.S_C_OSEE_SET_TRIBE_USER_POSITION_RESPONSE, typeof(SetTribeUserPositionResponse), On_SetTribeUserPositionResponse);//获取所有部落申请返回

        Register((int)OseeMsgCode.S_C_OSEE_GET_IN_OR_OUT_RESPONSE, typeof(GetInOrOutResponse), On_GetInOrOutResponse);//获取存取记录返回

        Register((int)OseeMsgCode.S_C_OSEE_UPDATE_TRIBE_LEVEL_RESPONSE, typeof(UpdateTribeLevelResponse), On_UpdateTribeLevelResponse);//获取存取记录返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_PLAYER_RANK_RESPONSE, typeof(PlayerRankResponse), On_PlayerRankResponse);//积分榜
        Register((int)OseeMsgCode.S_C_OSEE_GET_PLAYER_POINT_RESPONSE, typeof(PlayerPointResponse), On_PlayerPointResponse);//积分榜


        Register((int)OseeMsgCode.S_C_OSEE_GET_PLAYER_GOLD_RANK_RESPONSE, typeof(PlayerGoldRankResponse), On_PlayerGoldRankResponse);//获取玩家幸运王者榜排名和奖励返回
        Register((int)OseeMsgCode.S_C_OSEE_ROBOT_PLAY_GAME_RESPONSE, typeof(RobotPlayGameResponse), On_RobotPlayGameResponse);//机器人玩小游戏游戏返回

        Register((int)OseeMsgCode.S_C_FISHING_SYNC_LOCK_RESPONSE, typeof(FishingSyncLockResponse), On_FishingSyncLockResponse);//f经典渔场同步锁定响应
        Register((int)OseeMsgCode.S_C_TRIBE_SEARCH_RESPONSE, typeof(TribeSearchResponse), On_TribeSearchResponse);//部落宝箱搜索返回

        Register((int)OseeMsgCode.S_C_GET_TRIBE_GIFT_RESPONSE, typeof(GetTribeGiftResponse), On_GetTribeGiftResponse);//获取部落礼包返回


        Register((int)OseeMsgCode.S_C_OSEE_GET_FIGHT_NUM_RESPONSE, typeof(GetFightNumResponse), On_GetFightNumResponse);//获取玩家连击次数返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_FIGHT_RANK_RESPONSE, typeof(GetFightRankResponse), On_GetFightRankResponse);//获取玩家连击榜榜排名和奖励返回
        Register((int)OseeMsgCode.S_C_OSEE_GET_KILL_FISH_RANK_RESPONSE, typeof(KillFishRankResponse), On_KillFishRankResponse);//获取玩家连击榜榜排名和奖励返回
        Register((int)OseeMsgCode.S_C_OSEE_PLAYER_MONEY_RESPONSE, typeof(PlayerMoneyResponse), On_PlayerMoneyResponse);//获取玩家货币返回
   
        

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

        PlayerData.DragonCrystal = pack.dragonCrystal;
 
        //if (pack.isFirstJoin == 1)
        //{
        //    NetMessage.Login.Req_FirstJoinRequest(PlayerData.PlayerId);
        //    //新手指引
        //    //common.firstLogin = pack.isFirstJoin;
        //    //UIMgr.ShowUI(UIPath.NewbiePrompt);
        //}
       UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerMoneyResponse, this, pack);
    }
     

    void myCaiLiao(int key,long value) {
        if (common.myCaiLiao.ContainsKey(key))
        {
            common.myCaiLiao[key] = value;
        }
        else
        {
            common.myCaiLiao.Add(key, value);
        }
    }
    /// <summary> 
    /// 玩家道具信息返回
    /// <summary>
    private void On_PlayerPropResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerPropResponse>();
        //Debug.LogError("玩家道具信息返回");
        PlayerData.GoldTorpedo = pack.goldTorpedo;

        //common.myDicItem.Add(7, pack.goldTorpedo);
        //common.myDicItem.Add(8, pack.skillLock);
        //common.myDicItem.Add(9, pack.skillFrozen);
        //common.myDicItem.Add(11, pack.skillCrit);

        common.myDicItem[7]= pack.goldTorpedo;
        common.myDicItem[8] = pack.skillLock;
        common.myDicItem[9] = pack.skillFrozen;
        common.myDicItem[11] = pack.skillCrit;
        

        common.myItem[0] = pack.bronzeTorpedo;
        common.myItem[1] = pack.silverTorpedo;
        common.myItem[2] = pack.goldTorpedo;
        common.myItem[3] = pack.skillLock;
        common.myItem[4] = pack.skillFrozen;
        common.myItem[5] = pack.skillFast;
        common.myItem[6] = pack.skillCrit;
        common.myItem[7] = pack.bossBugle;
        common.myItem[8] = pack.fenShen;
        common.myItem[9] = pack.goldTorpedoBang;
        common.myItem[10] = pack.dianchipao;

        common.myItem[11] = pack.rareTorpedo;
        common.myItem[12] = pack.rareTorpedoBang;

        //common.myPao[0] = pack.qszs;
        //common.myPao[1] = pack.blnh;
        //common.myPao[2] = pack.lhtz;
        //common.myPao[3] = pack.swhp;

        //common.myCaiLiao.Clear();
        myCaiLiao(23, pack.yuGu);
        myCaiLiao(24, pack.haiYaoShi);
        myCaiLiao(25, pack.wangHunShi);
        myCaiLiao(26, pack.haiHunShi);
        myCaiLiao(27, pack.zhenZhuShi);
        myCaiLiao(28, pack.haiShouShi);
        myCaiLiao(29, pack.haiMoShi);
        myCaiLiao(30, pack.zhaoHunShi);
        myCaiLiao(31, pack.dianCiShi);
        myCaiLiao(32, pack.heiDongShi);
        myCaiLiao(33, pack.lingZhuShi);
        myCaiLiao(34, pack.longGu);
        myCaiLiao(35, pack.longZhu);
        myCaiLiao(36, pack.longYuan);
        myCaiLiao(37, pack.longJi);
        myCaiLiao(51, pack.heiDongPao);
        myCaiLiao(52, pack.yuLeiPao);
        myCaiLiao(53, pack.sendCard);
        myCaiLiao(54, pack.blackBullet);
        myCaiLiao(55, pack.bronzeBullet);
        myCaiLiao(56, pack.silverBullet);
        myCaiLiao(57, pack.goldBullet);

        myCaiLiao(14, pack.qszs);
        myCaiLiao(15, pack.blnh);
        myCaiLiao(16, pack.lhtz);
        myCaiLiao(17, pack.swhp);

        myCaiLiao(58, pack.zlhp);
        myCaiLiao(59, pack.hjzp);
        myCaiLiao(60, pack.gjzy);
        myCaiLiao(61, pack.hjhp);
        myCaiLiao(62, pack.tjzx);
        myCaiLiao(63, pack.lbs);
        myCaiLiao(64, pack.skillBit);

        Debug.Log("个人道具："+pack.data);
        var PropCanShu = JsonMapper.ToObject(pack.data);
        if (PropCanShu != null)
        {
            int[] allkey = common4.PaoWinTime.Keys.ToArray<int>();
            for (int i = 0; i < allkey.Length; i++)
            {
                string strkey = allkey[i].ToString();
                if (PropCanShu.Keys.Contains(strkey))
                {
                    common4.PaoWinTime[allkey[i]] = long.Parse(PropCanShu[strkey].ToString());
                }
                else
                {
                    common4.PaoWinTime[allkey[i]] = 0;
                }
            }
            foreach (string strkey in PropCanShu.Keys)
            {
                int m = int.Parse(strkey);
                if (common.myDicItem.ContainsKey(m))
                {
                    common.myDicItem[m] = long.Parse(PropCanShu[strkey].ToString());
                }
            }

        }
        else
        {
            int[] allkey = common4.PaoWinTime.Keys.ToArray<int>();
            for (int i = 0; i < allkey.Length; i++)
            {
                common4.PaoWinTime[allkey[i]] = 0;
            }

            if (common.myDicItem.ContainsKey(38))
            {
                common.myDicItem[38] = 0;
            }
        }
        ////道具参数
        //var PropCanShu = JsonMapper.ToObject(pack.data);
        //if (PropCanShu != null)
        //{
        //    foreach (string strkey in PropCanShu.Keys)
        //    {
        //        int m = int.Parse(strkey);
        //        if (m==38)
        //        {
        //            common.myDicItem[38] = long.Parse(PropCanShu[strkey].ToString());
        //        }
        //        if (common4.PaoWinTime.ContainsKey(m))
        //        {
        //            common4.PaoWinTime[m] = long.Parse(PropCanShu[strkey].ToString());
        //            EventManager.ShopPaoWingUpdate?.Invoke();
        //        }
        //    }
        //}
        //if (common5.PropCanShu != null)
        //{
        //    foreach (string jo in common5.PropCanShu.Keys)
        //    {            
        //        var m= jo;
        //        Debug.Log("赋值" + jo.ToString());
        //        if (common.myDicItem.ContainsKey(long.Parse(jo)))
        //        {
        //            common.myDicItem[long.Parse(jo)] = long.Parse(common5.PropCanShu[jo].ToString());
        //        }
        //        else
        //        {
        //            common.myDicItem.Add(long.Parse(jo), long.Parse(common5.PropCanShu[jo].ToString()));
        //        }
        //    }     
        //}
        //事件触发
        EventManager.PropUpdate?.Invoke();
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerPropResponse, this, pack);
    }

 
    /// <summary>
    /// 获取玩家连击次数返回
    /// <summary>
    private void On_GetFightNumResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetFightNumResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetFightNumResponse, this, pack);
    }
    /// <summary>
    /// 获取玩家连击榜榜排名和奖励返回
    /// <summary>
    private void On_GetFightRankResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetFightRankResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetFightRankResponse, this, pack);
        PlayerData.LianJiMessage = pack.playerInfos;
        EventManager.ChangeRank?.Invoke();// TriggerEvent(EventKey.ChangeRank, "ChangeRank");
    }
    private void On_KillFishRankResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<KillFishRankResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.KillFishRankResponse, this, pack);

    }

    /// <summary> 
    /// 同步黑洞炮响应
    /// <summary>
    private void On_UseBlackResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseBlackResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseBlackResponse, this, pack);
    }
    /// <summary>
    /// 同步鱼雷炮响应
    /// <summary>
    private void On_UseTroResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseTroResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseTroResponse, this, pack);
    }
    /// <summary>
    /// 同步钻头响应
    /// <summary>
    private void On_UseBitResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseBitResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseBitResponse, this, pack);
    }
    ///// <summary>
    ///// 每日礼包购买成功响应
    ///// <summary>
    //private void On_DailyGiftBuySuccessResponse(NetMsgPack obj)
    //{
    //    var pack = obj.GetData<DailyBuyGiftSuccessResponse>();
    //    UEventDispatcher.Instance.DispatchEvent(UEventName.DailyBuyGiftSuccessResponse, this, pack);
    //}
    /// <summary>
    /// 每日礼包购买成功响应
    /// <summary>
    private void On_DailyBuyGiftSuccessResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DailyBuyGiftSuccessResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.DailyBuyGiftSuccessResponse, this, pack);
    }
    /// <summary>
    /// 每日礼包购买信息响应
    /// <summary>
    private void On_DailyBuyGiftInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DailyBuyGiftInfoResponse>();
        if (PlayerData.DailyBag)
        {
            PlayerData.DailyBag = false;
            if (pack.buyItems.Count < 5)
            {
                UIMgr.ShowUI(UIPath.UIDaySpecialPackage);
            }
            else
            {
                PlayerData.GoldCard = true;
                NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
            }
        }
        if (PlayerData.byGetSpecialPackage)
        {
            if (pack.buyItems.Count >= 5)
            {
                UIMgr.CloseUI(UIPath.UIDaySpecialPackage);
                PlayerData.GoldCard = true;
                NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.DailyBuyGiftInfoResponse, this, pack);
    }
    /// <summary>
    /// 创建部落返回
    /// <summary>
    private void On_TribrEsTabLishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<TribrEsTabLishResponse>();
        if (pack.name != "" && pack.name != null)
        {
            UIMgr.CloseUI(UIPath.UIBuLuoWai);
            MessageBox.ShowPopMessage("创建部落:" + pack.name + " 成功");
            NetMessage.OseeFishing.Req_IsJoinTribeRequest(PlayerData.PlayerId);
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.TribrEsTabLishResponse, this, pack);
    }


    /// <summary>
    /// 获取部落列表返回
    /// <summary>
    private void On_GetTribeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetTribeResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetTribeResponse, this, pack);
    }

    /// <summary>
    /// 获取部落所有成员返回
    /// <summary>
    private void On_GetTribeAllUserResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetTribeAllUserResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetTribeAllUserResponse, this, pack);
    }
    /// <summary>
    /// 申请部落返回
    /// <summary>
    private void On_ApplyTripeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ApplyTripeResponse>();
        if (pack.tribeId != 0)
        {
            MessageBox.ShowPopMessage("请求已发送");
        }
        NetMessage.OseeLobby.Req_IsTribeGetGiftRequest(PlayerData.PlayerId);
        NetMessage.OseeLobby.Req_GetTribeRequest();

        UEventDispatcher.Instance.DispatchEvent(UEventName.ApplyTripeResponse, this, pack);
    }

    /// <summary>
    /// 是否获取部落礼包返回
    /// <summary>
    private void On_IsTribeGetGiftResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<IsTribeGetGiftResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.IsTribeGetGiftResponse, this, pack);
    }

    /// <summary>
    /// 处理申请部落返回
    /// <summary>
    private void On_DealApplyTripeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DealApplyTripeResponse>();

        if (PlayerData.BuluoID != 0 && pack.tribeId == PlayerData.BuluoID)//代表处理同一个部落
        {
            if (pack.dealResult == 1)//同意
            {
                NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
            }
            else
            {
                NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
            }
            NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);

        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.DealApplyTripeResponse, this, pack);

    }
    /// <summary>
    /// 修改部落权限返回
    /// <summary>
    private void On_UpdateTribeJurisDictionResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UpdateTribeJurisDictionResponse>();
        if (pack.tribeId != 0)
        {
            MessageBox.ShowPopMessage("修改权限成功!");
        }
        NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
        UEventDispatcher.Instance.DispatchEvent(UEventName.UpdateTribeJurisDictionResponse, this, pack);
    }
    /// <summary>
    /// 存入部落仓库返回
    /// <summary>
    private void On_DepositTribeWareHouseResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DepositTribeWareHouseResponse>();
        if (pack.tribeId != 0)
        {
            MessageBox.ShowPopMessage("存入成功");
        }
        NetMessage.OseeLobby.Req_GetAllTribeWareHouseRequest(PlayerData.BuluoID);
        UEventDispatcher.Instance.DispatchEvent(UEventName.DepositTribeWareHouseResponse, this, pack);
    }
    /// <summary>
    /// 取出部落仓库返回
    /// <summary>
    private void On_OutTribeWareHouseResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<OutTribeWareHouseResponse>();
        if (pack.tribeId != 0)
        {
            MessageBox.ShowPopMessage("取出成功");
        }
        NetMessage.OseeLobby.Req_GetAllTribeWareHouseRequest(PlayerData.BuluoID);
        UEventDispatcher.Instance.DispatchEvent(UEventName.OutTribeWareHouseResponse, this, pack);
    }
    /// <summary>
    /// 修改部落名称返回
    /// <summary>
    private void On_UpdateTribeNameResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UpdateTribeNameResponse>();
        NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
        UEventDispatcher.Instance.DispatchEvent(UEventName.UpdateTribeNameResponse, this, pack);
    }
    /// <summary>
    /// 修改部落简介返回
    /// <summary>
    private void On_UpdateTribeContextResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UpdateTribeContextResponse>();
        NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
        UEventDispatcher.Instance.DispatchEvent(UEventName.UpdateTribeContextResponse, this, pack);
    }
    /// <summary>
    /// 踢人出部落返回
    /// <summary>
    private void On_KickOutTribeUserResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<KickOutTribeUserResponse>();


        UEventDispatcher.Instance.DispatchEvent(UEventName.KickOutTribeUserResponse, this, pack);
    }
    /// <summary>
    /// 是否加入部落返回
    /// <summary>
    private void On_IsJoinTribeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<IsJoinTribeResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.IsJoinTribeResponse, this, pack);
        if (pack.tribeId != null && pack.tribeId.Count > 0)
        {
            //已经加入部落
            PlayerData.isJoinBuluo = pack.isJoin;
            PlayerData.Jion_BuluoList = pack.tribeId;
            if (pack.isAgent)
            {
                UIBuLuoWai buluowai = UIMgr.ShowUISynchronize(UIPath.UIBuLuoWai).GetComponent<UIBuLuoWai>();
                buluowai.ChangeChazhao();
            }
            else
            {
                if (pack.tribeId.Count > 0)
                {
                    PlayerData.BuluoID = pack.tribeId[0];
                    UIMgr.ShowUI(UIPath.UIBuLuo);
                }
                else
                {
                    PlayerData.BuluoID = 0;
                }
            }

        }
        else
        {
            //没加入部落
            PlayerData.Jion_BuluoList.Clear();
            UIMgr.ShowUI(UIPath.UIBuLuoWai);
        }

    }
    /// <summary>
    /// 获取所有宝箱返回
    /// <summary>
    private void On_GetAllTribeWareHouseResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetAllTribeWareHouseResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetAllTribeWareHouseResponse, this, pack);
    }
    /// <summary>
    /// 获取所有部落申请返回
    /// <summary>
    private void On_GetAllTribeApplyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetAllTribeApplyResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetAllTribeApplyResponse, this, pack);
    }
    /// <summary>
    /// 获取当前部落信息返回
    /// <summary>
    private void On_GetOneTribeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetOneTribeResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetOneTribeResponse, this, pack);
    }
    /// <summary>
    /// 获取所有部落申请返回
    /// <summary>
    private void On_SetTribeUserPositionResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<SetTribeUserPositionResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.SetTribeUserPositionResponse, this, pack);
    }
    /// <summary>
    /// 获取存取记录返回
    /// <summary>
    private void On_GetInOrOutResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetInOrOutResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetInOrOutResponse, this, pack);
    }
    private void On_UpdateTribeLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UpdateTribeLevelResponse>();
        if (pack.tribeId != 0)
        {
            MessageBox.ShowPopMessage("修改申请权限成功!");
        }
        NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
    }

    /// <summary>
    /// 切换炮台
    /// <summary>
    private void On_changeBatteryViewResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<changeBatteryViewResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.changeBatteryViewResponse, this, pack);
    }

    /// <summary>
    /// 同步电磁炮响应
    /// <summary>
    private void On_UseEleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseEleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseEleResponse, this, pack);
    }
    /// <summary>
    /// 玩家最新状态响应 
    /// <summary>
    private void On_PlayerStatusResponse(NetMsgPack obj)
    {
        //if (ByData.nModule == 31)
        //{
        //    return;
        //}
        //var pack = obj.GetData<PlayerStatusResponse>();
        //Debug.Log("此消息更新炮台和翅膀");
        //Debug.Log("状态响应" + pack.index);
        //Debug.Log("状态响应" + pack.datetime);
        //Debug.Log("状态响应" + pack.data);
        //LitJson.JsonData packdata = JsonMapper.ToObject(pack.data);

        //if (packdata.Keys.Contains("batter:view"))
        //{
        //    int batterid = int.Parse(packdata["batter:view"].ToString());
        //    //赋值
        //    if (pack.index == PlayerData.PlayerId)
        //    {
        //        int tmpPaotai= int.Parse(packdata["batter:view"].ToString());
        //        if (common4.PaoWinTime.ContainsKey(tmpPaotai))
        //        {
        //            if (tmpPaotai == 70 || tmpPaotai == 71 || tmpPaotai == 72 || tmpPaotai == 73 || tmpPaotai == 74) 
        //            {
        //                PlayerData.PaoViewIndex = tmpPaotai;
        //            }
        //            else
        //            {
        //                if (common4.PaoWinTime[tmpPaotai] <= 0)
        //                {
        //                    NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(70);
        //                    PlayerData.PaoViewIndex = 70;
        //                }
        //                else
        //                {
        //                    PlayerData.PaoViewIndex = tmpPaotai;
        //                }
        //            }
        //        }
        //    }
        //    if (common3._UIFishingInterface != null)
        //    {
        //        var tmp = common3._UIFishingInterface.GetOnePlayer(pack.index);
        //        if (tmp != null)
        //        {
        //            tmp.ChangePaoView(batterid);
        //        }
        //    }
        //}
        //if (packdata.Keys.Contains("wing:view"))
        //{
        //    int wingid = int.Parse(packdata["wing:view"].ToString());
        //    //赋值
        //    if (pack.index == PlayerData.PlayerId)
        //    {
        //        int tmpWing = int.Parse(packdata["wing:view"].ToString());
        //        if (common4.PaoWinTime.ContainsKey(tmpWing))
        //        {
        //            if (common4.PaoWinTime[tmpWing] <= 0)
        //            {
        //                NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(80);
        //                PlayerData.WingIndex = 80;
        //            }
        //            else
        //            {
        //                PlayerData.WingIndex = tmpWing;
        //            }
        //        }
        //    }
        //    if (common3._UIFishingInterface != null)
        //    {
        //        var tmp = common3._UIFishingInterface.GetOnePlayer(pack.index);
        //        if (tmp != null)
        //        {
        //            tmp.ChangeWingView(wingid);
        //        }
        //    }
        //}

        //EventManager.ShopPaoWingUpdate?.Invoke();
        //UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerStatusResponse, this, pack);
    }
    /// <summary>
    /// 房间聊天返回 
    /// <summary>
    private void On_ChatInRoomResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChatInRoomResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChatInRoomResponse, this, pack);
    }
    private void On_PlayerRankResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerRankResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerRankResponse, this, pack);
    }
    private void On_PlayerPointResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerPointResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerPointResponse, this, pack);
    }
    /// <summary>
    /// 获取玩家幸运王者榜排名和奖励返回
    /// <summary>
    private void On_PlayerGoldRankResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerGoldRankResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerGoldRankResponse, this, pack);
    }
    /// <summary>
    /// 获取玩家幸运王者榜积分返回
    /// <summary>
    private void On_PlayerGoldResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerGoldResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerGoldResponse, this, pack);
    }
    /// <summary>
    /// 机器人玩小游戏游戏返回
    /// <summary>
    private void On_RobotPlayGameResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<RobotPlayGameResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.RobotPlayGameResponse, this, pack);
    }
   
    /// <summary>
    /// 经典渔场同步锁定响应
    /// <summary>
    private void On_FishingSyncLockResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingSyncLockResponse>();
        var mp = common3._UIFishingInterface.GetOnePlayer(pack.userId);
        if (mp!=null)
        {
            if (PlayerData.PlayerId!=pack.userId)
            {
                //赋值
                mp.nAutoFish = pack.fishId;
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingSyncLockResponse, this, pack);
    }
    /// <summary>
    /// 获取部落礼包返回
    /// <summary>
    private void On_GetTribeGiftResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetTribeGiftResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetTribeGiftResponse, this, pack);
    }

    /// <summary>
    /// 部落宝箱搜索返回
    /// <summary>
    private void On_TribeSearchResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<TribeSearchResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.TribeSearchResponse, this, pack);
    }


    ///// <summary> 
    ///// 玩家道具信息返回
    ///// <summary>
    //private void On_PlayerPropResponse(NetMsgPack obj)
    //{
    //    var pack = obj.GetData<PlayerPropResponse>();
    //    common.myItem[0] = pack.bronzeTorpedo;
    //    common.myItem[1] = pack.silverTorpedo;
    //    common.myItem[2] = pack.goldTorpedo;
    //    common.myItem[3] = pack.skillLock;
    //    common.myItem[4] = pack.skillFrozen;
    //    common.myItem[5] = pack.skillFast;
    //    common.myItem[6] = pack.skillCrit;
    //    common.myItem[7] = pack.bossBugle;
    //    common.myItem[8] = pack.fenShen;
    //    common.myItem[9] = pack.goldTorpedoBang;
    //    common.myItem[10] = pack.dianchipao;

    //    common.myItem[11] = pack.rareTorpedo;
    //    common.myItem[12] = pack.rareTorpedoBang;

    //    common.myPao[0] = pack.qszs;
    //    common.myPao[1] = pack.blnh;
    //    common.myPao[2] = pack.lhtz;
    //    common.myPao[3] = pack.swhp;

    //    common.myCaiLiao.Clear();
    //    //common.myCaiLiao.Add(23, pack.yuGu);
    //    common.myCaiLiao.Add(23, pack.yuGu);
    //    common.myCaiLiao.Add(24, pack.haiYaoShi);
    //    common.myCaiLiao.Add(25, pack.wangHunShi);
    //    common.myCaiLiao.Add(26, pack.haiHunShi);
    //    common.myCaiLiao.Add(27, pack.zhenZhuShi);
    //    common.myCaiLiao.Add(28, pack.haiShouShi);
    //    common.myCaiLiao.Add(29, pack.haiMoShi);
    //    common.myCaiLiao.Add(30, pack.zhaoHunShi);
    //    common.myCaiLiao.Add(31, pack.dianCiShi);
    //    common.myCaiLiao.Add(32, pack.heiDongShi);
    //    common.myCaiLiao.Add(33, pack.lingZhuShi);
    //    common.myCaiLiao.Add(34, pack.longGu);
    //    common.myCaiLiao.Add(35, pack.longZhu);
    //    common.myCaiLiao.Add(36, pack.longYuan);
    //    common.myCaiLiao.Add(37, pack.longJi);
    //    common.myCaiLiao.Add(51, pack.heiDongPao);
    //    common.myCaiLiao.Add(52, pack.yuLeiPao);
    //    common.myCaiLiao.Add(53, pack.sendCard);
    //    common.myCaiLiao.Add(54, pack.blackBullet);
    //    common.myCaiLiao.Add(55, pack.bronzeBullet);
    //    common.myCaiLiao.Add(56, pack.silverBullet);
    //    common.myCaiLiao.Add(57, pack.goldBullet);



    //    //common.myCaiLiao.Add(20001, pack.qszs);
    //    //common.myCaiLiao.Add(20002, pack.blnh);
    //    //common.myCaiLiao.Add(20003, pack.lhtz);
    //    //common.myCaiLiao.Add(20004, pack.swhp);
    //    //common.myCaiLiao.Add(20005, pack.lbs);
    //    //common.myCaiLiao.Add(20006, pack.tjzx);
    //    //common.myCaiLiao.Add(20007, pack.hjhp);
    //    //common.myCaiLiao.Add(20008, pack.gjzy);
    //    //common.myCaiLiao.Add(20009, pack.hjzp);
    //    //common.myCaiLiao.Add(20010, pack.zlhp);
    //    common.myCaiLiao.Add(14, pack.qszs);
    //    common.myCaiLiao.Add(15, pack.blnh);
    //    common.myCaiLiao.Add(16, pack.lhtz);
    //    common.myCaiLiao.Add(17, pack.swhp);

    //    common.myCaiLiao.Add(58, pack.zlhp);
    //    common.myCaiLiao.Add(59, pack.hjzp);
    //    common.myCaiLiao.Add(60, pack.gjzy);
    //    common.myCaiLiao.Add(61, pack.hjhp);
    //    common.myCaiLiao.Add(62, pack.tjzx);
    //    common.myCaiLiao.Add(63, pack.lbs);






    //    common.myCaiLiao.Add(64, pack.skillBit);
    //    int varm = 0;
    //    if (int.TryParse(pack.useBatteryView, out varm))
    //    {
    //        PlayerData.PaoViewIndex = varm;
    //    }
    //    else
    //    {
    //        PlayerData.PaoViewIndex = 0;
    //    }




    //    common.monthCardOverDate = pack.monthCardOverDate;

    //    if (common3._UIFishingInterface != null)
    //    {
    //        common3._UIFishingInterface.SetMyItem();
    //    }

    //    //事件触发
    //    EventManager.Instance.TriggerEvent(EventKey.ChangeItem, "type object param");

    //    //if (UIByRoomMain.instance!=null&& UIByRoomMain.instance.gameObject.activeSelf)
    //    //{
    //    //    UIByRoomMain.instance.SetMyItem();
    //    //}
    //    //if (UIByGrandPrix.instance != null && UIByGrandPrix.instance.gameObject.activeSelf)
    //    //{
    //    //    UIByGrandPrix.instance.SetMyItem();
    //    //}
    //    //if (UIByChange.instance != null && UIByChange.instance.gameObject.activeSelf)
    //    //{
    //    //    UIByChange.instance.SetMyItem();
    //    //}
    //    if (UIYueKa.instance != null && UIYueKa.instance.gameObject.activeSelf)
    //    {
    //        UIYueKa.instance.ChangeTime();
    //    }
    //    if (UIDragonScale.instance != null && UIDragonScale.instance.gameObject.activeSelf)
    //    {
    //        UIDragonScale.instance.ChangeMyNum();
    //    }
    //    if (UIHitPig.instance != null && UIHitPig.instance.gameObject.activeSelf)
    //    {
    //        //UIHitPig.instance.ChangeItem();
    //        //  UIHitPig.instance.ChangeItemNum();
    //    }

    //    UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerPropResponse, this, pack);
    //}
    /// <summary>
    /// 玩家消息列表响应
    /// <summary>
    private void On_MessageListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<MessageListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.MessageListResponse, this, pack);
    }
    /// <summary>
    /// 玩家未读消息数量响应
    /// <summary>
    private void On_UnreadMessageCountResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UnreadMessageCountResponse>();
        EventManager.XiaoNumUpdate?.Invoke(pack.count);
        UEventDispatcher.Instance.DispatchEvent(UEventName.UnreadMessageCountResponse, this, pack);
    }
    /// <summary>
    /// 读取消息响应
    /// <summary>
    private void On_ReadMessageResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ReadMessageResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ReadMessageResponse, this, pack);
    }
    /// <summary>
    /// 领取消息附件/删除响应
    /// <summary>
    private void On_ReceiveMessageItemsResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ReceiveMessageItemsResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ReceiveMessageItemsResponse, this, pack);
    }
    private void On_DeleteMessageResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DeleteMessageResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.DeleteMessageResponse, this, pack);
    }
   
    /// <summary>
    /// 更改昵称响应
    /// <summary>
    private void On_ChangeNicknameResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChangeNicknameResponse>();
        PlayerData.NickName = pack.nickname;
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChangeNicknameResponse, this, pack);
    }

    /// <summary>
    /// 功能启用状态响应
    /// <summary>
    private void On_FunctionStateResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FunctionStateResponse>();
        PlayerData.OpenCloseStates = pack.functionState;
        UEventDispatcher.Instance.DispatchEvent(UEventName.FunctionStateResponse, this, pack);
    }
    /// <summary>
    /// 首充奖励响应
    /// <summary>
    private void On_FirstChargeRewardsResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FirstChargeRewardsResponse>();
        UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
        Dictionary<int, long> Dictmp = new Dictionary<int, long>();
        foreach (var item in pack.rewards)
        {
            Dictmp.Add(item.itemId, item.itemNum);
        }
        tmp.InitItem(Dictmp, -1, true);
        UEventDispatcher.Instance.DispatchEvent(UEventName.FirstChargeRewardsResponse, this, pack);
    }
    /// <summary>
    /// 购买月卡奖励
    /// <summary>
    private void On_BuyMonthCardRewardsResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BuyMonthCardRewardsResponse>();
        UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
        Dictionary<int, long> Dictmp = new Dictionary<int, long>();
        foreach (var item in pack.rewards)
        {
            Dictmp.Add(item.itemId, item.itemNum);
        }
        tmp.InitItem(Dictmp, -1, true);
        UEventDispatcher.Instance.DispatchEvent(UEventName.BuyMonthCardRewardsResponse, this, pack);
    }

    /// <summary>
    /// 砸金猪响应
    /// <summary>
    private void On_GoldenPigBreakResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GoldenPigBreakResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GoldenPigBreakResponse, this, pack);
    }
    /// <summary>
    /// 获取今日砸金猪免费次数响应
    /// <summary>
    private void On_GoldenPigFreeTimesResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GoldenPigFreeTimesResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GoldenPigFreeTimesResponse, this, pack);
    }
    /// <summary>
    /// 获取今日VIP可砸的次数上限响应
    /// <summary>
    private void On_GoldenPigHitLimitResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GoldenPigHitLimitResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GoldenPigHitLimitResponse, this, pack);
    }


    /// <summary>
    /// 获取玩家等级请求
    /// <summary>
    private void On_PlayerLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerLevelResponse>();
        PlayerData.AccountPhone = pack.phone;
        PlayerData.PlayerLevel = pack.level;
        //if (pack.level <= 1)
        //{

        //     common.firstLogin = PlayerPrefs.GetInt("Newbie");
        //    if (common.firstLogin == 1)
        //    {
        //        NetMessage.Login.Req_FirstJoinRequest(PlayerData.PlayerId);
        //        common.firstLogin = 0;
        //         PlayerPrefs.SetInt("Newbie",0);
        //    }
        //    //if (common.firstLogin==0)
        //    //{
        //    //  //  SceneManager.LoadScene("NewbiePrompt", LoadSceneMode.Additive);
        //    //    UIMgr.ShowUI(UIPath.NewbiePrompt);
        //    //}            
        //}
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerLevelResponse, this, pack);
    }
    /// <summary>
    /// 第一次加入返回
    /// <summary>
    private void On_FirstJoinResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FirstJoinResponse>();

        UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
        Dictionary<int, long> Dictmp = new Dictionary<int, long>();
        foreach (var item in pack.items)
        {
            Dictmp.Add(item.itemId, item.itemNum);
        }
        //if (pack.diamond>0)
        //{
        //    Dictmp.Add(4, pack.diamond);
        //}
        //if (pack.money > 0)
        //{
        //    Dictmp.Add(1, pack.money);
        //}
        //if (pack.dragonCrystal > 0)
        //{
        //    Dictmp.Add(18, pack.dragonCrystal);
        //}
        //if (pack.lottery > 0)
        //{
        //    Dictmp.Add(3, pack.lottery);
        //}
        tmp.InitItem(Dictmp, -1, false, () => { 
            //NetMessage.OseeLobby.Req_PlayerMoneyRequest(0); 
        });
        //  NetMessage.OseeFishing.Req_QuickStartRequest();

        UEventDispatcher.Instance.DispatchEvent(UEventName.FirstJoinResponse, this, pack);
    }
    /// <summary>
    /// 第一次登陆的上周排行榜
    /// <summary>
    private void On_FirstWeekLoginResponse(NetMsgPack obj)
    {
        //var pack = obj.GetData<FirstWeekLoginResponse>();
        //UILastWeekRank lastweek = UIMgr.ShowUISynchronize(UIPath.UILastWeekRank).GetComponent<UILastWeekRank>();
        //lastweek.ShowPanel(pack.firstWeekLogin);
        //UEventDispatcher.Instance.DispatchEvent(UEventName.FirstWeekLoginResponse, this, pack);
    }
    private void On_GetPayWayAllResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetPayWayAllResponse>();
        common.wxH5 = pack.wxH5;
        common.wxS = pack.wxS;
        common.zfbH5 = pack.zfbH5;
        common.zfbS = pack.zfbS;
    }

   
    /// <summary>
    /// 获取兑换记录响应
    /// <summary>
    private void On_LotteryExchangeLogResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<LotteryExchangeLogResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.LotteryExchangeLogResponse, this, pack);
    }
    /// <summary>
    /// 每日福袋购买信息响应
    /// <summary>
    private void On_DailyBagBuyInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DailyBagBuyInfoResponse>();
        if (PlayerData.DailyBag)
        {
            PlayerData.DailyBag = false;
            if (pack.buyItems.Count < 6)
            {
                UIMgr.ShowUI(UIPath.UIDaySpecialPackage);
            }
            else
            {
                PlayerData.GoldCard = true;
                NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
            }
        }
        if (PlayerData.byGetSpecialPackage)
        {
            if (pack.buyItems.Count >= 6)
            {
                UIMgr.CloseUI(UIPath.UIDaySpecialPackage);
                PlayerData.GoldCard = true;
                NetMessage.Lobby.Req_MoneyCardBuyInfoRequest();
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.DailyBagBuyInfoResponse, this, pack);
    }
    /// <summary>
    /// 每日福袋购买成功响应
    /// <summary>
    private void On_DailyBagBuySuccessResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DailyBagBuySuccessResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.DailyBagBuySuccessResponse, this, pack);
    }
}
