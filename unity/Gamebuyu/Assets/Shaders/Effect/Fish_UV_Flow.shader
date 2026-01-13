Shader "Fish/UV_Flow"
{
  Properties
  {
    _Texture0 ("Texture 0", 2D) = "white" {}
    _R_ColoR ("R_ColoR", Color) = (0.5019608,0.5019608,0.5019608,0.5)
    _R_Intensity ("R_Intensity", float) = 1
    _Flow_Colpr ("Flow_Colpr", Color) = (0.5019608,0.5019608,0.5019608,0.5)
    _Rate ("Rate", float) = 2
    _G_UV_Tiling ("G_UV_Tiling", Vector) = (1,1,0,0)
    _G_Uv_SpeeD ("G_Uv_SpeeD", Vector) = (1,0,0,0)
    _Flow_Intensity ("Flow_Intensity", float) = 1
    [HideInInspector] _texcoord ("", 2D) = "white" {}
    [HideInInspector] __dirty ("", float) = 1
  }
  SubShader
  {
    Tags
    { 
      "IsEmissive" = "true"
      "QUEUE" = "Transparent"
      "RenderType" = "Opaque"
    }
    Pass // ind: 1, name: FORWARD
    {
      //Name "FORWARD"
      Tags
      { 
        "IsEmissive" = "true"
        "LIGHTMODE" = "FORWARDBASE"
       // "QUEUE" = "Transparent"
        "RenderType" = "Opaque"
      }
      ZWrite Off
      Blend One One
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
      uniform float4 _texcoord_ST;
      //uniform float4 _Time;
      uniform float4 _R_ColoR;
      uniform float _R_Intensity;
      uniform float _Rate;
      uniform float4 _Texture0_ST;
      uniform float2 _G_UV_Tiling;
      uniform float2 _G_Uv_SpeeD;
      uniform float _Flow_Intensity;
      uniform float4 _Flow_Colpr;
      uniform sampler2D _Texture0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float3 normal :NORMAL0;
          float4 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float2 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float4 texcoord5 :TEXCOORD5;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float2 texcoord :TEXCOORD0;
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
          out_v.texcoord2.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _texcoord);
          u_xlat0.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat0.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat0.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          out_v.texcoord1.xyz = normalize(u_xlat0.xyz);
          out_v.texcoord2.w = 0;
          out_v.texcoord3.xyz = float3(0, 0, 0);
          out_v.texcoord5 = float4(0, 0, 0, 0);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float2 u_xlat1_d;
      float2 u_xlat10_1;
      float3 u_xlat2;
      float3 u_xlat3;
      float u_xlat6_d;
      float u_xlat10_6;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.x = (_Time.y * _Rate);
          u_xlat0_d.x = sin(u_xlat0_d.x);
          u_xlat0_d.x = ((u_xlat0_d.x * 0.5) + 0.5);
          u_xlat2.xyz = (_R_ColoR.xyz * float3(_R_Intensity, _R_Intensity, _R_Intensity));
          u_xlat0_d.xyz = (u_xlat0_d.xxx * u_xlat2.xyz);
          u_xlat1_d.xy = (_Time.yy * float2(_G_Uv_SpeeD.x, _G_Uv_SpeeD.y));
          u_xlat1_d.xy = ((in_f.texcoord.xy * _G_UV_Tiling.xy) + u_xlat1_d.xy);
          u_xlat10_6 = tex2D(_Texture0, u_xlat1_d.xy).y;
          u_xlat1_d.xy = TRANSFORM_TEX(in_f.texcoord.xy, _Texture0);
          u_xlat10_1.xy = tex2D(_Texture0, u_xlat1_d.xy).xz;
          u_xlat6_d = (u_xlat10_6 * u_xlat10_1.y);
          u_xlat6_d = (u_xlat6_d * _Flow_Intensity);
          u_xlat3.xyz = (float3(u_xlat6_d, u_xlat6_d, u_xlat6_d) * _Flow_Colpr.xyz);
          u_xlat0_d.xyz = ((u_xlat0_d.xyz * u_xlat10_1.xxx) + u_xlat3.xyz);
          u_xlat0_d.xyz = clamp(u_xlat0_d.xyz, 0, 1);
          out_f.color.xyz = u_xlat0_d.xyz;
          out_f.color.w = 1;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    //Pass // ind: 2, name: FORWARD
    //{
    //  //Name "FORWARD"
    //  Tags
    //  { 
    //    "IsEmissive" = "true"
    //    "LIGHTMODE" = "FORWARDADD"
    //    //"QUEUE" = "Transparent+0"
    //    "RenderType" = "Opaque"
    //  }
    //  ZWrite Off
    //  Blend One One
    //  // m_ProgramMask = 6
    //  CGPROGRAM
    //  #pragma multi_compile POINT
    //  //#pragma target 4.0
    //  
    //  #pragma vertex vert
    //  #pragma fragment frag
    //  
    //  #include "UnityCG.cginc"
    //  #define conv_mxt4x4_0(mat4x4) float4(mat4x4[0].x,mat4x4[1].x,mat4x4[2].x,mat4x4[3].x)
    //  #define conv_mxt4x4_1(mat4x4) float4(mat4x4[0].y,mat4x4[1].y,mat4x4[2].y,mat4x4[3].y)
    //  #define conv_mxt4x4_2(mat4x4) float4(mat4x4[0].z,mat4x4[1].z,mat4x4[2].z,mat4x4[3].z)
    //  #define conv_mxt4x4_3(mat4x4) float4(mat4x4[0].w,mat4x4[1].w,mat4x4[2].w,mat4x4[3].w)
    //  
    //  
    //  #define CODE_BLOCK_VERTEX
    //  //uniform float4x4 unity_ObjectToWorld;
    //  //uniform float4x4 unity_WorldToObject;
    //  //uniform float4x4 unity_MatrixVP;
    //  uniform float4x4 unity_WorldToLight;
    //  struct appdata_t
    //  {
    //      float4 vertex :POSITION0;
    //      float3 normal :NORMAL0;
    //  };
    //  
    //  struct OUT_Data_Vert
    //  {
    //      float3 texcoord :TEXCOORD0;
    //      float3 texcoord1 :TEXCOORD1;
    //      float3 texcoord2 :TEXCOORD2;
    //      float4 vertex :SV_POSITION;
    //  };
    //  
    //  struct v2f
    //  {
    //      float3 texcoord :TEXCOORD0;
    //      float3 texcoord1 :TEXCOORD1;
    //      float3 texcoord2 :TEXCOORD2;
    //      float4 vertex :Position;
    //  };
    //  
    //  struct OUT_Data_Frag
    //  {
    //      float4 color :SV_Target0;
    //  };
    //  
    //  float4 u_xlat0;
    //  float4 u_xlat1;
    //  float4 u_xlat2;
    //  float u_xlat10;
    //  OUT_Data_Vert vert(appdata_t in_v)
    //  {
    //      OUT_Data_Vert out_v;
    //      u_xlat1 = mul(unity_ObjectToWorld, float4(in_v.vertex.xyz,1.0));
    //      out_v.vertex = mul(unity_MatrixVP, u_xlat1);
    //      u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
    //      u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
    //      u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
    //      out_v.texcoord.xyz = normalize(u_xlat1.xyz);
    //      out_v.texcoord1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
    //      u_xlat0 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
    //      u_xlat1.xyz = (u_xlat0.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
    //      u_xlat1.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * u_xlat0.xxx) + u_xlat1.xyz);
    //      u_xlat0.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * u_xlat0.zzz) + u_xlat1.xyz);
    //      out_v.texcoord2.xyz = ((conv_mxt4x4_3(unity_WorldToLight).xyz * u_xlat0.www) + u_xlat0.xyz);
    //      return out_v;
    //  }
    //  
    //  #define CODE_BLOCK_FRAGMENT
    //  OUT_Data_Frag frag(v2f in_f)
    //  {
    //      OUT_Data_Frag out_f;
    //      out_f.color = float4(0, 0, 0, 1);
    //      return out_f;
    //  }
    //  
    //  
    //  ENDCG
    //  
    //} // end phase
  }
  FallBack Off
}
