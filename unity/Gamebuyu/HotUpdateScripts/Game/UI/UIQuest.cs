using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIQuest : MonoBehaviour
    {
        public Button btn_close;
        public Button btn_close1;
        public GameObject view_quest;
        public GameObject view_qiandao;
        public GameObject view_award;
        //public Toggle tog_quest;
        //public Toggle tog_qiandao;

        public Text txt_Allhyd;
        public Image img_allprocess;
        public Button btn_allLq;
        public Button[] AllboxLinqu=new Button[4];
        //---------------------任务view------------------------
        //   public Text txt_info;
        public GameObject itemjob;
        public GameObject itemReward;
        public Transform grid_job; 

        GameObjectPool itemPool = new GameObjectPool();
        GameObjectPool itemRewardPool = new GameObjectPool();
        //---------------------获得奖励-------------------------
        public Transform grid_award;
        public Transform view_Sigin;
        public Button btn_AwardOk; 

        //---------------------签到view----------------------------
        public Button[] btn_days=new Button[7];
        private bool b_alreadyQiandao = false;
        public Image Img_qiandao;   
        public Text txt_qiandaoNum;
        public Button btn_qiandaoOk;
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
        private int[] int_prize ={
            1000,
            2000,
            3000,
            4000,
            5000,
            8000,
            10000,
        };
        void FindCompent() {

            btn_close = this.transform.Find("bg/view_quest/btn_close").GetComponent<Button>();
            btn_close1 = this.transform.Find("bg/view_qiandao/bg/btn_close1").GetComponent<Button>();
            view_quest = this.transform.Find("bg/view_quest").gameObject;
            view_qiandao = this.transform.Find("bg/view_qiandao").gameObject;
            view_award = this.transform.Find("bg/view_award").gameObject;


            txt_Allhyd = this.transform.Find("bg/view_quest/Image/txt_Allhyd").GetComponent<Text>();
            img_allprocess = this.transform.Find("bg/view_quest/bg_process/img_allprocess").GetComponent<Image>();
            btn_allLq = this.transform.Find("bg/view_quest/btn_allLq").GetComponent<Button>();


            itemjob = this.transform.Find("bg/view_quest/Scroll View/Viewport/grid_job/itemjob").gameObject;
            itemReward = this.transform.Find("bg/view_award/itemReward").gameObject;
            grid_job = this.transform.Find("bg/view_quest/Scroll View/Viewport/grid_job");
            
            grid_award=this.transform.Find("bg/view_award/bg/grid_award");
            view_Sigin=this.transform.Find("bg/view_Sigin");
            btn_AwardOk=this.transform.Find("bg/view_award/bg/btn_AwardOk").GetComponent<Button>();


            Img_qiandao = this.transform.Find("bg/view_Sigin/bg/get_award/Img_qiandao").GetComponent<Image>();
            txt_qiandaoNum = this.transform.Find("bg/view_Sigin/bg/get_award/Img_qiandao/txt_qiandaoNum").GetComponent<Text>();
            btn_qiandaoOk = this.transform.Find("bg/view_Sigin/bg/btn_qiandaoOk").GetComponent<Button>();

            for (int i = 0; i < btn_days.Length; i++)
            {
                btn_days[i] = transform.Find("bg/view_qiandao/root_day/item"+ i).GetComponent<MyButton>(); 
            }
            for (int i = 0; i < AllboxLinqu.Length; i++)
            {
                AllboxLinqu[i] = transform.Find("bg/view_quest/"+i+ "/btn_dac0").GetComponent<Button>();

            }
            
        }
        private void Awake()
        {
            FindCompent();
            itemPool.SetTemplete(itemjob);
            itemPool.Recycle(itemjob);
            itemRewardPool.SetTemplete(itemReward);
            itemRewardPool.Recycle(itemReward);

            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIQuest); });
            btn_close1.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIQuest); });
            //tog_quest.onValueChanged.AddListener((isOn) =>
            //{
            //    view_quest.SetActive(isOn);
            //    view_qiandao.SetActive(!isOn);
            //});
            btn_qiandaoOk.onClick.AddListener(() =>
            {
                view_Sigin.gameObject.SetActive(false);
            });

            for (int i = 0; i < btn_days.Length; i++)
            {
                int n = i;
                btn_days[n].onClick.AddListener(() =>
                {
                    if (!b_alreadyQiandao)
                        NetMessage.OseeLobby.Req_DailySignRequest();
                });
            }
            btn_allLq.onClick.AddListener(() =>
            {
                NetMessage.OseeFishing.Req_OneKeyGetDailyTaskRewardsRequest();
            });
            for (int i = 0; i < AllboxLinqu.Length; i++)
            {
                int n = i;
                AllboxLinqu[n].onClick.AddListener(() =>
                {
                    varHuoyue = n;
                    if (n == 0)
                    {
                        NetMessage.OseeFishing.Req_GetDailyActiveRewardRequest(30);
                    }
                    else if (n == 1)
                    {
                        NetMessage.OseeFishing.Req_GetDailyActiveRewardRequest(60);
                    }
                    else if (n == 2)
                    {
                        NetMessage.OseeFishing.Req_GetDailyActiveRewardRequest(100);
                    }
                    else if (n == 3)
                    {
                        NetMessage.OseeFishing.Req_GetDailyActiveRewardRequest(150);
                    }
                    //AllboxLinqu[n].GetComponent<Image>().enabled = false;
                    //AllboxLinqu[n].transform.GetChild(0).gameObject.SetActive(true);
                });
            }
            btn_AwardOk.onClick.AddListener(() => { view_award.SetActive(false); });

            UEventDispatcher.Instance.AddEventListener(UEventName.DailyTaskListResponse, On_DailyTaskListResponse);//每日任务列表响应
            UEventDispatcher.Instance.AddEventListener(UEventName.GetDailyTaskRewardResponse, On_GetDailyTaskRewardResponse);//领取每日任务奖励响应
            UEventDispatcher.Instance.AddEventListener(UEventName.GetDailyActiveRewardResponse, On_GetDailyActiveRewardResponse);//领取每日活跃奖励响应

            //UEventDispatcher.Instance.AddEventListener(UEventName.FishingTaskListResponse, On_FishingTaskListResponse);//捕鱼获取任务列表返回
            //UEventDispatcher.Instance.AddEventListener(UEventName.FishingGetTaskRewardResponse, On_FishingGetTaskRewardResponse);//捕鱼获取任务奖励返回
            UEventDispatcher.Instance.AddEventListener(UEventName.SignedTimesResponse, On_SignedTimesResponse);//获取已签到次数返回

            UEventDispatcher.Instance.AddEventListener(UEventName.OneKeyGetDailyTaskRewardsResponse, On_OneKeyGetDailyTaskRewardsResponse);//一键领取所有已完成的每日奖励响应

        }
        public void Setpanel(int m)
        {
            if (m == 0)
            {
               // tog_qiandao.isOn = true;
                view_quest.SetActive(false);
                view_qiandao.SetActive(true);
            }
            else if (m == 1)
            {
              //  tog_quest.isOn = true;
                view_quest.SetActive(true);
                view_qiandao.SetActive(false);
            }
        }
        void OnEnable()
        {
            //NetMessage.OseeFishing.Req_FishingTaskListRequest();
            NetMessage.OseeLobby.Req_SignedTimesRequest();

            NetMessage.OseeFishing.Req_DailyTaskListRequest();
            view_award.SetActive(false);

            for (int i = 0; i < btn_days.Length; i++)
            {
                int n = i;
                if (PlayerData.vipLevel > 0)
                {
                    var tmp = int_prize[i] * (PlayerData.vipLevel + 1);
                    // btn_days[i].transform.Find("txt_gold").GetComponent<Text>().text= "x"+tmp.ToString();
                    btn_days[i].transform.Find("tmp").gameObject.SetActive(true);
                    btn_days[i].transform.Find("tmp/Text").GetComponent<Text>().text = PlayerData.vipLevel.ToString();
                }
                else
                {
                    btn_days[i].transform.Find("tmp").gameObject.SetActive(false);
                }

            }
        }

        private void OnDestroy()
        {
            //UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingTaskListResponse, On_FishingTaskListResponse);//捕鱼获取任务列表返回
            //UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingGetTaskRewardResponse, On_FishingGetTaskRewardResponse);//捕鱼获取任务奖励返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.DailyTaskListResponse, On_DailyTaskListResponse);//每日任务列表响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetDailyTaskRewardResponse, On_GetDailyTaskRewardResponse);//领取每日任务奖励响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetDailyActiveRewardResponse, On_GetDailyActiveRewardResponse);//领取每日活跃奖励响应
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

                int nIco = 0;
                if (pack.times > i)
                {
                    nIco = 1;
                    objGou.SetActive(true);
                }
                else
                {
                    objGou.SetActive(false);
                }
                btn_days[i].GetComponent<Image>().sprite = common4.LoadSprite(string.Format("qiandao/{0}", nIco));// Resources.Load<Sprite>(string.Format("qiandao/{0}", nIco));
                if (i == pack.times - 1)
                {
                    if (pack.nowSign)
                    {
                        view_Sigin.gameObject.SetActive(true);
                        Img_qiandao.sprite = common4.LoadSprite(string.Format("qiandao/jinbi_" + pack.times));// Resources.Load<Sprite>("qiandao/jinbi_" + pack.times);
                        Img_qiandao.SetNativeSize();
                        var tmp = int_prize[i] * (PlayerData.vipLevel + 1);
                        txt_qiandaoNum.text = "x" + tmp.ToString();
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

        /// <summary>
        /// 领取每日任务奖励响应
        /// <summary>
        private void On_GetDailyTaskRewardResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetDailyTaskRewardResponse>();
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            foreach (var item in pack.rewards)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            tmp.InitItem(Dictmp, -1, true);
            NetMessage.OseeFishing.Req_DailyTaskListRequest();
        }
        int varHuoyue = 0;
        /// <summary> 
        /// 领取每日活跃奖励响应
        /// <summary>
        private void On_GetDailyActiveRewardResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetDailyActiveRewardResponse>();
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            foreach (var item in pack.rewards)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            tmp.InitItem(Dictmp, -1, true);
            AllboxLinqu[varHuoyue].GetComponent<group>().SetImgBool(false);
        }


        ///// <summary>
        ///// 捕鱼获取任务奖励返回
        ///// <summary>
        //private void On_FishingGetTaskRewardResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<FishingGetTaskRewardResponse>();
        //    //pack.taskInfos.
        //    //MessageBox.ShowPopMessage("领取任务奖励成功");
        //    view_award.SetActive(true);
        //    //清空
        //    for (int i = 0; i < grid_award.childCount; i++)
        //    {
        //        var go = grid_award.GetChild(i);
        //        itemRewardPool.Recycle(go);
        //    }
        //    for(int i=0;i<pack.taskInfos.rewards.Count;i++)
        //    {
        //        var data = pack.taskInfos.rewards[i];
        //        var go2 = itemRewardPool.Get();
        //        go2.SetActive(true);
        //        go2.transform.SetParent(grid_award, false);
        //        go2.transform.Find("img_item").GetComponent<Image>().sprite = common4.LoadSprite("item/" + data.itemId);
        //        go2.transform.Find("txt_item").GetComponent<Text>().text = "x" + data.itemNum;
        //    }
        //}

        /// <summary>
        /// 每日任务列表响应
        /// <summary>
        private void On_DailyTaskListResponse(UEventContext obj)
        {
            var pack = obj.GetData<DailyTaskListResponse>();
            for (int i = 0; i < pack.active.Count; i++)
            {
                if (pack.active[i].activeLevel == 30)
                {
                    AllboxLinqu[0].GetComponent<group>().SetImgBool(!pack.active[i].receive);
                }
                else if (pack.active[i].activeLevel == 60)
                {
                    AllboxLinqu[1].GetComponent<group>().SetImgBool(!pack.active[i].receive);
                }
                else if (pack.active[i].activeLevel == 100)
                {
                    AllboxLinqu[2].GetComponent<group>().SetImgBool(!pack.active[i].receive);
                }
                else if (pack.active[i].activeLevel == 150)
                {
                    AllboxLinqu[3].GetComponent<group>().SetImgBool(!pack.active[i].receive);
                }
            }
            txt_Allhyd.text = pack.totalActive.ToString();
            img_allprocess.fillAmount = pack.totalActive / 150f;
            //清空 
            for (int i = 0; i < grid_job.childCount; i++)
            {
                var go = grid_job.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }
            int nOver = 0;

            for (int i = 0; i < pack.tasks.Count; i++)
            {
                //// 捕鱼任务数据
                //message FishingTaskInfoProto {
                //    optional int64 taskId = 1; // 任务id
                //    optional string taskName = 2; // 任务名
                //    optional int32 nowNum = 3; // 当前已完成数量
                //    optional int32 targetNum = 4; // 目标数量
                //    repeated ItemDataProto rewards = 5; // 任务奖励
                //    optional bool received = 6; // 是否已领取
                //}
                int n = i;
                var data = pack.tasks[n];
                var go = itemPool.Get();
                go.SetActive(true);
                go.transform.SetParent(grid_job, false);
                //徽章
                Image imgIco = go.transform.Find("bg_huizhang/img_huizhang").GetComponent<Image>();
                imgIco.sprite = common4.LoadSprite(string.Format(string.Format("FishIcon/{0}", data.taskId)));// "qiandao/jinbi_" + pack.times Resources.Load<Sprite>(string.Format("fishIco/{0}", data.taskId));
                if (imgIco.sprite == null)
                {
                    imgIco.enabled = false;
                }
                else
                {
                    imgIco.enabled = true;
                }
                imgIco.SetNativeSize();
                go.transform.Find("txt_name").GetComponent<Text>().text = data.name;
                go.transform.Find("txt_detail").GetComponent<Text>().text = data.info;
                go.transform.Find("hyd/Text").GetComponent<Text>().text = data.active.ToString();
                go.transform.Find("bg_process/txt_process").GetComponent<Text>().text = string.Format("完成度 {0}/{1}", data.progress, data.target);
                go.transform.Find("bg_process/img_process").GetComponent<Image>().fillAmount = (float)data.progress / data.target;

                Button btnLingqu = go.transform.Find("btn_lingqu").GetComponent<Button>();
                Button btn_immediately = go.transform.Find("btn_immediately").GetComponent<Button>();

                btn_immediately.onClick.RemoveAllListeners();
                btn_immediately.onClick.AddListener(() =>
                {
                    UIMgr.CloseUI(UIPath.UIQuest);
                    UIMgr.ShowUI(UIPath.UIBuyuMenu);
                    NetMessage.OseeFishing.Req_QuickStartRequest();
                });
                if (pack.tasks[i].taskId == 1)
                {
                    btn_immediately.onClick.RemoveAllListeners();
                    btn_immediately.onClick.AddListener(() =>
                    {
                        Setpanel(0);
                    });
                }
                GameObject btnyiLingqu = go.transform.Find("btn_yilingqu").gameObject;
                btnLingqu.onClick.RemoveAllListeners();
                btnLingqu.onClick.AddListener(() =>
                {
                    btnLingqu.gameObject.SetActive(false);
                    btnyiLingqu.SetActive(true);
                    NetMessage.OseeFishing.Req_GetDailyTaskRewardRequest(data.taskId);
                });
                if (data.progress >= data.target)
                {
                    if (data.receive)
                    {
                        nOver++;
                        btnLingqu.gameObject.SetActive(false);
                        btnyiLingqu.SetActive(true);
                    }
                    else
                    {
                        btnLingqu.gameObject.SetActive(true);
                        btnyiLingqu.SetActive(false);
                    }
                }
                else
                {
                    btnLingqu.gameObject.SetActive(false);
                    btnyiLingqu.SetActive(false);
                }

                Transform grid2 = go.transform.Find("root_reward");
                //清空
                for (int j = 0; j < grid2.childCount; j++)
                {
                    var go2 = grid2.GetChild(j);
                    itemRewardPool.Recycle(go2.gameObject);
                }
                for (int j = 0; j < data.rewards.Count; j++)
                {
                    var go2 = itemRewardPool.Get();
                    go2.SetActive(true);
                    go2.transform.SetParent(grid2, false);
                    go2.transform.Find("img_item").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("item/{0}", data.rewards[j].itemId)); 
                    go2.transform.Find("txt_item").GetComponent<Text>().text = "x" + data.rewards[j].itemNum;
                }
            }

          //  txt_info.text = string.Format("今日任务{0}/{1}", nOver, pack.tasks.Count);
        }
        private void OnDisable()
        {
            //if (UIMainMenu.instance != null)
            //{
            //    UIMainMenu.instance.qiandaoPoint.gameObject.SetActive(false);
            //}
        }
    }
}