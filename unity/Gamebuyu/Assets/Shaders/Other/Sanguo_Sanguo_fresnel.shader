Shader "Sanguo/Sanguo_fresnel"
{
  Properties
  {
    _MainTex ("MainTex", 2D) = "white" {}
    _fresnel_exp ("fresnel_exp", Range(-2, 2)) = 1.219828
    _fresnel_glow ("fresnel_glow", float) = 1
    _fresnel_color ("fresnel_color", Color) = (0.5,0.5,0.5,1)
    _main_color ("main_color", Color) = (0.5,0.5,0.5,1)
    _main_glow ("main_glow", float) = 1
    [HideInInspector] _Cutoff ("Alpha cutoff", Range(0, 1)) = 0.5
    _StencilComp ("Stencil Comparison", float) = 8
    _Stencil ("Stencil ID", float) = 0
    _StencilOp ("Stencil Operation", float) = 0
    _StencilWriteMask ("Stencil Write Mask", float) = 255
    _StencilReadMask ("Stencil Read Mask", float) = 255
  }
  SubShader
  {
    Tags
    { 
      "QUEUE" = "Transparent"
      "RenderType" = "Transparent"
    }
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "LIGHTMODE" = "FORWARDBASE"
        "QUEUE" = "Transparent"
        "RenderType" = "Transparent"
        "SHADOWSUPPORT" = "true"
      }
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
      Blend SrcAlpha OneMinusSrcAlpha
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile DIRECTIONAL
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
      #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
      #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
      #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_WorldToObject;
      //uniform float4x4 unity_MatrixVP;
      //uniform float3 _WorldSpaceCameraPos;
      uniform float4 _MainTex_ST;
      uniform float _fresnel_exp;
      uniform float _fresnel_glow;
      uniform float4 _fresnel_color;
      uniform float4 _main_color;
      uniform float _main_glow;
      uniform sampler2D _MainTex;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float2 texcoord :TEXCOORD0;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float4 color :COLOR0;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float4 color :COLOR0;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float u_xlat6;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));
          out_v.texcoord1 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = in_v.texcoord.xy;
          u_xlat0.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat0.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat0.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          out_v.texcoord2.xyz = normalize(u_xlat0.xyz);
          out_v.color = in_v.color;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float3 u_xlat1_d;
      float4 u_xlat16_1;
      float u_xlat2;
      float u_xlat6_d;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.xyz = ((-in_f.texcoord1.xyz) + _WorldSpaceCameraPos.xyz);
          u_xlat0_d.xyz = normalize(u_xlat0_d.xyz);
          u_xlat1_d.xyz = normalize(in_f.texcoord2.xyz);
          u_xlat0_d.x = dot(u_xlat1_d.xyz, u_xlat0_d.xyz);
          u_xlat0_d.x = max(u_xlat0_d.x, 0);
          u_xlat0_d.x = ((-u_xlat0_d.x) + 1);
          u_xlat0_d.x = log2(u_xlat0_d.x);
          u_xlat2 = exp2(_fresnel_exp);
          u_xlat0_d.x = (u_xlat0_d.x * u_xlat2);
          u_xlat0_d.x = exp2(u_xlat0_d.x);
          u_xlat0_d.x = (u_xlat0_d.x * _fresnel_glow);
          u_xlat0_d.xyz = (u_xlat0_d.xxx * _fresnel_color.xyz);
          u_xlat1_d.xy = TRANSFORM_TEX(in_f.texcoord.xy, _MainTex);
          u_xlat16_1 = tex2D(_MainTex, u_xlat1_d.xy);
          u_xlat1_d.xyz = (u_xlat16_1.xyz * _main_color.xyz);
          out_f.color.w = (u_xlat16_1.w * in_f.color.w);
          u_xlat1_d.xyz = (u_xlat1_d.xyz * float3(_main_glow, _main_glow, _main_glow));
          out_f.color.xyz = ((u_xlat1_d.xyz * in_f.color.xyz) + u_xlat0_d.xyz);
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Diffuse"
}
