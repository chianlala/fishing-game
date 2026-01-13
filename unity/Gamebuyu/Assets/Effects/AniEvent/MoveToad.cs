using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MoveToad : MonoBehaviour
{
    //蟾蜍跳跃脚本
    public Transform Go;
    public Transform TargetGo; 
    Animator AniShaker;
    Animation thisAni;
    //蟾蜍跳跃结束
    public void CanChuJumpOver()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("pause");
    }
    //蟾蜍停顿结束
    public void CanChuPauseOver()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");

        Go.transform.eulerAngles = TargetGo.eulerAngles;
        Go.DOMove(TargetGo.position, 1f);
    }

    //蟾蜍抖屏
    public void DouPingEvent() 
    {
        if (AniShaker==null)
        {
            AniShaker = Root3D.Instance.cam3D.GetComponent<Animator>();
        }
        AniShaker.Play("cattle_shaker");
    }
    
}
