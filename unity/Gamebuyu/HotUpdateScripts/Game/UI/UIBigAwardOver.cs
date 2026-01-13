using com.maple.game.osee.proto;
using com.maple.game.osee.proto.agent;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.fruit;
using CoreGame;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIBigAwardOver : MonoBehaviour
    {

        public Text txt_score;
        public Text txt_paoaddition;
        public Text txt_changeaddition;
        public Text txt_vipaddition;
        public Text txt_mostranlk;
        public Text txt_ranlkreward;
        public Text txt_reward;
        public Text txt_myScore;

        public Button btn_ReChange;
        public Button btn_return;
        public Button btn_Close;
        public Image img_Award;
        public Text txt_Cost;
        void OnInitComponent()
        {
            txt_score = this.transform.Find("bg/TextJF/txt_score").GetComponent<Text>();
            txt_paoaddition = this.transform.Find("bg/TextlePTJC/txt_paoaddition").GetComponent<Text>();
            txt_changeaddition = this.transform.Find("bg/TextleTZJC/txt_changeaddition").GetComponent<Text>();
            txt_vipaddition = this.transform.Find("bg/TextleVipJC/txt_vipaddition").GetComponent<Text>();
            txt_mostranlk = this.transform.Find("bg/TextleDQPM/txt_mostranlk").GetComponent<Text>();
            txt_ranlkreward = this.transform.Find("bg/TextleftPMJL/txt_ranlkreward").GetComponent<Text>();
            txt_reward = this.transform.Find("bg/Award/txt_reward").GetComponent<Text>();
            txt_myScore = this.transform.Find("bg/TextleftMySocre/txt_myScore").GetComponent<Text>();
            btn_ReChange = this.transform.Find("bg/btn_ReChallege").GetComponent<Button>();
            btn_return = this.transform.Find("bg/btn_return").GetComponent<Button>();
            btn_Close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            img_Award = this.transform.Find("bg/Award").GetComponent<Image>();

            btn_Close = transform.Find("bg/btn_close").GetComponent<Button>();
            txt_Cost = transform.Find("bg/btn_ReChallege/txt_Cost").GetComponent<Text>();

            btn_Close.onClick.AddListener(() =>
            {
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
                UIMgr.CloseUI(UIPath.UIBigAwardOver);

            });
            btn_return.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIBigAwardOver);
                NetMessage.BigAward.Req_FishingGrandPrixStartRequest(PlayerData.PlayerId);
            });
            btn_ReChange.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIBigAwardOver);
                if (IsInDate(DateTime.Now, common.dt1_Start, common.dt2_end))
                {

                }
                else
                {
                    MessageBox.Show("开放时间为每日" + common.dt1_Start.ToString("HH:mm") + "-" + common.dt2_end.ToString("HH:mm"));
                    return;
                }
                NetMessage.BigAward.Req_FishingGrandPrixJoinRoomRequest();
            });
        }
        /// <summary> 
        /// 判断某个日期是否在某段日期范围内，返回布尔值
        /// </summary> 
        /// <param name="dt">要判断的日期</param> 
        /// <param name="dt1">开始日期</param> 
        /// <param name="dt2">结束日期</param> 
        /// <returns></returns>  
        private bool IsInDate(DateTime dt, DateTime dt1, DateTime dt2)
        {
            return dt.CompareTo(dt1) >= 0 && dt.CompareTo(dt2) <= 0;
        }
        public void OnInitJieSuan(double vip, double battery, double ga, int dayPoint, int rank, long nowPoint, int itemId, int itemNum)
        {
            txt_paoaddition.text = (battery * 100) + "%";
            //vip就是翅膀加成
            txt_vipaddition.text = (vip * 100) + "%";
            txt_mostranlk.text = rank.ToString();
            txt_changeaddition.text = (ga * 100) + "%";
            txt_score.text = dayPoint.ToString();
            txt_myScore.text = nowPoint.ToString();
            if (itemId == 0 || itemNum == 0)
            {
                img_Award.gameObject.SetActive(false);
                txt_reward.gameObject.SetActive(false);
            }
            else
            {
                img_Award.gameObject.SetActive(true);
                txt_reward.gameObject.SetActive(true);
                if (itemId == 3)
                {
                    img_Award.sprite = common4.LoadSprite("item/jiangquan2");//Resources.Load<Sprite>("item/jiangquan2");
                }
                else
                {
                    img_Award.sprite = common4.LoadSprite("item/" + itemId);// Resources.Load<Sprite>("item/" + itemId);
                }
                txt_reward.text = "x" + itemNum;
            }
        }
        private void OnEnable()
        {
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingGrandPrixStartResponse, On_FishingGrandPrixStartResponse);//大奖赛开赛响应
            NetMessage.BigAward.Req_FishingGrandPrixStartRequest(PlayerData.PlayerId);            
        }

        private void OnDisable()
        {
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingGrandPrixStartResponse, On_FishingGrandPrixStartResponse);//大奖赛开赛响应
        }
        /// <summary> 
        /// 大奖赛开赛响应
        /// <summary>
        private void On_FishingGrandPrixStartResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingGrandPrixStartResponse>();
            txt_Cost.text = pack.cost.ToString();
        }
        private void Awake()
        {
            OnInitComponent();
        }

        private void OnDestroy()
        {

        }
    }
}
