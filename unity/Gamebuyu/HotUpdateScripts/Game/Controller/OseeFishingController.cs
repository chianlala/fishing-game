
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;
using NetLib;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;



public class OseeFishingController : BaseController
{
    public OseeFishingController()
    {
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_JOIN_ROOM_RESPONSE, typeof(FishingJoinRoomResponse), On_FishingJoinRoomResponse);//捕鱼加入房间返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_PLAYER_INFO_RESPONSE, typeof(FishingPlayerInfoResponse), On_FishingPlayerInfoResponse);//捕鱼玩家信息返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_PLAYERS_INFO_RESPONSE, typeof(FishingPlayersInfoResponse), On_FishingPlayersInfoResponse);//捕鱼玩家列表信息返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_EXIT_ROOM_RESPONSE, typeof(FishingExitRoomResponse), On_FishingExitRoomResponse);//捕鱼退出房间返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_GET_TREASURE_RESPONSE, typeof(FishingGetTreasureResponse), On_FishingGetTreasureResponse);//捕鱼获取宝藏返回


        Register((int)OseeMsgCode.S_C_TTMY_DAILY_TASK_LIST_RESPONSE, typeof(DailyTaskListResponse), On_DailyTaskListResponse);//每日任务列表响应
        Register((int)OseeMsgCode.S_C_TTMY_GET_DAILY_TASK_REWARD_RESPONSE, typeof(GetDailyTaskRewardResponse), On_GetDailyTaskRewardResponse);//领取每日任务奖励响应
        Register((int)OseeMsgCode.S_C_TTMY_GET_DAILY_ACTIVE_REWARD_RESPONSE, typeof(GetDailyActiveRewardResponse), On_GetDailyActiveRewardResponse);//领取每日活跃奖励响应

        //Register((int)OseeMsgCode.S_C_OSEE_FISHING_ROOM_TASK_LIST_RESPONSE, typeof(FishingRoomTaskListResponse), On_FishingTaskListResponse);// 捕鱼获取任务列表返回     
        //Register((int)OseeMsgCode.S_C_OSEE_FISHING_GET_ROOM_TASK_REWARD_RESPONSE, typeof(FishingGetRoomTaskRewardResponse), On_FishingGetTaskRewardResponse);//捕鱼获取房间任务奖励返回

        Register((int)OseeMsgCode.S_C_OSEE_FISHING_GET_ROOM_TASK_REWARD_RESPONSE, typeof(FishingGetRoomTaskRewardResponse), On_FishingGetRoomTaskRewardResponse);//捕鱼获取房间任务奖励返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_ROOM_TASK_LIST_RESPONSE, typeof(FishingRoomTaskListResponse), On_FishingRoomTaskListResponse);//捕鱼获取房间任务列表返回

