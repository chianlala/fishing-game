using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MoveNiuMoWang : MonoBehaviour
{
    //牛魔王跳跃脚本
    public Transform Go;
    //public Transform TargetGo; 
   
    Animation thisAni;

    //跳跃点
    public Transform[] PosAll;
     
    public Transform wai1;
    public Transform wai2;

    //跳跃次数
    int nJumpIndex = 0;
    int nNowPosIndex=0; 

    
    //此点为世界坐标
    Vector3 GoMuBiaoDian(int NextPos) {
        if (PosAll.Length> NextPos)
        {
            return PosAll[NextPos].position;
        }
        else
        {
            nNowPosIndex = 0;
            return PosAll[nNowPosIndex].position;
        }
    }
    private void Awake()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
    }

    private void OnEnable()
    {
        nJumpIndex = 0;
        thisAni.Play("jump_1");
    }
    //牛魔王跳跃结束
    public void OverJump_1()  
    {
        if (thisAni == null) 
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("jump_2");

        //说明是第一次
        if (nJumpIndex==0)
        {
            float distance = Vector3.Distance(Go.position, wai1.position);
            //说明离外面点(wai1)比较近
            if (distance<0.1f)
            {
                nNowPosIndex = 0;
            }
            else
            {
                nNowPosIndex = 1;
            }
        }

        //跳跃了5次
        if (nJumpIndex >= 5)
        {
            float distance = Vector3.Distance(Go.position, PosAll[0].position);
            //说明离外面点(PosAll[0])比较近
            if (distance < 0.1f)
            {
                Go.DOMove(wai1.position, 1f).OnComplete(() => {
                    Go.parent.gameObject.SetActive(false);
                    DestroyImmediate(Go.parent.gameObject);
                });
                nNowPosIndex++;
            }
            else
            {
                Go.DOMove(wai2.position, 1f).OnComplete(()=> {
                    Go.parent.gameObject.SetActive(false);
                    DestroyImmediate(Go.parent.gameObject);
                });
                nNowPosIndex++;
            }
            return;
        }
        Go.DOMove(GoMuBiaoDian(nNowPosIndex), 1f);
        nNowPosIndex++;
        nJumpIndex++;
    } 
    public void OverJump_2()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("jump_3");
    }
    public void OverJump_3()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("attack_1");
    }
    public void OverAttack_1()
    { 
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("attack_2");
    }
    public void OverAttack_2()
    { 
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");

    }
    public void OverStage()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");
    }
    public void OverIdle() 
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle_2");
    } 
    public void OverIdle_2()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("jump_1");
    }
}
