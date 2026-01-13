using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIDuihuan : MonoBehaviour
    {
        public Button btn_close, btn_ok;
        public InputField input_cdkey;
        //----------view_ok----------
        public GameObject view_ok;
        public Button btn_okClose;
        public GameObject item;
        public Transform grid;
        GameObjectPool itemPool = new GameObjectPool();

        private void Awake()
        {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_ok = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            input_cdkey = this.transform.Find("bg/input_cdkey").GetComponent<InputField>();
            view_ok = this.transform.Find("view_ok").gameObject;
            btn_okClose = this.transform.Find("view_ok/bg/btn_ok").GetComponent<Button>();
            item = this.transform.Find("view_ok/bg/root_reward/bg_item").gameObject;
            grid = this.transform.Find("view_ok/bg/root_reward");

            itemPool.SetTemplete(item);
            itemPool.Recycle(item);

            btn_close.onClick.AddListener(() => UIMgr.CloseUI(UIPath.UIDuihuan));
            btn_ok.onClick.AddListener(() =>
            {
                if (input_cdkey.text != "")
                    NetMessage.OseeLobby.Req_UseCdkRequest(input_cdkey.text);
            });
            btn_okClose.onClick.AddListener(() => { view_ok.SetActive(false); });

            UEventDispatcher.Instance.AddEventListener(UEventName.UseCdkResponse, On_UseCdkResponse);//使用cdk返回
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UseCdkResponse, On_UseCdkResponse);//使用cdk返回
        }

        private void OnEnable()
        {
            view_ok.SetActive(false);
        }

        /// <summary>
        /// 使用cdk返回
        /// <summary>
        private void On_UseCdkResponse(UEventContext obj)
        {
            var pack = obj.GetData<UseCdkResponse>();
            //清空
            for (int i = 0; i < grid.childCount; i++)
            {
                var go = grid.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }
            view_ok.SetActive(true);
            if (pack.itemData.Count > 0)
            {
                for (int i = 0; i < pack.itemData.Count; i++)
                {
                    var data = pack.itemData[i];
                    var go2 = itemPool.Get();
                    go2.SetActive(true);
                    go2.transform.SetParent(grid, false);
                    go2.transform.Find("img_item").GetComponent<Image>().sprite = common4.LoadSprite("item/" + data.itemId);
                    go2.transform.Find("txt_item").GetComponent<Text>().text = "x" + data.itemNum;
                }
            }
        }
    }

}