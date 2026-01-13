using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BingLongSkill : MonoBehaviour
{
    //冰龙技能脚本
    Animation thisAni;

    public Transform trasIceMouth; 
    public Transform trasEFSkill; 
    public Action<string> aAction;

    //private void Awake()
    //{
    //    if (thisAni == null)
    //    {
    //        thisAni = this.GetComponent<Animation>();
    //    }
    //}
    //public void skillIceSmoke()  
    //{
    //    trasEFSkill.gameObject.SetActive(false);
    //    trasEFSkill.gameObject.SetActive(true);
    //    trasEFSkill.transform.GetChild(0).GetComponent<Animator>().Play("bs_0013_penhuo");

    //    if (aAction!=null)
    //    {
    //        aAction("skillIceSmoke");
    //        trasIceBoomSkill.gameObject.SetActive(false);
    //    }
    //}
    //public void Over_skill_idle() 
    //{
    //    thisAni.Play("skill");
    //}
    //public void Over_skill() 
    //{
    //    thisAni.Play("skill_idle");
    //}
    //public void skillIceBoom() 
    //{
    //    if (aAction != null)
    //    {
    //        aAction("skillIceBoom");
    //        trasIceBoomSkill.gameObject.SetActive(true);
    //    }
    //}
}
