using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CtGoFishMtsAniLsit : CtGoFish
{ 
    public SkinnedMeshRenderer[] AllSkinned;
    private Color[] beforColor;
    private Color[] hitColor;
    public Animation[] Ani;
    private void Awake() 
    {
     
        if (AllSkinned!=null)
        {
            
            hitColor = new Color[AllSkinned.Length];

            //保存颜色
            beforColor = new Color[AllSkinned.Length];
            for (int i = 0; i < beforColor.Length; i++)
            {
                beforColor[i] = AllSkinned[i].material.color;
            }
        }
    }

    public override void SetAniTime(string aniName, float time)
    {
        if (Ani == null)
        {
            return;
        }
        for (int i = 0; i < Ani.Length; i++)
        {
            Ani[i].Play(aniName); 
            Ani[i][aniName].time = time;
        }
    }
    public override void SetAniTimeSpeed(float speed)
    {
        if (Ani == null)
        {
            return;
        }
        for (int i = 0; i < Ani.Length; i++)
        {
            string aniName = Ani[i].clip.name;
            Ani[i][aniName].speed = speed;
        }
    }
    public override void SetBeHitColor(float fBeHitTime)
    {
        if (AllSkinned.Length == 0)
        {
            return;
        }
        if (fBeHitTime<=0)
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].materials[0].SetColor("_Color", beforColor[i]);
            }
        }
        else
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {

                hitColor[i].r = beforColor[i].r;
                hitColor[i].g = beforColor[i].g - fBeHitTime;
                hitColor[i].b = beforColor[i].b - fBeHitTime;

                AllSkinned[i].materials[0].SetColor("_Color", hitColor[i]);
            }
        }
        
    }
  
    public override void SetCutoffIce(float fCutoff)
    {
        if (AllSkinned.Length == 0)
        {
            return;
        }
        for (int i = 0; i < AllSkinned.Length; i++)
        {

            var Allm = AllSkinned[i].materials;
            if (Allm.Length > 1)
            {
                Allm[Allm.Length - 1].SetFloat("_CutoffIce", fCutoff);
            }
        }
    }
    public override void SetIsShowSkin(bool state)
    {
        if (state)
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].gameObject.SetActive(true);
            }
        }
        else
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].gameObject.SetActive(false);
            }
        }
    }
} 
