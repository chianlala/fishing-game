using System.Collections;
using System.Collections.Generic;
using UnityEngine;
//using UnityEngine.UI;
using DG.Tweening;
using UnityEngine.Scripting;

[Preserve]
public class paotai : MonoBehaviour {

    public Transform[] pos_fire;
    public int TypeType;
    public Animator Ani_pao;
    public Transform root;
    public GameObject trasWing;
    public Transform violent;   
    //空物体
    private GameObject kong;
    private void Awake()
    {
        if (pos_fire[0]==null)
        {
            pos_fire[0] = transform.Find("pao/root/head");
        }
    }
    void Start() 
    {
     
    }
    void OnEnable () {
        violent.gameObject.SetActive(false);
    }
    int intPaoGuan = 1;
    public void Fire()
    {
        Ani_pao.Play("fire",0,0f);
    }
    public void Fire1()
    {
        //UnityArmature1.animation.Play("pao1_1", 1);
    }
    //这时必为锁定
    public void violentFire(float varlength)  
    {
        violent.localScale=new Vector3(1f, varlength, 1f);
        violent.gameObject.SetActive(true);
        syTime = 0.3f;
    }
    float syTime = 2f;
    public void Update()
    {
        if (violent!=null)
        {
            if (violent.gameObject.activeSelf)
            {
                syTime = syTime - Time.deltaTime;
                if (syTime<=0)
                {
                    violent.gameObject.SetActive(false);
                }
            }
        }
    }
    //public void Fire2()
    //{
    //}
    //public void InsertWang()
    //{
    //}
    //public void ChangePao1()
    //{
    //    intPaoGuan = 1;
    //}
    //public void ChangePao3()
    //{
    //    intPaoGuan = 3;
    //}
    //void CloseAni()
    //{

    //}
    ///// <summary>
    ///// 改变炮台造型
    ///// </summary>
    ///// <param name="paoType">0普通 1特殊</param>
    //public void ChangeType(int paoType)
    //{
    //    if(paoType==0)
    //    {

    //    }
    //    else
    //    {

    //    }
    //}   
}
