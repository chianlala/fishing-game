using System;
using System.Collections.Generic;
using com.maple.common.login.proto;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
using CoreGame;
using Game.UI;
using JEngine.Core;
using ProtoBuf;
using UnityEngine;
using UnityEngine.EventSystems;

namespace NetMessage
{
    /// <summary>
    /// JEngine's UI
    /// </summary>
    public static class BigAward 
    {
        /// <summary>
        /// 大奖赛是否开赛请求
        /// </summary>
        public static void Req_FishingGrandPrixStartRequest(long playerid)
        {
            var pack = new FishingGrandPrixStartRequest();
            pack.playerId = playerid;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_START_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛用户信息请求
        /// </summary>
        public static void Req_FishingGrandPrixPlayerInfoRequest(long playerId)
        {
            var pack = new FishingGrandPrixPlayerInfoRequest();
            pack.playerId = playerId.ToString();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FINSHING_GRAND_PRIX_PLAYER_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛退出房间请求
        /// </summary>
        public static void Req_FishingGrandPrixQuitRequest()
        {
            var pack = new FishingGrandPrixQuitRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FINSHING_GRAND_PRIX_QUIT_REQUEST, pack);
        }
        public static void Req_FishingGrandPrixPropsUseStateSyncRequest()
        {
            var pack = new FishingGrandPrixPropsUseStateSyncRequest();
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_GRAND_PRIX_PROPS_USE_STATE_SYNC_REQUEST, pack);
        }
        
