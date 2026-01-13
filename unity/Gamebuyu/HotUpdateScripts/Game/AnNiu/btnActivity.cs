using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class btnActivity : MonoBehaviour
    {
        public Button Btn_this;
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UI_HuoDong);
            });
        }
    }
}