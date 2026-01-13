using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
public class group : MonoBehaviour {

    public Transform AllTras1;
    public Transform AllTras2;
    public void SetBool(bool isbool)  
    {
        if (isbool)
        {
            AllTras1.gameObject.SetActive(true);
            AllTras2.gameObject.SetActive(false);
        }
        else
        {
            AllTras1.gameObject.SetActive(false); 
            AllTras2.gameObject.SetActive(true);
        }
    }
    public void SetImgBool(bool isbool) 
    {
        if (isbool)
        {
            AllTras1.GetComponent<Image>().enabled=true;
            AllTras2.GetComponent<Image>().enabled = false;
        }
        else
        {
            AllTras1.GetComponent<Image>().enabled = false;
            AllTras2.GetComponent<Image>().enabled = true;
        }
    }
}
