using UnityEngine;
using System.Collections;

/// <summary>
/// UI事件名
/// </summary>
public abstract class UEventName
{
    public const string GetServerList = "GetServerList";
    public const string WelcomeFlash = "WelcomeFlash";
    public const string AutoLogin = "AutoLogin";    //微信自动登录
    public const string SetLoginBtn = "SetLoginBtn";    //设置登录按钮可用性
    public const string StartConnect = "StartConnect";      //开始连接服务器
    public const string UpdateLoadingProgress = "UpdateLoadingProgress";

    public const string LoadSceneAsync = "LoadSceneAsync";  
    //public const string MOVE_OP = "MOVE_OP";
    //public const string BATTLE_OP = "BATTLE_OP";
    public const string LOAD_SCENE_PROGRESS = "LOAD_SCENE_PROGRESS";//加载场景
    public const string EndRound = "EndRound";//回合结束
    /// <summary>
    /// 获取是否首充响应
    /// <summary>
    public const string BuyIsFirstResponse= "BuyIsFirstResponse";
    public const string PlayerRankResponse = "PlayerRankResponse";
    public const string PlayerPointResponse = "PlayerPointResponse";
    /// <summary>
    /// 获取玩家幸运王者榜排名和奖励返回
    /// <summary>
    public const string PlayerGoldRankResponse = "PlayerGoldRankResponse";
    /// <summary>
    /// 部落宝箱搜索返回
    /// <summary>
    public const string TribeSearchResponse = "TribeSearchResponse";
    /// <summary>
    /// 改变狂暴倍数响应
    /// <summary>
    public const string FishingChangeCritMultResponse = "FishingChangeCritMultResponse";
    /// <summary>
    /// 获取服务器是否开服返回
    /// <summary>
    public const string AllIsOpenResponse = "AllIsOpenResponse";
    /// <summary>
    /// 获取部落礼包返回
    /// <summary>
    public const string GetTribeGiftResponse = "GetTribeGiftResponse";
    /// <summary>
    /// 获取玩家连击次数返回
    /// <summary>
    public const string GetFightNumResponse = "GetFightNumResponse";
    /// <summary>
    /// 获取玩家连击榜榜排名和奖励返回
    /// <summary>
    public const string GetFightRankResponse = "GetFightRankResponse";
    public const string KillFishRankResponse = "KillFishRankResponse"; 
    /// <summary>
    /// 锁定
    /// <summary>
    public const string FishingGrandPrixSyncLockResponse = "FishingGrandPrixSyncLockResponse"; 
    /// <summary>
    /// 获取玩家幸运王者榜积分返回
    /// <summary>
    public const string PlayerGoldResponse = "PlayerGoldResponse";
    /// <summary>
    /// 经典渔场同步锁定响应
    /// <summary>
    public const string FishingSyncLockResponse = "FishingSyncLockResponse";

    /// <summary>
    /// 是否加入部落返回
    /// <summary>
    public const string IsJoinTribeResponse = "IsJoinTribeResponse";
    /// <summary>
    /// 机器人玩小游戏游戏返回
    /// <summary>
    public const string RobotPlayGameResponse = "RobotPlayGameResponse";
    /// <summary>
    /// 二次伤害杀死鱼返回
    /// <summary>
    public const string FishingDoubleKillFishResponse = "FishingDoubleKillFishResponse";
    /// <summary>
    /// 二次伤害鱼返回
    /// <summary>
    public const string FishingDoubleKillResponse = "FishingDoubleKillResponse";
    /// <summary>
    /// 捕鱼房间特殊鱼倍数
    /// <summary>
    public const string FishingRoomFishMultipleResponse = "FishingRoomFishMultipleResponse";
    /// <summary>
    /// 龙晶场二次伤害杀死鱼返回
    /// <summary>
    public const string FishingChallengeDoubleKillFishResponse = "FishingChallengeDoubleKillFishResponse";
    /// <summary>
    /// 龙晶场二次伤害鱼返回
    /// <summary>
    public const string FishingChallengeDoubleKillResponse = "FishingChallengeDoubleKillResponse";
    /// <summary>
    /// 龙晶场二次伤害结束返回
    /// <summary>
    public const string FishingChallengeDoubleKillEndResponse = "FishingChallengeDoubleKillEndResponse";
    /// <summary>
    /// 特殊BOSS鱼倍数信息
    /// <summary>
    public const string FishBossMultipleResponse = "FishBossMultipleResponse";
    /// <summary>
    /// 背景同步响应
    /// <summary>
    public const string BackgroundSyncResponse = "BackgroundSyncResponse"; 
    /// <summary>
    /// 切换炮台
    /// <summary>
    public const string changeBatteryViewResponse = "changeBatteryViewResponse";
    /// <summary>
    /// 每日礼包购买成功响应
    /// <summary>
    public const string DailyBuyGiftSuccessResponse = "DailyBuyGiftSuccessResponse";
    /// <summary>
    /// 每日礼包购买信息响应
    /// <summary>
    public const string DailyBuyGiftInfoResponse = "DailyBuyGiftInfoResponse";
    /// <summary>
    /// 每日礼包购买信息响应
    /// <summary>
    public const string FruitBonusResponse = "FruitBonusResponse";  
    /// <summary> 
    /// 昵称注册响应
    /// <summary>
    public const string UserRegisterResponse = "UserRegisterResponse";
    /// <summary>
    /// 绑定昵称响应
    /// <summary>
    public const string BangNiceNameResponse = "BangNiceNameResponse";
    /// <summary>
    /// 绑定身份信息响应
    /// <summary>
    public const string BangIdCardAllResponse = "BangIdCardAllResponse";
    /// <summary>
    /// 修改登陆密码响应
    /// <summary>
    public const string UpdateLoginPasswordResponse = "UpdateLoginPasswordResponse";
    /// <summary>
    /// 换绑个人信息响应
    /// <summary>
    public const string UpdateIdCardNoResponse = "UpdateIdCardNoResponse";
    /// <summary>
    /// 解绑微信响应
    /// <summary>
    public const string DeleteWxResponse = "DeleteWxResponse";
    /// <summary>
    /// 添加用户反馈响应
    /// <summary>
    public const string FeedBackResponse = "FeedBackResponse";
    /// <summary>
    /// 换绑发送验证码
    /// <summary>
    public const string ChangePhoneResponse = "ChangePhoneResponse";
    /// <summary>
    /// 获取锻造/合成/强化组合
    /// <summary>
    public const string GetForgingGroupResponse = "GetForgingGroupResponse";
    /// <summary>
    /// 使用锻造/合成/强化组合
    /// <summary>
    public const string UseForgingGroupResponse = "UseForgingGroupResponse";
    /// <summary>
    /// 切换锻造/合成/强化组合
    /// <summary>
    public const string ChangeForgingGroupResponse = "ChangeForgingGroupResponse";
    /// <summary>
    /// 获取所有部落申请返回
    /// <summary>
    public const string SetTribeUserPositionResponse = "SetTribeUserPositionResponse";
    /// <summary>
    /// 获取存取记录返回
    /// <summary>
    public const string GetInOrOutResponse = "GetInOrOutResponse";

