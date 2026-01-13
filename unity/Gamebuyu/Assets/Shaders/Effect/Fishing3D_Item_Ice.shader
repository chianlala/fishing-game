Shader "Fishing3D/Item/Ice"
{
  Properties
  {
    _Color ("Main Color", Color) = (1,1,1,1)
    _SpecColor ("Specular Color", Color) = (0.5,0.5,0.5,1)
    _Shininess ("Shininess", Range(0.01, 1)) = 0.078125
    _ReflectColor ("Reflection Color", Color) = (1,1,1,0.5)
    _ReflectionStrength ("ReflectionStrength", Range(1, 20)) = 1
    _MainTex ("Base (RGB) Emission Tex (A)", 2D) = "white" {}
    _Opacity ("Material opacity", Range(-1, 1)) = 0.5
    _Cube ("Reflection Cubemap", Cube) = "" {}
    _BumpMap ("Normalmap", 2D) = "bump" {}
    _FPOW ("FPOW Fresnel", float) = 5
    _R0 ("R0 Fresnel", float) = 0.05
    _CutoffIce ("Cutoff", Range(0, 1)) = 0.5
    _LightStr ("Light strength", Range(0, 1)) = 1
  }
  SubShader
  {
    Tags
    { 
     // "QUEUE" = "Transparent"
      "RenderType" = "Transperent"
    }
    LOD 200
    Pass // ind: 1, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "LIGHTMODE" = "FORWARDBASE"
        "QUEUE" = "Transparent"
        "RenderType" = "Transperent"
      }
      LOD 200
      ZWrite Off
      Blend SrcAlpha OneMinusSrcAlpha
      ColorMask RGB
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
      //uniform float4 unity_WorldTransformParams;
      //uniform float4x4 unity_MatrixVP;
      uniform float4 _MainTex_ST;
      uniform float4 _BumpMap_ST;
      //uniform float3 _WorldSpaceCameraPos;
      //uniform float4 _WorldSpaceLightPos0;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float4 _ReflectColor;
      uniform float _ReflectionStrength;
      uniform float _FPOW;
      uniform float _R0;
      uniform float _Opacity;
      uniform float _CutoffIce;
      uniform float _LightStr;
      uniform sampler2D _MainTex;
      uniform sampler2D _BumpMap;
      uniform samplerCUBE _Cube;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float4 tangent :TANGENT0;
          float3 normal :NORMAL0;
          float4 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float4 texcoord3 :TEXCOORD3;
          float4 texcoord6 :TEXCOORD6;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float4 texcoord1 :TEXCOORD1;
          float4 texcoord2 :TEXCOORD2;
          float4 texcoord3 :TEXCOORD3;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float4 u_xlat2;
      float3 u_xlat3;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));
          u_xlat0.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.texcoord.zw = TRANSFORM_TEX(in_v.texcoord.xy, _BumpMap);
          out_v.texcoord1.w = u_xlat0.x;
          u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat0.x = dot(u_xlat1.xyz, u_xlat1.xyz);
          u_xlat0.x = rsqrt(u_xlat0.x);
          u_xlat1.xyz = (u_xlat0.xxx * u_xlat1.xyz);
          u_xlat2.xyz = (in_v.tangent.yyy * conv_mxt4x4_1(unity_ObjectToWorld).yzx);
          u_xlat2.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).yzx * in_v.tangent.xxx) + u_xlat2.xyz);
          u_xlat2.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).yzx * in_v.tangent.zzz) + u_xlat2.xyz);
          u_xlat0.x = dot(u_xlat2.xyz, u_xlat2.xyz);
          u_xlat0.x = rsqrt(u_xlat0.x);
          u_xlat2.xyz = (u_xlat0.xxx * u_xlat2.xyz);
          u_xlat3.xyz = (u_xlat1.xyz * u_xlat2.xyz);
          u_xlat3.xyz = ((u_xlat1.zxy * u_xlat2.yzx) + (-u_xlat3.xyz));
          u_xlat0.x = (in_v.tangent.w * unity_WorldTransformParams.w);
          u_xlat3.xyz = (u_xlat0.xxx * u_xlat3.xyz);
          out_v.texcoord1.y = u_xlat3.x;
          out_v.texcoord1.x = u_xlat2.z;
          out_v.texcoord1.z = u_xlat1.y;
          out_v.texcoord2.x = u_xlat2.x;
          out_v.texcoord3.x = u_xlat2.y;
          out_v.texcoord2.z = u_xlat1.z;
          out_v.texcoord3.z = u_xlat1.x;
          out_v.texcoord2.w = u_xlat0.y;
          out_v.texcoord3.w = u_xlat0.z;
          out_v.texcoord2.y = u_xlat3.y;
          out_v.texcoord3.y = u_xlat3.z;
          out_v.texcoord6 = float4(0, 0, 0, 0);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float3 u_xlat10_0;
      int u_xlatb0;
      float3 u_xlat1_d;
      float4 u_xlat10_1;
      float3 u_xlat10_2;
      float3 u_xlat16_3;
      float3 u_xlat16_4;
      float u_xlat5;
      float u_xlat15;
      float u_xlat16_18;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat0_d.x = in_f.texcoord1.w;
          u_xlat0_d.y = in_f.texcoord2.w;
          u_xlat0_d.z = in_f.texcoord3.w;
          u_xlat0_d.xyz = ((-u_xlat0_d.xyz) + _WorldSpaceCameraPos.xyz);
          u_xlat0_d.xyz = normalize(u_xlat0_d.xyz);
          u_xlat1_d.xyz = (u_xlat0_d.yyy * in_f.texcoord2.xyz);
          u_xlat1_d.xyz = ((in_f.texcoord1.xyz * u_xlat0_d.xxx) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = ((in_f.texcoord3.xyz * u_xlat0_d.zzz) + u_xlat1_d.xyz);
          u_xlat1_d.xyz = normalize(u_xlat1_d.xyz);
          u_xlat10_2.xyz = tex2D(_BumpMap, in_f.texcoord.zw).xyz;
          u_xlat16_3.xyz = ((u_xlat10_2.xyz * float3(2, 2, 2)) + float3(-1, (-1), (-1)));
          u_xlat15 = dot(u_xlat16_3.xyz, u_xlat1_d.xyz);
          u_xlat15 = ((-u_xlat15) + 1);
          u_xlat15 = clamp(u_xlat15, 0, 1);
          u_xlat15 = log2(u_xlat15);
          u_xlat15 = (u_xlat15 * _FPOW);
          u_xlat15 = exp2(u_xlat15);
          u_xlat1_d.x = ((-_R0) + 1);
          u_xlat15 = ((u_xlat1_d.x * u_xlat15) + _R0);
          u_xlat16_4.x = dot(in_f.texcoord1.xyz, u_xlat16_3.xyz);
          u_xlat16_4.y = dot(in_f.texcoord2.xyz, u_xlat16_3.xyz);
          u_xlat16_4.z = dot(in_f.texcoord3.xyz, u_xlat16_3.xyz);
          u_xlat1_d.x = dot((-u_xlat0_d.xyz), u_xlat16_4.xyz);
          u_xlat1_d.x = (u_xlat1_d.x + u_xlat1_d.x);
          u_xlat0_d.xyz = ((u_xlat16_4.xyz * (-u_xlat1_d.xxx)) + (-u_xlat0_d.xyz));
          u_xlat10_0.xyz = texCUBE(_Cube, u_xlat0_d.xyz).xyz;
          u_xlat10_1 = tex2D(_MainTex, in_f.texcoord.xy);
          u_xlat1_d.xyz = (u_xlat10_1.xyz * _Color.xyz);
          u_xlat16_3.xyz = ((u_xlat10_0.xyz * u_xlat10_1.www) + (-u_xlat1_d.xyz));
          u_xlat16_3.xyz = ((float3(u_xlat15, u_xlat15, u_xlat15) * u_xlat16_3.xyz) + u_xlat1_d.xyz);
          u_xlat0_d.xyz = (u_xlat16_3.xyz * _ReflectColor.xyz);
          u_xlat0_d.xyz = (u_xlat0_d.xyz * float3(_ReflectionStrength, _ReflectionStrength, _ReflectionStrength));
          u_xlat0_d.xyz = (u_xlat0_d.xyz * float3(float3(_LightStr, _LightStr, _LightStr)));
          u_xlat16_3.xyz = (_LightColor0.xyz * _Color.xyz);
          u_xlat1_d.xyz = normalize(u_xlat16_4.xyz);
          u_xlat16_18 = dot(u_xlat1_d.xyz, _WorldSpaceLightPos0.xyz);
          u_xlat16_18 = max(u_xlat16_18, 0);
          out_f.color.xyz = ((u_xlat16_3.xyz * float3(u_xlat16_18, u_xlat16_18, u_xlat16_18)) + u_xlat0_d.xyz);
          u_xlatb0 = (u_xlat10_1.w<_CutoffIce);
          u_xlat5 = (u_xlat10_1.w + _Opacity);
          u_xlat0_d.x = (u_xlatb0)?(u_xlat5):(float(0));
          out_f.color.w = u_xlat0_d.x;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
    Pass // ind: 2, name: FORWARD
    {
      Name "FORWARD"
      Tags
      { 
        "LIGHTMODE" = "FORWARDADD"
        "QUEUE" = "Transparent+1"
        "RenderType" = "Transperent"
      }
      LOD 200
      ZWrite Off
      Blend SrcAlpha One
      ColorMask RGB
      // m_ProgramMask = 6
      CGPROGRAM
      #pragma multi_compile POINT
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
      //uniform float4 unity_WorldTransformParams;
      //uniform float4x4 unity_MatrixVP;
      uniform float4x4 unity_WorldToLight;
      uniform float4 _MainTex_ST;
      uniform float4 _BumpMap_ST;
      //uniform float4 _WorldSpaceLightPos0;
      uniform float4 _LightColor0;
      uniform float4 _Color;
      uniform float _Opacity;
      uniform float _CutoffIce;
      uniform sampler2D _MainTex;
      uniform sampler2D _BumpMap;
      uniform sampler2D _LightTexture0;
      struct appdata_t
      {
          float4 vertex :POSITION0;
          float4 tangent :TANGENT0;
          float3 normal :NORMAL0;
          float4 texcoord :TEXCOORD0;
      };
      
      struct OUT_Data_Vert
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
          float3 texcoord5 :TEXCOORD5;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 texcoord :TEXCOORD0;
          float3 texcoord1 :TEXCOORD1;
          float3 texcoord2 :TEXCOORD2;
          float3 texcoord3 :TEXCOORD3;
          float3 texcoord4 :TEXCOORD4;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      float4 u_xlat0;
      float4 u_xlat1;
      float4 u_xlat2;
      float3 u_xlat3;
      float u_xlat13;
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          out_v.vertex = UnityObjectToClipPos(in_v.vertex);
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _MainTex);
          out_v.texcoord.zw = TRANSFORM_TEX(in_v.texcoord.xy, _BumpMap);
          u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          u_xlat1.xyz = normalize(u_xlat1.xyz);
          u_xlat2.xyz = (in_v.tangent.yyy * conv_mxt4x4_1(unity_ObjectToWorld).yzx);
          u_xlat2.xyz = ((conv_mxt4x4_0(unity_ObjectToWorld).yzx * in_v.tangent.xxx) + u_xlat2.xyz);
          u_xlat2.xyz = ((conv_mxt4x4_2(unity_ObjectToWorld).yzx * in_v.tangent.zzz) + u_xlat2.xyz);
          u_xlat2.xyz = normalize(u_xlat2.xyz);
          u_xlat3.xyz = (u_xlat1.xyz * u_xlat2.xyz);
          u_xlat3.xyz = ((u_xlat1.zxy * u_xlat2.yzx) + (-u_xlat3.xyz));
          u_xlat13 = (in_v.tangent.w * unity_WorldTransformParams.w);
          u_xlat3.xyz = (float3(u_xlat13, u_xlat13, u_xlat13) * u_xlat3.xyz);
          out_v.texcoord1.y = u_xlat3.x;
          out_v.texcoord1.x = u_xlat2.z;
          out_v.texcoord1.z = u_xlat1.y;
          out_v.texcoord2.x = u_xlat2.x;
          out_v.texcoord3.x = u_xlat2.y;
          out_v.texcoord2.z = u_xlat1.z;
          out_v.texcoord3.z = u_xlat1.x;
          out_v.texcoord2.y = u_xlat3.y;
          out_v.texcoord3.y = u_xlat3.z;
          out_v.texcoord4.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
          u_xlat0 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
          u_xlat1.xyz = (u_xlat0.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat1.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * u_xlat0.xxx) + u_xlat1.xyz);
          u_xlat0.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * u_xlat0.zzz) + u_xlat1.xyz);
          out_v.texcoord5.xyz = ((conv_mxt4x4_3(unity_WorldToLight).xyz * u_xlat0.www) + u_xlat0.xyz);
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      float3 u_xlat0_d;
      float3 u_xlat10_0;
      float3 u_xlat16_1;
      float3 u_xlat16_2;
      float3 u_xlat3_d;
      int u_xlatb4;
      float3 u_xlat16_5;
      float u_xlat12;
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          u_xlat10_0.xyz = tex2D(_BumpMap, in_f.texcoord.zw).xyz;
          u_xlat16_1.xyz = ((u_xlat10_0.xyz * float3(2, 2, 2)) + float3(-1, (-1), (-1)));
          u_xlat16_2.x = dot(in_f.texcoord1.xyz, u_xlat16_1.xyz);
          u_xlat16_2.y = dot(in_f.texcoord2.xyz, u_xlat16_1.xyz);
          u_xlat16_2.z = dot(in_f.texcoord3.xyz, u_xlat16_1.xyz);
          u_xlat0_d.x = dot(u_xlat16_2.xyz, u_xlat16_2.xyz);
          u_xlat0_d.x = rsqrt(u_xlat0_d.x);
          u_xlat0_d.xyz = (u_xlat0_d.xxx * u_xlat16_2.xyz);
          u_xlat3_d.xyz = ((-in_f.texcoord4.xyz) + _WorldSpaceLightPos0.xyz);
          u_xlat3_d.xyz = normalize(u_xlat3_d.xyz);
          u_xlat16_1.x = dot(u_xlat0_d.xyz, u_xlat3_d.xyz);
          u_xlat16_1.x = max(u_xlat16_1.x, 0);
          u_xlat0_d.xyz = (in_f.texcoord4.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
          u_xlat0_d.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * in_f.texcoord4.xxx) + u_xlat0_d.xyz);
          u_xlat0_d.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * in_f.texcoord4.zzz) + u_xlat0_d.xyz);
          u_xlat0_d.xyz = (u_xlat0_d.xyz + conv_mxt4x4_3(unity_WorldToLight).xyz);
          u_xlat0_d.x = dot(u_xlat0_d.xyz, u_xlat0_d.xyz);
          u_xlat0_d.x = tex2D(_LightTexture0, u_xlat0_d.xx).x;
          u_xlat16_5.xyz = (u_xlat0_d.xxx * _LightColor0.xyz);
          u_xlat16_5.xyz = (u_xlat16_5.xyz * _Color.xyz);
          out_f.color.xyz = (u_xlat16_1.xxx * u_xlat16_5.xyz);
          u_xlat0_d.x = tex2D(_MainTex, in_f.texcoord.xy).w;
          u_xlatb4 = (u_xlat0_d.x<_CutoffIce);
          u_xlat0_d.x = (u_xlat0_d.x + _Opacity);
          u_xlat0_d.x = (u_xlatb4)?(u_xlat0_d.x):(float(0));
          out_f.color.w = u_xlat0_d.x;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack "Reflective/Bumped Diffuse"
}
