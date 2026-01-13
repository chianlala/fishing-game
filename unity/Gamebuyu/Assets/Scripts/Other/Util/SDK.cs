using System.Collections;
using System.Collections.Generic;
using System.IO;
//using System.Xml;
using UnityEngine;
using UnityEngine.UI;
using System.Runtime.InteropServices;
using System;

public class SDK : MonoBehaviour
{
    static string strYYFile = "";
    public static string _strSpeak = "33";
    #region 委托事件
    public delegate void GetRecorderDelegate(byte[] btYY);
    public static event GetRecorderDelegate GetRecorderEvent;
    static void GetRecorderAction(byte[] btYY)
    {
        if (GetRecorderEvent == null)
            return;//DownLoadEndEvent += new DownLoadEndDelegate(t_ProcessEvent);
        GetRecorderEvent(btYY);
    }

    public delegate void GetReceiveDelegate(string strReceive);
    public static event GetReceiveDelegate GetReceiveEvent;
    static void GetReceiveAction(string strReceive)
    {
        if (GetReceiveEvent == null)
            return;//DownLoadEndEvent += new DownLoadEndDelegate(t_ProcessEvent);
        GetReceiveEvent(strReceive);
    }

    public static Action<int> Get4GAction;
    public static Action<int> GetWifiAction;
    public static Action<double> GetBatteryAction;
    public static Action<bool> GetShareSuccess;

    public static string GetIMEI()
    {
        string strImei = "";
#if UNITY_ANDROID
        strImei= m_jo.Call<string>("GetImei");
#elif UNITY_IPHONE
#endif
        return strImei;
    }

    #endregion
    //public static List<string> listRead = new List<string>();
    static byte[] TextureToByte(Texture2D Tex)
    {

        //Tex TextureFormat.RGB24
        //Tex screenShot = new Texture2D.EXRFlags 

        byte[] bytes = Tex.EncodeToPNG();
        // 最后，我返回这个Texture2d对象，这样我们直接，所这个截图图示在游戏中，当然这个根据自己的需求的。
        return bytes;
    }
#if UNITY_ANDROID
    public static readonly string _pathUrl = "file://";
    public static string _pathLocal;
    public static readonly string _pathSpeak = "/recordtest/";

    //android 实例
    private static AndroidJavaObject m_jo;

    /// <summary>
    /// 安装APK
    /// </summary>
    /// <param name="strPath">安装文件路径</param>
    public static void InstallApk(string strPath)
    {
        m_jo.Call("InstallApk", strPath);
    }


    // Use this for initialization
    void Start()
    {

        AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        m_jo = jc.GetStatic<AndroidJavaObject>("currentActivity");
        _pathLocal = m_jo.Call<string>("GetSDPath");
        _pathLocal = _pathLocal + "/yuanwu/";
        CheckDirWithDir(_pathLocal);
        //InitXml();
    }
        //调用iOS或Android原生方法保存图片后更新相册.
    public static void SavePngAndUpdate(string fileName)
    {
        m_jo.Call("scanFile", fileName, "保存成功辣٩(๑>◡<๑)۶ ");//这里我们可以设置保存成功弹窗内容
    }
    //语音
    /// <summary>
    /// 开始录音
    /// </summary>
    public static void StartRecorder()
    {
        strYYFile = m_jo.Call<string>("StartRecorder", _strSpeak);
    }
    /// <summary>
    /// 结束录音
    /// </summary>
    /// <returns>录音byte数组</returns>
    public static byte[] StopRecorder()
    {
        m_jo.Call("StopRecorder");
        return readFile(strYYFile);
        //listRead.Add(strYYFile);
        //string 转换为byte[]
        //byte[] bt = System.Convert.FromBase64String(str);
    }
    /// <summary>
    /// 播放语音
    /// </summary>
    /// <param name="bt">语音byte数组</param>
    /// <returns>语音长度</returns>
    public static float PlayYuyin(byte[] bt)
    {
        return writerFile(bt);

    }

    /// <summary>
    /// 结束播放
    /// </summary>
    public static void StopYuyin()
    {
        m_jo.Call("StopYuyin");
    }
    
