using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace NetMessage
{ 
    public abstract class OseeFishing
    {
        /// <summary>
        /// 使用boss号角请求
        /// </summary>
        public static void Req_UseBossBugleRequest()
        {
            var pack = new UseBossBugleRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_USE_BOSS_BUGLE_REQUEST, pack);
        }

        public static void Req_GetBossBugleRequest()
        {
            var pack = new GetBossBugleRequest();
            pack.userId = PlayerData.PlayerId;
            common.SendMessage((int)OseeMsgCode.C_S_GET_BOSS_BUGLE_REQUEST, pack);
        }
        /// <summary>
        /// 第五房间列表请求
        /// </summary>
        public static void Req_FishingRoomListRequest()
        {
            var pack = new FishingRoomListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_ROOM_LIST_REQUEST, pack);
        }
        public static void Req_FishingSyncLockRequest(long userId, long fishId, long fishId1, long fishId2)
        {
            var pack = new FishingSyncLockRequest();
            pack.userId = userId;
            pack.fishId = fishId;
            pack.fishId1 = fishId1;
            pack.fishId2 = fishId2;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_SYNC_LOCK_REQUEST, pack);
        }

        /// <summary>
        /// 改变狂暴倍数请求
        /// </summary>
        /// <param name="mult"> 倍数</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingChangeCritMultRequest(int mult, long userId)
        {
            var pack = new FishingChangeCritMultRequest();
            pack.mult = mult;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHANGE_CRIT_MULT_REQUEST, pack);
        }
        /// <summary>
        /// 加入第五房间请求
        /// </summary>
        /// <param name="roomCode"> 房间号</param>
        public static void Req_FishingJoinRoomByRoomCodeRequest(int roomCode)
        {
            var pack = new FishingJoinRoomByRoomCodeRequest();
            pack.roomCode = roomCode;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_JOIN_ROOM_BY_ROOM_CODE_REQUEST, pack);
        }
      
        /// <summary>
        /// 切换第五房间座位请求
        /// </summary>
        /// <param name="seat"> 座位序号</param>
        public static void Req_FishingChangeSeatRequest(int seat)
        {
            var pack = new FishingChangeSeatRequest();
            pack.seat = seat;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHANGE_SEAT_REQUEST, pack);
        }

        /// <summary>
        /// 每日任务列表请求
        /// </summary>
        public static void Req_DailyTaskListRequest()
        {
            var pack = new DailyTaskListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_DAILY_TASK_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 是否加入部落请求
        /// </summary>
        /// <param name="userId">用户id</param>
        public static void Req_IsJoinTribeRequest(long userId)
        {
            var pack = new IsJoinTribeRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_IS_JOIN_TRIBE_REQUEST, pack);
        }
        /// <summary>
        /// 领取每日活跃奖励请求
        /// </summary>
        /// <param name="activeLevel"> 要领取奖励的活跃等级</param>
        public static void Req_GetDailyActiveRewardRequest(int activeLevel)
        {
            var pack = new GetDailyActiveRewardRequest();
            pack.activeLevel = activeLevel;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_DAILY_ACTIVE_REWARD_REQUEST, pack);
        }

        //static int varm1 = 0;
        /// <summary>
        /// 捕鱼加入房间请求
        /// </summary>
        /// <param name="roomIndex"> 房间序号 1-3</param>
        public static void Req_FishingJoinRoomRequest(int roomIndex)
        {
            //varm1++;
            //Debug.Log("测试加入请求" + varm1);
            var pack = new FishingJoinRoomRequest();
            pack.roomIndex = roomIndex;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_JOIN_ROOM_REQUEST, pack);
        }
        /// <summary>
        /// 获取捕鱼场次信息请求
        /// </summary>
        public static void Req_FishingGetFieldInfoRequest()
        {
            var pack = new FishingGetFieldInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GET_FIELD_INFO_REQUEST, pack);
        }

        /// <summary>
        /// 捕鱼玩家信息请求
        /// </summary>
        /// <param name="seat"> 玩家座位 -1:自己</param>
        public static void Req_FishingPlayerInfoRequest(int seat)
        {
            var pack = new FishingPlayerInfoRequest();
            pack.seat = seat;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_PLAYER_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼玩家列表信息请求
        /// </summary>
        public static void Req_FishingPlayersInfoRequest()
        {
            var pack = new FishingPlayersInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_PLAYERS_INFO_REQUEST, pack);
        }
        //static int varm = 0;
        /// <summary>
        /// 捕鱼退出房间请求
        /// </summary>
        public static void Req_FishingExitRoomRequest()
        {     
            var pack = new FishingExitRoomRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_EXIT_ROOM_REQUEST, pack,true);
        }
        /// <summary>
        /// 捕鱼获取宝藏请求
        /// </summary>
        /// <param name="index"> 宝藏序号 1-6</param>
        /// <param name="drawIndex"> 抽取序号 0-5</param>
        public static void Req_FishingGetTreasureRequest(int index, int drawIndex)
        {
            var pack = new FishingGetTreasureRequest();
            pack.index = index;
            pack.drawIndex = drawIndex;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_GET_TREASURE_REQUEST, pack, true);
        }
        /// <summary>
        /// 钻头击中鱼请求
        /// </summary>
        /// <param name="fishId"> 目标id</param>
        public static void Req_BitFightFishRequest(List<long> fishId)
        {
            var pack = new BitFightFishRequest();
            for (int i = 0; i < fishId.Count; i++)
                pack.fishId.Add(fishId[i]);
            common.SendMessage((int)OseeMsgCode.C_S_BIT_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 二次伤害杀死鱼请求
        /// </summary>
        /// <param name="fishds"> 鱼ids</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingDoubleKillFishRequest(List<long> fishds, long userId)
        {
            var pack = new FishingDoubleKillFishRequest();
            for (int i = 0; i < fishds.Count; i++)
                pack.fishds.Add(fishds[i]);
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_DOUBLE_KILL_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 二次伤害结束请求
        /// </summary>
        /// <param name="winMoney"> 获取的金币数</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingDoubleKillEndRequest(long winMoney, long userId,long mult,string fishName)
        {
            var pack = new FishingDoubleKillEndRequest();
            pack.winMoney = winMoney;
            pack.userId = userId;
            pack.mult = mult; 
            pack.fishName = fishName;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_DOUBLE_KILL_END_REQUEST, pack);
        }

        /// <summary>
        /// 捕鱼获取任务列表请求
        /// </summary>
        public static void Req_FishingRoomTaskListRequest() 
        {
            var pack = new FishingRoomTaskListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_ROOM_TASK_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼获取任务奖励请求
        /// </summary>
        /// <param name="taskId"> 任务序号</param>
        public static void Req_FishingGetRoomTaskRewardRequest(long taskId)
        {
            var pack = new FishingGetRoomTaskRewardRequest();
            pack.taskId = taskId; 
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_GET_ROOM_TASK_REWARD_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼获取任务奖励请求
        /// </summary>
        /// <param name="taskId"> 任务序号</param>
        public static void Req_GetDailyTaskRewardRequest(long taskId)
        {
            var pack = new GetDailyTaskRewardRequest();
            pack.taskId = taskId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_DAILY_TASK_REWARD_REQUEST, pack);
        } 
        
        /// <summary>
        /// 捕鱼改变炮台外观请求
        /// </summary>
        /// <param name="targetViewIndex"> 目标外观序号</param>
        public static void Req_FishingChangeBatteryViewRequest(int targetViewIndex)
        {
            var pack = new FishingChangeBatteryViewRequest();
            pack.targetViewIndex = targetViewIndex;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_VIEW_REQUEST, pack);
        }
        /// <summary>
        /// 切换炮台
        /// </summary>
        /// <param name="targetViewIndex"> 目标外观序号</param>
        public static void Req_changeBatteryViewRequest(int targetViewIndex)
        {
            var pack = new changeBatteryViewRequest();
            pack.targetViewIndex = targetViewIndex;
            common.SendMessage((int)OseeMsgCode.C_S_CHANGE_BBATTERY_VIEW_REQUEST, pack);
        }

        /// <summary>
        /// 捕鱼改变炮台等级请求
        /// </summary>
        /// <param name="targetLevel"> 目标等级</param>
        public static void Req_FishingChangeBatteryLevelRequest(int targetLevel)
        {
            var pack = new FishingChangeBatteryLevelRequest();
            pack.targetLevel = targetLevel;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_LEVEL_REQUEST, pack);
        }
        ///// <summary>
        ///// 捕鱼改变炮台倍数请求
        ///// </summary>
        ///// <param name="targetMult"> 目标倍数</param>
        //public static void Req_FishingChangeBatteryMultRequest(int targetMult)
        //{
        //    var pack = new FishingChangeBatteryMultRequest();
        //    pack.targetMult = targetMult;
        //    commonhot.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_MULT_REQUEST, pack);
        //}
        /// <summary>
        /// 捕鱼发射子弹请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="angle"> 子弹角度</param>
        public static void Req_FishingFireRequest(long fireId,long fishId, float angle)
        {
            var pack = new FishingFireRequest();          
            pack.fireId = fireId;
            pack.fishId = fishId;
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_FIRE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼击中鱼类请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        public static void Req_FishingFightFishRequest(long fireId, long fishId)
        {
            var pack = new FishingFightFishRequest();
            pack.fireId = fireId;
            //for (int i = 0; i < fishId.Count; i++)
            //{
            //    pack.fishId.Add(fishId[i]);
            //}
            pack.fishId= fishId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼同步请求
        /// </summary>
        public static void Req_FishingSynchroniseRequest()
        {
            var pack = new FishingSynchroniseRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_SYNCHRONISE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼机器人击中鱼类请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="robotId"> 机器人id</param>
        public static void Req_FishingRobotFightFishRequest(long fireId, long fishId, long robotId)
        {
            var pack = new FishingRobotFightFishRequest();
            pack.fireId = fireId;
            //for(int i=0;i<fishId.Count;i++)
            //{
            //    pack.fishId.Add(fishId[i]);
            //}
            pack.fishId = fishId;
            pack.robotId = robotId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_ROBOT_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingUseSkillRequest(int skillId)
        {
            var pack = new FishingUseSkillRequest();
            pack.skillId = skillId;
            if (PlayerData.firstoperating == false)
            {
                PlayerData.firstoperating = true;
            }
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_USE_SKILL_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingUseSkillRequest(int skillId,int pathID)
        {
            var pack = new FishingUseSkillRequest();
            pack.skillId = skillId;
            pack.routeId = pathID;
            if (PlayerData.firstoperating == false)
            {
                PlayerData.firstoperating = true;
            }
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_USE_SKILL_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕鱼重新激活请求
        /// </summary>
        public static void Req_FishingReactiveRequest()
        {
            var pack = new FishingReactiveRequest();            
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_REACTIVE_REQUEST, pack, true);
        }    
        /// <summary>
        /// 解锁炮台等级提示请求
        /// </summary>
        public static void Req_UnlockBatteryLevelHintRequest()
        {
            var pack = new UnlockBatteryLevelHintRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_UNLOCK_BATTERY_LEVEL_HINT_REQUEST, pack);
        }
        /// <summary>
        /// 解锁炮台等级请求
        /// </summary>
        /// <param name="level"> 要解锁的炮台等级</param>
        public static void Req_UnlockBatteryLevelRequest(int level)
        {
            var pack = new UnlockBatteryLevelRequest();
            pack.level = level;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_UNLOCK_BATTERY_LEVEL_REQUEST, pack);
        }
        /// <summary>
        /// 
        /// 
        /// 
        /// 
        /// 
        /// 请求
        /// </summary>
        /// <param name="fishIds"> 特殊鱼所影响的目标鱼id</param>
        public static void Req_CatchSpecialFishRequest(long specialFishId, List<long> fishIds,long playerid)
        {
            var pack = new CatchSpecialFishRequest();
            pack.specialFishId = specialFishId;
            pack.playerId = playerid;
            for (int i = 0; i < fishIds.Count; i++)
                pack.fishIds.Add(fishIds[i]);
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_CATCH_SPECIAL_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 同步黑洞炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_UseBlackRequest(float x, float y)
        {
            var pack = new UseBlackRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_USE_BLACK_REQUEST, pack);
        }
        /// <summary>
        /// 同步鱼雷炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_UseTroRequest(float x, float y)
        {
            var pack = new UseTroRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_USE_TRO_REQUEST, pack);
        }
        /// <summary>
        /// 同步钻头请求
        /// </summary>
        /// <param name="angle"> 角度</param>
        public static void Req_UseBitRequest(float angle)
        {
            var pack = new UseBitRequest();
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_USE_BIT_REQUEST, pack);
        }

        /// <summary>
        /// 同步闪电炮锁定鱼请求
        /// </summary>
        /// <param name="fishIds"> 特殊鱼所影响的目标鱼id</param>
        public static void Req_UseEleRequest(long fishId)
        {
            var pack = new UseEleRequest();  
            pack.fishId = fishId;     
            common.SendMessage((int)OseeMsgCode.C_S_USE_ELE_REQUEST, pack);
        }
        /// <summary>
        /// 一键领取所有已完成的每日奖励请求
        /// </summary>
        public static void Req_OneKeyGetDailyTaskRewardsRequest()
        {
            var pack = new OneKeyGetDailyTaskRewardsRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_ONE_KEY_GET_DAILY_TASK_REWARDS_REQUEST, pack);
        }

        //---------------背包
        /// <summary>
        /// 玩家道具信息请求
        /// </summary>
        public static void Req_PlayerPropRequest()
        {
            var pack = new PlayerPropRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_PLAYER_PROP_REQUEST, pack);
        }
        /// <summary>
        /// 玩家获取消息列表
        /// </summary>
        public static void Req_MessageListRequest()
        {
            var pack = new MessageListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_MESSAGE_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家未读消息数量
        /// </summary>
        public static void Req_UnreadMessageCountRequest()
        {
            var pack = new UnreadMessageCountRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_UNREAD_MESSAGE_COUNT_REQUEST, pack);
        }
        /// <summary>
        /// 读取消息
        /// </summary>
        /// <param name="id"> 读取的消息id</param>
        public static void Req_ReadMessageRequest(long id)
        {
            var pack = new ReadMessageRequest();
            pack.id = id;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_READ_MESSAGE_REQUEST, pack);
        }
        /// <summary>
        /// 领取消息附件
        /// </summary>
        /// <param name="id"> 要领取附件的消息id</param>
        public static void Req_ReceiveMessageItemsRequest(long id)
        {
            var pack = new ReceiveMessageItemsRequest();
            pack.id = id;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_RECEIVE_MESSAGE_ITEMS_REQUEST, pack);
        }
        /// <summary>
        /// 删除邮件请求
        /// </summary>
        /// <param name="id"> 要领取附件的消息id</param>
        public static void Req_DeleteMessageRequest(long id)
        {
            var pack = new DeleteMessageRequest();
            pack.id = id;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_DELETE_MESSAGE_REQUEST, pack);
        }
        
        /// <summary>
        /// 使用龙珠请求
        /// </summary>
        /// <param name="torpedoId"> 龙珠id</param>
        /// <param name="torpedoNum"> 龙珠数量</param>
        /// <param name="angle"> 角度</param>
        public static void Req_FishingUseTorpedoRequest(List<FishingUseTorpedo> torpedoes)
        {
            //float angle,int torpedoId, int torpedoNum
            var pack = new FishingUseTorpedoRequest();
            //pack.torpedoId = torpedoId;
            //pack.torpedoNum = torpedoNum;
            //pack.angle = angle;
            pack.fishingUseTorpedo.AddRange(torpedoes);
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_USE_TORPEDO_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼快速开始
        /// </summary>
        public static void Req_QuickStartRequest()
        {
            var pack = new QuickStartRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_QUICK_START_REQUEST, pack);
        }
        /// <summary>
        /// 获取是否在捕鱼房间内
        /// </summary>
        public static void Req_IsInFishingRoomRequest()
        {
            var pack = new IsInFishingRoomRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_IS_IN_FISHING_ROOM_REQUEST, pack);
        }
        /// <summary>
        /// 获取是否在捕鱼房间内
        /// </summary>
        public static void Req_BackgroundSyncRequest()
        {
            var pack = new BackgroundSyncRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BACKGROUND_SYNC_REQUEST, pack);
        }
        /// <summary>
        /// 获取兑换记录请求
        /// </summary>
        /// <param name="pageNo"> 当前页码 从1开始</param>
        /// <param name="pageSize"> 每页数据数量</param>
        public static void Req_LotteryExchangeLogRequest(int pageNo, int pageSize)
        {
            var pack = new LotteryExchangeLogRequest();
            pack.pageNo = pageNo;
            pack.pageSize = pageSize;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_LOTTERY_EXCHANGE_LOG_REQUEST, pack);
        }

        /// <summary>
        /// VIP换座请求
        /// </summary>
        /// <param name="seat"> 座位序号</param>
        public static void Req_FishingChallengeChangeSeatRequest(int seat)
        {
            var pack = new FishingChallengeChangeSeatRequest();
            pack.seat = seat;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_SEAT_REQUEST, pack);
        }
        /// <summary>
        /// 炮台直升请求
        /// </summary>
        public static void Req_BuyBatteryLevelRequest(int type)
        {
            var pack = new BuyBatteryLevelRequest();
            pack.type = type;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BUY_BATTERY_LEVEL_REQUEST, pack);
        }
        /// <summary>
        /// 请求最高炮倍 
        /// </summary> 
        public static void Req_PlayerBatteryLevelRequest()   
        {
            var pack = new PlayerBatteryLevelRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_PLAYER_BATTERY_LEVEL_REQUEST, pack);
        }

        /// <summary>
        /// 召唤一条指定的鱼(测试用)
        /// </summary>
        /// <param name="id"> 房间内鱼的唯一id</param>
        /// <param name="ruleId"> 刷新规则id</param>
        /// <param name="configId">配置id</param>
        /// <param name="routeId">路线id</param>
        /// <param name="lifeTime">存活时间</param>
        /// <param name="safeTimes">安全次数</param>
        /// <param name="createTime">创建时间</param>
        /// <param name="fishType">鱼类型</param>
        /// <param name="isFirst"> 是否第一次刷鱼</param>
        public static void Req_FishInfo(long id, long ruleId, long configId, long routeId, float lifeTime, int safeTimes, long createTime, int fishType, bool isFirst)
        {
            FishInfo pack = new FishInfo();
            pack.id = id;
            pack.ruleId = ruleId;
            pack.configId = configId;
            pack.routeId = routeId;
            pack.lifeTime = lifeTime;
            pack.safeTimes = safeTimes;
            pack.createTime = createTime;
            pack.fishType = fishType;
            pack.isFirst = isFirst;
            common.SendMessage((int)OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST, pack);
        }

        public static long ConvertDateTimeToUtc_13(DateTime _time)
        {
            TimeSpan timeSpan = _time.ToUniversalTime() - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(timeSpan.TotalMilliseconds);
        }
        public static void Req_FishInfo(string fishname)
        {
            FishInfo pack = new FishInfo();
            //string fishname = "";
            foreach (var item in common4.dicFishConfig)
            {
                if (item.Value.name==fishname)
                {
                    //pack.id = 11;
                    pack.ruleId = 116;
                    
                    pack.configId = item.Key;

                    pack.routeId = 10421;
                    pack.lifeTime = 100;
                    pack.safeTimes = 300;

                    pack.createTime = ConvertDateTimeToUtc_13(DateTime.Now);

                    pack.fishType = item.Value.fishType;
                    pack.isFirst = false;
                    common.SendMessage((int)OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST, pack);
                    break;
                }
            }

        }
    }
}

