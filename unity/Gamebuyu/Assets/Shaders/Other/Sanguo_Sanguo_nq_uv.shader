Shader "Sanguo/Sanguo_nq_uv"
{
  Properties
  {
    _nq_texture ("nq_texture", 2D) = "white" {}
    _U ("U", float) = 0
    _V ("V", float) = 0
    _nq_qd ("nq_qd", float) = 0.15
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
    Pass // ind: 1, name: 
    {
      Tags
      { 
      }
      ZClip Off
      ZWrite Off
      Cull Off
      Stencil
      { 
        Ref 0
        ReadMask 0
        WriteMask 0
        Pass Keep
        Fail Keep
        ZFail Keep
        PassFront Keep
        FailFront Keep
        ZFailFront Keep
        PassBack Keep
        FailBack Keep
        ZFailBack Keep
      } 
      // m_ProgramMask = 0
      
    } // end phase
    Pass // ind: 2, name: FORWARD
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
      //uniform float4 _Time;
      //uniform float4 _ProjectionParams;
      uniform float4 _TimeEditor;
      uniform float4 _nq_texture_ST;
      uniform float _U;
      uniform float _V;
      uniform float _nq_qd;
      uniform sampler2D _nq_texture;
      uniform sampler2D _GrabTexture;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 color :COLOR0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
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
          out_v.texcoord1 = u_xlat0;
          out_v.texcoord.xy = in_v.texcoord.xy;
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float2 u_xlat0_d;
      float3 u_xlat16_0;
      float3 u_xlat1_d;
      float2 u_xlat4;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.x = (_Time.y + _TimeEditor.y);
          u_xlat0_d.xy = ((float2(_U, _V) * u_xlat0_d.xx) + in_f.texcoord.xy);
          u_xlat0_d.xy = TRANSFORM_TEX(u_xlat0_d.xy, _nq_texture);
          u_xlat16_0.xy = tex2D(_nq_texture, u_xlat0_d.xy).xy;
          u_xlat0_d.xy = (u_xlat16_0.xy * in_f.color.ww);
          u_xlat4.x = (_ProjectionParams.x * _ProjectionParams.x);
          u_xlat1_d.xy = (in_f.texcoord1.xy / in_f.texcoord1.ww);
          u_xlat1_d.z = (u_xlat4.x * u_xlat1_d.y);
          u_xlat4.xy = ((u_xlat1_d.xz * float2(0.5, 0.5)) + float2(0.5, 0.5));
          u_xlat0_d.xy = ((u_xlat0_d.xy * float2(float2(_nq_qd, _nq_qd))) + u_xlat4.xy);
          u_xlat16_0.xyz = tex2D(_GrabTexture, u_xlat0_d.xy).xyz;
          out_f.color.xyz = u_xlat16_0.xyz;
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Diffuse"
}
