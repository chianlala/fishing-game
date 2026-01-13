using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MoveSytm : MonoBehaviour
{
    //深渊屠魔出现脚本
    public Transform VfxGo; 
 
    Animation thisAni;
    private void OnEnable()
    {
        //VfxGo.GetComponent<ChangeLightPoint>().enabled = false;
        //VfxGo.GetComponent<ChangeLightPoint>().enabled = true;
    }
  
    public void Over_stage() 
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
        thisAni.Play("idle");

        //VfxGo.GetComponent<ChangeLightPoint>().enabled=true;
    }
    private void OnDisable()
    {
         //VfxGo.GetComponent<ChangeLightPoint>().enabled = false;

        //状态关闭
        BmController.instance.CloseZheZhao();
    }
}
