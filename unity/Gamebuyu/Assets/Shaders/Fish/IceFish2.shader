Shader "Custom/IceFish2"
{
        Properties{

           _MainTex("Texture", 2D) = "white" {}  //主贴图
           _Color("Color",Color) = (0,0,0,0)

           _BumpMap("Bumpmap", 2D) = "bump" {}  //高度贴图

           _EmissionTex("Emission Texture", 2D) = "white" {}  //高光贴图   一般为黑白图   这样只会更改白色区域部分的显示
           _EmissionColor("Emission Color", Color) = (0.26,0.19,0.16,0.0) // 边缘光的颜色

           _Amount("Extrusion Amount", Range(-1,20)) = 0.5 // 挤压参数

          
        } 
        SubShader{
          Tags { "RenderType" = "Opaque" }

     
          CGPROGRAM
         #pragma surface surf Standard fullforwardshadows alpha  vertex:vert
         #pragma target 3.0
        
         //#pragma surface surf Standard fullforwardshadows
         // #pragma surface surf Lambert vertex:vert // 声明会使用顶点修改器

          struct Input {
              float2 uv_MainTex;  //主贴图  
              float2 uv_BumpMap;  //高度贴图
          };

          float _Amount;//挤压强度

          sampler2D _MainTex; //主贴图    
          sampler2D _BumpMap; //高度贴图
          sampler2D _EmissionTex;//高光贴图
  
         
          fixed4 _Color;
          fixed4 _EmissionColor;

          void vert(inout appdata_full v) { // 顶点修改器实现
              
              v.vertex.xyz = v.vertex.xyz + v.normal; // 沿法线移动顶点的坐标
          }

          void surf(Input IN, inout SurfaceOutputStandard o) {
              fixed4 c =  _Color;
              o.Albedo = c.rgb;
              o.Alpha = c.a;
              o.Normal = UnpackNormal(tex2D(_BumpMap, IN.uv_BumpMap));
              //自发光
              o.Emission = tex2D(_EmissionTex, IN.uv_MainTex).rgb * _EmissionColor;
           }
           ENDCG
       }
           Fallback "Diffuse"
}
