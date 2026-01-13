Shader "BR/Effect/Dissolve/Additive"
{
  Properties
  {
    _Color ("Color", Color) = (1,1,1,1)
    _B ("Brightness", Range(0, 5)) = 1
    _MainTex ("Particle Texture", 2D) = "white" {}
    _UVScroll_MainTex ("UV Scroll MainTex", Vector) = (0,0,0,0)
    _DissolveTex ("DissolveMask (RGB)", 2D) = "white" {}
    _DissSize ("DissolveSize", Range(0, 1)) = 0.15
    _UVScroll_DissolveTex ("UV Scroll DissolveTex", Vector) = (0,0,0,0)
    [HDR] _DissColor ("DissolveColor", Color) = (1,0,0,1)
    [HDR] _AddColor ("EdgeColor", Color) = (1,1,0,1)
    _Opacity ("Opacity", Range(0, 1)) = 1
  }
  SubShader
  {
    Tags
    { 
      "IGNOREPROJECTOR" = "true"
      "PreviewType" = "Plane"
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: BR_Effect_Dissolve_Additive
    {
      Name "BR_Effect_Dissolve_Additive"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "PreviewType" = "Plane"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
      }
      ZWrite Off
      Cull Off
      Blend SrcAlpha One
      // m_ProgramMask = 6
      CGPROGRAM
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4 _Time;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4 _DissolveTex_ST;
      uniform float2 _UVScroll_MainTex;
      uniform float2 _UVScroll_DissolveTex;
      uniform float _DissSize;
      uniform float _B;
      uniform float4 _DissColor;
      uniform float4 _AddColor;
      uniform float _Opacity;
      uniform float4 _Color;
      uniform sampler2D _MainTex;
      uniform sampler2D _DissolveTex;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float2 texcoord1 :TEXCOORD1;
          float4 color :COLOR0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
          float2 texcoord1 :TEXCOORD1;
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
          u_xlat0.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.texcoord.xy = ((_Time.xx * _UVScroll_MainTex.xy) + u_xlat0.xy);
          u_xlat0.xy = TRANSFORM_TEX(in_v.texcoord.xy, _DissolveTex);
          out_v.texcoord1.xy = ((_Time.xx * float2(_UVScroll_DissolveTex.x, _UVScroll_DissolveTex.y)) + u_xlat0.xy);
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float4 u_xlat16_0;
      float u_xlat10_0;
      int u_xlatb0;
      float3 u_xlat16_1;
      float4 u_xlat2;
      float u_xlat3;
      float3 u_xlat16_4;
      float3 u_xlat5;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat10_0 = tex2D(_DissolveTex, in_f.texcoord1.xy).x;
          u_xlat16_1.x = (((-in_f.color.w) * _Opacity) + 1);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_1.x = min(max(u_xlat16_1.x, 0), 1);
          #else
          u_xlat16_1.x = clamp(u_xlat16_1.x, 0, 1);
          #endif
          u_xlat16_1.x = (u_xlat10_0 + (-u_xlat16_1.x));
          u_xlat16_4.x = (u_xlat16_1.x + (-0.00999999978));
          #ifdef UNITY_ADRENO_ES3
          u_xlatb0 = (u_xlat16_4.x<0);
          #else
          u_xlatb0 = (u_xlat16_4.x<0);
          #endif
          if(u_xlatb0)
          {
              out_f.color = float4(0, 0, 0, 0);
              return out_f;
          }
          u_xlat16_0 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat16_0 = (u_xlat16_0 * _Color);
          u_xlat16_4.xyz = (u_xlat16_0.xyz * in_f.color.xyz);
          #ifdef UNITY_ADRENO_ES3
          u_xlatb0 = (u_xlat16_1.x<_DissSize);
          #else
          u_xlatb0 = (u_xlat16_1.x<_DissSize);
          #endif
          u_xlat3 = (u_xlat16_1.x / _DissSize);
          u_xlat2 = ((-_DissColor) + _AddColor);
          u_xlat2 = ((float4(u_xlat3, u_xlat3, u_xlat3, u_xlat3) * u_xlat2) + _DissColor);
          u_xlat2 = (u_xlat2.wxyz + u_xlat2.wxyz);
          u_xlat2.x = u_xlat2.x;
          #ifdef UNITY_ADRENO_ES3
          u_xlat2.x = min(max(u_xlat2.x, 0), 1);
          #else
          u_xlat2.x = clamp(u_xlat2.x, 0, 1);
          #endif
          u_xlat5.xyz = (u_xlat16_4.xyz * u_xlat2.yzw);
          u_xlat3 = (u_xlat16_0.w * u_xlat2.x);
          u_xlat16_1.xyz = (int(u_xlatb0))?(u_xlat5.xyz):(u_xlat16_4.xyz);
          out_f.color.w = (u_xlatb0)?(u_xlat3):(u_xlat16_0.w);
          u_xlat0_d.xyz = (u_xlat16_1.xyz * float3(float3(_B, _B, _B)));
          u_xlat16_1.xyz = max(u_xlat0_d.xyz, float3(0, 0, 0));
          out_f.color.xyz = min(u_xlat16_1.xyz, float3(8, 8, 8));
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
