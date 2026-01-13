using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CtGoFish2D : CtGoFish
{
    public Animation Ani;
    public SpriteRenderer AllSkinned; 

    private Color beforColor;
    private Color hitColor;
     
    private void Awake()
    {
        if (Ani == null) 
        {
            Ani = this.GetComponentInChildren<Animation>();
        }
        if (AllSkinned == null)
        {
            AllSkinned = this.transform.Find("FishView/Adapt/DieAction/Bone").GetComponentInChildren<SpriteRenderer>();
            beforColor = Color.white;
        }
        else
        {
            beforColor = Color.white;
        }
    }

    public override void SetAniTime(string aniName, float time)
    {
        if (Ani == null)
        {
            return;
        }
        Ani.Play(aniName);
        Ani[aniName].time = time;
    }
    public override void SetAniTimeSpeed(float speed)
    {
        if (Ani==null)
        {
            return;
        }
        string aniName = Ani.clip.name;
        Ani[aniName].speed = speed;
    }
    public override void SetBeHitColor(float fBeHitTime)
    {
        if (AllSkinned == null)
        {
            return;
        }
        if (fBeHitTime<=0)
        {
            AllSkinned.color=beforColor;
        }
        else
        {
            //hitColor.r = beforColor.r - 0.1f;
            hitColor.r = beforColor.r;
            hitColor.g = beforColor.g - fBeHitTime;
            hitColor.b = beforColor.b - fBeHitTime;
            hitColor.a = 1f;
            AllSkinned.color = hitColor;
        }
    }
    public override void SetCutoffIce(float fCutoff) 
    {
       
    }
    public override void SetIsShowSkin(bool state)
    {
        if (state)
        {
            AllSkinned.gameObject.SetActive(true);
        }
        else
        {
            AllSkinned.gameObject.SetActive(false);
        }
    }
} 
