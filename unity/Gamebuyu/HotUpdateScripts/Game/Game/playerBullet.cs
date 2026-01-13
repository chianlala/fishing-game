using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using UnityEngine.UI;
using CoreGame;

namespace Game.UI
{
    public class playerBullet : MonoBehaviour
    {
        //[HideInInspector]
        public bool bHit = false;//是否已经击中
        float fSpeed = 2f;
        float fLastTimeX = 0f;
        float fLastTimeY = 0f;
        int nOldDir = 0;
        public int nPower = 1;//炮台等级
        public int nPaoMute = 1;//炮台倍数
        public long nTargetId = 0;//跟踪目标 0为普通子弹
        public long robotId = -1;//机器人ID
        public long m_playerID = 0;
        public long bulletID = -1;
        //激活 
        //float fLiveTime = 0;
        bool bActiveTriger = false;
        /// <summary>
        /// 每秒最大可旋转的角度.
        /// </summary>
        private float MAX_ROTATION = 360;

        /// <summary>
        /// 每帧最大可旋转的角度.
        /// </summary>
        private float MAX_ROTATION_FRAME;


        [HideInInspector]
        public int _nPaoLevel = 0;
        public int localPaoPrefab = 10000;
        public List<long> OnlyOne = new List<long>();

        //基于炮台 更换子弹等等
        public paotai base_paotai;
        //子弹拖尾
        public TrailRenderer[] allRenDerer;
        void Awake()
        {
            allRenDerer = this.transform.GetComponentsInChildren<TrailRenderer>();
            MAX_ROTATION_FRAME = 360;
            // Rig = this.gameObject.AddComponent<Rigidbody>();
            //Rig = this.GetComponent<Rigidbody2D>();
        }
        Vector3 v3 = new Vector3(23f, 0f, 0f);
        private void OnEnable()
        {
            mBullet = 0;
            bHit = false;//是否已经击中
                         //num = 0;
                         // fLiveTime = 0;
            bActiveTriger = false;
            //tmpGo = null;
            //  Rig.velocity=new Vector2(0,1);
            //if (allRenDerer.Length>0)
            //{
            //    for (int i = 0; i < allRenDerer.Length; i++)
            //    {
            //        allRenDerer[i].enabled = true;
            //    }
            //}
            //Debug.Log("进入子弹OnEnable");
        }
        public void playWang()
        {

        }

        //Unity 计算子节点的世界坐标  
        private Vector3 CalculatePos(Transform parent)
        {
            Vector3 offset = transform.localPosition * 1;//计算相对父节点偏差  
            Vector3 result = parent.position + parent.rotation * offset;//父节点的转角四元数乘以差值算出子节点在世界坐标系下相对父节点的差值  
            return result;
        }

