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

    public class JxJinGangMovePath : MonoBehaviour
    {
        //走路为 35秒 
        public float SetLifeTime = 0; //存活时间决定播放到哪里
        public float m_fLifeTime = 0; //当前生存时间
        public bool bInit = false;
        public bool bIce = false;
        float beforeTime = 0f;
        public Transform FishView;

        public Transform Child1;
        public Transform Child2;

        public Animation Ani1;
        public Animation Ani2;

        //down  idle_1  idle_2  idle_3  idle_1  idle_2  idle_3 walk  idle_4 stay_out  jump  jump_out

        //"stay_out"  "jump_out"
        public List<string> AllAni = new List<string>() {
            "down",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3", 
            "walk", "walk", "walk", "walk","jump_out", "stay_out", "stay_out", "stay_out",

            "down",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3",
            "walk", "walk", "walk", "walk","jump_out", "stay_out", "stay_out", "stay_out",

            "down",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3",
            "idle", "idle", "idle", "idle_2",
            "idle", "idle", "idle", "idle_3",
            "idle", "idle", "idle_4", "stay_out","stay_out","stay_out"};
        void Awake()
        {
            Child1 = this.transform.Find("FishView/bs_0025_idle/Child1");
            Child2 = this.transform.Find("FishView/bs_0025_idle/Child2");
            Ani1 = this.transform.Find("FishView/bs_0025_idle/Child1/mdl_model1_bs_0025_idle").GetComponent<Animation>();
            Ani2 = this.transform.Find("FishView/bs_0025_idle/Child2/TweenScale/mdl_model1_bs_0025_walk").GetComponent<Animation>();
            FishView = this.transform.Find("FishView");
        }
  
        int targetI=-1;
        public void SetfLifeTime(float varfLifeTime) {
            //float tmpTime = 0;
            //for (int i = 0; i < AllAni.Count; i++)
            //{
            //    if (Ani1.GetClip(AllAni[i]) != null)
            //    {
            //        tmpTime += Ani1[AllAni[i]].length;
            //    }
            //    else
            //    {
            //        tmpTime += Ani2[AllAni[i]].length;
            //    }
            //}
            //Debug.Log("JxJinGangMovePath" + tmpTime);
            //*****************
            beforeTime = 0f;
            targetI = -1;
            //跳转到对应时间的动画
            float mfLifeTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                if (Ani1.GetClip(AllAni[i]) !=null)
                {
                    if (mfLifeTime >= varfLifeTime)
                    {
                        ChildGo = true;
                        targetI = i;
                        if (AllAni[targetI]== "down")
                        {
                            SoundLoadPlay.PlaySound("sd_t1_JXJG_luodi");
                        }
                        Ani1.Play(AllAni[targetI]);
                        Ani1[AllAni[targetI]].time = mfLifeTime + varfLifeTime;
                        if (Ani1[AllAni[targetI]].time == 0)
                        {
                            Ani1[AllAni[targetI]].time = 0.01f;
                        }

                        break;
                    }
                    mfLifeTime += Ani1[AllAni[i]].length;
                }
                else
                {
                    if (mfLifeTime >= varfLifeTime)
                    {
                        ChildGo = false;
                        targetI = i;
                        Ani2.Play(AllAni[targetI]);
                        Ani2[AllAni[targetI]].time = mfLifeTime - varfLifeTime;
                        if (Ani2[AllAni[targetI]].time == 0)
                        {
                            Ani2[AllAni[targetI]].time = 0.01f;
                        }
                        break;
                    }
                    mfLifeTime += Ani2[AllAni[i]].length;
                }
            }
            if (targetI==-1)
            {
                //说明时间时间动画已经播完
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
            }
        }
        public void SetfAniSpeed(float speed)
        {
            try
            {
                if (Ani1.GetClip(AllAni[targetI]) != null)//对象1
                {
                    Ani1[AllAni[targetI]].speed = speed;
                }
                else //对象2
                {
                    Ani2[AllAni[targetI]].speed = speed;
                }
            }
            catch
            {

            }
        }
    
        void ChangeTimeAni() {
            if (targetI >= AllAni.Count)
            {
                //说明时间时间动画已经播完
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
                return;
            }
            if (Ani1.GetClip(AllAni[targetI]) != null)//对象1
            {
                if (Ani1[AllAni[targetI]].time >= Ani1[AllAni[targetI]].length ||Ani1[AllAni[targetI]].time == 0)
                {
                    ChildGo = true;
                    //加1后继续判断
                    targetI++;
                    if (targetI >= AllAni.Count)
                    {
                        //结束
                        //说明时间时间动画已经播完
                        this.gameObject.SetActive(false);
                        Destroy(this.gameObject);
                        return;
                    }
                    if (AllAni[targetI] == "idle_2")
                    {
                        SoundLoadPlay.PlaySound("sd_t1_JXJG_chuixiong");
                    }
                    if (AllAni[targetI] == "idle_3")
                    {
                        SoundLoadPlay.PlaySound("sd_t1_JXJG_chuizongji");
                    }
                    if (Ani1.GetClip(AllAni[targetI]) != null)//对象1
                    {
                        ChildGo = true;
                        Ani1.Play(AllAni[targetI]);
                        //Ani1[AllAni[targetI]].time = 0.0001f;
                    }
                    else
                    {
                        ChildGo = false;
                        beforeTime = 0f;
                        Ani2.Play(AllAni[targetI]);
                        //Ani2[AllAni[targetI]].time = 0.0001f;
                    }
                }
            }
            else //对象2
            {
                if (Ani2[AllAni[targetI]].time > 0)
                {
                    //大于0赋值
                    beforeTime = Ani2[AllAni[targetI]].time;
                }
                if (Ani2[AllAni[targetI]].time >= Ani2[AllAni[targetI]].length || Ani2[AllAni[targetI]].time == 0)
                {
                    //加1后继续判断
                    targetI++;
                    if (AllAni[targetI] == "walk")
                    {
                        SoundLoadPlay.PlaySound("sd_t1_JXJG_panpa");
                    }
                    if (targetI >= AllAni.Count)
                    {
                        //结束
                        //说明时间时间动画已经播完
                        this.gameObject.SetActive(false);
                        Destroy(this.gameObject);
                    }
                    if (Ani1.GetClip(AllAni[targetI]) != null)//对象1
                    {
                        ChildGo = true;
                        Ani1.Play(AllAni[targetI]);
                        Ani1[AllAni[targetI]].time = 0.0001f;
                    }
                    else
                    {
                        ChildGo = false;
                        Ani2.Play(AllAni[targetI]);
                        Ani2[AllAni[targetI]].time = 0.0001f;
                    }
                     beforeTime = 0f;
                }
            }
            SetZhaungTai();
        }
        bool ChildGo= true;
      
        bool tmpJilv=true;
        void SetZhaungTai() {
            if (tmpJilv!=ChildGo)
            {
                tmpJilv = ChildGo;
                if (tmpJilv) //为true为 child1打开
                {
                    Child1.gameObject.SetActive(true);
                    Child2.gameObject.SetActive(false);
                }
                else
                {
                    Child1.gameObject.SetActive(false);
                    Child2.gameObject.SetActive(true);
                }
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
