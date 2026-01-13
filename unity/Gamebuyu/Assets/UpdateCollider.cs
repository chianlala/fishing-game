using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UpdateCollider : MonoBehaviour
{
    // Start is called before the first frame update
    MeshCollider collider;
    public SkinnedMeshRenderer meshRenderer;

    //一秒播放一次
    public float jianGe=1.0f;
    float nTime=0f;
    void Start()
    {
        collider = this.GetComponent<MeshCollider>();
        colliderMesh=new Mesh();
    }

    // Update is called once per frame
    Mesh colliderMesh;
    void Update()
    {
        nTime = nTime + Time.deltaTime;
        if (nTime >= jianGe)
        {
            nTime = 0;
            //colliderMesh.Clear();
            meshRenderer.BakeMesh(colliderMesh); //更新mesh
            //collider.sharedMesh = null;
            collider.sharedMesh = colliderMesh; //将新的mesh赋给meshcollider
        }
    
    }
}
