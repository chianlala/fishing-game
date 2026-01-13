using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class CtGoFish : MonoBehaviour
{
    public abstract void SetBeHitColor(float time);  

    public abstract void SetAniTime(string aniName,float time);
    public abstract void SetAniTimeSpeed(float speed);

    public abstract void SetCutoffIce(float time);

    public abstract void SetIsShowSkin(bool time); 
} 
