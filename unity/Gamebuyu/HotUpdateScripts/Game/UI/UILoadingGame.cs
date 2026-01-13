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
    public class UILoadingGame : MonoBehaviour 
    {
        public Image img_bg; //背景
        public Slider slider_process;//进度条
        public Text txt_tips; //文字提示 

        //需加载的场景
        public string m_sence = "ZBuyuRoom";


        public SceneAssetRequest m_AsyncOperation;
        //[HideInInspector]
        //public AsyncOperation m_AsyncOperation;
        [HideInInspector]
        public AssetRequest m_ResourceRequest;
        [HideInInspector]
        public AssetRequest m_LoadBgRequest;
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
            // m_AsyncOperation = Assets.LoadSceneAsync(varSceneName, true);                                                                         
            m_AsyncOperation = Assets.LoadSceneAsync("ZBuyuRoom.unity", true);
            Debug.Log("开始加载场景");
            if (UIMgr.IsContains(varloadUI)==false)//不存在 则开始加载
            {
                m_nextUI = varloadUI;
                m_ResourceRequest = common4.LoadAssetPrefab(varloadUI.name);
                LoadBg();
            }
            else
            {
                m_nextUI = varloadUI;
                m_ResourceRequest = null;
                LoadBg();
            }
        }
        void LoadBg() {
            m_LoadBgRequest = common4.LoadAssetPrefab("SceneBg/Bg/" + ByData.nModule);
        }
        private void Update()
        {
            if (m_ResourceRequest==null)
            {
                if (m_AsyncOperation != null && m_LoadBgRequest != null)
                {
                    if (m_AsyncOperation.isDone && m_LoadBgRequest.isDone)
                    {
                        //代表完成
                        Debug.Log("isDone");

                        SetProgress(1);
                        //告诉Root3D可以查找物体了
                        common2.BulletPos = GameObject.Find("BulletPos").transform;
                        //common2.YuSheFish = GameObject.Find("YuSheFish").transform;
                        Root3D.Instance.loadBuyuRoom(true);

                        common2.base_BG = GameObject.Find("base_BG").transform;
                    
                        var varUIGoBg = (GameObject)m_LoadBgRequest.asset;
                        var mm = Instantiate(varUIGoBg, common2.base_BG);
                        mm.transform.localScale = Vector3.one;
                        mm.transform.localPosition = Vector3.zero;

                        m_AsyncOperation = null;
                        //UIMgr.CloseUI(UIPath.UILoadingGame);

                        //if (m_nextUI.name == UIPath.UIByRoomMain.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIChangeHall);
                        //}
                        //if (m_nextUI.name == UIPath.UIByGrandPrix.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIBigAwardHall);
                        //}
                        //if (m_nextUI.name == UIPath.UIByChange.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIBuyuMenu);
                        //}
                        UIMgr.CloseAllwithOut(UIPath.UILoadingGame);
                        UIMgr.ShowUI(m_nextUI);
                        //此时可以分发消息了
                        ControllerMgr.Instance.WaitForLoading = false;
                    }
                    else
                    {
                        SetProgress((m_AsyncOperation.progress + m_LoadBgRequest.progress) * 0.333f);
                    }
                }
                //if (m_LoadBgRequest != null)
                //{
                //    if ( m_LoadBgRequest.isDone)
                //    {
                //        //代表完成
                //        Debug.Log("isDone");

                //        SetProgress(1);
                //        //告诉Root3D可以查找物体了
                //        common2.BulletPos = GameObject.Find("BulletPos").transform;
                //        common2.YuSheFish = GameObject.Find("YuSheFish").transform;
                //        Root3D.Instance.loadBuyuRoom(true);

                //        common2.base_BG = GameObject.Find("base_BG").transform;
                //        var varUIGoBg = (GameObject)m_LoadBgRequest.asset;
                //        var mm = Instantiate(varUIGoBg, common2.base_BG);
                //        mm.transform.localScale = Vector3.one;
                //        mm.transform.localPosition = Vector3.zero;
                //        UIMgr.CloseUI(UIPath.UILoadingGame);

                //        if (m_nextUI.name == UIPath.UIByRoomMain.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIChangeHall);
                //        }
                //        if (m_nextUI.name == UIPath.UIByGrandPrix.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIBigAwardHall);
                //        }
                //        if (m_nextUI.name == UIPath.UIByChange.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIBuyuMenu);
                //        }
                //        UIMgr.ShowUI(m_nextUI);
                //        //此时可以分发消息了
                //        ControllerMgr.Instance.WaitForLoading = false;
                //    }
                //    else
                //    {
                //        SetProgress(( m_LoadBgRequest.progress) * 0.333f);
                //    }
                //}
            }
            else
            {
                if (m_AsyncOperation != null && m_ResourceRequest != null && m_LoadBgRequest != null)
                {
                    if (m_AsyncOperation.isDone && m_ResourceRequest.isDone && m_LoadBgRequest.isDone)
                    {
                        //代表完成
                        Debug.Log("isDone");

                        SetProgress(1);
                        //告诉Root3D可以查找物体了
                        common2.BulletPos = GameObject.Find("BulletPos").transform;
                        Root3D.Instance.loadBuyuRoom(true);

                        common2.base_BG = GameObject.Find("base_BG").transform;
                       
                        var varUIGoBg = (GameObject)m_LoadBgRequest.asset;
                        var mm = Instantiate(varUIGoBg, common2.base_BG);
                        mm.transform.localScale = Vector3.one;
                        mm.transform.localPosition = Vector3.zero;

                        m_AsyncOperation = null;
                        //UIMgr.CloseUI(UIPath.UILoadingGame);

                        //if (m_nextUI.name == UIPath.UIByRoomMain.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIChangeHall);
                        //}
                        //if (m_nextUI.name == UIPath.UIByGrandPrix.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIBigAwardHall);
                        //}
                        //if (m_nextUI.name == UIPath.UIByChange.name)
                        //{
                        //    UIMgr.CloseUI(UIPath.UIBuyuMenu);
                        //}
                        UIMgr.CloseAllwithOut(UIPath.UILoadingGame);
                        varUIGo = (GameObject)m_ResourceRequest.asset;
                        UIMgr.ShowUI(m_nextUI, varUIGo);

                        //此时可以分发消息了
                        ControllerMgr.Instance.WaitForLoading = false;
                    }
                    else
                    {
                        SetProgress((m_AsyncOperation.progress + m_ResourceRequest.progress + m_LoadBgRequest.progress) * 0.333f);
                    }
                }

                //if ( m_ResourceRequest != null && m_LoadBgRequest != null)
                //{
                //    if (m_ResourceRequest.isDone && m_LoadBgRequest.isDone)
                //    {
                //        //代表完成
                //        Debug.Log("isDone");

                //        SetProgress(1);
                //        //告诉Root3D可以查找物体了
                //        common2.BulletPos = GameObject.Find("BulletPos").transform;
                //        Root3D.Instance.loadBuyuRoom(true);

                //        common2.base_BG = GameObject.Find("base_BG").transform;
                //        var varUIGoBg = (GameObject)m_LoadBgRequest.asset;
                //        var mm = Instantiate(varUIGoBg, common2.base_BG);
                //        mm.transform.localScale = Vector3.one;
                //        mm.transform.localPosition = Vector3.zero;

                        
                //        UIMgr.CloseUI(UIPath.UILoadingGame);

                //        if (m_nextUI.name == UIPath.UIByRoomMain.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIChangeHall);
                //        }
                //        if (m_nextUI.name == UIPath.UIByGrandPrix.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIBigAwardHall);
                //        }
                //        if (m_nextUI.name == UIPath.UIByChange.name)
                //        {
                //            UIMgr.CloseUI(UIPath.UIBuyuMenu);
                //        }
                //        varUIGo = (GameObject)m_ResourceRequest.asset;
                //        UIMgr.ShowUI(m_nextUI, varUIGo);

                //        //此时可以分发消息了
                //        ControllerMgr.Instance.WaitForLoading = false;
                //    }
                //    else
                //    {
                //        SetProgress(( m_ResourceRequest.progress + m_LoadBgRequest.progress) * 0.333f);
                //    }
                //}
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