    /// <summary>
    /// 开启奖池请求响应
    /// <summary>
    public const string OpenJcResponse = "OpenJcResponse";
    /// <summary>
    /// 获取奖池金额响应
    /// <summary>
    public const string GetJcAllMoneyResponse = "GetJcAllMoneyResponse";
    /// <summary>
    /// 获取奖池抽取记录响应
    /// <summary>
    public const string GetJcAllRecordResponse = "GetJcAllRecordResponse";
    
    public const string FightTenBonusResponse = "FightTenBonusResponse";
    /// <summary>
    /// 获取所有宝箱返回
    /// <summary>
    public const string GetAllTribeWareHouseResponse = "GetAllTribeWareHouseResponse";
    /// <summary>
    /// 获取所有部落申请返回
    /// <summary>
    public const string GetAllTribeApplyResponse = "GetAllTribeApplyResponse";
    /// <summary>
    /// 获取当前部落信息返回
    /// <summary>
    public const string GetOneTribeResponse = "GetOneTribeResponse";

    /// <summary>
    /// 同步电磁炮响应
    /// <summary>
    public const string UseEleResponse = "UseEleResponse";
    /// <summary>
    /// 同步黑洞炮响应
    /// <summary>
    public const string UseBlackResponse = "UseBlackResponse";
    /// <summary>
    /// 同步鱼雷炮响应
    /// <summary>
    public const string UseTroResponse = "UseTroResponse";
    /// <summary>
    /// 同步钻头响应
    /// <summary>
    public const string UseBitResponse = "UseBitResponse";
    /// <summary>
    /// 钻头击中鱼响应
    /// <summary>
    public const string BitFightFishResponse = "BitFightFishResponse";
    /// <summary>
    /// 挑战赛钻头击中鱼响应
    /// <summary>
    public const string ChallengeBitFightFishResponse = "ChallengeBitFightFishResponse";
    /// <summary>
    /// 龙晶战场同步锁定响应
    /// <summary>
    public const string FishingChallengeSyncLockResponse = "FishingChallengeSyncLockResponse";
    /// <summary>
    /// 大奖赛钻头击中鱼响应
    /// <summary>
    public const string GrandPrixBitFightFishResponse = "GrandPrixBitFightFishResponse";

    /// <summary>
    /// 第五房间列表响应
    /// <summary>
    public const string FishingRoomListResponse = "FishingRoomListResponse";

    /// <summary>
    /// 每周第一次进入返回的上周排行榜
    /// <summary>
    public const string FirstWeekLoginResponse = "FirstWeekLoginResponse";
    /// <summary>
    /// 大奖赛机器人
    /// </summary>
    public const string FishingGrandPrixRobotFireResponse = "FishingGrandPrixRobotFireResponse";
    /// <summary>
    /// 挑战赛机器人
    /// </summary>
    public const string FishingChallengeRobotFireResponse = "FishingChallengeRobotFireResponse";   
    /// <summary>
    /// 每周第一次进入返回的上周排行榜
    /// <summary> 
    public const string ReceiveItemsResponse = "ReceiveItemsResponse";

    /// <summary>
    /// 删除邮件响应
    /// <summary> 
    public const string DeleteMessageResponse = "DeleteMessageResponse"; 
    /// <summary>
    /// 第一次进入返回
    /// <summary>
    public const string FirstJoinResponse = "FirstJoinResponse";  
    /// <summary>
    /// 玩家今日充值剩余限制金额响应
    /// <summary>
    public const string RechargeLimitRestResponse = "RechargeLimitRestResponse";
    /// <summary>
    /// 获取玩家等级请求
    /// <summary>
    public const string PlayerLevelResponse = "PlayerLevelResponse";

