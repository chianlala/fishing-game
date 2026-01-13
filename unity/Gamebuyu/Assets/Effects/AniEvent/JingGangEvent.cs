using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class JingGangEvent : MonoBehaviour
{
    public GameObject BaseGo; 
    public GameObject Child1;
    public GameObject Child2;
    //觉醒金刚脚本
    Animation thisAni;

    //idle次数
    public int IdleIndex;
    public void Overdown()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");
    }
    public void Overidle()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        IdleIndex++;


        if (IdleIndex > 8)
        {
            thisAni.Play("idle_3");
        }
        else  if (IdleIndex > 7)
        {
            thisAni.Play("idle");
        }
        else if (IdleIndex > 6)
        {
            thisAni.Play("idle");
        }
        else if(IdleIndex > 5)
        {
            thisAni.Play("idle_2");
        }
        else
        {
            thisAni.Play("idle");
        }
    }
    public void Overidle_2()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        IdleIndex++;
        thisAni.Play("idle");
     
    }
    public void Overidle_3()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        IdleIndex++;
        thisAni.Play("idle_4");
    }
    public void Overidle_4()
    {
        //if (thisAni == null)
        //{
        //    thisAni = this.GetComponent<Animation>();
        //}
        //Child1.gameObject.SetActive(false);
        //Child2.gameObject.SetActive(true);
        BaseGo.gameObject.SetActive(false);
        Destroy(BaseGo.gameObject);
        //BaseGo.gameObject.SetActive(false);
    }
  
}
