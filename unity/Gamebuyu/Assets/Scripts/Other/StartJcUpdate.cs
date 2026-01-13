using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class StartJcUpdate : MonoBehaviour
{
    private Text thisText; 
    public long Num = 1980000;

    void Awake()
    { 
        thisText = this.GetComponent<Text>();
    }
    public void SetStartText(long num)
    {
        Num = num;
    } 

    void Update()
    {
        Num++;
        thisText.text = Num.ToString("N0");
    }
}