    /// <summary>
    /// 检查更新进度
    /// </summary>
    public const string UpdateProgress = "UpdateProgress";
    /// <summary>
    /// 网络诊断返回
    /// <summary>
    public const string PingResponse = "PingResponse";
    /// <summary>
    /// 心跳返回
    /// <summary>
    public const string HeartBeatResponse = "HeartBeatResponse";
    /// <summary>
    /// 提示消息返回
    /// <summary>
    public const string HintMessageResponse = "HintMessageResponse";
    /// <summary>
    /// 获取连接token返回
    /// <summary>
    public const string GetConnectTokenResponse = "GetConnectTokenResponse";
    /// <summary>
    /// 无效token返回
    /// <summary>
    public const string InvalidTokenResponse = "InvalidTokenResponse";

    /// <summary>
    /// 房间聊天返回
    /// <summary>
    public const string ChatInRoomResponse = "ChatInRoomResponse";
    /// <summary>
    /// 玩家最新状态响应
    /// <summary> 
    public const string PlayerStatusResponse = "PlayerStatusResponse"; 
    /// <summary>
    /// 获取未使用的用户名返回
    /// <summary>
    public const string GetUnusedUsernameResponse = "GetUnusedUsernameResponse";
    /// <summary>
    /// 标准注册返回
    /// <summary>
    public const string CommonRegisterResponse = "CommonRegisterResponse";
    /// <summary>
    /// 用户登录成功返回
    /// <summary>
    public const string LoginSuccessResponse = "LoginSuccessResponse";
    /// <summary>
    /// 游客登录返回
    /// <summary>
    public const string TouristLoginResponse = "TouristLoginResponse";
    /// <summary>
    /// 修改密码返回(成功时)
    /// <summary>
    public const string ChangePasswordResponse = "ChangePasswordResponse";
    /// <summary>
    /// 退出返回
    /// <summary>
    public const string LogoutResponse = "LogoutResponse";
    /// <summary>
    /// 微信code换取Token响应
    /// <summary>
    public const string WxLoginCodeResponse = "WxLoginCodeResponse";
    /// <summary>
    /// 微信code换取Token响应new
    /// <summary>
    public const string WxLoginCodeResponseNew = "WxLoginCodeResponseNew";
     
    /// <summary>
    /// 通知前端重新授权的响应
    /// <summary>
    public const string WxLoginReAuthResponse = "WxLoginReAuthResponse";

    /// <summary>
    /// 获取用户详情返回
    /// <summary>
    public const string GetUserInfoResponse = "GetUserInfoResponse";
    /// <summary>
    /// 系统游走字幕返回
    /// <summary>
    public const string WanderSubtitleResponse = "WanderSubtitleResponse";
    /// <summary>
    /// 检查版本信息返回
    /// <summary>
    public const string CheckVersionResponse = "CheckVersionResponse";
    /// <summary>
    /// 领取救济金响应
    /// <summary>
    public const string GetReliefMoneyResponse = "GetReliefMoneyResponse";
    /// <summary>
    /// 获取收货地址响应
    /// <summary>
    public const string GetAddressResponse = "GetAddressResponse";
    /// <summary>
    /// 获取玩家充值返利返回
    /// <summary>
    public const string GetUserRechargeMoneyResponse = "GetUserRechargeMoneyResponse";
    /// <summary>
    /// 获取玩家充值返利奖励返回
    /// <summary>
    public const string GetUserRechargeMoneyRewordResponse = "GetUserRechargeMoneyRewordResponse";
    /// <summary>
    /// 设置收货地址响应
    /// <summary>
    public const string SetAddressResponse = "SetAddressResponse";
    /// <summary>
    /// 获取玩家货币返回
    /// <summary>
    public const string PlayerMoneyResponse = "PlayerMoneyResponse";
    /// <summary>
    /// 获取玩家vip等级返回
    /// <summary>
    public const string VipLevelResponse = "VipLevelResponse";
    /// <summary>
    /// 获取下次抽奖费用返回
    /// <summary>
    public const string NextLotteryDrawFeeResponse = "NextLotteryDrawFeeResponse";
    /// <summary>
    /// 转盘抽奖返回
    /// <summary>
    public const string LotteryDrawResponse = "LotteryDrawResponse";
    /// <summary>
    /// 转盘中奖全部记录返回
    /// <summary>
    public const string TurnTableAllResponse = "TurnTableAllResponse";
    /// <summary>
    /// 转盘中奖用户记录返回
    /// <summary>
    public const string TurnTableUserResponse = "TurnTableUserResponse";

