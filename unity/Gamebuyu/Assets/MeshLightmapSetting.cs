using UnityEngine;
[ExecuteInEditMode]
[RequireComponent(typeof(Renderer))]
public class MeshLightmapSetting : MonoBehaviour
{
    [HideInInspector]
    public int lightmapIndex;
    [HideInInspector]
    public Vector4 lightmapScaleOffset;
    public void SaveSettings()
    {
        MeshRenderer renderer = GetComponent<MeshRenderer>();
        lightmapIndex = renderer.lightmapIndex;
        lightmapScaleOffset = renderer.lightmapScaleOffset;
    }
    public void LoadSettings()
    {
        MeshRenderer renderer = GetComponent<MeshRenderer>();
        renderer.lightmapIndex = lightmapIndex;
        renderer.lightmapScaleOffset = lightmapScaleOffset;
    }
    void Start()
    {
        LoadSettings();
        if (Application.isPlaying)
            Destroy(this);
    }
}