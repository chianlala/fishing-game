Shader "Custom/TestSurefaceCube"
{   
     Properties {
      _Color ("Color", Color) = (1,1,1,1)
      _MainTex ("Texture", 2D) = "white" {}
      _DetailPower("_Metallic", Range(0,2)) = 1.5
      _BumpMap ("Bumpmap", 2D) = "bump" {}
      _JInTex ("Metallic Texture", 2D) = "white" {}
      _Cube ("Cubemap", CUBE) = "" {} // 立方体贴图[6]
	  _Metallic ("_Metallic", Range(0,1)) = 0.5
      _Glossiness ("Smoothness", Range(0,1)) = 0.5
	  _RimColor ("Rim Color", Color) = (0.26,0.19,0.16,0.0) // 边缘光的颜色
      _RimPower ("Rim Power", Range(0.5,8.0)) = 3.0 // 边缘光的强度
    }
    SubShader {
      Tags { "RenderType" = "Opaque" }
      CGPROGRAM
      #pragma surface surf Standard fullforwardshadows
      
	  struct Input {
          float2 uv_MainTex;  //主贴图      
		  float2 uv_BumpMap;  //高度贴图
          float2 uv_JInTex;   //金属化
          float3 viewDir; // 观察向量
		  float3 worldRefl; // 世界反射向量
          INTERNAL_DATA
      };
      sampler2D _MainTex; //主贴图    
      fixed _DetailPower;
      sampler2D _BumpMap; //高度贴图
      sampler2D _JInTex;  //金属化
      samplerCUBE _Cube; //反射球
      half _Glossiness;
	  half _Metallic;
      fixed4 _Color;
	  float4 _RimColor;
      float _RimPower;
      void surf (Input IN, inout SurfaceOutputStandard o) {
          o.Albedo = tex2D (_MainTex, IN.uv_MainTex).rgb * _DetailPower * _Color;
		  half rim = 1.0 - saturate(dot (normalize(IN.viewDir), o.Normal)); 
		  // o.Emission = _RimColor.rgb * pow (rim, _RimPower);
		  // 根据世界反射向量和立方体贴图，反射相应的rgb
		  o.Albedo +=texCUBE (_Cube, IN.worldRefl).rgb*0.2;
         // o.Emission = _RimColor.rgb * pow (rim, _RimPower);
		  o.Normal = UnpackNormal (tex2D (_BumpMap, IN.uv_BumpMap));
	 	  o.Metallic = tex2D (_JInTex, IN.uv_JInTex).rgb*_Metallic;
          o.Smoothness = _Glossiness;
          //o.Smoothness = _Glossiness;
         // o.Alpha = _Color.a;
      }
      ENDCG
    } 
    Fallback "Diffuse"
}
