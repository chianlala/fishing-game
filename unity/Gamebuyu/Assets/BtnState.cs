using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class BtnState : MonoBehaviour
{
    public Sprite[] AllSpState;
    private Image isImage; 
    public void PlaySetState(int AllSp) {
        if (AllSp<AllSpState.Length)
        {
            isImage.sprite = AllSpState[AllSp];
        }
    }
}
