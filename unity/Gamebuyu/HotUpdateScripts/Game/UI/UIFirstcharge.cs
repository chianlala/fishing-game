using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIFirstcharge : MonoBehaviour
    {
        public Button btn_close;
        public Button btn_ok;
        void Awake()
        {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_ok = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            btn_close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIFirstcharge);
            });
            btn_ok.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIShop);
                UIMgr.CloseUI(UIPath.UIFirstcharge);
            });
        }


    }
}
