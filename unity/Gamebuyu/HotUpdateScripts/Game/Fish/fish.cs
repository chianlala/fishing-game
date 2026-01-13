using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using UnityEngine.Profiling;
using com.maple.game.osee.proto.fishing;
using DG.Tweening.Core;
using DG.Tweening.Plugins.Core.PathCore;
using DG.Tweening.Plugins.Options;
using System.Linq;
using UnityEngine.UI;

using System;
using com.maple.game.osee.proto;
using GameFramework;
using CoreGame;


namespace Game.UI
{
    public class fish : MonoBehaviour// fish
    {
        public long routeId;
        public long LocalrouteId;
        //主物体
        public Transform FishView;

        public Transform DieView;
         
        public Transform traChuQiZhiSheng;
        //冰冻盒子
        public Transform IceCube;
        //鱼类型 
        public int FishType;

        public string DieSoundName;
        //锁定等级 
        public int nAutoLevel;


        private float m_fScale = 0f;//鱼的大小

        //public UnityEngine.Animator _animation = null;  //动画

        public GameObject GoAnimation;
        //鱼游临时路径点
        private Vector3[] move_path;
        //路径操作
        TweenerCore<Vector3, Path, PathOptions> TC_Path;

        //--------------鱼在场景中的状态----------------------- 
        private bool bInit = false;    //是否初始化
        private bool bLife = true;     //是否活着(死亡时未销毁仍在播放动画)
        private bool bEscape = false;  //是否正在逃离(鱼潮来袭 或者boss清场次)

        private bool m_bIce = false;   //是否被冰冻
        private bool isCanHit = true;  //是否能被攻击

        public float  IceTime;
        public CtGoFish vCtGoFish;
        //碰撞体位置
        public Transform traCollider;
        public Collider[] ListColliderTrans;
        //能否被攻击的属性
        public bool IsCanHit
        {
            get
            {
                return isCanHit;
            }
            set
            {
                if (value)
                {
                    if (ListColliderTrans!=null)
                    {
                        for (int i = 0; i < ListColliderTrans.Length; i++)
                        {
                            ListColliderTrans[i].enabled = true;
                        }
                    }
                }
                else
                {
                    if (ListColliderTrans != null)
                    {
                        for (int i = 0; i < ListColliderTrans.Length; i++)
                        {
                            ListColliderTrans[i].enabled = false;
                        }
                    }
                }
                isCanHit = value;
            }
        }


        public float fBeHitTime = -1;

        public TmpFishingFishInfoProto fishState = new TmpFishingFishInfoProto(0, 0, 0, 0, 0, 0,false);//鱼的状态 用于和服务器同步
        public string fishName;
        //--------------鱼轨迹动画状态----------------------- 
        private float m_fLifeTime = 0;//当前生存时间
        public float m_fAnimalTime = 0;//动画播放时间
        private Queue<FishAnimalState> que_animalStateTime = new Queue<FishAnimalState>();
        private Queue<FishMoveState> que_moveStateTime = new Queue<FishMoveState>();
        private FishAnimalState cur_animalState;
        //回收机制使用的初始化数据
        private Vector3 v3_startScale;
        private Vector3 v3_startAngle;

        //不同步位移物体  例如蟾蜍
        public Transform MovePointArrow;

        public class FishMoveState
        {
            public float time;
            public MoveState state = MoveState.line;
            public FishMoveState(float _time, MoveState _state)
            {
                time = _time;
                state = _state;
            }
        }
        public class FishAnimalState
        {
            public float time;
            public string animal;
            public FishAnimalState(float _time, string _animal)
            {
                time = _time;
                animal = _animal;
            }
        }

