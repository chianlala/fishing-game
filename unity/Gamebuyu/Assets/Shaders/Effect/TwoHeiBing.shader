// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "Unlit/TwoHeiBing"
{
    Properties{
        _MainTex ("Texture", 2D) = "white" {}
        _MainTex2("Texture", 2D) = "white" {}
        _MainColor("MainColor", Color) = (1,1,1,1)
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
             };

             float _Amount;//挤压强度

             sampler2D _MainTex; //主贴图  
             sampler2D _MainTex2;

   

             void surf(Input IN, inout SurfaceOutputStandard o) {
                 fixed4 c = tex2D(_MainTex, IN.uv_MainTex);
                 fixed4 c2 = tex2D(_MainTex2, IN.uv_MainTex);
                 o.Albedo = c.rgba + c2.rgb;
                 o.Alpha = c.a;

                    ////自发光
                    //o.Emission = tex2D(_EmissionTex, IN.uv_MainTex).rgb * _EmissionColor;
             }
              ENDCG
        }
            Fallback "Diffuse"
  //  Properties
  //  {
  //      _MainTex ("Texture", 2D) = "white" {}
  //      _MainTex2("Texture", 2D) = "white" {}
		//_MainColor("MainColor", Color) = (1,1,1,1)
  //  } 
  //  SubShader {
  //      Tags { "QUEUE"="Transparent" "IGNOREPROJECTOR"="true" "RenderType"="Transparent" }

  //      Pass {
  //   
  //          Blend SrcAlpha One//use (SrcAlpha,One), not (One,One)
  //          CGPROGRAM
  //          #pragma vertex vert
  //          #pragma fragment frag
  //          #pragma target 3.0

  //          #include "UnityCG.cginc"
  //          
  //          sampler2D _MainTex;
  //          sampler2D _MainTex2;
		//	  fixed4  _MainColor;
  //          struct myV2F{
  //              float4 pos:SV_POSITION;
  //              float2 uv    : TEXCOORD0;
  //          };
  //          
  //          myV2F vert(appdata_base v)  {
  //              myV2F v2f;
  //              v2f.pos=UnityObjectToClipPos (v.vertex);
  //              v2f.uv=v.texcoord;
  //              return v2f;
  //          }

  //          
  //          fixed4 frag(myV2F v2f) : COLOR {
		//	    //v2f.uv.x+=_Time.y;
  //              fixed4 c = tex2D (_MainTex, v2f.uv) ;
  //              fixed4 c2 = tex2D(_MainTex2, v2f.uv);
  //             // c=mul(c,_MainColor);
  //              c = mul(c, c2);
  //              return c;
  //          }
  //          ENDCG
  //      }
  //  }
}