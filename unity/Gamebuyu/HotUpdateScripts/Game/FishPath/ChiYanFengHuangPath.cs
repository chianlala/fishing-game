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
     
    public class ChiYanFengHuangPath : MonoBehaviour
    {
        public Transform FishView;
        public Animation Ani;
        float beforeTime = 0f;
        //是否加载了 用来播放背景的
        public bool bInit = false;

        public List<string> AllAni = new List<string>() { "idle", "idle", "idle", "idle", "idle", "idle", "idle", "idle", "idle"};
        void Awake()
        {
            Ani = this.transform.Find("FishView/bs_0010/bs_0010").GetComponent<Animation>();
            FishView = this.transform.Find("FishView");
        }
  
        int targetI=-1;
        public void SetfLifeTime(float varfLifeTime) {
            float tmpTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                tmpTime += Ani[AllAni[i]].length;
            }
            Debug.Log("ChiYanFengHuangPath" + tmpTime);
            //*****************
            targetI = -1;
            //跳转到对应时间的动画
            float mfLifeTime = 0;
            if (varfLifeTime<=0)
            {
                SoundLoadPlay.PlaySound("sd_t2_phoenix_moving");
            }
            for (int i = 0; i < AllAni.Count; i++)
            {
                
                if (mfLifeTime >= varfLifeTime)
                {
                    targetI = i;

                    if (targetI%2==0)
                    {
                        //旋转
                        if (PlayerData.IsRotateGame)//旋屏了
                        {
                            FishView.transform.localEulerAngles = new Vector3(0f,0f,180f);
                        }
                        else
                        {
                            FishView.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                        }
                    }
                    else
                    {
                        //旋转
                        if (PlayerData.IsRotateGame)//旋屏了
                        {
                            FishView.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                        }
                        else
                        {
                            FishView.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                        }
                    }
                    Ani.Play(AllAni[targetI]);
                    Ani[AllAni[targetI]].time = mfLifeTime - varfLifeTime;
                    if (Ani[AllAni[targetI]].time == 0)
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
        void ChangeTimeAni() {
            if (targetI >= AllAni.Count)
            {
                //说明时间时间动画已经播完
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
                return;
            }
         
            if (Ani[AllAni[targetI]].time >=Ani[AllAni[targetI]].length|| Ani[AllAni[targetI]].time == 0)
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
                if (targetI % 2 == 0)
                {
                    //旋转
                    if (PlayerData.IsRotateGame)//旋屏了
                    {
                        FishView.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                    }
                    else
                    {
                        FishView.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                    }
                }
                else
                {
                    //旋转
                    if (PlayerData.IsRotateGame)//旋屏了
                    {
                        FishView.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                    }
                    else
                    {
                        FishView.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                    }
                }
                Ani.Play(AllAni[targetI]);
                Ani[AllAni[targetI]].time = 0.001f;
                SoundLoadPlay.PlaySound("sd_t2_phoenix_moving");
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
