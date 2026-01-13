Shader "Custom/MyShaderDouble" {	
	Properties
	{
		_MainTex ("Texture", 2D) = "white" {}
        [HDR]_HColor("高光",Color) = (0.9,0,0,0.9)
	   	_SeaTex("游动纹理",2D) = "black"{}
		_Speed("游动纹理速度",float) = 0.1
		_isEnter("是否自发光",int) = 0
		_isShowBo("是否显示波纹",int)=0
	    [HDR]_HisEnterColor("高光",Color) = (0,1,0.6,1)	
	}
	SubShader
	{
		Tags { "RenderType"="Opaque" }
		LOD 100	
        Pass {
            Cull Front    
          CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			// make fog work
			//#pragma multi_compile_fog  导入雾气
			
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
				UNITY_FOG_COORDS(1)
				float4 vertex : SV_POSITION;
			};

			sampler2D _MainTex;
			sampler2D _SeaTex;
			float4 _MainTex_ST;
			//高光颜色
			fixed4 _HColor;
			//被打颜色
			fixed4 _HisEnterColor;
			float _Speed;
			int _isEnter;
			int _isShowBo;
			//*******属性区域结束
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
				if(_isShowBo==1)
				{
					float2 uv_offset=float2(0,0);//定义两个浮点向量
					uv_offset.x=_Time.y*0.1;  //时间*速度
					uv_offset.y=_Time.y*0.1;
					fixed4 col_offset = tex2D(_SeaTex, i.uv+uv_offset);//得到可移动的纹理颜色	
					//distance(,)
					fixed4 col= tex2D(_MainTex, i.uv)+col_offset;	//叠加颜色                
					col=_HColor+col;      //高光处理					
					if (_isEnter > 0) {				

						//float3 view = normalize(col_offset);
						////计算法线和视觉方向上的点积
						//float value = dot(view, col);		
						col= (col+_HisEnterColor);	
						col=clamp(col,0,1); //限制范围
					}
					return  col;   
				}
				else
				{		
					fixed4 col= tex2D(_MainTex, i.uv);                
					col=_HColor+col;      //高光处理					
					if (_isEnter > 0) {				
						//float3 view = normalize(col_offset);
						////计算法线和视觉方向上的点积
						//float value = dot(view, col);		
						col= (col+_HisEnterColor);	
						col=clamp(col,0,1); //限制范围
					}
					return  col;   
				}
			            
			}
			ENDCG
		}	
		Pass
		{		
		   Cull Back                                         
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			// make fog work
			//#pragma multi_compile_fog  导入雾气
			
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
				UNITY_FOG_COORDS(1)
				float4 vertex : SV_POSITION;
			};

			sampler2D _MainTex;
			sampler2D _SeaTex;
			float4 _MainTex_ST;
			//高光颜色
			fixed4 _HColor;
			//被打颜色
			fixed4 _HisEnterColor;
			float _Speed;
			int _isEnter;
			int _isShowBo;
			//*******属性区域结束
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
				if(_isShowBo==1)
				{
					float2 uv_offset=float2(0,0);//定义两个浮点向量
					uv_offset.x=_Time.y*0.1;  //时间*速度
					uv_offset.y=_Time.y*0.1;
					fixed4 col_offset = tex2D(_SeaTex, i.uv+uv_offset);//得到可移动的纹理颜色	
					//distance(,)
					fixed4 col= tex2D(_MainTex, i.uv)+col_offset;	//叠加颜色                
					col=_HColor+col;      //高光处理					
					if (_isEnter > 0) {				

						//float3 view = normalize(col_offset);
						////计算法线和视觉方向上的点积
						//float value = dot(view, col);		
						col= (col+_HisEnterColor);	
						col=clamp(col,0,1); //限制范围
					}
					return  col;   
				}
				else
				{		
					fixed4 col= tex2D(_MainTex, i.uv);                
					col=_HColor+col;      //高光处理					
					if (_isEnter > 0) {				
						//float3 view = normalize(col_offset);
						////计算法线和视觉方向上的点积
						//float value = dot(view, col);		
						col= (col+_HisEnterColor);	
						col=clamp(col,0,1); //限制范围
					}
					return  col;   
				}
			            
			}
			ENDCG
		}
	}
}