    public const string LotteryInfoResponse = "LotteryInfoResponse";
    /// <summary>
    /// 获取已签到次数返回
    /// <summary>
    public const string SignedTimesResponse = "SignedTimesResponse";
    public const string SignedTimesResponseMenu = "SignedTimesResponseMenu";
    /// <summary>
    /// 检查保险箱密码返回
    /// <summary>
    public const string CheckBankPasswordResponse = "CheckBankPasswordResponse";
    /// <summary>
    /// 存取金币返回
    /// <summary>
    public const string SaveMoneyResponse = "SaveMoneyResponse";
    /// <summary>
    /// 修改保险箱密码返回
    /// <summary>
    public const string ChangeBankPasswordResponse = "ChangeBankPasswordResponse";
    /// <summary>
    /// 获取排行榜数据请求
    /// <summary>
    public const string GetRankingListResponse = "GetRankingListResponse";
    /// <summary>
    /// 获取奖券商品列表返回
    /// <summary>
    public const string GetLotteryShopListResponse = "GetLotteryShopListResponse";
    /// <summary>
    /// 购买商城商品返回
    /// <summary>
    public const string BuyShopItemResponse = "BuyShopItemResponse";
    /// <summary>
    /// 客服微信返回
    /// <summary>
    public const string ServiceWechatResponse = "ServiceWechatResponse";
    /// <summary>
    /// 公告列表返回
    /// <summary>
    public const string NoticeListResponse = "NoticeListResponse";
    /// <summary>
    /// 使用cdk返回
    /// <summary>
    public const string UseCdkResponse = "UseCdkResponse";
    /// <summary>
    /// 实名认证信息返回
    /// <summary>
    public const string AuthenticateInfoResponse = "AuthenticateInfoResponse";
    /// <summary>
    /// 实名认证手机验证请求
    /// <summary>
    public const string AuthenticatePhoneCheckResponse = "AuthenticatePhoneCheckResponse";
    /// <summary>
    /// 提交实名认证信息返回
    /// <summary>
    public const string SubmitAuthenticateResponse = "SubmitAuthenticateResponse";
    /// <summary>
    /// 获取重置密码手机号返回
    /// <summary>
    public const string GetResetPasswordPhoneNumResponse = "GetResetPasswordPhoneNumResponse";
    /// <summary>
    /// 重置用户密码手机验证返回
    /// <summary>
    public const string ResetPasswordPhoneCheckResponse = "ResetPasswordPhoneCheckResponse";
    /// <summary>
    /// 重置密码返回
    /// <summary>
    public const string ResetPasswordResponse = "ResetPasswordResponse";
    /// <summary>
    /// 微信分享返回
    /// <summary>
    public const string WechatShareResponse = "WechatShareResponse";
    /// <summary>
    /// 玩家道具信息返回
    /// <summary>
    public const string PlayerPropResponse = "PlayerPropResponse";
    /// <summary>
    /// 玩家消息列表响应
    /// <summary>
    public const string MessageListResponse = "MessageListResponse";
    /// <summary>
    /// 玩家未读消息数量响应
    /// <summary>
    public const string UnreadMessageCountResponse = "UnreadMessageCountResponse";
    /// <summary>
    /// 读取消息响应
    /// <summary>
    public const string ReadMessageResponse = "ReadMessageResponse";
    /// <summary>
    /// 领取消息附件/删除响应
    /// <summary>
    public const string ReceiveMessageItemsResponse = "ReceiveMessageItemsResponse";

    /// <summary>
    /// 功能启用状态响应
    /// <summary>
    public const string FunctionStateResponse = "FunctionStateResponse";
    /// <summary>
    /// 更改昵称响应
    /// <summary>
    public const string ChangeNicknameResponse = "ChangeNicknameResponse";
    /// <summary>
    /// 首充奖励响应
    /// <summary>
    public const string FirstChargeRewardsResponse = "FirstChargeRewardsResponse";
    /// <summary>
    /// 购买月卡奖励
    /// <summary>
    public const string BuyMonthCardRewardsResponse = "BuyMonthCardRewardsResponse";
    /// <summary>
    /// 兑换龙晶响应
    /// <summary>
    public const string ExchangeDragonCrystalResponse = "ExchangeDragonCrystalResponse";
    /// <summary>
    /// 一键领取所有已完成的每日奖励响应
    /// <summary>
    public const string OneKeyGetDailyTaskRewardsResponse = "OneKeyGetDailyTaskRewardsResponse";
    /// <summary>
    /// 捕鱼加入房间返回
    /// <summary>
    public const string FishingJoinRoomResponse = "FishingJoinRoomResponse";
    /// <summary>
    /// boss号角使用返回
    /// <summary>
    public const string GetBossBugleResponse = "GetBossBugleResponse";
    
