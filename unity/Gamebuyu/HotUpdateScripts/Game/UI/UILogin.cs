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
using com.maple.common.login.proto;
using ProtoBuf;
using com.maple.network.proto;
using System.Security.Cryptography;
using NetLib;
using System;
using com.maple.game.osee.proto;
using UnityEngine.UI;
using CoreGame;
using System.Collections;
using com.maple.game.osee.proto.lobby;
using DG.Tweening;

namespace Game.UI 
{
    public class UILogin : MonoBehaviour 
    {
        private static bool IsFirst = true;

        //账号密码登陆
        public Transform view_accLogin;

        public Button btn_gxgg;
        public InputField input_user;
        public InputField input_pwd;
        public Button btn_commonLogin;

        public Button btn_nicheng;
        public Button btn_viewaccLogin_close;  
  

        public GameObject view_parent, view_login, view_register, view_xuke;

        public InputField input_userReg;
        public InputField input_pwdReg, input_pwdRegOk;
        public Button btn_wxLogin, btn_xuke, btn_xukeClose;
        public Button  btn_register;
        public Button btn_loginView;
        public Button btn_closeLogin;
        public Toggle tog_savePwd;
       // public Toggle tog_login, tog_reg;

        public Text txt_ver;
        public Text txt_logining;
        //按钮
        public Button btn_SelectNickName;
        public Button btn_StartGame;

        //---------------忘记密码---------------
        public Button btn_forget;
        public Button btn_zhuce;
        //public GameObject view_forget, view_forgetOk;
        //public Button btn_forgetClose, btn_forgetOkClose;
        //public Button btn_forgetOkView, btn_changePwd, btn_getYanzheng;
        //public InputField input_forgetAcc;
        //public InputField input_yanzheng, input_newPwd, input_newPwdOk;
        //public Text txt_forgetAcc, txt_forgetPhone;
        //public Text txt_PcTips;
        //public Button btn_gxgg;
        //------选择游客昵称  
        public Transform tra_SelectNickName;
        public Button btn_SelectNickName_close;
        public Button btn_SelectNickName_ok;
        public Transform tra_YouKeCode;
        public Text text_YouKeCode;

        public Transform Select_Content;
        public GameObject item_zhanghao;

        public Button btn_CreateNickName;
        public Button btn_youketest;
        public Text txtLogin;
        //创建昵称
        public Transform LoginMask;
        public Transform Tra_CreatName;
        public InputField input_CraetName_Name;
        public InputField input_CreatName_Password;
        public InputField input_CreatName_confirm;
        public InputField input_CraetName_Captcha;
        public InputField input_CraetName_phone;

        public Text input_CraetName_waitTime;
        public Button btn_CreatName_GetCaptcha;
        public Button btn_CreateNickName_ok;
        public Button btn_CreateNickName_close;
        public Button btn_kefu;

        //找回密码
        public GameObject FindPassword;
        public GameObject FindPasswordOk;

        public Text txt_forgetAcc; 
        public Text txt_forgetPhone;
        public Button btn_forgetOkView;
        public Button btn_changePwd;
        public Button btn_getYanzheng;
        
        public Button btn_forgetClose, btn_forgetOkClose;

