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
     
    public class TieShanGongZhuPath : MonoBehaviour
    {
        public Transform FishView;
        public Animation Ani;


        //动画名
        public List<string> AllAni = new List<string>() { 
            "idle", "idle_2", "attack_1", "attack_2", 
            "idle", "idle", "idle", "idle", "idle",
            "idle", "idle_2", "idle_2", "idle_2",
            "idle_2", "idle", "idle", "idle_2" };
        void Awake()
        {
            Ani = this.transform.Find("FishView/gny_0038_idle/mdl_model1_gny_0038_skin").GetComponent<Animation>();
            FishView = this.transform.Find("FishView");
        }
        int targetI = -1;
        public void SetfLifeTime(float varfLifeTime)
        {
            Debug.Log(".Count"+AllAni.Count);
   
            //float tmpTime = 0;
            //for (int i = 0; i < AllAni.Count; i++)
            //{
            //    tmpTime += Ani[AllAni[i]].length;
            //}
            //Debug.Log("TieShanGongZhuPath" + tmpTime);
            targetI = -1;
            //跳转到对应时间的动画
            float mfLifeTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                if (mfLifeTime >= varfLifeTime)
                {
                    targetI = i;

                    Ani.Play(AllAni[targetI]);
                    Ani[AllAni[targetI]].time = mfLifeTime - varfLifeTime;
                    if (Ani[AllAni[targetI]].time == 0)
                    {
                        Ani[AllAni[targetI]].time = 0.01f;
                    }
                    break;
                }
                mfLifeTime += Ani[AllAni[i]].length;
            }
            if (targetI == -1)
            {
                ////说明时间时间动画已经播完
                //this.gameObject.SetActive(false);
                //Destroy(this.gameObject);
                Ani.Play(AllAni[0]);
            }
        }
        public void SetfAniSpeed(float speed)
        {
            try
            {
                Ani[AllAni[targetI]].speed = speed;
            }
            catch
            {

            }
        }
        void ChangeTimeAni()
        {
            if (targetI >= AllAni.Count)
            {
                ////说明时间时间动画已经播完
                //this.gameObject.SetActive(false);
                //Destroy(this.gameObject);
                return;
            }
       
            if (Ani[AllAni[targetI]].time >= Ani[AllAni[targetI]].length || Ani[AllAni[targetI]].time == 0)
            {
                //加1后继续判断
                targetI++;
                if (targetI >= AllAni.Count)
                {
                    ////说明时间时间动画已经播完
                    //this.gameObject.SetActive(false);
                    //Destroy(this.gameObject);
                    return;
                }
                if (AllAni[targetI]== "idle_2")
                {
                    SoundLoadPlay.PlaySound("sd_t2_tie_comingC");
                }
                Ani.Play(AllAni[targetI]);
                Ani[AllAni[targetI]].time = 0.01f;
                //Ani[AllAni[targetI]].time = 0.01f;
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
