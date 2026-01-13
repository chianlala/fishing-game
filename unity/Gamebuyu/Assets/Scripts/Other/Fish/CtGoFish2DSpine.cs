using Spine.Unity;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CtGoFish2DSpine : CtGoFish
{ 
    public Animation Ani;
    public MeshRenderer AllSkinned; 

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
            AllSkinned = this.transform.Find("FishView/Adapt/DieAction/Bone").GetComponentInChildren<MeshRenderer>();
            beforColor = AllSkinned.material.color;
        }
        else
        {
            beforColor = AllSkinned.material.color;
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
            AllSkinned.material.color=beforColor;
        }
        else
        {
            hitColor.r = beforColor.r - 0.2f;
            hitColor.g = beforColor.g - fBeHitTime;
            hitColor.b = beforColor.b - fBeHitTime;
            AllSkinned.material.color = hitColor;
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
