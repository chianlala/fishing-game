using UnityEngine;
using System.Collections;
using DG.Tweening;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;

public class TestMoveOnePlay : MonoBehaviour
{
    [Header("路径")]
    public iTweenPath ipath; 
    [Header("几秒内游完")]
    public float fDurTime = 25; 

    [Header("当前时间")]
    public float fLifeTime = 0;

    public Transform rootPath;

    public Animator _animator;


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
         _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
                .SetOptions(false, AxisConstraint.None, AxisConstraint.Z)
                .SetEase(Ease.Linear)
                .SetLookAt(0.01f);

        //this.transform.GetComponent<fish>().Init(new TmpFishingFishInfoProto(11, 110, 2, 50,10, 0));    
    }

    TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path;
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
        _TC_Path = transform.DOLocalPath(path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red).SetOptions(false, AxisConstraint.None, AxisConstraint.Z).SetEase(Ease.Linear).SetLookAt(0.01f);
    }
    AnimatorStateInfo info;
    private void Update()
    {
        if (_TC_Path.timeScale == 0)
        {

        }
        else
        {
            fLifeTime += Time.deltaTime* _TC_Path.timeScale;
        }
        


        if (fLifeTime > fDurTime) {
            fLifeTime = 0;
            ipath = rootPath.GetChild(Random.Range(0, rootPath.childCount)).GetComponent<iTweenPath>();
            OnChangeIpath();
        }
        if (playTimes<3)
        {
            //是否在屏幕内
            if (CheckShandianAutoFishInScreen())
            {
                if (playTimes == 1)
                {
                    _TC_Path.timeScale = 0;
                    _animator.Play("pause");
                    playTimes = 2;
                }
            }
            info = _animator.GetCurrentAnimatorStateInfo(0);
            if (info.normalizedTime >= 0.9f && (info.IsName("pause")))//normalizedTime: 范围0 -- 1,  0是动作开始，1是动作结束
            {
                Root3D.Instance.showShaker();
            }
            if (info.normalizedTime >= 1.0f && (info.IsName("pause")))//normalizedTime: 范围0 -- 1,  0是动作开始，1是动作结束
            {
                _TC_Path.timeScale = 1;
                _animator.Play("idle");
                playTimes = 3;
            }
        }
       

    }
    Vector3 VarCamrePosV3;
    public int playTimes=1; 
    bool CheckShandianAutoFishInScreen()
    {
        return true;
        //VarCamrePosV3 = common.WordToUI(this.transform.position);
        ////  VarCamrePosV3 = UIRoot.Instance.cam3D.WorldToScreenPoint(common.listFish[nShanDianAutoFish].transform.position);// common.WordToUI(common.listFish[nShanDianAutoFish].transform.position);
        //if (VarCamrePosV3.x >= 0 && VarCamrePosV3.y >= 0 && VarCamrePosV3.x <= common.W * 2 && VarCamrePosV3.y <= common.H * 2)
        //{
        //    return true;
        //}
        //else
        //{
        //    return false;
        //}
    }
}