using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class changeLength : MonoBehaviour {
    public Transform Now;
    public Transform ShanNow; 
    public Transform Line; 
    public Transform Target;
    public Transform varTarget;  
    //public ParticleSystem leidian;
    //public ParticleSystem leidian_andi;
    //public ParticleSystem leidian_glow;
    //public ParticleSystem leidian_02;

    public float bei;

    private AudioSource varAudioSource;
    // Use this for initialization
    void Start () {
        varAudioSource=this.transform.GetComponent<AudioSource>();

    }
    private void OnEnable()
    {
        
    }
    // Update is called once per frame
    void Update () {
        float mDistance = Vector3.Distance(Now.localPosition, varTarget.localPosition);
    
        Target.transform.position = ShanNow.transform.position;
        if (varAudioSource!=null)
        {
            varAudioSource.volume = SoundHelper.GameVolume;
        }

        //leidian.startSize = mDistance / bei;
        //leidian_andi.startSize = mDistance / bei;
        //leidian_glow.startSize = mDistance / bei;
        //leidian_02.startSize = mDistance / bei;
        //leidian.startSize = bei;
        //leidian_andi.startSize =  bei;
        //leidian_glow.startSize = bei;
        //leidian_02.startSize =  bei;
        if (mDistance>0)
        {
            Line.transform.localScale = new Vector3(1f, mDistance / bei, 1f);
        }
        else
        {
            Line.transform.localScale = new Vector3(1f, 0.1f / bei, 1f);
        }
    }
}
