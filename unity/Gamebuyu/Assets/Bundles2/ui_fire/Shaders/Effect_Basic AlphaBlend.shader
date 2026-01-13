Shader "Effect/Basic AlphaBlend"
{
  Properties
  {
    [HDR] _TintColor ("Tint Color", Color) = (1,1,1,1)
    _MainTex ("Particle Texture", 2D) = "white" {}
    _UVScroll ("UV Scroll", Vector) = (0,0,0,0)
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
    Pass // ind: 1, name: BR_Effect_Basic_AlphaBlend
    {
      Name "BR_Effect_Basic_AlphaBlend"
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
      //uniform float4 _Time;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float2 _UVScroll;
      uniform float4 _TintColor;
      uniform sampler2D _MainTex;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float4 color :COLOR0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 color :COLOR0;
          float2 texcoord :TEXCOORD0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 color :COLOR0;
          float2 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float2 u_xlat16_2;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = UnityObjectToClipPos(in_v.vertex);
          out_v.vertex = u_xlat0;
          out_v.color = in_v.color;
          u_xlat16_2.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          u_xlat0.xy = ((_Time.xx * _UVScroll.xy) + u_xlat16_2.xy);
          out_v.texcoord.xy = u_xlat0.xy;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat16_0;
      float4 u_xlat16_1;
      float u_xlat16_6;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0.xyz = (in_f.color.xyz + in_f.color.xyz);
          u_xlat16_1 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat16_1 = (u_xlat16_1 * _TintColor);
          u_xlat16_0.xyz = (u_xlat16_0.xyz * u_xlat16_1.xyz);
          u_xlat16_6 = (u_xlat16_1.w * _TintColor.w);
          out_f.color.w = (u_xlat16_6 * in_f.color.w);
          out_f.color.xyz = (u_xlat16_0.xyz * _TintColor.xyz);
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Mobile/Particles/Additive"
}
