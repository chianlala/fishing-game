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
    //冰霜巨龙
    public class IceDragonPath : MonoBehaviour
    {
        public Transform FishView;
        public Animation Ani;
        float beforeTime = 0f;
        //是否加载了 用来播放背景的
        public bool bInit = false;

        public List<string> AllAni = new List<string>() {
            "idle",
            "idle_2", "idle_2", "idle_2", "idle_2", "idle_2", "idle_2", "idle_2",
            "idle_2", "idle_2", "idle_2", "idle_2", "idle_2", "idle_2", "idle_2",
            "idle_2", "idle_2", "flyaway" };
        //public List<string> AllAni = new List<string>() {
        //     "idle_2", "flyaway" };
        void Awake()
        {
            Ani = this.transform.Find("FishView/bs_0013/bs_0013").GetComponent<Animation>();
            FishView = this.transform.Find("FishView");
        }

        int targetI = -1;
        public void SetfLifeTime(float varfLifeTime)
        {
            float tmpTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                tmpTime += Ani[AllAni[i]].length;
            }
            Debug.Log("IceDragonPath" + tmpTime);
            // *****************
            targetI = -1;
            //跳转到对应时间的动画
            float mfLifeTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                if (mfLifeTime >= varfLifeTime)
                {
                    targetI = i;
                    Ani.Play(AllAni[targetI]);
                    //等于大于的时间
                    Ani[AllAni[targetI]].time = mfLifeTime - varfLifeTime;
                    if (Ani[AllAni[targetI]].time == 0f)
                    {
                        Ani[AllAni[targetI]].time = 0.01f;
                    }
                    break;
                }
                //时间累加
                mfLifeTime += Ani[AllAni[i]].length;
            }
           
            if (targetI == -1)
            {
                //没找到说明时间时间动画已经播完
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
        void ChangeTimeAni()
        {
            if (targetI >= AllAni.Count)
            {
                //说明时间时间动画已经播完
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
                return;
            }

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
                Ani[AllAni[targetI]].time = 0.001f;
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
