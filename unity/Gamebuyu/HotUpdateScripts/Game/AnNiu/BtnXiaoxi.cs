
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class BtnXiaoxi : MonoBehaviour
    {
        public Button Btn_this;
        public Transform XiaoXiredPoint; 
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            XiaoXiredPoint  = this.transform.Find("XiaoXiredPoint");
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIXiaoxi);
            });
            EventManager.XiaoNumUpdate += On_XiaoNumUpdate;
        }
        void OnDestory() {
            EventManager.XiaoNumUpdate -= On_XiaoNumUpdate;
        }
        void On_XiaoNumUpdate(int Num)
        {
            if (Num > 0)
            {
                XiaoXiredPoint.gameObject.SetActive(true);
            }
            else
            {
                XiaoXiredPoint.gameObject.SetActive(false);
            }
        }
    }
}