    /// <summary>
    /// 捕鱼玩家信息返回
    /// <summary>
    public const string FishingPlayerInfoResponse = "FishingPlayerInfoResponse";
    /// <summary>
    /// 捕鱼玩家列表信息返回
    /// <summary>
    public const string FishingPlayersInfoResponse = "FishingPlayersInfoResponse";
    /// <summary>
    /// 捕鱼退出房间返回
    /// <summary>
    public const string FishingExitRoomResponse = "FishingExitRoomResponse";
    /// <summary>
    /// 捕鱼获取宝藏返回
    /// <summary>
    public const string FishingGetTreasureResponse = "FishingGetTreasureResponse";
    /// <summary>
    /// 捕鱼获取任务列表返回
    /// <summary>
    public const string FishingTaskListResponse = "FishingTaskListResponse";
    /// <summary>
    /// 捕鱼获取任务奖励返回
    /// <summary>
    public const string FishingGetTaskRewardResponse = "FishingGetTaskRewardResponse";
    /// <summary>
    /// 捕鱼玩家升级返回
    /// <summary>
    public const string FishingLevelUpResponse = "FishingLevelUpResponse";
    /// <summary>
    /// 捕鱼改变炮台外观返回
    /// <summary>
    public const string FishingChangeBatteryViewResponse = "FishingChangeBatteryViewResponse";
    /// <summary>
    /// 捕鱼改变炮台等级返回
    /// <summary>
    public const string FishingChangeBatteryLevelResponse = "FishingChangeBatteryLevelResponse";
    /// <summary>
    /// 捕鱼改变炮台倍数返回
    /// <summary>
    public const string FishingChangeBatteryMultResponse = "FishingChangeBatteryMultResponse";
    /// <summary>
    /// 捕鱼发射子弹返回
    /// <summary>
    public const string FishingFireResponse = "FishingFireResponse";
    /// <summary>
    /// 捕鱼击中鱼类返回
    /// <summary>
    public const string FishingFightFishResponse = "FishingFightFishResponse";
    /// <summary>
    /// 捕鱼刷新房间鱼类返回
    /// <summary>
    public const string FishingRefreshFishesResponse = "FishingRefreshFishesResponse";
    /// <summary>
    /// 捕鱼同步返回
    /// <summary>
    public const string FishingSynchroniseResponse = "FishingSynchroniseResponse";
    /// <summary>
    /// 捕鱼使用技能返回
    /// <summary>
    public const string FishingUseSkillResponse = "FishingUseSkillResponse";
    /// <summary>
    /// 捕鱼完成房间目标返回
    /// <summary>
    public const string FishingFinishRoomGoalResponse = "FishingFinishRoomGoalResponse";
    /// <summary>
    /// 捕鱼机器人发射子弹返回
    /// <summary>
    public const string FishingRobotFireResponse = "FishingRobotFireResponse";
    /// <summary>
    /// 捕鱼鱼潮返回
    /// <summary>
    public const string FishingFishTideResponse = "FishingFishTideResponse";
    /// <summary>
    /// 使用龙珠响应
    /// <summary>
    public const string FishingUseTorpedoResponse = "FishingUseTorpedoResponse";
    /// <summary>
    /// 玩家捕获boss鱼响应
    /// <summary>
    public const string CatchBossFishResponse = "CatchBossFishResponse";
    /// <summary>
    /// 解锁炮台等级提示返回
    /// <summary>
    public const string UnlockBatteryLevelHintResponse = "UnlockBatteryLevelHintResponse";
    /// <summary>
    /// 解锁炮台等级响应
    /// <summary>
    public const string UnlockBatteryLevelResponse = "UnlockBatteryLevelResponse";
    /// <summary>
    /// 拥有最高炮台等级响应
    /// <summary>
    public const string PlayerBatteryLevelResponse = "PlayerBatteryLevelResponse";
    /// <summary>
    /// 捕捉特殊鱼响应
    /// <summary>
    public const string CatchSpecialFishResponse = "CatchSpecialFishResponse";
    /// <summary>
    /// 捕鱼获取房间任务奖励返回
    /// <summary>
    public const string FishingGetRoomTaskRewardResponse = "FishingGetRoomTaskRewardResponse";
    /// <summary>
    /// 捕鱼获取房间任务列表返回
    /// <summary>
    public const string FishingRoomTaskListResponse = "FishingRoomTaskListResponse";
    /// <summary>
    /// 使用boss号角响应
    /// <summary>
    public const string UseBossBugleResponse = "UseBossBugleResponse";

    /// <summary>
    /// 拼十所有场次的信息
    /// <summary>
    public const string TenGetFieldListResponse = "TenGetFieldListResponse";
    /// <summary>
    /// 拼十房间信息
    /// <summary>
    public const string TenRoomInfoResponse = "TenRoomInfoResponse";
    /// <summary>
    /// 离开房间响应
    /// <summary>
    public const string TenLeaveRoomResponse = "TenLeaveRoomResponse";
    /// <summary>
    /// 发送房间内玩家信息给房间内所有玩家
    /// <summary>
    public const string TenRoomPlayerInfoResponse = "TenRoomPlayerInfoResponse";
    /// <summary>
    /// 发送房间内所有玩家信息给某个玩家
    /// <summary>
    public const string TenRoomPlayerInfoListResponse = "TenRoomPlayerInfoListResponse";
    /// <summary>
    /// 房间状态改变响应
    /// <summary>
    public const string TenChangeRoomStateResponse = "TenChangeRoomStateResponse";
    /// <summary>
    /// 准备响应
    /// <summary>
    public const string TenReadyRoomResponse = "TenReadyRoomResponse";
    /// <summary>
    /// 发牌响应
    /// <summary>
    public const string TenDispatchCardResponse = "TenDispatchCardResponse";
    /// <summary>
    /// 抢庄响应
    /// <summary>
    public const string TenFightBankerResponse = "TenFightBankerResponse";
    /// <summary>
    /// 庄家选择响应
    /// <summary>
    public const string TenSelectBankerResponse = "TenSelectBankerResponse";
    /// <summary>
    /// 可下注金额列表
    /// <summary>
    public const string TenBetMoneyListResponse = "TenBetMoneyListResponse";
    /// <summary>
    /// 下注响应
    /// <summary>
    public const string TenBetMoneyResponse = "TenBetMoneyResponse";
    /// <summary>
    /// 发送最后一张牌的响应
    /// <summary>
    public const string TenSendLastCardResponse = "TenSendLastCardResponse";
    /// <summary>
    /// 看牌响应
    /// <summary>
    public const string TenSeeCardResponse = "TenSeeCardResponse";
    /// <summary>
    /// 拼十房间对局结束响应
    /// <summary>
    public const string TenRoundOverResponse = "TenRoundOverResponse";
    /// <summary>
    /// 拼十任务
    /// <summary>
    public const string TenTaskListResponse = "TenTaskListResponse";
    /// <summary>
    /// 拼十礼物
    /// <summary>
    public const string TenGiveGiftResponse = "TenGiveGiftResponse";

