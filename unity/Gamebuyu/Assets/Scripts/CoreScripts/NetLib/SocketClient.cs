using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;
//using com.loncent.protocol;
using ProtoBuf;
using com.maple.network.proto;
using UnityEngine;
using com.maple.game.osee.proto;
using com.maple.common.login.proto;
using JEngine.Core;
//using com.lyh.protocol;

namespace NetLib
{
    public class SocketClient
    {
        public int nNowSocketID;
        //public const int max_buffer_size = 1024 * 1024;
        public const int max_buffer_size = 256 * 1024;
        string server_ip;
        int server_port;
        int _connectTimeoutSec = 5; //连接超时时间
        Socket _socket = null;  //socket实例
        //Dictionary<int, Type> msgDic = new Dictionary<int, Type>();
        //Queue<NetMsgPack> _msgQueue = new Queue<NetMsgPack>();
        Thread receiveThread = null;    //接收数据线程
        ManualResetEvent _connectTimeOutObj = new ManualResetEvent(false);

        public Action onConnectEvent;
        public Action onCloseEvent;
        public bool IsReceiveEnd = false; //中断接收消息循环

        public int isConnectEorro = 0; //中断接收消息循环 
        bool isHaveEprro = false;
        public int ConnectTimeoutSeconds
        {
            get { return _connectTimeoutSec; }
            set { _connectTimeoutSec = value; }
        }
        public bool IsConnected
        {
            get
            {
                return _socket != null && _socket.Connected;
            }
        }
        /// <summary>
        /// 异步连接服务器
        /// </summary>
        /// <param name="ip"></param>
        /// <param name="port"></param>
        public void Connect(string ip, int port)
        {
            try
            {
                server_ip = ip;
                server_port = port;
                //DebugSystem.Log("AsyncConnect当前ConnectID：" + Thread.CurrentThread.ManagedThreadId);
                //DebugSystem.Log(string.Format("尝试连接服务器...{0}：{1}", server_ip, server_port));
                commonunity.ListLog.Add(string.Format("尝试连接服务器...{0}：{1}", server_ip, server_port));
                IPAddress ipAdd = IPAddress.Parse(ip);
                IPEndPoint endpoint = new IPEndPoint(ipAdd, port);
                _socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                _socket.ReceiveBufferSize = 1024 * 1024;
                _connectTimeOutObj.Reset();
                _socket.BeginConnect(endpoint, AsyncConnectCallback, _socket);//启用异步回调
                _connectTimeOutObj.WaitOne(_connectTimeoutSec * 1000, false); //不用新线程会卡死主线程
                //等待5秒后查看是否是连接状态
                if (_socket.Connected)
                {
                }
                else
                {
                    ChangeErro(100, "连接服务器超时...");
                }
            }
            catch (Exception ex)
            {
                ChangeErro(101, "启动连接出错..." + ex.Message);
            }
        }

        void ChangeErro(int errorMa, string message)
        {
            if (nNowSocketID == commonunity.nNowSocketID)
            {
                if (isHaveEprro)
                {
                    return;
                }
                isHaveEprro = true;
                //出现这些情况都可以关闭这个Socket了
                commonunity.ListLog.Add(message);

                //  状态 代码
                //  ChangeErro(100, "连接服务器超时...");
                //  ChangeErro(101, "启动连接出错..." + ex.Message);
                //  ChangeErro(103, "成功回调方法未注册...");
                //  ChangeErro(104, "连接服务器失败...");
                //  ChangeErro(106, "[主动断开当前Socket]");
                //  ChangeErro(108, "中断Abort抛出异常 Exception!ex:" + ex.ToString());
                //  ChangeErro(109, "解析长度出错 抛弃本条消息" + length);
                //  ChangeErro(111, "无法解析的消息 抛弃本条消息" + length);
                //  ChangeErro(113, "接收处错误SocketException：" + ex.Message);
                //  ChangeErro(114, "接收处错误Exception：" + ex.Message);
                //错误码
                commonunity.isConnectEorro = errorMa;

            }
            else
            {
                Debug.Log("已抛弃" + message);
            }
        }


        private void AsyncConnectCallback(IAsyncResult ar)//异步连接回调
        {
            try
            {
                _connectTimeOutObj.Set();
                Socket sock = ar.AsyncState as Socket;
                if (sock.Connected)
                {
                    IsReceiveEnd = false;
                    receiveThread = new Thread(new ThreadStart(ReceiveThread));//启用接收线程
                    receiveThread.IsBackground = true; //为true线程才会随着主线程的退出而退出。
                    receiveThread.Start();

                    if (onConnectEvent != null)
                    {
                        //是当前的
                        if (nNowSocketID == commonunity.nNowSocketID)
                        {
                            onConnectEvent();
                            commonunity.ListLog.Add("缓冲区大小..." + _socket.ReceiveBufferSize);
                        }
                    }
                    else
                    {
                        ChangeErro(103, "成功回调方法未注册...");
                    }
                }
                else
                {
                    ChangeErro(104, "连接服务器失败...");
                }
                sock.EndConnect(ar);  //如果 5秒内关闭这个Socket 就会报错
            }
            catch (Exception ex)
            {

                //这里必须try 而且不用重连
                // ChangeErro("异步连接服务器失败:" + ex);                
            }
        }

