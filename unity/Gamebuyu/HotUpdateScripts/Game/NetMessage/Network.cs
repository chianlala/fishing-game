using com.maple.network.proto;
using CoreGame;
using Game.UI;

namespace NetMessage
{
    public abstract class Network
    {
        /// <summary>
        /// 网络诊断请求
        /// </summary>
        /// <param name="pingTime"> 请求时间</param>
        public static void Req_PingRequest(long pingTime)
        {
            var pack = new PingRequest();
            pack.pingTime = pingTime;
            common.SendMessage((int)NetworkMsgCode.C_S_PING_REQUEST, pack);
        }
        /// <summary>
        /// 心跳请求  服务端请求心跳 客户端返回
        /// </summary>
        public static void Req_HeartBeatRequest()
        {
            var pack = new HeartBeatRequest();     
           
            common.SendMessage((int)NetworkMsgCode.C_S_HEART_BEAT_REQUEST, pack);
        }
        /// <summary>
        /// 获取连接token请求
        /// </summary>
        public static void Req_GetConnectTokenRequest()
        {
            var pack = new GetConnectTokenRequest();
            common.SendMessage((int)NetworkMsgCode.C_S_GET_CONNECT_TOKEN_REQUEST, pack);
        }
    }
}