        // Update is called once per frame 
        void FixedUpdate()
        {
            if (!bActiveTriger)
            {
                bActiveTriger = true;
                //mRender.enabled = true;
                //mCollider.enabled = true;
            }
            if (nTargetId > 0)//是跟踪弹
            {
                if (common.listFish.ContainsKey(nTargetId))//鱼活着
                {
                    //var mToLook = common.WordToUI(common.listFish[nTargetId].nowLockPos.position);
                    if (common.listFish[nTargetId].nowLockPos!=null)
                    {
                        var mToLook = common.WordToUI(common.listFish[nTargetId].nowLockPos.position);

                        ToLookTaget(new Vector2(mToLook.x, mToLook.y));

                        PengTargetId();
                        if (common.listFish[nTargetId].IsCanHit == false)//已经不能打了
                        {
                            nTargetId = -1;//跟踪弹失效
                        }
                    }
                    else
                    {
                        nTargetId = -1;//跟踪弹失效
                    }
                    //return;
                }
                else//鱼死了
                {
                    nTargetId = -1;//跟踪弹失效
                }
                //if (nTargetId == -1)
                //{
                //    // ToLookTaget(new Vector2(0, 100));
                //}
            }
            this.transform.Translate(0, fSpeed, 0, Space.Self);
            CheckBianjie();
            if (Root3D.Instance.cam3D != null)
            {
                //射线碰撞
                ray = Root3D.Instance.cam3D.ScreenPointToRay(Root3D.Instance.UICamera.WorldToScreenPoint(transform.position));
                if (Physics.Raycast(ray, out hit))
                {
                    //Debug.DrawLine(ray.origin, hit.point, Color.red);
                    //Debug.Log(hit.transform.name);
                    var other = hit.transform.GetComponent<QuoteFish>();
                    if (other==null)
                    {
                        //Debug.Log("other.transQuoteParent.name");
                        //为空继续找 则不执行后面的
                        return;
                    }

                    if (bHit)
                        //已经击中则不执行后面的
                        return;

                    if (other.CompareTag("fish"))//击中鱼摆摆
                    {
                        long fishId = -1;
                        fish fs = other.transQuoteParent.GetComponent<fish>(); 
                        if (fs != null)
                        {
                            if (fs.fishState != null)
                            {
                                fishId = fs.fishState.id;
                            }
                            //fHitRate = fs.fHitRate;
                        }
                        else
                        {
                            //Debug.LogError("错误fish");
                        }

                        if (nTargetId > 0)//跟踪弹  则不检测
                        {
                            bHit = false;
                        }
                        else//普通子弹
                        {
                            bHit = true;
                        }

                        if (bHit)
                        {
                            //渔网动画               
                            Vector3 pos = transform.position;
                            Vector3 pos2 = other.transform.position;
                            if (nTargetId > 0)
                            {
                                common3._UIFishingInterface.AnimalNet(localPaoPrefab,_nPaoLevel, pos, gameObject);
                                OnRecycle();
                            }
                            else
                            {
                                common3._UIFishingInterface.AnimalNet(localPaoPrefab, _nPaoLevel, pos, gameObject);
                                OnRecycle();
                            }

                            //发送server
                            long netId = bulletID;
                            if (robotId == -1)//是自己 传碰撞鱼集合
                            {
                                if (m_playerID == PlayerData.PlayerId)
                                {
                                    common3._UIFishingInterface.Req_FishingFightFishRequest(netId, fishId);
                                }
                            }
                            else//不是自己 机器人传碰撞鱼集合
                            {

                                common3._UIFishingInterface.Req_FishingRobotFightFishRequest(netId, fishId, robotId);
                            }

                            if (fs != null)
                            {
                                fs.fishBeHitColor();
                            }
                        }
                    }
                }
            }
            else
            {
                Destroy(this.gameObject);
            }      
         
        }
        public List<long> hitFishId = new List<long>();
        void PengTargetId()
        {
            var mToLook = common.WordToUI(common.listFish[nTargetId].nowLockPos.position);

            if (Vector2.Distance(transform.localPosition, mToLook) <= 30f) //打中
            {
                common3._UIFishingInterface.AnimalNet(localPaoPrefab, _nPaoLevel, this.transform.position, gameObject);
                fish fs = common.listFish[nTargetId];
                //发送server
                long netId = bulletID;
                if (nTargetId <= 0)//不为锁定
                {

                }
                else//是锁定
                {
                    if (netId != -1)
                    {
                        if (robotId == -1)//是自己
                        {

                            if (m_playerID == PlayerData.PlayerId)
                            {
                                common3._UIFishingInterface.Req_FishingFightFishRequest(netId, nTargetId);
                            }

                        }
                    }
                }
                if (fs != null)
                {
                    fs.fishBeHitColor();
                }
                OnRecycle();
            }
        }
        void ToLookTaget(Vector3 pos)
        {
            //转向屏幕中心
            float dx = pos.x - this.transform.localPosition.x;
            float dy = pos.y - this.transform.localPosition.y;
            float rotationZ = Mathf.Atan2(dy, dx) * Mathf.Rad2Deg;
            //得到最终的角度并且确保在 [0, 360) 这个区间内
            rotationZ -= 90;
            rotationZ = MakeSureRightRotation(rotationZ);
            //获取增加的角度
            float originRotationZ = MakeSureRightRotation(this.transform.eulerAngles.z);
            float addRotationZ = rotationZ - originRotationZ;
            //超过 180 度需要修改为负方向的角度
            if (addRotationZ > 180)
            {
                addRotationZ -= 360;
            }
            //不超过每帧最大可旋转的阀值
            addRotationZ = Mathf.Clamp(addRotationZ, -MAX_ROTATION_FRAME, MAX_ROTATION_FRAME);
            //应用旋转
            this.transform.localEulerAngles = new Vector3(0, 0, this.transform.eulerAngles.z + addRotationZ);

        }

        /// <summary>
        /// 确保角度在 [0, 360) 这个区间内.
        /// </summary>
        /// <param name="rotation">任意数值的角度.</param>
        /// <returns>对应的在 [0, 360) 这个区间内的角度.</returns>
        private float MakeSureRightRotation(float rotation)
        {
            rotation += 360;
            rotation %= 360;
            return rotation;
        }
        //计算夹角的角度 0~360
        float angle_360(Vector3 from_, Vector3 to_)
        {
            Vector3 v3 = Vector3.Cross(from_, to_);
            if (v3.z > 0)
                return Vector3.Angle(from_, to_);
            else
                return 360 - Vector3.Angle(from_, to_);
        }