        /// <summary>
        /// 关闭socket
        /// </summary>
        public void CloseSocket()
        {
            ChangeErro(106, "[主动断开当前Socket]");
            IsReceiveEnd = true; //终止此线程中的循环
            //关闭socket
            if (_socket != null)
            {
                //安全关闭
                try
                {
                    _socket.Shutdown(SocketShutdown.Both);
                }
                catch
                {
                }
                _socket.Close();
                try
                {
                    if (receiveThread != null)
                    {
                        receiveThread.Abort();
                    }
                }
                catch (ThreadAbortException ex)
                {
                    //不进行操作
                }
                catch (Exception ex)
                {
                    ChangeErro(108, "中断Abort抛出异常 Exception!ex:" + ex.ToString());
                }
                finally
                {
                    if (receiveThread != null)
                    {
                        receiveThread.Abort();
                    }
                }
            }
        }
        /// <summary>
        /// 发送一个消息包
        /// </summary>
        /// <param name="cmd"></param>
        /// <param name="msg"></param>
        public void SendMsg(int cmd, IExtensible msg)
        {
            if (_socket != null && _socket.Connected)//这个是防止报错 程序无法执行
            {
                //这里使用MinaMessage在外面包装消息内容，达到加密的效果
                //DebugSystem.Log("请求线程ID：" + Thread.CurrentThread.ManagedThreadId+ " cmd" + cmd);
                BaseMessage sendmsg = new BaseMessage();
                sendmsg.code = cmd;
                sendmsg.body = NetHelper.SerializeObject(msg);
                byte[] bytes = CreateNetMsgPack(sendmsg, cmd);
                // DebugSystem.Log(msg.ToString() + "长度：" + bytes.Length);
                if (bytes == null)
                {
                    return;
                }
                _socket.Send(bytes);
            }
            else
            {
                //commonunity.ListLog.Add(string.Format("未连通的时的请求{0}", cmd));
            }
        }
        //创建一个具体内容消息包，包含长度和内容
        byte[] CreateNetMsgPack(IExtensible msgData, int cmd)
        {
            try
            {
                byte[] msg = NetHelper.SerializeObject(msgData);
                int length = msg.Length;
                byte[] buffer = new byte[length + 4];
                byte[] lenArr = NetHelper.IntToBytes(length);
                lenArr.CopyTo(buffer, 0);
                msg.CopyTo(buffer, 4);
                return buffer;
            }
            catch (Exception e)
            {
                //commonunity.ListLog.Add(string.Format("createNetMessagePack Exception!error:{0}", e.ToString()));
                //commonunity.ListLog.Add(string.Format("createNetMessagePack Exception!cmd:{0}", cmd));
                //Root3D.Instance.DebugString(string.Format("createNetMessagePack Exception!error:{0}", e.ToString()));
            }
            return null;
        }
        private void ReceiveThread()
        {
            //定义缓冲区
            int nRecvState = 0;//0正常 1处理中断包 2处理完成 3处理连包
            byte[] oldBuffer = new byte[max_buffer_size];
            int recvLenth = 0;
            int leftLenth = 0;
            int length = 0;//定义的消息长度
            byte[] buffer = new byte[max_buffer_size];

            while (true)
            {
                if (IsReceiveEnd)
                {
                    break;
                }
                //DebugSystem.Log("当前线程ID：" + Thread.CurrentThread.ManagedThreadId);
                if (_socket == null)
                {
                    if (onCloseEvent != null)
                    {
                        //是当前的
                        if (nNowSocketID == commonunity.nNowSocketID)
                        {
                            onCloseEvent();
                        }
                    }
                    break;
                }
                try
                {
                    Array.Clear(buffer, 0, max_buffer_size);
                    //这里因为是另外开启线程进行处理，所以此处用同步方法
                    int count = _socket.Receive(buffer);
                    //DebugSystem.Log("run ReceiveThread:" + count);
                    int nCheck = 4;
                    if (nRecvState > 0)
                    {
                        nCheck = 0;
                    }
                    if (count > nCheck)
                    {
                        int offset = 0;
                        do
                        {
                            if (IsReceiveEnd)
                            {
                                break;
                            }
                            if (nRecvState == 0)//处理包头
                            {
                                // 开始解析服务端发下来的数据  （长度+内容 格式）必须与服务器约定的一致
                                byte[] lengthArr = new byte[4];
                                //从buffer的offset位置开始，复制到lengthArr中
                                Array.Copy(buffer, offset, lengthArr, 0, 4);
                                offset += 4;
                                length = NetHelper.BytesToInt(lengthArr);
                                if (length > 3000000)//解析长度出错 抛弃本条消息
                                {
                                    ChangeErro(109, "解析长度出错 抛弃本条消息" + length);
                                    break;
                                }
                                //检查是否断包
                                if (count < length + offset)
                                {
                                    //包未收完 准备处理断包
                                    nRecvState = 1;
                                    recvLenth = count - offset;
                                    leftLenth = length - recvLenth;
                                    //DebugSystem.Log(string.Format("开始处理断包count:{0},leftLenth:{1},recvLenth:{2},totalLen:{3},offset:{4}", count, leftLenth, recvLenth, length, offset));
                                    Array.Copy(buffer, offset, oldBuffer, 0, recvLenth);
                                    offset += recvLenth;
                                }
                            }
                            else if (nRecvState == 1)//断包处理
                            {
                                if (count < leftLenth)
                                {
                                    //继续处理断包                               
                                    Array.Copy(buffer, 0, oldBuffer, recvLenth, count);
                                    recvLenth += count;
                                    leftLenth -= count;
                                    offset += count;
                                    //DebugSystem.Log(string.Format("继续处理断包count:{0},leftLenth:{1},recvLenth:{2},length:{3},offset:{4}", count, leftLenth, recvLenth, length,offset));
                                }
                                else
                                {
                                    nRecvState = 2;
                                    //完成处理断包                                      
                                    Array.Copy(buffer, 0, oldBuffer, recvLenth, leftLenth);
                                    //DebugSystem.Log(string.Format("完成处理断包count:{0},leftLenth:{1},recvLenth:{2},length:{3},offset:{4}", count, leftLenth, recvLenth, length,offset));
                                    offset += leftLenth;
                                    //清空断包缓存数据
                                    recvLenth = 0;
                                    leftLenth = 0;
                                }
                            }

                            //接收消息完成 处理消息
                            if (nRecvState != 1)
                            {
                                byte[] msg = new byte[length];
                                //从buffer的offset位置开始，复制到msg中
                                if (nRecvState == 2)
                                {
                                    Array.Copy(oldBuffer, 0, msg, 0, length);
                                    Array.Clear(oldBuffer, 0, max_buffer_size);
                                }
                                else
                                {
                                    Array.Copy(buffer, offset, msg, 0, length);
                                    offset += length;
                                }


                                BaseMessage obj = (BaseMessage)NetHelper.DeSerializeBytes(msg, typeof(BaseMessage));
                                if (obj == null)
                                {
                                    ChangeErro(111, "无法解析的消息 抛弃本条消息" + length);
                                }
                                else
                                {
                                    int command = obj.code;
                                    //DebugSystem.LogWarning((OseeMsgCode)(obj.code) + "长度"+ msg.Length);
                                    // DebugSystem.LogWarning(obj.body.ToString() + "长度" + msg.Length);
                                    Type t;
                                    if (commonunity.msgDic.TryGetValue(command, out t))
                                    {
                                        //object body = NetHelper.DeSerializeBytes(obj.body, t);
                                        // 添加到本地消息队列中
                                        EnterMsgQuene(new NetMsgPack(command, obj.body));
                                        //DebugSystem.Log(string.Format("消息解析成功,cmd:{0}", command));
                                    }
                                    else
                                    {
                                        DebugSystem.Log(string.Format("消息解析错误,cmdType没有注册!cmd:{0}({1})", command, Convert0x(command)));
                                    }
                                    nRecvState = 0;
                                }

                            }
                            if (count - offset > 4)
                            {
                                //DebugSystem.Log(string.Format("处理连包 count:{0} offset:{1} count-offset:{2}", count, offset, count - offset)); 
                            }

                        } while (count - offset > 4);//处理连包
                    }

                }
                catch (SocketException ex)
                {
                    ChangeErro(113, "接收处错误SocketException：" + ex.Message);
                    break;
                }
                catch (Exception ex)
                {
                    //主动断开这个Socket 则意外中止是必报错的 就可以中止这个循环了  不需要在这里重连
                    ChangeErro(115, "接收处错误Exception：" + ex.Message);
                    break;
                }
            }
        }
        //注册协议

        void EnterMsgQuene(NetMsgPack pack)
        {
            if (pack == null)
                return;

            lock (commonunity._msgQueue)
            {
                commonunity._msgQueue.Enqueue(pack);
                //DebugSystem.Log("_msgQueue.Count:" + _msgQueue.Count);
            }
        }
        /// <summary>
        /// 从接收到的消息队列获取一个消息
        /// </summary>
        /// <returns></returns>
        //public NetMsgPack GetNetMessage()
        //{
        //    lock (commonunity._msgQueue)
        //    {
        //        if (commonunity._msgQueue.Count > 0)
        //        {
        //            //DebugSystem.Log("_msgQueue.Count:" + _msgQueue.Count);
        //            return commonunity._msgQueue.Dequeue();
        //        }
        //    }

        //    return null;
        //}

        //转换成16进制0x的形式
        string Convert0x(int num)
        {
            return string.Format("0x{0}", Convert.ToString(num, 16));
        }
    }
}