    /// <summary>
    /// 钻石兑换挑战次数响应
    /// <summary>
    public const string TenChallengeExchangeTimeResponse = "TenChallengeExchangeTimeResponse";
    /// <summary>
    /// 获取拼十挑战赛房间列表
    /// <summary>
    public const string TenChallengeRoomListResponse = "TenChallengeRoomListResponse";
    /// <summary>
    /// 匹配请求响应
    /// <summary>
    public const string TenChallengeMatchResponse = "TenChallengeMatchResponse";
    /// <summary>
    /// 申请加入房间返回
    /// <summary>
    public const string TenChallengeJoinRoomResponse = "TenChallengeJoinRoomResponse";
    /// <summary>
    /// 拼十房间信息
    /// <summary>
    public const string TenChallengeRoomInfoResponse = "TenChallengeRoomInfoResponse";
    /// <summary>
    /// 重连响应
    /// <summary>
    public const string TenChallengeReconnectResponse = "TenChallengeReconnectResponse";


    public const string InitFlipCard = "InitFlipCard";
    public const string ShowGift = "ShowGift";
    public const string EmoteChat = "EmoteChat";
    /// <summary>
    /// 设置账号时手机号验证返回
    /// <summary>
    public const string AccountPhoneCheckResponse = "AccountPhoneCheckResponse";
    /// <summary>
    /// 设置账号返回
    /// <summary>
    public const string AccountSetResponse = "AccountSetResponse";

    /// <summary>
    /// 玩家重连信息
    /// <summary>
    public const string ReconnectInfoResponse = "ReconnectInfoResponse";
    /// <summary>
    /// 每日任务列表响应
    /// <summary>
    public const string DailyTaskListResponse = "DailyTaskListResponse";
    /// <summary>
    /// 领取每日任务奖励响应
    /// <summary>
    public const string GetDailyTaskRewardResponse = "GetDailyTaskRewardResponse";
    /// <summary>
    /// 领取每日活跃奖励响应
    /// <summary>
    public const string GetDailyActiveRewardResponse = "GetDailyActiveRewardResponse";
    /// <summary>
    /// 获取捕鱼场次信息响应
    /// <summary>
    public const string FishingGetFieldInfoResponse = "FishingGetFieldInfoResponse";
    /// <summary>
    /// 取消匹配响应
    /// <summary>
    public const string TenChallengeCancelMatchResponse = "TenChallengeCancelMatchResponse";
    /// <summary>
    /// 房主踢人响应
    /// <summary>
    public const string TenChallengeKickPlayerResponse = "TenChallengeKickPlayerResponse";
    /// <summary>
    /// 排行榜数据响应
    /// <summary>
    public const string TenChallengeRankingListResponse = "TenChallengeRankingListResponse";
    //********挑战赛
    /// <summary>
    /// 龙晶战场同步电磁炮响应
    /// <summary>
    public const string ChallengeUseEleResponse = "ChallengeUseEleResponse";
    /// <summary>
    /// 龙晶战场同步黑洞炮响应
    /// <summary>
    public const string ChallengeUseBlackResponse = "ChallengeUseBlackResponse";
    /// <summary>
    /// 龙晶战场同步鱼雷炮响应
    /// <summary>
    public const string ChallengeUseTroResponse = "ChallengeUseTroResponse";
    /// <summary>
    /// 龙晶战场同步钻头响应
    /// <summary>
    public const string ChallengeUseBitResponse = "ChallengeUseBitResponse";
    /// <summary>
    /// 大奖赛同步电磁炮响应
    /// <summary>
    public const string GrandPrixUseEleResponse = "GrandPrixUseEleResponse";
    /// <summary>
    /// 大奖赛同步黑洞炮响应
    /// <summary>
    public const string GrandPrixUseBlackResponse = "GrandPrixUseBlackResponse";
    /// <summary>
    /// 大奖赛同步鱼雷炮响应
    /// <summary>
    public const string GrandPrixUseTroResponse = "GrandPrixUseTroResponse";
    /// <summary>
    /// 大奖赛同步钻头响应
    /// <summary>
    public const string GrandPrixUseBitResponse = "GrandPrixUseBitResponse";

    /// <summary>
    /// 挑战赛房间列表响应
    /// <summary>
    public const string FishingChallengeRoomListResponse = "FishingChallengeRoomListResponse";
    /// <summary>
    /// 加入捕鱼挑战赛房间响应
    /// <summary>
    public const string FishingChallengeJoinRoomResponse = "FishingChallengeJoinRoomResponse";
    /// <summary>
    /// 使用鱼雷响应
    /// <summary>
    public const string FishingChallengeUseTorpedoResponse = "FishingChallengeUseTorpedoResponse";

