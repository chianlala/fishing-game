using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayEnemy : MonoBehaviour {

	// Use this for initialization
	void Start () {
		
	}
    void OnEnable() 
    {
        int mRand= Random.Range(0, 4);
        if (mRand == 1)
        {
            this.GetComponent<DragonBones.UnityArmatureComponent>().animation.Play("animation",0);
        }
        else if (mRand == 2)
        {
            this.GetComponent<DragonBones.UnityArmatureComponent>().animation.Play("animation1", 0);
        }
        else if (mRand == 3)
        {

            this.GetComponent<DragonBones.UnityArmatureComponent>().animation.Play("animation2", 0);
        }
        else
        {
            this.GetComponent<DragonBones.UnityArmatureComponent>().animation.Play("animation3", 0);
        }

    }
    // Update is called once per frame
    void Update () {
		
	}
}
