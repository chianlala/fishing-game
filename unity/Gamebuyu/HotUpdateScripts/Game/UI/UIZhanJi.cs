using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIZhanJi : MonoBehaviour
    { 
        public Button btn_close;
 
        void FindCompent() {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
        }
        private void Awake()
        {
            FindCompent();
            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIZhanJi); });
        }
    }
}