    /// <summary>
    /// 退出捕鱼挑战赛房间响应
    /// <summary>
    public const string FishingChallengeExitRoomResponse = "FishingChallengeExitRoomResponse";
    /// <summary>
    /// 发送房间内玩家信息给房间内所有玩家
    /// <summary>
    public const string FishingChallengeRoomPlayerInfoResponse = "FishingChallengeRoomPlayerInfoResponse";
    /// <summary>
    /// 发送房间内所有玩家信息给某个玩家
    /// <summary>
    public const string FishingChallengeRoomPlayerInfoListResponse = "FishingChallengeRoomPlayerInfoListResponse";
    /// <summary>
    /// 捕鱼改变炮台外观返回
    /// <summary>
    public const string FishingChallengeChangeBatteryViewResponse = "FishingChallengeChangeBatteryViewResponse";
    /// <summary>
    /// 捕鱼改变炮台等级返回
    /// <summary>
    public const string FishingChallengeChangeBatteryLevelResponse = "FishingChallengeChangeBatteryLevelResponse";
    /// <summary>
    /// 捕鱼发射子弹返回
    /// <summary>
    public const string FishingChallengeFireResponse = "FishingChallengeFireResponse";
    /// <summary>
    /// 捕鱼击中鱼类返回
    /// <summary>
    public const string FishingChallengeFightFishResponse = "FishingChallengeFightFishResponse";
    /// <summary>
    /// 捕鱼刷新房间鱼类返回
    /// <summary>
    public const string FishingChallengeRefreshFishesResponse = "FishingChallengeRefreshFishesResponse";
    /// <summary>
    /// 捕鱼同步返回
    /// <summary>
    public const string FishingChallengeSynchroniseResponse = "FishingChallengeSynchroniseResponse";
    /// <summary>
    /// 捕鱼鱼潮返回
    /// <summary>
    public const string FishingChallengeFishTideResponse = "FishingChallengeFishTideResponse";
    /// <summary>
    /// 捕鱼使用技能返回
    /// <summary>
    public const string FishingChallengeUseSkillResponse = "FishingChallengeUseSkillResponse";
    /// <summary>
    /// 玩家捕获boss鱼响应
    /// <summary>
    public const string FishingChallengeCatchBossFishResponse = "FishingChallengeCatchBossFishResponse";
    /// <summary>
    /// 捕捉到特殊鱼响应
    /// <summary>
    public const string FishingChallengeCatchSpecialFishResponse = "FishingChallengeCatchSpecialFishResponse";
    /// <summary>
    /// 使用boss号角响应
    /// <summary>
    public const string FishingChallengeUseBossBugleResponse = "FishingChallengeUseBossBugleResponse";
    /// <summary>
    /// 房间刷新了boss响应
    /// <summary>
    public const string FishingChallengeRefreshBossResponse = "FishingChallengeRefreshBossResponse";
    /// <summary>
    /// 玩家捕获boss响应
    /// <summary>
    public const string FishingChallengeCatchBossResponse = "FishingChallengeCatchBossResponse";

    /// <summary>
    /// 是否在捕鱼房间内响应
    /// <summary>
    public const string IsInFishingRoomResponse = "IsInFishingRoomResponse";

    //**********砸金猪
    /// <summary>
    /// 砸金猪响应
    /// <summary>
    public const string GoldenPigBreakResponse = "GoldenPigBreakResponse";
    /// <summary>
    /// 获取今日砸金猪免费次数响应
    /// <summary>
    public const string GoldenPigFreeTimesResponse = "GoldenPigFreeTimesResponse";
    /// <summary>
    /// 获取今日VIP可砸的次数上限响应
    /// <summary>
    public const string GoldenPigHitLimitResponse = "GoldenPigHitLimitResponse";


    /// <summary>
    /// VIP换座响应
    /// <summary>
    public const string FishingChallengeChangeSeatResponse = "FishingChallengeChangeSeatResponse";
    /// <summary>
    /// 玩家今日boss号角限购次数信息
    /// <summary>
    public const string BossBugleBuyLimitResponse = "BossBugleBuyLimitResponse";
    /// <summary>
    /// 获取兑换记录响应
    /// <summary>
    public const string LotteryExchangeLogResponse = "LotteryExchangeLogResponse";

    /// <summary>
    /// 每日福袋购买信息响应
    /// <summary>
    public const string DailyBagBuyInfoResponse = "DailyBagBuyInfoResponse";
    /// <summary>
    /// 每日福袋购买成功响应
    /// <summary>
    public const string DailyBagBuySuccessResponse = "DailyBagBuySuccessResponse";
    /// <summary>
    /// 限次礼包购买信息响应
    /// <summary>
    public const string OnceBagBuyInfoResponse = "OnceBagBuyInfoResponse";
    /// <summary>
    /// 限次礼包购买成功响应
    /// <summary>
    public const string OnceBagBuySuccessResponse = "OnceBagBuySuccessResponse";
    /// <summary>
    /// 金币卡购买信息响应
    /// <summary>
    public const string MoneyCardBuyInfoResponse = "MoneyCardBuyInfoResponse";
    /// <summary>
    /// 购买金币卡成功响应
    /// <summary>
    public const string MoneyCardBuySuccessResponse = "MoneyCardBuySuccessResponse";
    /// <summary>
    /// 领取金币卡金币响应
    /// <summary>
    public const string ReceiveMoneyResponse = "ReceiveMoneyResponse";
    /// <summary>
    /// 炮台直升响应
    /// <summary>
    public const string BuyBatteryLevelResponse = "BuyBatteryLevelResponse";
    /// <summary>
    /// 大奖赛开赛响应
    /// <summary>
    public const string FishingGrandPrixStartResponse = "FishingGrandPrixStartResponse";
    /// <summary>
    /// 大奖赛用户信息响应
    /// <summary>
    public const string FishingGrandPrixPlayerInfoResponse = "FishingGrandPrixPlayerInfoResponse";
    /// <summary>
    /// 大奖赛排名响应
    /// <summary>
    public const string FishingGrandPrixRankResponse = "FishingGrandPrixRankResponse";
    /// <summary>
    /// 大奖赛退出房间响应
    /// <summary>
    public const string FishingGrandPrixQuitResponse = "FishingGrandPrixQuitResponse";
    /// <summary>
    /// 大奖赛结算的响应
    /// <summary>
    public const string FishingGrandPrixEndResponse = "FishingGrandPrixEndResponse";
    /// <summary>
    /// 拉取排名和奖励响应
    /// <summary>
    public const string RankRewordResponse = "RankRewordResponse"; 
    //*******************
    //大奖赛
    //*******************

