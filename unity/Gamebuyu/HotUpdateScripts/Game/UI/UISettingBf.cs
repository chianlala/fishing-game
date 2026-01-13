using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;


namespace Game.UI
{
    public class UISettingBf : MonoBehaviour
    {
        public Slider slider_yinyue;   //背景音乐
        public Slider slider_yinxiao;    //音效
        public Toggle tog_yinxiao;
        public Toggle tog_yinyue;

        public Button btn_exitLogin; 
        public Button btn_close;

        float cacheBgm;
        float cacheSoundEffect;
     
        void Awake()
        {


            slider_yinyue = this.transform.Find("bg/root_yinyue/slider_yinyue").GetComponent<Slider>();
            slider_yinxiao = this.transform.Find("bg/root_yinxiao/slider_yinxiao").GetComponent<Slider>();

            tog_yinxiao = this.transform.Find("bg/root_yinxiao/tog_yinxiao").GetComponent<Toggle>();
            tog_yinyue = this.transform.Find("bg/root_yinyue/tog_yinyue").GetComponent<Toggle>();

            btn_exitLogin = this.transform.Find("bg/btn_exitLogin").GetComponent<Button>();
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();

            slider_yinyue.onValueChanged.AddListener(ChangeBgmValue);
            slider_yinxiao.onValueChanged.AddListener(ChangeSfValue);

            tog_yinxiao.onValueChanged.AddListener(OnSoundEffectValueChanged);
            tog_yinyue.onValueChanged.AddListener(OnBgmValueChanged);

            btn_exitLogin.onClick.AddListener(OnExitPlayer);
            btn_close.onClick.AddListener(CloseUI);
           
            slider_yinyue.value = SoundHelper.BgmVolume;
            slider_yinxiao.value = SoundHelper.GameVolume;
       

        }
        private void OnEnable()
        {
            //mDropdown.value=common.Qualitylv;
            cacheBgm = slider_yinyue.value;
            cacheSoundEffect = slider_yinxiao.value;
            //if (UIMainMenu.instance.gameObject.activeSelf)
            //{
            //    btn_exitLogin.gameObject.SetActive(true);
            //}
            //else
            //{
            //    btn_exitLogin.gameObject.SetActive(false);
            //}
        }
        private void OnDisable()
        {
            cacheBgm = slider_yinyue.value;
            cacheSoundEffect = slider_yinxiao.value;
            //common.Qualitylv= mDropdown.value;
        }

        private void CloseUI()
        {
            SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
            UIMgr.CloseUI(UIPath.UISetting);
        }
        private void HelpUI()
        {
            UIMgr.CloseUI(UIPath.UISetting);
            UIMgr.ShowUI(UIPath.UIHelp);
        }
        private void NewBieTipsUI()
        {
            UIMgr.CloseUI(UIPath.UISetting);
            //UIMgr.ShowUI(UIPath.UIHelp);
           // UIMgr.ShowUI(UIPath.NewbiePrompt);
        }

        public void OnClickOpenGPS()
        {
            SDK.GetGPSstring();
        }
        public void OnExitPlayer()
        {
            MessageBox.ShowConfirm("确认退出登录？", null, () =>
            {
                if (true)
                {

                }
                common.LoginState = 0;

                common3._UIFishingInterface.DestroyThisRoomToLogin();
                
                UIMgr.ShowUI(UIPath.UILogin);
                UIMgr.CloseAllwithOut(UIPath.UILogin);
                NetMgr.Instance.OnZhuxiao();
            });
        }

        public void ChangeBgmValue(float num)
        {
            SoundHelper.BgmVolume = num;
            PlayerPrefs.SetFloat("Bgm", num);
        }
        public void ChangeSfValue(float num)
        {
            SoundHelper.GameVolume = num;
            EventManager.SoundYinXiaoUpdate?.Invoke(SoundHelper.GameVolume);
            PlayerPrefs.SetFloat("Sound", num);
        }
        public void OnSoundEffectValueChanged(bool val)
        {
            if (val == false)
            {
                cacheSoundEffect = SoundHelper.GameVolume;
                ChangeSf(0);
            }
            else
            {
                ChangeSf(cacheSoundEffect);
            }
        }
        public void OnBgmValueChanged(bool val)
        {
            if (val == false)
            {
                cacheBgm = SoundHelper.BgmVolume;
                ChangeBgm(0);
            }
            else
            {
                ChangeBgm(cacheBgm);
            }
        }
        void ChangeBgm(float val)
        {
            SoundHelper.BgmVolume = val;
            slider_yinyue.value = val;
        }
        void ChangeSf(float val)
        {
            SoundHelper.GameVolume = val;
            slider_yinxiao.value = val;
        }

    }
}