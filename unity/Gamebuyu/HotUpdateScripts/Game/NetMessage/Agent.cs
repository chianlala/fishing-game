using NetLib;
using System.Collections.Generic;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fruit;
using com.maple.game.osee.proto.agent;
using com.maple.game.osee.proto.lobby;
using System.ComponentModel;
using ProtoBuf;
using CoreGame;
using Game.UI;

namespace NetMessage
{
    public abstract class Agent 
    {
        /// <summary>
     /// 玩家绑定状态请求
     /// </summary>
        public static void Req_PlayerBindStateRequest()
        {
            var pack = new PlayerBindStateRequest();
            common.SendMessage((int)OseeMsgCode.C_S_PLAYER_BIND_STATE_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家充值返利请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        public static void Req_GetUserRechargeMoneyRequest(long userId)
        {
            var pack = new GetUserRechargeMoneyRequest();
            pack.userId = userId;
            common.SendMessage((int)OseeMsgCode.C_S_GET_USER_RECHARGE_MONEY_REQUEST, pack);
        }
        /// <summary>
        /// 获取玩家充值返利奖励请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        /// <param name="num"> 领取的级数从1开始</param>
        public static void Req_GetUserRechargeMoneyRewordRequest(long userId, long num)
        {
            var pack = new GetUserRechargeMoneyRewordRequest();
            pack.userId = userId;
            pack.num = num;
            common.SendMessage((int)OseeMsgCode.C_S_GET_USER_RECHARGE_MONEY_REWORD_REQUEST, pack);
        }
        /// <summary>
        /// 设置账号手机号验证请求
        /// </summary>
        /// <param name="phoneNum"> 手机号码</param>
        public static void Req_AccountPhoneCheckRequest(string phoneNum)
        {
            var pack = new AccountPhoneCheckRequest();
            pack.phoneNum = phoneNum;
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_ACCOUNT_PHONE_CHECK_REQUEST, pack);
        }
        /// <summary>
        /// 设置账号请求
        /// </summary>
        /// <param name="phoneNum"> 手机号，即账号</param>
        /// <param name="checkCode"> 验证码</param>
        /// <param name="password"> 密码</param>
        public static void Req_AccountSetRequest(string phoneNum, int checkCode, string password)
        {
            var pack = new AccountSetRequest();
            common.phoneNum = phoneNum;
            pack.phoneNum = phoneNum;
            pack.checkCode = checkCode;           
            pack.password = common.MD5Encrypt(password);
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_ACCOUNT_SET_REQUEST, pack);
        }

    }
}

//public class LuaRequest : IExtensible
//{
//    public int targetViewIndex = 1;
//    IExtension IExtensible.GetExtensionObject(bool createIfMissing)
//    {
//        throw new System.NotImplementedException();
//    }
//}
[ProtoContract(Name = "LuaRequest")]
public class LuaRequest : IExtensible
{ 
    //public LuaRequest();

    [DefaultValue(0)]
    [ProtoMember(1, IsRequired = false, Name = "targetViewIndex", DataFormat = DataFormat.TwosComplement)]
    public int targetViewIndex { get; set; }

    IExtension IExtensible.GetExtensionObject(bool createIfMissing)
    {
        throw new System.NotImplementedException();
    }
}