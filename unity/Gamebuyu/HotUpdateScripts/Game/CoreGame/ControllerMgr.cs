using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using NetLib;
using JEngine.Examples;
using JEngine.Core;

public class ControllerMgr : Singleton<ControllerMgr>
{
    private Dictionary<int, Action<NetMsgPack>> dic = new Dictionary<int, Action<NetMsgPack>>();
    private Queue<NetMsgPack> que_waitPack = new Queue<NetMsgPack>();

    private bool _WaitForLoading = false;
    /// <summary>
    /// 是否等待分发
    /// </summary>
    public bool WaitForLoading
    {
        get { return _WaitForLoading; }
        set
        {
            _WaitForLoading = value;
            if (!_WaitForLoading)
            {
                while (que_waitPack.Count > 0)
                {
                    NetMsgPack pack = que_waitPack.Dequeue();
                    var action = dic[pack.MsgId];
                    action.Invoke(pack);
                }
            }
        }
    }

    public void RegisterController(int msgId, Action<NetMsgPack> controller)
    {
        if (dic.ContainsKey(msgId))
        {
            //Debug.LogWarning(string.Format("CmdType:{2} {0}({1}) 已存在，注册多个消息处理函数无效！！！", GameHelper.Convert0x(msgId), msgId, (CmdType)msgId));
            return;
        }

        dic.Add(msgId, controller);
    }
    public void RemoveController(int msgId)
    {
        if (dic.ContainsKey(msgId))
            dic.Remove(msgId);
    }
    public void ProcessPack(NetMsgPack pack)
    {
        if (pack == null)
            return;

        int msgId = pack.MsgId;

        if (dic.ContainsKey(msgId))
        {
            var action = dic[msgId];
            if (GameStats.GameDebug)
            {
                if (msgId != (int)com.maple.network.proto.NetworkMsgCode.S_C_HEART_BEAT_RESPONSE)//不是心跳
                Debug.Log(string.Format("处理消息 {0}({1}), 处理脚本 {2}, 处理方法:{3}", msgId, GameHelper.Convert0x(msgId), action.Target, action.Method));
            }

            if (WaitForLoading)
            {
                que_waitPack.Enqueue(pack);
            }
            else
            {
                if (action!=null)
                {
                    action.Invoke(pack);
                }                
            }
        }
    }
}
