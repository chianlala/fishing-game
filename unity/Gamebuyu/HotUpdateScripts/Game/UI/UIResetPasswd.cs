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

namespace Game.UI 
{
    public class UIResetPasswd : MonoBehaviour 
    {
        private static bool IsFirst = true;
        //重置密码
        public InputField resetp_Name;
        public InputField resetp_OldPassword;
        public InputField resetp_Password; 
        public InputField resetp_confirm;
        public Button btn_ok; 
        public Button btn_close;
        public  void Awake()
        {
            if (IsFirst)
            {
                IsFirst = false;
                FindCompent();

                btn_ok.onClick.AddListener(() =>
                {
                    //if (resetp_OldPassword.text == "")
                    //{
                    //    MessageBox.Show("密码不能为空", null, null, null);
                    //    return;
                    //}
                    //if (resetp_OldPassword.text=="")
                    //{
                    //    MessageBox.Show("旧密码不能为空", null, null, null);
                    //    return;
                    //}
                    //if (resetp_Password.text.Length < 6)
                    //{
                    //    MessageBox.Show("密码至少需要6位", null, null, null);
                    //    return;
                    //}
                    //if (resetp_confirm.text.Length<6)
                    //{
                    //    MessageBox.Show("密码至少需要6位", null, null, null);
                    //    return;
                    //}
                    //if (resetp_confirm.text!= resetp_Password.text)
                    //{
                    //    MessageBox.Show("两次密码输入不一致", null, null, null);
                    //    return;
                    //}
                    //string strOldPwdMd5 = common.MD5Encrypt(resetp_OldPassword.text.Trim());
                    //string strPwdMd5 = common.MD5Encrypt(resetp_Password.text.Trim());
                    //string strConfirmPwdMd5 = common.MD5Encrypt(resetp_Password.text.Trim());
                    //NetMessage.OseeLobby.Req_ResetPasswordRequest(resetp_Name.text, strOldPwdMd5, strPwdMd5);
                });
                btn_close.onClick.AddListener(() =>
                {
                    UIMgr.CloseUI(UIPath.UIResetPasswd);
                });
            }
        }
        void FindCompent() {
            btn_ok = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            resetp_Name = this.transform.Find("bg/resetp_Name/input_acc").GetComponent<InputField>();
            resetp_OldPassword = this.transform.Find("bg/resetp_OldPassword/input_pwd").GetComponent<InputField>();
            resetp_Password = this.transform.Find("bg/resetp_Password/input_pwd").GetComponent<InputField>();
            resetp_confirm = this.transform.Find("bg/resetp_confirm/input_pwd").GetComponent<InputField>();
        }

        /// <summary>
        /// 获取重置密码手机号返回
        /// <summary>
        private void On_ResetPasswordResponse(UEventContext obj)
        {
            var pack = obj.GetData<ResetPasswordResponse>();
            if (pack.result)
            {
                UIMgr.CloseUI(UIPath.UIResetPasswd);
            }
            else
            {

            }
        }
        void OnEnable() {
            UEventDispatcher.Instance.AddEventListener(UEventName.ResetPasswordResponse, On_ResetPasswordResponse);//获取重置密码手机号返回                                                                                                  //SoundMgr.Instance.loginBgMusic();
        }

        void OnDisable() {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ResetPasswordResponse, On_ResetPasswordResponse);//获取重置密码手机号返回
        }
       
       
    }
}