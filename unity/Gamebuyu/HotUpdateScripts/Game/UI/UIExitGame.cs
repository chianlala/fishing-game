using UnityEngine;
using JEngine.Core;
using JEngine.UI;
using com.maple.common.login.proto;
using ProtoBuf;
using com.maple.network.proto;
using System.Security.Cryptography;
using NetLib;
using System;
using com.maple.game.osee.proto;
using UnityEngine.UI;
using CoreGame;
using System.Collections;
using UnityEngine.SceneManagement;
using libx;

namespace Game.UI 
{
    public class UIExitGame : MonoBehaviour 
    {
        public Image img_bg; //背景
        public Slider slider_process;//进度条 
       public Text txt_tips; //文字提示 

        //需移除的场景
        public string m_sence = "ZBuyuRoom";


        public AsyncOperation m_AsyncOperation;
   
        public UIContext m_nextUI; //下一个页面
        public GameObject varUIGo;
        private float f_showProcess = 0;
        int nWaitframe = 0;
        void Awake() {
            slider_process = transform.Find("bg/slider").GetComponent<Slider>();
            txt_tips = transform.Find("bg/Imagetips/txt_tips").GetComponent<Text>();
        }
        void OnEnable()
        {
            f_showProcess = 0;
            nWaitframe = 0;
            SetProgress(0);
  
        }
        public void StartLoad(string varSceneName, UIContext varloadUI)
        {
            m_AsyncOperation = SceneManager.UnloadSceneAsync("ZBuyuRoom");
            m_nextUI = varloadUI;
            Debug.Log("开始移除场景");
            if (m_AsyncOperation==null)
            {
                //代表完成
                Debug.Log("isDone");
                SetProgress(1);
                UIMgr.ShowUI(m_nextUI);
                UIMgr.CloseUI(UIPath.UIExitGame);
            }
        }
      
        private void Update()
        {
                if (m_AsyncOperation != null )
                {
                    if (m_AsyncOperation.isDone)
                    {
                        //代表完成
                        Debug.Log("isDone");
                        SetProgress(1);
                        UIMgr.ShowUI(m_nextUI);
                         UIMgr.CloseUI(UIPath.UIExitGame);
                    }
                    else
                    {
                        SetProgress(m_AsyncOperation.progress);
                    }
                }
        }

        private void UpdateProgress(UEventContext context)
        {
            float num = context.GetData<float>();
            SetProgress(num);
        }

        void SetProgress(float val)
        {
            slider_process.value = val;
            SetText(Mathf.Floor(val * 100) + "%");
        }
        void SetText(string str)
        {
            txt_tips.text = str;
        }
    }
}