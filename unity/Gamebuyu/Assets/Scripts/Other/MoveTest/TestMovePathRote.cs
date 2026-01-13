using UnityEngine;
using System.Collections;
using DG.Tweening;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;

public class TestMovePathRote : MonoBehaviour
{
    [Header("路径")]
    public iTweenPath ipath;
    [Header("几秒内游完")]
    public float fDurTime = 25;

    [Header("当前时间")]
    public float fLifeTime = 0;

    public Transform rootPath;
 
    void Start()
    {
       
    }
    private void OnEnable()
    {
        if (rootPath == null)
        {
            rootPath = GameObject.Find("rootPath").transform;
        }
        if (ipath == null)
        {
            ipath = rootPath.GetChild(Random.Range(0, rootPath.childCount)).GetComponent<iTweenPath>();
        }
        fLifeTime = 0;
        //获取路径节点
        Vector3[] path = new Vector3[ipath.nodeCount];
        for (int i = 0; i < ipath.nodeCount; i++)
        {
            path[i] = ipath.nodes[i];
        }
        transform.localPosition = path[0];
        OnChangeIpath();
    }
    
    
    private void OnChangeIpath()  
    {
        fLifeTime = 0;
        //获取路径节点
        Vector3[] path = new Vector3[ipath.nodeCount];
        for (int i = 0; i < ipath.nodeCount; i++)
        {
            path[i] = ipath.nodes[i];
        }

        transform.position = path[0];
        TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f);
    }
    private void Update()
    {
        fLifeTime += Time.deltaTime;
        if (fLifeTime > fDurTime) {
            fLifeTime = 0;
            ipath = rootPath.GetChild(Random.Range(0, rootPath.childCount)).GetComponent<iTweenPath>();
            OnChangeIpath();
        }       
    }
}