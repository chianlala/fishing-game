using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;

[Preserve]
public class SoundMgr : MonoSingletion<SoundMgr> 
{
    public AudioSource bgSource;
    public AudioSource gameSource;
}
