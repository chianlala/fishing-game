// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "Unlit/LiuDong2"
{
        Properties{
            _Color("Color",Color) = (0,0,0,0)
            _MainTex("Texture", 2D) = "white" {}  //主贴图
            _NoiseBlackTex("NoiseBlackTex", 2D) = "white" {}  //主贴图
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
             sampler2D  _NoiseBlackTex;
             fixed4 _Color;


             void surf(Input IN, inout SurfaceOutputStandard o) {
                 //主体颜色
                 IN.uv_MainTex.y = _Time.y;
                 o.Albedo = tex2D(_MainTex, IN.uv_MainTex).rgb * _Color;
                 o.Albedo = o.Albedo * tex2D(_NoiseBlackTex, IN.uv_MainTex).rgba;
                 o.Alpha = _Color.a;
              }
              ENDCG
        }
            Fallback "Diffuse"
}