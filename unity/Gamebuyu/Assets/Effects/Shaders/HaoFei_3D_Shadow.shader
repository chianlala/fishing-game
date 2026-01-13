Shader "HaoFei/3D/Shadow"
{
  Properties
  {
    _ShadowOffset ("Shadow Offset", Vector) = (0.1,0.1,0,0)
    _ShadowColor ("Shadow Color", Color) = (0,0,0,0.4)
  }
  SubShader
  {
    Tags
    { 
    }
    Pass // ind: 1, name: SimpleShadow
    {
      Name "SimpleShadow"
      Tags
      { 
      }
      ZWrite Off
      Stencil
      { 
        Ref 0
        ReadMask 255
        WriteMask 255
        Pass IncrWrap
        Fail Keep
        ZFail Keep
        PassFront IncrWrap
        FailFront Keep
        ZFailFront Keep
        PassBack IncrWrap
        FailBack Keep
        ZFailBack Keep
      } 
      Blend SrcAlpha OneMinusSrcAlpha
      ColorMask RGB
      // m_ProgramMask = 6
      CGPROGRAM
      //#pragma target 4.0
      
      #pragma vertex vert
      #pragma fragment frag
      
      #include "UnityCG.cginc"
      
      
      #define CODE_BLOCK_VERTEX
      //uniform float4x4 unity_ObjectToWorld;
      //uniform float4x4 unity_MatrixVP;
      uniform float2 _ShadowOffset;
      uniform float4 _ShadowColor;
      struct appdata_t
      {
          float4 vertex :POSITION;
      };
      
      struct OUT_Data_Vert
      {
          float4 xlv_COLOR :COLOR;
          float4 vertex :SV_POSITION;
      };
      
      struct v2f
      {
          float4 xlv_COLOR :COLOR;
      };
      
      struct OUT_Data_Frag
      {
          float4 color :SV_Target0;
      };
      
      OUT_Data_Vert vert(appdata_t in_v)
      {
          OUT_Data_Vert out_v;
          float4 tmpvar_1;
          float3 wPos_2;
          float3 shadowPos_3;
          float3 tmpvar_4;
          tmpvar_4 = mul(unity_ObjectToWorld, in_v.vertex).xyz;
          wPos_2 = tmpvar_4;
          shadowPos_3.z = wPos_2.z;
          shadowPos_3.xy = (wPos_2.xy - _ShadowOffset);
          float3 pos_5;
          pos_5 = shadowPos_3;
          float4 tmpvar_6;
          tmpvar_6.w = 1;
          tmpvar_6.xyz = float3(pos_5);
          tmpvar_1 = _ShadowColor;
          out_v.vertex = mul(unity_MatrixVP, tmpvar_6);
          out_v.xlv_COLOR = tmpvar_1;
          return out_v;
      }
      
      #define CODE_BLOCK_FRAGMENT
      OUT_Data_Frag frag(v2f in_f)
      {
          OUT_Data_Frag out_f;
          out_f.color = in_f.xlv_COLOR;
          return out_f;
      }
      
      
      ENDCG
      
    } // end phase
  }
  FallBack Off
}
