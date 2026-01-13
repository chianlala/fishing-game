Shader "Custom/KuoShan"
{
    Properties
    {
      _Texture0("Texture 0", 2D) = "white" {}

      _Texture1("范围", 2D) = "white" {}
     
      _R_ColoR("R_ColoR", Color) = (0.5019608,0.5019608,0.5019608,0.5)
      _R_Intensity("R_Intensity", float) = 1

      _Flow_Colpr("Flow_Colpr", Color) = (0.5019608,0.5019608,0.5019608,0.5)
      _Rate("Rate", float) = 2
      _G_UV_Tiling("G_UV_Tiling", Vector) = (1,1,0,0)
      _G_Uv_SpeeD("G_Uv_SpeeD", Vector) = (1,0,0,0)
      _Flow_Intensity("Flow_Intensity", float) = 1

      [HideInInspector] _texcoord("", 2D) = "white" {}
      [HideInInspector] __dirty("", float) = 1
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
        //  Name "FORWARD"
          Tags
          {
            "IsEmissive" = "true"
            "LIGHTMODE" = "FORWARDBASE"
            "QUEUE" = "Transparent"
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
      sampler2D  _Texture1;
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
          //转化为4x4矩阵的第1列 然后全乘 顶点的y轴  unity_ObjectToWorld unity对象空间转世界空间
          u_xlat0 = (in_v.vertex.yyyy * conv_mxt4x4_1(unity_ObjectToWorld));
          
          //转化为4x4矩阵的第0列 然后全乘 顶点的x轴   再加上本身
          u_xlat0 = ((conv_mxt4x4_0(unity_ObjectToWorld) * in_v.vertex.xxxx) + u_xlat0);

          //转化为4x4矩阵的第2列 然后全乘 顶点的z轴   再加上本身
          u_xlat0 = ((conv_mxt4x4_2(unity_ObjectToWorld) * in_v.vertex.zzzz) + u_xlat0);
          
          //u_xlat1=u_xlat0 + 世界空间的矩阵
          u_xlat1 = (u_xlat0 + conv_mxt4x4_3(unity_ObjectToWorld));

          //
          out_v.texcoord2.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);

          //两个矩阵相乘
          out_v.vertex = mul(unity_MatrixVP, u_xlat1);

          //TRANSFORM_TEX主要作用是拿顶点的uv去和材质球的tiling和offset作运算,确保材质球里的缩放和偏移设置是正确的
          out_v.texcoord.xy = TRANSFORM_TEX(in_v.texcoord.xy, _texcoord);
          //法线 和模型空间的 点积
          u_xlat0.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
          u_xlat0.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
          u_xlat0.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
          //归一化
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
          //速度
          u_xlat0_d.x = (_Time.y * _Rate);
          //做sin 波浪
          u_xlat0_d.x = sin(u_xlat0_d.x);

          //收缩波浪
          u_xlat0_d.x = ((u_xlat0_d.x * 0.5) + 0.5);
          
          //更改颜色强度
          u_xlat2.xyz = (_R_ColoR.xyz * float3(_R_Intensity, _R_Intensity, _R_Intensity));
          
          //波浪乘以颜色
          u_xlat0_d.xyz = (u_xlat0_d.xxx * u_xlat2.xyz);
          
          //时间乘 Uv上的速度
          u_xlat1_d.xy = (_Time.yy * float2(_G_Uv_SpeeD.x, _G_Uv_SpeeD.y));

          //乘Tilling
          u_xlat1_d.xy = ((in_f.texcoord.xy * _G_UV_Tiling.xy) + u_xlat1_d.xy);
          
          //获取主贴图根据uv移动后的  y颜色 也是就是g = green颜色
          u_xlat10_6 = tex2D(_Texture0, u_xlat1_d.xy).y;

          //TRANSFORM_TEX主要作用是拿顶点的uv去和材质球的tiling和offset作运算,确保材质球里的缩放和偏移设置是正确的
          u_xlat1_d.xy = TRANSFORM_TEX(in_f.texcoord.xy, _Texture0);
          

          //获取主贴图根据uv移动后的  xz颜色 也是就是rb = red blue颜色
          u_xlat10_1.xy = tex2D(_Texture0, u_xlat1_d.xy).xz;
          

          //u_xlat6_d即是偏移后的  z=b=blue颜色
          u_xlat6_d = (u_xlat10_6 * u_xlat10_1.y);
          
          //blue颜色乘质量
          u_xlat6_d = (u_xlat6_d * _Flow_Intensity);
          

          //blue颜色乘光晕颜色 _Flow_Colpr
          u_xlat3.xyz = (float3(u_xlat6_d, u_xlat6_d, u_xlat6_d) * _Flow_Colpr.xyz);
          

          u_xlat0_d.xyz = ((u_xlat0_d.xyz * u_xlat10_1.xxx) + u_xlat3.xyz);
          
          //clamp(x,a,b)  如果 x 值小于 a，则返回 a；如果 x 值大于 b，返回 b；否则，返回 x  限定值在 0-1里面
          u_xlat0_d.xyz = clamp(u_xlat0_d.xyz, 0, 1);
          
          //赋值颜色
          out_f.color.xyz = u_xlat0_d.xyz * tex2D(_Texture1, u_xlat0_d.xy).xyz;

          //赋值透明度
          out_f.color.w = 1;
          return out_f;
      }


      ENDCG

    } // end phase
    Pass // ind: 2, name: FORWARD
    {
      Name "FORWARD"
      Tags
      {
        "IsEmissive" = "true"
        "LIGHTMODE" = "FORWARDADD"
        "QUEUE" = "Transparent+0"
        "RenderType" = "Opaque"
      }
      ZWrite Off
      Blend One One
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
          //uniform float4x4 unity_MatrixVP;
          uniform float4x4 unity_WorldToLight;
          struct appdata_t
          {
              float4 vertex :POSITION0;
              float3 normal :NORMAL0;
          };

          struct OUT_Data_Vert
          {
              float3 texcoord :TEXCOORD0;
              float3 texcoord1 :TEXCOORD1;
              float3 texcoord2 :TEXCOORD2;
              float4 vertex :SV_POSITION;
          };

          struct v2f
          {
              float3 texcoord :TEXCOORD0;
              float3 texcoord1 :TEXCOORD1;
              float3 texcoord2 :TEXCOORD2;
              float4 vertex :Position;
          };

          struct OUT_Data_Frag
          {
              float4 color :SV_Target0;
          };

          float4 u_xlat0;
          float4 u_xlat1;
          float4 u_xlat2;
          float u_xlat10;
          OUT_Data_Vert vert(appdata_t in_v)
          {
              OUT_Data_Vert out_v;
              
              u_xlat1 = mul(unity_ObjectToWorld, float4(in_v.vertex.xyz,1.0));

              out_v.vertex = mul(unity_MatrixVP, u_xlat1);
              u_xlat1.x = dot(in_v.normal.xyz, conv_mxt4x4_0(unity_WorldToObject).xyz);
              u_xlat1.y = dot(in_v.normal.xyz, conv_mxt4x4_1(unity_WorldToObject).xyz);
              u_xlat1.z = dot(in_v.normal.xyz, conv_mxt4x4_2(unity_WorldToObject).xyz);
              out_v.texcoord.xyz = normalize(u_xlat1.xyz);
              out_v.texcoord1.xyz = ((conv_mxt4x4_3(unity_ObjectToWorld).xyz * in_v.vertex.www) + u_xlat0.xyz);
              u_xlat0 = ((conv_mxt4x4_3(unity_ObjectToWorld) * in_v.vertex.wwww) + u_xlat0);
              u_xlat1.xyz = (u_xlat0.yyy * conv_mxt4x4_1(unity_WorldToLight).xyz);
              u_xlat1.xyz = ((conv_mxt4x4_0(unity_WorldToLight).xyz * u_xlat0.xxx) + u_xlat1.xyz);
              u_xlat0.xyz = ((conv_mxt4x4_2(unity_WorldToLight).xyz * u_xlat0.zzz) + u_xlat1.xyz);
              out_v.texcoord2.xyz = ((conv_mxt4x4_3(unity_WorldToLight).xyz * u_xlat0.www) + u_xlat0.xyz);
              return out_v;
          }

          #define CODE_BLOCK_FRAGMENT
          OUT_Data_Frag frag(v2f in_f)
          {
              OUT_Data_Frag out_f;
              out_f.color = float4(0, 0, 0, 1);
              return out_f;
          }


          ENDCG

        } // end phase
      }
          FallBack Off
}
