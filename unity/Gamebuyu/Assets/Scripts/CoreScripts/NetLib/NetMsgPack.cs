using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NetLib
{
    /// <summary>
    /// 表示一个网络消息包
    /// </summary>
    public class NetMsgPack
    {
        /// <summary>
        /// 消息ID,协议号
        /// </summary>
        public int MsgId { get; private set; }
        /// <summary>
        /// 包数据
        /// </summary>
        public object Data { get; private set; }

        public NetMsgPack(int msgId, object data)
        {
            MsgId = msgId;
            Data = data;
        }
        public T GetData<T>()
        {
            //return (T)Data;
            return (T)NetHelper.DeSerializeBytes(Data as byte[],typeof(T));
            //return NetHelper.DeSerializeBytes<T>(Data as byte[]);
        }
    }
}
