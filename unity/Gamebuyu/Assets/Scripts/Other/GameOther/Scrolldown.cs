using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

public class Scrolldown : MonoBehaviour
{
    private Button btn_mask; 
    private void Awake()
    {
        btn_mask = this.transform.Find("btn_mask").GetComponent<Button>();
        btn_mask.onClick.AddListener(()=> {
            this.gameObject.SetActive(false);
        });
    }
}
