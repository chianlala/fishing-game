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
    //龙龟
    public class LongGuiMovePath : MonoBehaviour
    {
        public Transform FishView;
        public Animation Ani;
        public Transform Bip001;
        float beforeTime = 0f;

        //是否加载了 用来播放背景的
        public bool bInit = false;
        public Transform paidashuimian;
        public List<string> AllAni = new List<string>() { "stage", "idle", "idle", "idle", "idle"};
        void Awake()
        {
            Ani = this.transform.Find("FishView/xb_0003/xb_0003").GetComponent<Animation>();

            Bip001 = this.transform.Find("FishView/xb_0003/xb_0003/Bip001");
            FishView = this.transform.Find("FishView");
            paidashuimian = this.transform.Find("paidashuimian");


            AniOverEventTA Bskill = Ani.transform.GetComponent<AniOverEventTA>();
            Bskill.aAction = AniAction;
        }
        void AniAction(string aniname)
        {
            if (aniname == "douping")
            {
                Root3D.Instance.showShaker();
            }
        }
        int targetI = -1;
        public void SetfLifeTime(float varfLifeTime)
        {
            beforeTime = 0;
            float tmpTime = 0;
            for (int i = 0; i < AllAni.Count; i++)
            {
                tmpTime += Ani[AllAni[i]].length;
            }
            Debug.Log("LongGuiMovePathBf" + tmpTime);
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
             
                    if (AllAni[targetI]== "idle")
                    {
                        if (targetI % 2 == 0)
                        {
                            Ani.transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                        }
                        else
                        {
                            Ani.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                        }
                    }
                    else
                    {
                        Ani.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                        if (mfLifeTime == varfLifeTime)
                        {
                            if (AllAni[targetI] == "stage")
                            {
                                SoundLoadPlay.PlaySound("sd_t2_longgui_chuchang");
                            }
                        }
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
            catch { 
            
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
            if (Ani[AllAni[targetI]].time > 0)
            {
                //大于0赋值
                beforeTime = Ani[AllAni[targetI]].time;
            }
            if (Ani[AllAni[targetI]].time >=  Ani[AllAni[targetI]].length || beforeTime > 0 && Ani[AllAni[targetI]].time == 0)
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
                if (AllAni[targetI] == "idle")
                {
                    if (targetI % 2 == 0)
                    {
                        Ani.transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                    }
                    else
                    {
                        Ani.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                    }
                }
                else
                {
                    Ani.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                }
                Ani.Play(AllAni[targetI]);
                beforeTime = 0;
                //Ani[AllAni[targetI]].time = 0.01f;
            }
        }
        void FixedUpdate()
        {
            ChangeTimeAni();
        }
    }
}
