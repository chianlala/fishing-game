Shader "TechY/BG_Caustic_BossMask"
{
  Properties
  {
    _MainTex ("MainTex", 2D) = "white" {}
    _BaseColor ("BaseColor", Color) = (0,0,0,0)
    _BaseFactor ("BaseFactor", float) = 1
    _NoiseColor ("NoiseColor", Color) = (0,0,0,0)
    _Caustics ("Caustics", 2D) = "white" {}
    _CausticTile ("CausticTile", float) = 1
    _Speed ("Speed", float) = 1
    _Factor ("Factor", float) = 1
    _Contrast ("Contrast", float) = 1
    _Desaturation ("Desaturation", Range(-1, 1)) = 0
    _MaskTex ("MaskTex", 2D) = "white" {}
    [Toggle(_BOSSMASKON_ON)] _BossMaskOn ("BossMaskOn", float) = 0
    _Radius ("Radius", float) = 1
    _Radius2 ("Radius2", float) = 1
    _Position ("Position", Vector) = (0,0,0,0)
    _Position2 ("Position2", Vector) = (0,0,0,0)
    _RimAlpha ("RimAlpha", float) = 0
    _MaskFactor ("MaskFactor", float) = 1
    [HideInInspector] _texcoord ("", 2D) = "white" {}
    [HideInInspector] __dirty ("", float) = 1
  }
  SubShader
  {
    Tags
    { 
      "FORCENOSHADOWCASTING" = "true"
      "IGNOREPROJECTOR" = "true"
      "IsEmissive" = "true"
      "QUEUE" = "Geometry+0"
      "RenderType" = "Opaque"
    }
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "FORCENOSHADOWCASTING" = "true"
        "IGNOREPROJECTOR" = "true"
        "IsEmissive" = "true"
        "LIGHTMODE" = "FORWARDBASE"
        "QUEUE" = "Geometry+0"
        "RenderType" = "Opaque"
      }
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile DIRECTIONAL
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _texcoord_ST;
      //uniform float4 _Time;
      uniform float4 _NoiseColor;
      uniform float _Desaturation;
      uniform float4 _BaseColor;
      uniform float _BaseFactor;
      uniform float _Speed;
      uniform float _CausticTile;
      uniform float _Contrast;
      uniform float _Factor;
      uniform sampler2D _Caustics;
      uniform sampler2D _MainTex;
      uniform sampler2D _MaskTex;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float4 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float u_xlat6;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));
          out_v.texcoord2.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _texcoord);
          u_xlat0.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat0.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat0.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          out_v.texcoord1.xyz = normalize(u_xlat0.xyz);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat0_d;
      float4 u_xlat16_0;
      float2 u_xlat1_d;
      float u_xlat16_1;
      float3 u_xlat16_2;
      float3 u_xlat16_3;
      float3 u_xlat16_4;
      float2 u_xlat6_d;
      float u_xlat16_6;
      float u_xlat16_11;
      float u_xlat16_16;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0 = (float4(float4(_CausticTile, _CausticTile, _CausticTile, _CausticTile)) * float4(1.5, 1, 1.5, 1));
          u_xlat0_d = (u_xlat16_0 * in_f.texcoord.xyxy);
          u_xlat1_d.x = (_Time.y * _Speed);
          u_xlat0_d = ((u_xlat1_d.xxxx * float4(0.100000001, 0.100000001, (-0.100000001), (-0.100000001))) + u_xlat0_d);
          u_xlat1_d.xy = (u_xlat0_d.zw + float2(0.418000013, 0.354999989));
          u_xlat16_11 = tex2D(_Caustics, u_xlat0_d.xy).x;
          u_xlat16_1 = tex2D(_Caustics, u_xlat1_d.xy).x;
          u_xlat16_1 = min(u_xlat16_1, u_xlat16_11);
          u_xlat16_1 = log2(u_xlat16_1);
          u_xlat16_1 = (u_xlat16_1 * _Contrast);
          u_xlat16_1 = exp2(u_xlat16_1);
          u_xlat16_1 = ((u_xlat16_1 * _Factor) + (-1));
          u_xlat6_d.xy = ((_Time.yy * float2(0.0500000007, 0)) + in_f.texcoord.xy);
          u_xlat16_6 = tex2D(_Caustics, u_xlat6_d.xy).y;
          u_xlat6_d.xy = ((float2(u_xlat16_6, u_xlat16_6) * float2(0.00600000005, 0.00600000005)) + (-in_f.texcoord.xy));
          u_xlat16_16 = tex2D(_MaskTex, u_xlat6_d.xy).x;
          u_xlat16_2.xyz = tex2D(_MainTex, u_xlat6_d.xy).xyz;
          u_xlat16_1 = ((u_xlat16_16 * u_xlat16_1) + 1);
          u_xlat16_6 = dot(u_xlat16_2.xyz, float3(0.298999995, 0.587000012, 0.114));
          u_xlat16_3.xyz = ((-u_xlat16_2.xyz) + float3(u_xlat16_6, u_xlat16_6, u_xlat16_6));
          u_xlat16_3.xyz = ((float3(_Desaturation, _Desaturation, _Desaturation) * u_xlat16_3.xyz) + u_xlat16_2.xyz);
          u_xlat16_4.xyz = (u_xlat16_3.xyz * _BaseColor.xyz);
          u_xlat16_3.xyz = (u_xlat16_3.xyz * _NoiseColor.xyz);
          u_xlat16_4.xyz = ((u_xlat16_4.xyz * float3(_BaseFactor, _BaseFactor, _BaseFactor)) + (-u_xlat16_3.xyz));
          out_f.color.xyz = ((float3(u_xlat16_1, u_xlat16_1, u_xlat16_1) * u_xlat16_4.xyz) + u_xlat16_3.xyz);
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
