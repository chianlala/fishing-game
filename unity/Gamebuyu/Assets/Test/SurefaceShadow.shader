Shader "Custom/SurefaceShadow"
{   
     Properties {
      _Color ("Color", Color) = (1,1,1,1)
      _MainTex ("Texture", 2D) = "white" {}
    }
    SubShader {
      Tags { "RenderType" = "Transparent" }
      CGPROGRAM
      #pragma surface surf Standard fullforwardshadows
      
	  struct Input {
          float2 uv_MainTex;  //主贴图          
      };
      sampler2D _MainTex; //主贴图    
      fixed4 _Color;

      void surf (Input IN, inout SurfaceOutputStandard o) {
          o.Albedo = tex2D (_MainTex, IN.uv_MainTex).rgb  * _Color;
      }
      ENDCG
    } 
    Fallback "Diffuse"
}
