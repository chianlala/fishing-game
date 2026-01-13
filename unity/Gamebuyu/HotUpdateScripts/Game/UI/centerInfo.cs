using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class centerInfo : MonoBehaviour
    {
        public Text txt_gold;
        public Text txt_diamond;
        public Text txt_dragoncrash;
        public Text txt_dantou;
        public Button btn_glodadd;
        public Button btn_diamondadd;
        public Button btn_dragoncrashadd;
        public Button btn_dantouadd; 
        void Awake()
        {
            txt_gold = transform.Find("bg_gold/txt_gold").GetComponent<Text>();
            txt_diamond = transform.Find("bg_dimond/txt_dimond").GetComponent<Text>();
            txt_dragoncrash = transform.Find("bg_dragoncrystal/txt_dragoncrystal").GetComponent<Text>();
            txt_dantou = transform.Find("bg_dantou/txt_dantou").GetComponent<Text>();

            btn_glodadd = transform.Find("bg_gold/jia").GetComponent<Button>();
            btn_diamondadd = transform.Find("bg_dimond/jia").GetComponent<Button>();
            btn_dragoncrashadd = transform.Find("bg_dragoncrystal/jia").GetComponent<Button>();
            btn_dantouadd = transform.Find("bg_dantou/jia").GetComponent<Button>();
            btn_glodadd.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIShop);
                // vartmp.Setpanel(1);
            });
            btn_diamondadd.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIShop);
                // vartmp.Setpanel(4);
            });
            btn_dragoncrashadd.onClick.AddListener(() =>
            {
                var goShop = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                goShop.Setpanel(JieMain.金币商城);
            });
            btn_dantouadd.onClick.AddListener(() =>
            {
                var goShop = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                goShop.Setpanel(JieMain.核弹商城);
            });
            txt_gold.text = PlayerData.Gold.ToString();
            txt_diamond.text = PlayerData.Diamond.ToString();
            txt_dragoncrash.text = PlayerData.DragonCrystal.ToString();
            txt_dantou.text = PlayerData.GoldTorpedo.ToString();
        }
        private void OnEnable()
        {
            txt_gold.text = PlayerData.Gold.ToString();
            txt_diamond.text = PlayerData.Diamond.ToString();
            txt_dragoncrash.text = PlayerData.DragonCrystal.ToString();
            txt_dantou.text = common.myItem[2].ToString();

            EventManager.DiamondUpdate += On_ChangeDiamond;
            EventManager.GoldUpdate+=On_ChangeGold;
            EventManager.DragonCrystalUpdate += On_DragonCrystal;
            EventManager.GoldTorpedoUpdate += fGoldTorpedoUpdate;
        }
        private void OnDisable()
        {
            EventManager.DiamondUpdate -= On_ChangeDiamond;
            EventManager.GoldUpdate -= On_ChangeGold;
            EventManager.DragonCrystalUpdate -= On_DragonCrystal;
            EventManager.GoldTorpedoUpdate -= fGoldTorpedoUpdate;
        }
        void fGoldTorpedoUpdate(long num) {

            txt_dantou.text = num.ToString();
        }
        void On_DragonCrystal(long num)
        {
            txt_dragoncrash.text = num.ToString();
        } 
        void On_ChangeGold(long num)
        {
            txt_gold.text = num.ToString();
        }
        
        void On_ChangeDiamond(long num)
        {
            txt_diamond.text = num.ToString();
        }
    }
}