using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using com.maple.common.login.proto;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.fruit;
using com.maple.game.osee.proto.goldenpig;
using com.maple.game.osee.proto.lobby;
using com.maple.network.proto;
using CoreGame;
using Game.UI;
using JEngine.Core;
using ProtoBuf;
using UnityEngine;
using UnityEngine.EventSystems;
namespace NetMessage
{
    public static class OseeLobby
    {
        /// <summary>
        /// 获取玩家货币请求
        /// </summary>
        public static void Req_PlayerMoneyRequest(int varnum)
        {
            var pack = new PlayerMoneyRequest();
            pack.num = varnum;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_PLAYER_MONEY_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家vip等级请求
        /// </summary>
        public static void Req_VipLevelRequest()
        {
            var pack = new VipLevelRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_VIP_LEVEL_REQUEST, pack);
        }
        public static void Req_PlayerRankRequest(int varRankType, int pageCurrent, int varpageSize, int vartotal)
        {
            var pack = new PlayerRankRequest();
            pack.rankType = varRankType;
            pack.pageCurrent = pageCurrent;
            pack.pageSize = varpageSize;
            pack.total = vartotal;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_PLAYER_RANK_REQUEST, pack);
        }
        /// <summary>
        /// 是否获取部落礼包请求
        /// </summary>
        /// <param name="userId">用户id</param>
        public static void Req_IsTribeGetGiftRequest(long userId)
        {
            var pack = new IsTribeGetGiftRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_IS_GET_TRIBE_GIFT_REQUEST, pack);
        }
        /// <summary>
        /// 获取部落礼包请求
        /// </summary>
        /// <param name="userId">用户id</param>
        public static void Req_GetTribeGiftRequest(long userId)
        {
            var pack = new GetTribeGiftRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_GET_TRIBE_GIFT_REQUEST, pack);
        }

        /// <summary>
        /// 创建部落请求
        /// </summary>
        /// <param name="userId">创建者id</param>
        /// <param name="name">部落名称</param>
        /// <param name="context">部落简介</param>
        /// <param name="vipRestrict">vip等级限制</param>
        /// <param name="levelRestrict">等级限制</param>
        /// <param name="verificationRestrict">验证限制(1.不需要校验 2需审核.)</param>
        /// <param name="headUrl">部落头像</param>
        public static void Req_TribeEsTabLishRequest(long userId, string name, string context, long vipRestrict, long levelRestrict, long verificationRestrict, string headUrl)
        {
            var pack = new TribeEsTabLishRequest();
            pack.userId = userId;
            pack.name = name;
            pack.context = context;
            pack.vipRestrict = vipRestrict;
            pack.levelRestrict = levelRestrict;
            pack.verificationRestrict = verificationRestrict;
            pack.headUrl = headUrl;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_TRIBE_ESTABLISH_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家连击次数请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        /// <param name="num"> 连击次数</param>
        public static void Req_GetFightNumRequest(long userId, long num)
        {
            var pack = new GetFightNumRequest();
            pack.userId = userId;
            pack.num = num;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_FIGHT_NUM_REQUEST, pack);
        }
        public static void Req_KillFishRankRequest(int rankType, int pageCurrent, int pageSize, int total)
        {
            var pack = new KillFishRankRequest();
            pack.rankType = rankType;
            pack.pageCurrent = pageCurrent;
            pack.pageSize = pageSize;
            pack.total = total;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_KILL_FISH_RANK_REQUEST, pack);
        }
        
        /// <summary>
        /// 获取玩家连击榜排名和奖励请求
        /// </summary>
        /// <param name="rankType"> 排名类型 1：周排名   2：日排名  3：月排名</param>
        /// <param name="pageCurrent">当前页码</param>
        /// <param name="pageSize"> 单页大小</param>
        /// <param name="total"> 数据总数</param>
        public static void Req_GetFightRankRequest(int rankType, int pageCurrent, int pageSize, int total)
        {
            var pack = new GetFightRankRequest();
            pack.rankType = rankType;
            pack.pageCurrent = pageCurrent;
            pack.pageSize = pageSize;
            pack.total = total;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_FIGHT_RANK_REQUEST, pack);
        }
        /// <summary>
        /// 转盘中奖全部记录请求
        /// </summary>
        public static void Req_TurnTableAllRequest()
        {
            var pack = new TurnTableAllRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TURN_TABLE_ALL_REQUEST, pack);
        }
        /// <summary>
        /// 转盘中奖用户记录请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        public static void Req_TurnTableUserRequest(long userId)
        {
            var pack = new TurnTableUserRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TURN_TABLE_USER_REQUEST, pack);
        }

        public static void Req_LotteryInfoRequest()
        {
            var pack = new LotteryInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_LOTTERY_INFO_REQUEST, pack);
        }
        public static void Req_GetTribeRequest()
        {
            var pack = new GetTribeRequest();     
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_TRIBE_REQUEST, pack);
        }
        /// <summary>
        /// 获取部落所有成员请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        public static void Req_GetTribeAllUser(long tribeId)
        {
            var pack = new GetTribeAllUser();
            pack.tribeId = tribeId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_TRIBE_ALL_USER_REQUEST, pack);
        }
        /// <summary>
        /// 获取所有宝箱请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="itemId">物品id</param>
        /// <param name="itemNum">物品数量</param>
        /// <param name="wareHouseId">格子id</param>
        public static void Req_GetAllTribeWareHouseRequest(long tribeId)
        {
            var pack = new GetAllTribeWareHouseRequest();
            pack.tribeId = tribeId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_ALL_TRIBE_WAREHOUSE_REQUEST, pack);
        }
        /// <summary>
        /// 获取所有部落申请请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="userName">用户名</param>
        /// <param name="userLevel">用户等级</param>
        /// <param name="vipLevel">vip等级</param>
        /// <param name="headUrl">用户头像</param>
        public static void Req_GetAllTribeApplyRequest(long tribeId)
        {
            var pack = new GetAllTribeApplyRequest();
            pack.tribeId = tribeId; 
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_ALL_TRIBE_APPLY_REQUEST, pack);
        }
        /// <summary>
        /// 获取当前部落信息请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        public static void Req_GetOneTribeRequest(long tribeId)
        {
            var pack = new GetOneTribeRequest();
            pack.tribeId = tribeId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_ONE_TRIBE_REQUEST, pack);
        }
        /// <summary>
        /// 获取存取记录请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        public static void Req_GetInOrOutRequest(long tribeId, long userId)
        {
            var pack = new GetInOrOutRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_IN_OR_OUT_REQUEST, pack);
        }

        /// <summary>
        /// 申请部落请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        public static void Req_ApplyTribeRequest(long tribeId, long userId)
        {
            var pack = new ApplyTribeRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_APPLY_TRIBE_REQUEST, pack);
        }
        /// <summary>
        /// 处理申请部落请求
        /// </summary>
        /// <param name="applyId">申请id</param>
        /// <param name="dealResult">处理结果（0：未通过 1：通过）</param>
        public static void Req_DealApplyTribeRequest(long applyId, long dealResult, long tribeId)
        {
            var pack = new DealApplyTribeRequest();
            pack.applyId = applyId;
            pack.dealResult = dealResult;
            pack.tribeId = tribeId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_DEAL_APPLY_TRIBE_REQUEST, pack);
        }
        /// <summary>
        /// 修改部落权限请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="jurisDiction">权限几</param>
        /// <param name="result">修改结果</param>
        public static void Req_UpdateTribeJurisDictionRequest(long tribeId, long userId, long jurisDiction, long result)
        {
            var pack = new UpdateTribeJurisDictionRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.jurisDiction = jurisDiction;
            pack.result = result;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_UPDATE_TRIBE_JURISDICTION_REQUEST, pack);
        }
        /// <summary>
        /// 存入部落仓库请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="password">密码</param>
        /// <param name="itemId">物品id</param>
        /// <param name="itemNum">物品数量</param>
        public static void Req_DepositTribeWareHouseRequest(long tribeId, long userId, long itemId, long itemNum)
        {
            var pack = new DepositTribeWareHouseRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            //pack.password = password;
            pack.itemId = itemId;
            pack.itemNum = itemNum;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_DEPOSIT_TRIBE_WAREHOUSE_REQUEST, pack);
        }
        /// <summary>
        /// 设置部落成员职位请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="position">职位</param>
        public static void Req_SetTribeUserPositionRequest(long tribeId, long userId, long position)
        {
            var pack = new SetTribeUserPositionRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.position = position;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_SET_TRIBE_USER_POSITION_REQUEST, pack);
        }

        /// <summary>
        /// 取出部落仓库请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="password">密码</param>
        /// <param name="wareId">格子id</param>
        public static void Req_OutTribeWareHouseRequest(long tribeId, long userId, string password, long wareId)
        {
            var pack = new OutTribeWareHouseRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.password = password;
            pack.wareId = wareId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_OUT_TRIBE_WAREHOUSE_REQUEST, pack);
        }
        /// <summary>
        /// 修改部落名称请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="name">名称</param>
        public static void Req_UpdateTribeNameRequest(long tribeId, long userId, string name)
        {
            var pack = new UpdateTribeNameRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.name = name;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_UPDATE_TRIBE_NAME_REQUEST, pack);
        }
        /// <summary>
        /// 修改部落简介请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="context">简介</param>
        public static void Req_UpdateTribeContextRequest(long tribeId, long userId, string context)
        {
            var pack = new UpdateTribeContextRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.context = context;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_UPDATE_TRIBE_CONTEXT_REQUEST, pack);
        }
        /// <summary>
        /// 部落宝箱搜索请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="context">搜索内容</param>
        public static void Req_TribeSearchRequest(long tribeId, long userId, string context)
        {
            var pack = new TribeSearchRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            if (context.Contains("我存储了一个宝箱"))
            {
                context = context.Replace("我存储了一个宝箱：","");            
            }
            if (context.Contains("密码："))
            {
                context = context.Replace("密码：", "");
            }
            if (context.Contains("密码："))
            {
                context = context.Replace("密码：", "");
            }
            if (context.Contains("粘贴到搜索框即可直接打开！"))
            {
                context = context.Replace("粘贴到搜索框即可直接打开！", "");
            }
            if (context.Contains("，"))
            {
                context = context.Replace("，", ",");
            }

            context = Regex.Replace(context, @"\s", "");
            pack.context = context.Trim();


            common.SendMessage((int)OseeMsgCode.C_S_TRIBE_SEARCH_REQUEST, pack);
        }

        /// <summary>
        /// 修改部落等级限制请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        /// <param name="vipRestrict">vip等级限制</param>
        /// <param name="levelRestrict">等级限制</param>
        public static void Req_UpdateTribeLevelRequest(long tribeId, long userId, long vipRestrict, long levelRestrict, long verificationRestrict)
        {
            var pack = new UpdateTribeLevelRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            pack.vipRestrict = vipRestrict;
            pack.levelRestrict = levelRestrict;
            pack.verificationRestrict = verificationRestrict; 
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_UPDATE_TRIBE_LEVEL_REQUEST, pack);
        }


        /// <summary>
        /// 踢人出部落请求
        /// </summary>
        /// <param name="tribeId">部落id</param>
        /// <param name="userId">用户id</param>
        public static void Req_KickOutTribeUserRequest(long tribeId, long userId)
        {
            var pack = new KickOutTribeUserRequest();
            pack.tribeId = tribeId;
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_KICK_OUT_TRIBE_USER_REQUEST, pack);
        }

        /// <summary>
        /// 获取玩家积分请求
        /// </summary>
        /// <param name="rankType"> 排名类型 1：周排名   2：日排名  3：月排名</param>
        public static void Req_PlayerPointRequest(int rankType)
        {
            var pack = new PlayerPointRequest();
            pack.rankType = rankType;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_PLAYER_POINT_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家幸运王者榜排名和奖励请求
        /// </summary>
        /// <param name="rankType"> 排名类型 1：周排名   2：日排名  3：月排名</param>
        /// <param name="pageCurrent">当前页码</param>
        /// <param name="pageSize"> 单页大小</param>
        /// <param name="total"> 数据总数</param>
        public static void Req_PlayerGoldRankRequest(int rankType, int pageCurrent, int pageSize, int total)
        {
            var pack = new PlayerGoldRankRequest();
            pack.rankType = rankType;
            pack.pageCurrent = pageCurrent;
            pack.pageSize = pageSize;
            pack.total = total;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_PLAYER_GOLD_RANK_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家幸运王者榜积分请求
        /// </summary>
        /// <param name="rankType"> 排名类型 1：周排名   2：日排名  3：月排名</param>
        public static void Req_PlayerGoldRequest(int rankType)
        {
            var pack = new PlayerGoldRequest();
            pack.rankType = rankType;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_PLAYER_GOLD_REQUEST, pack);
        }

        /// <summary>
        /// 获取下次抽奖费用请求
        /// </summary>
        public static void Req_NextLotteryDrawFeeRequest()
        {
            var pack = new NextLotteryDrawFeeRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_NEXT_LOTTERY_DRAW_FEE_REQUEST, pack);
        }
        /// <summary>
        /// 转盘抽奖请求
        /// </summary>
        public static void Req_LotteryDrawRequest()
        {
            var pack = new LotteryDrawRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_LOTTERY_DRAW_REQUEST, pack);
        }
        /// <summary>
        /// 获取已签到次数请求
        /// </summary>
        public static void Req_SignedTimesRequest()
        {
            var pack = new SignedTimesRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_SIGNED_TIMES_REQUEST, pack);
        }
        /// <summary>
        /// 每日签到请求
        /// </summary>
        public static void Req_DailySignRequest()
        {
            var pack = new DailySignRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_DAILY_SIGN_REQUEST, pack);
        }
        /// <summary>
        /// 检查保险箱密码请求
        /// </summary>
        /// <param name="password"> 保险箱密码(md5)</param>
        public static void Req_CheckBankPasswordRequest(string password)
        {
            var pack = new CheckBankPasswordRequest();
            pack.password = password;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_CHECK_BANK_PASSWORD_REQUEST, pack);
        }
        /// <summary>
        /// 存取金币请求
        /// </summary>
        /// <param name="password"> 保险箱密码(md5)</param>
        /// <param name="money"> 金额 >0:存入金币 <0:取出金币</param>
        public static void Req_SaveMoneyRequest(string password, long money)
        {
            var pack = new SaveMoneyRequest();
            pack.password = password;
            pack.money = money;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_SAVE_MONEY_REQUEST, pack);
        }
        /// <summary>
        /// 修改保险箱密码请求
        /// </summary>
        /// <param name="oldPassword"> 旧密码</param>
        /// <param name="newPassword"> 新密码</param>
        public static void Req_ChangeBankPasswordRequest(string oldPassword, string newPassword)
        {
            var pack = new ChangeBankPasswordRequest();
            pack.oldPassword = oldPassword;
            pack.newPassword = newPassword;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_CHANGE_BANK_PASSWORD_REQUEST, pack);
        }
        /// <summary>
        /// 获取排行榜数据请求
        /// </summary>
        /// <param name="rankingType"> 排行榜类型 0:金币榜 1:vip榜</param>
        public static void Req_GetRankingListRequest(int rankingType)
        {
            var pack = new GetRankingListRequest();
            pack.rankingType = rankingType;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_RANKING_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 获取奖券商品列表请求
        /// </summary>
        public static void Req_GetLotteryShopListRequest()
        {
            var pack = new GetLotteryShopListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_LOTTERY_SHOP_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 购买商城商品请求
        /// </summary>
        /// <param name="index"> 商品序号(1-n)</param>
        public static void Req_BuyShopItemRequest(long index)
        {
            var pack = new BuyShopItemRequest();
            pack.index = index;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_BUY_SHOP_ITEM_REQUEST, pack);
        }
        /// <summary>
        /// 获取是否首充请求
        /// </summary>
        public static void Req_BuyIsFirstRequest()
        {
            var pack = new BuyIsFirstRequest();
            common.SendMessage((int)OseeMsgCode.C_S_BUY_ISFIRST_REQUEST, pack);
        }

        /// <summary>
        /// 客服微信请求
        /// </summary>
        public static void Req_ServiceWechatRequest()
        {
            var pack = new ServiceWechatRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_SERVICE_WECHAT_REQUEST, pack);
        }
        /// <summary>
        /// 添加用户反馈请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        /// <param name="context"> 反馈内容</param>
        public static void Req_FeedBackRequest(int userId, string context)
        {
            var pack = new FeedBackRequest();
            pack.userId = userId;
            pack.context = context;
            common.SendMessage((int)OseeMsgCode.C_S_FEED_BACK_REQUEST, pack);
        }

        /// <summary>
        /// 公告列表请求
        /// </summary>
        public static void Req_NoticeListRequest()
        {
            var pack = new NoticeListRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_NOTICE_LIST_REQUEST, pack);
        }
        /// <summary>
        /// 使用cdk请求
        /// </summary>
        /// <param name="cdk"> cdk</param>
        public static void Req_UseCdkRequest(string cdk)
        {
            var pack = new UseCdkRequest();
            pack.cdk = cdk;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_USE_CDK_REQUEST, pack);
        }
        /// <summary>
        /// 实名认证信息请求
        /// </summary>
        public static void Req_AuthenticateInfoRequest()
        {
            var pack = new AuthenticateInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_AUTHENTICATE_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 提交实名认证信息请求
        /// </summary>
        /// <param name="realName"> 真实姓名</param>
        /// <param name="idCardNum"> 身份证号</param>
        /// <param name="phoneNum"> 手机号</param>
        /// <param name="checkCode"> 手机验证码</param>
        public static void Req_SubmitAuthenticateRequest(string realName, string idCardNum)
        {
            var pack = new SubmitAuthenticateRequest();
            pack.realName = realName;
            pack.idCardNum = idCardNum;    
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_SUBMIT_AUTHENTICATE_REQUEST, pack);
        }
        /// <summary>
        /// 实名认证手机验证请求
        /// </summary>
        /// <param name="phoneNum"> 手机号</param>
        public static void Req_AuthenticatePhoneCheckRequest(string phoneNum)
        {
            var pack = new AuthenticatePhoneCheckRequest();
            pack.phoneNum = phoneNum;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_AUTHENTICATE_PHONE_CHECK_REQUEST, pack);
        }

        /// <summary>
        /// 获取重置密码手机号请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        public static void Req_GetResetPasswordPhoneNumRequest(string username)
        {
            var pack = new GetResetPasswordPhoneNumRequest();
            pack.username = username;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_GET_RESET_PASSWORD_PHONE_NUM_REQUEST, pack);
        }
        /// <summary>
        /// 重置用户密码手机验证请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        public static void Req_ResetPasswordPhoneCheckRequest(string username)
        {
            var pack = new ResetPasswordPhoneCheckRequest();
            pack.username = username;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_RESET_PASSWORD_PHONE_CHECK_REQUEST, pack);
        }
        /// <summary>
        /// 重置密码请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="checkCode"> 验证码</param>
        /// <param name="password"> 密码</param>
        public static void Req_ResetPasswordRequest(string username, int checkCode, string password)
        {
            var pack = new ResetPasswordRequest(); 
            pack.username = username;
            pack.checkCode = checkCode;
            pack.password = password;
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_RESET_PASSWORD_REQUEST, pack);
        }
        /// <summary>
        /// 微信分享请求
        /// </summary>
        public static void Req_WechatShareRequest()
        {
            var pack = new WechatShareRequest();
            common.SendMessage((int)OseeMsgCode.C_S_OSEE_WECHAT_SHARE_REQUEST, pack);
        }
        /// <summary>
        /// 获取功能启用状态
        /// </summary>
        public static void Req_FunctionStateRequest()
        {
            var pack = new FunctionStateRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_FUNCTION_STATE_REQUEST, pack);
        }
        /// <summary>
        /// 获取金币启用状态
        /// </summary>
        public static void Req_GetPayWayRequest()
        {
            var pack = new GetPayWayAllRequest();
            common.SendMessage((int)OseeMsgCode.C_S_GET_PAY_WAY_REQUEST, pack);
        }
         
        /// <summary>
        /// 获取玩家今日充值剩余限制金额请求
        /// </summary>
        public static void Req_RechargeLimitRestRequest()
        {
            var pack = new RechargeLimitRestRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_RECHARGE_LIMIT_REST_REQUEST, pack);
        }
        /// <summary>
        /// 兑换龙晶请求
        /// </summary>
        /// <param name="items"> 兑换的龙珠数量</param>
        /// <param name="exchangeType"> 兑换类型 0-龙珠兑换龙晶 1-龙晶兑换龙珠</param>
        public static void Req_ExchangeDragonCrystalRequest(List<ItemDataProto> items, int exchangeType)
        {
            var pack = new ExchangeDragonCrystalRequest();
            for (int i = 0; i < items.Count; i++)
                pack.items.Add(items[i]);
            pack.exchangeType = exchangeType;
            common.SendMessage((int)OseeMsgCode.C_S_EXCHANGE_DRAGON_CRYSTAL_REQUEST, pack);
        }
        /// <summary>
        /// 砸金猪请求
        /// </summary>
        /// <param name="index"> 选用的锤子 0-免费 1-木锤 2-铁锤 3-金锤</param>
        public static void Req_GoldenPigBreakRequest(int index)
        {
            var pack = new GoldenPigBreakRequest();
            pack.index = index;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GOLDEN_PIG_BREAK_REQUEST, pack);
        }
        /// <summary>
        /// 获取今日砸金猪免费次数请求
        /// </summary>
        public static void Req_GoldenPigFreeTimesRequest()
        {
            var pack = new GoldenPigFreeTimesRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GOLDEN_PIG_FREE_TIMES_REQUEST, pack);
        }
        /// <summary>
        /// 获取今日VIP可砸的次数上限请求
        /// </summary>
        public static void Req_GoldenPigHitLimitRequest()
        {
            var pack = new GoldenPigHitLimitRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GOLDEN_PIG_HIT_LIMIT_REQUEST, pack);
        }

        /// <summary>
        /// 玩家今日boss号角限购次数信息
        /// </summary>
        public static void Req_BossBugleBuyLimitRequest()
        {
            var pack = new BossBugleBuyLimitRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BOSS_BUGLE_BUY_LIMIT_REQUEST, pack);
        }
        /// <summary>
        /// 每日福袋购买信息请求
        /// </summary>
        public static void Req_DailyBagBuyInfoRequest()
        {
            var pack = new DailyBagBuyInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_DAILY_BAG_BUY_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 购买每日福袋请求
        /// </summary>
        /// <param name="bagId"> 福袋id</param>
        public static void Req_BuyDailyBagRequest(int bagId)
        {
            var pack = new BuyDailyBagRequest();
            pack.bagId = bagId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BUY_DAILY_BAG_REQUEST, pack);
        }
        /// <summary>
        /// 购买每日礼包请求
        /// </summary>
        /// <param name="bagId"> 福袋id</param>
        public static void Req_BuyDailyGiftRequest(int bagId)
        {
            var pack = new BuyDailyGiftRequest();
            pack.bagId = bagId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BUY_DAILY_GIFT_REQUEST, pack);
        }
        /// <summary>
        /// 每日礼包购买信息请求
        /// </summary>
        public static void Req_DailyBuyGiftInfoRequest()
        {
            var pack = new DailyBuyGiftInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_DAILY_BUY_GIFT_INFO_REQUEST, pack);
        }

    }
}
