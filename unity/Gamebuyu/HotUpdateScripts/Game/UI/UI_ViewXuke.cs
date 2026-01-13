using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UI_ViewXuke : MonoBehaviour
    {
        //***安全中心许可
        //public Transform tras_AccountXuKe;
        public Toggle tog_agreeAccountXuKe;
        public Button btn_AccountXuKe;
        public Button btn_AccountXuKe_Close;
        void FindCompent()
        {
            tog_agreeAccountXuKe = this.transform.Find("bg/Toggle").GetComponent<Toggle>();
            btn_AccountXuKe = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            btn_AccountXuKe_Close = this.transform.Find("bg/btn_close").GetComponent<Button>();
        }
        void Awake()
        {
            FindCompent();
            btn_AccountXuKe.onClick.AddListener(() =>
            {
                if (tog_agreeAccountXuKe.isOn)
                {
                    //tras_AccountXuKe.gameObject.SetActive(false);
                    UIMgr.CloseUI(UIPath.UI_ViewXuke);
                    UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>().SetPanel(UIAccountPanel.BangDingIDNumber);
                    //UIMgr.ShowUI(UIPath.UIAccount).GetComponent<UIAccount>().SetPanel(UIAccountPanel.BangDingIDNumber);
                }
                else
                {
                    MessageBox.Show("请先同意用户许可");
                }
            });
            btn_AccountXuKe_Close.onClick.AddListener(() =>
            {
            //tras_AccountXuKe.gameObject.SetActive(false);
            UIMgr.CloseUI(UIPath.UI_ViewXuke);
            });
        }
    }
}
