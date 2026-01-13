using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;
using System;
using CoreGame;

namespace Game.UI
{
    public static class UnityToMessage
    {
        static DateTime lifet;
        static bool IsPause;
        public static void UnityApplicationPause(bool pause)
        {
            Debug.Log("OnApplicationPause " + pause);
            //切入切出
            //lifet = DateTime.Now;
            //IsPause = true;
            if (pause)//切换回来
            {
                IsPause = true;
                lifet = DateTime.Now;
            }
            else
            {
                //游戏正常运行
                //如果UIlogin还在 重连
                commonunity._msgQueue.Clear();
                //if (common.IsLoginState == false)//代表还未登录
                //{
                //    //登录界面注销
                //    NetMgr.Instance.OnZhuxiao();
                //    return;
                //}
                if (IsPause == false)//是否切出去过
                {
                    //UIMgr.CloseUI(UIPath.UIByChange);
                    //common3._UIFishingInterface = null;
                    return;
                }
                IsPause = false;
                TimeSpan timenow = DateTime.Now - lifet;
                
                if (NetMgr.Instance.Socket == null)
                {
                    //在登录界面切有可能为null
                    common3.CloseLoginZhuXiao();
                    return;
                }

                if (timenow.TotalSeconds > 1800f)//大于1800秒直接提示
                {
                    MessageBox.ShowNet("您与服务器断开连接，请重新登录","",()=> {
                        UIMgr.CloseAll();
                        UIMgr.ShowUI(UIPath.UILogin); });
                    return;
                }
                if (timenow.TotalSeconds > 120f)//大于120秒直接重连
                {
                    common.nLoinWay = 3;
                    NetMgr.Instance.OnSpecialLogin();
                    return;
                }
          
                if (common3._UIFishingInterface != null)
                {
                    common3._UIFishingInterface.OpenReConnecting(true);
                }
                if (common.IsAlreadyLogin)
                {
                    //如果已经登录过 则再次登录重连
                    if (common.OpenUsername != "")
                    {
                        //这时如果收不到回包消息  则会卡死
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
        }
    }
}
