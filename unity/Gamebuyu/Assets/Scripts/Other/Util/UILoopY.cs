using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
public class UILoopY : MonoBehaviour {
    public float fRotateAngle = 10;
	void Update () {
        this.transform.localEulerAngles += new Vector3(0, Time.deltaTime * fRotateAngle, 0);
	}
}
