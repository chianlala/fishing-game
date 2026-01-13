
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class BtnOpenShopZs : MonoBehaviour
    {
        public Button Btn_this;  
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() => 
            {
                var goShop = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                goShop.Setpanel(JieMain.钻石商城);
            });
        } 
    }
}