        public enum MoveState//0直游 1停顿
        {
            line = 0,
            stay = 1,
        }
        //最好是按顺序检查  从中间数字排序 增大
        public Transform[] allPos;
        public Transform nowLockPos;//当前锁定点       
        private void Awake()
        {
            vCtGoFish = this.GetComponent<CtGoFish>();
            FishView = transform.Find("FishView");
            DieView = transform.Find("DieView");
            IceCube = transform.Find("IceCube");
            traChuQiZhiSheng = transform.Find("FishView/Adapt/DieAction/ChuQiZhiSheng");
            if (m_fScale == 0f)
            {
                m_fScale = this.transform.localScale.x;
            }
            if (traCollider == null)
            {
                var ListtraCollider = GetComponentsInChildren<Collider>();
                for (int i = 0; i < ListtraCollider.Length; i++)
                {
                    if (ListtraCollider[i].name == "hittrigger")
                    {
                        traCollider = ListtraCollider[i].transform;
                    }
                }
                if (traCollider == null)
                {
                    Debug.Log(this.name);
                }
            }
            //if (_animation == null)
            //{
            //    if (GoAnimation == null)
            //    {
            //        _animation = this.transform.GetComponentInChildren<UnityEngine.Animation>();
            //    }
            //    else
            //    {
            //        _animation = GoAnimation.GetComponent<UnityEngine.Animation>();
            //    }
            //}
            v3_startAngle = this.transform.localEulerAngles;
        }
        public void Start()
        {
            ListColliderTrans = this.GetComponentsInChildren<Collider>();
            HitAllPos m = this.transform.GetComponent<HitAllPos>();
            if (m != null)
            {
                allPos = m.allPos;
                nowLockPos = allPos[0];
            }
            else
            {
                allPos = null;
                nowLockPos = traCollider;
            }
        }
        //初始化
        public void Init(TmpFishingFishInfoProto ms)
        {
            fishState = ms;
            Debug.Log("clientLifeTime" + ms.clientLifeTime);
            Debug.Log("createTime" + ms.createTime);
            //fishState.routeId = ms.routeId;
            //初始化
            bInit = true;//是否已经初始化
            IsCanHit = true;//是否可以被自己击打
            bLife = true;//是否存活
            bEscape = false;//是否正在逃离(鱼潮来袭)
            fBeHitTime = -1;//被打颜色改变
            m_fLifeTime = 0;//当前生存时间
            m_fAnimalTime = 0;//动画播放时间
            m_bIce = false;//冰冻状态 设置为未冰冻
            SetIce(false);
            IceTime = 0;
            routeId = ms.routeId;//轨迹id

            //LocalrouteId = common.dicPathConfig[fishState.routeId].pathId;//本地轨迹id
            fishName = common4.dicFishConfig[ms.fishId].name;
            if (vCtGoFish!=null)
            {
                vCtGoFish.SetAniTimeSpeed(1);
                vCtGoFish.SetIsShowSkin(true);
            }
            if (TC_Path != null)
            {
                TC_Path.Kill();
                TC_Path = null;
            }
            v3_startScale = new Vector3(m_fScale, m_fScale, m_fScale);

            if (traChuQiZhiSheng!=null)
            {
                if (ms.chuQiZhiShengFlag)
                {
                    traChuQiZhiSheng.gameObject.SetActive(true);
                }
                else
                {
                    traChuQiZhiSheng.gameObject.SetActive(false);
                }
            }
           
            this.transform.localScale = v3_startScale;
            this.transform.localEulerAngles = v3_startAngle;

            //为0则回到初始颜色
            vCtGoFish.SetBeHitColor(0);
            //------------------配置读取-------------------
            Transform mmp = common.GetRootPath().Find(fishState.routeId.ToString());
            if (mmp == null)
            {
                FishPathOverOnRecyle();
                Debug.Log("轨迹ID不存在：" + fishState.routeId);
                return;
            }
            iTweenPath ipath = mmp.GetComponent<iTweenPath>();

            move_path = new Vector3[ipath.nodeCount];
            for (int i = 0; i < ipath.nodeCount; i++)
            {
                move_path[i] = ipath.nodes[i];
            }
            //----------------------初始化----------------------
            this.name = fishState.id.ToString();
            if (common.listFish.ContainsKey(fishState.id))
            {
                //如果已经存在这个条鱼了
                if (common.listFish[fishState.id]!=null)
                {
                    common.listFish[fishState.id].FishPathOverOnRecyle();
                }
                common.listFish.Remove(fishState.id);
            }
            common.listFish.Add(fishState.id, this);

            m_fLifeTime = fishState.clientLifeTime;
            transform.localPosition = move_path[0];
            if (FishType == 800) //俯视的2D鱼 可以旋转的2d小鱼 
            {
                transform.localPosition = move_path[0];
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 1000) //背朝上的2D鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DXuan(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DXuan(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 1500) //背朝上的2D鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DBackUp(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DBackUp(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 1513) //三条背朝上的2D鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DBackUp3T(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DBackUp3T(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 1514) //四条背朝上的2D鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DBackUp4T(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DBackUp4T(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 1515) //五条背朝上的2D鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DBackUp5T(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DBackUp5T(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 2000) //头朝上的2D spine鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DheadUpSpine(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.Ignore, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                { 
                    Fish2DheadUpSpine(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 2500) //背朝上的2D spine鱼 
            {
                transform.localPosition = move_path[0];
                Fish2DBackUpSpine(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DBackUpSpine(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else if (FishType == 2600) //奖券 
            {
                transform.localPosition = move_path[0];
                TextMesh tmsh= this.transform.Find("FishView/Adapt/DieAction/Bone/TextMesh").GetComponent<TextMesh>();
                tmsh.text = ms.lottery.ToString();
                Fish2DXuan(0);
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red).SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnWaypointChange((w) =>
                {
                    Fish2DXuan(w);
                })
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            else//其它普通类型
            {
                transform.localPosition = move_path[0];
                TC_Path = transform.DOLocalPath(move_path, ms.createTime, PathType.CatmullRom, PathMode.TopDown2D, 10, Color.red)
                .SetEase(Ease.Linear)
                .SetLookAt(0.01f)
                .OnComplete(() =>
                {
                    FishPathOverOnRecyle();
                });
            }
            if (TC_Path != null)
            {
                if (m_fLifeTime > 0f)//中途加入游到该位置
                {
                    TC_Path.Pause();
                    TC_Path.Goto(m_fLifeTime);
                    TC_Path.Play();
                }
            }
        } 
        void Fish2DXuan(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
        }
        //2D背朝上的鱼
        void Fish2DBackUp(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
        }
        void Fish2DBackUp3T(int wnext)
        { 
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
        }
        void Fish2DBackUp4T(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {

                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);

                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                
            }
            else//说明向右边
            {

                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);

                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
               

            }
            //if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            //{               
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            //}
            //else//说明向右边
            //{
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            //    this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            //}
        }
        void Fish2DBackUp5T(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone4").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
             
                //this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(180f, 0f, -90f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone1").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone2").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone3").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                this.transform.Find("FishView/Adapt/DieAction/Bone/Bone4").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
                
                //this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, -90f);
            }
        }
        //2D头朝上的鱼Spine
        void Fish2DheadUpSpine(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }
        }
        //2D背朝上的鱼Spine
        void Fish2DBackUpSpine(int wnext) 
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }
            else//说明向右边
            {
                this.transform.Find("FishView/Adapt/DieAction/Bone").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
                this.transform.Find("FishView/Adapt/DieAction/Shadow").transform.localEulerAngles = new Vector3(0f, 180f, 0f);
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
        void ToScreen(int wnext)
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
        void ToScreenTsgz(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
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
        void ToScreenHaiDaoChuan(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明从右向左
            {
                this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }
            else//说明从左向右边
            {
                this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 180f, 45f);
            }
        }
        void ToScreenGeShiLa(int wnext)
        {
            if (wnext >= move_path.Length - 1)
            {
                return;
            }
            if (move_path[wnext].x > move_path[wnext + 1].x)//说明向左
            {
                this.transform.GetChild(0).transform.localEulerAngles = new Vector3(45f, 180f, 0f);
            }
            else//说明向右边
            {
                this.transform.GetChild(0).transform.localEulerAngles = new Vector3(0f, 0f, 0f);
            }
        }
        ////去掉轨迹影响
        //public void PuseFish(bool bBlack)
        //{
        //    if (TC_Path != null)
        //    {
        //        TC_Path.Kill();
        //    }
        //}
        /// <summary>
        /// 冰冻或解冻此鱼
        /// </summary>
        /// <param name="bIce"></param>
        ///  

        public void Skill_Ice(bool bIce)
        {
            try
            {
                m_bIce = bIce;
                SetIce(bIce);
            }
            catch 
            {
            }
        }
        void SetIce(bool bIce) {

            if (bIce)
            {
                if (TC_Path != null)
                {
                    TC_Path.timeScale = 0;
                }
                if (vCtGoFish!=null)
                {
                    vCtGoFish.SetAniTimeSpeed(0);
                    vCtGoFish.SetCutoffIce(1f);
                }
                IceCube?.gameObject.SetActive(true);
            }
            else
            {
                if (TC_Path != null)
                {
                    TC_Path.timeScale = 1;
                }
                if (vCtGoFish != null)
                {
                    vCtGoFish.SetAniTimeSpeed(1);
                    vCtGoFish.SetCutoffIce(0f);
                }
                IceCube?.gameObject.SetActive(false);
            }
        }
        //设置倍数
        public void setText(long mult)
        {
            //this.transform.Find("FishView/panels/text").GetComponent<TextMesh>().text= mult.ToString();
        }
        private void OnEnable()
        {
            IceCube?.gameObject.SetActive(false);
            //if (_animation != null)
            //{
            //    _animation.Play();
            //}
        }
        public bool GetNowPos()
        {
            //代表有多个点
            if (allPos != null)
            {
                if (FishType == -10)
                {
                    return true;
                }
                //有多个点时 则从小到大判断 所以下表越小的点 在中间
                for (int i = 0; i < allPos.Length; i++)
                {
                    if (CheckAutoFishInScreen(allPos[i].position))
                    {
                        nowLockPos = allPos[i];
                        return true;
                    }
                }
                //nowLockPos = null;
                return false;
            }
            else
            {
                if (CheckAutoFishInScreen(traCollider.position))
                {
                    //没有赋值为普通碰撞体的 默认点
                    nowLockPos = traCollider;
                    return true;
                }
                return false;
            }
        }
        bool CheckAutoFishInScreen(Vector3 varV3)
        {
            Vector3 VarCamrePosV3 = common.WordToUI(varV3);
            if (VarCamrePosV3.x >= 0 && VarCamrePosV3.y >= 0 && VarCamrePosV3.x <= common.W && VarCamrePosV3.y <= common.H)
            {
                //屏幕内
                return true;
            }
            else
            {
                return false;
            }
        }
        void FixedUpdate()
        {
            if (bInit == false)
                return;
            if (bLife)//活着时处理
            {

                if (!bEscape)//不在 逃跑
                {
                    //if (!m_bIce)//是否被冻住
                    //{
                    //    m_fLifeTime += Time.fixedDeltaTime;                    
                    //}
                    if (IceTime>0)//冰冻了
                    {
                        IceTime= IceTime- Time.fixedDeltaTime;
                        if (IceTime<=0)
                        {
                            Skill_Ice(false);
                        }
                    }
                    else
                    {
                        m_fLifeTime += Time.fixedDeltaTime;
                    }
                    CheckBeHit();
                }
            }
        }
        private void OnDisable()
        {
            CancelInvoke("BoosSound");
        }
        public void fishBeHitColor()//被击中颜色变化
        {
            //重置被打时间
            fBeHitTime = 0.5f;
        }
        Color hitColor = new Color(1, 1, 1, 1);
        //检查被打颜色改变
        void CheckBeHit()
        {
            if (fBeHitTime > 0)
            {
                fBeHitTime -= 0.02f;
                vCtGoFish.SetBeHitColor(fBeHitTime);
            }
        }
        //死亡
        public void FishDie(bool bShowAnimal = true, long varPlayerId = -1, int itemID = -1, int itemNum = -1)
        {
            FishByDie0(bShowAnimal, varPlayerId, itemID, itemNum);
        }

        public void FishDieT() 
        {
            FishByDie1();
        } 
        void FishByDie1()
        {
            //在集合里移除当前鱼
            if (common.listFish.ContainsKey(fishState.id))
            {
                common.listFish.Remove(fishState.id);
            }
            //停止路径移动
            if (TC_Path != null)
            {
                TC_Path.Kill();
            }
            //已经死亡
            bLife = false;
            //不能被击打 
            IsCanHit = false;
            //死亡动画
            DieAniPlay(false);
        }
        void FishByDie0(bool bShowAnimal = true, long varPlayerId = -1, int itemID = -1, int itemNum = -1)
        {
            //在集合里移除当前鱼
            if (common.listFish.ContainsKey(fishState.id))
            {
                common.listFish.Remove(fishState.id);
            }
            //停止路径移动
            if (TC_Path != null)
            {
                TC_Path.Kill();
            }
            //已经死亡
            bLife = false;
            //不能被击打 
            IsCanHit = false;
            //死亡动画
            DieAniPlay(true);
        }
        void DieAniPlay(bool bShowAnimal)
        {
            //死亡动画
            //if (bShowAnimal)
            //{
            //}
            //else
            //{
             
            //}
            DOVirtual.DelayedCall(1f, () =>
            {
                this.transform.DOScale(0, 2f).OnComplete(() =>
                {
                    HitDieOnRecyle();
                });
            });
        }
        void FishPathOverOnRecyle()
        {
            bInit = false;
            //回收对象
            try
            {
                this.gameObject.SetActive(false);
                commonLoad.ReciveOneFish(common4.GetFishModleID(fishState.fishId), this.gameObject);
            }
            catch
            {
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
            }

            //鱼集合
            if (common.listFish.ContainsKey(fishState.id))
            {
                common.listFish.Remove(fishState.id);
            }
            this.transform.localScale = v3_startScale;
            fishState = null;
        }
        public void ThisOnlyRecycle()
        {
            //在集合里移除当前鱼
            if (common.listFish.ContainsKey(fishState.id))
            {
                common.listFish.Remove(fishState.id);
            }
            //回收对象
            try
            {
                this.gameObject.SetActive(false);
                commonLoad.ReciveOneFish(common4.GetFishModleID(fishState.fishId), this.gameObject);
            }
            catch
            {
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
            }
            bInit = false;
            //停止路径移动
            TC_Path.Kill();
            //已经死亡
            bLife = false;
            //不能被击打 
            IsCanHit = false;
            //死亡动画
        }
        //回收此鱼
        void HitDieOnRecyle()
        {
            bInit = false;
            try
            {
                this.gameObject.SetActive(false);
                commonLoad.ReciveOneFish(common4.GetFishModleID(fishState.fishId), this.gameObject);
            }
            catch
            {
                this.gameObject.SetActive(false);
                Destroy(this.gameObject);
            }
            if (common.listFish.ContainsKey(fishState.id))
            {
                common.listFish.Remove(fishState.id);
            }
            this.transform.localScale = v3_startScale;
            fishState = null;
        }
    }
}