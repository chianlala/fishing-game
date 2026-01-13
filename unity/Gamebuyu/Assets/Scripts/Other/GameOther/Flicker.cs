using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Flicker : MonoBehaviour {

    public Transform Target;
    [Header("频率")]
    public float frequency = 0.1f; 
    float time = 1; 
	void Start () {
        if (Target==null)
        {
            Target=this.transform.GetChild(0);
        }
	}
	
	// Update is called once per frame
	void Update () {
        time += Time.deltaTime;
        if (time> frequency)
        {
            Target.gameObject.SetActive(!Target.gameObject.activeSelf);
            time = 0;
        }

    }
}
