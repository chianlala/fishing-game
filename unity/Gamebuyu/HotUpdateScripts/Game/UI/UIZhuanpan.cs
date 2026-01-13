using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIZhuanpan : MonoBehaviour
    {
        //---------------------view main----------------------
        public GameObject[] obj_lights=new GameObject[8];
        public Text txt_cost;
        public Text txt_mianfei;
        public Text txt_myyongyou;
        public Button btn_go, btn_close;
        private float normalSpeed = 0.1f;
        private int _index = 0;//最后停的位置
        private Coroutine _T_StartZhuan = null;
        //--------------------view award----------------------
        public GameObject view_award;
        public Text img_award1;
        public Image img_award2;
        public Button btn_ok;
        public string[] all_caozuo=new string[8] { "极速X10" , "锁定X15", "电磁炮X5", "金币X10000", "分身X40", "金币X100000", "核弹x1", "核弹x10" };
        //记录
        public GameObjectPool JlPool;
        public GameObject itemtext;
        public Transform AllcjjlParent;
        public Transform MycjjlParent;

        public Scrollbar Scrollbar1;
        public Scrollbar Scrollbar2;
        void FindCompent() {

            obj_lights[0] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight3").gameObject;
            obj_lights[1] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight4").gameObject;
            obj_lights[2] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight5").gameObject;
            obj_lights[3] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight6").gameObject;
            obj_lights[4] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight7").gameObject;
            obj_lights[5] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight0").gameObject;
            obj_lights[6] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight1").gameObject;
            obj_lights[7] = this.transform.Find("bg/view_main/bgZhuan/root_light/imgLight2").gameObject;

            txt_cost = this.transform.Find("bg/view_main/btn_go/txt_cost").GetComponent<Text>();
            txt_mianfei = this.transform.Find("bg/view_main/txt_mianfei").GetComponent<Text>();
            txt_myyongyou = this.transform.Find("bg/view_main/Image/Text").GetComponent<Text>();
            btn_go = this.transform.Find("bg/view_main/btn_go").GetComponent<Button>();
            btn_close = this.transform.Find("bg/view_main/btn_close").GetComponent<Button>();
            view_award = this.transform.Find("bg/view_award").gameObject;
            img_award1 = this.transform.Find("bg/view_award/bg/award1").GetComponent<Text>();
            btn_ok = this.transform.Find("bg/view_award/bg/btn_ok").GetComponent<Button>();
            itemtext = this.transform.Find("bg/view_main/item").gameObject;
            AllcjjlParent = this.transform.Find("bg/view_main/Scroll View (2)/Viewport/Content");
            MycjjlParent = this.transform.Find("bg/view_main/Scroll View (3)/Viewport/Content");
        }
        private void Awake()
        {
            FindCompent();
            JlPool = new GameObjectPool();
            JlPool.SetTemplete(itemtext);

            UEventDispatcher.Instance.AddEventListener(UEventName.NextLotteryDrawFeeResponse, On_NextLotteryDrawFeeResponse);//获取下次抽奖费用返回
            UEventDispatcher.Instance.AddEventListener(UEventName.LotteryDrawResponse, On_LotteryDrawResponse);//转盘抽奖返回

            UEventDispatcher.Instance.AddEventListener(UEventName.TurnTableAllResponse, On_TurnTableAllResponse);//转盘中奖全部记录返回
            UEventDispatcher.Instance.AddEventListener(UEventName.TurnTableUserResponse, On_TurnTableUserResponse);//转盘中奖用户记录返回
            UEventDispatcher.Instance.AddEventListener(UEventName.LotteryInfoResponse, On_LotteryInfoResponse);//转盘中奖用户记录返回

            btn_go.onClick.AddListener(() =>
            {
                if (_T_StartZhuan != null)
                    return;
                if (txt_cost.text == "20000金币" && PlayerData.Gold < 20000)
                {
                    MessageBox.Show("金币不足");
                }
                else
                {
                    NetMessage.OseeLobby.Req_LotteryDrawRequest();
                }
            });
            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIZhuanpan); });
            btn_ok.onClick.AddListener(() =>
            {
                view_award.SetActive(false);
                NetMessage.OseeLobby.Req_NextLotteryDrawFeeRequest();
            });
        }

        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.NextLotteryDrawFeeResponse, On_NextLotteryDrawFeeResponse);//获取下次抽奖费用返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.LotteryDrawResponse, On_LotteryDrawResponse);//转盘抽奖返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.TurnTableAllResponse, On_TurnTableAllResponse);//转盘中奖全部记录返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.TurnTableUserResponse, On_TurnTableUserResponse);//转盘中奖用户记录返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.LotteryInfoResponse, On_LotteryInfoResponse);//转盘抽奖物品返回
        }

        private void OnEnable()
        {
            if (_T_StartZhuan != null)
            {
                StopCoroutine(_T_StartZhuan);
                _T_StartZhuan = null;
            }
            NetMessage.OseeLobby.Req_NextLotteryDrawFeeRequest();
            for (int i = 0; i < obj_lights.Length; i++)
            {
                obj_lights[i].SetActive(false);
            }
            txt_myyongyou.text = common.myCaiLiao[28].ToString();
            NetMessage.OseeLobby.Req_TurnTableAllRequest();
            NetMessage.OseeLobby.Req_TurnTableUserRequest(PlayerData.PlayerId);
            NetMessage.OseeLobby.Req_LotteryInfoRequest();
        }

        /// <summary>
        /// 获取下次抽奖费用返回
        /// <summary>
        private void On_NextLotteryDrawFeeResponse(UEventContext obj)
        {
            var pack = obj.GetData<NextLotteryDrawFeeResponse>();
            if (pack.freeCount > 0)
            {
                txt_mianfei.text = "免费";
                txt_cost.text = "";
                txt_cost.gameObject.SetActive(false);
            }
            else
            {
                txt_mianfei.text = "";
                txt_cost.gameObject.SetActive(true);
                txt_cost.text = "X" + pack.playNum;
            }
        }
        /// <summary>
        /// 转盘抽奖返回
        /// <summary>
        private void On_LotteryDrawResponse(UEventContext obj)
        {
            var pack = obj.GetData<LotteryDrawResponse>();
            _index = pack.index;
            if (_T_StartZhuan != null)
            {
                StopCoroutine(_T_StartZhuan);
                _T_StartZhuan = null;
            }
            _T_StartZhuan = StartCoroutine(T_startZhuanpan());

            //显示自己海兽石
            txt_myyongyou.text = common.myCaiLiao[28].ToString();
        }

        /// <summary>
        /// 转盘中奖全部记录返回
        /// <summary>
        private void On_TurnTableAllResponse(UEventContext obj)
        {
            var pack = obj.GetData<TurnTableAllResponse>();
            foreach (Transform item in AllcjjlParent)
            {
                JlPool.Recycle(item.gameObject);
            }
            for (int i = 0; i < pack.turnTable.Count; i++)
            {
                var mm = JlPool.Get(AllcjjlParent);
                mm.GetComponent<Text>().text = "  玩家：<color=yellow>" + pack.turnTable[i].userName + "</color>抽中了<color=yellow>" + (MjCanShu.BY_ItemLName[(int)pack.turnTable[i].itemId]) + "</color>X" + pack.turnTable[i].itemNum;
            }
            Scrollbar1.value = 1;
        }
        /// <summary>
        /// 转盘中奖用户记录返回
        /// <summary>
        private void On_TurnTableUserResponse(UEventContext obj)
        {
            var pack = obj.GetData<TurnTableUserResponse>();
            foreach (Transform item in MycjjlParent)
            {
                JlPool.Recycle(item.gameObject);
            }
            for (int i = 0; i < pack.turnTable.Count; i++)
            {
                var mm = JlPool.Get(MycjjlParent);
                mm.GetComponent<Text>().text = "  玩家：<color=yellow>" + pack.turnTable[i].userName + "</color>抽中了<color=yellow>" + MjCanShu.BY_ItemLName[(int)pack.turnTable[i].itemId] + "</color>X" + pack.turnTable[i].itemNum;
            }
            Scrollbar1.value = 1;
        }
        private void On_LotteryInfoResponse(UEventContext obj)
        {
            var pack = obj.GetData<LotteryInfoResponse>();
            for (int i = 0; i < all_caozuo.Length; i++)
            {
                all_caozuo[i] = pack.itemList[i].rewardList[0].reward;
            }
        }
        IEnumerator T_startZhuanpan()
        {
            float speed = 0f;
            int turn = 0;
            //加速
            while (turn < 1)
            {
                for (int i = 0; i < obj_lights.Length; i++)
                {
                    ChangeLight(i);
                    yield return new WaitForSeconds(0.15f - (normalSpeed * speed));
                    speed += 1f / (obj_lights.Length * 1);
                }
                turn++;
            }

            //voice_player.Stop();
            //voice_player.clip = Resources.Load<AudioClip>("BGM/Zhuaning");
            //voice_player.loop = true;
            //voice_player.Play();


            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < obj_lights.Length; j++)
                {
                    ChangeLight(j);
                    yield return new WaitForSeconds(normalSpeed);
                }
            }
            yield return TurnStay();
            NetMessage.OseeLobby.Req_TurnTableAllRequest();
            NetMessage.OseeLobby.Req_TurnTableUserRequest(PlayerData.PlayerId);
        }

        IEnumerator TurnStay()
        {
            float speed = 1.0f;//转盘速度
            int turnCount = 2;//转盘圈数
            int pointer = 0;//转盘当前所转圈数

            while (true)
            {
                for (int i = 0; i < obj_lights.Length; i++)
                {
                    ChangeLight(i);
                    yield return new WaitForSeconds(normalSpeed * speed);
                    speed += 0.2f;
                    if ((i == _index - 1) && (pointer == turnCount))
                    {
                        _T_StartZhuan = null;
                        view_award.SetActive(true);
                        // img_award1.sprite = Resources.Load<Sprite>(string.Format("zhuanpan/{0}_1",_index));
                        img_award1.text = all_caozuo[_index - 1];// Resources.Load<Sprite>(string.Format("item/{0}", _index));
                        img_award1.SetNativeSize();
                        //img_award2.sprite = Resources.Load<Sprite>(string.Format("zhuanpan/{0}_2", _index));
                        img_award2.sprite = Resources.Load<Sprite>(string.Format("zhuanpan/{0}", _index));
                        img_award2.SetNativeSize();
                        yield break;
                    }
                }
                pointer++;
                //if (pointer == turnCount)
                //{
                //    //最后一圈
                //    voice_player.Stop();
                //    voice_player.clip = Resources.Load<AudioClip>("BGM/stopZhuan");
                //    voice_player.loop = false;
                //    voice_player.Play();
                //}
            }

        }

        void ChangeLight(int nIndex)
        {
            for (int i = 0; i < obj_lights.Length; i++)
            {
                if (nIndex == i)
                {
                    obj_lights[i].SetActive(true);
                }
                else
                {
                    obj_lights[i].SetActive(false);
                }
            }
            nIndex++;
            if (nIndex >= obj_lights.Length)
                nIndex = 0;
        }
    }
}