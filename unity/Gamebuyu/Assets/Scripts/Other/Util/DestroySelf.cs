using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;

[Preserve]
public class DestroySelf : MonoBehaviour {
    public float SetDestroyTime = 3;
    //为true 为只隐藏
    public bool bHide = false;

    private float fDestroyTime = 3;
    private void OnEnable()
    {
        fDestroyTime = SetDestroyTime;
    }
    // Update is called once per frame
    void Update () {
        fDestroyTime -= Time.deltaTime;
        if(fDestroyTime<=0)
        {
            if (bHide)
                this.gameObject.SetActive(false);
            else
                Destroy(this.gameObject);
        }
	}
}
