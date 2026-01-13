using com.maple.game.osee.proto;
using CoreGame;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIAccount : MonoBehaviour
    {
        public GameObject[] ListPanel=new GameObject[7];
        /***绑定身份信息**/
        public Button btn_BangD_ok;
        public Button btn_BangD_Close;
        public InputField Input_BangD_IDNumber;
        public InputField Input_BangD_RealName;
        public InputField Input_BangD_CellPhone;
        public InputField Input_BangD_Captcha;
        public Button btn_BangD_Captcha;
        public Text txt_BangD_Captcha;
        /***安全中心**/
        public Button btn_SecurityC_changesendPwd;
        public Button btn_SecurityC_changelogPwd;
        public Button btn_SecurityC_wxjieban;
        public Button btn_SecurityC_changeBang;
        public Button btn_SecurityC_close;
        public Text txt_NowPhoneID;
        /***创建昵称**/
        public Button btn_CreatN_ok;
        public Button btn_CreatN_Close;
        public InputField Input_CreatN_nicheng;
        public InputField Input_CreatN_setpassword;
        public InputField Input_CreatN_confirm;
        public InputField Input_CreatN_Captcha;
        public InputField Input_CreatN_CellPhone;
        public Button btn_CreatN_Captcha;
        public Text txt_CreatN_Captcha;
        /***更换密码**/
        public Button btn_ChangeP_ok;
        public Button btn_ChangeP_Close;
        public InputField Input_ChangeP_setpassword;
        public InputField Input_ChangeP_confirm;
        public InputField Input_ChangeP_CellPhone;
        public InputField Input_ChangeP_Captcha;
        public Button btn_ChangeP_Captcha;
        public Text txt_ChangeP_Captcha;
        /***更换绑定**/
        public Button btn_Changeb_ok;
        public Button btn_Changeb_Close;

        public InputField Input_rightChangeb_realName;

        public InputField Input_leftChangeb_IDNumber;
        public InputField Input_rightChangeb_IDNumber;

        public InputField Input_leftChangeb_CellPhone;
        public InputField Input_rightChangeb_CellPhone;

        public InputField Input_leftChangeb_Captcha;
        public InputField Input_rightChangeb_Captcha;

        public Button btn_leftChangeb_Captcha;
        public Button btn_rightChangeb_Captcha;

        public Text txt_leftChangeb_Captcha;
        public Text txt_rightChangeb_Captcha;
        /***微信解绑**/
        public Button btn_Wxjiebang_ok;
        public Button btn_Wxjiebang_cancel;
        public Button btn_Wxjiebang_Close;
        public void SetPanel(UIAccountPanel varm)
        {
            switch (varm)
            {
                case UIAccountPanel.ChangePassword:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    Input_ChangeP_setpassword.text = "";
                    Input_ChangeP_confirm.text = "";
                    Input_ChangeP_CellPhone.text = "";
                    Input_ChangeP_Captcha.text = "";
                    btn_ChangeP_Captcha.interactable = true;
                    txt_ChangeP_Captcha.text = "";
                    ListPanel[(int)UIAccountPanel.ChangePassword].gameObject.SetActive(true);
                    break;
                case UIAccountPanel.Changebinding:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    Input_rightChangeb_realName.text = "";
                    Input_leftChangeb_IDNumber.text = "";
                    Input_rightChangeb_IDNumber.text = "";
                    Input_leftChangeb_CellPhone.text = "";
                    Input_rightChangeb_CellPhone.text = "";
                    Input_leftChangeb_Captcha.text = "";
                    Input_rightChangeb_Captcha.text = "";
                    btn_leftChangeb_Captcha.interactable = true;
                    btn_rightChangeb_Captcha.interactable = true;
                    txt_leftChangeb_Captcha.text = "";
                    txt_rightChangeb_Captcha.text = "";
                    ListPanel[(int)UIAccountPanel.Changebinding].gameObject.SetActive(true);
                    break;
                case UIAccountPanel.SecurityCenter:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    if (common.LoginState == 4)
                    {
                        btn_SecurityC_wxjieban.gameObject.SetActive(true);
                    }
                    else
                    {
                        btn_SecurityC_wxjieban.gameObject.SetActive(false);
                    }
                    ListPanel[(int)UIAccountPanel.SecurityCenter].gameObject.SetActive(true);
                    string str2 = GetxxxString(PlayerData.AccountPhone);
                    txt_NowPhoneID.text = "亲爱的玩家你当前绑定的号码为：" + str2;
                    break;
                case UIAccountPanel.BangDingIDNumber:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    Input_BangD_IDNumber.text = "";
                    Input_BangD_RealName.text = "";
                    Input_BangD_CellPhone.text = "";
                    Input_BangD_Captcha.text = "";
                    btn_BangD_Captcha.interactable = true;
                    txt_BangD_Captcha.text = "";
                    ListPanel[(int)UIAccountPanel.BangDingIDNumber].gameObject.SetActive(true);
                    break;
                case UIAccountPanel.BangDingNackName:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    Input_CreatN_nicheng.text = "";
                    Input_CreatN_setpassword.text = "";
                    Input_CreatN_confirm.text = "";
                    Input_CreatN_Captcha.text = "";
                    btn_CreatN_Captcha.interactable = true;
                    txt_CreatN_Captcha.text = "";
                    ListPanel[(int)UIAccountPanel.BangDingNackName].gameObject.SetActive(true);
                    break;
                case UIAccountPanel.WeiXinJieBang:
                    for (int i = 0; i < ListPanel.Length; i++)
                    {
                        ListPanel[i].gameObject.SetActive(false);
                    }
                    ListPanel[(int)UIAccountPanel.WeiXinJieBang].gameObject.SetActive(true);
                    break;
                default:
                    break;
            }
        }
        public static string GetxxxString(string Input)
        {
            string Output = "";
            switch (Input.Length)
            {
                case 1:
                    Output = "*";
                    break;
                case 2:
                    Output = Input.Substring(0, 1) + "*";
                    break;
                case 3:
                    Output = Input.Substring(0, 1) + "**";
                    break;
                case 4:
                    Output = Input.Substring(0, 1) + "***";
                    break;
                case 5:
                    Output = Input.Substring(0, 1) + "****";
                    break;
                case 6:
                    Output = Input.Substring(0, 1) + "*****";
                    break;
                case 0:
                    Output = "";
                    break;
                default:
                    Output = Input.Substring(0, 3);
                    for (int i = 0; i < Input.Length - 4; i++)
                    {
                        Output += "*";
                    }
                    Output += Input.Substring(Input.Length - 3, 3);
                    break;
            }
            return Output;
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ChangePhoneResponse, On_ChangePhoneResponse);//换绑发送验证码
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BangIdCardAllResponse, On_BangIdCardAllResponse);//绑定身份信息响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UpdateIdCardNoResponse, On_UpdateIdCardNoResponse);//换绑个人信息响应     
        }
        void FindCompent() {

            ListPanel[0] = this.transform.Find("ChangePassword").gameObject;
            ListPanel[1] = this.transform.Find("GiveAwayPassword").gameObject;
            ListPanel[2] = this.transform.Find("Changebinding").gameObject;
            ListPanel[3] = this.transform.Find("SecurityCenter").gameObject;
            ListPanel[4] = this.transform.Find("BangDingIDCard").gameObject;
            ListPanel[5] = this.transform.Find("CreatName").gameObject;
            ListPanel[6] = this.transform.Find("WxJieBang").gameObject;
            btn_BangD_ok = this.transform.Find("BangDingIDCard/btn_ok").GetComponent<Button>();
            btn_BangD_Close = this.transform.Find("BangDingIDCard/btn_cloe").GetComponent<Button>();
            Input_BangD_IDNumber = this.transform.Find("BangDingIDCard/IDNumber/InputField").GetComponent<InputField>();
            Input_BangD_RealName = this.transform.Find("BangDingIDCard/RealName/InputField").GetComponent<InputField>();
            Input_BangD_CellPhone = this.transform.Find("BangDingIDCard/CellPhone/InputField").GetComponent<InputField>();
            Input_BangD_Captcha = this.transform.Find("BangDingIDCard/Captcha/InputField").GetComponent<InputField>();
            btn_BangD_Captcha = this.transform.Find("BangDingIDCard/btn_Captcha").GetComponent<Button>();
            txt_BangD_Captcha = this.transform.Find("BangDingIDCard/bdyzmText").GetComponent<Text>();
            btn_SecurityC_changesendPwd = this.transform.Find("SecurityCenter/btn_change_sendPwd").GetComponent<Button>();
            btn_SecurityC_changelogPwd = this.transform.Find("SecurityCenter/btn_change_logPwd").GetComponent<Button>();
            btn_SecurityC_wxjieban = this.transform.Find("SecurityCenter/btn_wx_jieban").GetComponent<Button>();
            btn_SecurityC_changeBang = this.transform.Find("SecurityCenter/btn_changeBang").GetComponent<Button>();
            btn_SecurityC_close = this.transform.Find("SecurityCenter/btn_cloe").GetComponent<Button>();
            txt_NowPhoneID = this.transform.Find("SecurityCenter/Text").GetComponent<Text>();
            btn_CreatN_ok = this.transform.Find("CreatName/btn_ok").GetComponent<Button>();
            btn_CreatN_Close = this.transform.Find("CreatName/btn_cloe").GetComponent<Button>();
            Input_CreatN_nicheng = this.transform.Find("CreatName/nicheng/InputField").GetComponent<InputField>();
            Input_CreatN_setpassword = this.transform.Find("CreatName/setpassword/InputField").GetComponent<InputField>();
            Input_CreatN_confirm = this.transform.Find("CreatName/confirm/InputField").GetComponent<InputField>();
            Input_CreatN_Captcha = this.transform.Find("CreatName/Captcha/InputField").GetComponent<InputField>();
            Input_CreatN_CellPhone = this.transform.Find("CreatName/CellPhone/InputField").GetComponent<InputField>();
            btn_CreatN_Captcha = this.transform.Find("CreatName/btn_Captcha").GetComponent<Button>();
            txt_CreatN_Captcha = this.transform.Find("CreatName/yzmText").GetComponent<Text>();
            btn_ChangeP_ok = this.transform.Find("ChangePassword/btn_ok").GetComponent<Button>();
            btn_ChangeP_Close = this.transform.Find("ChangePassword/btn_cloe").GetComponent<Button>();
            Input_ChangeP_setpassword = this.transform.Find("ChangePassword/setpassword/InputField").GetComponent<InputField>();
            Input_ChangeP_confirm = this.transform.Find("ChangePassword/confirm/InputField").GetComponent<InputField>();
            Input_ChangeP_CellPhone = this.transform.Find("ChangePassword/CellPhone/InputField").GetComponent<InputField>();
            Input_ChangeP_Captcha = this.transform.Find("ChangePassword/Captcha/InputField").GetComponent<InputField>();
            btn_ChangeP_Captcha = this.transform.Find("ChangePassword/btn_Captcha").GetComponent<Button>();
            txt_ChangeP_Captcha = this.transform.Find("ChangePassword/Text").GetComponent<Text>();
            btn_Changeb_ok = this.transform.Find("Changebinding/btn_ok").GetComponent<Button>();
            btn_Changeb_Close = this.transform.Find("Changebinding/btn_cloe").GetComponent<Button>();
            Input_rightChangeb_realName = this.transform.Find("Changebinding/right/realName/InputField").GetComponent<InputField>();
            Input_leftChangeb_IDNumber = this.transform.Find("Changebinding/left/IDNumber/InputField").GetComponent<InputField>();
            Input_rightChangeb_IDNumber = this.transform.Find("Changebinding/right/IDNumber/InputField").GetComponent<InputField>();
            Input_leftChangeb_CellPhone = this.transform.Find("Changebinding/left/CellPhone/InputField").GetComponent<InputField>();
            Input_rightChangeb_CellPhone = this.transform.Find("Changebinding/right/CellPhone/InputField").GetComponent<InputField>();
            Input_leftChangeb_Captcha = this.transform.Find("Changebinding/left/Captcha/InputField").GetComponent<InputField>();
            Input_rightChangeb_Captcha = this.transform.Find("Changebinding/right/Captcha/InputField").GetComponent<InputField>();
            btn_leftChangeb_Captcha = this.transform.Find("Changebinding/left/btn_Captcha").GetComponent<Button>();
            btn_rightChangeb_Captcha = this.transform.Find("Changebinding/right/btn_Captcha").GetComponent<Button>();
            txt_leftChangeb_Captcha = this.transform.Find("Changebinding/left/Text").GetComponent<Text>();
            txt_rightChangeb_Captcha = this.transform.Find("Changebinding/right/Text").GetComponent<Text>();
            btn_Wxjiebang_ok = this.transform.Find("WxJieBang/btn_ok").GetComponent<Button>();
            btn_Wxjiebang_cancel = this.transform.Find("WxJieBang/btn_cancel").GetComponent<Button>();
            btn_Wxjiebang_Close = this.transform.Find("WxJieBang/btn_cloe").GetComponent<Button>();
        }
        public void Awake()
        {
            FindCompent();
            UEventDispatcher.Instance.AddEventListener(UEventName.ChangePhoneResponse, On_ChangePhoneResponse);//换绑发送验证码
            UEventDispatcher.Instance.AddEventListener(UEventName.BangIdCardAllResponse, On_BangIdCardAllResponse);//绑定身份信息响应
            UEventDispatcher.Instance.AddEventListener(UEventName.UpdateIdCardNoResponse, On_UpdateIdCardNoResponse);//换绑个人信息响应     
            btn_CreatN_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIAccount);
            });
            btn_BangD_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIAccount);
            });
            btn_ChangeP_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIAccount);
            });
            btn_Changeb_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIAccount);
            });
            btn_SecurityC_close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIAccount);
            });
            btn_SecurityC_wxjieban.onClick.AddListener(() =>
            {

                SetPanel(UIAccountPanel.WeiXinJieBang);
            // NetMessage.Login.Req_DeleteWxNoRequest();
        });
            btn_CreatN_ok.onClick.AddListener(() =>
            {
                if (Input_CreatN_nicheng.text == "")
                {
                    MessageBox.Show("昵称不能为空");
                    return;
                }
                if (Input_CreatN_setpassword.text == "")
                {
                    MessageBox.Show("密码不能为空");
                    return;
                }
                if (Input_CreatN_setpassword.text != Input_CreatN_confirm.text)
                {
                    MessageBox.Show("两次密码不一致");
                    return;
                }
                UserInfo.tmpPlayer.Account = Input_CreatN_nicheng.text;
                UserInfo.tmpPlayer.PassWorld = Input_CreatN_setpassword.text;
                string pwd = common.MD5Encrypt(Input_CreatN_setpassword.text);
                NetMessage.Login.Req_BangNiceNameRequest(Input_CreatN_nicheng.text, pwd, Input_CreatN_CellPhone.text, Input_CreatN_Captcha.text, PlayerData.UserName);
            //btn_CreatN_ok.enabled = false;
        });


            btn_ChangeP_Captcha.onClick.AddListener(() =>
            {
                if (Input_ChangeP_CellPhone.text == "")
                {
                    MessageBox.Show("手机号不能为空");
                }
                btn_ChangeP_Captcha.interactable = false;
                StartCoroutine(tmp(btn_ChangeP_Captcha, txt_ChangeP_Captcha, 60));
                NetMessage.Agent.Req_AccountPhoneCheckRequest(Input_ChangeP_CellPhone.text.Trim());
            });
            btn_BangD_Captcha.onClick.AddListener(() =>
            {
                if (Input_BangD_CellPhone.text == "")
                {
                    MessageBox.Show("手机号不能为空");
                }
                btn_BangD_Captcha.interactable = false;
                StartCoroutine(tmp(btn_BangD_Captcha, txt_BangD_Captcha, 60));
                NetMessage.Agent.Req_AccountPhoneCheckRequest(Input_BangD_CellPhone.text.Trim());
            });
            btn_leftChangeb_Captcha.onClick.AddListener(() =>
            {
                if (Input_leftChangeb_CellPhone.text == "")
                {
                    MessageBox.Show("手机号不能为空");
                }
                btn_leftChangeb_Captcha.interactable = false;
                StartCoroutine(tmp(btn_leftChangeb_Captcha, txt_leftChangeb_Captcha, 60));
                NetMessage.Login.Req_ChangePhoneRequest(Input_leftChangeb_CellPhone.text.Trim());
            });
            btn_CreatN_Captcha.onClick.AddListener(() =>
            {
                if (Input_CreatN_CellPhone.text == "")
                {
                    MessageBox.Show("手机号不能为空");
                }
                btn_CreatN_Captcha.interactable = false;
                StartCoroutine(tmp(btn_CreatN_Captcha, txt_CreatN_Captcha, 60));
                NetMessage.Agent.Req_AccountPhoneCheckRequest(Input_CreatN_CellPhone.text.Trim());
            });
            btn_Wxjiebang_ok.onClick.AddListener(() =>
            {
                NetMessage.Login.Req_DeleteWxNoRequest();
            });
            btn_Wxjiebang_cancel.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIAccount);
            });
            btn_Wxjiebang_Close.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIAccount);
            });
            btn_rightChangeb_Captcha.onClick.AddListener(() =>
            {
                if (Input_rightChangeb_CellPhone.text == "")
                {
                    MessageBox.Show("手机号不能为空");
                }
                btn_rightChangeb_Captcha.interactable = false;
                StartCoroutine(tmp(btn_rightChangeb_Captcha, txt_rightChangeb_Captcha, 60));
                NetMessage.Agent.Req_AccountPhoneCheckRequest(Input_rightChangeb_CellPhone.text.Trim());
            });
            btn_ChangeP_ok.onClick.AddListener(() =>
            {
                if (Input_ChangeP_setpassword.text == "")
                {
                    MessageBox.Show("请输入要设置的密码");
                    return;
                }
                if (Input_ChangeP_confirm.text != Input_ChangeP_setpassword.text)
                {
                    MessageBox.Show("两次输入密码不一致");
                    return;
                }
                if (Input_ChangeP_CellPhone.text == "")
                {
                    MessageBox.Show("请输入手机号");
                    return;
                }
                if (Input_ChangeP_Captcha.text == "")
                {
                    MessageBox.Show("请输入验证码");
                    return;
                }
                string pwd = common.MD5Encrypt(Input_ChangeP_setpassword.text);
                NetMessage.Login.Req_UpdateLoginPasswordRequest(PlayerData.UserName, pwd, Input_ChangeP_CellPhone.text, Input_ChangeP_Captcha.text);
            });
            btn_Changeb_ok.onClick.AddListener(() =>
            {

                if (Input_rightChangeb_realName.text == "")
                {
                    MessageBox.Show("请输入要设置的真实姓名");
                    return;
                }
                if (Input_leftChangeb_IDNumber.text == "")
                {
                    MessageBox.Show("请输入之前绑定的身份证号");
                    return;
                }
                if (Input_rightChangeb_IDNumber.text == "")
                {
                    MessageBox.Show("请输入需要绑定的身份证号");
                    return;
                }
                if (Input_leftChangeb_CellPhone.text == "")
                {
                    MessageBox.Show("请输入之前绑定的手机号");
                    return;
                }
                if (Input_leftChangeb_Captcha.text == "")
                {
                    MessageBox.Show("请输入之前手机验证码");
                    return;
                }
                if (Input_rightChangeb_CellPhone.text == "")
                {
                    MessageBox.Show("请输入需要绑定的手机号");
                    return;
                }
                if (Input_rightChangeb_Captcha.text == "")
                {
                    MessageBox.Show("请输入需要绑定的手机验证码");
                    return;
                }
                NetMessage.Login.Req_UpdateIdCardNoRequest(PlayerData.UserName, Input_leftChangeb_IDNumber.text, Input_leftChangeb_CellPhone.text, Input_leftChangeb_Captcha.text,
                    Input_rightChangeb_CellPhone.text, Input_rightChangeb_IDNumber.text, Input_rightChangeb_realName.text, Input_rightChangeb_Captcha.text);
            });
            btn_SecurityC_changelogPwd.onClick.AddListener(() =>
            {
                SetPanel(UIAccountPanel.ChangePassword);
            });
            btn_SecurityC_changeBang.onClick.AddListener(() =>
            {
                SetPanel(UIAccountPanel.Changebinding);
            });
            btn_BangD_ok.onClick.AddListener(() =>
            {
                NetMessage.Login.Req_BangIdCardAllRequest(Input_BangD_RealName.text, Input_BangD_IDNumber.text, Input_BangD_CellPhone.text, Input_BangD_Captcha.text);
            });
        }
        void OnEnable()
        {
            UEventDispatcher.Instance.AddEventListener(UEventName.BangNiceNameResponse, On_BangNiceNameResponse);//绑定昵称响应

                                                                                                                             //btn_CreatN_ok.enabled = true;

        }
        void On_UpdateSendPasswordResponse(UEventContext obj)
        {
            var pack = obj.GetData<BangIdCardAllResponse>();
        }
        private void OnDisable()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BangNiceNameResponse, On_BangNiceNameResponse);//绑定昵称响应
        }
        /// <summary>
        /// 绑定身份信息响应
        /// <summary>
        private void On_BangIdCardAllResponse(UEventContext obj)
        {
            var pack = obj.GetData<BangIdCardAllResponse>();
            SetPanel(UIAccountPanel.SecurityCenter);
        }
        /// <summary>
        /// 绑定昵称响应
        /// <summary>
        private void On_BangNiceNameResponse(UEventContext obj)
        {
            ListPanel[(int)UIAccountPanel.BangDingNackName].gameObject.SetActive(true);
            UIMgr.CloseUI(UIPath.UIAccount);
            var pack = obj.GetData<BangNiceNameResponse>();
            if (common.phoneNum != null)
            {
                PlayerData.PhoneNum = common.phoneNum;
            }
            //NetMessage.Login.Req_CommonLoginRequest(pack.username, pack.password);
        }

        private void On_UpdateIdCardNoResponse(UEventContext obj)
        {
            var pack = obj.GetData<UpdateIdCardNoResponse>();
        }
        /// <summary>
        /// 换绑发送验证码
        /// <summary>
        private void On_ChangePhoneResponse(UEventContext obj)
        {
            var pack = obj.GetData<ChangePhoneResponse>();
        }

        IEnumerator tmp(Button btn_tmp, Text waitTime, int time)
        {
            while (time > 0)
            {
                time--;
                yield return new WaitForSeconds(1f);
                waitTime.text = time.ToString() + "秒";
            }
            btn_tmp.interactable = true;
            waitTime.text = "";
        }
    }
    public enum UIAccountPanel
    {
        ChangePassword = 0,
        Changebinding = 2,
        SecurityCenter = 3,
        BangDingIDNumber = 4,
        BangDingNackName = 5,
        WeiXinJieBang = 6,
    }
}