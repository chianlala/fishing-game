using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AniOverEventTA : MonoBehaviour 
{
    //动画结束技能脚本 
    public Action<string> aAction;
    public void AniOver(string strname)    
    {
        if (aAction!=null)
        {
            aAction(strname);
        }
    }
}
