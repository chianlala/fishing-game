Shader "Fish2DShader"
{
  Properties
  {
    //[PerRendererData]
	_MainTex ("Sprite Texture", 2D) = "white" {}
    _Color ("Tint", Color) = (1,1,1,1)
    _Alpha ("Alpha", Range(0, 1)) = 1
    [MaterialToggle] PixelSnap ("Pixel snap", float) = 0
    [HideInInspector] _RendererColor ("RendererColor", Color) = (1,1,1,1)
    [HideInInspector] _Flip ("Flip", Vector) = (1,1,1,1)
    [PerRendererData] _AlphaTex ("External Alpha", 2D) = "white" {}
    [PerRendererData] _EnableExternalAlpha ("Enable External Alpha", float) = 0
    [Toggle] _Shadow ("Shadow", float) = 1
    _ShadowColor ("ShadowColor", Color) = (0,0,0,1)
    _ShadowOffset ("ShadowOffset", Vector) = (0.04,0.85,0,0.2)
    [HideInInspector] _OverlayColor ("OverlayColor", Color) = (1,1,1,1)
    [HideInInspector] _OverlayMultiple ("OverlayMultiple", float) = 1
    [Toggle] _Overlay ("Overlay", float) = 0
    _HitColor ("_HitColor", Color) = (1,1,1,1)
    _HitMultiple ("_HitMultiple", float) = 1
    [Toggle] _AdjustHSV ("Adjust HSV", float) = 0
    _AdjustHue ("Hue", Range(0, 360)) = 0
    _AdjustSaturation ("Saturation", Range(0, 2)) = 1
    _AdjustValue ("Value", Range(0, 2)) = 1
    [Toggle] _Contrast ("AdjustContrast", float) = 0
  }
  SubShader
  {
    Tags
    { 
      "CanUseSpriteAtlas" = "true"
      "IGNOREPROJECTOR" = "true"
      "PreviewType" = "Plane"
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: 
    {
      Tags
      { 
        "CanUseSpriteAtlas" = "true"
        "IGNOREPROJECTOR" = "true"
        "PreviewType" = "Plane"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
      }
      ZWrite Off
      Cull Off
      Blend SrcAlpha OneMinusSrcAlpha
      //m_ProgramMask = 6
      CGPROGRAM
      #pragma target 4.0
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _RendererColor;
      uniform float2 _Flip;
      uniform float4 _Color;
      uniform float _Alpha;
      uniform float4 _OverlayColor;
      uniform float _OverlayMultiple;
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
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0.xy = (in_v.vertex.xy * _Flip.xy);
          out_v.vertex = UnityObjectToClipPos(u_xlat0);
          u_xlat0 = (in_v.color * _Color);
          u_xlat0 = (u_xlat0 * _RendererColor);
          out_v.color = u_xlat0;
          out_v.texcoord.xy = in_v.texcoord.xy;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat16_0;
      float4 u_xlat16_1;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat16_0.xyz = (_OverlayColor.xyz * float3(_OverlayMultiple, _OverlayMultiple, _OverlayMultiple));
          u_xlat16_1 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat16_1 = (u_xlat16_1 * in_f.color);
          out_f.color.xyz = (u_xlat16_0.xyz * u_xlat16_1.xyz);
          out_f.color.w = (u_xlat16_1.w * _Alpha);
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
