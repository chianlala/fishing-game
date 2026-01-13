using System;
using System.Collections.Generic;
using com.maple.common.login.proto;
using com.maple.game.osee.proto;
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
    public static class Login
    {
        public static void Req_CommonLoginRequest(string username, string password, bool exitRoomFlag, bool isConnect)
        {
            common.OpenType = 1;
            common.OpenUsername = username;
            common.OpenPassw = password;
            var pack = new CommonLoginRequest();
            pack.username = username;
            pack.password = password;
            pack.exitRoomFlag = exitRoomFlag;
            pack.isConnect = isConnect;
            common.SendMessage((int)LoginMsgCode.C_S_COMMON_LOGIN_REQUEST, pack);
        }

        /// <summary>
        /// 获取未使用的用户名请求
        /// </summary>
        public static void Req_GetUnusedUsernameRequest()
        {
            var pack = new GetUnusedUsernameRequest();
            common.SendMessage((int)LoginMsgCode.C_S_GET_UNUSED_USERNAME_REQUEST, pack);
        }
        /// <summary>
        /// 标准注册请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="password"> 密码(32位md5小写密文)</param>
        public static void Req_CommonRegisterRequest(string username, string password)
        {
            var pack = new CommonRegisterRequest();
            pack.username = username;
            pack.password = password;
            common.SendMessage((int)LoginMsgCode.C_S_COMMON_REGISTER_REQUEST, pack);
        }
        /// <summary>
        /// 标准邀请注册请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="password"> 密码(32位md5小写密文)</param>
        /// <param name="inviteCode"> 邀请码</param>
        public static void Req_CommonInviteRegisterRequest(string username, string password, long inviteCode)
        {
            var pack = new CommonInviteRegisterRequest();
            pack.username = username;
            pack.password = password;
            pack.inviteCode = inviteCode;
            common.SendMessage((int)LoginMsgCode.C_S_COMMON_INVITE_REGISTER_REQUEST, pack);
        }
        /// <summary>
        /// 昵称注册
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="password"> 密码(32位md5小写密文)</param>
        /// <param name="phoneNum">手机账号</param>
        /// <param name="phoneCode">验证码</param>
        public static void Req_UserRegisterRequest(string username, string password, string phoneNum, string phoneCode)
        {
            var pack = new UserRegisterRequest();
            pack.username = username;
            pack.password = password;
            pack.phoneNum = phoneNum;
            pack.phoneCode = phoneCode;
            string qudaoID = commonunity.StartFile();
            pack.qudaoID = qudaoID; 
            common.SendMessage((int)OseeMsgCode.C_S_USE_REGISTER_REQUEST, pack);
        }
        /// <summary>
        /// 获取用户ip请求
        /// </summary>
        /// <param name="userId"> 用户id</param>
        /// <param name="userIp"> 用户ip</param>
        public static void Req_GetUserIpRequest(long userId, string userIp)
        {
            var pack = new GetUserIpRequest();
            pack.userId = userId;
            pack.userIp = userIp;
            common.SendMessage((int)OseeMsgCode.C_S_FISHING_GET_USER_IP_REQUEST, pack);
        }
        public static void Req_PlayerRoomStatusRequest()
        {
            var pack = new PlayerRoomStatusRequest();
            //if (Time.realtimeSinceStartup - PlayerData.CaoShiNum > 5f)
            //{
            //    Root3D.Instance.DebugString(PlayerData.PlayerId+common.GetBiaoShiDebug()+"发送请求Req_PlayerRoomStatusRequest"+ (Time.realtimeSinceStartup - PlayerData.CaoShiNum));                
            //}
            common.SendMessage((int)OseeMsgCode.C_S_TTMY_PLAYER_ROOM_STATUS_REQUEST, pack);
        }
        /// <summary>
        /// 绑定昵称
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="password"> 密码(32位md5小写密文)</param>
        /// <param name="phoneNum">手机账号</param>
        /// <param name="phoneCode">验证码</param>
        /// <param name="oldUsername"> 之前用户名</param>
        public static void Req_BangNiceNameRequest(string username, string password, string phoneNum, string phoneCode, string oldUsername)
        {
            var pack = new BangNiceNameRequest();
            pack.username = username;
            pack.password = password;
            pack.phoneNum = phoneNum;
            pack.phoneCode = phoneCode;
            pack.oldUsername = oldUsername;
            common.phoneNum = phoneNum;
            common.SendMessage((int)OseeMsgCode.C_S_BANG_NICKNAME_REQUEST, pack);
        }
        /// <summary>
        /// 绑定身份信息
        /// </summary>
        /// <param name="name"> 姓名</param>
        /// <param name="idcard"> 身份证</param>
        /// <param name="phoneNum">手机账号</param>
        /// <param name="phoneCode">验证码</param>
        public static void Req_BangIdCardAllRequest(string name, string idcard, string phoneNum, string phoneCode)
        {
            var pack = new BangIdCardAllRequest();
            pack.name = name;
            pack.idcard = idcard;
            pack.phoneNum = phoneNum;
            pack.phoneCode = phoneCode;
            common.SendMessage((int)OseeMsgCode.C_S_BANG_IDCARDALL_REQUEST, pack);
        }
        /// <summary>
        /// 修改登陆密码
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="oldPassword"> 旧密码(32位md5小写密文)</param>
        /// <param name="newPassword"> 新密码(32位md5小写密文)</param>
        /// <param name="phoneNum">手机账号</param>
        /// <param name="phoneCode">验证码</param>
        public static void Req_UpdateLoginPasswordRequest(string username, string newPassword, string phoneNum, string phoneCode)
        {
            var pack = new UpdateLoginPasswordRequest();
            pack.username = username;
            //pack.oldPassword = oldPassword;
            pack.newPassword = newPassword;
            pack.phoneNum = phoneNum;
            pack.phoneCode = phoneCode;
            common.SendMessage((int)OseeMsgCode.C_S_UPDATE_LOGIN_PASSWORD_REQUEST, pack);
        }
        /// <summary>
        /// 换绑个人信息
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="oldIdCardNo"> 旧身份证</param>
        /// <param name="oldPhoneNum">旧手机账号</param>
        /// <param name="oldPhoneCode">验证码</param>
        /// <param name="newPhoneNum"> 新手机</param>
        /// <param name="newIdCardNo"> 新身份证</param>
        /// <param name="newRealName">新姓名</param>
        /// <param name="newPhoneCode">验证码</param>
        public static void Req_UpdateIdCardNoRequest(string username, string oldIdCardNo, string oldPhoneNum, string oldPhoneCode, string newPhoneNum, string newIdCardNo, string newRealName, string newPhoneCode)
        {
            var pack = new UpdateIdCardNoRequest();
            pack.username = username;
            pack.oldIdCardNo = oldIdCardNo;
            pack.oldPhoneNum = oldPhoneNum;
            pack.oldPhoneCode = oldPhoneCode;
            pack.newPhoneNum = newPhoneNum;
            pack.newIdCardNo = newIdCardNo;
            pack.newRealName = newRealName;
            pack.newPhoneCode = newPhoneCode;
            common.SendMessage((int)OseeMsgCode.C_S_UPDATE_IDCARDNO_REQUEST, pack);
        }
        /// <summary>
        /// 换绑发送验证码
        /// </summary>
        /// <param name="phone">手机号</param>
        public static void Req_ChangePhoneRequest(string phone)
        {
            var pack = new ChangePhoneRequest();
            pack.phone = phone;
            common.SendMessage((int)OseeMsgCode.C_S_CHANGE_PHONE_REQUEST, pack);
        }
        /// <summary>
        /// 解绑微信
        /// </summary>
        public static void Req_DeleteWxNoRequest()
        {
            var pack = new DeleteWxNoRequest();
            common.SendMessage((int)OseeMsgCode.C_S_DELETE_WX_REQUEST, pack);
        }
        /// <summary>
        /// 用户在线码登录请求
        /// </summary>
        /// <param name="id"> 用户id</param>
        /// <param name="onlineToken"> 在线码</param>
        public static void Req_OnlineTokenLoginRequest(long id, string onlineToken)
        {
            var pack = new OnlineTokenLoginRequest();
            pack.id = id;
            pack.onlineToken = onlineToken;
            common.SendMessage((int)LoginMsgCode.C_S_ONLINE_TOKEN_LOGIN_REQUEST, pack);
        }
        ///// <summary>
        ///// 标准登录请求
        ///// </summary>
        ///// <param name="username"> 用户名</param>
        ///// <param name="password"> 密码(32位md5小写密文)</param>
        //public static void Req_CommonLoginRequest(string username, string password)
        //{

        //    common.OpenType = 1;
        //    common.OpenUsername = username;
        //    common.OpenPassw = password;
        //    Req_AllIsOpenRequest();
        //    //if (common.isOpen==1)
        //    //{       
        //    //    var pack = new CommonLoginRequest();
        //    //    pack.username = username;
        //    //    pack.password = password;
        //    //    common.SendMessage((int)LoginMsgCode.C_S_COMMON_LOGIN_REQUEST, pack);
        //    //}
        //}
        /// <summary>
        /// 游客登录请求
        /// </summary>
        /// <param name="touristCode"> 游客码(空值为服务器生成)</param>
        public static void Req_TouristLoginRequest(string touristCode)
        {

            common.OpenType = 2;
            common.OpenUsername = touristCode;
            common.OpenPassw = "";
            Req_AllIsOpenRequest();
            //if (common.isOpen == 1)
            //{
            //    var pack = new TouristLoginRequest();
            //    pack.touristCode = touristCode;
            //    common.SendMessage((int)LoginMsgCode.C_S_TOURIST_LOGIN_REQUEST, pack);
            //}
        }
        /// <summary>
        /// 获取服务器是否关服请求
        /// </summary>
        public static void Req_AllIsOpenRequest()
        {
            var pack = new AllIsOpenRequest();
            common.SendMessage((int)OseeMsgCode.C_S_ALL_IS_OPEN_REQUEST, pack);
        }

        /// <summary>
        /// 修改密码请求
        /// </summary>
        /// <param name="username"> 用户名</param>
        /// <param name="oldPassword"> 旧密码(32位md5小写密文)</param>
        /// <param name="newPassword"> 新密码(32位md5小写密文)</param>
        public static void Req_ChangePasswordRequest(string username, string oldPassword, string newPassword)
        {
            var pack = new ChangePasswordRequest();
            pack.username = username;
            pack.oldPassword = oldPassword;
            pack.newPassword = newPassword;
            common.SendMessage((int)LoginMsgCode.C_S_CHANGE_PASSWORD_REQUEST, pack);
        }
        /// <summary>
        /// 通过用户名登录请求
        /// </summary>
        /// <param name="username"> 登录的用户名</param>
        public static void Req_CommonNameLoginRequest(string username)
        {
            var pack = new CommonNameLoginRequest();
            pack.username = username;
            common.SendMessage((int)LoginMsgCode.C_S_COMMON_NAME_LOGIN_REQUEST, pack);
        }
        /// <summary>
        /// 微信code换取Token请求
        /// </summary>
        /// <param name="code"> 前端sdk获取的登录码</param>
        public static void Req_WxLoginCodeRequest(string code)
        {
            var pack = new WxLoginCodeRequest();
            pack.code = code;
            //common.SendMessage((int)LoginMsgCode.C_S_WX_LOGIN_CODE_REQUEST, pack);
            common.SendMessage((int)OseeMsgCode.C_S_WEIXIN_CODE_GET_REQUEST, pack);
        }
        /// <summary>
        /// 微信code换取Token请求new
        /// </summary>
        /// <param name="code"> 前端sdk获取的登录码</param>
        //public static void Req_WxLoginCodeRequestNew(string code)
        //{
        //    var pack = new com.maple.game.osee.proto.WxLoginCodeRequest();
        //    pack.code = code;
        //    common.SendMessage((int)OseeMsgCode.C_S_WEIXIN_CODE_GET_REQUEST, pack);
        //}
        /// <summary>
        /// 微信登录
        /// </summary>
        /// <param name="refreshToken"> 微信的刷新令牌</param>
        public static void Req_WxLoginRequest(string refreshToken)
        {
            var pack = new WxLoginRequest();
            pack.refreshToken = refreshToken;
            // common.SendMessage((int)LoginMsgCode.C_S_WX_LOGIN_REQUEST, pack);
            common.SendMessage((int)OseeMsgCode.C_S_WEIXIN_LOGIN_GET_REQUEST, pack);
        }
        ///// <summary>
        ///// 第一次进入游戏响应
        ///// </summary>
        ///// <param name="refreshToken"> 微信的刷新令牌</param>
        //public static void Req_FirstJoinResponse(string refreshToken)
        //{
        //    var pack = new FirstJoinResponse(); 
        //    pack.refreshToken = refreshToken;
        //    common.SendMessage((int)LoginMsgCode.C_S_WX_LOGIN_REQUEST, pack);
        //}
        /// <summary>
        /// 第一次进入游戏请求
        /// </summary>
        public static void Req_FirstJoinRequest(long varUserId)
        {
            var pack = new FirstJoinRequest();
            pack.userId = varUserId;
            common.SendMessage((int)OseeMsgCode.C_S_FIRST_JOIN_REQUEST, pack);
        }
    }
}
   
