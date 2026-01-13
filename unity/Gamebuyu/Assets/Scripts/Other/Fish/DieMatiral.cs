using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DieMatiral : MonoBehaviour 
{
    public float ftime=1f;
    private float varftime; 
    public SkinnedMeshRenderer[] AllSkinned;
    private void OnEnable()
    {
        varftime = ftime;
    }
    private void Update()
    {
        if (varftime > 0)
        {
            for (int i = 0; i < AllSkinned.Length; i++)
            {
                AllSkinned[i].materials[0].SetFloat("_AlphaScale", varftime/ ftime);
            }
            varftime = varftime - Time.deltaTime;
            if (varftime<=0)
            {
                this.gameObject.SetActive(false);
            }
        }
    }
} 
