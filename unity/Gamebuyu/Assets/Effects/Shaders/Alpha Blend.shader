
Shader "FX/Alpha Blended" {
Properties {
	_TintColor ("ParticleColor", Color) = (1.0,1.0,1.0,1.0)
	_MainTex ("Particle Texture", 2D) = "white" {}
}

Category {
	Tags { "Queue"="Transparent" "IgnoreProjector"="True" "RenderType"="Transparent" }
	Blend SrcAlpha OneMinusSrcAlpha
	Cull Off Lighting Off ZWrite Off Fog{Mode Off}
	
	BindChannels {
		Bind "Color", color
		Bind "Vertex", vertex
		Bind "TexCoord", texcoord
	}
	
	SubShader {
		Pass {
			CGPROGRAM
				#pragma vertex vert
				#pragma fragment frag
				#include "UnityCG.cginc"	
				float4 _TintColor;
				sampler2D _MainTex;
				float4 _MainTex_ST;				

				struct appdata_t {
					float4 vertex : POSITION;
					fixed4 color : COLOR;
					float2 texcoord : TEXCOORD0;
				};

				struct v2f
				{
					half4 pos : SV_POSITION;
					half2 uv : TEXCOORD0;
					half4 color : COLOR;
				};

				v2f vert(appdata_t v) {
						v2f o;
						o.pos = UnityObjectToClipPos(v.vertex);
						o.color = v.color;
						o.uv = TRANSFORM_TEX(v.texcoord, _MainTex);
						return o;
				}

				float4 frag(v2f i) : COLOR{
				fixed4 col = 2.0f * i.color * _TintColor * tex2D(_MainTex, i.uv);
					return col;
				}

			ENDCG
		}
	}
}
}
