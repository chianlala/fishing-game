using com.maple.common.lobby.proto;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;
using NetLib;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class LobbyController : BaseController
{
    public LobbyController()
    {
        Register((int)LobbyMsgCode.S_C_GET_USER_INFO_RESPONSE, typeof(GetUserInfoResponse), On_GetUserInfoResponse);//获取用户详情返回
        Register((int)LobbyMsgCode.S_C_WANDER_SUBTITLE_RESPONSE, typeof(WanderSubtitleResponse), On_WanderSubtitleResponse);//系统游走字幕返回
        Register((int)LobbyMsgCode.S_C_CHECK_VERSION_RESPONSE, typeof(CheckVersionResponse), On_CheckVersionResponse);//检查版本信息返回
    
        Register((int)OseeMsgCode.S_C_TTMY_RECHARGE_LIMIT_REST_RESPONSE, typeof(RechargeLimitRestResponse), On_RechargeLimitRestResponse);//玩家今日充值剩余限制金额响应

        Register((int)OseeMsgCode.S_C_TTMY_GET_RELIEF_MONEY_RESPONSE, typeof(GetReliefMoneyResponse), On_GetReliefMoneyResponse);//领取救济金响应

        Register((int)OseeMsgCode.S_C_TTMY_BOSS_BUGLE_BUY_LIMIT_RESPONSE, typeof(BossBugleBuyLimitResponse), On_BossBugleBuyLimitResponse);//玩家今日boss号角限购次数信息

        Register((int)OseeMsgCode.S_C_TTMY_GET_ADDRESS_RESPONSE, typeof(GetAddressResponse), On_GetAddressResponse);//获取收货地址响应
        Register((int)OseeMsgCode.S_C_TTMY_SET_ADDRESS_RESPONSE, typeof(SetAddressResponse), On_SetAddressResponse);//设置收货地址响应


        Register((int)OseeMsgCode.S_C_GET_FORGING_GROUP_RESPONSE, typeof(GetForgingGroupResponse), On_GetForgingGroupResponse);//获取锻造/合成/强化组合
        Register((int)OseeMsgCode.S_C_USE_FORGING_GROUP_RESPONSE, typeof(UseForgingGroupResponse), On_UseForgingGroupResponse);//使用锻造/合成/强化组合
        Register((int)OseeMsgCode.S_C_CHANGE_FORGING_GROUP_RESPONSE, typeof(ChangeForgingGroupResponse), On_ChangeForgingGroupResponse);//切换锻造/合成/强化组合

        Register((int)OseeMsgCode.S_C_BUY_ISFIRST_RESPONSE, typeof(BuyIsFirstResponse), On_BuyIsFirstResponse);//获取是否首充响应

    }

    /// <summary>
    /// 获取用户详情返回
    /// <summary>
    private void On_GetUserInfoResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetUserInfoResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetUserInfoResponse, this, pack);
    }
    /// <summary>
    /// 系统游走字幕返回
    /// <summary>
    private void On_WanderSubtitleResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<WanderSubtitleResponse>();
        UIGuangbo view = UIMgr.ShowUISynchronize(UIPath.UIGuangbo).GetComponent<UIGuangbo>();
        view.Init(pack.content, pack.level);
        UEventDispatcher.Instance.DispatchEvent(UEventName.WanderSubtitleResponse, this, pack);
    }
    /// <summary>
    /// 检查版本信息返回
    /// <summary>
    private void On_CheckVersionResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<CheckVersionResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.CheckVersionResponse, this, pack);
    }

    /// <summary>
    /// 玩家今日充值剩余限制金额响应
    /// <summary>
    private void On_RechargeLimitRestResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<RechargeLimitRestResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.RechargeLimitRestResponse, this, pack);
    }
    /// <summary>
    /// 领取救济金响应
    /// <summary>
    private void On_GetReliefMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetReliefMoneyResponse>();

        if (pack.money==0)
        {
            PlayerData.LastLottery = pack.restTimes;
            //if (pack.restTimes > 0)
            //{
            //    //可领救济金
            //    MessageBox.Show("你已破产，是否领取救济金!",null,()=> {
            //        NetMessage.OseeLobby.Req_PlayerMoneyRequest(2);
            //    });
            //}
            //else
            //{
            //    //不可领救济金
            //}
        }
        else
        {
            MessageBox.Show("你已破产，系统自动为你发救济金:x" + pack.money + "剩余救济金次数：x" + pack.restTimes);
            UEventDispatcher.Instance.DispatchEvent(UEventName.GetReliefMoneyResponse, this, pack);
        }
    }
    /// <summary>
    /// 玩家今日boss号角限购次数信息
    /// <summary>
    private void On_BossBugleBuyLimitResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BossBugleBuyLimitResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.BossBugleBuyLimitResponse, this, pack);
    }
    /// <summary>
    /// 获取收货地址响应
    /// <summary>
    private void On_GetAddressResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetAddressResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetAddressResponse, this, pack);
    }
    /// <summary>
    /// 设置收货地址响应
    /// <summary>
    private void On_SetAddressResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<SetAddressResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.SetAddressResponse, this, pack);
    }

    /// <summary>
    /// 获取锻造/合成/强化组合
    /// <summary>
    private void On_GetForgingGroupResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetForgingGroupResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetForgingGroupResponse, this, pack);
    }
    /// <summary>
    /// 使用锻造/合成/强化组合
    /// <summary>
    private void On_UseForgingGroupResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<UseForgingGroupResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.UseForgingGroupResponse, this, pack);
    }
    /// <summary>
    /// 切换锻造/合成/强化组合
    /// <summary>
    private void On_ChangeForgingGroupResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<ChangeForgingGroupResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.ChangeForgingGroupResponse, this, pack);
    }
    /// <summary>
    /// 获取是否首充响应
    /// <summary>
    private void On_BuyIsFirstResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<BuyIsFirstResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.BuyIsFirstResponse, this, pack);
    }

}
