using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LoopY : MonoBehaviour {
    public float fRotateAngle = 30;
    private void OnEnable()
    {
        this.transform.DOScale(0.4f,5f);
    }
    void Update () {
        this.transform.localEulerAngles += new Vector3(0, Time.deltaTime * fRotateAngle, 0);
	}
}
 