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
using com.maple.game.osee.proto.lobby;
using System.Collections;
using com.maple.common.lobby.proto;
using com.maple.game.osee.proto.agent;
using System.Collections.Generic;

namespace Game.UI
{
    public class UIMainMenu : MonoBehaviour
    {
        private static bool IsFirst = true;
        ////消息红点提示 
        //public Transform XiaoXiredPoint;
        //public Transform root_middle; 
        //public Transform jiangchi;
        //public Animation aniCamera;
        //public Animation aniChuan01;
        //public Animation aniChuan01di;
        //public Button btn_bangding;
        //public Button BtnBuyuMain;
        //public Button BtnBigAward; 
        //AniOverOneTA aniOverCamera; 
        //AniOverOneTA aniOverChuan; 
        void FindCompent()
        {
            //root_middle = this.transform.Find("bg/view_main/root_middle");
            //jiangchi = this.transform.Find("jiangchi");
            //BtnBuyuMain = this.transform.Find("bg/view_main/root_middle/BtnBuyu").GetComponent<Button>();
            //BtnBigAward = this.transform.Find("bg/view_main/root_middle/BtnBigAward").GetComponent<Button>();

            //aniCamera = this.transform.Find("Scenes_dating2/Camera").GetComponent<Animation>();
            //aniChuan01 = this.transform.Find("Scenes_dating2/Scenes_dating/Scenes_dating_chuan01").GetComponent<Animation>();
            //aniChuan01di = aniChuan01.transform.GetChild(0).GetComponent<Animation>();
            //aniOverCamera = aniCamera.GetComponent<AniOverOneTA>();
            //aniOverChuan = aniChuan01.GetComponent<AniOverOneTA>();

            //XiaoXiredPoint = transform.Find("bg/view_main/root_topRight/btn_xiaoxi/Image");
            //btn_bangding = transform.Find("bg/view_main/btn_bangding").GetComponent<Button>();
        }
        public void Awake()
        {
            if (IsFirst)
            {
                IsFirst = false;
                FindCompent();
                //aniOverChuan.aAction += AniAction;
                //aniOverCamera.aAction += AniActionCamera;
                //BtnBuyuMain.onClick.AddListener(() =>
                //{
                //    root_middle.gameObject.SetActive(false);
                //    jiangchi.gameObject.SetActive(false);
                //    //视角
                //    aniCamera.Play("kaichuan_Animation");
                //});
                //BtnBigAward.onClick.AddListener(() =>
                //{
                //    UIMgr.ShowUI(UIPath.UIBigAwardHall);
                //});
                UEventDispatcher.Instance.AddEventListener(UEventName.PlayerMoneyResponse, On_PlayerMoneyResponse);//获取玩家货币返回
                UEventDispatcher.Instance.AddEventListener(UEventName.WanderSubtitleResponse, On_WanderSubtitleResponse);//系统游走字幕返回
                UEventDispatcher.Instance.AddEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取玩家vip等级返回                
                UEventDispatcher.Instance.AddEventListener(UEventName.SignedTimesResponseMenu, On_SignedTimesResponse);//获取已签到次数返回 
                UEventDispatcher.Instance.AddEventListener(UEventName.GetUserInfoResponse, On_GetUserInfoResponse);//获取用户详情返回
                UEventDispatcher.Instance.AddEventListener(UEventName.AuthenticateInfoResponse, On_AuthenticateInfoResponse);//实名认证信息返回
                UEventDispatcher.Instance.AddEventListener(UEventName.WechatShareResponse, On_WechatShareResponse);//微信分享返回
                UEventDispatcher.Instance.AddEventListener(UEventName.FunctionStateResponse, On_FunctionStateResponse);//功能启用状态响应
                UEventDispatcher.Instance.AddEventListener(UEventName.FirstChargeRewardsResponse, On_FirstChargeRewardsResponse);//首充奖励响应
                UEventDispatcher.Instance.AddEventListener(UEventName.BuyMonthCardRewardsResponse, On_BuyMonthCardRewardsResponse);//购买月卡奖励
            }

        }
        //void AniActionCamera()
        //{
        //    //船
        //    aniChuan01.Play("Scenes_dating_chuan01_Ani");
        //    aniChuan01di.Play("move");
        //}
        //void AniAction() {
        //    UIMgr.ShowUI(UIPath.UIBuyuMenu);
        //    UIMgr.CloseUI(UIPath.UIMainMenu);
        //}
    
        
        void AniRest()
        {
            //aniCamera.Play("resetchuan_Animation");
            //aniChuan01.Play("Scenes_dating_chuan01_Ani_reset");
            //aniChuan01di.Play("idle");
            //root_middle.gameObject.SetActive(true);
            //jiangchi.gameObject.SetActive(true);
        }
        void On_XiaoNumUpdate() {
            //if (common.redpoint > 0)
            //{
            //    XiaoXiredPoint.gameObject.SetActive(true);
            //}
            //else
            //{
            //    XiaoXiredPoint.gameObject.SetActive(false);
            //}
        }

