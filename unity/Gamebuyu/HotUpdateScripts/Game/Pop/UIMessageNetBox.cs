using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using CoreGame;
namespace Game.UI
{
    public class UIMessageNetBox : MonoBehaviour
    {
        public Text txt_title;
        public Text txt_text;
        public Button btn_ok;
        public Button btn_close;

        Action pressOkHandler;
        Action PressCloseHandler;

        [HideInInspector]
        public float fLiveTime = -1f;

        public string Title
        {
            get { return txt_title.text; }
            set { txt_title.text = value; }
        }
        public string Text
        {
            get { return txt_text.text; }
            set { txt_text.text = value; }
        }
        void Awake()
        {
            btn_ok = transform.Find("bg/Button_Ok").GetComponent<Button>();
            btn_close = transform.Find("bg/Button_Close").GetComponent<Button>();
            txt_text = transform.Find("bg/bgContent/Text_Content").GetComponent<Text>();
            txt_title = transform.Find("bg/Text").GetComponent<Text>();
        }
        // Use this for initialization
        void Start()
        {
            btn_ok.onClick.AddListener(OnClickOk);
            btn_close.onClick.AddListener(OnClickClose);
        }

        void Update()
        {
            if (fLiveTime > 0)
            {
                fLiveTime -= Time.deltaTime;
                if (fLiveTime <= 0)
                {
                    fLiveTime = -1;
                    UIMgr.CloseUI(UIPath.UIMessageNetBox);
                }
            }
        }
        void OnClickOk()
        {
            UIMgr.CloseUI(UIPath.UIMessageNetBox);

            if (pressOkHandler != null)
                pressOkHandler();
        }
        private void OnClickClose()
        {
            UIMgr.CloseUI(UIPath.UIMessageNetBox);

            if (PressCloseHandler != null)
                PressCloseHandler();
        }

        public void Reset()
        {
            txt_text.text = "";
        }
        public void SetOkHandler(Action callback)
        {
            pressOkHandler = callback;
        }
        public void SetCloseHandler(Action callback)
        {
            PressCloseHandler = callback;
        }
    }
}