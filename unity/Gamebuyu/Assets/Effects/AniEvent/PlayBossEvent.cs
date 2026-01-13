using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayBossEvent : MonoBehaviour
{
    public GameObject Go;

    public void ChangeEvent()
    {
        if (Go!=null)
        {
            Go.SetActive(false);
            Go.SetActive(true);
        }
    }
    Animator AniShaker;
    public void DouPingEvent() 
    {
        if (AniShaker==null)
        {
            AniShaker = Root3D.Instance.cam3D.GetComponent<Animator>();
        }
        AniShaker.Play("cattle_shaker");
    }
    
}
