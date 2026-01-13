using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UpdateBowen : MonoBehaviour
{
    public float scrollSpeed = 0.1f;
    public float x=1;
    public float y=1; 
    // Use this for initialization
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        float offset = Time.time * scrollSpeed;
        GetComponent<Renderer>().material.SetTextureOffset("_DetailAlbedoMap", new Vector2(offset*x, offset*y)); 
    }
}