using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ZhiJinBoSkill  : MonoBehaviour
{

    //天雷紫金钵脚本
    public GameObject baseGO; 


    Animation thisAni;

    //特效
    public Transform stageTrail;
    public Transform stageEffect; 

    public Transform trasSkil_l;
    public Transform trasSkil_2;

    //滚动
    public Transform Scorll;
    private void Awake()
    {
        if (thisAni == null)
        {
            thisAni = this.GetComponent<Animation>();
        }
    }
    private void OnEnable()
    {
        trasSkil_l.gameObject.SetActive(false);
        trasSkil_2.gameObject.SetActive(false);
        //等待时的闪电特效
        stageEffect.gameObject.SetActive(false);
        //进入时打开拖尾
        stageTrail.gameObject.SetActive(true);
    }
    public void Over_skill_idle()  
    {
        thisAni.Play("skill_1");
        
        trasSkil_l.gameObject.SetActive(true);
        //trasSkil_2.gameObject.SetActive(true);

        trasSkil_l.GetComponent<Animation>().Play("anim_gny_0056_skill_01");
     
         
        stageEffect.gameObject.SetActive(false);
    }

    int NumP = 0;
    public void Over_skill_1()
    {
        NumP++;

        if (NumP==1)
        {
            thisAni.Play("skill_2_1");
            trasSkil_2.gameObject.SetActive(true);
            trasSkil_2.GetComponent<Animation>().Play("anim_gny_0056_skill_02_02_start");

            //播放第二个特效这时会停顿一下 需显示
            stageEffect.gameObject.SetActive(true);
        }
        else
        {
            baseGO.SetActive(false);
        }
     
    }
    public void Over_skill_2_1()
    {
        thisAni.Play("skill_2_2");
        trasSkil_2.GetComponent<Animation>().Play("anim_gny_0056_skill_02_02_loop");

        stageEffect.gameObject.SetActive(true);
        stageEffect.transform.localScale = new Vector3(2f, 2f, 2f);
        Scorll.gameObject.SetActive(true);
    }
    int nSkill2stand;
    public void Over_skill_2_2()
    {
        nSkill2stand++;
        if (nSkill2stand>=5)
        {    
            //这里需停顿几次
            thisAni.Play("skill_2_3");
            trasSkil_2.GetComponent<Animation>().Play("anim_gny_0056_skill_02_02_end");
        }
        else
        {
            thisAni.Play("skill_2_2");
        }
    }
    public void Over_skill_2_3()
    {
        thisAni.Play("skill_idle");
        stageEffect.gameObject.SetActive(false);
    }

    //开场画面
    public void Over_stage()
    {
        thisAni.Play("skill_idle");

        trasSkil_l.gameObject.SetActive(false);
        trasSkil_2.gameObject.SetActive(false);

        stageTrail.gameObject.SetActive(false);
        stageEffect.gameObject.SetActive(true);

        //stageEffect.GetComponent<Animation>().Play("anim_gny_0056_stage");
    }
}