    /// <summary>
    /// 获取GPS信息
    /// </summary>
    /// <returns>经度,纬度</returns>
    public static string GetGPSstring()
    {
        //string strTemp = m_jo.Call<string>("getGPSstring");
        ////MessageBox.ShowPopMessage("获取经纬度:"+strTemp);
        //if (strTemp == "")
        //{
        //    strTemp = "-999999,-999999";
        //}
        //string[] str = strTemp.Split(',');
        //if (Mathf.Abs(float.Parse(str[0])) > 1000)
        //{
        //    strTemp = "-999999,-999999";
        //}
        //if (Mathf.Abs(float.Parse(str[1])) > 1000)
        //{
        //    strTemp = "-999999,-999999";
        //}
        //if (Mathf.Abs(float.Parse(str[0])) < 1 && Mathf.Abs(float.Parse(str[1])) < 1)//安卓取值失败
        //{
        //    strTemp = "-999999,-999999";
        //}
        //common.strGPS = strTemp;
        ////MessageBox.ShowPopMessage(string.Format("经度:{0},纬度:{1}", str[0], str[1]));
        //return strTemp;
        return "9999";
    }

    /// <summary>
    /// 授权
    /// </summary>
    /// <returns>是否授权成功</returns>
    public static bool GetCode()
    {
        return m_jo.Call<bool>("GetCode");
    }

    /// <summary>
    /// 检查token是否过期
    /// </summary>
    /// <param name="_refreshToken"></param>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    /// <returns>是否过期</returns>
    public static bool CheckToken(string _refreshToken, string _AccToken, string _Openid)
    {
        return m_jo.Call<bool>("CheckToken", _refreshToken, _AccToken, _Openid);
    }

    /// <summary>
    /// 获取微信用户信息
    /// </summary>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    public static void GetUserInfo(string _AccToken, string _Openid)
    {
        m_jo.Call("GetUserInfo", _AccToken, _Openid);
    }

    /// <summary>
    /// 分享给朋友(网页)
    /// </summary>
    public static void ShareFriend(string strShare, bool bTimeline = false)
    {        
        //m_jo.Call("ShareFriend", strShare, bTimeline);
        //NetMessage.OseeLobby.Req_WechatShareRequest();
    }

    /// <summary>
    /// 分享给朋友(截图)
    /// </summary>
    public static void ShareFriendScreenImage(bool bTimeline = false) 
    {
       //m_jo.Call("ShareFriendImage", CaptureScreenshot2(new Rect(0, 0, Screen.width, Screen.height)), bTimeline);
       // NetMessage.OseeLobby.Req_WechatShareRequest();
    }
    /// <summary>
    /// 分享给朋友图 
    /// </summary>
    public static void ShareFriendImage(Texture2D tex, bool bTimeline = false)
    {
        //m_jo.Call("ShareFriendImage", TextureToByte(tex), bTimeline); //图片
        //NetMessage.OseeLobby.Req_WechatShareRequest();
    }
    /// <summary>
    /// 微信支付
    /// </summary>
    /// <param name="_serverIp"></param>
    /// <param name="_OpenId"></param>
    /// <param name="_price"></param>
    public static void WxPay(string _serverIp, string _OpenId, int _price)
    {
        m_jo.Call("OnAskFoWxPay", _serverIp, _OpenId, _price);
    }
    public static void WxPay(string orderInfo)
    {
        m_jo.Call("StartWeixinPay", orderInfo);
    }

    /// <summary>
    /// 复制字符串到剪切板
    /// </summary>
    /// <param name="strCopy"></param>
    public static void onCopy(string strCopy)
    {
        m_jo.Call("onCopy", strCopy);
    }
    public static void onOpenWeb(string url)
    {
        m_jo.Call("onOPenWeb", url);
    }

    public static void makeCall(string strCall)
    {
        m_jo.Call("makeCall", strCall);
    }

    public static double GetBatteryLevel()
    {
        BatteryLevel = m_jo.Call<double>("GetBatteryLevel");
        return BatteryLevel;
    }

#elif UNITY_IPHONE

