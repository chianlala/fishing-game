Shader "Custom/FishDieAlphaAdd" {	

        Properties
        {
            _Color("Color",Color)= (1,1,1,1)
            _MainTex("MainTex",2D) = "white"{}  //  2D纹理贴图
            _AlphaScale("Alpha Scale",Range(0,1)) = 1  //  控制Alpha参数
        }
        SubShader
        {
            Tags { "Queue" = "Transparent" "IgnoreProjector" = "True" "RenderType" = "AlphaTest" }   //  渲染顺序设置Transparent
            LOD 100

            Pass  //  用两个pass通道来处理，防止出现渲染错误，第一个pass通道  每个pass通道都会渲染一次
            {
                ZWrite Off  //  写入深度  为了确认渲染顺序
                ColorMask 0  //  掩码遮罩  代表这个pass通道不写入任何颜色值
            }
            Pass  //  第二个pass通道
            {
                Tags{"LightMode" = "ForwardBase"}
                ZWrite Off  //  关闭ZWrite（深度写入）
                Blend SrcAlpha OneMinusSrcAlpha  //  源颜色因子  正常透明混合
                CGPROGRAM
                #pragma vertex vert
                #pragma fragment frag
                #include "UnityCG.cginc"
                #include "Lighting.cginc"
                struct v2f {
                    float4 vertex :SV_POSITION;  //  输出顶点信息
                    fixed3 worldNormal : TEXCOORD0;
                    float3 worldPos:TEXCOORD1;
                    float2 uv:TEXCOORD2;
                };
                float _AlphaScale;
                float4 _Color;
                sampler2D _MainTex;
                float4 _MainTex_ST;  //  是MainTex的Tiling和Offset两个属性  不需要在外面定义
                v2f vert(appdata_base v)  //  顶点着色器
                {
                    v2f o;  //  用这个结构体
                    o.vertex = UnityObjectToClipPos(v.vertex);  //  顶点信息从模型转到裁剪空间再放到输出结构体
                    fixed3 worldNormal = UnityObjectToWorldNormal(v.normal);  //  法线从模型变换到世界坐标
                    o.worldNormal = worldNormal;
                    o.worldPos = mul(unity_ObjectToWorld, v.vertex);  //  顶点信息从模型转到世界坐标系
                    o.uv = TRANSFORM_TEX(v.texcoord, _MainTex);  //  unity纹理函数，和上面效果一样
                    return o;
                }
                fixed4 frag(v2f i) : SV_Target  //  片元着色器  片元高光反射
                {
                    fixed4 texColor = tex2D(_MainTex, i.uv)* _Color;  //  采样这个图
                    //texColor = mul(texColor_Color);
                    texColor.a = texColor.a * _AlphaScale;
                    return texColor;  //  texColor的a值就是颜色通道的值  乘上_AlphaScale来控制
                }
                ENDCG
            }
        }
       FallBack "Transparent/VertexLit"
}
