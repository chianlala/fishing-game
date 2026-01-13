using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using CoreGame;
namespace Game.UI
{
    public class UIMessageBoxEx : MonoBehaviour
    {
        public Text txt_title;
        public Text txt_text;
        public Button btn_ok;
        public Button btn_cancel;
        public Button btn_close;

        Action pressOkHandler;
        Action pressCancelHandler;
        Action PressCloseHandler;

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
            btn_cancel = transform.Find("bg/Button_Cancel").GetComponent<Button>(); 
        }
        // Use this for initialization
        void Start()
        {
            btn_ok.onClick.AddListener(OnClickOk);
            btn_cancel.onClick.AddListener(OnClickCancel);
            btn_close.onClick.AddListener(OnClickClose);
        }

        void OnClickOk()
        {
            UIMgr.CloseUI(UIPath.UIMessageBoxEx);

            if (pressOkHandler != null)
                pressOkHandler();
        }
        void OnClickCancel()
        {
            UIMgr.CloseUI(UIPath.UIMessageBoxEx);

            if (pressCancelHandler != null)
                pressCancelHandler();
        }
        private void OnClickClose()
        {
            UIMgr.CloseUI(UIPath.UIMessageBoxEx);

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
        public void SetCancelHandler(Action handler)
        {
            pressCancelHandler = handler;
        }
        public void SetCloseHandler(Action callback)
        {
            PressCloseHandler = callback;
        }
    }
}