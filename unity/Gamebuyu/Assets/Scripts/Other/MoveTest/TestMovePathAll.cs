using UnityEngine;
using System.Collections;
using DG.Tweening;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;

public class TestMovePathAll : MonoBehaviour
{ 
    [Header("路径")]
    public iTweenPath ipath;
    [Header("几秒内游完")] 
    public float fDurTime = 25;

    [Header("当前时间")]
    public float fLifeTime = 0;

    public Transform rootPath;

    //初始状态 
    public int FishType;

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
       move_path = new Vector3[ipath.nodeCount];
        for (int i = 0; i < ipath.nodeCount; i++)
        {
            move_path[i] = ipath.nodes[i];
        }

        transform.position = move_path[0];
        TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> TC_Path;
        if (FishType == 1000) //面朝左右 有倍数显示不能翻转的 财神
        {
            transform.localPosition = move_path[0]; 
            MoveStateCaiShen(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Ignore, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateCaiShen(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 1100) //面朝左右 有影子的2d鱼 不能翻转的 黄金水母
        {
            transform.localPosition = move_path[0];
            MoveStateShuiMu(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Ignore, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateShuiMu(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 1500) //俯视的2D鱼 可以旋转的2d小鱼 
        {
            transform.localPosition = move_path[0];
            MoveStateYinZhi(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 3000) //侧视的2D鱼 面朝左右 需同时翻转影子的
        {
            transform.localPosition = move_path[0];
            MoveStateYinZhi(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateYinZhi(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 3100) //侧视的2D鱼 水母
        {
            transform.localPosition = move_path[0];
            YinZhiAndNoUpDown(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                YinZhiAndNoUpDown(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 4000) //俯视3D鱼 可以旋转的
        {
            transform.localPosition = move_path[0];
            MoveStateRote3D(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 5000) //侧视3D鱼 头朝上类型  不能上下翻转
        {
            transform.localPosition = move_path[0];
            MoveStateOne(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Sidescroller2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateOne(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 5050) //俯视 但有倾斜3D鱼 向上倾斜 40度
        {
            transform.localPosition = move_path[0];
            MoveStateTwo40du(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateTwo40du(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 5100) //俯视 但有倾斜3D鱼 向上倾斜 60度
        {
            transform.localPosition = move_path[0];
            MoveStateTwo60du(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateTwo60du(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 5150) //俯视 但有倾斜3D鱼 向上倾斜 75度
        {
            transform.localPosition = move_path[0];
            MoveStateTwo75Sdu(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateTwo75Sdu(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 5200) //3D小海马
        {
            transform.localPosition = move_path[0];
            MoveStateTwo50du(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                MoveStateTwo50du(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 6000) //面朝玩家的 boos
        {
            transform.localPosition = move_path[0];
            ToScreenNoUpDown(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                ToScreenNoUpDown(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 6100) //面朝玩家的 boos血域魔僧
        {
            transform.localPosition = move_path[0];
            ToScreenMoSheng(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                ToScreenMoSheng(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 6200) //侧面面朝玩家的金龙
        {
            transform.localPosition = move_path[0];
            ToScreenJiLong(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Ignore, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                ToScreenJiLong(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 6250) //侧面面朝玩家的金龙
        {
            transform.localPosition = move_path[0];
            ToYouLinBaoChuan(0);
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnWaypointChange((w) => {
                ToYouLinBaoChuan(w);
            })
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 10001) //有影子 不能翻转类型
        {
            transform.localPosition = move_path[0];
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else if (FishType == 100000) //在屏幕中心 不动的boss 创世女娲
        {
            transform.localPosition = move_path[0];
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        else//其它普通类型
        {
            transform.localPosition = move_path[0];
            TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
            .SetLookAt(0.01f)
            .OnComplete(() =>
            {
                OnRecyle();
            });
        }
        //TweenerCore<Vector3, Path, DG.Tweening.Plugins.Options.PathOptions> _TC_Path = transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Full3D, 10, Color.red)
        //    .SetLookAt(0.01f).OnWaypointChange((w) =>
        //    {
        //        MoveStateTwo(w);
        //    });
        //transform.DOLocalPath(move_path, fDurTime, PathType.CatmullRom, PathMode.Sidescroller2D, 10, Color.red)
    }
    Vector3[] move_path;
    //左右类型5100 
    void MoveStateTwo(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 60f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 120f);
        }
    }
    //左右类型1000 需同时翻转影子的 
    void MoveStateCaiShen(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左 不翻转
        {
            this.transform.Find("FishView/Adapt").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
        }
        else//说明向右边
        {
            this.transform.Find("FishView/Adapt").transform.localEulerAngles = new Vector3(16f, 200f, 6f);
        }
    }
    //3d俯视鱼类型4000
    void MoveStateRote3D(int wnext)
    {
        this.transform.GetChild(0).localEulerAngles = new Vector3(0f, 0f, 180f);
    }
    void MoveStateShuiMu(int wnext)
    {
        //if (wnext >= move_path.Length - 1)
        //{
        //    return;
        //}
        //if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左 不翻转
        //{
        //    this.transform.Find("FishView/Adapt").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
        //}
        //else//说明向右边
        //{
        //    this.transform.Find("FishView/Adapt").transform.localEulerAngles = new Vector3(16f, 200f, 6f);
        //}
    }
    void OnRecyle()
    {
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

            //var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            //var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            //if (varBone != null && varShadow != null)
            //{
            //    varBone.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            //    varShadow.transform.localEulerAngles = new Vector3(0f, 0f, 180f);

            //}
            var varBone = this.transform.Find("FishView/Adapt");
            if (varBone != null)
            {
                varBone.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            }
        }
        else//说明向右边
        {
            //var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            //var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            //if (varBone != null && varShadow != null)
            //{
            //    varBone.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            //    varShadow.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            //}
            //this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            var varBone = this.transform.Find("FishView/Adapt");
            if (varBone != null)
            {
                varBone.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }

        }
    }
    //左右类型3000 需同时翻转影子的2d鱼类型
    void MoveStateYinZhi(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左 不翻转
        {
            var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            if (varBone != null && varShadow != null)
            {
                varBone.transform.localEulerAngles = new Vector3(180f, 180f, 0f);
                varShadow.transform.localEulerAngles = new Vector3(180f, 180f, 0f);
            }
            //this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(180f, 180f, 0f);
            //this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles new Vector3(180f, 180f, 0f);
        }
        else//说明向右边
        {
            var varBone = this.transform.Find("FishView/Adapt/DieAction/Bone");
            var varShadow = this.transform.Find("FishView/Adapt/DieAction/Shadow");
            if (varBone != null && varShadow != null)
            {
                varBone.transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                varShadow.transform.localEulerAngles = new Vector3(0f, 180f, 0f);
            }
            //this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
            //this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
        }
    }
    //3d鱼倾斜类型75度 
    void MoveStateTwo75Sdu(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 75f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 105f);
        }
    }
   
    //3d鱼倾斜类型40度
    void MoveStateTwo40du(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 40f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 140f);
        }
    }
    //3d鱼倾斜类型50度 
    void MoveStateTwo50du(int wnext) 
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 50f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 130f);
        }
    }
    //左右类型5000
    void MoveStateOne(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 180f, 0f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(60f, 180f, 0f);
        }
    }
    //3d鱼倾斜类型60度 
    void MoveStateTwo60du(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 60f);
        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);
            this.transform.Find("FishView/Adapt/DieAction").GetChild(0).localEulerAngles = new Vector3(0f, 0f, 120f);
        }
    }
    
    //面向屏幕boss6000
    void ToScreenNoUpDown(int wnext) 
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);

        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);

        }
    }
    void ToYouLinBaoChuan(int wnext) 
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(-60f, 0f, -90f);

        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(60f, 0f, -90f);

        }
    }
    void ToScreenJiLong(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 140f, 0f);

        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);

        }
    }
    //面向屏幕boss魔僧
    void ToScreenMoSheng(int wnext) 
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 180f);

        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 270f, 0f);

        }
    }
    //海马5200
    void HaiMaAndNoUpDown(int wnext)
    {
        if (wnext >= move_path.Length - 1)
        {
            return;
        }
        if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);

        }
        else//说明向右边
        {
            this.transform.GetChild(0).transform.localEulerAngles = new Vector3(60f, 180f, 0f);

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