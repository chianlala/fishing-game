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
     
    public class LongWangPath : MonoBehaviour
    { 
        //是否是左边出来的
        public bool bIsleft;
        public Transform FishView; 
        public Animation Ani;

        public List<string> AllAni;  
        public List<string> AllAniLeft = new List<string>() { "idle", "idle_R", "idle", "idle_R", "idle", "idle_R" };
        public List<string> AllAniRight = new List<string>() { "idle_R", "idle", "idle_R", "idle", "idle_R", "idle" };
        void Awake() 
        {
            var tmp = this.transform.Find("FishView/bs_0007/bs_0007");
            if (tmp!=null)
            {
                Ani = this.transform.Find("FishView/bs_0007/bs_0007").GetComponent<Animation>();
            }
            else
            {
                Ani = this.transform.Find("FishView/bs_0006/bs_0006").GetComponent<Animation>();
            }
         
            FishView = this.transform.Find("FishView");
        }
  
        int targetI=-1;
        public void SetfLifeTime(bool isleft, float varfLifeTime) {

            //float tmpTime = 0;
            //for (int i = 0; i < AllAni.Count; i++)
            //{
            //    tmpTime += Ani[AllAni[i]].length;
            //}
            //Debug.Log("LongWangPath" + tmpTime);
            //*********************
            targetI = -1;
            bIsleft = isleft;
            //跳转到对应时间的动画
            float mfLifeTime = 0;
            if (bIsleft)
            {
                AllAni = AllAniLeft;
            }
            else
            {
                AllAni = AllAniRight;
            }
            //播放
            for (int i = 0; i < AllAni.Count; i++)
            {
                if (mfLifeTime >= varfLifeTime)
                {
                    targetI = i;
                    Ani.Play(AllAni[targetI]);
                    Ani[AllAni[targetI]].time = mfLifeTime - varfLifeTime;
                    if (Ani[AllAni[targetI]].time == 0f)
                    {
                        Ani[AllAni[targetI]].time = 0.001f;
                    }
                    break;
                }
                mfLifeTime += Ani[AllAni[i]].length;
            }

            if (targetI == -1)
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
                Ani[AllAni[targetI]].speed = speed;
            }
            catch
            {

            }
        }
        float beforeTime = 0f;
        void ChangeTimeAni() {
            if (AllAni==null)
            {
                return;
            }
            if (targetI >= AllAni.Count)
            {
                //说明时间时间动画已经播完
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
                return;
            }
            if (Ani[AllAni[targetI]].speed > 0)
            {
                if (Ani[AllAni[targetI]].time >= Ani[AllAni[targetI]].length || Ani[AllAni[targetI]].time == 0)
                {
                    //加1后继续判断
                    targetI++;
                    if (targetI >= AllAni.Count)
                    {
                        //说明时间时间动画已经播完
                        this.gameObject.SetActive(false);
                        Destroy(this.gameObject);
                        return;
                    }
                    Ani.Play(AllAni[targetI]);
                    Ani[AllAni[targetI]].time = 0.01f;
                }
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
