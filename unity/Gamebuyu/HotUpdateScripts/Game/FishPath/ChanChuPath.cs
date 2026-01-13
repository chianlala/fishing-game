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
    //蟾蜍
    public class ChanChuPath : MonoBehaviour
    { 
        public Transform FishView;
        public Animation Ani;
        float beforeTime = 0f;
        public Transform EF_bs_0008_run;
       
        //动画名 根据轨迹来 最多左右四次
        public List<string> AllAni = new List<string>() { 
            "idle", "pause", "idle", "pause", "idle","pause","idle","pause",
            "idle", "pause", "idle", "pause", "idle","pause","idle","pause",
            "idle", "pause", "idle", "pause", "idle","pause","idle","pause",
            "idle", "pause", "idle", "pause", "idle","pause","idle","pause",
            "idle","pause","idle","pause",
        };
        void Awake()
        {
            Ani = this.transform.Find("FishView/bs_0008/bs_0008").GetComponent<Animation>();
            FishView = this.transform.Find("FishView");
            EF_bs_0008_run = this.transform.Find("FishView/bs_0008/bs_0008/EF_bs_0008_run");
            AniOverEventTA Bskill = Ani.transform.GetComponent<AniOverEventTA>();
            Bskill.aAction = AniAction;
        }
        void AniAction(string aniname)
        {
            if (aniname == "douping")
            {
                try
                {
                    //大于80一般都出界了
                    if (this.transform.localPosition.x > -80f && this.transform.localPosition.x < 80f)
                    {
                        Root3D.Instance.showShaker();
                    }
                    EF_bs_0008_run.gameObject.SetActive(true);
                }
                catch 
                {
                   
                }
             
            }
            if (aniname == "CloseEffect")
            {
                EF_bs_0008_run.gameObject.SetActive(false);
            }
        }

        float IceSpeed = 1;
        public void SetfAniSpeed(float speed)
        {
            IceSpeed = speed;
            ChangePathSpeed();
            Ani[AllAni[targetI]].speed = speed;
        }
        void ChangePathSpeed() {

            if (moveTween!=null)
            {
                if (IceSpeed <= 0)//冰冻
                {
                    moveTween.timeScale = 0;
                }
                else
                {
                    //移动动画
                    if (moveTween.timeScale != 1)
                    {
                        moveTween.timeScale = 1;
                    }
                }
            }
        }
        int targetI = -1;
        TweenerCore<Vector3, Path, PathOptions> TC_Path;
        //鱼游临时路径点
        private Vector3[] move_path;

        //去下一个点差不多是3.3秒
        public void SetfLifeTime(Vector3[] varmove_path, float varfLifeTime)
        {
            //float tmpTime = 0;
            //for (int i = 0; i < AllAni.Count; i++)
            //{
            //    tmpTime += Ani[AllAni[i]].length;
            //}
            //Debug.Log("ChanChuPath" + tmpTime);
            //*****************
            //iTweenPath ipath = common.GetRootPath().Find("cctoadtrack").GetComponent<iTweenPath>();
            move_path = varmove_path;
            transform.localPosition = move_path[0];
            nMoveIndex = 0;
            targetI = -1;
            //跳转到对应时间的动画
            float tmpfLifeTime = 0;
        
            for (int i = 0; i < AllAni.Count; i++)
            {
              
                if (tmpfLifeTime>= varfLifeTime)
                {
                    targetI = i;
                    //当前动画的时间;
                    var nowAniTime = tmpfLifeTime - varfLifeTime;
                    //播放对应动画
                    Ani.Play(AllAni[targetI]);
                    //当前动画播放到哪里了
                    Ani[AllAni[targetI]].time = nowAniTime;

                    //跳跃动画
                    if (AllAni[targetI] == "idle")
                    {
                        //赋值
                        transform.position = move_path[nMoveIndex];

                        moveTween = transform.DOMove(move_path[nMoveIndex], 1f);

                        if (nowAniTime<1&&nowAniTime>0)
                        {
                            moveTween.Goto(nowAniTime);
                        }

                        transform.up= move_path[nMoveIndex] -move_path[nMoveIndex + 1];
                    }
                    else
                    {
                        transform.position = move_path[nMoveIndex];
                        transform.up = move_path[nMoveIndex] - move_path[nMoveIndex + 1];
                    }
                    break;
                }
               
                tmpfLifeTime += Ani[AllAni[i]].length;
                if (AllAni[i] == "idle")//idle计数
                {
                    nMoveIndex++;
                    if (nMoveIndex>= move_path.Length-1)
                    {
                        PathOver();
                        return;
                    }
                }
            }
            if (targetI == -1)
            {
                PathOver();
            }
        }
        Tweener moveTween;
        void ChangeTimeAni()
        {
            if (targetI == -1)
            {
                return;
            }
            if (targetI >= AllAni.Count)
            {
                PathOver();
                return;
            }


            if (Ani[AllAni[targetI]].time > 0)
            {
                //大于0赋值
                beforeTime = Ani[AllAni[targetI]].time;
            }
            if (Ani[AllAni[targetI]].normalizedTime >=1f ||  Ani[AllAni[targetI]].time == 0)
            {
                targetI++;
                //加1后继续判断
                if (targetI >= AllAni.Count)
                {
                    //说明时间时间动画已经播完
                    PathOver();
                    return;
                }
                if (AllAni[targetI]=="idle")
                {
                    nMoveIndex++;
                    if (nMoveIndex >= move_path.Length - 1)
                    {                   
                        //轨迹点已经没了
                        PathOver();
                        return;
                    }
                    //移动
                    moveTween = transform.DOMove(move_path[nMoveIndex], 1f);
                }
                else
                {
                    transform.up = move_path[nMoveIndex] - move_path[nMoveIndex + 1];
                }
                Ani.Play(AllAni[targetI]);
                Ani[AllAni[targetI]].time = 0.001f;
            }
        }
        void PathOver() {

            //动画已经播完
            this.gameObject.SetActive(false);
            Destroy(this.gameObject);
        }
        int nMoveIndex;
        void FixedUpdate()
        {
            ChangePathSpeed();
            ChangeTimeAni();
        }
    }
}
