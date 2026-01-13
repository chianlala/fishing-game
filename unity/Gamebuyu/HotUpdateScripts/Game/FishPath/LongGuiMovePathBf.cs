using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using UnityEngine.Profiling;
using com.maple.game.osee.proto.fishing;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;
using DG.Tweening.Plugins.Options;
using System.Linq;
using UnityEngine.UI;

using System;
using com.maple.game.osee.proto;
using GameFramework;
using CoreGame;


namespace Game.UI
{

    public class LongGuiMovePathBf : MonoBehaviour
    {
        //走路为 35秒
        public float SetLifeTime = 0; //存活时间决定播放到哪里
        public Animation Ani;
        public float m_fLifeTime = 0;//当前生存时间 
        public bool bInit=false;
        public bool bIce=false;
        //鱼游临时路径点
        //private Vector3[] move_path;
        //龙龟不需要 路径操作
        //TweenerCore<Vector3, Path, PathOptions> TC_Path;
        public Transform paidashuimian;
        public Transform FishView;
        void Awake()
        {
            Ani = this.transform.Find("FishView/xb_0003/xb_0003").GetComponent<Animation>();
            paidashuimian = this.transform.Find("paidashuimian");
            FishView = this.transform.Find("FishView");
            AniOverEventTA Bskill = Ani.transform.GetComponent<AniOverEventTA>();
            Bskill.aAction = AniAction;
        }

        void AniAction(string aniname) {
            if (aniname== "stage")
            {
                Ani.Play("idle");
            }
            if (aniname == "douping")
            {
                Root3D.Instance.showShaker(); 
            }
        }
        public void InitPath(TmpFishingFishInfoProto fishState)
        {
        
        }
        public void SetfLifeTime(float mfLifeTime) {
            bInit = true;
            m_fLifeTime = mfLifeTime;
        
        }
        void ChangeAni() {
            //if (m_fLifeTime < 0.1f) //播放boss来袭
            //{
            //    //更换背景
            //    int bgID = common4.dicMoldConfig["熔岩龙龟"].BgID;
            //    if (bgID > 0)
            //    {
            //        GameObject varUIGoBg = common4.LoadPrefab("SceneBg/bg_" + bgID);
            //        var mm = Instantiate(varUIGoBg, common2.base_BG);
            //        mm.transform.localScale = Vector3.one;
            //        mm.transform.localPosition = Vector3.zero;
            //    }
            //    this.transform.GetChild(0).gameObject.SetActive(false);
            //}
            if (m_fLifeTime <= 0.1f)//播放跳出动画
            {
                if (Ani.clip.name != "stage")
                {
                    Ani.Play("stage");
                    Ani["stage"].time = m_fLifeTime - 3f;
                    paidashuimian.gameObject.SetActive(true);
                    FishView.gameObject.SetActive(true);
                }
            }
            else//播放走动动画
            {
                //因为存活时间过了 6秒 所所以用m_fLifeTime-6
                if (Ani.clip.name != "idle")
                {
                    paidashuimian.gameObject.SetActive(false);
                    Ani.Play("idle");
                    Ani["idle"].time = m_fLifeTime - 6f;
                }
            }
        }
        void FixedUpdate()
        {
            //if (bInit == false)
            //    return;
            //if (bIce == true)
            //    return;
            m_fLifeTime += Time.fixedDeltaTime;
            ChangeAni();
        }
    }
}
