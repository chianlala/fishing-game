using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AniOverOneTA : MonoBehaviour 
{
    //动画结束技能脚本 
    public Action aAction;
    public void AniOver()    
    {
        if (aAction!=null)
        {
            aAction(); 
        }
    }
}
