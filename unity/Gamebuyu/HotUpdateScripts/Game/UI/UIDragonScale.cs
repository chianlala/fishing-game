using com.maple.game.osee.proto;
using com.maple.game.osee.proto.agent;
using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.fruit;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIDragonScale : MonoBehaviour
    {
        public Button btn_close;
        int Panel1;
        public Button btn_chnageLj;
        public Button btn_chnageDt;
        public Text txt_yongyou;
        public Text txt_yongyoulj;

        public Toggle leftdantou;
        public Toggle leftlongjin;

        public InputField Input_NumLj;
        public InputField Input_NumDt;

        public InputField Input_NumLjShow;
        public InputField Input_NumDtShow;

        public Button btn_jiaDantou;
        public Button btn_jianDantou;

        public Button btn_jiaLongjin;
        public Button btn_jianLongjin;

        public long dantou;
        public long longjin;
        //数量文本 
        //public InputField[] Input_ArrNum;
        // private Outline color; 
        void FindCompent() {
            btn_close = this.transform.Find("btn_close2").GetComponent<Button>();
            btn_chnageLj = this.transform.Find("panel1/btn_oklj").GetComponent<Button>();
            btn_chnageDt = this.transform.Find("panel0/btn_okdt").GetComponent<Button>();
            txt_yongyou = this.transform.Find("bottom/show/num").GetComponent<Text>();
            txt_yongyoulj = this.transform.Find("bottom/showlj/numlj").GetComponent<Text>();
            leftdantou = this.transform.Find("root_tog/tog_dantou").GetComponent<Toggle>();
            leftlongjin = this.transform.Find("root_tog/tog_longjin").GetComponent<Toggle>();
            Input_NumLj = this.transform.Find("panel1/zhuanhuanlj/InputField").GetComponent<InputField>();
            Input_NumDt = this.transform.Find("panel0/zhuanhuandt/InputField").GetComponent<InputField>();
            Input_NumLjShow = this.transform.Find("panel1/zhuanhuandt/InputField").GetComponent<InputField>();
            Input_NumDtShow = this.transform.Find("panel0/zhuanhuanlj/InputField").GetComponent<InputField>();
            btn_jiaDantou = this.transform.Find("panel0/zhuanhuandt/jia").GetComponent<Button>();
            btn_jianDantou = this.transform.Find("panel0/zhuanhuandt/jian").GetComponent<Button>();
            btn_jiaLongjin = this.transform.Find("panel1/zhuanhuanlj/jia").GetComponent<Button>();
            btn_jianLongjin = this.transform.Find("panel1/zhuanhuanlj/jian").GetComponent<Button>();
        }
        void Awake()
        {
            FindCompent();
            btn_close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIDragonScale);
            });


            Input_NumLj.onValueChanged.AddListener((arg) =>
            {
                long nowNum = 0;
                if (long.TryParse(arg, out nowNum))
                {
                    if (nowNum % 500000 == 0)
                    {
                    //是50万倍数
                }
                    else
                    {
                    //不是50万倍数
                    // nowNum = nowNum - nowNum % 500000;
                }
                    longjin = nowNum;
                    Input_NumLj.text = longjin.ToString();
                    Input_NumLjShow.text = (nowNum / 500000).ToString();
                }
            });
            Input_NumDt.onValueChanged.AddListener((arg) =>
            {
                long nowNum = 0;
                if (long.TryParse(arg, out nowNum))
                {
                    dantou = nowNum;
                    Input_NumDt.text = dantou.ToString();
                }
                Input_NumDtShow.text = (nowNum * 500000).ToString();
            });
            btn_jiaDantou.onClick.AddListener(() =>
            {
                dantou++;
                if (dantou > 100)
                {
                    dantou = 0;
                }
                Input_NumDt.text = dantou.ToString();
            });
            btn_jianDantou.onClick.AddListener(() =>
            {
                dantou--;
                if (dantou < 0)
                {
                    dantou = 100;
                }

                Input_NumDt.text = dantou.ToString();
            });
            btn_jiaLongjin.onClick.AddListener(() =>
            {

                longjin = longjin + 500000;
                if (longjin > 50000000)
                {
                    longjin = 0;
                }

                Input_NumLj.text = longjin.ToString();
            });
            btn_jianLongjin.onClick.AddListener(() =>
            {
                longjin = longjin - 500000;
                if (longjin < 0)
                {
                    longjin = 50000000;
                }

                Input_NumLj.text = longjin.ToString();
            });
            btn_chnageLj.onClick.AddListener(() =>
            {
                if (longjin % 500000 == 0)
                {
                //是50万倍数
            }
                else
                {
                    MessageBox.ShowPopOneMessage("龙晶需为50万的倍数");
                    return;
                //不是50万倍数
                // nowNum = nowNum - nowNum % 500000;
            }
                List<FishingChallengeUseTorpedo> torpedoes = new List<FishingChallengeUseTorpedo>();
                FishingChallengeUseTorpedo varNum = new FishingChallengeUseTorpedo();
                varNum.angle = 0;
                varNum.torpedoId = 18;

                varNum.torpedoNum = int.Parse(Input_NumLj.text);
                torpedoes.Add(varNum);

                NetMessage.Chanllenge.Req_FishingChallengeUseTorpedoRequest(torpedoes);
            });
            btn_chnageDt.onClick.AddListener(() =>
            {

                List<FishingChallengeUseTorpedo> torpedoes = new List<FishingChallengeUseTorpedo>();
                FishingChallengeUseTorpedo varNum = new FishingChallengeUseTorpedo();
                varNum.angle = 0;
                varNum.torpedoId = 7;
                varNum.torpedoNum = int.Parse(Input_NumDt.text);
                torpedoes.Add(varNum);

                NetMessage.Chanllenge.Req_FishingChallengeUseTorpedoRequest(torpedoes);
            });
        
            UEventDispatcher.Instance.AddEventListener(UEventName.ExchangeDragonCrystalResponse, On_ExchangeDragonCrystalResponse);//兑换龙晶响应
        }
        private void InitThis()
        {
        }
        private void OnEnable()
        {
            ChangeMyNum();

            // tog_show.isOn = false;

            txt_yongyou.text = common.myItem[2].ToString();
            txt_yongyoulj.text = PlayerData.DragonCrystal.ToString();
            EventManager.GoldTorpedoUpdate += fGoldTorpedoUpdate;
            EventManager.DragonCrystalUpdate += fDragonCrystalUpdate;
            InitThis();
        }
        void OnDisable() {
            EventManager.GoldTorpedoUpdate -= fGoldTorpedoUpdate;
            EventManager.DragonCrystalUpdate -= fDragonCrystalUpdate; 
        }
        void fGoldTorpedoUpdate(long arg) {
            txt_yongyou.text = arg.ToString();
        } 
        void fDragonCrystalUpdate(long arg)
        {
            txt_yongyoulj.text = arg.ToString();
        }
        public void ChangeMyNum()
        {
            txt_yongyou.text = common.myItem[2].ToString();
            txt_yongyoulj.text = PlayerData.DragonCrystal.ToString();
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ExchangeDragonCrystalResponse, On_ExchangeDragonCrystalResponse);//兑换龙晶响应
        }
        /// <summary>
        /// 兑换龙晶响应
        /// <summary>
        private void On_ExchangeDragonCrystalResponse(UEventContext obj)
        {
            var pack = obj.GetData<ExchangeDragonCrystalResponse>();
            MessageBox.Show("兑换成功");
        }

    }
}
enum DragonScale
{
    金币=1, 
    低阶龙珠 =5,
    中阶龙珠 = 6,
    高阶龙珠 = 7,
}