        public static void Req_FishingGrandPrixSyncLockRequest(long userId, long fishId, long fishId1, long fishId2)
        {
            var pack = new FishingSyncLockRequest();
            pack.userId = userId;
            pack.fishId = fishId;
            pack.fishId1 = fishId1;
            pack.fishId2 = fishId2;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_GRANDPRIX_SYNC_LOCK_REQUEST, pack);
        }
        /// <summary>
        /// 二次伤害杀死鱼请求
        /// </summary>
        /// <param name="fishds"> 鱼ids</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingDoubleKillFishRequest(List<long> fishds, long userId)
        {
            //这场次没有
        }
        /// <summary>
        /// 大奖赛用户信息请求
        /// </summary>
        /// <param name="playerId">equired string playerId = 1;</param>
        public static void Req_FishingGrandPrixPlayerInfoRequest(string playerId)
        {
            var pack = new FishingGrandPrixPlayerInfoRequest();
            pack.playerId = playerId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FINSHING_GRAND_PRIX_PLAYER_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛排名请求
        /// </summary>
        /// <param name="rankType"> 排名类型 1：周排名   2：日排名</param>
        /// <param name="pageCurrent">当前页码</param>
        /// <param name="pageSize"> 单页大小</param>
        /// <param name="total"> 数据总数</param>
        public static void Req_FishingGrandPrixRankRequest(int rankType, int pageCurrent, int pageSize, int total)
        {
            var pack = new FishingGrandPrixRankRequest();
            pack.rankType = rankType;
            pack.pageCurrent = pageCurrent;
            pack.pageSize = pageSize;
            pack.total = total;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FINSHING_GRAND_PRIX_RANK_REQUEST, pack);
        }
        /// <summary>
        /// 加入捕鱼挑战赛房间请求
        /// </summary>
        public static void Req_FishingGrandPrixJoinRoomRequest()
        {
            var pack = new FishingGrandPrixJoinRoomRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_JOIN_ROOM_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼改变炮台外观请求
        /// </summary>
        /// <param name="targetViewIndex"> 目标外观序号</param>
        public static void Req_FishingGrandPrixChangeBatteryViewRequest(int targetViewIndex)
        {
            var pack = new FishingGrandPrixChangeBatteryViewRequest();
            pack.targetViewIndex = targetViewIndex;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_CHANGE_BATTERY_VIEW_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼改变炮台等级请求
        /// </summary>
        /// <param name="targetLevel"> 目标等级</param>
        public static void Req_FishingGrandPrixChangeBatteryLevelRequest(int targetLevel)
        {
            var pack = new FishingGrandPrixChangeBatteryLevelRequest();
            pack.targetLevel = targetLevel;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_CHANGE_BATTERY_LEVEL_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼发射子弹请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="angle"> 子弹角度</param>
        public static void Req_FishingGrandPrixFireRequest(long fireId, long fishId, float angle)
        {
            var pack = new FishingGrandPrixFireRequest();
            pack.fireId = fireId;
            pack.fishId = fishId;
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_FIRE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼击中鱼类请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        public static void Req_FishingGrandPrixFightFishRequest(long fireId, long fishId)
        {
            var pack = new FishingGrandPrixFightFishRequest();
            pack.fireId = fireId;
            //for (int i = 0; i < fishId.Count; i++)
            //    pack.fishId.Add(fishId[i]);
            pack.fishId = fishId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛场机器人命中鱼请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="robotId"> 机器人id</param>
        public static void Req_FishingGrandPrixRobotFightFishRequest(long fireId, long fishId, long robotId)
        {
            var pack = new FishingGrandPrixRobotFightFishRequest();
            pack.fireId = fireId;
            pack.fishId = fishId;
            pack.robotId = robotId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_GRANDPRIX_ROBOT_FIGHT_FISH_REQUEST, pack);
        }

        /// <summary>
        /// 捕鱼同步请求
        /// </summary>
        public static void Req_FishingGrandPrixSynchroniseRequest()
        {
            var pack = new FishingGrandPrixSynchroniseRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_SYNCHRONISE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼重新激活请求
        /// </summary>
        public static void Req_FishingGrandPrixReactiveRequest()
        {
            var pack = new FishingGrandPrixReactiveRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_REACTIVE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingGrandPrixUseSkillRequest(int skillId)
        {
            var pack = new FishingGrandPrixUseSkillRequest();
            pack.skillId = skillId;
            if (PlayerData.firstoperating == false)
            {
                PlayerData.firstoperating = true;
            }
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_USE_SKILL_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingGrandPrixUseSkillRequest(int skillId, int routeId)
        {
            var pack = new FishingGrandPrixUseSkillRequest();
            pack.skillId = skillId;
            //pack.routeId = routeId;
            if (PlayerData.firstoperating == false)
            {
                PlayerData.firstoperating = true;
            }
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_USE_SKILL_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕捉到特殊鱼请求
        /// </summary>
        /// <param name="fishIds"> 特殊鱼所影响的目标鱼id</param>
        /// <param name="specialFishId"> 特殊鱼id</param>
        /// <param name="playerId"> 玩家id</param>
        public static void Req_FishingGrandPrixCatchSpecialFishRequest(long specialFishId, List<long> fishIds,long playerId)
        {
            var pack = new FishingGrandPrixCatchSpecialFishRequest();
            for (int i = 0; i < fishIds.Count; i++)
                pack.fishIds.Add(fishIds[i]);
            pack.specialFishId = specialFishId;
            pack.playerId = playerId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_CATCH_SPECIAL_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 使用boss号角请求
        /// </summary>
        public static void Req_FishingGrandPrixUseBossBugleRequest()
        {
            var pack = new FishingGrandPrixUseBossBugleRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_GRAND_PRIX_USE_BOSS_BUGLE_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛同步电磁炮请求
        /// </summary>
        /// <param name="fishId"> 锁定目标id</param>
        public static void Req_GrandPrixUseEleRequest(long fishId)
        {
            var pack = new GrandPrixUseEleRequest();
            pack.fishId = fishId;
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_USE_ELE_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛同步黑洞炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_GrandPrixUseBlackRequest(float x, float y)
        {
            var pack = new GrandPrixUseBlackRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_USE_BLACK_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛同步鱼雷炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_GrandPrixUseTroRequest(float x, float y)
        {
            var pack = new GrandPrixUseTroRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_USE_TRO_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛同步钻头请求
        /// </summary>
        /// <param name="angle"> 角度</param>
        public static void Req_GrandPrixUseBitRequest(float angle)
        {
            var pack = new GrandPrixUseBitRequest();
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_USE_BIT_REQUEST, pack);
        }
        /// <summary>
        /// 拉取排名和奖励请求
        /// 排名类型 0：日排名 1：周排名
        /// </summary>
        public static void Req_RankRewordRequest(int varrankType)
        {
            var pack = new RankRewordRequest();
            pack.rankType = varrankType;
            common.SendMessage((int)OseeMsgCode.C_S_RANK_REWORD_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛结束请求
        /// </summary>
        public static void Req_GrandPrixEndRequest()
        {
            var pack = new FishingGrandPrixEndRequest();
            pack.playerId = PlayerData.PlayerId;
            pack.battueryLevel = PlayerData.PaoLevel;
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_END_REQUEST, pack);
        }
        /// <summary>
        /// 大奖赛钻头击中鱼请求
        /// </summary>
        /// <param name="fishId"> 目标id</param>
        public static void Req_GrandPrixBitFightFishRequest(List<long> fishId)
        {
            var pack = new GrandPrixBitFightFishRequest();
            for (int i = 0; i < fishId.Count; i++)
                pack.fishId.Add(fishId[i]);
            common.SendMessage((int)OseeMsgCode.C_S_GRANDPRIX_BIT_FIGHT_FISH_REQUEST, pack);
        }
    }
}
   