        void On_ChangePao(int mess)
        {
            SetChange();
        }
        //改变明暗和锁
        public void SetChange()
        {
            Debug.Log("炮等级：" + PlayerData.PaoLevel);
        
        }
        private void On_PlayerBindStateResponse(UEventContext obj)
        {
            var pack = obj.GetData<PlayerBindStateResponse>();
            //if (pack.state)
            //{
            //    btn_bangding.gameObject.SetActive(false);
            //}
            //else
            //{
            //    btn_bangding.gameObject.SetActive(true);
            //}
        }
        /// <summary>
        /// 购买月卡奖励
        /// <summary>
        private void On_BuyMonthCardRewardsResponse(UEventContext obj)
        {
            var pack = obj.GetData<BuyMonthCardRewardsResponse>();
        }
        /// <summary>
        /// 首充奖励响应
        /// <summary>
        private void On_FirstChargeRewardsResponse(UEventContext obj)
        {
            var pack = obj.GetData<FirstChargeRewardsResponse>();
        }
        /// <summary>
        /// 实名认证信息返回
        /// <summary>
        private void On_AuthenticateInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<AuthenticateInfoResponse>();
        }
        /// <summary>
        /// 微信分享返回
        /// <summary>
        private void On_WechatShareResponse(UEventContext obj)
        {
            var pack = obj.GetData<WechatShareResponse>();
            MessageBox.ShowPopMessage("微信分享成功,获得金币" + pack.rewardMoney);
        }
        /// <summary>
        /// 获取玩家货币返回
        /// <summary>
        private void On_PlayerMoneyResponse(UEventContext obj)
        {
            var pack = obj.GetData<PlayerMoneyResponse>();
            Debug.Log("获取玩家货币返回");
            PlayerData.Gold = pack.money;
            PlayerData.Diamond = pack.diamond;
            PlayerData.DragonCrystal = pack.dragonCrystal;
            PlayerData.BankGold = pack.bankMoney;
        }
        private void Start()
        {
        }
        void OnEnable()
        {
            //炮等级事件
            EventManager.PaoLevelUpdate += On_ChangePao;

            //玩家道具
            NetMessage.OseeFishing.Req_PlayerPropRequest();
            //用户信息
            NetMessage.Lobby.Req_GetUserInfoRequest();
            //炮台等级
            NetMessage.OseeFishing.Req_PlayerBatteryLevelRequest();
            //VIP等级
            NetMessage.OseeLobby.Req_VipLevelRequest();
        
            //更换音乐
            SoundLoadPlay.ChangeBgMusic("LobbyBG");
            //状态
            NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
            //请求未读消息数量
            NetMessage.OseeFishing.Req_UnreadMessageCountRequest();
       
            AniRest();
            //UIMgr.CloseUI(UIPath.UIByChange);
            //UIMgr.CloseUI(UIPath.UIByGrandPrix);
            //UIMgr.CloseUI(UIPath.UIBuyuMenu);

            NetMessage.Agent.Req_PlayerBindStateRequest();


            //UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            //Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            //for (int i = 1; i < 4; i++)
            //{
            //    Dictmp.Add(i, 10);
            //}
            //tmp.InitItem(Dictmp, -1, true);
        }
        void OnDisable()
        {
            EventManager.PaoLevelUpdate -= On_ChangePao;
        }
        void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerMoneyResponse, On_PlayerMoneyResponse);//获取玩家货币返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.WanderSubtitleResponse, On_WanderSubtitleResponse);//系统游走字幕返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取玩家vip等级返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerMoneyResponse, On_PlayerMoneyResponse);//获取玩家货币返回  
            UEventDispatcher.Instance.RemoveEventListener(UEventName.SignedTimesResponseMenu, On_SignedTimesResponse);//获取已签到次数返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetUserInfoResponse, On_GetUserInfoResponse);//获取用户详情返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.AuthenticateInfoResponse, On_AuthenticateInfoResponse);//实名认证信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.WechatShareResponse, On_WechatShareResponse);//微信分享返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FunctionStateResponse, On_FunctionStateResponse);//功能启用状态响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FirstChargeRewardsResponse, On_FirstChargeRewardsResponse);//首充奖励响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.BuyMonthCardRewardsResponse, On_BuyMonthCardRewardsResponse);//购买月卡奖励  
        }
        //#######方法 
        /// <summary>
        /// 系统游走字幕返回
        /// <summary>
        private void On_WanderSubtitleResponse(UEventContext obj)
        {
            var pack = obj.GetData<WanderSubtitleResponse>();
            //if (UILogin.instance.gameObject.activeSelf == false)
            //{
            //    UIGuangbo view = UIMgr.ShowUI(UIPath.UIGuangbo) as UIGuangbo;
            //    view.Init(pack.content, pack.level);
            //}
        }
        /// <summary>
        /// 获取玩家vip等级返回
        /// <summary>
        private void On_VipLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<VipLevelResponse>();
        }
        /// <summary>
        /// 获取用户详情返回
        /// <summary>
        private void On_GetUserInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetUserInfoResponse>();
            Debug.Log("pack.headUrl"+ pack.headUrl);
            PlayerData.StrHeadUrl = pack.headUrl;
            PlayerData.NickName = pack.nickname;
            PlayerData.HeadIndex = pack.headIndex;
            PlayerData.UserName = pack.username;
            PlayerData.PhoneNum = pack.phoneNum;

        }
        /// <summary>
        /// 获取已签到次数返回
        /// <summary>
        private void On_SignedTimesResponse(UEventContext obj)
        {
            bool bSign = obj.GetData<bool>();
            //if (!bSign)
            //{
            //    //UIQuest ui = UIMgr.ShowUI(UIPath.UIQuest) as UIQuest;
            //    //ui.Setpanel(0);
            //    //ui.tog_quest.isOn = false;
            //    qiandaoPoint.gameObject.SetActive(true);
            //}
            //else
            //{
            //    qiandaoPoint.gameObject.SetActive(false);
            //}
        }
        void Update()
        {
            if (NetMgr.Instance.nLoginState == 2)
            {
                PlayerData.CanRechiveXiaoXi += Time.deltaTime;
                if (PlayerData.CanRechiveXiaoXi > 6f)
                {
                    PlayerData.CanRechiveXiaoXi = 0f;
                    if (common.IsOpenUILogin)
                    {
                    }
                    else
                    {
                        common.nLoinWay = 3;
                        NetMgr.Instance.OnSpecialLogin();
                    }
                }
            }
        }
        /// <summary>
        /// 功能启用状态响应
        /// <summary>
        private void On_FunctionStateResponse(UEventContext obj)
        {
            var pack = obj.GetData<FunctionStateResponse>();
            for (int i = 0; i < PlayerData.OpenCloseStates.Count; i++)
            {
                if (PlayerData.OpenCloseStates[i].funcId == 2)
                {
                    //btn_firstchange.gameObject.SetActive(PlayerData.OpenCloseStates[i].state);
                }
                if (PlayerData.OpenCloseStates[i].funcId == 3)
                {
                    //btn_28gang.gameObject.SetActive(common.OpenCloseStates[i].state);
                    //btn_niuniu.gameObject.SetActive(common.OpenCloseStates[i].state);
                }
                if (PlayerData.OpenCloseStates[i].funcId == 4)
                {
                    PlayerData.isShengFenZheng = PlayerData.OpenCloseStates[i].state;
                }
                if (PlayerData.OpenCloseStates[i].funcId == 5)
                {
                    PlayerData.isZhengSongPwd = PlayerData.OpenCloseStates[i].state;
                }
                if (PlayerData.OpenCloseStates[i].funcId == 6)
                {
                    PlayerData.isWxSheZhiNickName = PlayerData.OpenCloseStates[i].state;
                }
            }
        }
    }
}