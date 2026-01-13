Shader "Custom/MyShaderDoubleAlpha" {	
	Properties
	{
		_Color("颜色",Color) = (0.9,0,0,0.9)
		_MainTex ("Texture", 2D) = "white" {}
 
	}
	SubShader
	{
		//Tags { "RenderType"="Transparent" }
		Tags {
			"Queue" = "AlphaTest" //在不透明物体之后、透明物体之前渲染该物体；
			"IgnoreProjector" = "True" //不产生阴影；
			}
		LOD 100	
        Pass {
            Cull Front    
            CGPROGRAM
			#pragma vertex vert 
			#pragma fragment frag
	
			#include "UnityCG.cginc"
			//******属性区域开始
			struct appdata
			{
				float4 vertex : POSITION;
				float2 uv : TEXCOORD0;
			};
			struct v2f
			{
				float2 uv : TEXCOORD0;
				float4 vertex : SV_POSITION;
			};
			sampler2D _MainTex;
			float4 _MainTex_ST;

			fixed4 _Color;
	
		
			//*******属性区域结束
			//顶点入口函数
			v2f vert (appdata v)
			{
				v2f o;
				o.vertex = UnityObjectToClipPos(v.vertex);
				o.uv = TRANSFORM_TEX(v.uv, _MainTex);         	
				return o;
			}	
			//片元入口函数
			fixed4 frag (v2f i) : SV_Target
			{			
					fixed4 col = tex2D(_MainTex, i.uv) * _Color;//得到可移动的纹理颜色	
					return  col;
			}
			ENDCG
		}	
		Pass
		{		
		   Cull Back                                         
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			#include "UnityCG.cginc"
			//******属性区域开始
			struct appdata
			{
				float4 vertex : POSITION;
				float2 uv : TEXCOORD0;
			};

			struct v2f
			{
				float2 uv : TEXCOORD0;
				float4 vertex : SV_POSITION;
			};
			sampler2D _MainTex;
			float4 _MainTex_ST;
			fixed4 _Color;
	
			//顶点入口函数
			v2f vert (appdata v)
			{
				v2f o;
				o.vertex = UnityObjectToClipPos(v.vertex);
				o.uv = TRANSFORM_TEX(v.uv, _MainTex);         	
				//UNITY_TRANSFER_FOG(o,o.vertex);
				return o;
			}	
			//片元入口函数
			fixed4 frag (v2f i) : SV_Target
			{			
					fixed4 col = tex2D(_MainTex, i.uv ) * _Color;//得到可移动的纹理颜色	
					return  col;
			}
			ENDCG
		}
	}
}
