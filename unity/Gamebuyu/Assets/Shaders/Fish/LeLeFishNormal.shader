
Shader "Custom/LeLeFishNormal"
{
        Properties{
            //边缘光  反射球  高度贴图 普通鱼  金属化   没有高光贴图

           _Color("Color", Color) = (1,1,1,1)  //颜色  这个可以用来显示被击打颜色
           _MainTex("Texture", 2D) = "white" {}  //主贴图
           _BumpMap("Bumpmap", 2D) = "bump" {}  //高度贴图
           _JInTex("Metallic Texture", 2D) = "white" {}  //金属化贴图
      
           _Glossiness("Smoothness", Range(0,1)) = 0.5  //光滑度
           _Metallic("Metallic", Range(0,1)) = 0.0 //金属化程度
        
           _CubePower("Cubemap Power", Range(0,2)) = 0.5 // 边缘光的强度 也是范围

           _RimColor("Rim Color", Color) = (0.26,0.19,0.16,0.0) // 边缘光的颜色
           _RimPower("Rim Power", Range(0.5,8)) = 0 // 边缘光的强度 也是范围
        } 
        SubShader{
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
          sampler2D _BumpMap; //高度贴图
          
          sampler2D _JInTex;  //金属化贴图
          

          fixed _CubePower; //反射球强度  
          half _Glossiness; //光滑度
          half _Metallic;  //金属度

          //此类型范围在-2到2之间
          fixed4 _Color;
          fixed4 _RimColor;
          fixed4 _EmissionColor;
          
          float _RimPower;
          void surf(Input IN, inout SurfaceOutputStandard o) {
              //主体颜色
              o.Albedo = tex2D(_MainTex, IN.uv_MainTex).rgb * _Color;
              //边缘光 显示加在自发光上面 
              half rim = 1.0 - saturate(dot(normalize(IN.viewDir), o.Normal));
              
              //自发光
              o.Emission = _EmissionColor+ _RimColor.rgb * pow(rim, _RimPower);;
              
              o.Normal = UnpackNormal(tex2D(_BumpMap, IN.uv_BumpMap));
              o.Metallic = tex2D(_JInTex, IN.uv_MainTex).rgb * _Metallic;
              o.Smoothness = _Glossiness;
           }
           ENDCG
       }
           Fallback "Diffuse"
}
