using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class JxFengHuang : MonoBehaviour
{
    //觉醒凤凰脚本
    Animation thisAni;

    public int nIdleTimes;
    private void Awake()
    {
        thisAni = this.GetComponent<Animation>();
    }
    private void OnEnable()
    {
        thisAni.Play("stage");
    }
   
    public void OverStage()
    {
        if (thisAni == null)
        {
          
        }
        thisAni.Play("idle");
    }
    public void OverIdle() 
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        nIdleTimes++;
        if (nIdleTimes>5)
        {
            nIdleTimes = 0;
            thisAni.Play("idle_2");
        }
        else
        {
            thisAni.Play("idle");
        }
                
       
    } 
    public void OverIdle_2()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle_3");
    } 
    public void OverIdle_3()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");
    }
    public void OverDead_1()
    {
 
    }
}
