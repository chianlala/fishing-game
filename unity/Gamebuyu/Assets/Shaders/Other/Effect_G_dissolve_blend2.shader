Shader "Effect/G_dissolve_blend2"
{
  Properties
  {
    _MainColor ("MainColor", Color) = (0.5,0.5,0.5,1)
    _MainTex ("MainTex", 2D) = "white" {}
    _MainTex_Power ("MainTex_Power", float) = 3
    _alpha ("alpha", float) = 1
    _diss_amount ("diss_amount", Range(-0.01, 1)) = -0.01
    [MaterialToggle] _particle_controlA ("particle_control(A)", float) = -0.01
    _Dissolve_Tex ("Dissolve_Tex", 2D) = "white" {}
    [HideInInspector] _Cutoff ("Alpha cutoff", Range(0, 1)) = 0.5
  }
  SubShader
  {
    Tags
    { 
      "IGNOREPROJECTOR" = "true"
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "LIGHTMODE" = "FORWARDBASE"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
        "SHADOWSUPPORT" = "true"
      }
      ZWrite Off
      Cull Off
      Blend SrcAlpha OneMinusSrcAlpha
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile DIRECTIONAL
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4 _MainColor;
      uniform float _MainTex_Power;
      uniform float _diss_amount;
      uniform float _particle_controlA;
      uniform float4 _Dissolve_Tex_ST;
      uniform float _alpha;
      uniform sampler2D _MainTex;
      uniform sampler2D _Dissolve_Tex;
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
          out_v.texcoord.xy = in_v.texcoord.xy;
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float4 u_xlat16_0;
      int u_xlatb0;
      float4 u_xlat16_1;
      float2 u_xlat2;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.xy = TRANSFORM_TEX(in_f.texcoord.xy, _Dissolve_Tex);
          u_xlat16_0 = tex2D(_Dissolve_Tex, u_xlat0_d.xy);
          u_xlat16_0.x = dot(u_xlat16_0.xyz, float3(0.300000012, 0.589999974, 0.109999999));
          u_xlat0_d.x = (u_xlat16_0.w * u_xlat16_0.x);
          u_xlat2.x = ((-in_f.color.w) + 1);
          u_xlat2.x = ((u_xlat2.x * 1.00999999) + (-0.00999999978));
          u_xlat2.x = (u_xlat2.x + (-_diss_amount));
          u_xlat2.x = ((_particle_controlA * u_xlat2.x) + _diss_amount);
          #ifdef UNITY_ADRENO_ES3
          u_xlatb0 = (u_xlat2.x>=u_xlat0_d.x);
          #else
          u_xlatb0 = (u_xlat2.x>=u_xlat0_d.x);
          #endif
          u_xlat0_d.x = (u_xlatb0)?(0):(1);
          u_xlat2.xy = TRANSFORM_TEX(in_f.texcoord.xy, _MainTex);
          u_xlat16_1 = tex2D(_MainTex, u_xlat2.xy);
          u_xlat2.x = (u_xlat16_1.w * _alpha);
          out_f.color.w = (u_xlat0_d.x * u_xlat2.x);
          #ifdef UNITY_ADRENO_ES3
          out_f.color.w = min(max(out_f.color.w, 0), 1);
          #else
          out_f.color.w = clamp(out_f.color.w, 0, 1);
          #endif
          u_xlat0_d.xyz = (in_f.color.xyz * _MainColor.xyz);
          u_xlat0_d.xyz = (u_xlat16_1.xyz * u_xlat0_d.xyz);
          out_f.color.xyz = (u_xlat0_d.xyz * float3(_MainTex_Power, _MainTex_Power, _MainTex_Power));
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    Pass // ind: 2, name: ShadowCaster
    {
      Name "ShadowCaster"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "LIGHTMODE" = "SHADOWCASTER"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
        "SHADOWSUPPORT" = "true"
      }
      Cull Off
      Offset 1, 1
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile SHADOWS_DEPTH
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4 unity_LightShadowBias;
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      struct appdata_t
      {
          float4 vertex :POSITION0;
      };
      
      struct OUT_Data_Vert
      {
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 vertex :Position;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float u_xlat4;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = UnityObjectToClipPos(in_v.vertex);
          u_xlat1.x = (unity_LightShadowBias.x / u_xlat0.w);
          #ifdef UNITY_ADRENO_ES3
          u_xlat1.x = min(max(u_xlat1.x, 0), 1);
          #else
          u_xlat1.x = clamp(u_xlat1.x, 0, 1);
          #endif
          u_xlat4 = (u_xlat0.z + u_xlat1.x);
          u_xlat1.x = max((-u_xlat0.w), u_xlat4);
          out_v.vertex.xyw = u_xlat0.xyw;
          u_xlat0.x = ((-u_xlat4) + u_xlat1.x);
          out_v.vertex.z = ((unity_LightShadowBias.y * u_xlat0.x) + u_xlat4);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          out_f.color = float4(0, 0, 0, 0);
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Diffuse"
}
