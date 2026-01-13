using CoreGame;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class LeftHead : MonoBehaviour
    {
        public Image ImageHead;
        public Text txt_name;
        public Text txt_vip;
        public Button btn_vip;
        void Awake() {
            ImageHead = this.transform.Find("mask/img_head").GetComponent<Image>();
            txt_name = this.transform.Find("bg_info/txt_name").GetComponent<Text>();
            txt_vip = this.transform.Find("bottom/bg_vip2/vip2").GetComponent<Text>();
            btn_vip = this.transform.Find("bottom/bg_vip2").GetComponent<Button>();
            EventManager.StrHeadUrlUpdate += On_StrHeadUrl;
            EventManager.ChangeVipLevel += On_ChangeVipLevel;
            EventManager.NackNameUpdate += On_ChangePlayerName;
            EventManager.intHeadUrlUpdate += On_intHeadUrlUpdate;
        }
        void Start()
        {
            //btn_vip.onClick.AddListener(() =>
            //{
            //    UIMgr.ShowUI(UIPath.UIVipInfo);
            //});
    
            UIEventListener.Get(ImageHead.gameObject).onClick = (a, b) =>
            {
                //SoundHelper.PlayBtnClip();
                UIMgr.ShowUI(UIPath.UIUserInfo);
            };
            txt_name.text = PlayerData.NickName;
            txt_vip.text = PlayerData.vipLevel.ToString();
        }
        void OnDestroy() {
            EventManager.StrHeadUrlUpdate -= On_StrHeadUrl;
            EventManager.ChangeVipLevel -= On_ChangeVipLevel;
            EventManager.NackNameUpdate -= On_ChangePlayerName;
            EventManager.intHeadUrlUpdate -= On_intHeadUrlUpdate;
        } 
        private void OnEnable()
        {
            txt_name.text = PlayerData.NickName;
            txt_vip.text = PlayerData.vipLevel.ToString();
            GetMyHeadImage((sp) =>
            {
                ImageHead.sprite = sp;
            });
      

        }
        private void OnDisable()
        {

         
        }
        void On_ChangePlayerName(string v)
        {
            txt_name.text = v.ToString();
        }
        void On_ChangeVipLevel(int v)
        {
            txt_vip.text = v.ToString();
        }
        void On_StrHeadUrl(string v)
        {
            //UIHelper.GetHeadImage(v.ToString(), (sp) => {
            //    if (ImageHead.GetComponent<Image>().sprite != sp)
            //    {
            //        ImageHead.GetComponent<Image>().sprite = sp;
            //    }
            //});
            GetMyHeadImage((sp) =>
            {
                ImageHead.sprite = sp;
            });
        }
        void On_intHeadUrlUpdate(int m) {
            GetMyHeadImage((sp) =>
            {
                ImageHead.sprite = sp;
            });
        }
        /// <summary>
        /// 获取自己的头像
        /// </summary>
        /// <param name="callback"></param>
        public  void GetMyHeadImage(Action<Sprite> callback)
        {
           
            if (PlayerData.StrHeadUrl=="")
            {
                string url = PlayerData.HeadIndex.ToString("00");
              
                UIHelper.GetHeadImage(url, callback);
            }
            else
            {
                string url = "";
                url = PlayerData.StrHeadUrl;
                UIHelper.GetHeadImage(url, callback);
            }
      
        }

    }
}