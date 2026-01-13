Shader "M-Game/Particles/AlphaBlend"
{
  Properties
  {
    _TintColor ("Tint Color", Color) = (1,1,1,1)
    _MainTex ("Particle Texture", 2D) = "white" {}
    _AlphaTex ("Aplha (RGB)", 2D) = "white" {}
    _Brightness ("Brightness", float) = 2
  }
  SubShader
  {
    Tags
    { 
      "IGNOREPROJECTOR" = "true"
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    LOD 100
    Pass // ind: 1, name: 
    {
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
      }
      LOD 100
      ZWrite Off
      Cull Off
      Fog
      { 
        Mode  Off
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
      uniform float4 _MainTex_ST;
      uniform float4 _TintColor;
      uniform float _Brightness;
      uniform sampler2D _MainTex;
      uniform sampler2D _AlphaTex;
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
          out_v.vertex = UnityObjectToClipPos(in_v.vertex);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.color = (in_v.color * _TintColor);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float4 u_xlat0_d;
      float3 u_xlat10_0;
      float u_xlat1_d;
      float u_xlat10_1;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat10_0.xyz = tex2D(_MainTex, in_f.texcoord.xy).xyz;
          u_xlat0_d.xyz = (u_xlat10_0.xyz * in_f.color.xyz);
          u_xlat0_d.xyz = (u_xlat0_d.xyz * float3(_Brightness, _Brightness, _Brightness));
          u_xlat10_1 = tex2D(_AlphaTex, in_f.texcoord.xy).x;
          u_xlat1_d = (u_xlat10_1 * in_f.color.w);
          u_xlat0_d.w = (u_xlat1_d * _Brightness);
          out_f.color = u_xlat0_d;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
