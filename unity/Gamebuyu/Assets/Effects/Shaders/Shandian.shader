Shader "Custom/Shandian" {
Properties{
		_MainTex("Albedo RGB", 2D) = "white"{}
		_Color("Color", color) = (0,0,0,1)
		_Alpha("Alpha", Range(0.5,1)) = 0.6
		_SpeedX("X_Speed",Range(0,100)) = 0.6
		_SpeedY("Y_Speed",Range(0,100)) = 0.6
	}

	SubShader{
		Tags {"RenderType" = "Transparent"}
		LOD 200

		CGPROGRAM
		//alpha 便是透明度
		#pragma surface surf Lambert alpha
		sampler2D _MainTex;
		fixed4 _Color;
		half _Alpha;
		half _SpeedX;
		half _SpeedY;

		struct Input{
			float2 uv_MainTex;
		};

		void surf(Input IN, inout SurfaceOutput o){
			fixed  xSpeed = _SpeedX * _Time;
			fixed  ySpeed = _SpeedY * _Time;

			fixed2 uv = IN.uv_MainTex - fixed2(xSpeed,ySpeed);
			fixed4 c = tex2D(_MainTex, uv);
			o.Albedo = _Color * c.rgb *3;
			o.Alpha = _Color.a * c.a * _Alpha;
		}

		ENDCG
	}


	FallBack "Diffuse"}
