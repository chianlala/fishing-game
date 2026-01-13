using CoreGame;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class UISpecialInfo : MonoBehaviour
    {
        public Button btn_close;  
        void Awake() {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UISpecialInfo); });
        } 
    }
}  