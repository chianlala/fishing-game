using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using NetLib;
using UnityEngine.UI;

using com.maple.network.proto;
using System.Threading;

using ProtoBuf;
using System.Security.Cryptography;
using UnityEngine.Scripting;
using JEngine.Core;
using System.Reflection;

[Preserve]
public class NetMgr : MonoSingletion<NetMgr>
{
    //public bool debug = false;

    //[Header("是否是外网")]
    //public bool isWan = false;
    [Header("是否是测试网")]
    public bool isCeiShi = false;
    public SocketClient _socket;

    private bool tmps = false;
    private string ipNeiWang = "10.1.2.201";
    private string ipWaiWang = "10.1.2.201";
    private string ipCeShiFu = "10.1.2.201";   
    private int ipNeiWangPort = 8011;

    /// <summary>
    /// -1 Socket未连接  0 Socket连接失败或关闭  1 Socket连接中  2 socket连接成功  3 连接成功并登录成功
    /// </summary>
    public int ConnectState = -1;
    public bool Tmps
    {
        get
        {
            return tmps;
        }
        set
        {
            tmps = value;
            tmps = !tmps;
            if (tmps)
            {
                //GameMgr.Instance.OnApplicationPause(tmps);
                OnApplicationPause(tmps);
            }
            else
            {
                OnApplicationPause(tmps);
            }
        }
    }
    private  object[] _paramchuandi = new object[1];
    public void OnApplicationPause(bool pause)
    {
        //Debug.Log("pauseunity"+ pause);
        _paramchuandi[0] = pause;
        if (InitJEngine.Appdomain != null)
        {
            InitJEngine.Appdomain.Invoke("Game.UI.UnityToMessage", "UnityApplicationPause", _paramchuandi, _paramchuandi);
        }
    }
    private void OnApplicationFocus(bool focus)
    {
        //if (focus)
        //{ //回到游戏
        //    Debug.Log("回到游戏");
        //}
        //else
        //{//切入后台
        //    Debug.Log("切入后台");
        //}
        _paramchuandi[0] = focus; 
        if (InitJEngine.Appdomain != null)
        {
            InitJEngine.Appdomain.Invoke("Game.UI.UnityToMessage", "UnityApplicationFocus", _paramchuandi, _paramchuandi);
        }
    }
    public int pLoginState = 0;
    public int nLoginState
    {
        get { return pLoginState; }
        set
        {
            pLoginState = value;
            string str = "";
            switch (pLoginState)
            {
                case 0:
                    str = "与服务器连接断开";
                    break;
                case 1:
                    str = "正在连接服务器";
                    break;
                case 2:
                    str = "连接成功服务器";
                    break;
                default:
                    break;
            }
            Debug.Log(str);
        }
    }
    public SocketClient Socket
    {
        get { return _socket; }
    }

    protected override void Awake()
    {
        base.Awake();

        if (Application.platform != RuntimePlatform.WindowsEditor)
            Debug.unityLogger.logEnabled = GameStats.GameDebug;
        //打印日志
        if (GameStats.GameDebug)
        {
            DebugSystem.RegisterDebugFunc((s) => {
                print(s);
            });
        }
    }
    void FangJianQie()
    {
        PlayerPrefs.GetFloat("Bgm", 0.5f);
        string ds = SystemInfo.operatingSystem;
        string varOsType = Application.platform.ToString();
        WWW ww = new WWW("string");
        if (ww.text == "")
        {

        }
        Animation sp;
        List<int> a = new List<int>();
        a.Contains(0);
        a.Sort();
        this.transform.up = new Vector3(0,0,0);

        System.Type type;
        PropertyInfo property;
    }
    private void Start()
    {
        //Init();
        if (GameStats.IsTestGame)
        {
            GameObject.Find("TestFishTwoAttack").transform.GetChild(0).gameObject.SetActive(true);
        }
    }
    long _lastLocalTicks;
    public long _lastServerTime;
    public long ServerTime
    {
        get
        {
            long m = (DateTime.Now.Ticks - _lastLocalTicks) / 10000;
            long m2 = _lastServerTime;
            return (DateTime.Now.Ticks - _lastLocalTicks) / 10000 + _lastServerTime;
        }
    }
    System.Threading.Thread resourcesLoadThread;
    public void Init()
    {
        ConnectState = 1;
        //关闭线程
        if (resourcesLoadThread != null)
        {
            resourcesLoadThread.Abort();
            resourcesLoadThread.Join();
        }
        //开辟新线程
        resourcesLoadThread = new System.Threading.Thread(this.InitSocket);
        resourcesLoadThread.IsBackground = true;
        resourcesLoadThread.Start();
    }
    //初始化
    public void InitSocket()
    {
        //连接ID 和次数增加
        commonunity.nNowSocketID++;
        //commonunity.nConnectTimes++;
        commonunity.isConnectEorro = 0;
        //新增socket
        if (_socket != null)
        {
            _socket.CloseSocket();
        }
        _socket = null;
        _socket = new SocketClient();
        _socket.nNowSocketID = commonunity.nNowSocketID;
        _socket.onConnectEvent += OnConnectCall;
        _socket.onCloseEvent += OnCloseCall;
        if (GameStats.IsWaiNet)
        {
            if (isCeiShi)
            {
                _socket.Connect(ipCeShiFu, ipNeiWangPort);//连接    
            }
            else
            {
                _socket.Connect(ipWaiWang, ipNeiWangPort);//连接    
            }
        }
        else
        {
            _socket.Connect(ipNeiWang, ipNeiWangPort);//连接    
        }
    }
    /// <summary>
    /// 注销
    /// </summary>
    public void OnZhuxiao()
    {
        commonunity.nNowSocketID++;
        //关闭线程
        if (resourcesLoadThread != null)
        {
            resourcesLoadThread.Abort();
            resourcesLoadThread.Join();
        }
        //关闭Socket
        if (_socket != null)
        {
            _socket.CloseSocket();
        }
        _socket = null;
        commonunity.nLoginWay = 1;

        ConnectState = 0;
        nLoginState = 0;
        //nLoginState = 0;
        //commonunity.nLoginWay = 1;
        //Init();
    }
    //点击登录
    public void OnSpecialLogin()  
    {  
        nLoginState = 0;
        ConnectState = 1;
        commonunity.nLoginWay = 2;
        Init();
    }
    //重连登录
    public void OnConnectLogin()
    {
        //防止连接的时候 再次重连
        nLoginState = 0;
        commonunity.nLoginWay = 3;
        Init();
    }
    /// <summary> 每帧最多处理消息的条数 </summary>
    private int maxReceiveNum = 20;
    /// <summary> 计数变量 </summary>
    private int tempReceiveNum;
    public float fReconnectTime = 0;
    private int nReconnectCount = 0;

