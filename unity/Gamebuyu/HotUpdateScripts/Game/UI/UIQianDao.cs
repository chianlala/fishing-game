using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIQianDao : MonoBehaviour
    {
        public Button btn_close1;
 
        public GameObject view_qiandao;
        public GameObject view_award;
        //---------------------获得奖励-------------------------
        public Transform grid_award;
        public Transform view_Sigin;
        public Button btn_AwardOk; 

        //---------------------签到view----------------------------
        public Button[] btn_days=new Button[30];
        private bool b_alreadyQiandao = false;
        public Image Img_qiandao;   
        public Text txt_qiandaoNum;
        public Button btn_qiandaoOk;

        public Image Img_sgin;
        public Image Img_notsgin; 
        //private string[] str_prize =
        //{                             
        //    "1000金币", 
        //    "2000金币", 
        //    "3000金币", 
        //    "4000金币", 
        //    "5000金币", 
        //    "8000金币", 
        //    "10000金币"
        //};
  
        void FindCompent() {
            btn_close1 = this.transform.Find("bg/view_qiandao/title/btn_closeqiandao").GetComponent<Button>();
            view_qiandao = this.transform.Find("bg/view_qiandao").gameObject;
            view_award = this.transform.Find("bg/view_award").gameObject;
            grid_award=this.transform.Find("bg/view_award/bg/grid_award");
            view_Sigin=this.transform.Find("bg/view_Sigin");
            btn_AwardOk=this.transform.Find("bg/view_award/bg/btn_AwardOk").GetComponent<Button>();
            //Img_qiandao = this.transform.Find("bg/view_Sigin/anchor/content/get_award/Img_qiandao").GetComponent<Image>();
            txt_qiandaoNum = this.transform.Find("bg/view_Sigin/anchor/content/Img_qiandao/txt_qiandaoNum").GetComponent<Text>();
            btn_qiandaoOk = this.transform.Find("bg/view_Sigin/anchor/content/btn_qiandaoOk").GetComponent<Button>();
            Img_sgin = this.transform.Find("Img_sgin").GetComponent<Image>();
            Img_notsgin = this.transform.Find("Img_notsgin").GetComponent<Image>();
            for (int i = 0; i < btn_days.Length; i++)
            {
                btn_days[i] = transform.Find("bg/view_qiandao/ScrollView/Viewport/Content/item" + (i+1)).GetComponent<Button>();
         
            }
        }
        private void Awake()
        {
            FindCompent();
            btn_close1.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIQianDao); });
            btn_qiandaoOk.onClick.AddListener(() =>
            { 
                view_Sigin.gameObject.SetActive(false);
            });

            for (int i = 0; i < btn_days.Length; i++)
            {
                btn_days[i].transform.Find("txt_gold").GetComponent<Text>().text= "x"+common5.JsonCanShu["Sign"][(i+1).ToString()][1].ToString();
                int n = i;
                btn_days[n].onClick.AddListener(() =>
                {
                    NetMessage.OseeLobby.Req_DailySignRequest();
                });
            }
            btn_AwardOk.onClick.AddListener(() => { view_award.SetActive(false); });
            UEventDispatcher.Instance.AddEventListener(UEventName.SignedTimesResponse, On_SignedTimesResponse);//获取已签到次数返回
            UEventDispatcher.Instance.AddEventListener(UEventName.OneKeyGetDailyTaskRewardsResponse, On_OneKeyGetDailyTaskRewardsResponse);//一键领取所有已完成的每日奖励响应

        }
  
        void OnEnable()
        {
            NetMessage.OseeLobby.Req_SignedTimesRequest();
            NetMessage.OseeFishing.Req_DailyTaskListRequest();
            view_award.SetActive(false);
        }

        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.SignedTimesResponse, On_SignedTimesResponse);//获取已签到次数返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.OneKeyGetDailyTaskRewardsResponse, On_OneKeyGetDailyTaskRewardsResponse);//一键领取所有已完成的每日奖励响应
        }

        /// <summary>
        /// 获取已签到次数返回
        /// <summary>
        private void On_SignedTimesResponse(UEventContext obj)
        {
            var pack = obj.GetData<SignedTimesResponse>();
            b_alreadyQiandao = pack.signed;
            //根据签到次数改变界面
            for (int i = 0; i < btn_days.Length; i++)
            {
                GameObject objGou = btn_days[i].transform.Find("gou").gameObject;
                if (pack.times==30&& pack.nowSign==false)
                {
                    //代表已经过了30天
                    objGou.SetActive(false);
                    btn_days[i].GetComponent<Image>().sprite = Img_notsgin.sprite;
                }
                else
                {
                    
                    int nIco = 0;
                    if (pack.times > i)
                    {
                        nIco = 1;
                        objGou.SetActive(true);
                        btn_days[i].GetComponent<Image>().sprite = Img_sgin.sprite;
                    }
                    else
                    {
                        objGou.SetActive(false);
                        btn_days[i].GetComponent<Image>().sprite = Img_notsgin.sprite;
                    }
                    if (i == pack.times)
                    {
                        if (pack.nowSign)
                        {
                            view_Sigin.gameObject.SetActive(true);
                            //Img_qiandao.sprite =  common4.LoadSprite(string.Format("qiandao/jinbi_" + pack.times));// Resources.Load<Sprite>("qiandao/jinbi_" + pack.times);
                            //Img_qiandao.SetNativeSize();
                            var tmp = common5.JsonCanShu["Sign"][i.ToString()][1].ToString();
                            txt_qiandaoNum.text = "x" + tmp;
                        }
                    }
                }
              
            }
        }
        /// <summary>
        /// 一键领取所有已完成的每日奖励响应
        /// <summary>
        private void On_OneKeyGetDailyTaskRewardsResponse(UEventContext obj)
        {
            var pack = obj.GetData<OneKeyGetDailyTaskRewardsResponse>();
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            foreach (var item in pack.rewards)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            tmp.InitItem(Dictmp, -1, true);
            NetMessage.OseeFishing.Req_DailyTaskListRequest();
        }

      
        private void OnDisable()
        {
         
        }
    }
}