
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class BtnFishInfo : MonoBehaviour
    {
        public Button Btn_this;  
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIFishInfo);
            });
        }
    }
}