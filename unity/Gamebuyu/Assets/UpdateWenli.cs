using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UpdateWenli : MonoBehaviour
{
    float scrollSpeed = 0.1f;
    // Use this for initialization 
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {


        float offset = Time.time * scrollSpeed;
        GetComponent<Renderer>().material.mainTextureOffset= new Vector2(-offset * 2, -offset);//Set();//("_DetailAlbedoMap", new Vector2(-offset*2, -offset));
    }
}