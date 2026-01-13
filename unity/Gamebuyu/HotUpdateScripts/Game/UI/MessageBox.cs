using UnityEngine;
using System.Collections;
using System;
using CoreGame;

namespace Game.UI
{
    /// <summary>
    /// 显示可包含文本、按钮和符号（通知并指示用户）的消息框。
    /// </summary>
    public abstract class MessageBox
    {



        //public static GameObject ShowNotCick(string text, string title = null, Action okHandler = null, Action closeHandler = null, float fLiveTime = 0)
        //{
        //    //GameObject go = UIMgr.ShowUI(UIPath.MessageBox);
        //    UIMessageNoCilck msgBox = UIMgr.ShowUI(UIPath.MessageBox).GetComponent<UIMessageNoCilck>();
        //    msgBox.Text = text;

        //    if (!string.IsNullOrEmpty(title))
        //        msgBox.Title = title;

        //    if (fLiveTime > 0)
        //    {
        //        msgBox.fLiveTime = fLiveTime;
        //    }
        //    if (okHandler == null)
        //    {
        //        msgBox.SetOkHandler(null);
        //    }
        //    if (closeHandler == null)
        //    {
        //        msgBox.SetOkHandler(null);
        //    }
        //    if (okHandler != null)
        //        msgBox.SetOkHandler(okHandler);
        //    if (closeHandler != null)
        //        msgBox.SetCloseHandler(closeHandler);

        //    return msgBox.gameObject;

        //}

        public static GameObject Show(string text, string title = null, Action okHandler = null, Action closeHandler = null, float fLiveTime = 0)
        {
            //GameObject go = UIMgr.ShowUI(UIPath.MessageBox);
            UIMessageBox msgBox = UIMgr.ShowUISynchronize(UIPath.UIMessageBox).GetComponent<UIMessageBox>();
            if (msgBox.Text==null)
            {
                Debug.Log("msgBox.Text");
            }
            msgBox.Text = text;

            if (!string.IsNullOrEmpty(title))
                msgBox.Title = title;
            else
                msgBox.Title = "提示";
            if (fLiveTime > 0)
            {
                msgBox.fLiveTime = fLiveTime;
            }
            if (okHandler == null)
            {
                msgBox.SetOkHandler(null);
            }
            if (closeHandler == null)
            {
                msgBox.SetOkHandler(null);
            }
            if (okHandler != null)
                msgBox.SetOkHandler(okHandler);
            if (closeHandler != null)
                msgBox.SetCloseHandler(closeHandler);

            return msgBox.gameObject;

        }
        public static GameObject ShowNet(string text, string title = null, Action okHandler = null, Action closeHandler = null, float fLiveTime = 0)
        {
            //GameObject go = UIMgr.ShowUI(UIPath.MessageBox);
            UIMessageNetBox msgBox = UIMgr.ShowUISynchronize(UIPath.UIMessageNetBox).GetComponent<UIMessageNetBox>();
            if (msgBox.Text == null)
            {
                Debug.Log("msgBox.Text");
            }
            msgBox.Text = text;

            if (!string.IsNullOrEmpty(title))
                msgBox.Title = title;
            else
                msgBox.Title = "提示";
            if (fLiveTime > 0)
            {
                msgBox.fLiveTime = fLiveTime;
            }
            if (okHandler == null)
            {
                msgBox.SetOkHandler(null);
            }
            if (closeHandler == null)
            {
                msgBox.SetOkHandler(null);
            }
            if (okHandler != null)
                msgBox.SetOkHandler(okHandler);
            if (closeHandler != null)
                msgBox.SetCloseHandler(closeHandler);
            return msgBox.gameObject;
        }
        public static void ShowPay(string text, string title = null, Action okHandler = null, Action cancelHandler = null, Action closeHandler = null)
        {
            //UIMessageBoxEx obj = UIMgr.ShowUI(UIPath.MessageBoxEx);
            //UIMessageBoxPay msgbox = UIMgr.ShowUI(UIPath.UIMessageBoxPay).GetComponent<UIMessageBoxPay>();
            //msgbox.Text = text;

            //if (!string.IsNullOrEmpty(title))
            //    msgbox.Title = title;

            //if (okHandler != null)
            //    msgbox.SetOkHandler(okHandler);
            //if (cancelHandler != null)
            //    msgbox.SetCancelHandler(cancelHandler);
            //if (closeHandler != null)
            //    msgbox.SetCloseHandler(closeHandler);
        }

        public static void ShowConfirm(string text, string title = null, Action okHandler = null, Action cancelHandler = null, Action closeHandler = null)
        {
            //UIMessageBoxEx obj = UIMgr.ShowUI(UIPath.MessageBoxEx);
            UIMessageBoxEx msgbox = UIMgr.ShowUISynchronize(UIPath.UIMessageBoxEx).GetComponent<UIMessageBoxEx>();
            msgbox.Text = text;

            if (!string.IsNullOrEmpty(title))
                msgbox.Title = title;

            if (okHandler != null)
                msgbox.SetOkHandler(okHandler);
            if (cancelHandler != null)
                msgbox.SetCancelHandler(cancelHandler);
            if (closeHandler != null)
                msgbox.SetCloseHandler(closeHandler);

            if (okHandler == null)
            {
                msgbox.SetOkHandler(null);
            }
            if (cancelHandler == null)
            {
                msgbox.SetCancelHandler(null);
            }
        }
        public static void ShowPopMessage(string text)
        {
            //GameObject obj = UIMgr.ShowUI(UIPath.PopMessage);
            UIPopMessage popMsg = UIMgr.ShowUISynchronize(UIPath.UIPopMessage).GetComponent<UIPopMessage>();
            popMsg.ShowMessage(text);
        } 
        public static void ShowPopOneMessage(string text)
        {
            //GameObject obj = UIMgr.ShowUI(UIPath.PopMessage);
            UIPopOneMsg popMsg = UIMgr.ShowUISynchronize(UIPath.UIPopOneMsg).GetComponent<UIPopOneMsg>();
            popMsg.ShowMessage(text);
        }
    }

}