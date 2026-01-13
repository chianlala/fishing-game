using com.maple.network.proto;
using CoreGame;
using Game.UI;
using NetLib;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class NetworkController : BaseController
{
    public NetworkController()
    {
        Register((int)NetworkMsgCode.S_C_PING_RESPONSE, typeof(PingResponse), On_PingResponse);//网络诊断返回
        Register((int)NetworkMsgCode.S_C_HEART_BEAT_RESPONSE, typeof(HeartBeatResponse), On_HeartBeatResponse);//心跳返回
        Register((int)NetworkMsgCode.S_C_HINT_MESSAGE_RESPONSE, typeof(HintMessageResponse), On_HintMessageResponse);//提示消息返回
        Register((int)NetworkMsgCode.S_C_GET_CONNECT_TOKEN_RESPONSE, typeof(GetConnectTokenResponse), On_GetConnectTokenResponse);//获取连接token返回
        Register((int)NetworkMsgCode.S_C_INVALID_TOKEN_RESPONSE, typeof(InvalidTokenResponse), On_InvalidTokenResponse);//无效token返回
    }
    /// <summary>
    /// 网络诊断返回
    /// <summary>
    private void On_PingResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<PingResponse>();
        common.bWaiting = false;
        UEventDispatcher.Instance.DispatchEvent(UEventName.PingResponse, this, pack);
    }
    /// <summary>
    /// 心跳返回
    /// <summary>
    private void On_HeartBeatResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<HeartBeatResponse>();
        //common.bWaiting = !common.bWaiting;
        common.HeartTime = 0;
       // UIRoot.Instance.StartHeart();
        NetMessage.Network.Req_HeartBeatRequest();
        //UEventDispatcher.Instance.DispatchEvent(UEventName.HeartBeatResponse, this, pack);
    }
  

    /// <summary>
    /// 提示消息返回
    /// <summary>
    private void On_HintMessageResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<HintMessageResponse>();
        if (pack.level==10)
        {
            MessageBox.ShowPopOneMessage(pack.content);
            return;
        }
 
        string strTitle = "";
        switch(pack.level)
        {
            case 0:
                strTitle = "系统提示";
                break;
            case 1:
                strTitle = "系统警告";
                break;
            case 2:
                strTitle = "系统错误";
                break;
        }
        if (pack.content == "请设置账号后重试")
        {
            MessageBox.Show(pack.content, strTitle, () =>
            {
                UIMgr.ShowUI(UIPath.UISetAccount);

            }, () => { });
            return;
        }
        if (pack.content=="请设置收货地址后再兑换")
        {
            //MessageBox.Show(pack.content, strTitle, () => {
            //    UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>().SetAddress();
            //}, () => { });
            //UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            MessageBox.Show(pack.content, strTitle, () =>
            {
                EventManager.OpenAddress?.Invoke();

            }, () => { });
            UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            return;
        }
        if (pack.content == "剩余奖券不足，无法兑换")
        {
            MessageBox.ShowPopOneMessage(pack.content);
            UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            return;
        }
        if (pack.content == "您的钻石不足，无法购买")
        {
            MessageBox.ShowConfirm(pack.content, strTitle, () => {
                var goShop = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                goShop.Setpanel(JieMain.钻石商城);
            }, () => { });
            UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            return;
        }
        if (pack.content == "钻石不足，无法购买")
        {
            MessageBox.ShowConfirm(pack.content, strTitle, () => {
                var goShop = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                goShop.Setpanel(JieMain.钻石商城);

            }, () => { });
            UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            return;
        }
        //if (pack.content == "龙晶不足,请前往充值")
        //{
        //    GameObject go = MessageBox.Show("龙晶不足是否里立即兑换？", "", () =>
        //    {
        //        UIMgr.ShowUI(UIPath.UIDragonScale);
        //    });
        //    if (common3._UIFishingInterface != null)
        //    {
        //        if (common3._UIFishingInterface.GetOnePlayer(PlayerData.PlayerId) != null)
        //        {
        //            common3._UIFishingInterface.GetOnePlayer(PlayerData.PlayerId).IsHaveMoney = false;
        //        }

        //    }
        //    UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
        //    return;
        //}
        if (pack.content == "金币不足,请前往充值")
        {
            GameObject go = MessageBox.Show("你已破产,是否前往商城购买金币？", "", () =>
            {
                // NetMessage.OseeFishing.Req_FishingExitRoomRequest();
                var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                //tmp.Setpanel(JieMain.金币商城);
                tmp.Setpanel(JieMain.道具商城);
            }, () => { });

            //var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
            ////tmp.Setpanel(JieMain.金币商城);
            //tmp.Setpanel(JieMain.道具商城);

            if (common3._UIFishingInterface != null)
            {
                if (common3._UIFishingInterface.GetOnePlayer(PlayerData.PlayerId) != null)
                {
                    common3._UIFishingInterface.GetOnePlayer(PlayerData.PlayerId).StopSkill();// = false;
                }
            }
            UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
            return;
        }
        if (pack.content == "你的账号已在其他设备登录，你已被迫下线")
        {
            MessageBox.Show(pack.content, strTitle, () => { }, () => { });
            UIMgr.ShowUI(UIPath.UILogin);
            return;
        }
        MessageBox.Show(pack.content,strTitle, () => { }, ()=> { });
        UEventDispatcher.Instance.DispatchEvent(UEventName.HintMessageResponse, this, pack);
    }
    /// <summary>
    /// 获取连接token返回
    /// <summary>
    private void On_GetConnectTokenResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetConnectTokenResponse>();
        common.token = pack.token;
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetConnectTokenResponse, this, pack);
    }
    /// <summary>
    /// 无效token返回
    /// <summary>
    private void On_InvalidTokenResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<InvalidTokenResponse>();
        Debug.LogError("无效token:"+ pack.token);
        MessageBox.Show("无效token");
        //UEventDispatcher.Instance.DispatchEvent(UEventName.InvalidTokenResponse, this, pack);
    }


}
