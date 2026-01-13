using UnityEngine;
using System.Collections;
using DG.Tweening;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;

public class TestMovePathLR : MonoBehaviour
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
        //if (rootPath==null)
        //{
        //    rootPath = GameObject.Find("rootPath").transform;
        //}
        //if (ipath==null)
        //{
        //    ipath = rootPath.GetChild(Random.Range(0, rootPath.childCount)).GetComponent<iTweenPath>();
        //}
        //fLifeTime = 0;
        ////获取路径节点
        //Vector3[] path = new Vector3[ipath.nodeCount];
        //for (int i = 0; i < ipath.nodeCount; i++)
        //{
        //    path[i] = ipath.nodes[i];
        //}

        ////DoTween设置路径
        ////transform.DOPath(path, 5, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
        ////        .SetOptions(false,AxisConstraint.None,AxisConstraint.Z)
        ////        .SetEase(Ease.Linear)
        ////        .SetLookAt(0.01f);
        ////        用DoTween基本的DoPath(...)方法，来做，然后实时改变 timeScale值
        ////例子：
        ////TweenerCore<Vector3, Path, PathOptions> _TC_Path = transform.DOPath(_WayPoints, _Duration, _PathType, PathMode.Full3D, 10, _Color).SetAutoKill(_IsAutoKill);
        ////        _TC_Path.timeScale = 1.5f;  //实时改变这个值就可以了（PS：这个值与系统的是分开的，不会影响）

        //transform.localPosition = path[0];
        //TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
        //        .SetOptions(false, AxisConstraint.None, AxisConstraint.Z)
        //        .SetEase(Ease.Linear)
        //        .SetLookAt(0.01f);

        ////DOVirtual.DelayedCall(2, () => { _TC_Path.timeScale = 0; });
        ////DOVirtual.DelayedCall(4, () => { _TC_Path.timeScale = 1; });
        ////transform.DOLocalPath(path, 5, PathType.Linear, PathMode.Full3D, 10, Color.red)
        ////        .SetEase(Ease.Linear)
        ////        .SetLookAt(0.01f, up: Vector3.up);
        ///        
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

        //旋转
        for (int i = 0; i < ipath.nodeCount; i++)
        {
            path[i] = ipath.nodes[i];
        }
        //if (path[0].x > path[1].x) //开始点大于另外一点  则是从右边出现
        //{
        //    if (this.transform.GetChild(0) != null)
        //    {
        //        this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
        //        //this.transform.GetChild(0).GetChild(0).transform.localEulerAngles = new Vector3(75f, 0f, 0f);
        //    }
        //}
        //else // 从左边边出现
        //{
        //    if (this.transform.GetChild(0) != null)
        //    {
        //        this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
        //        //this.transform.GetChild(0).GetChild(0).transform.localEulerAngles = new Vector3(75f, 0f, 0f);
        //    }
        //}


        transform.localPosition = path[0];
        TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Ignore, 10, Color.red)
                .SetOptions(false, AxisConstraint.None, AxisConstraint.Z)
                .SetEase(Ease.Linear)
                .SetLookAt(0.01f);

        //this.transform.GetComponent<fish>().Init(new TmpFishingFishInfoProto(11, 110, 2, 50,10, 0));    
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
        TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red).SetOptions(false, AxisConstraint.None, AxisConstraint.Z).SetEase(Ease.Linear).SetLookAt(0.01f);
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