    public static readonly string _pathUrl = "file://";
	public static string _pathLocal;
	private float fGetWifiTime = 0;
	// Use this for initialization
	void Start () {
		_pathLocal = Application.persistentDataPath+"/";
		//CheckDirWithDir(_pathLocal + "yuanwu/");
		//InitXml();
		fGetWifiTime = Time.time;
	}
    public static void makeCall(string strCall)
    {
        //m_jo.Call("makeCall", strCall);
    }
    public static int GetPort()
    {
        return 0;
     // return IOS.GetPort();
    }
    /// <summary>
    /// 开始录音
    /// </summary>
    public static void StartRecorder()
    {
		//IOS.StartRecorder (_pathLocal + _strSpeak);
		//strYYFile = _pathLocal + _strSpeak;
    }
    /// <summary>
    /// 结束录音
    /// </summary>
    /// <returns>录音byte数组</returns>
    public static byte[] StopRecorder()
    {
        return null;
		//strYYFile = IOS.StopRecorder ();
		//return  readFile(strYYFile);
    }
    /// <summary>
    /// 播放语音
    /// </summary>
    /// <param name="bt">语音byte数组</param>
    /// <returns>语音长度</returns>
    public static float PlayYuyin(byte[] bt)
    {
		return 0;
    }
    /// <summary>
    /// 结束播放
    /// </summary>
    public static void StopYuyin()
    { 
		//IOS.StopYuyin ();
    }
        public static void SavePhoto(string path){
		//IOS.SavePhoto(path);
	}
    /// <summary>
    /// 获取GPS信息
    /// </summary>
    /// <returns>经度,纬度</returns>
    public static string GetGPSstring()
    {
        string strTemp = "";// IOS.getGPSstring();
		if(strTemp=="")
		{
			strTemp = "-999999,-999999";
		}
       // common.strGPS = strTemp;
		return strTemp;
    }
		

    /// <summary>
    /// 授权
    /// </summary>
    /// <returns>是否授权成功</returns>
    public static bool GetCode()
    {
		return  false;
    }

    /// <summary>
    /// 检查token是否过期
    /// </summary>
    /// <param name="_refreshToken"></param>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    /// <returns>是否过期</returns>
    public static bool CheckToken(string _refreshToken, string _AccToken,string _Openid)
    {
        return true;
		//return IOS.CheckToken(_refreshToken,_AccToken,_Openid);
    }

    /// <summary>
    /// 获取微信用户信息
    /// </summary>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    public static void GetUserInfo(string _AccToken, string _Openid)
    {
		//IOS.GetUserInfo (_AccToken, _Openid);
    }

    public static void ShareFriend(string strShare,bool bTimeline=false)
    {       
		//IOS.ShareFriend (strShare,bTimeline);
        //NetMessage.OseeLobby.Req_WechatShareRequest();
    }    
	public static void ShareFriendScreenImage(bool bTimeline=false)
	{
		//byte[] bytes = CaptureScreenshot2 (new Rect (0, 0, Screen.width, Screen.height));
		//IOS.ShareFriendImage (bytes,bytes.Length,bTimeline);
	}
    	public static void ShareFriendImage(Texture2D tex, bool bTimeline=false)
	{
		//byte[] bytes = CaptureScreenshot2 (new Rect (0, 0, Screen.width, Screen.height));
        //byte[] bytes = TextureToByte(tex);
        //IOS.ShareFriendImage (bytes, bytes.Length,bTimeline);
//        if(bTimeline)
//        {
//            CompleteShareTaskRequest rq = new CompleteShareTaskRequest();
//            NetHelper.SendMessage(CmdType.C_S_COMPLETE_SHARE_TASK_REQUEST, rq);
//        }
	}
     public static void WxPay(string strOrder)
     {
		//IOS.OnPay(strOrder);
     }

    /// <summary>
    /// 复制字符串到剪切板
    /// </summary>
    /// <param name="strCopy"></param>
    public static void onCopy(string strCopy)
    {
       // IOS.onCopy(strCopy);
        //MessageBox.ShowPopMessage("复制成功，可以发给朋友们了。");
    }

    public static double GetBatteryLevel()
    {
        //        BatteryLevel =IOS.GetBatteryLevel();
        return 0;
    }
#else
    public static string _pathLocal = "";
    public static readonly string _pathUrl = "file://";
    // Use this for initialization
    void Start()
    {
        _pathLocal = Application.streamingAssetsPath + "/";
        //InitXml();
    }

    /// <summary>
    /// 开始录音
    /// </summary>
    public static void StartRecorder()
    {
    }
    /// <summary>
    /// 结束录音
    /// </summary>
    /// <returns>录音byte数组</returns>
    public static byte[] StopRecorder()
    {
        return null;
    }
    /// <summary>
    /// 播放语音
    /// </summary>
    /// <param name="bt">语音byte数组</param>
    /// <returns>语音长度</returns>
    public static float PlayYuyin(byte[] bt)
    {
        return 0;
    }
    /// <summary>
    /// 结束播放
    /// </summary>
    public static void StopYuyin()
    {
    }

