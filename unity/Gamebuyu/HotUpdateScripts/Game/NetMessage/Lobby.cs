using com.maple.common.lobby.proto;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;

namespace NetMessage
{
    public abstract class Lobby
    {
        
        /// <summary>
        /// 获取用户道具状态
        /// </summary>
        public static void Req_PlayerStatusRequest(long playerID)
        { 
            var pack = new PlayerStatusRequest();
            pack.index = playerID;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_PLAYER_STATUS_REQUEST, pack);
        }
        /// <summary>
        /// 获取用户详情请求
        /// </summary>
        public static void Req_GetUserInfoRequest()
        {
            var pack = new GetUserInfoRequest();
            common.SendMessage((int)LobbyMsgCode.C_S_GET_USER_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 修改用户信息请求
        /// </summary>
        /// <param name="nickName"> 昵称</param>
        /// <param name="headIndex"> 头像序号</param>
        /// <param name="headUrl"> 头像地址</param>
        public static void Req_ChangeUserInfoRequest(string nickName, int headIndex, string headUrl)
        {
            var pack = new ChangeUserInfoRequest();
            pack.nickName = nickName;
            pack.headIndex = headIndex;
            pack.headUrl = headUrl;
            common.SendMessage((int)LobbyMsgCode.C_S_CHANGE_USER_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 更改昵称请求
        /// </summary>
        /// <param name="nickname"> 更改的昵称</param>
        public static void Req_ChangeNicknameRequest(string nickname)
        {
            var pack = new ChangeNicknameRequest();
            pack.nickname = nickname;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_CHANGE_NICKNAME_REQUEST, pack);
        }

        /// <summary>
        /// 检查版本信息请求
        /// </summary>
        /// <param name="version"> 版本信息</param>
        public static void Req_CheckVersionRequest(string version)
        {
            var pack = new CheckVersionRequest();
            pack.version = version;
            common.SendMessage((int)LobbyMsgCode.C_S_CHECK_VERSION_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家等级请求
        /// </summary>
        public static void Req_PlayerLevelRequest()
        {
            var pack = new PlayerLevelRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_PLAYER_LEVEL_REQUEST, pack);
        }
        public static void Req_LoginEndRequest()
        {
            var pack = new LoginEndRequest();
            common.SendMessage((int)OseeMsgCode.C_S_LOGIN_END_REQUEST, pack);
        }
        
        /// <summary>
        /// 获取收货地址请求
        /// </summary>
        public static void Req_GetAddressRequest()
        {
            var pack = new GetAddressRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_ADDRESS_REQUEST, pack);
        }
        /// <summary>
        /// 设置收货地址请求
        /// </summary>
        /// <param name="name"> 姓名</param>
        /// <param name="phone"> 手机号码</param>
        /// <param name="address"> 收货地址</param>
        public static void Req_SetAddressRequest(string name, string phone, string address)
        {
            var pack = new SetAddressRequest();
            pack.name = name;
            pack.phone = phone;
            pack.address = address;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_SET_ADDRESS_REQUEST, pack);
        }
        /// <summary>
        /// 小玛丽游戏结束请求
        /// </summary>
        /// <param name="userId"> 玩家id</param>
        public static void Req_XmlEndRequest(long userId, long reword, int type)
        {
            var pack = new XmlEndRequest();
            pack.userId = userId;
            pack.reword = reword;
            pack.type = type;
            common.SendMessage((int)OseeMsgCode.C_S_XML_END_REQUEST, pack);
        }

        /// <summary>
        /// 限次礼包购买信息请求
        /// </summary>
        public static void Req_OnceBagBuyInfoRequest()
        {
            var pack = new OnceBagBuyInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_ONCE_BAG_BUY_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 购买限次礼包请求
        /// </summary>
        /// <param name="bagId"> 礼包id</param>
        public static void Req_BuyOnceBagRequest(int bagId)
        {
            var pack = new BuyOnceBagRequest();
            pack.bagId = bagId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BUY_ONCE_BAG_REQUEST, pack);
        }
        /// <summary>
        /// 金币卡购买信息请求
        /// </summary>
        public static void Req_MoneyCardBuyInfoRequest()
        {
            var pack = new MoneyCardBuyInfoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_MONEY_CARD_BUY_INFO_REQUEST, pack);
        }
        /// <summary>
        /// 购买金币卡请求
        /// </summary>
        public static void Req_BuyMoneyCardRequest()
        {
            var pack = new BuyMoneyCardRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_BUY_MONEY_CARD_REQUEST, pack);
        }
        /// <summary>
        /// 领取金币卡金币请求
        /// </summary>
        public static void Req_ReceiveMoneyRequest()
        {
            var pack = new ReceiveMoneyRequest();
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_RECEIVE_MONEY_REQUEST, pack);
        }
        /// <summary>
        /// 获取锻造/合成/强化组合
        /// </summary>
        /// <param name="id">组合id</param>
        public static void Req_GetForgingGroupRequest(long id)
        {
            var pack = new GetForgingGroupRequest();
            pack.id = id;
            common.SendMessage((int)OseeMsgCode.C_S_GET_FORGING_GROUP_REQUEST, pack);
        }
        /// <summary>
        /// 使用锻造/合成/强化组合
        /// </summary>
        /// <param name="id">组合id</param>
        public static void Req_UseForgingGroupRequest(int id, int forgingType, int fogringId)
        {
            var pack = new UseForgingGroupRequest();
            pack.id = id;
            pack.fogringId = fogringId;  
            pack.forgingType = forgingType;
            common.SendMessage((int)OseeMsgCode.C_S_USE_FORGING_GROUP_REQUEST, pack);
        }
        /// <summary>
        /// 切换锻造/合成/强化组合
        /// </summary>
        /// <param name="forgingId">锻造/合成/强化id</param>
        public static void Req_ChangeForgingGroupRequest(long forgingId)
        {
            var pack = new ChangeForgingGroupRequest();
            pack.forgingId = forgingId;
            common.SendMessage((int)OseeMsgCode.C_S_CHANGE_FORGING_GROUP_REQUEST, pack);
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
        /// <summary>
        /// 获取奖池抽取记录请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        public static void Req_GetJcAllRecordRequest(long userId)
        {
            var pack = new GetJcAllRecordRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_GET_JC_RECORD_REQUEST, pack);
        }

    }
}