        public List<long> hitOnlyOne = new List<long>();
     

        Ray ray;
        Ray ray2;
        RaycastHit hit;
   
            //初始化子弹图片 
        public void Init(int varlocalPaoPrefab, int nPaoLevel, long playerID, long robotOpenID = -1)
        {
            robotId = robotOpenID; 
            localPaoPrefab = varlocalPaoPrefab;
            _nPaoLevel = nPaoLevel; 
            m_playerID = playerID;
            //if (common.dicBullet.ContainsKey(playerID))
            //{
            //    common.dicBullet[playerID]++;
            //}
            //else
            //{
            //    common.dicBullet.Add(playerID, 1);
            //}
          
        }
  
        //触发回收
        public void OnRecycle()
        {
            //if (common.dicBullet.ContainsKey(m_playerID))
            //{
            //    common.dicBullet[m_playerID]--;
            //}
        }

        int m = 0;
        int mBullet = 0; 
        //过卡跑出边界
        void CheckBianjie()
        {
            if (this.transform.localPosition.x < 0 || this.transform.localPosition.x > common.W || this.transform.localPosition.y < 0 || this.transform.localPosition.y > common.H)//该物体的中心点出边界
            {
                int nDir = 0;
                if (this.transform.localPosition.x > common.W && this.transform.localPosition.y > common.H)//大于右上角
                {
                    nDir = 11;
                }
                else if (this.transform.localPosition.x < 0 && this.transform.localPosition.y < 0)//左下角
                {
                    nDir = 12;
                }
                else if (this.transform.localPosition.x > common.W && this.transform.localPosition.y < 0) //右下角
                {
                    nDir = 13;
                }
                else if (this.transform.localPosition.x < 0 && this.transform.localPosition.y > common.H)//左上角
                {
                    nDir = 14;
                }
                else if (this.transform.localPosition.x > common.W)
                {
                    nDir = 6;
                }
                else if (this.transform.localPosition.x < 0)
                {
                    nDir = 4;
                }

                else if (this.transform.localPosition.y > common.H)
                {
                    nDir = 8;
                }

                else if (this.transform.localPosition.y < 0)
                {
                    nDir = 2;
                }
                if (mBullet>3)
                {
                    commonLoad.ReciveOneBullet(localPaoPrefab, this.gameObject);
                }


                nOldDir = nDir;
                float fAngle = 0f;
                switch (nDir)
                {
                    case 4://左边越界
                        this.transform.localPosition.Set(0f, this.transform.localPosition.y, this.transform.localPosition.z);// =new Vector3(0, this.transform.localPosition.y, this.transform.localPosition.z);
                        fAngle = -this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 6://右边越界
                        this.transform.localPosition.Set(common.W, this.transform.localPosition.y, this.transform.localPosition.z);
                        fAngle = -this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 8://上边越界
                        this.transform.localPosition.Set(this.transform.localPosition.x, common.H, this.transform.localPosition.z);
                        fAngle = 180f - this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 2://下边越界
                        this.transform.localPosition.Set(this.transform.localPosition.x, 0, this.transform.localPosition.z);
                        fAngle = 180f - this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 11://右上角越界
                        this.transform.localPosition.Set(common.W, common.H, this.transform.localPosition.z);
                        fAngle = 180f + this.transform.localEulerAngles.z;
                        mBullet++;
                        mBullet++;
                        break;
                    case 12://左下角
                        this.transform.localPosition.Set(0f, 0f, this.transform.localPosition.z);
                        fAngle = -180f + this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 13://右下角
                        this.transform.localPosition.Set(common.W, 0f, this.transform.localPosition.z);
                        fAngle = -180f + this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                    case 14://左上角
                        this.transform.localPosition.Set(0f, common.H, this.transform.localPosition.z);
                        fAngle = 180f + this.transform.localEulerAngles.z;
                        mBullet++;
                        break;
                }
                CheckAngle(ref fAngle);
                this.transform.localEulerAngles = new Vector3(this.transform.localEulerAngles.x, this.transform.localEulerAngles.y, fAngle);
            }
            else
            {

            }
        }

        void CheckAngle(ref float angle)
        {
            if (angle > 360)
                angle -= 360;
            else if (angle < 0)
                angle += 360;
        }
  
        void OnDisable()
        {

            if (allRenDerer!=null)
            {
                if (allRenDerer.Length > 0)
                {
                    for (int i = 0; i < allRenDerer.Length; i++)
                    {
                        allRenDerer[i].Clear();// = false;
                    }
                }
            }
        
        }

    }
}