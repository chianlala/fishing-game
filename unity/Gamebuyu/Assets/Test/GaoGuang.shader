// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "Unlit/GaoGuang"
{
	Properties
	{
	    _MainTex ("Albedo", 2D) = "white" {}
		_Diffuse ("Diffuse", Color) = (1, 1, 1, 1)
		_Specular ("Specular", Color) = (1, 1, 1, 1)
		_Gloss ("Gloss", Range(8.0, 256)) = 20
	}
	SubShader
	{
		Pass
		{
			Tags {"LightMode" = "ForwardBase"}
 
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			
			#include "Lighting.cginc"
			sampler2D _MainTex;
			fixed4 _Diffuse;
			fixed4 _Specular;
			float _Gloss;
 
			struct appdata
			{
				float4 vertex : POSITION;
				float3 normal : NORMAL;
			   float2 texcoord :TEXCOORD0;
			};
 
			struct v2f
			{
				float4 vertex : SV_POSITION;
				fixed3 color : COLOR;
				float2 uv    : TEXCOORD0;
			};
 
			v2f vert (appdata v)
			{
				v2f o;
				o.vertex = UnityObjectToClipPos(v.vertex);
				o.uv=v.texcoord;
				fixed3 ambient = UNITY_LIGHTMODEL_AMBIENT.xyz;
 
				fixed3 worldNormal = normalize(mul(v.normal, (float3x3)unity_WorldToObject));
				fixed3 worldLightDir = normalize(_WorldSpaceLightPos0.xyz);
				fixed3 diffuse = _LightColor0.rgb * _Diffuse.rgb * (saturate(dot(worldNormal, worldLightDir)));
 
				fixed3 reflectDir = normalize(reflect(-worldLightDir, worldNormal));
				fixed3 viewDir = normalize(_WorldSpaceCameraPos.xyz - mul(unity_ObjectToWorld, v.vertex).xyz);
				fixed3 specular = _LightColor0.rgb * _Specular.rgb * pow(saturate(dot(viewDir, reflectDir)), _Gloss);
 
				o.color = ambient + diffuse + specular;
				
				return o;
			}
 
			fixed4 frag (v2f i) : SV_Target
			{
				fixed4 col = fixed4(i.color, 1.0);
 
				//return col;
				
                fixed4 c = tex2D (_MainTex, i.uv) ;
                c=c*col;
                return c;

			}
			ENDCG
		}
	}
 
	FallBack "Specular"
}
