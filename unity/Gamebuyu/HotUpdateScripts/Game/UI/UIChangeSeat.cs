using com.maple.game.osee.proto.fishing;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIChangeSeat : MonoBehaviour
    {
        public Button btn_Close;
        public Sprite green;
        public Sprite red;
        GameObjectPool AllboosRoomPool = new GameObjectPool();
        public Transform Item;
        public Transform ItemParent;
        void OnInitComponent()
        {
            btn_Close = this.transform.Find("btn_close").GetComponent<Button>();
            Item = this.transform.Find("center/Scroll View/Viewport/Content/Item");
            ItemParent = this.transform.Find("center/Scroll View/Viewport/Content");
            green = this.transform.Find("green").GetComponent<Image>().sprite;
            red = this.transform.Find("red").GetComponent<Image>().sprite;
            btn_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIChangeSeat);
            });
            AllboosRoomPool.SetTemplete(Item.gameObject);
        }
        private void OnEnable()
        {
            //UIMgr.CloseUI(UIPath.UIByRoomMain);
            //UIMgr.CloseUI(UIPath.UIByChange);
            //UIMgr.CloseUI(UIPath.UIByGrandPrix);
            foreach (Transform item in ItemParent)
            {
                AllboosRoomPool.Recycle(item.gameObject);
            }
            RefashRoomRoomList();
            CancelInvoke("RefashRoom");
            InvokeRepeating("RefashRoom", 5f, 5f);
        }
        void RefashRoomRoomList()
        {
            NetMessage.OseeFishing.Req_FishingRoomListRequest();
        }
        void RefashRoom()
        {
            RefashRoomRoomList();
        }
        private void OnDisable()
        {
            CancelInvoke("RefashRoom");
        }
        private void Awake()
        {
            OnInitComponent();
            UEventDispatcher.Instance.AddEventListener(UEventName.FishingRoomListResponse, On_FishingRoomListResponse);//获取经典渔场房间列表响应

        }
        /// <summary>
        /// 获取经典渔场房间列表响应
        /// <summary>
        private void On_FishingRoomListResponse(UEventContext obj)
        {
            var pack = obj.GetData<FishingRoomListResponse>();

            foreach (Transform item in ItemParent)
            {
                AllboosRoomPool.Recycle(item.gameObject);
            }

            for (int i = 0; i < pack.roomList.Count; i++)
            {
                var vartmp = AllboosRoomPool.Get(ItemParent);
                vartmp.transform.Find("Idex").GetComponent<Text>().text = pack.roomList[i].roomCode.ToString();
                vartmp.GetComponent<Button>().onClick.RemoveAllListeners();
                vartmp.GetComponent<Button>().onClick.AddListener(() =>
                {
                NetMessage.OseeFishing.Req_FishingJoinRoomByRoomCodeRequest(int.Parse(vartmp.transform.Find("Idex").GetComponent<Text>().text));
                    UIMgr.CloseUI(UIPath.UIChangeSeat);
                });
                vartmp.transform.Find("people").GetComponent<Text>().text = pack.roomList[i].headImg.Count + "/4";
                if (pack.roomList[i].headImg.Count < 4)
                {
                    vartmp.transform.Find("slider/Image").GetComponent<Image>().sprite = green;
                }
                else
                {
                    vartmp.transform.Find("slider/Image").GetComponent<Image>().sprite = red;
                }
                vartmp.transform.Find("slider/Image").GetComponent<Image>().fillAmount = pack.roomList[i].headImg.Count / 4f;
                //if (pack.roomList[i].vip >= 6)
                //{
                //    vartmp.transform.Find("vip").gameObject.SetActive(true);
                //    vartmp.transform.SetAsFirstSibling();
                //}
                //else
                //{
                //    vartmp.transform.Find("vip").gameObject.SetActive(false);
                //}
            }
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.FishingRoomListResponse, On_FishingRoomListResponse);//获取经典渔场房间列表响应
        }
    }
}