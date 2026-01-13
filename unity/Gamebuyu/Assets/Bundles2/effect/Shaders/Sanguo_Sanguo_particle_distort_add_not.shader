Shader "Sanguo/Sanguo_particle_distort_add"
{
  Properties
  {
    _MainTex ("MainTex", 2D) = "white" {}
    _distort_tex ("distort_tex", 2D) = "white" {}
    _QD ("QD", float) = 0.1
    _U ("U", float) = 0.2
    _V ("V", float) = 0.1
    _U_MainTex ("U_MainTex", float) = 0
    _V_MainTex ("V_MainTex", float) = 0
    _Glow ("Glow", float) = 5
    _Color ("Color", Color) = (0.5,0.5,0.5,1)
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
      "QUEUE" = "Transparent+1"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "IGNOREPROJECTOR" = "true"
        "LIGHTMODE" = "FORWARDBASE"
        "QUEUE" = "Transparent+1"
        "RenderType" = "Transparent"
        "SHADOWSUPPORT" = "true"
      }
      ZWrite Off
      Cull Off
      Blend One One
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
      //uniform float4 _Time;
      uniform float4 _TimeEditor;
      uniform float4 _MainTex_ST;
      uniform float4 _distort_tex_ST;
      uniform float _QD;
      uniform float _U;
      uniform float _V;
      uniform float _U_MainTex;
      uniform float _V_MainTex;
      uniform float _Glow;
      uniform float4 _Color;
      uniform float _MinX;
      uniform float _MaxX;
      uniform float _MinY;
      uniform float _MaxY;
      uniform sampler2D _distort_tex;
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
          float3 texcoord2 :TEXCOORD2;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
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
          out_v.texcoord.xy = in_v.texcoord.xy;
          out_v.color = in_v.color;
          out_v.texcoord2.xyz = in_v.vertex.xyz;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float4 u_xlat16_0;
      float4 u_xlat1_d;
      float3 u_xlat16_1;
      float4 u_xlatb1;
      float4 u_xlat2;
      float2 u_xlat6;
      float u_xlat9;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.x = (_Time.y + _TimeEditor.y);
          u_xlat6.x = (u_xlat0_d.x * _U_MainTex);
          u_xlat6.y = (u_xlat0_d.x * _V_MainTex);
          u_xlat0_d.xy = ((float2(_U, _V) * u_xlat0_d.xx) + in_f.texcoord.xy);
          u_xlat0_d.xy = TRANSFORM_TEX(u_xlat0_d.xy, _distort_tex);
          u_xlat16_1.xyz = tex2D(_distort_tex, u_xlat0_d.xy).xyz;
          u_xlat0_d.xy = (u_xlat6.xy + in_f.texcoord.xy);
          u_xlat0_d.xy = ((u_xlat16_1.xx * float2(_QD, _QD)) + u_xlat0_d.xy);
          u_xlat0_d.xy = TRANSFORM_TEX(u_xlat0_d.xy, _MainTex);
          u_xlat16_0 = tex2D(_MainTex, u_xlat0_d.xy);
          u_xlat2 = (in_f.color * _Color);
          u_xlat2 = (u_xlat16_0 * u_xlat2);
          u_xlat2 = (u_xlat2 * float4(float4(_Glow, _Glow, _Glow, _Glow)));
          u_xlat0_d.xyz = (u_xlat16_1.xyz * u_xlat2.xyz);
          u_xlat0_d.xyz = (u_xlat2.www * u_xlat0_d.xyz);
          u_xlat0_d.xyz = (u_xlat16_0.www * u_xlat0_d.xyz);
          u_xlatb1.xy = bool4(in_f.texcoord2.xyxx >= float4(_MinX, _MinY, _MinX, _MinX)).xy;
          u_xlatb1.zw = bool4(float4(_MaxX, _MaxX, _MaxX, _MaxY) >= in_f.texcoord2.xxxy).zw;
          u_xlat1_d = lerp(float4(0, 0, 0, 0), float4(1, 1, 1, 1), float4(u_xlatb1));
          u_xlat9 = (u_xlat1_d.z * u_xlat1_d.x);
          u_xlat9 = (u_xlat1_d.y * u_xlat9);
          u_xlat9 = (u_xlat1_d.w * u_xlat9);
          out_f.color.xyz = (float3(u_xlat9, u_xlat9, u_xlat9) * u_xlat0_d.xyz);
          out_f.color.w = u_xlat9;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Mobile/Particles/Additive"
}
