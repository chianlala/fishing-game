
using UnityEngine;
using System.Collections;

public class NewBehaviourScript : MonoBehaviour
{

    public RenderTexture t;
    Renderer OnRenderer;
    private void Awake()
    {
        OnRenderer = this.GetComponent<Renderer>();
    }
    void Update()
    {
        OnRenderer.material.mainTexture = t;
    }

    void OnGUI()
    {
        GUI.DrawTexture(new Rect(0, 0, 100, 100), t);
    }
}
