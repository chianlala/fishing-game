using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class JiangChiUpdate : MonoBehaviour
{
    private Text thisText;
    public int Num = 1980000;

    void Awake()
    { 
        thisText = this.GetComponent<Text>();
    }
    void OnEnable()
    {
        Num = Random.Range(500000,900000000);
    } 

    void Update()
    {
        Num++;
        thisText.text = Num.ToString("N0");
    }
}
