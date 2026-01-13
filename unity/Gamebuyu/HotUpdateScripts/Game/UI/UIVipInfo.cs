using com.maple.game.osee.proto.lobby;
using CoreGame;
using LitJson;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI{
    public class UIVipInfo : MonoBehaviour {
        public Button btn_close;
        public Button btn_shop;
        public Text txt_nextMoney;
        public Text txt_process;
        public ArtNumQuote num_pageLevel;
        public Text num_vipLevel;
        public Image img_process;
        public Text txt_info;
        public Button btn_prev;
        public Button btn_next;
        //特权炮台
        public Image img_zhensong;
        //public Sprite[] img_tmp;

        public GameObjectPool AwardImgPool;
        public Transform Awarditem;
        private int n_curPage = 1;
        private void Awake()
        {
            FindCompent();
            AwardImgPool = new GameObjectPool();
            AwardImgPool.SetTemplete(Awarditem.gameObject);
            AwardImgPool.Recycle(Awarditem.gameObject);
            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIVipInfo); });
            btn_shop.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIVipInfo);
                UIMgr.ShowUISynchronize(UIPath.UIShop);
            });
            btn_prev.onClick.AddListener(() =>
            {
                n_curPage--;
                if (n_curPage < 1)
                {
                    n_curPage = 1;
                }
                else
                    UpdateView();
            });
            btn_next.onClick.AddListener(() =>
            {
                n_curPage++;
                if (n_curPage > 9)
                    n_curPage = 9;
                else
                    UpdateView();
            });

        }

        void FindCompent() {


            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_shop = this.transform.Find("bg/btn_shop").GetComponent<Button>();
            txt_nextMoney = this.transform.Find("bg/txt_nextMoney").GetComponent<Text>();
            txt_process = this.transform.Find("bg/viewBase/bg_process/txt_process").GetComponent<Text>();
            num_pageLevel = this.transform.Find("bg/viewBase/num_pageLevel").GetComponent<ArtNumQuote>();
            num_vipLevel = this.transform.Find("bg/bg_vip/num_vipLevel").GetComponent<Text>();
            img_process = this.transform.Find("bg/viewBase/bg_process/img_process").GetComponent<Image>();
            txt_info = this.transform.Find("bg/viewBase/Scroll View/Viewport/Content/txt_info").GetComponent<Text>();
            btn_prev = this.transform.Find("bg/btn_prev").GetComponent<Button>();
            btn_next = this.transform.Find("bg/btn_next").GetComponent<Button>();
            img_zhensong = this.transform.Find("bg/img_zhensong").GetComponent<Image>();
            Awarditem = this.transform.Find("bg/viewBase/tqlb/Scroll View/Viewport/Content/Awarditem");
        }
        private void On_VipLevelResponse(UEventContext obj)
        {
            var pack = obj.GetData<VipLevelResponse>();
            txt_nextMoney.text = PlayerData.vipNextMoney.ToString();
            num_vipLevel.text = PlayerData.vipLevel.ToString();

            if (PlayerData.vipLevel < common.dicVipConfig.Count)
            {
                txt_process.text = string.Format("{0}/{1}", PlayerData.vipTotalMoney, common.dicVipConfig[PlayerData.vipLevel + 1].money);
                img_process.fillAmount = (float)PlayerData.vipTotalMoney / common.dicVipConfig[PlayerData.vipLevel + 1].money;
            }
            else
            {
                txt_process.text = string.Format("最高等级");
                img_process.fillAmount = 1f;
            }
            UpdateView();
        }
        private void OnDisable()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取下次抽奖费用返回  
        }
        public void SetPanel(int NowPage)
        {
            n_curPage = NowPage;
            if (n_curPage < 1)
            {
                n_curPage = 1;
            }
            if (n_curPage > 9)
            {
                n_curPage = 9;
            }
            UpdateView();
        }
        private void OnEnable()
        {
            //txt_nextMoney.text = PlayerData.vipNextMoney.ToString();
            //num_vipLevel.Init("vip", PlayerData.vipLevel);
            //txt_process.text = string.Format("{0}/{1}", PlayerData.vipTotalMoney, common.dicVipConfig[n_curPage].money);
            //img_process.fillAmount = (float)PlayerData.vipTotalMoney / common.dicVipConfig[n_curPage].money;
            //UpdateView();
            NetMessage.OseeLobby.Req_VipLevelRequest();
            txt_nextMoney.text = PlayerData.vipNextMoney.ToString();
            num_vipLevel.text = PlayerData.vipLevel.ToString();

            if (PlayerData.vipLevel < common.dicVipConfig.Count)
            {
                txt_process.text = string.Format("{0}/{1}", PlayerData.vipTotalMoney, common.dicVipConfig[PlayerData.vipLevel + 1].money);
                img_process.fillAmount = (float)PlayerData.vipTotalMoney / common.dicVipConfig[PlayerData.vipLevel + 1].money;
            }
            else
            {
                txt_process.text = string.Format("最高等级");
                img_process.fillAmount = 1f;
            }
            UpdateView();
            UEventDispatcher.Instance.AddEventListener(UEventName.VipLevelResponse, On_VipLevelResponse);//获取下次抽奖费用返回  
        }

        void UpdateView()
        {
            num_pageLevel.Init(n_curPage);
            if (n_curPage >= 7)
            {
                img_zhensong.sprite = common4.LoadSprite("PaoIcon/"+3);
            }
            else if (n_curPage >= 4)
            {
                img_zhensong.sprite = common4.LoadSprite("PaoIcon/" + 2);
            }
            else if (n_curPage >= 1)
            {
                img_zhensong.sprite = common4.LoadSprite("PaoIcon/" + 1);
            }


            for (int i = 0; i < Awarditem.parent.childCount; i++)
            {
                AwardImgPool.Recycle(Awarditem.parent.GetChild(i).gameObject);
            }
            var str = common.dicVipConfig[n_curPage].strAward;

            // JsonData tmp = common.dicVipConfig[n_curPage].strAward.ToString();
            //JsonData allaward = JsonMapper.ToObject(str);
            //for (int i = 0; i < allaward.Count; i++) 
            //{
            //    int n = i;
            //    var tmpGO = AwardImgPool.Get(Awarditem.parent);
            //    JsonData vartmp = allaward[n];
            //    tmpGO.GetComponent<Image>().sprite = common4.LoadSprite("item/" + vartmp[0].ToString());
            //    tmpGO.transform.Find("Text").GetComponent<Text>().text = "x" + vartmp[1].ToString();
            //}
            string[] allaward = str.Split('.');
            for (int i = 0; i < allaward.Length; i++)
            {
                int n = i;
                var tmpGO = AwardImgPool.Get(Awarditem.parent);
                string[] vartmp = allaward[n].Split('&');
                tmpGO.GetComponent<Image>().sprite = common4.LoadSprite("item/" + vartmp[0]);
                tmpGO.transform.Find("Text").GetComponent<Text>().text = "x" + vartmp[1];
            }
            //内容分行
            //JsonData tmp = common.dicVipConfig[n_curPage].strInfo.ToString();

            txt_info.text = common.dicVipConfig[n_curPage].strInfo.Replace(' ', '\n');
            //txt_info.text = common.dicVipConfig[n_curPage].strInfo.Replace(' ', '\n');
        }
    }
}
