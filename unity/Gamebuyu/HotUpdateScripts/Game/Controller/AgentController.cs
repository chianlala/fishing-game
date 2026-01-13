using NetLib;
using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fruit;
using com.maple.game.osee.proto.agent;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using Game.UI;

public class AgentController : BaseController 
{ 
	public AgentController()    
    {              
        //设置账号
        Register((int)OseeMsgCode.S_C_TTMY_ACCOUNT_PHONE_CHECK_RESPONSE, typeof(AccountPhoneCheckResponse), On_AccountPhoneCheckResponse);//设置账号时手机号验证返回
        Register((int)OseeMsgCode.S_C_TTMY_ACCOUNT_SET_RESPONSE, typeof(AccountSetResponse), On_AccountSetResponse);//设置账号返回
        Register((int)OseeMsgCode.S_C_GET_USER_RECHARGE_MONEY_REWORD_RESPONSE, typeof(GetUserRechargeMoneyRewordResponse), On_GetUserRechargeMoneyRewordResponse);//获取玩家充值返利奖励返回
    }
    /// <summary>
    /// 获取玩家充值返利返回
    /// <summary>
    private void On_GetUserRechargeMoneyResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetUserRechargeMoneyResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetUserRechargeMoneyResponse, this, pack);
    }
    /// <summary>
    /// 获取玩家充值返利奖励返回
    /// <summary>
    private void On_GetUserRechargeMoneyRewordResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<GetUserRechargeMoneyRewordResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.GetUserRechargeMoneyRewordResponse, this, pack);
    }

    //设置账号 ------------------->
    /// <summary>
    /// 设置账号时手机号验证返回
    /// <summary>
    private void On_AccountPhoneCheckResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<AccountPhoneCheckResponse>();
        UEventDispatcher.Instance.DispatchEvent(UEventName.AccountPhoneCheckResponse, this, pack);
    }
    /// <summary>
    /// 设置账号返回
    /// <summary>
    private void On_AccountSetResponse(NetMsgPack obj)
    {
        var pack = obj.GetData<AccountSetResponse>();
        if (common.phoneNum!=null)
        {
            PlayerData.PhoneNum = common.phoneNum;
        }        
        UEventDispatcher.Instance.DispatchEvent(UEventName.AccountSetResponse, this, pack);
    }

}
