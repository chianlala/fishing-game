using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;
using UnityEngine.UI;
[Preserve]
public class toggleTwo : MonoBehaviour {

    public Transform T1;
    public Transform T2; 
    private  void  Awake()
    {        
        this.GetComponent<Toggle>().onValueChanged.AddListener((arg) =>
        {
            if (arg)
            {
                T1.gameObject.SetActive(true);
                T2.gameObject.SetActive(false);
            }
            else
            {
                T1.gameObject.SetActive(false);
                T2.gameObject.SetActive(true);
            }
        });
    }
    private void OnEnable()
    {
        if (this.GetComponent<Toggle>().isOn)
        {
            T1.gameObject.SetActive(true);
            T2.gameObject.SetActive(false);
        }
        else
        {
            T1.gameObject.SetActive(false);
            T2.gameObject.SetActive(true);
        }
    }
}
