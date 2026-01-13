// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "Unlit/LiuDong"
{
    Properties
    {
        _MainTex ("Texture", 2D) = "white" {}
		_MainColor("MainColor", Color) = (1,1,1,1)
    } 
    SubShader {
        Tags { "QUEUE"="Transparent" "IGNOREPROJECTOR"="true" "RenderType"="Transparent" }

        Pass {
     
            Blend SrcAlpha One//use (SrcAlpha,One), not (One,One)
            CGPROGRAM
            #pragma vertex vert
            #pragma fragment frag
            #pragma target 3.0

            #include "UnityCG.cginc"
            
            sampler2D _MainTex;
			  fixed4  _MainColor;
            struct myV2F{
                float4 pos:SV_POSITION;
                float2 uv    : TEXCOORD0;
            };
            
            myV2F vert(appdata_base v)  {
                myV2F v2f;
                v2f.pos=UnityObjectToClipPos (v.vertex);
                v2f.uv=v.texcoord;
                return v2f;
            }

            
            fixed4 frag(myV2F v2f) : COLOR {
			    v2f.uv.x+=_Time.y;
                fixed4 c = tex2D (_MainTex, -v2f.uv) ;
                c=mul(c,_MainColor);
                return c;
            }
            ENDCG
        }
    }
}