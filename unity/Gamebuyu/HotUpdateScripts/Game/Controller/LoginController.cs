using System;
using System.Collections;
using System.Collections.Generic;
using com.maple.common.login.proto;
using com.maple.game.osee.proto;
using com.maple.network.proto;
using CoreGame;
using Game.UI;
using JEngine.Core;
using NetLib;
using UnityEngine;

//战绩回放等
public class LoginController:BaseController
{
	public LoginController()
    {
        Register((int)LoginMsgCode.S_C_GET_UNUSED_USERNAME_RESPONSE, typeof(GetUnusedUsernameResponse), On_GetUnusedUsernameResponse);//获取未使用的用户名返回
        Register((int)LoginMsgCode.S_C_COMMON_REGISTER_RESPONSE, typeof(CommonRegisterResponse), On_CommonRegisterResponse);//标准注册返回
        Register((int)OseeMsgCode.S_C_USE_REGISTER_RESPONSE, typeof(UserRegisterResponse), On_UserRegisterResponse);//昵称注册返回
        Register((int)LoginMsgCode.S_C_LOGIN_SUCCESS_RESPONSE, typeof(LoginSuccessResponse), On_LoginSuccessResponse);//用户登录成功返回
        Register((int)LoginMsgCode.S_C_TOURIST_LOGIN_RESPONSE, typeof(TouristLoginResponse), On_TouristLoginResponse);//游客登录返回
        Register((int)LoginMsgCode.S_C_CHANGE_PASSWORD_RESPONSE, typeof(ChangePasswordResponse), On_ChangePasswordResponse);//修改密码返回(成功时)
        Register((int)LoginMsgCode.S_C_LOGOUT_RESPONSE, typeof(LogoutResponse), On_LogoutResponse);//退出返回
        Register((int)LoginMsgCode.S_C_WX_LOGIN_CODE_RESPONSE, typeof(WxLoginCodeResponse), On_WxLoginCodeResponse);//微信code换取Token响应
        Register((int)LoginMsgCode.S_C_WX_LOGIN_REAUTH_RESPONSE, typeof(WxLoginReAuthResponse), On_WxLoginReAuthResponse);//通知前端重新授权的响应                                                                                                                        
        Register((int)OseeMsgCode.S_C_ALL_IS_OPEN_RESPONSE, typeof(AllIsOpenResponse), On_AllIsOpenResponse);//获取服务器是否开服返回
        Register((int)OseeMsgCode.S_C_TTMY_PLAYER_ROOM_STATUS_RESPONSE, typeof(PlayerRoomStatusResponse), On_PlayerRoomStatusResponse);//
    }
    private void On_PlayerRoomStatusResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PlayerRoomStatusResponse>();
        if (pack.index == 0)
        {
            Root3D.Instance.DebugString(PlayerData.PlayerId + "[客户端 状态已变更 回到大厅]");
            UIMgr.ShowUI(UIPath.UIMainMenu);
            UIMgr.CloseAllwithOutTwo(UIPath.UIMainMenu,UIPath.UIMessageBox);
        }
    }
    /// <summary>
    /// 获取服务器是否开服返回
    /// <summary>
    private void On_AllIsOpenResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<AllIsOpenResponse>();

        common.isOpen = pack.isOpen;
        if (pack.isOpen == 0)
        {
            MessageBox.ShowPopOneMessage("正在维护中预计开启时间：" + pack.createTime);
        }
        else
        {
            if (common.OpenType == 1)
            {
                var pack01 = new CommonLoginRequest();
                pack01.username = common.OpenUsername;
                pack01.password = common.OpenPassw;
                common.SendMessage((int)LoginMsgCode.C_S_COMMON_LOGIN_REQUEST, pack01);

            }
            else if (common.OpenType == 2)
            {
                var pack03 = new TouristLoginRequest();
                pack03.touristCode = common.OpenUsername;
                common.SendMessage((int)LoginMsgCode.C_S_TOURIST_LOGIN_REQUEST, pack03);
            }
            else
            {
                Debug.Log("错误类型" + common.OpenType);
            }
        }
        UEventDispatcher.Instance.DispatchEvent(UEventName.AllIsOpenResponse, this, pack);
    }
    /// <summary>
    /// 提示消息返回
    /// <summary>
    private void On_HintMessageResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<HintMessageResponse>();
        //提示一条不上浮
        if (pack.level==10)
        {
            MessageBox.ShowPopOneMessage(pack.content);
        }
        else
        {
            MessageBox.Show(pack.content);
        }
        Debug.Log(pack.level+"" +pack.content);
    }
    /// <summary>
    /// 微信code换取Token响应
    /// <summary>
    private void On_WxLoginCodeResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<WxLoginCodeResponse>();
    
    }
    /// <summary>
    /// 通知前端重新授权的响应
    /// <summary>
    private void On_WxLoginReAuthResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<WxLoginReAuthResponse>();
    
    }


    /// <summary>
    /// 获取未使用的用户名返回
    /// <summary>
    private void On_GetUnusedUsernameResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetUnusedUsernameResponse>();

    }
    /// <summary>
    /// 标准注册返回
    /// <summary>
    private void On_CommonRegisterResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<CommonRegisterResponse>();

    
    }

    private void On_UserRegisterResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UserRegisterResponse>();
        NetMessage.Login.Req_CommonLoginRequest(pack.username, pack.password, true, false);
    }
    /// <summary>
    /// 用户登录成功返回
    /// <summary>
    private void On_LoginSuccessResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<LoginSuccessResponse>();
        if (pack==null)
        {
            Debug.Log("LoginSuccessResponse is null");
            return;
        }
        if (common2.LoginMask != null)
        {
            common2.LoginMask.gameObject.SetActive(false);
            //Debug.Log(pack.code);
        }
        if (pack.code==200)
        {
            common.IsLoginState = true;
            Debug.Log("登陆成功");
            if (PlayerData.Acount != "")
            {
                if (common.IsSavePwd == 0)
                {
                    PlayerPrefs.SetString("Acc", PlayerData.Acount);
                    PlayerPrefs.SetString("Psw", PlayerData.PassWord);
                }
                else
                {
                    PlayerPrefs.SetString("Acc", PlayerData.Acount);
                    PlayerPrefs.SetString("Psw", "");
                }
            }

            PlayerData.NickName = pack.name;
            PlayerData.StrHeadUrl = pack.headUrl;
            PlayerData.PlayerId = pack.id;

            //UIMgr.ShowUI(UIPath.UIMainMenu);
            //UIMgr.CloseUI(UIPath.UILogin);
            //UIMgr.CloseUI(UIPath.UIChangePaoTai);
            Root3D.Instance.DebugString(PlayerData.PlayerId + "[登陆成功]");
            Root3D.Instance.DebugString(PlayerData.PlayerId + "isConnect" + pack.isConnect);
            Root3D.Instance.DebugString(PlayerData.PlayerId + "roomIndex" + pack.roomIndex);
            if (pack.isConnect)
            {
                if (pack.roomIndex == 0)
                {
                    UIMgr.ShowUI(UIPath.UIMainMenu);
                    UIMgr.CloseAllwithOut(UIPath.UIMainMenu);
                }
            }
            else
            {
                UIMgr.ShowUI(UIPath.UIMainMenu);
                UIMgr.CloseAllwithOut(UIPath.UIMainMenu);
            }
            //NetMessage.OseeLobby.Req_PlayerMoneyRequest(0);

            NetMessage.OseeFishing.Req_PlayerPropRequest();
            NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);

            //参数保存
            //PlayerPrefs.SetString("UserName", pack.touristCode);
            PlayerAccount varAccount = new PlayerAccount();

            varAccount.Account = PlayerData.Acount;
            varAccount.PassWorld = PlayerData.PassWord;
            if (varAccount.Account != "")
            {
                if (UserInfo.playerData.list_Account.Contains(varAccount))
                {
                    //UserInfo.playerData.list_Account.Remove(varAccount);
                    //UserInfo.playerData.list_Account.Add(varAccount);
                    //UserInfo.SavePlayerData();
                }
                else
                {
                    UserInfo.playerData.list_Account.Add(varAccount);
                    UserInfo.SavePlayerData();
                }
            }
            common.LoginState = 2;
            //是否在房间内
            NetMessage.OseeFishing.Req_IsInFishingRoomRequest();
            common.IsAlreadyLogin = true;
        }
        else if (pack.code==300)
        {
            MessageBox.Show("预计开服时间"+ pack.name+pack.id);
            if (common2.LoginMask != null)
            {
                common2.LoginMask.gameObject.SetActive(false);
            }
        }
        else
        {
            if (common2.LoginMask != null)
            {
                common2.LoginMask.gameObject.SetActive(false);
            }
        }
    }
    /// <summary>
    /// 游客登录返回
    /// <summary>
    private void On_TouristLoginResponse(NetMsgPack obj)
    {
        Debug.Log("sdiao");
        var pack = obj.GetData<TouristLoginResponse>();
        //PlayerPrefs.SetString("UserName", pack.touristCode);
        PlayerAccount varAccount = new PlayerAccount();

        varAccount.Account = pack.touristCode;
        varAccount.PassWorld = "";
        if (varAccount.Account != "")
        {
            if (UserInfo.playerData.list_Account.Contains(varAccount))
            {
                //UserInfo.playerData.list_Account.Remove(varAccount);
                //UserInfo.playerData.list_Account.Add(varAccount);
                //UserInfo.SavePlayerData();
            }
            else
            {
                UserInfo.playerData.list_Account.Add(varAccount);
                UserInfo.SavePlayerData();
            }
        }
        common.LoginState = 2;
        UEventDispatcher.Instance.DispatchEvent(UEventName.TouristLoginResponse, this, pack);

    }
    /// <summary>
    /// 修改密码返回(成功时)
    /// <summary>
    private void On_ChangePasswordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChangePasswordResponse>();
       
    }
    /// <summary>
    /// 退出返回
    /// <summary>
    private void On_LogoutResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<LogoutResponse>();
        if (pack.result == 1)
        {
            MessageBox.Show("你的账号已经在其他设备登录，如果这不是你进行的操作。请尽快修改你的账户密码！","提示");
            //UIMgr.ShowUI(UIPath.UILogin);
            //NetMgr.Instance.OnZhuxiao();
            common3.CloseLoginZhuXiao();
        }
        else if (pack.result == 2)
        {
            MessageBox.Show("服务器维护中！请稍候登录..", "提示");
            //UIMgr.ShowUI(UIPath.UILogin);
            //NetMgr.Instance.OnZhuxiao();
            common3.CloseLoginZhuXiao();
        }
    }
}
