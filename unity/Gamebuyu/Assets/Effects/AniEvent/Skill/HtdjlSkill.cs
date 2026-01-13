using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HtdjlSkill : MonoBehaviour
{
    //虎头大金锣技能脚本
    public GameObject baseGO;


    Animation thisAni;
     
    //特效
    public Transform chuchang;

    public Transform ef_hdy_0016_Bone13;
    public Transform ef_hdy_0016_skill01_boom;
    public Transform ef_hdy_0016_skill02_boom;
    public Transform ef_hdy_016_skill_pen;
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
    }
    public void Over_idle()
    { 
        thisAni.Play("skill_1_1");
    }
    int NumP = 0;
    public void Over_skill_1_1()
    {
        thisAni.Play("skill_1_2");
    }
    public void Over_skill_1_2()
    {
        thisAni.Play("skill_1_3");
    }
    public void Over_skill_1_3()
    {
        thisAni.Play("skill_2_1");
    }
    public void Over_skill_2_1()
    {
        thisAni.Play("skill_2_2");
    }
    int nSkill2stand;
    public void Over_skill_2_2()
    {
        thisAni.Play("skill_2_3");
    }
    public void Over_skill_2_3()
    {
        thisAni.Play("skill_1_1");
    }

    //开场画面 
    public void Over_loop()
    {
        thisAni.Play("skill_idle");
    }

    void BoomSkill1() {
        ef_hdy_0016_skill01_boom.gameObject.SetActive(true);
        ef_hdy_0016_skill02_boom.gameObject.SetActive(false);
    }
    void BoomSkill2()
    {
        ef_hdy_0016_skill01_boom.gameObject.SetActive(false);
        ef_hdy_0016_skill02_boom.gameObject.SetActive(true);
    }
}
