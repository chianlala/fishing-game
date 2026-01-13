using com.maple.game.osee.proto.lobby;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using LitJson;
using CoreGame;

namespace Game.UI
{
    public class UIGonggao : MonoBehaviour
    {
        public Button btn_close;
        public Text txt_title;
        public Text txt_content;
        public ContentSizeFitter size_fit;

        public GameObject tog_item;
        public Transform gridContent;
        private GameObjectPool itemPool = new GameObjectPool();


        private void Awake()
        {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            txt_title = this.transform.Find("bg/root_tog/tog_item/txt_title").GetComponent<Text>();
            txt_content = this.transform.Find("bg/Scroll View/Viewport/gridContent/txt_content").GetComponent<Text>();
           // size_fit = this.transform.Find("").GetComponent<ContentSizeFitter>();
            tog_item = this.transform.Find("bg/root_tog/tog_item").gameObject;
            gridContent = this.transform.Find("bg/Scroll View/Viewport/gridContent");

            btn_close.onClick.AddListener(() => UIMgr.CloseUI(UIPath.UIGonggao));
            //GetNotice();
        }

        private void OnDestroy()
        {
        }
        private void OnEnable()
        {
        }
        private void OnDisable()
        {
            EventManager.GonggaoTips?.Invoke(false);
        }
        void GetNotice()
        {

         
        }
    }
}