//
// InstantiateDemo.cs
//
// Author:
//       JasonXuDeveloper（傑） <jasonxudeveloper@gmail.com>
//
// Copyright (c) 2021 JEngine
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
using UnityEngine;
using JEngine.Core;
using JEngine.UI;
using CoreGame;
using NetLib;
using System;
using com.maple.game.osee.proto;
using LitJson;
using UnityEngine.UI;
using DG.Tweening;

namespace Game.UI
{

    public class UIRoot : MonoBehaviour
    {
        private static bool IsFirst = true;
        private int maxReceiveNum = 20;

        float fPingTimeJianGe = 0f;
        public Transform ReConnecting;
        public Text ReConnectingText;

        float fNoConectJianGe = 0f;
        float isPingStartTime = 0f;

        int nConnectTimes = 0;

        public void Awake()
        {
            if (IsFirst)
            {
                IsFirst = false;
                //Debug.LogError("初始化 UIRoot");
                common2.TraUIRoot = this.transform;
                ReConnecting = this.transform.Find("ReConnecting");
                ReConnectingText = this.transform.Find("ReConnecting/Text").GetComponent<Text>();
                common2.PopUpUICanvas = this.transform.Find("PopUpUICanvas");
                common2.FixedUICanvas = this.transform.Find("FixedUICanvas");
                common2.NormalUICanvas = this.transform.Find("NormalUICanvas");
                common2.TwoAttackCanvas = this.transform.Find("TwoAttackCanvas");
                common2.BuyuUICanvas = this.transform.Find("BuyuUICanvas");
                common2.GoldNormal = this.transform.Find("BuyuUICanvas/GoldNormalPos");
                common2.ApperAnimation = this.transform.Find("BuyuUICanvas/ApperAnimation");
                common2.transICE = this.transform.Find("BuyuUICanvas/transICE");
                
                InitController();
                InitFishConfig();
                //屏幕尺寸
                var left = this.transform.Find("withandhight/left");
                var right = this.transform.Find("withandhight/right");
                var up = this.transform.Find("withandhight/up");
                var down = this.transform.Find("withandhight/down");
                common.W = right.transform.localPosition.x - left.transform.localPosition.x;
                common.H = up.transform.localPosition.y - down.transform.localPosition.y;
     
                UIMgr.ShowUI(UIPath.UILogin);
            }
        }

   
        void Update()
        {
            if (nConnectTimes > 3)
            {
                commonunity.nConnectTimes++;
                return;
            }
            //报错重连
            if (commonunity.isConnectEorro > 1)
            {
                if (common.IsOpenUILogin)   //登录页面
                {
                    if (common.nLoinWay == 1 || common.nLoinWay == 2)//点击登录
                    {
                        NetMgr.Instance.ConnectState = 0;
                        ReConnecting.gameObject.SetActive(false);
                        MessageBox.ShowNet(string.Format("您与服务器断开连接，请重新尝试!错误码({0})", commonunity.isConnectEorro));
                        common.nLoinWay = 0;
                        commonunity.isConnectEorro = 0;
                        if (common2.LoginMask != null)
                        {
                            common2.LoginMask.gameObject.SetActive(false);
                        }
                        return;
                    }
                }
                else //其它页面
                {
                    if (nConnectTimes == 0)
                    {
                        commonunity.isConnectEorro = 0;
                        nConnectTimes++;
                        //启动连接
                        ShowNetConnect(string.Format("网络异常，尝试重新连接连!({0}/3)", nConnectTimes));
                        NetMgr.Instance.OnConnectLogin();
                    }
                    else
                    {
                        int tmpEorro = commonunity.isConnectEorro;
                        commonunity.isConnectEorro = 0;
                        DOVirtual.DelayedCall(4f, () => {
                            if (nConnectTimes >= 3)
                            {
                                nConnectTimes++;
                                ReConnecting.gameObject.SetActive(false);
                                MessageBox.ShowNet(string.Format("您与服务器断开连接，请重新登录！错误码({0})", tmpEorro), "提示",
                                () => { common3.CloseLoginZhuXiao(); nConnectTimes = 0; },
                                () => { common3.CloseLoginZhuXiao(); nConnectTimes = 0; }
                                );
                                return;
                            }
                            else
                            {
                                nConnectTimes++;
                                //启动连接
                                ShowNetConnect(string.Format("网络异常，尝试重新连接连!({0}/3)", nConnectTimes));
                                NetMgr.Instance.OnConnectLogin();
                            }
                        });
                    }
                }
                return;
            }

            switch (NetMgr.Instance.nLoginState)
            {
                case 0://未连接全断开
                    return;
                case 1://正在连接
                    return;
                case 2://连接成功
                       //判空
                    if (NetMgr.Instance.Socket == null)
                    {
                        //Root3D.Instance.DebugString(PlayerData.PlayerId + "[客户端断开 _socket == null]");
                        NetMgr.Instance.nLoginState = 0;
                        UIMgr.DestroyAllCreatePerfab();
                        common3.CloseLoginZhuXiao();
                        fNoConectJianGe = 0f;
                        return;
                    }
                    //关闭转圈
                    if (ReConnecting.gameObject.activeSelf)
                    {
                        ReConnecting.gameObject.SetActive(false);
                        //未连接间隔归0
                        fNoConectJianGe = 0f;
                        common.isPingStartTime = Time.realtimeSinceStartup;
                    }
                    //每过4秒 开始网络诊断
                    fPingTimeJianGe += Time.deltaTime;
                    if (fPingTimeJianGe > 4f)
                    {
                        fPingTimeJianGe = 0;
                        NetMessage.Network.Req_PingRequest(0);
                    }
                    break;
                default:
                    break;
            }
            if (commonunity.SpecialLogin)
            {
                //点击登录
                SpecialLogin();
                commonunity.SpecialLogin = false;
            }
            if (commonunity.ConnectReLogin)
            {
                //重连成功
                ConnectReLogin();
                commonunity.ConnectReLogin = false;
            }
            if (commonunity.ConnectSuccess)
            {
                //连接成功
                ConnectSuccess();
                commonunity.ConnectSuccess = false;
            }

            int tempReceiveNum = 0;
            while (tempReceiveNum <= maxReceiveNum)
            {
                NetMsgPack pack = NetMgr.Instance.GetNetMessage();
                if (pack == null)
                    break;
                //能收到消息
                PlayerData.CanRechiveXiaoXi = 0f;
                //common.packMsgId.Add(pack.MsgId);
                //if (common.packMsgId.Count > 10)
                //{
                //    common.packMsgId.RemoveAt(0);
                //}
                if (GameStats.Debug)
                {
                    if (pack.MsgId != (int)com.maple.network.proto.NetworkMsgCode.S_C_HEART_BEAT_RESPONSE)//过滤心跳消息打印
                        print(string.Format("接收到消息 {0}({1})", Enum.GetName(typeof(OseeMsgCode), (OseeMsgCode)pack.MsgId), GameHelper.Convert0x(pack.MsgId)));
                }
                ControllerMgr.Instance.ProcessPack(pack);
                tempReceiveNum++;
            }
        }
        void ShowNetConnect(string text)
        {
            ReConnecting.gameObject.SetActive(true);
            ReConnectingText.text = text;
        }
        public void SpecialLogin()
        {
            nConnectTimes = 0;
            Debug.Log("登录 SpecialLogin");
            Debug.Log("登录 SpecialLogin" + common.nLoinWay);
            if (common.nLoinWay == 1)//点击登录
            {
                DOVirtual.DelayedCall(0.5f, () =>
                {
                    if (common.OpenUsername != "")
                    {
                        NetMessage.Login.Req_CommonLoginRequest(common.OpenUsername, common.OpenPassw, true, false);
                        if (common2.LoginMask != null)
                        {
                            common2.LoginMask.DOScale(1, 4f).OnComplete(() =>
                            {
                                Debug.Log("登录 主动关闭LoginMask");

                                if (common2.LoginMask.gameObject.activeSelf)
                                {
                                    MessageBox.ShowPopOneMessage("网络环境较差，无法连接!");
                                    common2.LoginMask.gameObject.SetActive(false);
                                }
                            });
                        }
                    }
                    else
                    {
                        UIMgr.ShowUI(UIPath.UILogin);
                        UIMgr.CloseAllwithOut(UIPath.UILogin);
                        common2.LoginMask.gameObject.SetActive(false);
                    }
                });
            }
            else  if (common.nLoinWay == 2)//注册登录
            {
                //赋值 这个需要储存
                NetMessage.Login.Req_UserRegisterRequest(PlayerData.Acount, common.MD5Encrypt(PlayerData.PassWord), PlayerData.inputCraetName_phone, PlayerData.inputCraetName_Captcha);
                //NetMessage.Login.Req_UserRegisterRequest(PlayerData.Acount, common.MD5Encrypt(PlayerData.PassWord), "", "");
            }
            else if (common.nLoinWay == 4)
            {
             
                NetMessage.Agent.Req_AccountPhoneCheckRequest(PlayerData.str_CraetName_phone);
      
            }
            else if (common.nLoinWay == 5)
            {
                NetMessage.OseeLobby.Req_GetResetPasswordPhoneNumRequest(PlayerData.str_input_forgetAcc);
            }
            else //重连登录
            {
                //common2.LoginMask.gameObject.SetActive(false);
                NetMessage.Login.Req_CommonLoginRequest(common.OpenUsername, common.OpenPassw, false, true);
            }
        }
        public void ConnectReLogin()
        {
            nConnectTimes = 0;
            if (common.IsAlreadyLogin)
            {
                if (common.OpenUsername != "")
                {
                    NetMessage.Login.Req_CommonLoginRequest(common.OpenUsername, common.OpenPassw, false, true);
                }
                else
                {
                    UIMgr.ShowUI(UIPath.UILogin);
                    UIMgr.CloseAllwithOut(UIPath.UILogin);
                }
            }
            else
            {
                UIMgr.ShowUI(UIPath.UILogin);
                UIMgr.CloseAllwithOut(UIPath.UILogin);
            }
        }
        public void ConnectSuccess()
        {
            //nConnectTimes = 0;
            //UIMgr.ShowUI(UIPath.UILogin);
            //UIMgr.CloseAllwithOutTwo(UIPath.UILogin,UIPath.UIMessageBox);
        }
        private void InitController()
        {
            //注册
            new LoginController();
            new GameBaseController();
            new OseeFishingController();
            new NetworkController();
            new OseeLobbyController();
            new ChallengeController();
            new LobbyController();
            new AgentController();
        }
        private void Start()
        {
            LoadSound();
        }
        void LoadSound() {
            SoundHelper.BgmVolume=PlayerPrefs.GetFloat("Bgm", 0.3f);
            SoundHelper.GameVolume = PlayerPrefs.GetFloat("Sound", 0.7f);

            EventManager.SoundYinXiaoUpdate?.Invoke(SoundHelper.GameVolume);
        
        }
        public static void LoadFileAysn(string path, string fileName, Action compete)
        {
            var binAsset = common4.LoadAynsText("Config/" + fileName);
            CSV.LoadFile(path, fileName, binAsset);
            compete();
        }
        public void InitFishConfig()
        {
            //鱼模型表-------------
            var result = common4.LoadAynsJson("Json/FishJson");
            JsonData datAll = JsonMapper.ToObject(result.text);
            foreach (string itemname in datAll.Keys)
            {
                JsonData dat = datAll[itemname];
                FishJson fj = new FishJson();
                fj.modelID = (int)dat["modelID"];
                //fj.BgID = (int)dat["BgID"];
                fj.isBoss = (bool)dat["isBoss"];
                fj.isTwoAttack = (bool)dat["isTwoAttack"];
                fj.isSkill = (bool)dat["isSkill"];
                fj.isGold = (bool)dat["isGold"];

                //背景ID
                if (dat.Keys.Contains("xiaoboss"))
                {
                    fj.xiaoboss = (bool)dat["xiaoboss"];
                }
                else
                {
                    fj.xiaoboss = false;
                }
           
                //背景ID
                if (dat.Keys.Contains("BgID"))
                {
                    fj.BgID = (int)dat["BgID"];
                }
                else
                {
                    fj.BgID = 0;
                }
                //小鱼
                if (dat.Keys.Contains("isSmall"))
                {
                    fj.isSmall = (bool)dat["isSmall"];
                }
                else
                {
                    fj.isSmall = false;
                }
                //大鱼
                if (dat.Keys.Contains("isBig"))
                {
                    fj.isBig = (bool)dat["isBig"];
                }
                else
                {
                    fj.isBig = false;
                }
                //详情
                if (dat.Keys.Contains("info"))
                {
                    fj.info= dat["info"].ToString();
                }
                else
                {
                    fj.info = "";
                } 
                //Debug.Log("itemname"+ itemname);
                common4.dicMoldConfig.Add(itemname, fj);
            }
            //炮倍场次表范围-------------
            var resultPao = common4.LoadAynsJson("Json/PaoFanwei");
            JsonData datAllPao = JsonMapper.ToObject(resultPao.text);
            foreach (string itemname in datAllPao.Keys)
            {
                JsonData dat = datAllPao[itemname];
                PaoRangeConfig fj = new PaoRangeConfig();
                fj.Minlevel= (int)dat["batteryMin"];
                fj.Maxlevel = (int)dat["batteryMax"];
      
               // Debug.Log("itemname" + itemname);
                common.dicPaoFwConfig.Add(int.Parse(itemname), fj);
            }
            //炮倍翅膀表-------------
            var vPaoWinInfo = common4.LoadAynsJson("Json/PaoWinInfo"); 
            JsonData allPaoWin = JsonMapper.ToObject(vPaoWinInfo.text); 
            foreach (string itemname in allPaoWin.Keys)
            { 
                JsonData dat = allPaoWin[itemname];
                PaoWinJson fj = new PaoWinJson();
   
                fj.modelID = int.Parse(dat["modelId"].ToString());
                fj.name = dat["name"].ToString();
                //详情
                if (dat.Keys.Contains("get"))
                {
                    fj.info = dat["get"].ToString();
                }
                else
                {
                    fj.info = "";
                }
                //Debug.Log("itemname" + itemname);
                common4.PaoWinConfig.Add(int.Parse(itemname), fj);

                if (int.Parse(itemname)==70)
                {
                    common4.PaoWinTime.Add(int.Parse(itemname), -1);
                }
                else
                {
                    common4.PaoWinTime.Add(int.Parse(itemname), 0);
                }
            }
            //背包表-------------
            var vBagItemInfo = common4.LoadAynsJson("Json/BagItem");
            JsonData allBagItem = JsonMapper.ToObject(vBagItemInfo.text);
            foreach (string itemname in allBagItem.Keys)
            {
                JsonData dat = allBagItem[itemname];
                BagItemJson fj = new BagItemJson();
                fj.name = dat["name"].ToString();
                //详情
                if (dat.Keys.Contains("info"))
                {
                    fj.info = dat["info"].ToString();
                }
                else
                {
                    fj.info = "";
                }
                common4.BagItemConfig.Add(int.Parse(itemname), fj);
            }
            //fish表
            LoadFileAysn("Config", "cfg_fish", () =>
            {
                for (int i = 2; i < CSV.m_ArrayData.Count; i++)
                {
                    try
                    {
                        FishConfig fc = new FishConfig();
                        fc.monsterId = CSV.GetLong(i, "id");
                        fc.name = CSV.GetString(i, "name");
                        // fc.modelId =CSV.GetInt(i, "modelId");
                        fc.money = CSV.GetInt(i, "money");
                        fc.maxMoney = CSV.GetInt(i, "maxMoney");
                        fc.fishType = CSV.GetInt(i, "fishType");
                        fc.skill = CSV.GetInt(i, "skill");
                        fc.scene = CSV.GetInt(i, "scene");
                        common4.dicFishConfig.Add(fc.monsterId, fc);
                    }
                    catch
                    {
                    }
                }
            });
            ////鱼模型表
            //LoadFileAysn("Config", "cfg_moldfish", () =>
            //{
            //    for (int i = 2; i < CSV.m_ArrayData.Count; i++)
            //    {
            //        FishModleConfig fc = new FishModleConfig();
            //        fc.Id = CSV.GetInt(i, "id");
            //        fc.name = CSV.GetString(i, "name");
            //        fc.modelId = CSV.GetInt(i, "modelId");
            //        fc.fishType = CSV.GetInt(i, "fishType");
            //        common4.dicMoldConfig.Add(fc.name, fc);
            //    }
            //});
            LoadFileAysn("Config", "cfg_fish_route", () =>
            {
                for (int i = 2; i < CSV.m_ArrayData.Count; i++)
                {
                    FishMoveConfig fmc = new FishMoveConfig();
                    fmc.id = CSV.GetLong(i, "id");
                    fmc.pathId = CSV.GetInt(i, "routeId");
                    fmc.time = CSV.GetFloat(i, "time");
                    //fmc.strAnimal = CSV.GetString(i, "animation");
                    //fmc.strStop = CSV.GetString(i, "stopTime");
                    if (!common.dicPathConfig.ContainsKey(fmc.id))
                    {
                        common.dicPathConfig.Add(fmc.id, fmc);
                    }
                    else
                    {
                        Debug.Log("重复：" + fmc.id.ToString());
                    }
                }
            });
            LoadFileAysn("Config", "cfg_battery_level", () =>
            {
                PaoConfig pcOld = null;
                PaoConfig pc1 = new PaoConfig();

                for (int i = 2; i < CSV.m_ArrayData.Count; i++)
                {
                    if (i == 2)
                    {
                        // pc1.nNextLevel = CSV.GetInt(i + 1, "batteryLevel");// 2;
                        pc1.nNextLevel = CSV.GetInt(i, "batteryLevel");// 2;
                        pc1.nPrevLevel = 0;
                        pc1.nLevel = CSV.GetInt(i, "batteryLevel");
                        pc1.nModule = CSV.GetInt(i, "scene");
                        common.dicPaoConfig.Add(pc1.nLevel, pc1);
                        pcOld = pc1;
                        continue;
                    }
                    int nLevel = CSV.GetInt(i, "batteryLevel");
                    PaoConfig pc = new PaoConfig();
                    pc.nLevel = nLevel;
                    pc.nModule = CSV.GetInt(i, "scene");
                    if (pcOld != null)
                    {
                        pcOld.nNextLevel = nLevel;
                        pc.nPrevLevel = pcOld.nLevel;
                    }
                    if (i == CSV.m_ArrayData.Count - 1)
                    {
                        pc.nNextLevel = -1;
                    }
                    if (common.dicPaoConfig.ContainsKey(nLevel))
                    {
                        Debug.Log("nLevel" + nLevel);
                    }
                    else
                    {
                        common.dicPaoConfig.Add(nLevel, pc);
                    }
                    pcOld = pc;
                }
            });
            foreach (var item in common.dicPaoConfig)
            {
                Debug.Log("pc1.nNextLevel" + item.Key);
            }
            //CSV.LoadFileTest("cfg_pao.txt");
            //PaoConfig pcOld = null;
            //PaoConfig pc1 = new PaoConfig();
            //pc1.nNextLevel = 2;
            //pc1.nPrevLevel = 0;
            //pc1.nLevel = 1;
            //pc1.nModule = 1;
            //common.dicPaoConfig.Add(pc1.nLevel, pc1);
            //pcOld = pc1;

          
        }
      
    }
}