    /// <summary>
    /// 获取GPS信息
    /// </summary>
    /// <returns>经度,纬度</returns>
    public static string GetGPSstring()
    {
        return "-999999,-999999";
    }

    /// <summary>
    /// 授权
    /// </summary>
    /// <returns>是否授权成功</returns>
    public static bool GetCode()
    {
        return false;
    }

    /// <summary>
    /// 检查token是否过期
    /// </summary>
    /// <param name="_refreshToken"></param>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    /// <returns>是否过期</returns>
    public static bool CheckToken(string _refreshToken, string _AccToken, string _Openid)
    {
        return false;
    }

    /// <summary>
    /// 获取微信用户信息
    /// </summary>
    /// <param name="_AccToken"></param>
    /// <param name="_Openid"></param>
    public static void GetUserInfo(string _AccToken, string _Openid)
    {

    }

    /// <summary>
    /// 分享好友
    /// </summary>
    /// <param name="strShare">分享信息</param>
    /// 是否朋友圈
    public static void ShareFriend(string strShare, bool bTimeline = false)
    {

    }
    /// <summary>
    /// 分享给朋友(截图片)
    /// </summary>
    public static void ShareFriendScreenImage(bool bTimeline = false)
    {

    }
     
    /// <summary>
    /// 分享给朋友(图片)
    /// </summary>
    public static void ShareFriendImage(bool bTimeline = false)
    {

    }

    public static void WxPay(string _serverIp, string _OpenId, int _price)
    {

    }
    public static void WxPay(string orderInfo)
    {
        //m_jo.Call("StartWeixinPay", orderInfo);
    }
    public static void makeCall(string strCall)
    {
        //m_jo.Call("makeCall", strCall);
    }

    /// <summary>
    /// 复制字符串到剪切板
    /// </summary>
    /// <param name="strCopy"></param>
    public static void onCopy(string strCopy)
    {
        TextEditor text = new TextEditor();
        text.content = new GUIContent(strCopy);
        text.OnFocus();
        text.Copy();
       // MessageBox.ShowPopMessage("复制成功，可以发给朋友们了。");
    }


    public static double GetBatteryLevel()
    {
        return 1;
    }
#endif


    // Update is called once per frame
    void Update()
    {
//#if UNITY_IPHONE
//		//ios因为不能监听 定时获取信号强度
//		if(Time.time-fGetWifiTime>=10)
//		{
//			fGetWifiTime=Time.time;
//			IOS.GetWifiLevel();
//		}
//#endif
    }


    #region byte[]读写语音文件
    //利用byte[]数组写入文件
    protected static float writerFile(byte[] array)
    {
        string strFile = _pathLocal + "/34.amr";
#if UNITY_IPHONE
		strFile = _pathLocal + "/ux9999.amr";
#endif

        //创建一个文件流
        FileStream fs = new FileStream(strFile, FileMode.Create);
        //将byte数组写入文件中
        fs.Write(array, 0, array.Length);
        //所有流类型都要关闭流，否则会出现内存泄露问题
        fs.Close();
#if UNITY_ANDROID
        return m_jo.Call<float>("PlayYuyin");
#elif UNITY_IPHONE
		//return IOS.PlayYuyin(strFile);
#endif
        return 0;
    }
    //利用byte[]数组读取文件
    protected static byte[] readFile(string strFile)
    {
        if (File.Exists(strFile))//判断本地是否有
        {
            FileStream fs = new FileStream(strFile, FileMode.Open);

            //获取文件大小
            long size = fs.Length;

            byte[] array = new byte[size];

            //将文件读到byte数组中
            fs.Read(array, 0, array.Length);

            fs.Close();
            return array;
        }
        return null;
    }
    #endregion

    #region 初始化Prefs读取处理

  

    static void CheckDirWithDir(string strDir)//判断资源存放文件夹是否存在
    {
        if (strDir.LastIndexOf('/') != strDir.Length - 1)
            strDir += "/";
        if (!Directory.Exists(_pathLocal + strDir))
        {
            Directory.CreateDirectory(_pathLocal + strDir);
        }
    }
    static void CheckDirWithFile(string strSrc)//判断资源存放文件夹是否存在
    {
        string strDir = strSrc.Substring(0, strSrc.LastIndexOf('/'));
        if (!Directory.Exists(_pathLocal + strDir))
        {
            Directory.CreateDirectory(_pathLocal + strDir);
        }
    }
    #endregion

