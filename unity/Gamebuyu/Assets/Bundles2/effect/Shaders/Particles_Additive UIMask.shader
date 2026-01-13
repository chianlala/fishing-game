Shader "Particles/Additive UIMask"
{
  Properties
  {
    _TintColor ("Tint Color", Color) = (0.5,0.5,0.5,0.5)
    _MainTex ("Particle Texture", 2D) = "white" {}
    _InvFade ("Soft Particles Factor", Range(0.01, 3)) = 1
    _MinX ("Min X", float) = -100
    _MaxX ("Max X", float) = 100
    _MinY ("Min Y", float) = -100
    _MaxY ("Max Y", float) = 100
  }
  SubShader
  {
    Tags
    { 
      "IGNOREPROJECTOR" = "true"
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: 
    {
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
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
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4 _TintColor;
      uniform float _MinX;
      uniform float _MaxX;
      uniform float _MinY;
      uniform float _MaxY;
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
          float3 texcoord2 :TEXCOORD2;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 color :COLOR0;
          float2 texcoord :TEXCOORD0;
          float3 texcoord2 :TEXCOORD2;
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
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.texcoord2.xyz = in_v.vertex.xyz;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat16_0;
      float4 u_xlat16_1;
      float2 u_xlatb1;
      float2 u_xlat16_2;
      float2 u_xlat16_8;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0 = (in_f.color + in_f.color);
          u_xlat16_0 = (u_xlat16_0 * _TintColor);
          u_xlat16_1 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat16_0 = (u_xlat16_0 * u_xlat16_1);
          u_xlatb1.xy = bool4(in_f.texcoord2.xyxx >= float4(_MinX, _MinY, _MinX, _MinX)).xy;
          u_xlat16_2.x = (u_xlatb1.x)?(float(1)):(float(0));
          u_xlat16_2.y = (u_xlatb1.y)?(float(1)):(float(0));
          u_xlat16_2.x = (u_xlat16_0.w * u_xlat16_2.x);
          u_xlatb1.xy = bool4(float4(_MaxX, _MaxY, _MaxX, _MaxX) >= in_f.texcoord2.xyxx).xy;
          u_xlat16_8.x = (u_xlatb1.x)?(float(1)):(float(0));
          u_xlat16_8.y = (u_xlatb1.y)?(float(1)):(float(0));
          u_xlat16_2.x = (u_xlat16_8.x * u_xlat16_2.x);
          u_xlat16_2.x = (u_xlat16_2.y * u_xlat16_2.x);
          u_xlat16_2.x = (u_xlat16_8.y * u_xlat16_2.x);
          out_f.color.xyz = (u_xlat16_0.xyz * u_xlat16_2.xxx);
          out_f.color.w = u_xlat16_2.x;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
