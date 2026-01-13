using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class BtnChangeWeapon : MonoBehaviour
    {
        public Button Btn_this;
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIChangePaoTai);
                //UIMgr.ShowUI(UIPath.UIChangePao); 

            });
        } 
    }
}