    DateTime lifet;
    bool IsPause;
 
    private void OnDestroy()
    {
    }
    private readonly object[] _param0 = new object[0];
    void InitController()
    {
        if (InitJEngine.Appdomain!=null)
        {
            InitJEngine.Appdomain.Invoke("HotUpdateScripts.Program", "InitController", _param0, _param0);
        }
    }

    public static string MD5Encrypt(string strText)
    {
        string strRe = "";
        MD5 md5 = new MD5CryptoServiceProvider();
        byte[] result = md5.ComputeHash(System.Text.Encoding.UTF8.GetBytes(strText));
        for (int i = 0; i < result.Length; i++)
        {
            strRe += forDigit((result[i] & 0xF0) >> 4, 16);
            strRe += forDigit((result[i] & 0xF), 16);
        }
        return strRe;
    }

    public static char forDigit(int digit, int radix)
    {
        if (2 <= radix && radix <= 36)
        {
            if (digit >= 0 && digit < radix)
            {
                return (char)(digit < 10 ? digit + '0' : digit + 'a' - 10);
            }
        }
        return (char)0;
    }

    void OnConnectCall()
    {
        ConnectState = 2;
        commonunity.bWaiting = false;
        nLoginState = 2;
        //连接成功 
        if (commonunity.nLoginWay == 3)
        {
            commonunity.ConnectReLogin = true;
        }
        else if (commonunity.nLoginWay == 2)
        {
            commonunity.SpecialLogin = true;
        }
        commonunity.nConnectTimes = 0;
    }
    public NetMsgPack GetNetMessage()
    {
        lock (commonunity._msgQueue)
        {
            if (commonunity._msgQueue.Count > 0)
            {
                //DebugSystem.Log("_msgQueue.Count:" + _msgQueue.Count);
                return commonunity._msgQueue.Dequeue();
            }
        }
        return null;
    }
    public void RegisterCmd(int msgId, Type type)
    {
        if (commonunity.msgDic.ContainsKey(msgId))
        {
            //DebugSystem.Log(string.Format("RegisteredCmdType CmdType repeat!cmd:{0}", msgId));
            return;
        }
        commonunity.msgDic.Add(msgId, type);
    }
    void OnCloseCall()
    {
        ConnectState = 0;
        nLoginState = 0;
    }
    private void Quit()
    {
#if UNITY_EDITOR
        UnityEditor.EditorApplication.isPlaying = false;
#else
            Application.Quit();
#endif
    }
    private void OnDisable()
    {
        if (_socket != null)
        {
            _socket.CloseSocket();
            _socket.IsReceiveEnd = true;//结束接收线程
        }
    }
    private void Update()
    {
        if (commonunity.ListLog.Count > 0)
        {
            //Root3D.Instance.DebugString(commonunity.ListLog[0]);
            commonunity.ListLog.RemoveAt(0);
        }
    }
}

