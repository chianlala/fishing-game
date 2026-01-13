using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SetNameAniEvent : MonoBehaviour
{
    //需要打的特效
    public GameObject EfGo;
    //转变动画的动画名
    public string AniName;

    Animation thisAni;
 
    public void ChangeEffect()
    {
        if (EfGo != null)
        { 
            EfGo.SetActive(false);
            EfGo.SetActive(true);
        }
    }
    public void ChangeAni() 
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        if (thisAni!=null)
        {
            thisAni.Play(AniName);
        }
    }
    //Animation AniShaker;
    //public void DouPingEvent() 
    //{
    //    if (AniShaker==null)
    //    {
    //        AniShaker = Root3D.Instance.cam3D.GetComponent<Animation>();
    //    }
    //    AniShaker.Play("AniName");
    //}
    // Update is called once per frame
    void Update()
    {
        
    }
}
