using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CtGoFish2DList : CtGoFish
{
    public Animator[] Ani;
    public SpriteRenderer[] AllSkinned; 

    private Color beforColor;
    private Color hitColor; 
     
    private void Awake()
    {
        if (Ani == null) 
        {
            Ani = this.GetComponentsInChildren<Animator>();
        }
        if (AllSkinned == null)
        {
            AllSkinned = this.transform.Find("FishView/Adapt/DieAction/Bone").GetComponentsInChildren<SpriteRenderer>();
            beforColor = Color.white;
        }
        else
        {
            beforColor = Color.white;
        }
    }

    public override void SetAniTime(string aniName, float time)
    {
        //if (Ani == null)
        //{
        //    return;
        //}
        //for (int i = 0; i < Ani.Length; i++)
        //{
        //    Ani[i].Play(aniName);
        //    Ani[i][aniName].time = time;
        //}
    }
    public override void SetAniTimeSpeed(float speed)
    {
        if (Ani==null)
        {
            return;
        }
        for (int i = 0; i < Ani.Length; i++)
        {
            Ani[i].speed = speed;
        }
        //for (int i = 0; i < Ani.Length; i++)
        //{
        //    string aniName = Ani[i].clip.name;
        //    Ani[i][aniName].speed = speed;
        //}
    }
    public override void SetBeHitColor(float fBeHitTime)
    {
        if (AllSkinned == null)
        {
            return;
        }
        if (fBeHitTime<=0)
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].color = beforColor;
            }
        }
        else
        {
            //hitColor.r = beforColor.r - 0.1f;
            hitColor.r = beforColor.r;
            hitColor.g = beforColor.g - fBeHitTime;
            hitColor.b = beforColor.b - fBeHitTime;
            hitColor.a = 1f;

            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].color = hitColor;
            }
        }
    }
    public override void SetCutoffIce(float fCutoff) 
    {
       
    }
    public override void SetIsShowSkin(bool state)
    {
        for (int i = 0; i < AllSkinned.Length; i++)
        {
            if (state)
            {
                AllSkinned[i].gameObject.SetActive(true);
            }
            else
            {
                AllSkinned[i].gameObject.SetActive(false);
            }
        }
    }
} 
