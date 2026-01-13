Shader "ErbGameArt/Particles/Add_Trail"
{
  Properties
  {
    _MainTexture ("MainTexture", 2D) = "white" {}
    _SpeedMainTexUVNoiseZW ("Speed MainTex U/V + Noise Z/W", Vector) = (0,0,0,0)
    _StartColor ("StartColor", Color) = (1,0,0,1)
    _EndColor ("EndColor", Color) = (1,1,0,1)
    _Colorpower ("Color power", float) = 1
    _Colorrange ("Color range", float) = 1
    _Noise ("Noise", 2D) = "white" {}
    [Toggle(_USEDEPTH_ON)] _Usedepth ("Use depth?", float) = 0
    _Depthpower ("Depth power", float) = 1
    _Emission ("Emission", float) = 2
    [Toggle(_USEDARK_ON)] _Usedark ("Use dark", float) = 0
    [HideInInspector] _texcoord ("", 2D) = "white" {}
    [HideInInspector] __dirty ("", float) = 1
  }
  SubShader
  {
    Tags
    { 
      "IGNOREPROJECTOR" = "true"
      "IsEmissive" = "true"
      "PreviewType" = "Plane"
      "QUEUE" = "Transparent+0"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "IsEmissive" = "true"
        "LIGHTMODE" = "FORWARDBASE"
        "PreviewType" = "Plane"
        "QUEUE" = "Transparent+0"
        "RenderType" = "Transparent"
      }
      ZWrite Off
      Cull Off
      Blend SrcAlpha OneMinusSrcAlpha
      ColorMask RGB
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
      uniform float4 _StartColor;
      uniform float4 _EndColor;
      uniform float _Colorrange;
      uniform float _Colorpower;
      uniform float _Emission;
      uniform float4 _SpeedMainTexUVNoiseZW;
      uniform float4 _MainTexture_ST;
      uniform float4 _Noise_ST;
      uniform sampler2D _MainTexture;
      uniform sampler2D _Noise;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float4 texcoord :TEXCOORD0;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float4 color :COLOR0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
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
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat0_d;
      float u_xlat16_0;
      float3 u_xlat1_d;
      float2 u_xlat2;
      float3 u_xlat3;
      float2 u_xlat4;
      float u_xlat16_4;
      float u_xlat6_d;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.xy = TRANSFORM_TEX(in_f.texcoord.xy, _Noise);
          u_xlat0_d.xy = ((_SpeedMainTexUVNoiseZW.zw * _Time.yy) + u_xlat0_d.xy);
          u_xlat16_0 = tex2D(_Noise, u_xlat0_d.xy).x;
          u_xlat2.xy = ((-in_f.texcoord.xy) + float2(1, 1));
          u_xlat6_d = log2(u_xlat2.x);
          u_xlat2.x = (u_xlat2.y * u_xlat2.x);
          u_xlat2.x = (u_xlat2.x * in_f.texcoord.y);
          u_xlat2.x = (u_xlat2.x * 6);
          #ifdef UNITY_ADRENO_ES3
          u_xlat2.x = min(max(u_xlat2.x, 0), 1);
          #else
          u_xlat2.x = clamp(u_xlat2.x, 0, 1);
          #endif
          u_xlat4.x = (u_xlat6_d * 0.800000012);
          u_xlat4.x = exp2(u_xlat4.x);
          u_xlat4.x = max(u_xlat4.x, 0.200000003);
          u_xlat4.x = min(u_xlat4.x, 0.600000024);
          u_xlat0_d.x = (u_xlat4.x + u_xlat16_0);
          u_xlat0_d.x = (u_xlat0_d.x + (-in_f.texcoord.x));
          #ifdef UNITY_ADRENO_ES3
          u_xlat0_d.x = min(max(u_xlat0_d.x, 0), 1);
          #else
          u_xlat0_d.x = clamp(u_xlat0_d.x, 0, 1);
          #endif
          u_xlat4.xy = TRANSFORM_TEX(in_f.texcoord.xy, _MainTexture);
          u_xlat4.xy = ((_SpeedMainTexUVNoiseZW.xy * _Time.yy) + u_xlat4.xy);
          u_xlat16_4 = tex2D(_MainTexture, u_xlat4.xy).x;
          u_xlat16_4 = (u_xlat16_4 * in_f.color.w);
          u_xlat0_d.x = (u_xlat0_d.x * u_xlat16_4);
          u_xlat0_d.w = (u_xlat2.x * u_xlat0_d.x);
          u_xlat1_d.x = (in_f.texcoord.x * _Colorrange);
          u_xlat1_d.x = log2(u_xlat1_d.x);
          u_xlat1_d.x = (u_xlat1_d.x * _Colorpower);
          u_xlat1_d.x = exp2(u_xlat1_d.x);
          u_xlat1_d.x = min(u_xlat1_d.x, 1);
          u_xlat3.xyz = ((-_StartColor.xyz) + _EndColor.xyz);
          u_xlat1_d.xyz = ((u_xlat1_d.xxx * u_xlat3.xyz) + _StartColor.xyz);
          u_xlat1_d.xyz = (u_xlat1_d.xyz * in_f.color.xyz);
          u_xlat0_d.xyz = (u_xlat1_d.xyz * float3(float3(_Emission, _Emission, _Emission)));
          out_f.color = u_xlat0_d;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
