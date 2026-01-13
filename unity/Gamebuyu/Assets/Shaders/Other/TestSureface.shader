Shader "Custom/TestSureface"
{   
     Properties {
      _Color ("Color", Color) = (1,1,1,1)
      _MainTex ("Texture", 2D) = "white" {}
      _BumpMap ("Bumpmap", 2D) = "bump" {}
       _JInTex ("Metallic Texture", 2D) = "white" {}
     // _Cube ("Cubemap", CUBE) = "" {}

      _Glossiness ("Smoothness", Range(0,1)) = 0.5
      _Metallic ("Metallic", Range(0,1)) = 0.0

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
          INTERNAL_DATA
      };
      sampler2D _MainTex; //主贴图    
      sampler2D _BumpMap; //高度贴图
      sampler2D _JInTex;  //金属化
     // samplerCUBE _Cube;  //反射球
      half _Glossiness;
      half _Metallic;
      fixed4 _Color;
	  float4 _RimColor;
      float _RimPower;
      void surf (Input IN, inout SurfaceOutputStandard o) {
          o.Albedo = tex2D (_MainTex, IN.uv_MainTex).rgb  * _Color;
		  half rim = 1.0 - saturate(dot (normalize(IN.viewDir), o.Normal)); 
		  o.Emission = _RimColor.rgb * pow (rim, _RimPower);
          o.Normal = UnpackNormal (tex2D (_BumpMap, IN.uv_BumpMap));
	 	  o.Metallic = tex2D (_JInTex, IN.uv_JInTex).rgb*_Metallic;
          //o.Metallic = _Metallic;
          o.Smoothness = _Glossiness;
         // o.Alpha = _Color.a;
      }
      ENDCG
    } 
    Fallback "Diffuse"
}