        public InputField input_forgetAcc;
        public InputField input_yanzheng, input_newPwd, input_newPwdOk;
        public  void Awake()
        {
            if (IsFirst)
            {
              
                //Debug.LogError("进入UIPath.UILogin OVer");
                IsFirst = false;
                FindCompent();

                btn_viewaccLogin_close.onClick.AddListener(() => {
                    view_accLogin.gameObject.SetActive(false);
                });
                btn_youketest.onClick.AddListener(() =>
                {
                    NetMessage.Login.Req_TouristLoginRequest("");
                });
                btn_nicheng.onClick.AddListener(() => {
                    view_accLogin.gameObject.SetActive(true);
                });
                btn_commonLogin.onClick.AddListener(()=> {
                    LoginMask.gameObject.SetActive(true);
                    //这个只是保存一下
                    PlayerData.Acount = input_user.text;
                    PlayerData.PassWord = input_pwd.text;

                    if (NetMgr.Instance.ConnectState >= 2)
                    {
                        //Debug.Log("账号"+ PlayerData.Acount);
                        //Debug.Log("账号密码" + PlayerData.PassWord);
                        NetMessage.Login.Req_CommonLoginRequest(PlayerData.Acount, common.MD5Encrypt(PlayerData.PassWord), true, false);
                    }
                    else if (NetMgr.Instance.ConnectState == 1)
                    {
                    }
                    else
                    {
                        common.OpenUsername = input_user.text;
                        common.OpenPassw = common.MD5Encrypt(input_pwd.text);
                        common.nLoinWay = 1;
                        NetMgr.Instance.OnSpecialLogin();
                    }
                    //PlayerData.Acount = input_user.text;
                    //PlayerData.PassWord = input_pwd.text;
                    //NetMessage.Login.Req_CommonLoginRequest(PlayerData.Acount, common.MD5Encrypt(PlayerData.PassWord));
                });
                btn_gxgg.onClick.AddListener(() =>
                {
                    UIMgr.ShowUI(UIPath.UIGonggao);
                });
                btn_kefu.onClick.AddListener(() =>
                {
                    //StopCoroutine(LoginIEnumer());
                    txtLogin.text = "开始游戏";
                    UIMgr.ShowUI(UIPath.UIKefu);
                });
                btn_CreatName_GetCaptcha.onClick.AddListener(() =>
                {
                    PlayerData.str_CraetName_phone = input_CraetName_phone.text.Trim();
                    if (NetMgr.Instance.ConnectState >= 2)
                    {
                        NetMessage.Agent.Req_AccountPhoneCheckRequest(PlayerData.str_CraetName_phone);
                        btn_CreatName_GetCaptcha.interactable = false;
                        StartCoroutine(tmp(60));
                    }
                    else if (NetMgr.Instance.ConnectState == 1)
                    {
                        Debug.LogError("登录 ConnectState状态为1");
                    }
                    else 
                    {
                        PlayerData.str_CraetName_phone = input_CraetName_phone.text.Trim();
                        GetYanZhengMa();
                        btn_CreatName_GetCaptcha.interactable = false;
                        StartCoroutine(tmp(60));
                    }
       
                });
                btn_CreateNickName.onClick.AddListener(() =>
                {
                    //StopCoroutine(LoginIEnumer());
                    txtLogin.text = "开始游戏";

                    Tra_CreatName.gameObject.SetActive(true);
                });
                btn_CreateNickName_close.onClick.AddListener(() =>
                {
                    Tra_CreatName.gameObject.SetActive(false);
                    input_CraetName_Name.text = "";
                    input_CreatName_Password.text = "";
                    input_CreatName_confirm.text = "";
                    input_CraetName_Captcha.text = "";
                    input_CraetName_phone.text = "";
                    btn_CreatName_GetCaptcha.interactable = true;
                    input_CraetName_waitTime.text = "";
                });
                btn_SelectNickName_ok.onClick.AddListener(() =>
                {
                    //StopCoroutine(LoginIEnumer());
                    tra_SelectNickName.gameObject.SetActive(false);
                    if (UserInfo.playerData.list_Account.Count > 0)
                    {

                        text_YouKeCode.text = UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1].Account;
                        tra_YouKeCode.gameObject.SetActive(true);
                    }
                    else
                    {
                        text_YouKeCode.text = "";
                        tra_YouKeCode.gameObject.SetActive(false);
                    }
                });
                btn_CreateNickName_ok.onClick.AddListener(() =>
                {
                    if (input_CraetName_Name.text == "")
                    {
                        MessageBox.Show("昵称不能为空");
                        return;
                    }
                    if (input_CreatName_Password.text == "")
                    {
                        MessageBox.Show("密码不能为空");
                        return;
                    }
                    if (input_CreatName_confirm.text != input_CreatName_Password.text)
                    {
                        MessageBox.Show("两次密码输入不一致");
                        return;
                    }
                    if (input_CraetName_phone.text == "")
                    {
                        MessageBox.Show("请输入手机号");
                        return;
                    }
                    if (input_CraetName_Captcha.text == "")
                    {
                        MessageBox.Show("请输入手机短信验证码");
                        return;
                    }
                    string psw = common.MD5Encrypt(input_CreatName_Password.text);
                    //赋值
                    PlayerData.Acount = input_CraetName_Name.text;
                    PlayerData.PassWord = input_CreatName_Password.text;
                    PlayerData.inputCraetName_phone = input_CraetName_phone.text;
                    PlayerData.inputCraetName_Captcha = input_CraetName_Captcha.text;


                    if (NetMgr.Instance.ConnectState >= 2)
                    {
                        txt_logining.text = "正在登录游戏中，请耐心等待";
                        NetMessage.Login.Req_UserRegisterRequest(input_CraetName_Name.text, psw, input_CraetName_phone.text, input_CraetName_Captcha.text);
                    }
                    else if (NetMgr.Instance.ConnectState == 1)
                    {

                    }
                    else
                    {
                        common.nLoinWay = 2;
                        txt_logining.text = "正在登录游戏中，请耐心等待";
                        NetMgr.Instance.OnSpecialLogin();
                    }
                });

                btn_SelectNickName.onClick.AddListener(() => {
                    //StopCoroutine(LoginIEnumer());
                    tra_SelectNickName.gameObject.SetActive(true);
                    txtLogin.text = "开始游戏";
                    UserInfo.LoadPlayerData();
                    var ListCount = UserInfo.playerData.list_Account;
                    for (int i = Select_Content.childCount - 1; i > 0; i--)
                    {
                        Destroy(Select_Content.GetChild(i).gameObject);
                    }
                    foreach (var item in ListCount)
                    {
                        GameObject varcount = GameObject.Instantiate(item_zhanghao);
                        varcount.transform.SetParent(Select_Content);
                        varcount.transform.localScale = Vector3.one;
                        varcount.transform.localPosition = Vector3.zero;
                        varcount.gameObject.SetActive(true);
                        varcount.transform.SetSiblingIndex(1);
                        varcount.transform.Find("Name").GetComponent<Text>().text = item.Account;
                        varcount.transform.Find("btn_delete").GetComponent<Button>().onClick.AddListener(() =>
                        {
                            UserInfo.playerData.list_Account.Remove(item);
                            UserInfo.SavePlayerData();
                            Destroy(varcount);
                        });
                        varcount.transform.Find("btnChoice").GetComponent<Button>().onClick.AddListener(() =>
                        {
                            tra_SelectNickName.gameObject.SetActive(false);

                            var index = UserInfo.playerData.list_Account.IndexOf(item);


                            PlayerAccount varIndex = UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1];
                            UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1] = UserInfo.playerData.list_Account[index];
                            UserInfo.playerData.list_Account[index] = varIndex;
                            UserInfo.SavePlayerData();

                            if (UserInfo.playerData.list_Account.Count > 0)
                            {

                                text_YouKeCode.text = UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1].Account;
                                tra_YouKeCode.gameObject.SetActive(true);
                            }
                            else
                            {
                                text_YouKeCode.text = "";
                                tra_YouKeCode.gameObject.SetActive(false);
                            }
                        });

                    }
                });

                btn_SelectNickName_close.onClick.AddListener(() => {
                    tra_SelectNickName.gameObject.SetActive(false);
                    //StopCoroutine(LoginIEnumer());
                    if (UserInfo.playerData.list_Account.Count > 0)
                    {
                        text_YouKeCode.text = UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1].Account;
                        tra_YouKeCode.gameObject.SetActive(true);
                    }
                    else
                    {
                        text_YouKeCode.text = "";
                        tra_YouKeCode.gameObject.SetActive(false);
                    }
                });
                btn_StartGame.onClick.AddListener(() => {
                    txtLogin.text = "开始游戏";
                    if (NetMgr.Instance.ConnectState >= 2)
                    {
             
                        LoginDefault();
                    }
                    else if (NetMgr.Instance.ConnectState == 1)
                    {
                        Debug.LogError("登录 ConnectState状态为1");
                    }
                    else
                    {
                        ConnectNetLogin();
                    }
                    ////string UserName=PlayerPrefs.GetString("UserName");

                    //// NetMessage.Login.Req_TouristLoginRequest(UserName); 
                    //btn_StartGame.interactable = false;
                    ////StartCoroutine(Invokebtn_StartGame());
                    ////StopCoroutine(LoginIEnumer());
                    //txtLogin.text = "开始游戏";
                    //LoginDefault();
                    //StartCoroutine(OpenInteractable());
                });
                //忘记密码
                btn_forget.onClick.AddListener(() => {
                    //UIMgr.ShowUI(UIPath.UIResetPasswd);
                    FindPassword.gameObject.SetActive(true);
                });
                btn_zhuce.onClick.AddListener(() => {
                    view_accLogin.gameObject.SetActive(false);
                    Tra_CreatName.gameObject.SetActive(true);
                });
                btn_forgetOkView.onClick.AddListener(() => {
                    //view_forgetOk.SetActive(true);
                    //向服务器发送请求找回密码需求
                   // xx
                    //NetMessage.OseeLobby.Req_GetResetPasswordPhoneNumRequest(input_forgetAcc.text);

                    PlayerData.str_input_forgetAcc = input_forgetAcc.text.Trim();
                    if (NetMgr.Instance.ConnectState >= 2)
                    {
                        NetMessage.OseeLobby.Req_GetResetPasswordPhoneNumRequest(PlayerData.str_input_forgetAcc);
                    }
                    else if (NetMgr.Instance.ConnectState == 1)
                    {
                        Debug.LogError("登录 ConnectState状态为1");
                    }
                    else
                    {
                        common.nLoinWay = 5;
                        NetMgr.Instance.OnSpecialLogin();
                    }
                });
                btn_forgetOkClose.onClick.AddListener(() =>
                {
                    FindPasswordOk.SetActive(false);
                    input_forgetAcc.text = "";
                    input_newPwd.text = "";
                    input_newPwdOk.text = "";
                    input_yanzheng.text = "";
                });
                //btn_forgetOkView.onClick.AddListener(() => {
                //    //view_forgetOk.SetActive(true);
                //    //向服务器发送请求找回密码需求
                //    NetMessage.OseeLobby.Req_GetResetPasswordPhoneNumRequest(input_forgetAcc.text);
                //});
                btn_getYanzheng.onClick.AddListener(() =>
                {
                    NetMessage.OseeLobby.Req_ResetPasswordPhoneCheckRequest(txt_forgetAcc.text);
                });
                btn_changePwd.onClick.AddListener(() =>
                {
                    int nYanzheng = 0;
                    int.TryParse(input_yanzheng.text, out nYanzheng);
                    if (nYanzheng > 0)
                    {
                        if (input_newPwd.text == input_newPwdOk.text)
                        {
                            string strPwdMd5 = common.MD5Encrypt(input_newPwd.text.Trim());
                            NetMessage.OseeLobby.Req_ResetPasswordRequest(txt_forgetAcc.text, nYanzheng, strPwdMd5);
                        }
                        else
                            MessageBox.Show("两次密码输入不一致", null, null, null);
                    }
                    else
                    {
                        MessageBox.Show("请正确输入验证码", null, null, null);
                    }
                });

                common.IsSavePwd = PlayerPrefs.GetInt("IsSavePwd");
                if (common.IsSavePwd == 0)
                {
                    tog_savePwd.isOn = true;
                }
                else
                {
                    tog_savePwd.isOn = false;
                }
                tog_savePwd.onValueChanged.AddListener((arg) =>
                {
                    if (arg)
                    {
                        common.IsSavePwd = 0;
                        PlayerPrefs.SetInt("IsSavePwd", common.IsSavePwd);
                    }
                    else
                    {
                        common.IsSavePwd = 1;
                        PlayerPrefs.SetInt("IsSavePwd", common.IsSavePwd);
                    }
                  
                });
                btn_forgetClose.onClick.AddListener(() =>
                {
                    FindPassword.gameObject.SetActive(false);
                });
            }
        }
        IEnumerator OpenInteractable() {
            yield return new WaitForSeconds(2f);
            btn_StartGame.interactable = true;
        }
        //IEnumerator LoginIEnumer()
        //{
        //    txtLogin.gameObject.SetActive(true);
   
        //    txtLogin.text ="开始游戏5s";
        //    yield return new WaitForSeconds(1f);
        //    txtLogin.text = "开始游戏4s";
        //    yield return new WaitForSeconds(1f);
        //    txtLogin.text = "开始游戏3s";
        //    yield return new WaitForSeconds(1f);
        //    txtLogin.text = "开始游戏2s";
        //    yield return new WaitForSeconds(1f);
        //    txtLogin.text = "开始游戏1s";
        //    txtLogin.gameObject.SetActive(false);
        //    LoginDefault();
        //}
        //IEnumerator Invokebtn_StartGame()
        //{
        //    yield return new WaitForSeconds(6f);
        //    //if (this.enabled == true && btn_StartGame.interactable == false)
        //    //{
        //    //    MessageBox.ShowPopOneMessage("当前网络连接异常,请稍后尝试！");
        //    //    NetMgr.Instance.Init();
        //    //}
        //    btn_StartGame.interactable = true;

        //}
        void ConnectNetLogin()
        {
            var varlist_Account = UserInfo.playerData.list_Account;
            if (varlist_Account.Count > 0)
            {
                btn_StartGame.interactable = false;
                LoginMask.gameObject.SetActive(true);
                txt_logining.text = "正在登录游戏中，请耐心等待";
                common.OpenUsername = varlist_Account[varlist_Account.Count - 1].Account;
                string psw = common.MD5Encrypt(varlist_Account[varlist_Account.Count - 1].PassWorld);
                common.OpenPassw = psw;

                common.nLoinWay = 1;
                NetMgr.Instance.OnSpecialLogin();
                StartCoroutine(OpenInteractable());
            }
            else
            {
                view_accLogin.gameObject.SetActive(true);
            }
        }
        void GetYanZhengMa() 
        {
            common.nLoinWay = 4;
            NetMgr.Instance.OnSpecialLogin();
        }
        void LoginDefault()
        {
            var varlist_Account = UserInfo.playerData.list_Account;
            if (varlist_Account.Count > 0)
            {
                btn_StartGame.interactable = false;
                LoginMask.gameObject.SetActive(true);

                if (varlist_Account[varlist_Account.Count - 1].PassWorld == "")
                {
                    common.LoginState = 2;
                    common.Login_Zhanghao = varlist_Account[varlist_Account.Count - 1].Account;
                    common.Login_Mima = "";
                    NetMessage.Login.Req_TouristLoginRequest(varlist_Account[varlist_Account.Count - 1].Account);
                }
                else
                {
                    common.LoginState = 3;
                    //呢称登陆
                    string psw = common.MD5Encrypt(varlist_Account[varlist_Account.Count - 1].PassWorld);
                    common.Login_Zhanghao = varlist_Account[varlist_Account.Count - 1].Account;
                    common.Login_Mima = varlist_Account[varlist_Account.Count - 1].PassWorld;
                    NetMessage.Login.Req_CommonLoginRequest(varlist_Account[varlist_Account.Count - 1].Account, psw, true, false);
                }
            }
            else
            {         
                view_accLogin.gameObject.SetActive(true);
            }
            ////NetMessage.Login.Req_TouristLoginRequest("HY_187913y11tR");
            ////return;
            //var varlist_Account = UserInfo.playerData.list_Account;
            //if (varlist_Account.Count > 0)
            //{
            //    if (varlist_Account[varlist_Account.Count - 1].PassWorld == "")
            //    {
            //        common.LoginState = 2;
            //        common.Login_Zhanghao = varlist_Account[varlist_Account.Count - 1].Account;
            //        common.Login_Mima = "";
            //        NetMessage.Login.Req_TouristLoginRequest(varlist_Account[varlist_Account.Count - 1].Account);
            //        //Debug.LogError("Req_TouristLoginRequest()2");
            //    }
            //    else
            //    {
            //        common.LoginState = 3;
            //        //呢称登陆
            //        string psw = common.MD5Encrypt(varlist_Account[varlist_Account.Count - 1].PassWorld);

            //        common.Login_Zhanghao = varlist_Account[varlist_Account.Count - 1].Account;
            //        common.Login_Mima = varlist_Account[varlist_Account.Count - 1].PassWorld;
            //        NetMessage.Login.Req_CommonLoginRequest(varlist_Account[varlist_Account.Count - 1].Account, psw);
            //        //Debug.LogError("Req_CommonLoginRequest()3");
            //    }
            //}
            //else
            //{
            //    view_accLogin.gameObject.SetActive(true);
            //    //common.LoginState = 2;
            //    //common.Login_Zhanghao = "";
            //    //common.Login_Mima = "";
            //    //int day = PlayerPrefs.GetInt("NowDay");
            //    //if (day == DateTime.Now.Date.Day) //说明是同一天
            //    //{
            //    //    int m = PlayerPrefs.GetInt("CreateTimes");
            //    //    if (m > 3)
            //    //    {
            //    //        MessageBox.Show("每天只能创建三个游客账户");
            //    //        return;
            //    //    }
            //    //    else
            //    //    {
            //    //        PlayerPrefs.SetInt("CreateTimes", m + 1);
            //    //    }
            //    //}
            //    //else //进入了第二天
            //    //{
            //    //    PlayerPrefs.SetInt("CreateTimes", 1);
            //    //    PlayerPrefs.SetInt("NowDay", DateTime.Now.Date.Day);
            //    //}
            //    ////Debug.LogError("Req_TouristLoginRequest()");
            //    //NetMessage.Login.Req_TouristLoginRequest("");
            //}
        }
        Transform TransformFind(string str) {
            var tmp = this.transform.Find(str);
            if (tmp==null)
            {
                Debug.LogError("未找到路径："+str);
                return null;
            }
            else
            {
                return tmp;
            }
        }
        void FindCompent() {
            view_accLogin = TransformFind("view_accLogin");
            tog_savePwd = TransformFind("view_accLogin/bg/view_login/tog_savePwd").GetComponent<Toggle>();
            btn_viewaccLogin_close = TransformFind("view_accLogin/bg/btn_close").GetComponent<Button>();
            btn_commonLogin = TransformFind("view_accLogin/bg/view_login/btn_accLogin").GetComponent<Button>();
            LoginMask = TransformFind("LoginMask");
            txt_logining= TransformFind("LoginMask/txt_logining").GetComponent<Text>();
            input_user = TransformFind("view_accLogin/bg/view_login/bg_acc/input_acc").GetComponent<InputField>();
            input_pwd = TransformFind("view_accLogin/bg/view_login/bg_pwd/input_pwd").GetComponent<InputField>();
            btn_gxgg = TransformFind("TouristLogin/btn_gxgg").GetComponent<Button>();
            btn_forget = TransformFind("view_accLogin/bg/view_login/btn_fogetPwd").GetComponent<Button>();
            btn_zhuce = TransformFind("view_accLogin/bg/view_login/btn_zhuce").GetComponent<Button>();
            btn_youketest = TransformFind("yktest").GetComponent<Button>();
            btn_SelectNickName = TransformFind("TouristLogin/tra_YouKeCode/btn_SelectNickName").GetComponent<Button>();
            btn_StartGame = TransformFind("TouristLogin/btn_StartGame").GetComponent<Button>();
            Tra_CreatName = TransformFind("Tra_CreatName");
            input_CraetName_Name = TransformFind("Tra_CreatName/nicheng/input_CraetName_Name").GetComponent<InputField>();
            input_CreatName_Password = TransformFind("Tra_CreatName/setpassword/input_CreatName_Password").GetComponent<InputField>();
            input_CreatName_confirm = TransformFind("Tra_CreatName/confirm/input_CreatName_confirm").GetComponent<InputField>();
            
            input_CraetName_phone = TransformFind("Tra_CreatName/CellPhone/input_CreatName_phone").GetComponent<InputField>();
            input_CraetName_Captcha = TransformFind("Tra_CreatName/Captcha/input_CreatName_Captcha").GetComponent<InputField>();
            btn_CreatName_GetCaptcha = TransformFind("Tra_CreatName/btn_CreatName_GetCaptcha").GetComponent<Button>();

            input_CraetName_waitTime = TransformFind("Tra_CreatName/Text").GetComponent<Text>();
            btn_CreateNickName_ok = TransformFind("Tra_CreatName/btn_CreateNickName_ok").GetComponent<Button>();
            btn_CreateNickName_close = TransformFind("Tra_CreatName/btn_CreateNickName_close").GetComponent<Button>();
            btn_kefu = TransformFind("TouristLogin/btn_kefu").GetComponent<Button>();
            btn_nicheng = TransformFind("TouristLogin/btn_nicheng").GetComponent<Button>();
            tra_SelectNickName = TransformFind("tra_SelectNickName");
            btn_SelectNickName_close = TransformFind("tra_SelectNickName/btn_SelectNickName_close").GetComponent<Button>();
            btn_SelectNickName_ok = TransformFind("tra_SelectNickName/btn_SelectNickName_ok").GetComponent<Button>();
            Select_Content = TransformFind("tra_SelectNickName/Scroll View/Viewport/Select_Content");
            item_zhanghao = TransformFind("tra_SelectNickName/Scroll View/Viewport/Select_Content/item_zhanghao").gameObject;
            tra_YouKeCode = TransformFind("TouristLogin/tra_YouKeCode");
            text_YouKeCode = TransformFind("TouristLogin/tra_YouKeCode/text_YouKeCode").GetComponent<Text>();
            btn_CreateNickName = TransformFind("tra_SelectNickName/btn_CreateNickName").GetComponent<Button>();
            txtLogin = TransformFind("TouristLogin/btn_StartGame/txtLogin").GetComponent<Text>();

            FindPassword = TransformFind("FindPassword").gameObject;
            FindPasswordOk = TransformFind("FindPasswordOk").gameObject;

            txt_forgetAcc = TransformFind("FindPasswordOk/bg_acc/txt_acc").GetComponent<Text>();
            txt_forgetPhone = TransformFind("FindPasswordOk/bg_phone/txt_phone").GetComponent<Text>();
           
            btn_forgetOkView = TransformFind("FindPassword/btn_ok").GetComponent<Button>();
 
            btn_getYanzheng = TransformFind("FindPasswordOk/bg_yanzheng/btn_getYanzheng").GetComponent<Button>();
          
            btn_forgetClose = TransformFind("FindPassword/btn_close").GetComponent<Button>();
            btn_forgetOkClose = TransformFind("FindPasswordOk/btn_close").GetComponent<Button>();

            input_forgetAcc = TransformFind("FindPassword/setpassword/InputField").GetComponent<InputField>();
           
            input_yanzheng = TransformFind("FindPasswordOk/bg_yanzheng/input_yanzheng").GetComponent<InputField>();

            input_newPwd = TransformFind("FindPasswordOk/bg_newPwd/input_newPwd").GetComponent<InputField>();
            input_newPwdOk = TransformFind("FindPasswordOk/bg_newPwdOk/input_newPwdOk").GetComponent<InputField>();

            btn_changePwd = TransformFind("FindPasswordOk/btn_change").GetComponent<Button>();
            common2.LoginMask = LoginMask;
            //public InputField input_forgetAcc;
            //public InputField input_yanzheng, input_newPwd, input_newPwdOk;
            //view_parent = TransformFind("view_accLogin").gameObject;
            //view_login = TransformFind("view_accLogin/bg/view_login").gameObject;
            //view_register = TransformFind("view_accLogin/bg/view_reg").gameObject;
            //view_xuke = TransformFind("view_xuke").gameObject;
            //input_user = TransformFind("view_accLogin/bg/view_login/bg_acc/input_acc").GetComponent<InputField>();
            //input_pwd = TransformFind("view_accLogin/bg/view_login/bg_pwd/input_pwd").GetComponent<InputField>();
            //input_userReg = TransformFind("view_accLogin/bg/view_reg/bg_acc/input_acc").GetComponent<InputField>();
            //input_pwdReg = TransformFind("view_accLogin/bg/view_reg/bg_pwd/input_pwd").GetComponent<InputField>();
            //input_pwdRegOk = TransformFind("view_accLogin/bg/view_reg/bg_pwdOk/input_pwdOk").GetComponent<InputField>();
            //btn_wxLogin = TransformFind("btn_wxLogin").GetComponent<Button>();
            //btn_xuke = TransformFind("bg/btn_xuke").GetComponent<Button>();
            //btn_xukeClose = TransformFind("view_xuke/bg/btn_close").GetComponent<Button>();
            //btn_commonLogin = TransformFind("view_accLogin/bg/view_login/btn_accLogin").GetComponent<Button>();
            //btn_register = TransformFind("view_accLogin/bg/view_reg/btn_reg").GetComponent<Button>();
            //btn_loginView = TransformFind("TouristLogin/btn_nicheng").GetComponent<Button>();
            //btn_closeLogin = TransformFind("view_accLogin/bg/btn_close").GetComponent<Button>();
            //tog_savePwd = TransformFind("view_accLogin/bg/view_login/tog_savePwd").GetComponent<Toggle>();

            //txt_ver = TransformFind("ver").GetComponent<Text>();
            //txt_logining = TransformFind("mask/txt_logining").GetComponent<Text>();
            //btn_forget = TransformFind("view_accLogin/bg/view_login/btn_fogetPwd").GetComponent<Button>();

            //btn_gxgg = TransformFind("TouristLogin/btn_gxgg").GetComponent<Button>();
            //btn_SelectNickName = TransformFind("TouristLogin/kuang/btn_change").GetComponent<Button>();
            //btn_StartGame = TransformFind("TouristLogin/btn_start1").GetComponent<Button>();
            //tra_SelectNickName = TransformFind("SelectNickName");
            //btn_SelectNickName_close = TransformFind("SelectNickName/btn_cloe").GetComponent<Button>();
            //btn_SelectNickName_ok = TransformFind("SelectNickName/btn_ok").GetComponent<Button>();
            //tra_YouKeCode = TransformFind("TouristLogin/kuang");
            //text_YouKeCode = TransformFind("TouristLogin/kuang/Text").GetComponent<Text>();
            //item_zhanghao = TransformFind("SelectNickName/Scroll View/Viewport/Content/item").gameObject;
            //btn_CreateNickName = TransformFind("SelectNickName/btn_Captcha").GetComponent<Button>();
            //txtLogin = TransformFind("TouristLogin/btn_start1/auto/Text").GetComponent<Text>();
            //Tra_CreatName = TransformFind("CreatName");
            //input_CraetName_Name = TransformFind("CreatName/nicheng/InputField").GetComponent<InputField>();
            //input_CraetName_Password = TransformFind("CreatName/setpassword/InputField").GetComponent<InputField>();
            //input_CraetName_confirm = TransformFind("CreatName/confirm/InputField").GetComponent<InputField>();

            //input_CraetName_waitTime = TransformFind("CreatName/Text").GetComponent<Text>();
            //btn_CreateNickName_ok = TransformFind("CreatName/btn_ok").GetComponent<Button>();
            //btn_CreateNickName_close = TransformFind("CreatName/btn_cloe").GetComponent<Button>();

        }
        private void Start()
        {
            //if (!IsFirst) {
            //    input_user.text = PlayerPrefs.GetString("Acc");
            //    input_pwd.text = PlayerPrefs.GetString("Psw");
            //} 
            //  common.LoadAtlasSprite("allitem/icon");
            //common.LoadAtlasSprite("item/2");
        }
        private void On_UserRegisterResponse(UEventContext obj)
        {
            var pack = obj.GetData<UserRegisterResponse>();




            Tra_CreatName.gameObject.SetActive(false);
        }
        
        ///// <summary>
        ///// 获取重置密码手机号返回
        ///// <summary>
        //private void On_GetResetPasswordPhoneNumResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<GetResetPasswordPhoneNumResponse>();
        //    view_forget.SetActive(false);
        //    view_forgetOk.SetActive(true);
        //    txt_forgetAcc.text = pack.username;
        //    txt_forgetPhone.text = pack.phoneNum;
        //}
        void OnEnable() {
            common.IsAlreadyLogin = false;
            if (common2.LoginMask != null)
            {
                common2.LoginMask.DOKill();
            }
            input_user.text = PlayerPrefs.GetString("Acc");
            input_pwd.text = PlayerPrefs.GetString("Psw");
            //  common.IsLoginState = false;
            btn_StartGame.interactable = true;
            common.HeartTime = 0f;

            UEventDispatcher.Instance.AddEventListener(UEventName.BangNiceNameResponse, On_BangNiceNameResponse);//绑定昵称响应
            //UEventDispatcher.Instance.AddEventListener(UEventName.GetResetPasswordPhoneNumResponse, On_GetResetPasswordPhoneNumResponse);//获取重置密码手机号返回                                                                                                  //SoundMgr.Instance.loginBgMusic();
            UEventDispatcher.Instance.AddEventListener(UEventName.UserRegisterResponse, On_UserRegisterResponse);
            UEventDispatcher.Instance.AddEventListener(UEventName.GetResetPasswordPhoneNumResponse, On_GetResetPasswordPhoneNumResponse);//获取重置密码手机号返回
            UEventDispatcher.Instance.AddEventListener(UEventName.ResetPasswordPhoneCheckResponse, On_ResetPasswordPhoneCheckResponse);//重置用户密码手机验证返回
            UEventDispatcher.Instance.AddEventListener(UEventName.ResetPasswordResponse, On_ResetPasswordResponse);//重置密码返回
            // SoundLoadPlay.ChangeBgMusic("sd_t5_hall_background_music");
            SoundLoadPlay.ChangeBgMusic("LobbyBG");
            UIMgr.CloseUI(UIPath.UIGuangbo);
            // tog_reg.isOn = false;
            Tra_CreatName.gameObject.SetActive(false);
            tra_SelectNickName.gameObject.SetActive(false);
            view_accLogin.gameObject.SetActive(false);

            UserInfo.LoadPlayerData();
        
            if (UserInfo.playerData.list_Account.Count > 0)
            {

                text_YouKeCode.text = UserInfo.playerData.list_Account[UserInfo.playerData.list_Account.Count - 1].Account;
                tra_YouKeCode.gameObject.SetActive(true);
            }
            else
            {
                text_YouKeCode.text = "";
                tra_YouKeCode.gameObject.SetActive(false);
            }
            UIMgr.CloseAllwithOut(UIPath.UILogin);
        }
        /// <summary>
        /// 获取重置密码手机号返回
        /// <summary>
        private void On_GetResetPasswordPhoneNumResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetResetPasswordPhoneNumResponse>();
            FindPassword.SetActive(false);
            FindPasswordOk.SetActive(true);
            txt_forgetAcc.text = pack.username;
            txt_forgetPhone.text = pack.phoneNum;
        }
        /// <summary>
        /// 重置用户密码手机验证返回
        /// <summary>
        private void On_ResetPasswordPhoneCheckResponse(UEventContext obj)
        {
            var pack = obj.GetData<ResetPasswordPhoneCheckResponse>();
            string str = pack.result ? "成功" : "失败";
            MessageBox.ShowPopMessage("发送验证码" + str);
        }
        /// <summary>
        /// 重置密码返回
        /// <summary>
        private void On_ResetPasswordResponse(UEventContext obj)
        {
            var pack = obj.GetData<ResetPasswordResponse>();
            string str = pack.result ? "成功" : "失败";
            MessageBox.ShowPopMessage("重置密码" + str);
        }
        void OnDisable() {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BangNiceNameResponse, On_BangNiceNameResponse);//绑定昵称响应
            //UEventDispatcher.Instance.RemoveEventListener(UEventName.GetResetPasswordPhoneNumResponse, On_GetResetPasswordPhoneNumResponse);//获取重置密码手机号返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UserRegisterResponse, On_UserRegisterResponse);
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetResetPasswordPhoneNumResponse, On_GetResetPasswordPhoneNumResponse);//获取重置密码手机号返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ResetPasswordPhoneCheckResponse, On_ResetPasswordPhoneCheckResponse);//重置用户密码手机验证返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ResetPasswordResponse, On_ResetPasswordResponse);//重置密码返回
        }
        IEnumerator tmp(int time)
        {
            while (time > 0)
            {
                time--;
                yield return new WaitForSeconds(1f);
                input_CraetName_waitTime.text = time.ToString() + "秒";
            }
            //btn_CraetName_GetCaptcha.interactable = true;
            input_CraetName_waitTime.text = "";
        }
        /// <summary>
        /// 绑定昵称响应
        /// <summary>
        private void On_BangNiceNameResponse(UEventContext obj)
        {
            Tra_CreatName.gameObject.SetActive(false);
            var pack = obj.GetData<BangNiceNameResponse>();
            //NetMessage.Login.Req_CommonLoginRequest(pack.username, pack.password);
        }
    }
}