        Register((int)OseeMsgCode.S_C_OSEE_FISHING_LEVEL_UP_RESPONSE, typeof(FishingLevelUpResponse), On_FishingLevelUpResponse);//捕鱼玩家升级返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_CHANGE_BATTERY_VIEW_RESPONSE, typeof(FishingChangeBatteryViewResponse), On_FishingChangeBatteryViewResponse);//捕鱼改变炮台外观返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_CHANGE_BATTERY_LEVEL_RESPONSE, typeof(FishingChangeBatteryLevelResponse), On_FishingChangeBatteryLevelResponse);//捕鱼改变炮台等级返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_CHANGE_BATTERY_MULT_RESPONSE, typeof(FishingChangeBatteryMultResponse), On_FishingChangeBatteryMultResponse);//捕鱼改变炮台倍数返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_FIRE_RESPONSE, typeof(FishingFireResponse), On_FishingFireResponse);//捕鱼发射子弹返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_FIGHT_FISH_RESPONSE, typeof(FishingFightFishResponse), On_FishingFightFishResponse);//捕鱼击中鱼类返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_REFRESH_FISHES_RESPONSE, typeof(FishingRefreshFishesResponse), On_FishingRefreshFishesResponse);//捕鱼刷新房间鱼类返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_SYNCHRONISE_RESPONSE, typeof(FishingSynchroniseResponse), On_FishingSynchroniseResponse);//捕鱼同步返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_USE_SKILL_RESPONSE, typeof(FishingUseSkillResponse), On_FishingUseSkillResponse);//捕鱼使用技能返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_FINISH_ROOM_GOAL_RESPONSE, typeof(FishingFinishRoomGoalResponse), On_FishingFinishRoomGoalResponse);//捕鱼完成房间目标返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_ROBOT_FIRE_RESPONSE, typeof(FishingRobotFireResponse), On_FishingRobotFireResponse);//捕鱼机器人发射子弹返回
        Register((int)OseeMsgCode.S_C_OSEE_FISHING_FISH_TIDE_RESPONSE, typeof(FishingFishTideResponse), On_FishingFishTideResponse);//捕鱼鱼潮返回
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_USE_TORPEDO_RESPONSE, typeof(FishingUseTorpedoResponse), On_FishingUseTorpedoResponse);//使用龙珠响应
        Register((int)OseeMsgCode.S_C_TTMY_CATCH_BOSS_FISH_RESPONSE, typeof(CatchBossFishResponse), On_CatchBossFishResponse);//玩家捕获boss鱼响应
        Register((int)OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_HINT_RESPONSE, typeof(UnlockBatteryLevelHintResponse), On_UnlockBatteryLevelHintResponse);//解锁炮台等级提示返回
        Register((int)OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_RESPONSE, typeof(UnlockBatteryLevelResponse), On_UnlockBatteryLevelResponse);//解锁炮台等级响应
        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_BATTERY_LEVEL_RESPONSE, typeof(PlayerBatteryLevelResponse), On_PlayerBatteryLevelResponse);//拥有最高炮台等级响应

        Register((int)OseeMsgCode.S_C_TTMY_CATCH_SPECIAL_FISH_RESPONSE, typeof(CatchSpecialFishResponse), On_CatchSpecialFishResponse);//捕捉特殊鱼响应

        Register((int)OseeMsgCode.S_C_TTMY_ONE_KEY_GET_DAILY_TASK_REWARDS_RESPONSE, typeof(OneKeyGetDailyTaskRewardsResponse), On_OneKeyGetDailyTaskRewardsResponse);//一键领取所有已完成的每日奖励响应

        Register((int)OseeMsgCode.S_C_TTMY_FISHING_GET_FIELD_INFO_RESPONSE, typeof(FishingGetFieldInfoResponse), On_FishingGetFieldInfoResponse);//获取捕鱼场次信息响应

        Register((int)OseeMsgCode.S_C_TTMY_USE_BOSS_BUGLE_RESPONSE, typeof(UseBossBugleResponse), On_UseBossBugleResponse);//使用boss号角响应
        Register((int)OseeMsgCode.S_C_TTMY_IS_IN_FISHING_ROOM_RESPONSE, typeof(IsInFishingRoomResponse), On_IsInFishingRoomResponse);//是否在捕鱼房间内响应
           Register((int)OseeMsgCode.S_C_GET_BOSS_BUGLE_RESPONSE, typeof(GetBossBugleResponse), On_GetBossBugleResponse);//使用boss号角计时响应 
        Register((int)OseeMsgCode.S_C_BIT_FIGHT_FISH_RESPONSE, typeof(BitFightFishResponse), On_BitFightFishResponse);//钻头击中鱼响应
        //Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_SEAT_RESPONSE, typeof(FishingChallengeChangeSeatResponse), On_FishingChallengeChangeSeatResponse);//VIP换座响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_ROOM_LIST_RESPONSE, typeof(FishingRoomListResponse), On_FishingRoomListResponse);//第五房间列表响应
        Register((int)OseeMsgCode.S_C_TTMY_FISHING_CHANGE_CRIT_MULT_RESPONSE, typeof(FishingChangeCritMultResponse), On_FishingChangeCritMultResponse);//暴击

        Register((int)OseeMsgCode.S_C_FISHING_DOUBLE_KILL_FISH_RESPONSE, typeof(FishingDoubleKillFishResponse), On_FishingDoubleKillFishResponse);//二次伤害杀死鱼返回
        Register((int)OseeMsgCode.S_C_FISHING_DOUBLE_KILL_RESPONSE, typeof(FishingDoubleKillResponse), On_FishingDoubleKillResponse);//二次伤害鱼返回
        Register((int)OseeMsgCode.S_C_FISHING_ROOM_FISH_MULTIPLE_RESPONSE, typeof(FishingRoomFishMultipleResponse), On_FishingRoomFishMultipleResponse);//二次伤害鱼返回 
        Register((int)OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_FISH_RESPONSE, typeof(FishingChallengeDoubleKillFishResponse), On_FishingChallengeDoubleKillFishResponse);//龙晶场二次伤害杀死鱼返回
        Register((int)OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_RESPONSE, typeof(FishingChallengeDoubleKillResponse), On_FishingChallengeDoubleKillResponse);//龙晶场二次伤害鱼返回
        Register((int)OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_END_RESPONSE, typeof(FishingChallengeDoubleKillEndResponse), On_FishingChallengeDoubleKillEndResponse);//龙晶场二次伤害结束返回

        Register((int)OseeMsgCode.S_C_FISH_BOSS_MULTIPLE_RESPONSE, typeof(FishBossMultipleResponse), On_FishBossMultipleResponse);//特殊BOSS鱼倍数信息

        Register((int)OseeMsgCode.S_C_TTMY_BACKGROUND_SYNC_RESPONSE, typeof(BackgroundSyncResponse), On_BackgroundSyncResponse);//背景同步响应
    }
    /// <summary>
    /// 龙晶场二次伤害杀死鱼返回
    /// <summary>
    private void On_FishingChallengeDoubleKillFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeDoubleKillFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeDoubleKillFishResponse, this, pack);
    }
    /// <summary>
    /// 龙晶场二次伤害鱼返回
    /// <summary>
    private void On_FishingChallengeDoubleKillResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeDoubleKillResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeDoubleKillResponse, this, pack);
    }
    /// <summary>
    /// 龙晶场二次伤害结束返回
    /// <summary>
    private void On_FishingChallengeDoubleKillEndResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChallengeDoubleKillEndResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeDoubleKillEndResponse, this, pack);
    }

    /// <summary>
    /// 特殊BOSS鱼倍数信息
    /// <summary>
    private void On_FishBossMultipleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishBossMultipleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishBossMultipleResponse, this, pack);
    }
    private void On_BackgroundSyncResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BackgroundSyncResponse>();

 
        UEventDispatcher.Instance.DispatchEvent(UEventName.BackgroundSyncResponse, this, pack);
    }
    private void On_FishingChangeCritMultResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChangeCritMultResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChangeCritMultResponse, this, pack);
    }
    /// <summary>
    /// 二次伤害鱼 杀死的鱼返回
    /// <summary>
    private void On_FishingDoubleKillFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingDoubleKillFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingDoubleKillFishResponse, this, pack);
    }
    /// <summary>
    /// 杀死二次伤害鱼的返回
    /// <summary>
    private void On_FishingDoubleKillResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingDoubleKillResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingDoubleKillResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼房间特殊鱼倍数 财神 小黄鸭
    /// <summary>
    private void On_FishingRoomFishMultipleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingRoomFishMultipleResponse>();
        Debug.Log(pack.fishName+"Bei"+ pack.mult+"Time"+ pack.datetime);
        if (common.listFish.ContainsKey(pack.fishId))
        {
            common.listFish[pack.fishId].setText(pack.mult);
        }
        if (pack.fishName=="小黄鸭")
        {
            common.nXhya = pack.mult;
        }
        if (pack.fishName == "财神")
        {
            common.nCaisheng = pack.mult;
        }

       
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingRoomFishMultipleResponse, this, pack);
    }
    /// <summary> 
    /// 每日任务列表响应
    /// <summary>
    private void On_DailyTaskListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<DailyTaskListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.DailyTaskListResponse, this, pack);
    }
    /// <summary>
    /// 领取每日任务奖励响应
    /// <summary>
    private void On_GetDailyTaskRewardResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetDailyTaskRewardResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetDailyTaskRewardResponse, this, pack);
    }
    /// <summary>
    /// 领取每日活跃奖励响应
    /// <summary>
    private void On_GetDailyActiveRewardResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetDailyActiveRewardResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetDailyActiveRewardResponse, this, pack);
    }

    /// <summary>
    /// 捕鱼鱼潮返回
    /// <summary>
    private void On_FishingFishTideResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingFishTideResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingFishTideResponse, this, pack);
    }
    /// <summary>
    /// 使用龙珠响应
    /// <summary>
    private void On_FishingUseTorpedoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingUseTorpedoResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingUseTorpedoResponse, this, pack);
    }
    /// <summary>
    /// 玩家捕获boss鱼响应
    /// <summary>
    private void On_CatchBossFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<CatchBossFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.CatchBossFishResponse, this, pack);
    }
    /// <summary>
    /// 解锁炮台等级提示返回
    /// <summary>
    private void On_UnlockBatteryLevelHintResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UnlockBatteryLevelHintResponse>();
        //MessageBox.Show("升级到" + pack.nextLevel + "倍炮所需" + pack.cost + "钻石你确定升级吗？", null, () => { NetMessage.OseeFishing.Req_UnlockBatteryLevelRequest(pack.nextLevel); });
        PlayerData.next_bei = pack.nextLevel;
        UEventDispatcher.Instance.DispatchEvent(UEventName.UnlockBatteryLevelHintResponse, this, pack);
    }
    /// <summary>
    /// 拥有最高炮台等级响应
    /// <summary>
    private void On_PlayerBatteryLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerBatteryLevelResponse>();
        PlayerData.PaoLevel = pack.level;
        //if (pack.level >= 100)
        //{
        //    if (UIBuyuMenu.instance!=null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //    }
            
        //}
        //if (pack.level >= 1000)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[2].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        //if (pack.level >= 10000)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[2].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[3].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        UEventDispatcher.Instance.DispatchEvent(UEventName.PlayerBatteryLevelResponse, this, pack);
    }

    /// <summary>
    /// 解锁炮台等级响应
    /// <summary>
    private void On_UnlockBatteryLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UnlockBatteryLevelResponse>();
        PlayerData.PaoLevel = pack.level;
        //if (pack.level >= 100)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        //if (pack.level >= 1000)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[2].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        //if (pack.level >= 5000)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[2].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[3].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        //if (pack.level >= 10000)
        //{
        //    if (UIBuyuMenu.instance != null)
        //    {
        //        UIBuyuMenu.instance.btn_rooms[1].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[2].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[3].transform.Find("suo").gameObject.SetActive(false);
        //        UIBuyuMenu.instance.btn_rooms[4].transform.Find("suo").gameObject.SetActive(false);
        //    }
        //}
        ////MessageBox.ShowPopMessage("解锁" + pack.level + "倍炮成功"+"获得"+pack.rewardGold.itemNum+"金币奖励UIMessageItemBox");
        //if (pack.rewardGold.itemNum>0)
        //{
        //    UIMessageItemBox tmp = UIMgr.ShowUI(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
        //    Dictionary<int, long> Dictmp = new Dictionary<int, long>();
        //    Dictmp.Add(pack.rewardGold.itemId, pack.rewardGold.itemNum);
        //    tmp.InitItem(Dictmp, 2);
        //}
 
        UEventDispatcher.Instance.DispatchEvent(UEventName.UnlockBatteryLevelResponse, this, pack);
    }
    /// <summary>
    /// 捕捉特殊鱼响应
    /// <summary>
    private void On_CatchSpecialFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<CatchSpecialFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.CatchSpecialFishResponse, this, pack);
    } 
    /// <summary>
    /// 捕鱼加入房间返回
    /// <summary>
    private void On_FishingJoinRoomResponse(NetMsgPack obj)
    {
        //var pack = obj.GetData<FishingJoinRoomResponse>();
        //common.bySessionName = (BY_SESSIONNAME)pack.roomIndex;
        //Debug.Log("pack.roomIndex" + pack.roomIndex);

        ////ByData.nModule = pack.roomIndex;
        //Debug.Log("加入房间返回 UILoadingGame");
        //UIMgr.CloseAll();
        //UIMgr.CloseUI(UIPath.UIMainMenu);
        //UIMgr.CloseUI(UIPath.UIBuyuMenu);

        //UIMgr.DestroyUI(UIPath.UIByChange);
        //ControllerMgr.Instance.WaitForLoading = true;
        //try
        //{      //卸载场景
        //    SceneManager.UnloadSceneAsync("ZBuyuRoom");
        //}
        //catch
        //{
        //}
        ////清除玩家数据
        //common.listPlayer.Clear();
        //Debug.Log("开始加载UILoadingGame");
        //var Go = UIMgr.ShowUISynchronize(UIPath.UILoadingGame);
        //UILoadingGame uILoadingGame = Go.GetComponent<UILoadingGame>();// UIMgr.ShowUISynchronize(UIPath.UILoadingGame).GetComponent<UILoadingGame>();
        //Debug.Log("加载UILoadingGame成功");

        //uILoadingGame.StartLoad("ZBuyuRoom", UIPath.UIByRoomMain);
        //Root3D.Instance.ShowAllObject(true);
        //common.IDRoomFish = BY_SESSION.普通场;

    }
    /// <summary>
    /// 捕鱼玩家信息返回
    /// <summary>
    private void On_FishingPlayerInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingPlayerInfoResponse>();
        Debug.Log("pack.playerInfo" + pack.playerInfo.playerId);
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingPlayerInfoResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼玩家列表信息返回
    /// <summary>
    private void On_FishingPlayersInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingPlayersInfoResponse>();
        for (int i = 0; i < pack.playerInfos.Count; i++)
        {
            Debug.Log("pack.playerInfo" + pack.playerInfos[i].playerId);
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingPlayersInfoResponse, this, pack);
    }
 
    /// <summary>
    /// 捕鱼退出房间返回
    /// <summary>
    private void On_FishingExitRoomResponse(NetMsgPack obj)
    {
        ////测试
        //int mm = Random.Range(0, 3);
        //if (mm == 0)
        //{
        //    NetMessage.OseeFishing.Req_FishingJoinRoomRequest(Random.Range(1, 5));
        //}

        //else
        //{
        //    NetMessage.BigAward.Req_FishingGrandPrixJoinRoomRequest();
        //}
        //return;
        var pack = obj.GetData<FishingExitRoomResponse>();
      
        //NetMessage.OseeFishing.Req_FishingJoinRoomRequest(1);
        //return;
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingExitRoomResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼获取宝藏返回
    /// <summary>
    private void On_FishingGetTreasureResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingGetTreasureResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingGetTreasureResponse, this, pack);
    }
    /// <summary> 
    /// 捕鱼获取房间任务奖励返回
    /// <summary>
    private void On_FishingGetRoomTaskRewardResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingGetRoomTaskRewardResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingGetRoomTaskRewardResponse, this, pack);
    } 
    /// <summary>
    ///捕鱼获取房间任务列表返回
    /// <summary>  
    private void On_FishingRoomTaskListResponse(NetMsgPack obj)
    {
                                                                                                                                                                                                                    var pack = obj.GetData<FishingRoomTaskListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingRoomTaskListResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼玩家升级返回
    /// <summary>
    private void On_FishingLevelUpResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingLevelUpResponse>();
        // objPlayer[common.listPlayer[PlayerData.PlayerId].pos].txt_paoLevel.text = pack.level.ToString();
        PlayerData.Lv= pack.level;
        UIUpLevel go = UIMgr.ShowUISynchronize(UIPath.UIUpLevel).GetComponent<UIUpLevel>();
        go.Init(pack);
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingLevelUpResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼改变炮台外观返回
    /// <summary>
    private void On_FishingChangeBatteryViewResponse(NetMsgPack obj)
    {
        //var pack = obj.GetData<FishingChangeBatteryViewResponse>();
        //if (pack.playerId == PlayerData.PlayerId)
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
        //PlayerData.ChangePlayerPaoTai(pack.playerId, pack.viewIndex);
        //Debug.Log("pack.viewIndex"+pack.viewIndex);
        //UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChangeBatteryViewResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼改变炮台等级返回
    /// <summary>
    private void On_FishingChangeBatteryLevelResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChangeBatteryLevelResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChangeBatteryLevelResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼改变炮台倍数返回
    /// <summary>
    private void On_FishingChangeBatteryMultResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingChangeBatteryMultResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChangeBatteryMultResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼发射子弹返回
    /// <summary>
    private void On_FishingFireResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingFireResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingFireResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼击中鱼类返回
    /// <summary>
    private void On_FishingFightFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingFightFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingFightFishResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼刷新房间鱼类返回
    /// <summary>
    private void On_FishingRefreshFishesResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingRefreshFishesResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingRefreshFishesResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼同步返回
    /// <summary>
    private void On_FishingSynchroniseResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingSynchroniseResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingSynchroniseResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼使用技能返回
    /// <summary>
    private void On_FishingUseSkillResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingUseSkillResponse>();

        //
        //NetMessage.OseeFishing.Req_FishingUseSkillRequest((int)9);
        //return;
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingUseSkillResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼完成房间目标返回
    /// <summary>
    private void On_FishingFinishRoomGoalResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingFinishRoomGoalResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingFinishRoomGoalResponse, this, pack);
    }
    /// <summary>
    /// 捕鱼机器人发射子弹返回
    /// <summary>
    private void On_FishingRobotFireResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingRobotFireResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingRobotFireResponse, this, pack);
    }
    /// <summary>
    /// 一键领取所有已完成的每日奖励响应
    /// <summary>
    private void On_OneKeyGetDailyTaskRewardsResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<OneKeyGetDailyTaskRewardsResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.OneKeyGetDailyTaskRewardsResponse, this, pack);
    }
    /// <summary>
    /// 获取捕鱼场次信息响应
    /// <summary>
    private void On_FishingGetFieldInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingGetFieldInfoResponse>();
        common.fieldInfos = pack.fieldInfos;
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingGetFieldInfoResponse, this, pack);
    }
    /// <summary>
    /// 使用boss号角响应
    /// <summary>
    private void On_UseBossBugleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseBossBugleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseBossBugleResponse, this, pack);
    }
    /// <summary> 
    /// boss号角使用计时响应
    /// <summary>
    private void On_GetBossBugleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetBossBugleResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetBossBugleResponse, this, pack);
    }   

    /// <summary>
    /// 是否在捕鱼房间内响应
    /// <summary>
    private void On_IsInFishingRoomResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<IsInFishingRoomResponse>();
        try
        {
            for (int i = commonLoad._AllDieMoneyPerfect.Count - 1; i >= 0; i--)
            {
                commonLoad._AllDieMoneyPerfect[i].gameObject.SetActive(false);
            }
            commonLoad._AllDieMoneyPerfect.Clear();
        }
        catch
        {
        }
        if (pack.@in == true)
        {
            if (common3._UIFishingInterface != null)//在捕鱼房间
            {
                common3._UIFishingInterface.ReconnectionInRoom();
            }
        }
        else
        {
            //卸载场景
            try
            {
                UIExitGame uiExitGame = UIMgr.ShowUISynchronize(UIPath.UIExitGame).GetComponent<UIExitGame>();
                uiExitGame.StartLoad("ZBuyuRoom", UIPath.UIMainMenu);
            }
            catch
            {
                UIMgr.ShowUI(UIPath.UIMainMenu);
            }
            common3._UIFishingInterface = null;
            UIMgr.CloseAllwithOutTwo(UIPath.UIMainMenu, UIPath.UIMessageBox);
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.IsInFishingRoomResponse, this, pack);
        //var pack = obj.GetData<IsInFishingRoomResponse>();
        //if (pack.@in == true)
        //{
        //    if (common3._UIFishingInterface != null)//在捕鱼房间
        //    {
        //        common3._UIFishingInterface.ClearDestory();
        //        common3._UIFishingInterface.Req_FishingReactiveRequest();
        //    }
        //}
        //else
        //{
        //    //卸载场景
        //    try
        //    {
        //        UIExitGame uiExitGame = UIMgr.ShowUISynchronize(UIPath.UIExitGame).GetComponent<UIExitGame>();
        //        uiExitGame.StartLoad("ZBuyuRoom", UIPath.UIMainMenu);
        //    }
        //    catch
        //    {
        //    }
        //    common3._UIFishingInterface = null;
        //    UIMgr.CloseAllwithOut(UIPath.UIMainMenu);
        //    //if (UINiuRoomMain.Instance.gameObject.activeSelf)
        //    //{
        //    //    UIMgr.CloseUI(UIPath.UIByRoomMain);
        //    //    UIMgr.CloseUI(UIPath.UIByChange);
        //    //    //UIMgr.CloseUI(UIPath.UIByGrandPrix);
        //    //}
        //    //else
        //    //{
        //    //    UIMgr.ShowUI(UIPath.UIMainMenu);
        //    //    UIMgr.CloseUI(UIPath.UIByRoomMain);
        //    //    UIMgr.CloseUI(UIPath.UIByChange);
        //    //}
        //}
        //UEventDispatcher.Instance.DispatchEvent(UEventName.IsInFishingRoomResponse, this, pack);
    }
    /// <summary>
    /// 钻头击中鱼响应
    /// <summary>
    private void On_BitFightFishResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BitFightFishResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.BitFightFishResponse, this, pack);
    }
    /// <summary>
    /// 第五房间列表响应
    /// <summary>
    private void On_FishingRoomListResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<FishingRoomListResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.FishingRoomListResponse, this, pack);
    }

    /// <summary>
    /// VIP换座响应
    /// <summary>
    //private void On_FishingChallengeChangeSeatResponse(NetMsgPack obj)
    //{
    //    var pack = obj.GetData<FishingChallengeChangeSeatResponse>();
    //    UEventDispatcher.Instance.DispatchEvent(UEventName.FishingChallengeChangeSeatResponse, this, pack);
    //}

}
