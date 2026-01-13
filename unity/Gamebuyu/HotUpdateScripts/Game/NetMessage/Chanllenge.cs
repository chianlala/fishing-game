using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;
using System.Collections.Generic;
using UnityEngine;

namespace NetMessage
{
    public abstract class Chanllenge
    {
        /// <summary>
        /// 挑战赛房间列表请求
        /// </summary>
        public static void Req_FishingChallengeRoomListRequest(int roomType)
        {
            var pack = new FishingChallengeRoomListRequest();
            pack.roomType = roomType;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_ROOM_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 创建捕鱼挑战赛房间请求
        /// </summary>
        /// <param name="roomPassword"> 房间密码，可为空</param>
        public static void Req_FishingChallengeCreateRoomRequest(string roomPassword)
        {
            var pack = new FishingChallengeCreateRoomRequest();            
            pack.roomPassword = roomPassword;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CREATE_ROOM_REQUEST, pack);
        }
        public static void Req_FishingChallengeUseTorpedoRequest(List<FishingChallengeUseTorpedo> torpedoes)//float angle,int torpedoId, int torpedoNum
        {
            var pack = new FishingChallengeUseTorpedoRequest();
            //pack.torpedoId = torpedoId;
            //pack.torpedoNum = torpedoNum;
            //pack.angle = angle;
            pack.fishingChallengeUseTorpedo.AddRange(torpedoes);
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_Challenge_USE_TORPEDO_REQUEST, pack);
        }
        public static void Req_FishingChallengeSyncLockRequest(long userId, long fishId, long fishId1, long fishId2)//float angle,int torpedoId, int torpedoNum
        {
            var pack = new FishingChallengeSyncLockRequest();
     
            pack.userId = userId;
            pack.fishId = fishId;
            pack.fishId1 = fishId1;
            pack.fishId2 = fishId2;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_CHALLENGE_SYNC_LOCK_REQUEST, pack);
        }
        public static void Req_FishingChallengePropsUseStateSyncRequest()
        {
            var pack = new FishingChallengePropsUseStateSyncRequest();

            common.SendMessage((int)OseeMsgCode.C_S_FISHING_CHALLENGE_PROPS_USE_STATE_SYNC_REQUEST, pack);
        }
        
        /// <summary>
        /// 大奖赛机器人命中鱼请求
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
        /// 加入捕鱼挑战赛房间请求
        /// </summary>
        /// <param name="roomCode"> 房间号</param>
        /// <param name="roomPassword"> 房间密码，无密码就填入空字符串</param>
        public static void Req_FishingChallengeJoinRoomRequest(int roomType, int roomCode, string roomPassword)
        {
            var pack = new FishingChallengeJoinRoomRequest();
            pack.roomType = roomType;
            pack.roomCode = roomCode;
            pack.roomPassword = roomPassword;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_JOIN_ROOM_REQUEST, pack);
        }
        /// <summary>
        /// 退出捕鱼挑战赛房间请求
        /// </summary>
        public static void Req_FishingChallengeExitRoomRequest()
        {
            if (PlayerData._TwoAttackNum>0)
            {
                MessageBox.ShowPopOneMessage("技能鱼释放技能中,暂时无法退出！");
                return;
            }
            var pack = new FishingChallengeExitRoomRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_EXIT_ROOM_REQUEST, pack, true);
        }
        /// <summary>
        /// 快速加入房间请求
        /// </summary>
        public static void Req_FishingChallengeQuickJoinRequest(int roomType)
        {
            var pack = new FishingChallengeQuickJoinRequest();
            pack.roomType = roomType;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_QUICK_JOIN_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼改变炮台外观请求
        /// </summary>
        /// <param name="targetViewIndex"> 目标外观序号</param>
        public static void Req_FishingChallengeChangeBatteryViewRequest(int targetViewIndex)
        {
            var pack = new FishingChallengeChangeBatteryViewRequest();
            pack.targetViewIndex = targetViewIndex;
            
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_VIEW_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼改变炮台等级请求
        /// </summary>
        /// <param name="targetLevel"> 目标等级</param>
        public static void Req_FishingChallengeChangeBatteryLevelRequest(int targetLevel)
        {
            var pack = new FishingChallengeChangeBatteryLevelRequest();
            pack.targetLevel = targetLevel;
            //Debug.Log("pack.targetLevel"+ pack.targetLevel);
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_LEVEL_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼发射子弹请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="angle"> 子弹角度</param>
        public static void Req_FishingChallengeFireRequest(long fireId, long fishId, float angle)
        {
            var pack = new FishingChallengeFireRequest();           
            pack.fireId = fireId;
            pack.fishId = fishId;
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_FIRE_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕鱼击中鱼类请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        public static void Req_FishingChallengeFightFishRequest(long fireId, long fishId)
        {
            var pack = new FishingChallengeFightFishRequest();
            pack.fireId = fireId;
            //for (int i = 0; i < fishId.Count; i++)
            //    pack.fishId.Add(fishId[i]);
            pack.fishId = fishId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼同步请求
        /// </summary>
        public static void Req_FishingChallengeSynchroniseRequest()
        {
            var pack = new FishingChallengeSynchroniseRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_SYNCHRONISE_REQUEST, pack);
        }
        /// <summary>
        /// 捕鱼重新激活请求
        /// </summary>
        public static void Req_FishingChallengeReactiveRequest()
        {
            var pack = new FishingChallengeReactiveRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_REACTIVE_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingChallengeUseSkillRequest(int skillId)
        {
            var pack = new FishingChallengeUseSkillRequest();
            pack.skillId = skillId;
            if (PlayerData.firstoperating == false)
            {
                PlayerData.firstoperating = true;
            }
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_USE_SKILL_REQUEST, pack, true);
        }
        /// <summary>
        /// 捕鱼使用技能请求
        /// </summary>
        /// <param name="skillId"> 技能id</param>
        public static void Req_FishingChallengeUseSkillRequest(int skillId,int routeId)
        {
            var pack = new FishingChallengeUseSkillRequest();
            pack.skillId = skillId;
            pack.routeId = routeId;
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
        public static void Req_FishingChallengeCatchSpecialFishRequest(long specialFishId, List<long> fishIds,long playerId)
        {
            var pack = new FishingChallengeCatchSpecialFishRequest();
            for (int i = 0; i < fishIds.Count; i++)
                pack.fishIds.Add(fishIds[i]);
            pack.specialFishId = specialFishId;
            pack.playerId = playerId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CATCH_SPECIAL_FISH_REQUEST, pack, true);
        }
        /// <summary>
        /// 开启奖池请求
        /// </summary>
        /// <param name="type"> 奖池类型 1：小奖池 2：大奖池</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_OpenJcRequest(int type, long userId)
        {
            var pack = new OpenJcRequest();
            pack.type = type;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_OPEN_JC_REQUEST, pack);
        }
        /// <summary>
        /// 获取奖池金额请求
        /// </summary>
        /// <param name="type"> 奖池类型 1：小奖池 2：大奖池</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_GetJcAllMoneyRequest(int type, long userId)
        {
            var pack = new GetJcAllMoneyRequest();
            pack.type = type;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_JC_ALL_MONEY_REQUEST, pack);
        }
  
        /// 获取奖池抽取记录请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        public static void Req_GetJcAllRecordRequest(long userId)
        {
            var pack = new GetJcAllRecordRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_JC_RECORD_REQUEST, pack);
        }
        /// <summary>
        /// 使用boss号角请求
        /// </summary>
        public static void Req_FishingChallengeUseBossBugleRequest()
        {
            var pack = new FishingChallengeUseBossBugleRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_USE_BOSS_BUGLE_REQUEST, pack, true);
        }
        /// <summary>
        /// 龙晶战场同步电磁炮请求
        /// </summary>
        /// <param name="fishId"> 锁定目标id</param>
        public static void Req_ChallengeUseEleRequest(long fishId)
        {
            var pack = new ChallengeUseEleRequest();
            pack.fishId = fishId;
            common.SendMessage((int)OseeMsgCode.C_S_CHALLENGE_USE_ELE_REQUEST, pack);
        }
        /// <summary>
        /// 龙晶战场同步黑洞炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_ChallengeUseBlackRequest(float x, float y)
        {
            var pack = new ChallengeUseBlackRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_CHALLENGE_USE_BLACK_REQUEST, pack);
        }
        /// <summary>
        /// 龙晶战场同步鱼雷炮请求
        /// </summary>
        /// <param name="x"> 坐标x</param>
        /// <param name="y"> 坐标y</param>
        public static void Req_ChallengeUseTroRequest(float x, float y)
        {
            var pack = new ChallengeUseTroRequest();
            pack.x = x;
            pack.y = y;
            common.SendMessage((int)OseeMsgCode.C_S_CHALLENGE_USE_TRO_REQUEST, pack);
        }
        /// <summary>
        /// 龙晶战场同步钻头请求
        /// </summary>
        /// <param name="angle"> 角度</param>
        public static void Req_ChallengeUseBitRequest(float angle)
        {
            var pack = new ChallengeUseBitRequest();
            pack.angle = angle;
            common.SendMessage((int)OseeMsgCode.C_S_CHALLENGE_USE_BIT_REQUEST, pack);
        }
        /// <summary>
        /// 挑战赛钻头击中鱼请求
        /// </summary>
        /// <param name="fishId"> 目标id</param>
        public static void Req_ChallengeBitFightFishRequest(List<long> fishId)
        {
            var pack = new ChallengeBitFightFishRequest();
            for (int i = 0; i < fishId.Count; i++)
                pack.fishId.Add(fishId[i]);
            common.SendMessage((int)OseeMsgCode.C_S_CHALLENGE_BIT_FIGHT_FISH_REQUEST, pack);
        }

        /// <summary>
        /// 龙晶场二次伤害杀死鱼请求
        /// </summary>
        /// <param name="fishds"> 鱼ids</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingChallengeDoubleKillFishRequest(List<long> fishds, long userId)
        {
            var pack = new FishingChallengeDoubleKillFishRequest();
            for (int i = 0; i < fishds.Count; i++)
                pack.fishds.Add(fishds[i]);
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_CHALLENGE_DOUBLE_KILL_FISH_REQUEST, pack);
        }

        /// <summary>
        /// 挑战赛机器人命中鱼请求
        /// </summary>
        /// <param name="fireId"> 子弹id</param>
        /// <param name="fishId"> 目标id</param>
        /// <param name="robotId"> 机器人id</param>
        public static void Req_FishingChallengeRobotFightFishRequest(long fireId, long fishId, long robotId)
        {
            var pack = new FishingChallengeRobotFightFishRequest();
            pack.fireId = fireId;
            pack.fishId = fishId;
            pack.robotId = robotId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_FISHING_CHALLENGE_ROBOT_FIGHT_FISH_REQUEST, pack);
        }
        /// <summary>
        /// 龙晶场二次伤害结束请求
        /// </summary>
        /// <param name="winMoney"> 获取的金币数</param>
        /// <param name="userId"> 用户id</param>
        public static void Req_FishingChallengeDoubleKillEndRequest(long winMoney, long userId, long mult, string fishname)
        {
            var pack = new FishingChallengeDoubleKillEndRequest();
            pack.winMoney = winMoney;
            pack.userId = userId;
            pack.mult = mult;
            pack.fishName = fishname;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_CHALLENGE_DOUBLE_KILL_END_REQUEST, pack);
        }
    }
}

