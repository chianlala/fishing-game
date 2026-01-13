using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class JgJumpOut : MonoBehaviour
{
    // Start is called before the first frame update
    public GameObject Child1;
    public GameObject Child2;
    //觉醒金刚脚本
    Animation thisAni;
 
    public void OverWalk() 
    {
        if (thisAni == null)  
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle_3");
    } 
    public void OverJump() 
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("jump_out");
    }
     
    public void OverJumpOut()
    {
        Child1.gameObject.SetActive(true);
        Child2.gameObject.SetActive(false);
    }
}
