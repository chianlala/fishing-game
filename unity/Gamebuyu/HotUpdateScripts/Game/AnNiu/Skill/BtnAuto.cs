
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class BtnAuto : MonoBehaviour
    {
        public Button Btn_this;
        public Transform State1;
        public Transform State2; 
        void Awake() {
            Btn_this = this.transform.Find("Button").GetComponent<Button>();
            State1 = this.transform.Find("State1");
            State2 = this.transform.Find("State2");
            Btn_this.onClick.AddListener(() =>
            {
                if (PlayerData._bZiDong == false)
                {
                    if (PlayerData.DragonCrystal <= 0)
                    {
                        MessageBox.ShowPopOneMessage("金币不足!");
                        return;
                    }
                    PlayerData._bZiDong = true;
                    PlayerData.SetRootbZiDong(true);
                    State1.gameObject.SetActive(true);
                    State2.gameObject.SetActive(false);
                }
                else
                {
                    PlayerData._bZiDong = false;
                    PlayerData.SetRootbZiDong(false);
                    State1.gameObject.SetActive(false);
                    State2.gameObject.SetActive(true);
                }
            });
            EventManager.IsOnAuto += On_IsOnAuto;
        }
        void OnEnable() {
            PlayerData._bZiDong = false;
            PlayerData.SetRootbZiDong(false);
            State1.gameObject.SetActive(false);
            State2.gameObject.SetActive(true);
        }
        void On_IsOnAuto(bool bl) {
            //PlayerData._bZiDong = bl;
            //PlayerData.SetRootbZiDong(bl);
            if (bl)
            {
                State1.gameObject.SetActive(true);
                State2.gameObject.SetActive(false);
            }
            else
            {
                State1.gameObject.SetActive(false);
                State2.gameObject.SetActive(true);
            }
        }
        void OnDestroy() {

            EventManager.IsOnAuto -= On_IsOnAuto;
        }
    }
}