Shader "Effect/DissolveParticleAdd"
{
  Properties
  {
    [HDR] _Color ("Color", Color) = (1,1,1,1)
    _MainTex ("Base 2D", 2D) = "white" {}
    _DissolveMap ("DissolveMap", 2D) = "white" {}
    _AlpharFactor ("AlphaFactor", Range(0, 1)) = 0.2
  }
  SubShader
  {
    Tags
    { 
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: 
    {
      Tags
      { 
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
      }
      Blend SrcAlpha One
      // m_ProgramMask = 6
      CGPROGRAM
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float _AlpharFactor;
      uniform float4 _Color;
      uniform sampler2D _DissolveMap;
      uniform sampler2D _MainTex;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
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
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = UnityObjectToClipPos(in_v.vertex);
          out_v.vertex = u_xlat0;
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float u_xlat0_d;
      float u_xlat16_1;
      float4 u_xlat2;
      float3 u_xlat16_3;
      int u_xlatb3;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d = tex2D(_DissolveMap, in_f.texcoord.xy).w;
          u_xlat16_1 = ((-in_f.color.w) + 1);
          #ifdef UNITY_ADRENO_ES3
          u_xlatb3 = (u_xlat0_d<u_xlat16_1);
          #else
          u_xlatb3 = (u_xlat0_d<u_xlat16_1);
          #endif
          if(((int(u_xlatb3) * int(4294967295))!=0))
          {
              discard;
          }
          u_xlat16_3.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat2.xyz = (u_xlat16_3.xyz * _Color.xyz);
          u_xlat0_d = (u_xlat0_d + (-_AlpharFactor));
          #ifdef UNITY_ADRENO_ES3
          u_xlatb3 = (u_xlat0_d<u_xlat16_1);
          #else
          u_xlatb3 = (u_xlat0_d<u_xlat16_1);
          #endif
          if(u_xlatb3)
          {
              u_xlat0_d = ((-u_xlat0_d) + u_xlat16_1);
              u_xlat0_d = (u_xlat0_d / _AlpharFactor);
              u_xlat2.w = ((-u_xlat0_d) + 1);
              #ifdef UNITY_ADRENO_ES3
              u_xlat2.w = min(max(u_xlat2.w, 0), 1);
              #else
              u_xlat2.w = clamp(u_xlat2.w, 0, 1);
              #endif
              out_f.color = u_xlat2;
              return out_f;
          }
          out_f.color.xyz = u_xlat2.xyz;
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Diffuse"
}
