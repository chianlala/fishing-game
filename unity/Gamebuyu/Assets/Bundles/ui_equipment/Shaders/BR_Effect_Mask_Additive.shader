Shader "BR/Effect/Mask/Additive"
{
  Properties
  {
    [HDR] _TintColor ("Tint Color", Color) = (0.5,0.5,0.5,0.5)
    _MainTex ("Particle Texture", 2D) = "white" {}
    _Mask ("Mask Texture", 2D) = "white" {}
    _UVScrollTex ("UV Scroll Tex", Vector) = (0,0,0,0)
    _UVScrollMask ("UV Scroll Mask", Vector) = (0,0,0,0)
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
    Pass // ind: 1, name: BR_Effect_Mask_Additive
    {
      Name "BR_Effect_Mask_Additive"
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
      ColorMask RGB
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
      uniform float4 _Mask_ST;
      uniform float2 _UVScrollTex;
      uniform float2 _UVScrollMask;
      uniform float4 _TintColor;
      uniform sampler2D _MainTex;
      uniform sampler2D _Mask;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float4 color :COLOR0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 color :COLOR0;
          float4 texcoord :TEXCOORD0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 color :COLOR0;
          float4 texcoord :TEXCOORD0;
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
          out_v.vertex = UnityObjectToClipPos(in_v.vertex);
          out_v.color = in_v.color;
          #ifdef UNITY_ADRENO_ES3
          out_v.color = min(max(out_v.color, 0), 1);
          #else
          out_v.color = clamp(out_v.color, 0, 1);
          #endif
          u_xlat0.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.texcoord.xy = ((_Time.yy * _UVScrollTex.xy) + u_xlat0.xy);
          u_xlat0.xy = TRANSFORM_TEX(in_v.texcoord.xy, _Mask);
          out_v.texcoord.zw = ((_Time.yy * float2(_UVScrollMask.x, _UVScrollMask.y)) + u_xlat0.xy);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat0_d;
      float4 u_xlat16_0;
      float4 u_xlat1_d;
      float4 u_xlat16_1;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat1_d = (in_f.color * _TintColor);
          u_xlat0_d = (u_xlat16_0 * u_xlat1_d);
          u_xlat16_1 = tex2D(_Mask, in_f.texcoord.zw);
          u_xlat0_d = (u_xlat0_d * u_xlat16_1);
          u_xlat0_d = (u_xlat0_d + u_xlat0_d);
          out_f.color = u_xlat0_d;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
