using com.maple.game.osee.proto;
using com.maple.common.lobby.proto;
using NetLib;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using com.maple.game.osee.proto.fightten;
using com.maple.game.osee.proto.gobang;
using com.maple.game.osee.proto.fishing;
using UnityEngine.SceneManagement;
using DG.Tweening;
using CoreGame;
using Game.UI;

public class ChallengeController : BaseController 
{ 
	public ChallengeController()    
    {      

      //  Register((int)TwoEightMessageCode.S_C_TEROOM_PLAYER_RECONNECT_INFO_RESPONSE, typeof(ReconnectInfoResponse), On_ReconnectInfoResponse);//玩家重连信息
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_LIST_RESPONSE, typeof(FishingChallengeRoomListResponse), On_FishingChallengeRoomListResponse);//挑战赛房间列表响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_JOIN_ROOM_RESPONSE, typeof(FishingChallengeJoinRoomResponse), On_FishingChallengeJoinRoomResponse);//加入捕鱼挑战赛房间响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_EXIT_ROOM_RESPONSE, typeof(FishingChallengeExitRoomResponse), On_FishingChallengeExitRoomResponse);//退出捕鱼挑战赛房间响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_PLAYER_INFO_RESPONSE, typeof(FishingChallengeRoomPlayerInfoResponse), On_FishingChallengeRoomPlayerInfoResponse);//发送房间内玩家信息给房间内所有玩家
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_PLAYER_INFO_LIST_RESPONSE, typeof(FishingChallengeRoomPlayerInfoListResponse), On_FishingChallengeRoomPlayerInfoListResponse);//发送房间内所有玩家信息给某个玩家
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_VIEW_RESPONSE, typeof(FishingChallengeChangeBatteryViewResponse), On_FishingChallengeChangeBatteryViewResponse);//捕鱼改变炮台外观返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_LEVEL_RESPONSE, typeof(FishingChallengeChangeBatteryLevelResponse), On_FishingChallengeChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIRE_RESPONSE, typeof(FishingChallengeFireResponse), On_FishingChallengeFireResponse);//捕鱼发射子弹返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIGHT_FISH_RESPONSE, typeof(FishingChallengeFightFishResponse), On_FishingChallengeFightFishResponse);//捕鱼击中鱼类返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REFRESH_FISHES_RESPONSE, typeof(FishingChallengeRefreshFishesResponse), On_FishingChallengeRefreshFishesResponse);//捕鱼刷新房间鱼类返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_SYNCHRONISE_RESPONSE, typeof(FishingChallengeSynchroniseResponse), On_FishingChallengeSynchroniseResponse);//捕鱼同步返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_CHALLENGE_ROBOT_FIRE_RESPONSE, typeof(FishingChallengeRobotFireResponse), On_FishingChallengeRobotFireResponse);//发射子弹返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FISH_TIDE_RESPONSE, typeof(FishingChallengeFishTideResponse), On_FishingChallengeFishTideResponse);//捕鱼鱼潮返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE, typeof(FishingChallengeUseSkillResponse), On_FishingChallengeUseSkillResponse);//捕鱼使用技能返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CATCH_BOSS_FISH_RESPONSE, typeof(FishingChallengeCatchBossFishResponse), On_FishingChallengeCatchBossFishResponse);//玩家捕获boss鱼响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CATCH_SPECIAL_FISH_RESPONSE, typeof(FishingChallengeCatchSpecialFishResponse), On_FishingChallengeCatchSpecialFishResponse);//捕捉到特殊鱼响应

        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REFRESH_BOSS_RESPONSE, typeof(FishingChallengeRefreshBossResponse), On_FishingChallengeRefreshBossResponse);//房间刷新了boss响应

        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_BOSS_BUGLE_RESPONSE, typeof(FishingChallengeUseBossBugleResponse), On_FishingChallengeUseBossBugleResponse);//使用boss号角响应       


        Register((int)OseeMsgCode.S_C_TTMY_FISHING_Challenge_USE_TORPEDO_RESPONSE, typeof(FishingChallengeUseTorpedoResponse), On_FishingChallengeUseTorpedoResponse);//使用鱼雷响应
        Register((int)OseeMsgCode.S_C_CHALLENGE_BIT_FIGHT_FISH_RESPONSE, typeof(ChallengeBitFightFishResponse), On_ChallengeBitFightFishResponse);//挑战赛钻头击中鱼响应
        Register((int)OseeMsgCode.S_C_CHALLENGE_USE_ELE_RESPONSE, typeof(ChallengeUseEleResponse), On_ChallengeUseEleResponse);//龙晶战场同步电磁炮响应
        Register((int)OseeMsgCode.S_C_CHALLENGE_USE_BLACK_RESPONSE, typeof(ChallengeUseBlackResponse), On_ChallengeUseBlackResponse);//龙晶战场同步黑洞炮响应
        Register((int)OseeMsgCode.S_C_CHALLENGE_USE_TRO_RESPONSE, typeof(ChallengeUseTroResponse), On_ChallengeUseTroResponse);//龙晶战场同步鱼雷炮响应
        Register((int)OseeMsgCode.S_C_CHALLENGE_USE_BIT_RESPONSE, typeof(ChallengeUseBitResponse), On_ChallengeUseBitResponse);//龙晶战场同步钻头响应
        Register((int)OseeMsgCode.S_C_FISHING_CHALLENGE_SYNC_LOCK_RESPONSE, typeof(FishingChallengeSyncLockResponse), On_FishingChallengeSyncLockResponse);//龙晶战场同步锁定响应


        Register((int)OseeMsgCode.S_C_TTMY_OPEN_JC_RESPONSE, typeof(OpenJcResponse), On_OpenJcResponse);//开启奖池请求响应
        Register((int)OseeMsgCode.S_C_TTMY_GET_JC_ALL_MONEY_RESPONSE, typeof(GetJcAllMoneyResponse), On_GetJcAllMoneyResponse);//获取奖池金额响应
        Register((int)OseeMsgCode.S_C_TTMY_GET_JC_RECORD_RESPONSE, typeof(GetJcAllRecordResponse), On_GetJcAllRecordResponse);//获取奖池抽取记录响应
    }

    /// <summary>
    /// 开启奖池请求响应
    /// <summary>
    private void On_OpenJcResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<OpenJcResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.OpenJcResponse, this, pack);
    }
    /// <summary>
    /// 获取奖池金额响应
    /// <summary>
    private void On_GetJcAllMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetJcAllMoneyResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetJcAllMoneyResponse, this, pack);
    }
    /// <summary>
    /// 获取奖池抽取记录响应
    /// <summary>
    private void On_GetJcAllRecordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetJcAllRecordResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetJcAllRecordResponse, this, pack);
    }
    /// <summary>
    /// 挑战赛房间列表响应
    /// <summary>
    private void On_FishingChallengeRoomListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRoomListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRoomListResponse, this, pack);
    }
    /// <summary>
    /// 加入捕鱼挑战赛房间响应
    /// <summary>
    private void On_FishingChallengeJoinRoomResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeJoinRoomResponse>();
        common.bySessionName = BY_SESSIONNAME.龙晶3号场;
        try
        {      //卸载场景
            SceneManager.UnloadSceneAsync("ZBuyuRoom");
        }
        catch
        {
        }

        ControllerMgr.Instance.WaitForLoading = true;
        ByData.nModule = pack.roomType;
        //Debug.LogError("pack.roomIndex" + pack.roomType);
        UIMgr.CloseAll();
        UIMgr.CloseUI(UIPath.UIMainMenu);
        UIMgr.CloseUI(UIPath.UIBuyuMenu);
        UIMgr.DestroyUI(UIPath.UIByRoomMain);

        //清除玩家数据
        common.listPlayer.Clear();
        UILoadingGame uILoadingGame = UIMgr.ShowUISynchronize(UIPath.UILoadingGame).GetComponent<UILoadingGame>();
        uILoadingGame.StartLoad("ZBuyuRoom", UIPath.UIByChange);
      
        Root3D.Instance.ShowAllObject(true);

        common.IDRoomFish = BY_SESSION.龙晶场;
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeJoinRoomResponse, this, pack);
    }
    /// <summary>
    /// 退出捕鱼挑战赛房间响应
    /// <summary>
    private void On_FishingChallengeExitRoomResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeExitRoomResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeExitRoomResponse, this, pack);
    }
    /// <summary>
    /// 发送房间内玩家信息给房间内所有玩家
    /// <summary>
    private void On_FishingChallengeRoomPlayerInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRoomPlayerInfoResponse>();
        Debug.Log("pack.playerInfo"+ pack.playerInfo.playerId);
  
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRoomPlayerInfoResponse, this, pack);
    }
    /// <summary>
    /// 发送房间内所有玩家信息给某个玩家
    /// <summary>
    private void On_FishingChallengeRoomPlayerInfoListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRoomPlayerInfoListResponse>();
        for (int i = 0; i < pack.playerInfos.Count; i++)
        {
            Debug.Log("pack.playerInfo" + pack.playerInfos[i].playerId);
        }
      
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRoomPlayerInfoListResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼改变炮台外观返回
    /// <summary>
    private void On_FishingChallengeChangeBatteryViewResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeChangeBatteryViewResponse>();
        //if (pack.viewIndex == PlayerData.PlayerId)
        //{
        //    if (pack.viewIndex == 0)
        //    {
        //        PlayerData.PaoViewIndex = 70;
        //    }
        //    else
        //    {
        //        PlayerData.PaoViewIndex = pack.viewIndex;
        //    }
        //}
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeChangeBatteryViewResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼改变炮台等级返回
    /// <summary>
    private void On_FishingChallengeChangeBatteryLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeChangeBatteryLevelResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeChangeBatteryLevelResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼发射子弹返回
    /// <summary>
    private void On_FishingChallengeFireResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeFireResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeFireResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼击中鱼类返回
    /// 
    /// <summary>
    private void On_FishingChallengeFightFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeFightFishResponse>();
        if (pack==null)
        {
            Debug.Log("FishingChallengeFightFishResponse"+pack);
            return;
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeFightFishResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼刷新房间鱼类返回
    /// <summary>
    private void On_FishingChallengeRefreshFishesResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRefreshFishesResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRefreshFishesResponse, this, pack);

        //for (int i = 0; i < pack.fishInfos.Count; i++)
        //{
        //    common4._AllGameFish.Add(pack.fishInfos[0].id,);
        //}
    }
    /// <summary>
    /// 捕鱼同步返回
    /// <summary>
    private void On_FishingChallengeSynchroniseResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeSynchroniseResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeSynchroniseResponse, this, pack);
    }

    /// <summary>
    /// 捕鱼机器人发射子弹返回
    /// <summary>
    private void On_FishingChallengeRobotFireResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRobotFireResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRobotFireResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼鱼潮返回  
    /// <summary>
    private void On_FishingChallengeFishTideResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeFishTideResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeFishTideResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼使用技能返回
    /// <summary>
    private void On_FishingChallengeUseSkillResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeUseSkillResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeUseSkillResponse, this, pack);
    }
    /// <summary>
    /// 玩家捕获boss鱼响应
    /// <summary>
    private void On_FishingChallengeCatchBossFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeCatchBossFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeCatchBossFishResponse, this, pack);
    }
    /// <summary>
    /// 捕捉到特殊鱼响应
    /// <summary>
    private void On_FishingChallengeCatchSpecialFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeCatchSpecialFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeCatchSpecialFishResponse, this, pack);
    }
    /// <summary>
    /// 使用boss号角响应
    /// <summary>
    private void On_FishingChallengeUseBossBugleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeUseBossBugleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeUseBossBugleResponse, this, pack);
    }
    /// <summary>
    /// 使用鱼雷响应
    /// <summary>
    private void On_FishingChallengeUseTorpedoResponse(NetMsgPack obj)
    {
        //var pack = obj.GetData<FishingChallengeUseTorpedoResponse>();
        //UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeUseTorpedoResponse, this, pack);
    }
    /// <summary>
    /// 挑战赛钻头击中鱼响应
    /// <summary>
    private void On_ChallengeBitFightFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChallengeBitFightFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChallengeBitFightFishResponse, this, pack);
    }
    /// <summary>
    /// 房间刷新了boss响应
    /// <summary>
    private void On_FishingChallengeRefreshBossResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeRefreshBossResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeRefreshBossResponse, this, pack);
    }
    /// <summary>
    /// 龙晶战场同步锁定响应
    /// <summary>
    private void On_FishingChallengeSyncLockResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeSyncLockResponse>();
        var mp = common3._UIFishingInterface.GetOnePlayer(pack.userId);
        if (mp != null)
        {
            if (PlayerData.PlayerId != pack.userId)
            {
                //赋值
                mp.nAutoFish = pack.fishId;
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeSyncLockResponse, this, pack);
    }


    /// <summary>
    /// 龙晶战场同步电磁炮响应
    /// <summary>
    private void On_ChallengeUseEleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChallengeUseEleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChallengeUseEleResponse, this, pack);
    }
    /// <summary>
    /// 龙晶战场同步黑洞炮响应
    /// <summary>
    private void On_ChallengeUseBlackResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChallengeUseBlackResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChallengeUseBlackResponse, this, pack);
    }
    /// <summary>
    /// 龙晶战场同步鱼雷炮响应
    /// <summary>
    private void On_ChallengeUseTroResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChallengeUseTroResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChallengeUseTroResponse, this, pack);
    }
    /// <summary>
    /// 龙晶战场同步钻头响应
    /// <summary>
    private void On_ChallengeUseBitResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChallengeUseBitResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChallengeUseBitResponse, this, pack);
    }
}
