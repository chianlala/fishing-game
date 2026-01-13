using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SetGlobalColor : MonoBehaviour
{
    public Vector4 _Color = new Vector4(1, 1, 1, 1);
    public Vector4 _SpecColor = new Vector4(1, 1, 1, 1);
    public Vector4 _FlowColor = new Vector4(1, 1, 1, 1);
    public float _GrayValue = 0f;

    public float _HitColorEnable = 0f;

    public float _HitColorValue = 0f;
    public float _HitSaturation = 0.2f;
    public Vector4 _HitColor = new Vector4(1f, 0f, 0f, 1f);
    public Vector4 _PreHitColor = new Vector4(1, 0, 0, 1);
    public float _TestGrayFlag = 1f;
    public Vector4 _TestGrayColor = new Vector4(0.1f, 0.1f, 0.1f, 0.1f);
    public float _HitAniValue = 0f;
    public float _NewHitVer = 0f;
    public float _Alpha = 1f;

    public float _AlphaA_Strength = 1f;
    public float _AlphaB_Strength = 1f;

    public float _Albedo = 0.5f;
    public float _Shininess = 1.5f;
    public float _Gloss = 0.5f;
    public float _Reflection = 0.5f;
    public float _FrezFalloff = 4f;
    public float _FlowStrenth = 1f;
    public float _FlowTime = 0f;
    public float _Scale = 1f;
    public int _FresnelEnable = 0;
    public Vector4 _InteriorColor = new Vector4(1, 1, 1, 1);
    public Vector4 _InteriorColor2 = new Vector4(1, 1, 1, 1);
    public float _RimIntensity = 0f;
    public float _RimPower = 0f;

    Shader nShader;
    void Start()
    {
        nShader = this.transform.GetComponent<SkinnedMeshRenderer>().material.shader;
    }
    private void OnEnable()
    {

    }
    // Update is called once per frame
    void Update()
    {
        //Shader.SetGlobalColor("_Color", _Color);
        //Shader.SetGlobalColor("_SpecColor", _SpecColor);
        //Shader.SetGlobalColor("_FlowColor", _FlowColor);
        //Shader.SetGlobalFloat("_GrayValue", _GrayValue);


        //Shader.SetGlobalFloat("_HitColorEnable", _HitColorEnable);
        //Shader.SetGlobalFloat("_HitColorValue", _HitColorValue);

        Shader.SetGlobalFloat("_HitColorEnable", _HitColorEnable);

        Shader.SetGlobalFloat("_HitColorValue", _HitColorEnable);
        Shader.SetGlobalFloat("_HitSaturation", _HitColorEnable);
        Shader.SetGlobalColor("_HitColor", _HitColor);
        Shader.SetGlobalColor("_PreHitColor", _HitColor);

        Shader.SetGlobalFloat("_TestGrayFlag", _TestGrayFlag);
        Shader.SetGlobalColor("_TestGrayColor", _TestGrayColor);
        Shader.SetGlobalFloat("_HitAniValue", _HitAniValue);
        Shader.SetGlobalFloat("_NewHitVer", _NewHitVer);
        Shader.SetGlobalFloat("_Alpha", _Alpha);


        Shader.SetGlobalFloat("_AlphaB_Strength", _AlphaB_Strength);
        Shader.SetGlobalFloat("_Albedo", _Albedo);
        Shader.SetGlobalFloat("_Shininess", _Shininess);
        Shader.SetGlobalFloat("_Gloss", _Gloss);
        Shader.SetGlobalFloat("_Reflection", _Reflection);
        Shader.SetGlobalFloat("_FrezFalloff", _FrezFalloff);
        Shader.SetGlobalFloat("_FlowStrenth", _FlowStrenth);
        Shader.SetGlobalFloat("_FlowTime", _FlowTime);
        Shader.SetGlobalFloat("_Scale", _Scale);
        Shader.SetGlobalInt("_FresnelEnable", _FresnelEnable);
        Shader.SetGlobalColor("_InteriorColor", _InteriorColor);
        Shader.SetGlobalColor("_InteriorColor2", _InteriorColor2);
        Shader.SetGlobalFloat("_RimIntensity", _RimIntensity);
        Shader.SetGlobalFloat("_RimPower",_RimPower);

    //_Fresnel_Range("Fresnel Range", float) = 0
    //Shader.SetGlobalVector("_ShadowPlane", _ShadowPlane);
    //Shader.SetGlobalVector("_ShadowProjDir", _ShadowProjDir);
    //Shader.SetGlobalVector("_WorldPos", _WorldPos);


    //Shader.SetGlobalFloat("_ShadowInvLen", _ShadowInvLen);
    //Shader.SetGlobalFloat("_Scale", _Scale);

    //Shader.SetGlobalVector("_ShadowFadeParams", _ShadowFadeParams);
    //Shader.SetGlobalFloat("_Alpha", _Alpha);
}
}
