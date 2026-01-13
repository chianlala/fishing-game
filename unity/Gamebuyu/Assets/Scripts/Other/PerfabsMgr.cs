using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;

[Preserve]
public class PerfabsMgr : MonoSingletion<PerfabsMgr>
{
    //所有头像
    public List<Sprite> AllHead = new List<Sprite>();
    //所有道具
    public List<Sprite> AllItem = new List<Sprite>(); 
}
