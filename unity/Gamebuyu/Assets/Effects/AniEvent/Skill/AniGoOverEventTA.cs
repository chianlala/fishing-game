using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AniGoOverEventTA : MonoBehaviour 
{
    //动画结束技能脚本 
    public Action<string> aAction;

    public GameObject[] All_EfGo;  
    public void AniOver(string strname)    
    {
        if (aAction!=null)
        {
            aAction(strname);
        }
    }
}
