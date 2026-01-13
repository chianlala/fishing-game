Shader "Effect/Dissolve_Disturbance_AlphaBlend"
{
  Properties
  {
    _Color ("Color", Color) = (1,1,1,1)
    _B ("Brightness", Range(0, 5)) = 1
    _MainTex ("Base (RGB)", 2D) = "white" {}
    _DissolveTex ("DissolveMask (RGB)", 2D) = "white" {}
    _DissSize ("DissolveSize", Range(0, 1)) = 0.15
    [HDR] _DissColor ("DissolveColor", Color) = (1,0,0,1)
    [HDR] _AddColor ("EdgeColor", Color) = (1,1,0,1)
    _Opacity ("Opacity", Range(0, 1)) = 1
    _NoiseTex ("Noise Tex(RG)", 2D) = "white" {}
    _Speed ("XY noise1,ZW noise 2", Vector) = (0.5,0.5,0.5,0)
    _NoiseOffset ("Noise Offset", Range(-2, 2)) = 1.5
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
    Pass // ind: 1, name: BR_Effect_Dissolve_AlphaBlend
    {
      Name "BR_Effect_Dissolve_AlphaBlend"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "PreviewType" = "Plane"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
      }
      ZWrite Off
      Cull Off
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
      uniform float4 _MainTex_ST;
      //uniform float4 _Time;
      uniform float _DissSize;
      uniform float _B;
      uniform float4 _DissColor;
      uniform float4 _AddColor;
      uniform float _Opacity;
      uniform float4 _Color;
      uniform float _NoiseOffset;
      uniform float4 _Speed;
      uniform sampler2D _NoiseTex;
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
      float4 u_xlat0_d;
      float4 u_xlat16_0;
      float u_xlat10_0;
      float4 u_xlat16_1;
      float u_xlat16_2;
      float4 u_xlat16_3;
      float u_xlat4;
      float u_xlat16_4;
      int u_xlatb5;
      float u_xlat16_6;
      float u_xlat16_8;
      float3 u_xlat10;
      int u_xlatb11;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d = ((_Time.xxxx * _Speed) + in_f.texcoord.xyxy);
          u_xlat16_0.x = tex2D(_NoiseTex, u_xlat0_d.xy).x;
          u_xlat16_6 = tex2D(_NoiseTex, u_xlat0_d.zw).y;
          u_xlat16_1.x = (u_xlat16_6 * u_xlat16_0.x);
          u_xlat16_1.xy = ((u_xlat16_1.xx * float2(_NoiseOffset, _NoiseOffset)) + in_f.texcoord.xy);
          u_xlat10_0 = tex2D(_DissolveTex, u_xlat16_1.xy).x;
          u_xlat16_1 = tex2D(_MainTex, u_xlat16_1.xy);
          u_xlat16_1 = (u_xlat16_1 * _Color);
          u_xlat16_2 = (((-in_f.color.w) * _Opacity) + 1);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_2 = min(max(u_xlat16_2, 0), 1);
          #else
          u_xlat16_2 = clamp(u_xlat16_2, 0, 1);
          #endif
          u_xlat16_2 = (u_xlat10_0 + (-u_xlat16_2));
          u_xlat16_8 = (u_xlat16_2 / _DissSize);
          u_xlat16_0 = ((-_DissColor) + _AddColor);
          u_xlat16_0 = ((float4(u_xlat16_8, u_xlat16_8, u_xlat16_8, u_xlat16_8) * u_xlat16_0) + _DissColor);
          u_xlat16_3.w = 2;
          u_xlat16_3.xyz = (u_xlat16_1.xyz * in_f.color.xyz);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_3);
          u_xlat16_4 = u_xlat16_0.w;
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_4 = min(max(u_xlat16_4, 0), 1);
          #else
          u_xlat16_4 = clamp(u_xlat16_4, 0, 1);
          #endif
          u_xlat10.xyz = (u_xlat16_0.xyz * float3(2, 2, 2));
          u_xlat4 = (u_xlat16_1.w * u_xlat16_4);
          #ifdef UNITY_ADRENO_ES3
          u_xlatb5 = (u_xlat16_2<_DissSize);
          #else
          u_xlatb5 = (u_xlat16_2<_DissSize);
          #endif
          u_xlat16_2 = (u_xlat16_2 + (-0.00999999978));
          #ifdef UNITY_ADRENO_ES3
          u_xlatb11 = (u_xlat16_2>=0);
          #else
          u_xlatb11 = (u_xlat16_2>=0);
          #endif
          u_xlat16_2 = (u_xlatb11)?(1):(0);
          u_xlat16_8 = (u_xlatb5)?(u_xlat4):(u_xlat16_1.w);
          #ifdef UNITY_ADRENO_ES3
          u_xlat16_8 = min(max(u_xlat16_8, 0), 1);
          #else
          u_xlat16_8 = clamp(u_xlat16_8, 0, 1);
          #endif
          u_xlat16_3.xyz = (int(u_xlatb5))?(u_xlat10.xyz):(u_xlat16_3.xyz);
          u_xlat16_3.xyz = (u_xlat16_3.xyz * float3(float3(_B, _B, _B)));
          u_xlat16_3.xyz = max(u_xlat16_3.xyz, float3(0, 0, 0));
          out_f.color.xyz = min(u_xlat16_3.xyz, float3(8, 8, 8));
          out_f.color.w = (u_xlat16_8 * u_xlat16_2);
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
