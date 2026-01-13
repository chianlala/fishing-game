using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UpdateLightHigh : MonoBehaviour
{
    public float scrollSpeed = 0.1f;
    public float x=1;
    public float y=1;  
    // Use this for initialization
    void Start()
    {
        material = GetComponent<Renderer>().material;
    }
    Material material; 
    Color  NowColor ;
    // Update is called once per frame
    void Update()
    {
        float offset = Time.time * scrollSpeed;
        //Material.SetTextureOffset("_DetailAlbedoMap", new Vector2(offset*x, offset*y));
        float v = Mathf.Cos(offset);        
        NowColor = new Color(v, v/2, 0f, 1);
        material.SetColor("_EmissionColor", NowColor);
    }
}