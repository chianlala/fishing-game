Shader "Custom/IceFish"
{
    Properties{
       _Color("Color",Color) = (0,0,0,0)
       _MainTex("Texture", 2D) = "white" {}  //主贴图


       _BumpMap("Bumpmap", 2D) = "bump" {}  //高度贴图

       _EmissionTex("Emission Texture", 2D) = "white" {}  //高光贴图   一般为黑白图   这样只会更改白色区域部分的显示
       _EmissionColor("Emission Color", Color) = (0.26,0.19,0.16,0.0) // 边缘光的颜色

       _Cube("Cubemap", CUBE) = "" {} // 立方体贴图[6]
       _CubePower("Cubemap Power", Range(0,2)) = 0.5 // 反射的强度 也是范围
    }
        SubShader{
          Tags { "RenderType" = "Opaque" }


          CGPROGRAM
         #pragma surface surf Standard fullforwardshadows alpha
         #pragma target 3.0

           //#pragma surface surf Standard fullforwardshadows
           // #pragma surface surf Lambert vertex:vert // 声明会使用顶点修改器

            struct Input {
                float2 uv_MainTex;  //主贴图  
                float2 uv_BumpMap;  //高度贴图
                float3 worldRefl; // 世界反射向量
                INTERNAL_DATA
            };


            sampler2D _MainTex; //主贴图    
            sampler2D _BumpMap; //高度贴图
            sampler2D _EmissionTex;//高光贴图
            samplerCUBE _Cube; //反射球

            fixed _CubePower; //反射球强度  
            fixed4 _Color;
            fixed4 _EmissionColor;


            void surf(Input IN, inout SurfaceOutputStandard o) {
                //主体颜色
                o.Albedo = tex2D(_MainTex, IN.uv_MainTex).rgb * _Color;

                o.Alpha = _Color.a;
                //加反射球
                o.Albedo += texCUBE(_Cube, IN.worldRefl).rgb * _CubePower;

                o.Normal = UnpackNormal(tex2D(_BumpMap, IN.uv_BumpMap));
                //自发光
                o.Emission = tex2D(_EmissionTex, IN.uv_MainTex).rgb * _EmissionColor;
             }
             ENDCG
       }
           Fallback "Diffuse"
}
