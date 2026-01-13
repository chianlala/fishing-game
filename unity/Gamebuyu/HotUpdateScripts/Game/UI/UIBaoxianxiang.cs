using com.maple.game.osee.proto.lobby;
using CoreGame;
using GameFramework;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIBaoxianxiang : MonoBehaviour
    {
        public static UIBaoxianxiang instance;

        public Button btn_close;
        public Toggle tog_qianzhuang;
        public GameObject view_qianzhuang, view_changePwd;

        //-------------view_qianzhuang---------------
        public Text txt_gold, txt_bank;
        public InputField input_gold, input_pwd;
        public Button btn_in, btn_out;
        //public GameObject obj_Right, obj_Wrong;//密码对错

        //-------------view_changePwd----------------
        public InputField input_oldPwd, input_newPwd, input_okPwd;
        public Button btn_queren;

        private void Awake()
        {
            instance = this;
            FindCompent();
            UEventDispatcher.Instance.AddEventListener(UEventName.CheckBankPasswordResponse, On_CheckBankPasswordResponse);//检查保险箱密码返回
            UEventDispatcher.Instance.AddEventListener(UEventName.SaveMoneyResponse, On_SaveMoneyResponse);//存取金币返回
            UEventDispatcher.Instance.AddEventListener(UEventName.ChangeBankPasswordResponse, On_ChangeBankPasswordResponse);//修改保险箱密码返回

            btn_close.onClick.AddListener(() => {
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
                UIMgr.CloseUI(UIPath.UIBaoxianxiang); 
            });
            tog_qianzhuang.onValueChanged.AddListener((isOn) =>
            {
                view_qianzhuang.SetActive(isOn);
                view_changePwd.SetActive(!isOn);
            });
            btn_in.onClick.AddListener(() =>
            {
                //if (input_pwd.text.Trim().Length > 0)
                //{
                //    string strMd5 = common.MD5Encrypt(input_pwd.text);
                    NetMessage.OseeLobby.Req_SaveMoneyRequest("", long.Parse(input_gold.text));
                //}
            });
            btn_out.onClick.AddListener(() =>
            {
                if (input_pwd.text.Trim().Length > 0)
                {
                    string strMd5 = common.MD5Encrypt(input_pwd.text);
                    NetMessage.OseeLobby.Req_SaveMoneyRequest(strMd5, -long.Parse(input_gold.text));
                }
                else
                {
                    MessageBox.Show("你取钱不输入密码?");
                }
            });
            input_pwd.onEndEdit.AddListener((s) =>
            {
                if (s.Trim().Length > 0)
                {
                    string strMd5 = common.MD5Encrypt(s.Trim());
                    NetMessage.OseeLobby.Req_CheckBankPasswordRequest(strMd5);
                }
            });
            btn_queren.onClick.AddListener(() =>
            {
                if (input_oldPwd.text.Trim().Length == 0 || input_newPwd.text.Trim().Length == 0 || input_okPwd.text.Trim().Length == 0)
                {
                    MessageBox.Show("请正确填写");
                }
                else if (input_newPwd.text.Trim() != input_okPwd.text.Trim())
                {
                    MessageBox.Show("两次新密码输入不一样");
                }
                else
                {
                    NetMessage.OseeLobby.Req_ChangeBankPasswordRequest(common.MD5Encrypt(input_oldPwd.text.Trim()), common.MD5Encrypt(input_newPwd.text.Trim()));
                }
            });
        }
        void FindCompent()
        {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            tog_qianzhuang = this.transform.Find("bg/tog_qianzhuang").GetComponent<Toggle>();
            view_qianzhuang = this.transform.Find("bg/view_qianzhuang").gameObject;
            view_changePwd = this.transform.Find("bg/view_changePwd").gameObject;
            txt_gold = this.transform.Find("bg/view_qianzhuang/bg_gold/bg/txt_gold").GetComponent<Text>();
            txt_bank = this.transform.Find("bg/view_qianzhuang/bg_bank/bg/txt_bank").GetComponent<Text>();
            input_gold = this.transform.Find("bg/view_qianzhuang/bg_input/input_gold").GetComponent<InputField>();
            input_pwd = this.transform.Find("bg/view_qianzhuang/bg_pwd/input_pwd").GetComponent<InputField>();
            btn_in = this.transform.Find("bg/view_qianzhuang/btn_in").GetComponent<Button>();
            btn_out = this.transform.Find("bg/view_qianzhuang/btn_out").GetComponent<Button>();
            //obj_Right = this.transform.Find("bg/view_qianzhuang/bg_pwd/gou").gameObject;
            //obj_Wrong = this.transform.Find("bg/view_qianzhuang/bg_pwd/x").gameObject;
            input_oldPwd = this.transform.Find("bg/view_changePwd/bg_inputOld/input_oldPwd").GetComponent<InputField>();
            input_newPwd = this.transform.Find("bg/view_changePwd/bg_inputNew/input_newPwd").GetComponent<InputField>();
            input_okPwd = this.transform.Find("bg/view_changePwd/bg_inputOk/input_okPwd").GetComponent<InputField>();
            btn_queren = this.transform.Find("bg/view_changePwd/btn_querenxiugai").GetComponent<Button>();
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.CheckBankPasswordResponse, On_CheckBankPasswordResponse);//检查保险箱密码返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.SaveMoneyResponse, On_SaveMoneyResponse);//存取金币返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ChangeBankPasswordResponse, On_ChangeBankPasswordResponse);//修改保险箱密码返回
        }

        private void OnEnable()
        {
            txt_gold.text = PlayerData.DragonCrystal.ToString();
            txt_bank.text = PlayerData.BankGold.ToString();
            InitData();

            EventManager.BankLjUpdate += ChangeUpdate;
            EventManager.DragonCrystalUpdate += DragonCrystal;
        }
        void ChangeUpdate(long bank) {
            txt_bank.text = bank.ToString();
        }
        void DragonCrystal(long dragon)
        {
            txt_gold.text = dragon.ToString();
        } 
        void OnDisable() {
            EventManager.BankLjUpdate -= ChangeUpdate;
            EventManager.DragonCrystalUpdate -= DragonCrystal;
            
        }
        private void InitData()
        {
            input_gold.text = "";
            input_pwd.text = "";
            input_oldPwd.text = "";
            input_newPwd.text = "";
            input_okPwd.text = "";
            //obj_Right.SetActive(false);
            //obj_Wrong.SetActive(false);

            //NetMessage.OseeLobby.Req_PlayerMoneyRequest(0);
        }

        /// <summary>
        /// 检查保险箱密码返回
        /// <summary>
        private void On_CheckBankPasswordResponse(UEventContext obj)
        {
            var pack = obj.GetData<CheckBankPasswordResponse>();
            ////if (pack.success)
            ////{
            ////    obj_Right.SetActive(true);
            ////    obj_Wrong.SetActive(false);
            ////}
            ////else
            ////{
            ////    obj_Right.SetActive(false);
            ////    obj_Wrong.SetActive(true);
            ////}
        }
        /// <summary>
        /// 存取金币返回
        /// <summary>
        private void On_SaveMoneyResponse(UEventContext obj)
        {
            var pack = obj.GetData<SaveMoneyResponse>();
            Debug.Log(pack.success);
            string strShow = pack.success ? "成功" : "失败";
            MessageBox.ShowPopMessage("存取金币" + strShow);
            InitData();
        }
        /// <summary>
        /// 修改保险箱密码返回
        /// <summary>
        private void On_ChangeBankPasswordResponse(UEventContext obj)
        {
            var pack = obj.GetData<ChangeBankPasswordResponse>();
            string strShow = pack.success ? "成功" : "失败";
            MessageBox.Show("修改保险箱密码" + strShow);
            InitData();
        }
    }
}