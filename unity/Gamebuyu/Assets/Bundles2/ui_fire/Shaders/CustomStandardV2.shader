Shader "CustomStandardV2"
{
  Properties
  {
    _Color ("Color", Color) = (1,1,1,1)
    _MainTex ("Albedo", 2D) = "white" {}
    _ColorScale ("Color Scale", Range(1, 10)) = 1
    _Cutoff ("Alpha Cutoff", Range(0, 1)) = 0.5
    _Glossiness ("Smoothness", Range(0, 1)) = 0.5
    _GlossMapScale ("Smoothness Scale", Range(-1, 1)) = -0.8
    _SmoothnessRemapMin ("Smoothness RemapMin", float) = 0
    _SmoothnessRemapMax ("Smoothness RemapMax", float) = 1
    [Gamma] _Metallic ("Metallic", Range(0, 1)) = 0
    _MetallicGlossMap ("Metallic", 2D) = "white" {}
    _MetallicRemapMin ("Metallic RemapMin", float) = 0
    _MetallicRemapMax ("Metallic RemapMax", float) = 1
    [ToggleOff] _SpecularHighlights ("Specular Highlights", float) = 1
    [ToggleOff] _GlossyReflections ("Glossy Reflections", float) = 1
    [ToggleOff] _NotFixSubstance ("Not Fix Substance's roughness", float) = 1
    [Toggle] _PlanarReflections ("Planar Reflections", float) = 0
    _ReflectionTex ("Planar Reflection Texture", 2D) = "black" {}
    _ReflectionPower ("Planar Reflection Power", float) = 1
    [Toggle] _HeightFog ("Height Fog", float) = 0
    _HeightFogColor ("Height Fog Color", Color) = (0,0,1,1)
    _HeightFogDensity ("Height Fog Density", float) = 1
    _HeightFogStart ("Height Fog Start", float) = -10
    _HeightFogEnd ("Height Fog End", float) = 10
    [Toggle] _RimLight ("Rim Light", float) = 0
    [HDR] _RimColor ("Rim Color", Color) = (0.5,0.5,0.5,1)
    _RimPower ("Rim Power", Range(0.01, 10)) = 0.01
    _RimSpread ("Rim Spread", Range(0, 4.99)) = 0.01
    _RimOffset ("Rim Offset", Vector) = (0,0,0,0)
    [Toggle] _Translucent ("Translucent", float) = 0
    _Translucency ("Translucency", Range(0, 50)) = 20
    _TransScattering ("Scaterring Falloff", Range(1, 50)) = 10
    _TransDirect ("Trans Direct", Range(0, 1)) = 0.1
    _TransAmbient ("Trans Ambient", Range(0, 1)) = 0
    _TransColor ("Trans Color", Color) = (1,0,0,0)
    _TransMap ("Trans Map", 2D) = "black" {}
    [Enum(Trans Map,0,Metallic B,1)] _TransMapChannel ("Translucent map channel", float) = 0
    [Toggle] _AdjustHSV ("Adjust HSV", float) = 0
    _AdjustHue ("Hue", Range(0, 360)) = 0
    _AdjustSaturation ("Saturation", Range(0, 5)) = 1
    _AdjustValue ("Value", Range(0, 5)) = 1
    _BumpScale ("Scale", float) = 1
    _BumpMap ("Normal Map", 2D) = "bump" {}
    _Parallax ("Height Scale", Range(0.005, 0.08)) = 0.02
    _ParallaxMap ("Height Map", 2D) = "black" {}
    _OcclusionStrength ("Strength", Range(0, 1)) = 1
    [HDR] _EmissionColor ("Color", Color) = (0,0,0,1)
    _EmissionTexture ("Emission", 2D) = "white" {}
    [Enum(Metallic B,0,Emission Texture,1)] _EmissionMapChannel ("Emission map channel", float) = 0
    [HideInInspector] _Mode ("__mode", float) = 0
    [HideInInspector] _SrcBlend ("__src", float) = 1
    [HideInInspector] _DstBlend ("__dst", float) = 0
    [HideInInspector] _ZWrite ("__zw", float) = 1
    [HideInInspector] _CullMode ("__cullMode", float) = 2
    [Toggle] _Shadow ("Shadow", float) = 0
    [Toggle] _ShadowOffsetToggle ("ShadowOffsetToggle", float) = 1
    _ShadowOffset ("_Offset", Vector) = (-0.5,-1,2,0)
    _ShadowColor ("_Color", Color) = (0,0,0,0.8)
    [Toggle] _Perspective ("Perspective", float) = 0
    [Toggle] _Overlay ("Hit Color", float) = 0
    [KeywordEnum(Rim,Albedo)] _HitColorChannel ("HitColorType", float) = 0
    _OverlayColor ("Color", Color) = (1,1,1,1)
    _FinalColor ("Color", Color) = (1,1,1,1)
    _OverlayMultiple ("Multiple", float) = 1
    _OverlayRimPower ("Rim Power", Range(0.01, 10)) = 0.01
    _OverlayRimSpread ("Rim Spread", Range(0, 4.99)) = 0.01
    _OverlayRimOffset ("Rim Offset", Vector) = (0,0,0,0)
    _HitColor ("Color", Color) = (1,1,1,1)
    _HitMultiple ("Multiple", float) = 1
    _HitRimPower ("Rim Power", Range(0.01, 10)) = 0.01
    _HitRimSpread ("Rim Spread", Range(0, 4.99)) = 0.01
    _HitRimOffset ("Rim Offset", Vector) = (0,0,0,0)
    [Toggle] _Streamer ("Streamer", float) = 0
    _StreamerTex ("Texture", 2D) = "white" {}
    _StreamerMask ("Mask", 2D) = "white" {}
    _StreamerNoise ("Noise", 2D) = "white" {}
    _StreamerNoiseSpeed ("NoiseSpeed", float) = 1
    _StreamerColor ("Color", Color) = (1,1,1,1)
    _StreamerAlpha ("Alpha", float) = 1
    _StreamerScrollX ("speed X", float) = 1
    _StreamerScrollY ("speed Y", float) = 0
    [KeywordEnum(UVPos,ScreenPos,ModelPos)] _StreamerChannel ("StreamerType", float) = 0
    [Toggle] _Contrast ("AdjustContrast", float) = 0
    _Alpha ("Alpha", Range(0, 1)) = 1
  }
  SubShader
  {
    Tags
    { 
      "PerformanceChecks" = "False"
      "RenderType" = "Opaque"
    }
    LOD 300
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "LIGHTMODE" = "FORWARDBASE"
        "PerformanceChecks" = "False"
        "RenderType" = "Opaque"
        "SHADOWSUPPORT" = "true"
      }
      LOD 300
      ZWrite Off
      Cull Off
      Stencil
      { 
        Ref 3
        ReadMask 255
        WriteMask 255
        Pass IncrSat
        Fail Keep
        ZFail Keep
        PassFront IncrSat
        FailFront Keep
        ZFailFront Keep
        PassBack IncrSat
        FailBack Keep
        ZFailBack Keep
      } 
      Blend Zero Zero
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile DIRECTIONAL _HITCOLORCHANNEL_RIM _STREAMERCHANNEL_UVPOS __ALPHATEST_ON
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float3 _WorldSpaceCameraPos;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float _Metallic;
      uniform float _Glossiness;
      //uniform float4 _WorldSpaceLightPos0;
      //uniform float4 unity_SpecCube0_HDR;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float _ColorScale;
      uniform sampler2D _MainTex;
      uniform sampler2D unity_NHxRoughness;
      //uniform samplerCUBE unity_SpecCube0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float4 texcoord4 :TEXCOORD4;
          float4 texcoord5 :TEXCOORD5;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord4 :TEXCOORD4;
          float4 texcoord5 :TEXCOORD5;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float u_xlat16_2;
      float u_xlat9;
      float u_xlat10;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          out_v.vertex = UnityObjectToClipPos(in_v.vertex);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          u_xlat16_2 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat0.x = ((-u_xlat16_2) + _Glossiness);
          u_xlat0.w = (u_xlat0.x + 1);
          #ifdef UNITY_ADRENO_ES3
          u_xlat0.w = min(max(u_xlat0.w, 0), 1);
          #else
          u_xlat0.w = clamp(u_xlat0.w, 0, 1);
          #endif
          u_xlat1.xyz = (in_v.vertex.yyy * conv_mxt4x4_1(unity_ObjectToWorld).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).xyz * in_v.vertex.xxx) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).xyz * in_v.vertex.zzz) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat1.xyz);
          u_xlat1.xyz = (u_xlat1.xyz + (-_WorldSpaceCameraPos.xyz));
          u_xlat0.xyz = normalize(u_xlat1.xyz);
          out_v.texcoord1 = u_xlat0;
          out_v.texcoord2 = float4(0, 0, 0, 0);
          u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat1.xyz = normalize(u_xlat1.xyz);
          u_xlat16_2 = dot(u_xlat0.xyz, u_xlat1.xyz);
          u_xlat16_2 = (u_xlat16_2 + u_xlat16_2);
          out_v.texcoord4.yzw = ((u_xlat1.xyz * (-float3(u_xlat16_2, u_xlat16_2, u_xlat16_2))) + u_xlat0.xyz);
          u_xlat16_2 = dot(u_xlat1.xyz, (-u_xlat0.xyz));
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_2 = min(max(u_xlat16_2, 0), 1);
          #else
          u_xlat16_2 = clamp(u_xlat16_2, 0, 1);
          #endif
          out_v.texcoord5.xyz = u_xlat1.xyz;
          u_xlat16_2 = ((-u_xlat16_2) + 1);
          u_xlat16_2 = (u_xlat16_2 * u_xlat16_2);
          out_v.texcoord5.w = (u_xlat16_2 * u_xlat16_2);
          out_v.texcoord4.x = 0;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float u_xlat16_0;
      float4 u_xlat16_1;
      float3 u_xlat16_2_d;
      float3 u_xlat16_3;
      float3 u_xlat16_4;
      float3 u_xlat16_5;
      float3 u_xlat16_6;
      float u_xlat16_20;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.xz = ((-float2(float2(_Glossiness, _Glossiness))) + float2(1, 1));
          u_xlat16_1.x = (((-u_xlat0_d.x) * 0.699999988) + 1.70000005);
          u_xlat16_1.x = (u_xlat0_d.x * u_xlat16_1.x);
          u_xlat16_1.x = (u_xlat16_1.x * 6);
          u_xlat16_1 = UNITY_SAMPLE_TEXCUBE(unity_SpecCube0, float4(in_f.texcoord4.yzw, u_xlat16_1.x));
          u_xlat16_2_d.x = (u_xlat16_1.w + (-1));
          u_xlat16_2_d.x = ((unity_SpecCube0_HDR.w * u_xlat16_2_d.x) + 1);
          u_xlat16_2_d.x = log2(u_xlat16_2_d.x);
          u_xlat16_2_d.x = (u_xlat16_2_d.x * unity_SpecCube0_HDR.y);
          u_xlat16_2_d.x = exp2(u_xlat16_2_d.x);
          u_xlat16_2_d.x = (u_xlat16_2_d.x * unity_SpecCube0_HDR.x);
          u_xlat16_2_d.xyz = (u_xlat16_1.xyz * u_xlat16_2_d.xxx);
          u_xlat16_20 = dot(in_f.texcoord4.yzw, _WorldSpaceLightPos0.xyz);
          u_xlat16_20 = (u_xlat16_20 * u_xlat16_20);
          u_xlat16_6.x = (u_xlat16_20 * u_xlat16_20);
          u_xlat0_d.y = u_xlat16_6.x;
          u_xlat0_d.x = tex2D(unity_NHxRoughness, u_xlat0_d.yz).x;
          u_xlat0_d.x = (u_xlat0_d.x * 16);
          u_xlat16_6.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat16_3.xyz = (u_xlat16_6.xyz * _Color.xyz);
          u_xlat16_4.xyz = ((u_xlat16_3.xyz * float3(_ColorScale, _ColorScale, _ColorScale)) + float3(-0.0399999991, (-0.0399999991), (-0.0399999991)));
          u_xlat16_3.xyz = (u_xlat16_3.xyz * float3(_ColorScale, _ColorScale, _ColorScale));
          u_xlat16_4.xyz = ((float3(float3(_Metallic, _Metallic, _Metallic)) * u_xlat16_4.xyz) + float3(0.0399999991, 0.0399999991, 0.0399999991));
          u_xlat16_5.xyz = (u_xlat0_d.xxx * u_xlat16_4.xyz);
          u_xlat16_20 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat16_3.xyz = ((u_xlat16_3.xyz * float3(u_xlat16_20, u_xlat16_20, u_xlat16_20)) + u_xlat16_5.xyz);
          u_xlat16_0 = dot(in_f.texcoord5.xyz, _WorldSpaceLightPos0.xyz);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_0 = min(max(u_xlat16_0, 0), 1);
          #else
          u_xlat16_0 = clamp(u_xlat16_0, 0, 1);
          #endif
          u_xlat16_5.xyz = (float3(u_xlat16_0, u_xlat16_0, u_xlat16_0) * _LightColor0.xyz);
          u_xlat16_3.xyz = (u_xlat16_3.xyz * u_xlat16_5.xyz);
          u_xlat16_5.xyz = ((-u_xlat16_4.xyz) + in_f.texcoord1.www);
          u_xlat16_4.xyz = ((in_f.texcoord5.www * u_xlat16_5.xyz) + u_xlat16_4.xyz);
          out_f.color.xyz = ((u_xlat16_2_d.xyz * u_xlat16_4.xyz) + u_xlat16_3.xyz);
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    Pass // ind: 2, name: FORWARD_DELTA
    {
      Name "FORWARD_DELTA"
      Tags
      { 
        "LIGHTMODE" = "FORWARDADD"
        "PerformanceChecks" = "False"
        "RenderType" = "Opaque"
        "SHADOWSUPPORT" = "true"
      }
      LOD 300
      ZWrite Off
      Blend Zero One
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile POINT __ALPHATEST_ON
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float3 _WorldSpaceCameraPos;
      //uniform float4 _WorldSpaceLightPos0;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4x4 unity_WorldToLight;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float _ColorScale;
      uniform float _Metallic;
      uniform float _Glossiness;
      uniform sampler2D _MainTex;
      uniform sampler2D unity_NHxRoughness;
      uniform sampler2D _LightTexture0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
          float3 texcoord5 :TEXCOORD5;
          float4 texcoord6 :TEXCOORD6;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float4 u_xlat2;
      float3 u_xlat3;
      float u_xlat16_4;
      float u_xlat16;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          u_xlat1.xyz = (in_v.vertex.yyy * conv_mxt4x4_1(unity_ObjectToWorld).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).xyz * in_v.vertex.xxx) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).xyz * in_v.vertex.zzz) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat1.xyz);
          out_v.texcoord1.xyz = u_xlat1.xyz;
          u_xlat2.xyz = (u_xlat1.xyz + (-_WorldSpaceCameraPos.xyz));
          u_xlat1.xyz = (((-u_xlat1.xyz) * _WorldSpaceLightPos0.www) + _WorldSpaceLightPos0.xyz);
          u_xlat2.xyz = normalize(u_xlat2.xyz);
          u_xlat3.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat3.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat3.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat3.xyz = normalize(u_xlat3.xyz);
          u_xlat16_4 = dot(u_xlat2.xyz, u_xlat3.xyz);
          u_xlat16_4 = (u_xlat16_4 + u_xlat16_4);
          out_v.texcoord2.yzw = ((u_xlat3.xyz * (-float3(u_xlat16_4, u_xlat16_4, u_xlat16_4))) + u_xlat2.xyz);
          out_v.texcoord4.xyz = u_xlat3.xyz;
          out_v.texcoord2.x = 0;
          u_xlat1.xyz = normalize(u_xlat1.xyz);
          out_v.texcoord3.xyz = u_xlat1.xyz;
          u_xlat1.xyz = (u_xlat0.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * u_xlat0.xxx) + u_xlat1.xyz);
          u_xlat0.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * u_xlat0.zzz) + u_xlat1.xyz);
          out_v.texcoord5.xyz = ((conv_mxt4x4_3(unity_WorldToLight).xyz * u_xlat0.www) + u_xlat0.xyz);
          out_v.texcoord6 = float4(0, 0, 0, 0);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float2 u_xlat0_d;
      float u_xlat16_0;
      float3 u_xlat1_d;
      float3 u_xlat16_2;
      float3 u_xlat16_3;
      float3 u_xlat16_5;
      float u_xlat16_14;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0 = dot(in_f.texcoord2.yzw, in_f.texcoord3.xyz);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_0);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_0);
          u_xlat0_d.x = u_xlat16_0;
          u_xlat0_d.y = ((-_Glossiness) + 1);
          u_xlat1_d.x = tex2D(unity_NHxRoughness, u_xlat0_d.xy).x;
          u_xlat1_d.x = (u_xlat1_d.x * 16);
          u_xlat16_5.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat16_2.xyz = (u_xlat16_5.xyz * _Color.xyz);
          u_xlat16_3.xyz = ((u_xlat16_2.xyz * float3(_ColorScale, _ColorScale, _ColorScale)) + float3(-0.0399999991, (-0.0399999991), (-0.0399999991)));
          u_xlat16_2.xyz = (u_xlat16_2.xyz * float3(_ColorScale, _ColorScale, _ColorScale));
          u_xlat16_3.xyz = ((float3(float3(_Metallic, _Metallic, _Metallic)) * u_xlat16_3.xyz) + float3(0.0399999991, 0.0399999991, 0.0399999991));
          u_xlat16_3.xyz = (u_xlat1_d.xxx * u_xlat16_3.xyz);
          u_xlat16_14 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat16_2.xyz = ((u_xlat16_2.xyz * float3(u_xlat16_14, u_xlat16_14, u_xlat16_14)) + u_xlat16_3.xyz);
          u_xlat16_2.xyz = (u_xlat16_2.xyz * _LightColor0.xyz);
          u_xlat1_d.xyz = (in_f.texcoord1.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat1_d.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * in_f.texcoord1.xxx) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * in_f.texcoord1.zzz) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = (u_xlat1_d.xyz + conv_mxt4x4_3(unity_WorldToLight).xyz);
          u_xlat1_d.x = dot(u_xlat1_d.xyz, u_xlat1_d.xyz);
          u_xlat1_d.x = tex2D(_LightTexture0, u_xlat1_d.xx).x;
          u_xlat16_14 = dot(in_f.texcoord4.xyz, in_f.texcoord3.xyz);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_14 = min(max(u_xlat16_14, 0), 1);
          #else
          u_xlat16_14 = clamp(u_xlat16_14, 0, 1);
          #endif
          u_xlat16_14 = (u_xlat1_d.x * u_xlat16_14);
          out_f.color.xyz = (float3(u_xlat16_14, u_xlat16_14, u_xlat16_14) * u_xlat16_2.xyz);
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    Pass // ind: 3, name: 
    {
      Tags
      { 
        "LIGHTMODE" = "ALWAYS"
        "PerformanceChecks" = "False"
        "QUEUE" = "Transparent"
        "RenderType" = "Opaque"
      }
      LOD 300
      ZWrite Off
      Stencil
      { 
        Ref 1
        ReadMask 255
        WriteMask 255
        Pass Replace
        Fail Keep
        ZFail Keep
        PassFront Replace
        FailFront Keep
        ZFailFront Keep
        PassBack Replace
        FailBack Keep
        ZFailBack Keep
      } 
      Blend SrcAlpha OneMinusSrcAlpha
      // m_ProgramMask = 6
      CGPROGRAM
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _ShadowOffset;
      uniform float4 _ShadowColor;
      uniform float _Alpha;
      struct appdata_t
      {
          float4 vertex :POSITION0;
      };
      
      struct OUT_Data_Vert
      {
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 vertex :Position;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = mul(unity_ObjectToWorld, in_v.vertex);
          u_xlat0 = (u_xlat0 + _ShadowOffset);
          out_v.vertex = mul(unity_MatrixVP, u_xlat0);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat16_0;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          if((int(4294967295)!=0))
          {
              discard;
          }
          u_xlat16_0.w = (_ShadowColor.w * _Alpha);
          u_xlat16_0.xyz = _ShadowColor.xyz;
          out_f.color = u_xlat16_0;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  SubShader
  {
    Tags
    { 
      "PerformanceChecks" = "False"
      "RenderType" = "Opaque"
    }
    LOD 150
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "LIGHTMODE" = "FORWARDBASE"
        "PerformanceChecks" = "False"
        "RenderType" = "Opaque"
        "SHADOWSUPPORT" = "true"
      }
      LOD 150
      ZWrite Off
      Cull Off
      Stencil
      { 
        Ref 0
        ReadMask 255
        WriteMask 255
        Pass IncrSat
        Fail Keep
        ZFail Keep
        PassFront IncrSat
        FailFront Keep
        ZFailFront Keep
        PassBack IncrSat
        FailBack Keep
        ZFailBack Keep
      } 
      Blend Zero Zero
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile DIRECTIONAL _HITCOLORCHANNEL_RIM _STREAMERCHANNEL_UVPOS __ALPHATEST_ON
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float3 _WorldSpaceCameraPos;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float _Metallic;
      uniform float _Glossiness;
      //uniform float4 _WorldSpaceLightPos0;
      //uniform float4 unity_SpecCube0_HDR;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float _ColorScale;
      uniform sampler2D _MainTex;
      uniform sampler2D unity_NHxRoughness;
      //uniform samplerCUBE unity_SpecCube0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float4 texcoord4 :TEXCOORD4;
          float4 texcoord5 :TEXCOORD5;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord4 :TEXCOORD4;
          float4 texcoord5 :TEXCOORD5;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float u_xlat16_2;
      float u_xlat9;
      float u_xlat10;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          out_v.vertex = UnityObjectToClipPos(in_v.vertex);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          u_xlat16_2 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat0.x = ((-u_xlat16_2) + _Glossiness);
          u_xlat0.w = (u_xlat0.x + 1);
          #ifdef UNITY_ADRENO_ES3
          u_xlat0.w = min(max(u_xlat0.w, 0), 1);
          #else
          u_xlat0.w = clamp(u_xlat0.w, 0, 1);
          #endif
          u_xlat1.xyz = (in_v.vertex.yyy * conv_mxt4x4_1(unity_ObjectToWorld).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).xyz * in_v.vertex.xxx) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).xyz * in_v.vertex.zzz) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat1.xyz);
          u_xlat1.xyz = (u_xlat1.xyz + (-_WorldSpaceCameraPos.xyz));
          u_xlat0.xyz = normalize(u_xlat1.xyz);
          out_v.texcoord1 = u_xlat0;
          out_v.texcoord2 = float4(0, 0, 0, 0);
          u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat1.xyz = normalize(u_xlat1.xyz);
          u_xlat16_2 = dot(u_xlat0.xyz, u_xlat1.xyz);
          u_xlat16_2 = (u_xlat16_2 + u_xlat16_2);
          out_v.texcoord4.yzw = ((u_xlat1.xyz * (-float3(u_xlat16_2, u_xlat16_2, u_xlat16_2))) + u_xlat0.xyz);
          u_xlat16_2 = dot(u_xlat1.xyz, (-u_xlat0.xyz));
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_2 = min(max(u_xlat16_2, 0), 1);
          #else
          u_xlat16_2 = clamp(u_xlat16_2, 0, 1);
          #endif
          out_v.texcoord5.xyz = u_xlat1.xyz;
          u_xlat16_2 = ((-u_xlat16_2) + 1);
          u_xlat16_2 = (u_xlat16_2 * u_xlat16_2);
          out_v.texcoord5.w = (u_xlat16_2 * u_xlat16_2);
          out_v.texcoord4.x = 0;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float u_xlat16_0;
      float4 u_xlat16_1;
      float3 u_xlat16_2_d;
      float3 u_xlat16_3;
      float3 u_xlat16_4;
      float3 u_xlat16_5;
      float3 u_xlat16_6;
      float u_xlat16_20;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.xz = ((-float2(float2(_Glossiness, _Glossiness))) + float2(1, 1));
          u_xlat16_1.x = (((-u_xlat0_d.x) * 0.699999988) + 1.70000005);
          u_xlat16_1.x = (u_xlat0_d.x * u_xlat16_1.x);
          u_xlat16_1.x = (u_xlat16_1.x * 6);
          u_xlat16_1 = UNITY_SAMPLE_TEXCUBE(unity_SpecCube0, float4(in_f.texcoord4.yzw, u_xlat16_1.x));
          u_xlat16_2_d.x = (u_xlat16_1.w + (-1));
          u_xlat16_2_d.x = ((unity_SpecCube0_HDR.w * u_xlat16_2_d.x) + 1);
          u_xlat16_2_d.x = log2(u_xlat16_2_d.x);
          u_xlat16_2_d.x = (u_xlat16_2_d.x * unity_SpecCube0_HDR.y);
          u_xlat16_2_d.x = exp2(u_xlat16_2_d.x);
          u_xlat16_2_d.x = (u_xlat16_2_d.x * unity_SpecCube0_HDR.x);
          u_xlat16_2_d.xyz = (u_xlat16_1.xyz * u_xlat16_2_d.xxx);
          u_xlat16_20 = dot(in_f.texcoord4.yzw, _WorldSpaceLightPos0.xyz);
          u_xlat16_20 = (u_xlat16_20 * u_xlat16_20);
          u_xlat16_6.x = (u_xlat16_20 * u_xlat16_20);
          u_xlat0_d.y = u_xlat16_6.x;
          u_xlat0_d.x = tex2D(unity_NHxRoughness, u_xlat0_d.yz).x;
          u_xlat0_d.x = (u_xlat0_d.x * 16);
          u_xlat16_6.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat16_3.xyz = (u_xlat16_6.xyz * _Color.xyz);
          u_xlat16_4.xyz = ((u_xlat16_3.xyz * float3(_ColorScale, _ColorScale, _ColorScale)) + float3(-0.0399999991, (-0.0399999991), (-0.0399999991)));
          u_xlat16_3.xyz = (u_xlat16_3.xyz * float3(_ColorScale, _ColorScale, _ColorScale));
          u_xlat16_4.xyz = ((float3(float3(_Metallic, _Metallic, _Metallic)) * u_xlat16_4.xyz) + float3(0.0399999991, 0.0399999991, 0.0399999991));
          u_xlat16_5.xyz = (u_xlat0_d.xxx * u_xlat16_4.xyz);
          u_xlat16_20 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat16_3.xyz = ((u_xlat16_3.xyz * float3(u_xlat16_20, u_xlat16_20, u_xlat16_20)) + u_xlat16_5.xyz);
          u_xlat16_0 = dot(in_f.texcoord5.xyz, _WorldSpaceLightPos0.xyz);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_0 = min(max(u_xlat16_0, 0), 1);
          #else
          u_xlat16_0 = clamp(u_xlat16_0, 0, 1);
          #endif
          u_xlat16_5.xyz = (float3(u_xlat16_0, u_xlat16_0, u_xlat16_0) * _LightColor0.xyz);
          u_xlat16_3.xyz = (u_xlat16_3.xyz * u_xlat16_5.xyz);
          u_xlat16_5.xyz = ((-u_xlat16_4.xyz) + in_f.texcoord1.www);
          u_xlat16_4.xyz = ((in_f.texcoord5.www * u_xlat16_5.xyz) + u_xlat16_4.xyz);
          out_f.color.xyz = ((u_xlat16_2_d.xyz * u_xlat16_4.xyz) + u_xlat16_3.xyz);
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    Pass // ind: 2, name: FORWARD_DELTA
    {
      Name "FORWARD_DELTA"
      Tags
      { 
        "LIGHTMODE" = "FORWARDADD"
        "PerformanceChecks" = "False"
        "RenderType" = "Opaque"
        "SHADOWSUPPORT" = "true"
      }
      LOD 150
      ZWrite Off
      Blend Zero One
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile POINT __ALPHATEST_ON
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float3 _WorldSpaceCameraPos;
      //uniform float4 _WorldSpaceLightPos0;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4x4 unity_WorldToLight;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float _ColorScale;
      uniform float _Metallic;
      uniform float _Glossiness;
      uniform sampler2D _MainTex;
      uniform sampler2D unity_NHxRoughness;
      uniform sampler2D _LightTexture0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
          float3 texcoord5 :TEXCOORD5;
          float4 texcoord6 :TEXCOORD6;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float4 u_xlat2;
      float3 u_xlat3;
      float u_xlat16_4;
      float u_xlat16;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          u_xlat1.xyz = (in_v.vertex.yyy * conv_mxt4x4_1(unity_ObjectToWorld).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).xyz * in_v.vertex.xxx) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).xyz * in_v.vertex.zzz) + u_xlat1.xyz);
          u_xlat1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat1.xyz);
          out_v.texcoord1.xyz = u_xlat1.xyz;
          u_xlat2.xyz = (u_xlat1.xyz + (-_WorldSpaceCameraPos.xyz));
          u_xlat1.xyz = (((-u_xlat1.xyz) * _WorldSpaceLightPos0.www) + _WorldSpaceLightPos0.xyz);
          u_xlat2.xyz = normalize(u_xlat2.xyz);
          u_xlat3.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat3.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat3.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat3.xyz = normalize(u_xlat3.xyz);
          u_xlat16_4 = dot(u_xlat2.xyz, u_xlat3.xyz);
          u_xlat16_4 = (u_xlat16_4 + u_xlat16_4);
          out_v.texcoord2.yzw = ((u_xlat3.xyz * (-float3(u_xlat16_4, u_xlat16_4, u_xlat16_4))) + u_xlat2.xyz);
          out_v.texcoord4.xyz = u_xlat3.xyz;
          out_v.texcoord2.x = 0;
          u_xlat1.xyz = normalize(u_xlat1.xyz);
          out_v.texcoord3.xyz = u_xlat1.xyz;
          u_xlat1.xyz = (u_xlat0.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * u_xlat0.xxx) + u_xlat1.xyz);
          u_xlat0.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * u_xlat0.zzz) + u_xlat1.xyz);
          out_v.texcoord5.xyz = ((conv_mxt4x4_3(unity_WorldToLight).xyz * u_xlat0.www) + u_xlat0.xyz);
          out_v.texcoord6 = float4(0, 0, 0, 0);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float2 u_xlat0_d;
      float u_xlat16_0;
      float3 u_xlat1_d;
      float3 u_xlat16_2;
      float3 u_xlat16_3;
      float3 u_xlat16_5;
      float u_xlat16_14;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0 = dot(in_f.texcoord2.yzw, in_f.texcoord3.xyz);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_0);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_0);
          u_xlat0_d.x = u_xlat16_0;
          u_xlat0_d.y = ((-_Glossiness) + 1);
          u_xlat1_d.x = tex2D(unity_NHxRoughness, u_xlat0_d.xy).x;
          u_xlat1_d.x = (u_xlat1_d.x * 16);
          u_xlat16_5.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat16_2.xyz = (u_xlat16_5.xyz * _Color.xyz);
          u_xlat16_3.xyz = ((u_xlat16_2.xyz * float3(_ColorScale, _ColorScale, _ColorScale)) + float3(-0.0399999991, (-0.0399999991), (-0.0399999991)));
          u_xlat16_2.xyz = (u_xlat16_2.xyz * float3(_ColorScale, _ColorScale, _ColorScale));
          u_xlat16_3.xyz = ((float3(float3(_Metallic, _Metallic, _Metallic)) * u_xlat16_3.xyz) + float3(0.0399999991, 0.0399999991, 0.0399999991));
          u_xlat16_3.xyz = (u_xlat1_d.xxx * u_xlat16_3.xyz);
          u_xlat16_14 = (((-_Metallic) * 0.959999979) + 0.959999979);
          u_xlat16_2.xyz = ((u_xlat16_2.xyz * float3(u_xlat16_14, u_xlat16_14, u_xlat16_14)) + u_xlat16_3.xyz);
          u_xlat16_2.xyz = (u_xlat16_2.xyz * _LightColor0.xyz);
          u_xlat1_d.xyz = (in_f.texcoord1.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat1_d.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * in_f.texcoord1.xxx) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * in_f.texcoord1.zzz) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = (u_xlat1_d.xyz + conv_mxt4x4_3(unity_WorldToLight).xyz);
          u_xlat1_d.x = dot(u_xlat1_d.xyz, u_xlat1_d.xyz);
          u_xlat1_d.x = tex2D(_LightTexture0, u_xlat1_d.xx).x;
          u_xlat16_14 = dot(in_f.texcoord4.xyz, in_f.texcoord3.xyz);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_14 = min(max(u_xlat16_14, 0), 1);
          #else
          u_xlat16_14 = clamp(u_xlat16_14, 0, 1);
          #endif
          u_xlat16_14 = (u_xlat1_d.x * u_xlat16_14);
          out_f.color.xyz = (float3(u_xlat16_14, u_xlat16_14, u_xlat16_14) * u_xlat16_2.xyz);
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "VertexLit"
}