    /// <summary>
    /// 加入捕鱼挑战赛房间响应
    /// <summary>
    public const string FishingGrandPrixJoinRoomResponse = "FishingGrandPrixJoinRoomResponse";
    /// <summary>
    /// 发送房间内玩家信息给房间内所有玩家
    /// <summary>
    public const string FishingGrandPrixRoomPlayerInfoResponse = "FishingGrandPrixRoomPlayerInfoResponse";
    /// <summary>
    /// 发送房间内所有玩家信息给某个玩家
    /// <summary>
    public const string FishingGrandPrixRoomPlayerInfoListResponse = "FishingGrandPrixRoomPlayerInfoListResponse";
    /// <summary>
    /// 捕鱼改变炮台外观返回
    /// <summary>
    public const string FishingGrandPrixChangeBatteryViewResponse = "FishingGrandPrixChangeBatteryViewResponse";
    /// <summary>
    /// 捕鱼改变炮台等级返回
    /// <summary>
    public const string FishingGrandPrixChangeBatteryLevelResponse = "FishingGrandPrixChangeBatteryLevelResponse";
    /// <summary>
    /// 捕鱼发射子弹返回
    /// <summary>
    public const string FishingGrandPrixFireResponse = "FishingGrandPrixFireResponse";
    /// <summary>
    /// 捕鱼击中鱼类返回
    /// <summary>
    public const string FishingGrandPrixFightFishResponse = "FishingGrandPrixFightFishResponse";
    /// <summary>
    /// 捕鱼刷新房间鱼类返回
    /// <summary>
    public const string FishingGrandPrixRefreshFishesResponse = "FishingGrandPrixRefreshFishesResponse";
    /// <summary>
    /// 捕鱼同步返回
    /// <summary>
    public const string FishingGrandPrixSynchroniseResponse = "FishingGrandPrixSynchroniseResponse";
    /// <summary>
    /// 捕鱼鱼潮返回
    /// <summary>
    public const string FishingGrandPrixFishTideResponse = "FishingGrandPrixFishTideResponse";
    /// <summary>
    /// 捕鱼使用技能返回
    /// <summary>
    public const string FishingGrandPrixUseSkillResponse = "FishingGrandPrixUseSkillResponse";
    /// <summary>
    /// 玩家捕获boss鱼响应
    /// <summary>
    public const string FishingGrandPrixCatchBossFishResponse = "FishingGrandPrixCatchBossFishResponse";
    /// <summary>
    /// 捕捉到特殊鱼响应
    /// <summary>
    public const string FishingGrandPrixCatchSpecialFishResponse = "FishingGrandPrixCatchSpecialFishResponse";
    /// <summary>
    /// 使用boss号角响应
    /// <summary>
    public const string FishingGrandPrixUseBossBugleResponse = "FishingGrandPrixUseBossBugleResponse";
    /// <summary>
    /// 房间刷新了boss响应
    /// <summary>
    public const string FishingGrandPrixRefreshBossResponse = "FishingGrandPrixRefreshBossResponse";
    /// <summary>
    /// 创建部落返回
    /// <summary>
    public const string TribrEsTabLishResponse = "TribrEsTabLishResponse";
    /// <summary>
    /// 获取部落列表返回
    /// <summary>
    public const string GetTribeResponse = "GetTribeResponse";
    /// <summary>
    /// 获取部落所有成员返回
    /// <summary>
    public const string GetTribeAllUserResponse = "GetTribeAllUserResponse";
    /// <summary>
    /// 申请部落返回
    /// <summary>
    public const string ApplyTripeResponse = "ApplyTripeResponse";
    /// <summary>
    /// 处理申请部落返回
    /// <summary>
    public const string DealApplyTripeResponse = "DealApplyTripeResponse";
    /// <summary>
    /// 是否获取部落礼包返回
    /// <summary>
    public const string IsTribeGetGiftResponse = "IsTribeGetGiftResponse";

    /// <summary>
    /// 修改部落权限返回
    /// <summary>
    public const string UpdateTribeJurisDictionResponse = "UpdateTribeJurisDictionResponse";
    /// <summary>
    /// 存入部落仓库返回
    /// <summary>
    public const string DepositTribeWareHouseResponse = "DepositTribeWareHouseResponse";
    /// <summary>
    /// 取出部落仓库返回
    /// <summary>
    public const string OutTribeWareHouseResponse = "OutTribeWareHouseResponse";
    /// <summary>
    /// 修改部落名称返回
    /// <summary>
    public const string UpdateTribeNameResponse = "UpdateTribeNameResponse";
    /// <summary>
    /// 修改部落简介返回
    /// <summary>
    public const string UpdateTribeContextResponse = "UpdateTribeContextResponse";
    /// <summary>
    /// 踢人出部落返回
    /// <summary>
    public const string KickOutTribeUserResponse = "KickOutTribeUserResponse";

}
