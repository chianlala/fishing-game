using UnityEngine;
using System.Collections;
using DG.Tweening;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;

public class TestYinZhiAndNoUpDown : MonoBehaviour
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

    Vector3[] move_path;
    private void OnChangeIpath()   
    {
        fLifeTime = 0;
        //获取路径节点
        move_path = new Vector3[ipath.nodeCount];
        for (int i = 0; i < ipath.nodeCount; i++)
        {
            move_path[i] = ipath.nodes[i];
        }

        transform.position = move_path[0];
        //DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
        TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                YinZhiAndNoUpDown(w);
            });
    }
    //左右类型3100
    void YinZhiAndNoUpDown(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            //this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 180f, 0f);

            var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            if (varBone != null && varShadow != null)
            {
                varBone.transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                varShadow.transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            }
        }
        else//说明向右边
        {
            //this.transform.GetChild(0).transform.localEulerAngles = new Vector3(60f, 180f, 0f);

            var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            if (varBone != null && varShadow != null)
            {
                varBone.transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                varShadow.transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
        }
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