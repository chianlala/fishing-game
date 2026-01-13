using UnityEngine;
using System.Collections;
using NetLib;
using System;
using JEngine.Examples;

public interface IController
{

}

public abstract class Controller
{
    protected abstract void ProcessNetMessagePack(NetMsgPack pack);
}

public abstract class BaseController : IController
{
    protected void Register(int cmd, Type type, Action<NetMsgPack> handler)
    {
        ControllerMgr.Instance.RegisterController(cmd, handler);
        if (NetMgr.Instance!=null)
        {
            NetMgr.Instance.RegisterCmd(cmd, type);
        }
    }
}