    #region 截屏

    /// <summary>
    /// Captures the screenshot2.
    /// </summary>
    /// <returns>The screenshot2.</returns>
    /// <param name="rect">Rect.截图的区域，左下角为o点</param>
    static byte[] CaptureScreenshot2(Rect rect)
    {
        // 先创建一个的空纹理，大小可根据实现需要来设置
        Texture2D screenShot = new Texture2D((int)rect.width, (int)rect.height, TextureFormat.RGB24, false);
        screenShot.name = "截图";
        // 读取屏幕像素信息并存储为纹理数据，
        screenShot.ReadPixels(rect, 0, 0);
        screenShot.Apply();

        // 然后将这些纹理数据，成一个png图片文件
        byte[] bytes = screenShot.EncodeToPNG();
        //string filename = Application.dataPath + "/Screenshot.png";
        //System.IO.File.WriteAllBytes(filename, bytes);
        //Debug.Log(string.Format("截屏了一张图片: {0}", filename));

        // 最后，我返回这个Texture2d对象，这样我们直接，所这个截图图示在游戏中，当然这个根据自己的需求的。
        return bytes;
    }
    #endregion

    public static double BatteryLevel = 1;
    public static int WifiLevel = 0;
    public static int Net4GLevel = 0;
    /// <summary>
    /// 手机端返回消息
    /// </summary>
    /// <param name="strRecv"></param>
    public void Receive(string strRecv)
    {
        //Debug.Log("sdkReceive:" + strRecv);
        int type = 0;//0微信相关 1电池电量 2wifi信号强度 3外网信号强度 4 微信分享成功
        int.TryParse(strRecv.Substring(0, 1), out type);//错误返回为0 即微信相关
        switch (type)
        {
            case 0:
                GetReceiveAction(strRecv);
                break;
            case 1:
                {
                    string str = strRecv.Substring(1);
                    double.TryParse(str, out BatteryLevel);
                    if (GetBatteryAction != null)
                        GetBatteryAction(BatteryLevel);
                }
                break;
            case 2:
                {
                    string str = strRecv.Substring(1);
                    int.TryParse(str, out WifiLevel);
                    if (GetWifiAction != null)
                        GetWifiAction(WifiLevel);
                }
                break;
            case 3:
                {
                    string str = strRecv.Substring(1);
                    int.TryParse(str, out Net4GLevel);
                    if (Get4GAction != null)
                        Get4GAction(Net4GLevel);
                }
                break;
            case 4:
                {
                    //UEventDispatcher.Instance.DispatchEvent(UEventName.ShareOK);
                }
                break;
        }
    }


}


#if UNITY_IPHONE
public class IOS
{
	//[DllImport("__Internal")]
	//public static extern void GetPort();  
   

	//[DllImport("__Internal")]
	//public static extern bool GetCode();
	//[DllImport("__Internal")]
	//public static extern bool CheckToken(string _refreshToken, string _AccToken,string _Openid);
	//[DllImport("__Internal")]
	//public static extern void GetUserInfo (string _AccToken, string _Openid);
	//[DllImport("__Internal")]
	//public static extern void ShareFriend(string strShare,bool bTimeline);
	//[DllImport("__Internal")]
	//public static extern void ShareFriendImage(byte[] btImage , int lenth,bool bTimeline);
	//[DllImport("__Internal")]
	//public static extern void StartRecorder(string strSpeakFile);
	//[DllImport("__Internal")]
	//public static extern string StopRecorder();
	//[DllImport("__Internal")]
	//public static extern float PlayYuyin(string strYYFile);
	//[DllImport("__Internal")]
	//public static extern void StopYuyin();
	//[DllImport("__Internal")]
	//public static extern string getGPSstring();
	//[DllImport("__Internal")]
	//public static extern void OnPay (string strOrder);
    //[DllImport("__Internal")]
	//public static extern void onCopy (string strCopy);
	//[DllImport("__Internal")]
	//public static extern double GetBatteryLevel ();
	//[DllImport("__Internal")]
	//public static extern void GetWifiLevel ();
	//[DllImport("__Internal")]
    //public static extern void SavePhoto(string readAddr);
}
#endif
