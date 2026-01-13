using com.maple.game.osee.proto.fishing;
using CoreGame;
using DG.Tweening;
using GameFramework;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class player : MonoBehaviour
    {
        public bool isInit=false;
        public Transform imgDizuo;
        public Transform bgNameGold;
        public int n_nameSeat=-1;  //这个即是物体的name  所以不会变
        public int n_RotateSeat;  //这个旋转后和名字不一致 因为旋转改变了集合顺序
        public float fOldAngle;
         
        public bool _isChuShiHua;
        public bool _isFindCompent=false;  
        //当前个人信息
        public Text txt_Name;
        private Text txt_diamond;
        private Text txt_dragonScale;
        private Text txt_Gold;
        private Text txt_lottery;

        public Text txt_paoLevel;
        public Text txt_roleLevel;
        public Text txt_GameSate;

        public Scrolldown paoDropdown;
        [HideInInspector]
        public int nlocalPaoPerfab;
        public int nlocalWingPerfab;
        //public int nPaoView = -1;
        public int nTmpPower = -1; 
        public int nWingView = -1;
        //是否可以发射子弹
        public bool canfireLittleGame;

        public Button btn_add;//升降炮台
        public Button btn_Autofire;//升降炮台
        public Button clickpao;
        public Transform pos_caijin;
        public Transform posPoint;

         Vector3 VarCamrePosV3;
        float RobotOldAngle;
        //List<GameObject> ListEffect = new List<GameObject>();
        public long nIsChangeAutoFish = 0;//当前正在跟踪的目标 
        public long _nfwqAutoFish = 0;//当前正在跟踪的目标 
        //炮台 
        #region   

        public Transform objPaotai;
        public paotai objPao;
        public Transform obj_Paotaizhuan;
  
        public Image img_noFire;//炮台过热
        #endregion
        //技能时间
        public int m;
        public long nAutoFish = 0;//当前正在跟踪的目标1 

        private long befornAutoFish = 0;


        private GameObject objAutoEffect = null;

        public float MylastFireTime; 
        public bool IsHaveMoney;
        public float fCD=0.2f;
        // public float fCD = 0.02f;
        private float fSDCD = 0.17f;
        private int nKuangBaoMult = 1;
        Vector2 posPaotai = Vector2.zero;
        Vector2 posPaotaiScreen = Vector2.zero;
        public List<GameObject> DstmpList=new List<GameObject>();

        public GameObjectPool Dspool;
        public GameObject Dsitem;
        public Transform Dscontent;
        private ScrollRect varScrollRect;
        public Transform pochan;
        public Transform btJiuJi;
        [HideInInspector]
        public PlayerInfo _pi;//玩家信息
        //是否能开火
        public bool _bCanFire = true;
        public bool _bHaveMoney = false;
        public bool _bCanBossFire = true; //为true时可以发射子弹 默认为true 进入boss特效时为fale
                                          //是否能锁定
        public bool _bAllGenzong = false;
        public bool _bGenzong = false;
        public bool _bDiancipao = false; 
        public bool _bBaoJi = false;
        //public bool _diancipao = false;

        //当前是否自动开炮
        private bool pbAutoFire;



        public bool bAutoFire
        {
            get { return pbAutoFire; }
            set
            {
                pbAutoFire = value;
            }
        }

        //炮倍数
        private int pPower = 1;
        public int nPower
        {
            get { return pPower; }
            set
            {
                pPower = value;
                Debug.Log("nPower"+ nPower);
                if (txt_paoLevel != null)
                {
                    txt_paoLevel.text = pPower.ToString();

                    UpdateBeiPaotai(pPower);
                }
            }
        }

        public int NKuangBaoMult
        {

            get
            {
                return nKuangBaoMult;
            }
            set
            {
                nKuangBaoMult = value;
                ChangeMoney();
            }

        } 
        public void ChangeGold(long gold)
        {
            //Debug.Log("当前金币数："+ gold);
            if (txt_Gold!=null)
            {
                //txt_Gold.text = gold.ToString();
                txt_Gold.text = common4.ChangeNumStr(gold);
            }
        }

        //-----延迟加龙晶逻辑
        Dictionary<long, long> _ALLDropGold = new Dictionary<long, long>();
        public long waitDragonscale;
        public void AddWaitDragonScale(long fishid, long dragonscale)
        {
            if (_ALLDropGold.ContainsKey(fishid)==false)//不存在
            {
                _ALLDropGold.Add(fishid, dragonscale);
            }
            waitDragonscale = 0;

            var e= _ALLDropGold.GetEnumerator();
            e.MoveNext();
            for (int i = 0; i < _ALLDropGold.Count;e.MoveNext(), i++)
            {
                //Debug.Log("svwwwda");
                Debug.Log(e.Current.Key);
                Debug.Log(e.Current.Value);
            }
            foreach (var item in _ALLDropGold)
            {
                waitDragonscale = waitDragonscale + item.Value;
            }
        }
        public void ReMoveWaitDragonScale(long fishid)
        {
            if (_ALLDropGold.ContainsKey(fishid))
            {
                _ALLDropGold.Remove(fishid);
            }
            waitDragonscale = 0;
            foreach (var item in _ALLDropGold)
            {
                waitDragonscale = waitDragonscale + item.Value;
            }
            ChangeDragonScale(nBeforeDragonScale);
        }
        long nBeforeDragonScale;
        long sdragonscale;
        public void ChangeDragonScale(long dragonscale)
        {
            if (ByData.nModule == 31)
            {
                if (txt_dragonScale != null)
                {
                    txt_dragonScale.text = "∞";
                }
                if (pochan != null)
                {
                    if (pochan.gameObject.activeSelf)
                    {
                        btJiuJi.gameObject.SetActive(false);
                        pochan.gameObject.SetActive(false);
                    }
                }
            }
            else
            {
                //Debug.Log("当前龙晶数：" + dragonscale);
                nBeforeDragonScale = dragonscale;
                if (dragonscale<=0)
                {
                    if (pochan!=null)
                    {
                        if (pochan.gameObject.activeSelf == false)
                        {
                            if (PlayerData.LastLottery>0)
                            {
                                btJiuJi.gameObject.SetActive(true);
                            }
                            else
                            {
                                btJiuJi.gameObject.SetActive(false);
                            }
                            pochan.gameObject.SetActive(true);
                        }
                    }
                }
                else
                {
                    if (pochan != null)
                    {
                        if (pochan.gameObject.activeSelf)
                        {
                            btJiuJi.gameObject.SetActive(false);
                            pochan.gameObject.SetActive(false);
                        }
                    }
                }
                sdragonscale = dragonscale - waitDragonscale;
                if (sdragonscale<0)
                {
                    return;
                }
                if (txt_dragonScale != null)
                {
                    txt_dragonScale.text = sdragonscale.ToString("N0");
                }
                else
                {
                    txt_dragonScale = transform.Find("bgNameGold/txt_dragon").GetComponent<Text>();
                    txt_dragonScale.text = sdragonscale.ToString("N0");
                }
            }
         
        }
        public void ChangeDiamond(long diamond) 
        {
            if (txt_diamond!=null)
            {
                txt_diamond.text = common4.ChangeNumStr(diamond);
            }
            else
            {
                txt_diamond = transform.Find("bgNameGold/txt_diamond").GetComponent<Text>();
                txt_diamond.text = common4.ChangeNumStr(diamond);
            }
        }
        public void ChangeJiangQuan(long jiangquan) 
        {
            if (txt_lottery != null)
            {
                txt_lottery.text = common4.ChangeNumStr(jiangquan);
            }
            else
            {
                txt_lottery = transform.Find("bgNameGold/txt_lottery").GetComponent<Text>();
                txt_lottery.text = common4.ChangeNumStr(jiangquan);
            }
        }
        /// <summary>
        /// 重置技能计数
        /// </summary>
        void RestSkill()
        {
            //锁定鱼归零
            nAutoFish = 0;
            bAutoFire = false;
            _bAllGenzong = false;
            _bGenzong = false;
            _bDiancipao = false;
            _bBaoJi = false;
            //狂暴倍数归置为1
            ChangeNKuangBaoMult();
            canfireLittleGame = false;

            if (ByData.nModule == 31)
            {
                NetMessage.BigAward.Req_FishingGrandPrixPropsUseStateSyncRequest();
            }
            else
            {
                NetMessage.Chanllenge.Req_FishingChallengePropsUseStateSyncRequest();
            }
        }
        void ChangeMoney()
        {
            if (common.IDRoomFish == BY_SESSION.龙晶场)
            {
                if (IsEnoughDragonCrystal(PlayerData.DragonCrystal))
                {
                    _bHaveMoney = true;
                    SetbCanFire(_bHaveMoney);
                }
            }
            if (common.IDRoomFish == BY_SESSION.普通场 || common.IDRoomFish == BY_SESSION.大奖赛)
            {
                if (IsEnoughGold(PlayerData.Gold))
                {
                    _bHaveMoney = true;
                    SetbCanFire(_bHaveMoney);
                }
            }
        }
        public  bool IsEnoughDragonCrystal(long varnum)
        {
            //防止为负数
            //if (PlayerData.DragonCrystal- waitDragonscale > nPower * NKuangBaoMult)
            if (PlayerData.DragonCrystal - waitDragonscale > 0)
            {
                return true;
            }
            return false;
        }
        public void SetbCanFire(bool state)
        {
            if (state)
            {
                int m = 0;
                if (_bHaveMoney)
                {
                    m++;
                }
                if (_bCanBossFire)
                {
                    m++;
                }
                if (m >= 2)
                {
                    _bCanFire = true;
                }
            }
            else
            {
                _bCanFire = false;
            }
        }
       public bool IsEnoughGold(long varnum)
        {
            //闪电翻两倍
            if (PlayerData.Gold > nPower * NKuangBaoMult)
            {
                return true;
            }
            return false;
        }
  
   
        void Awake()
        {
            n_nameSeat = int.Parse(this.name);
            FindCompent();
        }

        void Update()
        {
            if (isInit)//是否已经赋值给玩家了
            {
                SkillEffect();
            }
        }
        public void ZhuanTouFire(Vector2 v2)
        {
        }
        void  FindCompent()
        {
            imgDizuo = transform.Find("imgDizuo");
            bgNameGold = transform.Find("bgNameGold");
            txt_dragonScale = transform.Find("bgNameGold/txt_dragon").GetComponent<Text>();
            txt_Name = transform.Find("bgNameGold/txt_Name").GetComponent<Text>();
            txt_diamond = transform.Find("bgNameGold/txt_diamond").GetComponent<Text>();
            txt_Gold = transform.Find("bgNameGold/txt_gold").GetComponent<Text>();
            txt_lottery = transform.Find("bgNameGold/txt_lottery").GetComponent<Text>();
            
            btn_add = transform.Find("imgDizuo/bgdzuo/btn_add").GetComponent<Button>();
            btn_Autofire = transform.Find("imgDizuo/bgdzuo/btn_AutoFire").GetComponent<Button>();
            txt_paoLevel = transform.Find("imgDizuo/bgdzuo/txt_Paobei").GetComponent<Text>();
            objPaotai = transform.Find("imgDizuo/rootPaotai");
            Dsitem = transform.Find("imgDizuo/Scrolldown/Viewport/Content/Item").gameObject;
            Dscontent = transform.Find("imgDizuo/Scrolldown/Viewport/Content");
            txt_GameSate = transform.Find("imgDizuo/Text").GetComponent<Text>();
            img_noFire = transform.Find("imgDizuo/img_nofire").GetComponent<Image>();
            paoDropdown = transform.Find("imgDizuo/Scrolldown").GetComponent<Scrolldown>();
            varScrollRect = transform.Find("imgDizuo/Scrolldown").GetComponent<ScrollRect>();
            posPoint = transform.Find("imgDizuo/rootPaotai/posFire");
            pochan = transform.Find("imgDizuo/pochan");
            btJiuJi = transform.Find("imgDizuo/pochan/btJiuJi");
            pos_caijin = transform.Find("imgDizuo/pos_caijin");
            clickpao = transform.Find("imgDizuo/clickpao").GetComponent<Button>();
            _isFindCompent = true;
            AddEvent();
        }

        void AddEvent()
        {
            varScrollRect = paoDropdown.transform.GetComponent<ScrollRect>();
            btn_add.onClick.RemoveAllListeners();
            btn_add.onClick.AddListener(() => {
                //if (ByData.nModule == 31)
                //{
                //    MessageBox.ShowPopOneMessage("大奖赛无法更改炮倍!");
                //    return;
                //}
                paoDropdown.gameObject.SetActive(true);
                ScrollTo(nPower);
            });
            btn_Autofire.onClick.RemoveAllListeners();
            btn_Autofire.onClick.AddListener(() =>
            {
                //if (ByData.nModule == 31)
                //{
                //    MessageBox.ShowPopOneMessage("大奖赛无法更改炮倍!");
                //    return;
                //}
                paoDropdown.gameObject.SetActive(true);
                ScrollTo(nPower);
                //EventManager.ClickAuto?.Invoke();
                //Debug.Log("svbsaca");
                //if (PlayerData._bZiDong == false)
                //{
                //    PlayerData._bZiDong = true;
                //    PlayerData.SetRootbZiDong(true);
                //    btn_Autofire.transform.GetComponent<Image>().color=Color.gray;
                //}
                //else
                //{
                //    PlayerData._bZiDong = false;
                //    PlayerData.SetRootbZiDong(false);
                //    btn_Autofire.transform.GetComponent<Image>().color = Color.white;
                //}
                //paoDropdown.gameObject.SetActive(true);
                //ScrollTo(nPower);
            });
            clickpao.onClick.RemoveAllListeners();
            clickpao.onClick.AddListener(() =>
            {
                if (PlayerData.PlayerId == _pi.playerId)
                {
                    EventManager.ClickPaoTai?.Invoke();

                }
            });
            //炮倍选择对象池
            Dspool = new GameObjectPool();
            Dspool.SetTemplete(Dsitem);
            Dsitem.gameObject.SetActive(false);
        }
        //判断子弹  并生成特效的路径
        string str = "Effect/0_fx2";
        private void SkillEffect()
        {
            if (_bCanFire)//能否开炮 false则不能
            {
                if (_bAllGenzong)//是否是跟踪状态
                {
                    if (nAutoFish > 0)//有锁定鱼
                    {
                        if (_pi.playerId == PlayerData.PlayerId)
                        {
                            //是自己
                            if (common.listFish.ContainsKey(nAutoFish)&& common.listFish[nAutoFish] != null)
                            {
                                if (objAutoEffect == null)
                                {
                                    string str = "Effect/0_fx2";
                                    if (PlayerData.PlayerId == _pi.playerId)
                                    {
                                        str = "Effect/0_fx";
                                    }
                                    //跟踪弹特效
                                    objAutoEffect = Instantiate(common4.LoadPrefab(str));
                                    objAutoEffect.transform.SetParent(common2.BulletPos);
                                    objAutoEffect.transform.localScale = Vector3.one;
                                    objAutoEffect.SetActive(false);
                                }
                                if (common.listFish[nAutoFish].GetComponent<fish>() != null)
                                {
                                    if (common.listFish[nAutoFish].nowLockPos != null)
                                    {
                                        var m = common.WordToUI(common.listFish[nAutoFish].nowLockPos.position);                         
                                        if (objAutoEffect.activeSelf == false)
                                        {
                                            objAutoEffect.SetActive(true);
                                        }
                                        objAutoEffect.transform.localPosition = m;
                                    }
                                }                                
                                Req_DoFire(common.OldInput);
                            }
                            else if (_pi.playerId == PlayerData.PlayerId || _pi.isRobot)
                            {
                                //如果锁定的鱼不在集合里 自动选择优先攻击鱼
                                nAutoFish = 0;
                                if (common.listFish.Count > 0)
                                {
                                    List<fish> tempList = new List<fish>();
                                    DictionaryEx.ForeachValue(common.listFish, (val) => { tempList.Add(val); });
                                    tempList.Sort(SortList);
                                    for (int i = 0; i < tempList.Count; i++)
                                    {
                                        if (tempList[i] != null)
                                        {
                                            if (CheckAutoFishInScreen(tempList[i].fishState.id))
                                            {
                                                if (tempList[i].IsCanHit == true)
                                                {
                                                    nAutoFish = tempList[i].fishState.id;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (nAutoFish > 0)
                                {
                                    Req_DoFire(common.OldInput);
                                }
                            }
                            if (nIsChangeAutoFish != nAutoFish)
                            {
                                nIsChangeAutoFish = nAutoFish; //切换的时候请求 省一点性能
                                common3._UIFishingInterface.SyncLockRequest(nAutoFish);
                            }
                        }
                        else
                        {
                            //不是是自己 只显示锁定特效就行了
                            if (common.listFish.ContainsKey(nAutoFish))
                            {
                                string str = "Effect/0_fx2";
                                if (PlayerData.PlayerId == _pi.playerId)
                                {
                                    str = "Effect/0_fx";
                                }
                                if (objAutoEffect == null)
                                {
                                    //跟踪弹特效
                                    objAutoEffect = Instantiate(common4.LoadPrefab(str));
                                    objAutoEffect.transform.SetParent(common2.BulletPos);
                                    objAutoEffect.transform.localScale = Vector3.one;
                                    objAutoEffect.SetActive(false);
                                }
                                if (common.listFish[nAutoFish]!=null)
                                {
                                    if (common.listFish[nAutoFish].nowLockPos != null)
                                    {
                                        var m = common.WordToUI(common.listFish[nAutoFish].nowLockPos.position);
                                        if (objAutoEffect.activeSelf == false)
                                        {
                                            objAutoEffect.SetActive(true);
                                        }
                                        objAutoEffect.transform.localPosition = m;
                                    }
                                  
                                }
                                else
                                {
                                    //隐藏
                                    objAutoEffect.SetActive(false);
                                }
                            }
                        }
                    }
                    else
                    {
                        if (_pi.playerId == PlayerData.PlayerId) //只改变自己的
                        {
                            //如果锁定的鱼不存在 自动选择优先攻击鱼
                            nAutoFish = 0;
                            if (common.listFish.Count > 0)
                            {
                                List<fish> tempList = new List<fish>();
                                DictionaryEx.ForeachValue(common.listFish, (val) => { tempList.Add(val); });
                                tempList.Sort(SortList);
                                for (int i = 0; i < tempList.Count; i++)
                                {
                                    if (tempList[i]!=null)
                                    {
                                        if (CheckAutoFishInScreen(tempList[i].fishState.id))
                                        {
                                            if (tempList[i].IsCanHit == true)
                                            {
                                                nAutoFish = tempList[i].fishState.id;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (nIsChangeAutoFish != nAutoFish)
                            {
                                nIsChangeAutoFish = nAutoFish; //切换的时候请求 省一点性能
                                common3._UIFishingInterface.SyncLockRequest(nAutoFish);
                            }
                        }
                    }

                    //只检查自己的 并清理
                    if (_pi.playerId == PlayerData.PlayerId)
                    {   
                        //检查跟踪的鱼是否游出锁定范围
                        if (!CheckAutoFishInScreen())
                        {
                            ClearAutoFish();
                        }
                    }
                    else
                    {
                        //其他玩家的不清理
                        if (nAutoFish > 0)
                        {
                            if (common.listFish.ContainsKey(nAutoFish))
                            {
                                common.listFish[nAutoFish].GetNowPos();
                            }
                        }
                    }
                }
            }
        }
        /// <summary>
        /// 检查自动攻击的鱼是否在可锁定范围内
        /// </summary>
        /// <returns>true 在</returns>    
        bool CheckAutoFishInScreen()
        {
            if (nAutoFish > 0)
            {
                if (common.listFish.ContainsKey(nAutoFish))
                {
                    return common.listFish[nAutoFish].GetNowPos();
                }
                return false;
            }
            else
                return false;
        }
        /// <summary>
        /// 检查自动攻击的鱼是否在可锁定范围内
        /// </summary>
        /// <returns>true 在</returns>    
        bool CheckAutoFishInScreen(long Fishid)
        {
            if (common.listFish.ContainsKey(Fishid))
            {
                return common.listFish[Fishid].GetNowPos();
            }
            return false;
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
        Ray ray;
        RaycastHit hit;
        int ClickNum = 0;
        //改变自动开火目标
        public void ChangeAutoTarget(Vector2 vInput)
        {
            if (_bAllGenzong)
            {
                ray = Root3D.Instance.cam3D.ScreenPointToRay(vInput);
                if (Physics.Raycast(ray, out hit))
                {
                    //  var other = hit.transform.parent;
                    if (hit.transform.GetComponent<QuoteFish>()==null)
                    {
                        return;
                    }
                    var other = hit.transform.GetComponent<QuoteFish>().yinyongfish;

                    var mfishId = other.fishState.id;
                    if (nAutoFish != mfishId)
                    {
                        if (objAutoEffect != null)
                        {
                            Destroy(objAutoEffect);
                            objAutoEffect = null;
                        }
                        nAutoFish = mfishId;
                    }
                }
            }
        }
      
      
        private int SortList(fish a, fish b) //根据自动攻击的鱼优先
        {
            //正常排序
            if (a.nAutoLevel < b.nAutoLevel) //这边的比较可以是任意的类型，只要是你可以比较的东西，比如student类中的年龄age stu1.age > stu2.age
            {
                return 1;

            }
            else if (a.nAutoLevel > b.nAutoLevel)
            {
                return -1;
            }
            return 0;
        }
     
        private void IsInitThis() 
        {
            _bCanFire = true;
            CanJiaZaiPaotai = false;
            if (obj_Paotaizhuan != null)
            {
                posPaotaiScreen = common.UIToScreenPointUI(objPaotai.position);
                posPaotai = obj_Paotaizhuan.position;
            }
            if (_pi.playerId==PlayerData.PlayerId)
            {
                txt_dragonScale.color =Color.yellow;
            }
            else
            {
                txt_dragonScale.color = Color.white;
            }
            //清空参数
            _ALLDropGold.Clear();
            waitDragonscale = 0;

            //打开时更新
            _bCanFire = true;
            _bCanBossFire = true;
            _bHaveMoney = true;
            RestSkill();
            img_noFire.gameObject.SetActive(false);
            this.transform.localScale = Vector3.zero;
            this.transform.DOScale(1, 0.5f);
            objPaotai.gameObject.SetActive(true);

            //关闭小游戏状态
            txt_GameSate.gameObject.SetActive(false);
            //删除所有特效
            if (suodingEf != null)
            {
                Destroy(suodingEf);
                suodingEf = null;
            }
            if (baojiEf != null)
            {
                Destroy(baojiEf);
                baojiEf = null;
            }
            ////子弹上限限制
            //if (_pi!=null)
            //{
            //    if (common.dicBullet.ContainsKey(_pi.playerId))
            //    {
            //        common.dicBullet.Remove(_pi.playerId);
            //    }
            //}
        }
        private void OnDisable()
        {
            _isChuShiHua = false;
            if (objAutoEffect!=null)
            {
                objAutoEffect.SetActive(false);
                Destroy(objAutoEffect);
            }
        }
        //清除锁定特效
        void ClearAutoFish()
        {

            if (objAutoEffect != null)
            {
                objAutoEffect.gameObject.SetActive(false);
                Destroy(objAutoEffect);
                objAutoEffect = null;
            }
            befornAutoFish = nAutoFish;
            nAutoFish = 0;

         
        }

        float fAngle;
        public void Req_DoFire(Vector2 ptTouch, float fangel = 0f)
        {
            if (PlayerData._isOpenShop)//商城打开了
            {
                return;
            }
            if (_pi.playerId != PlayerData.PlayerId)
            {
                //不是自己
                //Debug.Log("不是自己");
                return;
            }
            if (PlayerData.canfireLittleGame>0)
            {
                //Debug.Log("进入激光钻头炮无法开炮");  //进入小游戏无法开炮
                return;
            }
            //发射CD
            if (Time.time - MylastFireTime < fCD - 0.01f)
            {
                return;
            }
            MylastFireTime = Time.time;
            ////子弹上限限制
            //if (common.dicBullet.ContainsKey(_pi.playerId))
            //{
            //    if (common.dicBullet[_pi.playerId] > 50)
            //    {
            //        if (!img_noFire.gameObject.activeSelf)
            //        {
            //            img_noFire.gameObject.SetActive(true);
            //        }
            //        return;
            //    }
            //    else
            //    {
            //        if (img_noFire.gameObject.activeSelf)
            //        {
            //            img_noFire.gameObject.SetActive(false);
            //        }
            //    }
            //}
            if (common.IDRoomFish == BY_SESSION.龙晶场)
            {
                if (ByData.nModule == 51)
                {
                    if (sdragonscale <= 0)
                    {
                        if (IsHaveMoney == true)
                        {
                            if (nBeforeDragonScale <= 0)
                            {
                                GameObject go = MessageBox.Show("金币不足，请退出大厅可再来一次", "提示", () =>
                                {
                                    NetMessage.Chanllenge.Req_FishingChallengeExitRoomRequest();
                                });
                            }
                            //停掉锁定 电磁炮 暴击
                            StopSkill();
                        }
                        IsHaveMoney = false;
                        return;
                    }
                }
                else
                {
                    long mmp = PlayerData.DragonCrystal;
                    Debug.Log(mmp);
                    //判断龙晶足不足
                    if (sdragonscale <= 0)
                    {
                        //NetMessage.OseeLobby.Req_PlayerMoneyRequest(2);
                        if (IsHaveMoney == true)
                        {
                            CheckLjcTipsKuang();
                            //停掉锁定 电磁炮 暴击
                            StopSkill();
                        }
                        IsHaveMoney = false;
                        return;
                    }
                }
            }
            else
            {
                if (ByData.nModule == 31)
                {
                    IsHaveMoney = true;
                }
            }
            IsHaveMoney = true;
            //自动跟踪 炮台旋转
            if (nAutoFish > 0)
            {
                if (common.listFish.ContainsKey(nAutoFish))
                {
                }
                else
                {
                    return;
                }
            }
            else
            {
                if (obj_Paotaizhuan != null)
                {
                    posPaotai = common.UIToScreenPointUI(obj_Paotaizhuan.position); //因为本身会移动所以得实时检测
                }
                Vector2 dir = ptTouch - posPaotai;
                //计算角度
                fAngle = Vector2.Angle(Vector2.up, dir);
                if (ptTouch.x > posPaotai.x)
                    fAngle = -fAngle;
                if (fangel != 0f)
                {
                    fAngle = fangel;
                }
                SetPaotaiAngle(ref fAngle);
            }
            m++;
            FishingFireResponse vartmp = new FishingFireResponse();
            vartmp.playerId = _pi.playerId;
            vartmp.fireId = m;
            vartmp.angle = fAngle;
            common3._UIFishingInterface.ChangeS_C_DoFire(vartmp, m, nAutoFish, 0, 0, fAngle);
        }
         
        void CheckLjcTipsKuang() {
            //Debug.Log("common.dicBullet"+common.dicBullet[_pi.playerId]);
            //if (common.dicBullet.ContainsKey(_pi.playerId) == false || common.dicBullet[_pi.playerId] <= 0) //子弹归0  并且龙晶归0
            //{
            //    if (PlayerData.DragonCrystal <= nPower * NKuangBaoMult)
            //    {
            //        GameObject go = MessageBox.Show("金币不足是否立即兑换？", "提示", () =>
            //        {
            //            UIShop tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
            //            tmp.Setpanel(JieMain.金币商城);
            //        });
            //    }
            //}
            // if (PlayerData.DragonCrystal <= nPower * NKuangBaoMult)
            if (PlayerData.DragonCrystal <= 0)
            {
                //GameObject go = MessageBox.Show("你已破产,是否前往商城购买金币？", "提示", () =>
                //{
                //    UIShop tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                //    tmp.Setpanel(JieMain.金币商城);
                //});
                var tmp = UIMgr.ShowUISynchronize(UIPath.UIShop).GetComponent<UIShop>();
                //tmp.Setpanel(JieMain.金币商城);
                tmp.Setpanel(JieMain.钻石商城);
            }
        }
        /// <summary>
        ///停掉锁定 电磁炮 暴击        
        /// </summary>
        public void StopSkill() {
            if (common.IDRoomFish == BY_SESSION.龙晶场)
            {
                if (PlayerData._bZiDong)//关闭自动
                {
                    EventManager.IsOnAuto?.Invoke(false);
                }
                if (_bBaoJi)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.FURY);
                }
                if (_bDiancipao)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.SHANDIANPAO);
                }
                if (_bGenzong)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)8);
                }
            }
            else if (common.IDRoomFish == BY_SESSION.大奖赛)
            {
                if (PlayerData._bZiDong)//关闭自动
                {
                    EventManager.IsOnAuto?.Invoke(false);
                    //common3._UIFishingInterface.SetZiDong(false);
                }
                if (_bBaoJi)
                {
                    NetMessage.BigAward.Req_FishingGrandPrixUseSkillRequest((int)BY_SKILL.FURY);
                }
                if (_bDiancipao)
                {
                    NetMessage.BigAward.Req_FishingGrandPrixUseSkillRequest((int)BY_SKILL.SHANDIANPAO);
                }
                if (_bGenzong)
                {
                    NetMessage.BigAward.Req_FishingGrandPrixUseSkillRequest((int)8);
                }
            }
            //没钱的时候 请求救济金次数
            if (PlayerData.DragonCrystal<=0)
            {
                NetMessage.OseeLobby.Req_PlayerMoneyRequest(3);
            }
        }
        //点击射击
        public void Click_Req_DoFire(Vector2 ptTouch, float fangel = 0f)
        {
            if (_pi.playerId != PlayerData.PlayerId)
            {
                //不是自己
                //Debug.Log("不是自己");
                return;
            }
            if (PlayerData.canfireLittleGame > 0)
            {
                //Debug.Log("进入激光钻头炮无法开炮");  //进入小游戏无法开炮
                return;
            }
            //发射CD
            if (Time.time - MylastFireTime < fCD - 0.01f)
            {
                return;
            }
            MylastFireTime = Time.time;
            if (common.IDRoomFish == BY_SESSION.龙晶场)
            {
                if (ByData.nModule == 51)
                {
                    //判断龙晶足不足
                    //if (sdragonscale <= nPower * NKuangBaoMult)
                    if (sdragonscale <= 0)
                    {
                        GameObject go = MessageBox.Show("金币不足，请退出大厅可再来一次", "提示", () =>
                        {
                            NetMessage.Chanllenge.Req_FishingChallengeExitRoomRequest();
                        });
                        //停掉锁定 电磁炮 暴击
                        StopSkill();
                        return;
                    }
                }
                else
                {
                    //判断龙晶足不足
                    if (PlayerData.DragonCrystal - waitDragonscale <= 0)
                    {
                        CheckLjcTipsKuang();
                        return;
                    }
                }
            }
            else
            {
                if (ByData.nModule == 31)
                {
                    IsHaveMoney = true;
                }
      
            }
            IsHaveMoney = true;
            //自动跟踪 炮台旋转
            if (nAutoFish > 0)
            {
                if (common.listFish.ContainsKey(nAutoFish))
                {

                }
                else
                {
                    return;
                }
            }
            else
            {
                //ptTouch = ptTouch;// Root3D.Instance.UICamera.ScreenToWorldPoint(ptTouch);
                if (obj_Paotaizhuan != null)
                {
                    posPaotai = common.UIToScreenPointUI(obj_Paotaizhuan.position); //因为本身会移动所以得实时检测
                }
                Vector2 dir = ptTouch - posPaotai;
                //计算角度
                fAngle = Vector2.Angle(Vector2.up, dir);
                if (ptTouch.x > posPaotai.x)
                    fAngle = -fAngle;


                if (fangel != 0f)
                {
                    fAngle = fangel;
                }
                SetPaotaiAngle(ref fAngle);

            }

            m++;
            FishingFireResponse vartmp = new FishingFireResponse();
            vartmp.playerId = _pi.playerId;
            vartmp.fireId = m;
            vartmp.angle = fAngle;
            common3._UIFishingInterface.ChangeS_C_DoFire(vartmp, m, nAutoFish, 0, 0, fAngle);

        }
        /// <summary>
        /// 其它玩家发射子弹回包
        /// </summary>
        /// <param name="angle"></param>
        /// <param name="fireId"></param>
        public void Other_Re_DoFire(float angle, long fireId)
        {
            //不是锁定则需要改旋转的角度
            if (nAutoFish <= 0)
            {
                if (n_RotateSeat == n_nameSeat)//自己这方向没有变
                {
                    if (n_RotateSeat == 0 && n_nameSeat == 0)
                    {
                    }
                    else if (n_RotateSeat == 1 && n_nameSeat == 1)
                    {
                    }
                    else if (n_RotateSeat == 2 && n_nameSeat == 2)
                    {
                        angle = angle - 180f;
                    }
                    else if (n_RotateSeat == 3 && n_nameSeat == 3)
                    {
                        angle = angle - 180f;
                    }
                }
                else//自己这方旋转了
                {
                    //但如果自己旋转了 处于下方  但其他人和自己一样处于下方  则交换数据时 则不旋转
                    if (n_nameSeat == 0 || n_nameSeat == 1)
                    {
                      
                    }
                    else
                    {
                        if (n_RotateSeat == 0 && n_nameSeat == 2)
                        {
                            angle = angle - 180f;
                        }
                        else if (n_RotateSeat == 1 && n_nameSeat == 3)
                        {
                            angle = angle - 180f;
                        }
                        else if (n_RotateSeat == 2 && n_nameSeat == 0)
                        {
                            angle = 180f - angle;
                        }
                        else if (n_RotateSeat == 3 && n_nameSeat == 1)
                        {
                            angle = 180f - angle;
                        }
                    }
                }
            }

            Fire(angle, fireId, nPower, autoFishID: nAutoFish);
        }
        /// <summary>
        /// 自己发射子弹的 立马响应 没经过服务器
        /// </summary>
        /// <param name="fr"></param>
        public void Re_DoFire(FishingFireResponse fr)
        {
            if (fr.playerId != PlayerData.PlayerId)
            {
                return;
            }
       
            Fire(fr.angle, fr.fireId, nPower, autoFishID: nAutoFish);
        }
        /// <summary>
        /// 当前玩家开火
        /// </summary>
        /// <param name="fAngle"></param>
        /// <param name="id"></param>
        /// <param name="paoLevel"></param>
        /// <param name="autoFishID"></param>
        /// <param name="robotId"></param>
        void Fire(float fAngle, long id, int paoLevel, long autoFishID = 0, long robotId = -1)
        {
            if (autoFishID > 0)//如果跟踪弹 转向 纠正延迟方向偏差
            {
                if (common.listFish.ContainsKey(nAutoFish))
                {
                    if (common.listFish[nAutoFish].nowLockPos != null)
                    {
                        //位置
                        Vector2 ptTouch = common.WordToScreenPointUI(common.listFish[nAutoFish].nowLockPos.position);
                        
                        if (obj_Paotaizhuan!=null)
                        {
                            posPaotai = common.UIToScreenPointUI(obj_Paotaizhuan.position);
                        }
                        Vector2 dir = ptTouch - posPaotai;
                        //计算角度
                        fAngle = Vector2.Angle(Vector2.up, dir);
                        if (ptTouch.x > posPaotai.x)
                            fAngle = -fAngle;
                        SetPaotaiAngle(ref fAngle);
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    return;
                }
            }
            else
            {
                SetPaotaiAngle(ref fAngle);
            }
            common.OldInputAngle = fAngle;
         
            //SetPaotaiAngle(ref fAngle);
            InitBullet(fAngle, id, paoLevel, autoFishID, robotId);
        }
        public void RestObjPaoFire() {
            //炮口特效
            if (objPao != null)
            {
                objPao.Fire();
            }
        }
        /// <summary>
        /// 生成子弹
        /// </summary>
        /// <param name="fAngle"></param>
        /// <param name="id"></param>
        /// <param name="paoLevel"></param>
        /// <param name="autoFishID"></param>
        /// <param name="robotId"></param>
        void InitBullet(float fAngle, long id, int paoLevel, long autoFishID = 0, long robotId = -1)
        {
            if (_bDiancipao) //电磁炮使用激光
            {
                if (common.listFish.ContainsKey(nAutoFish))//存在此鱼
                {
                    var m = common.WordToScreenPointUI(common.listFish[nAutoFish].nowLockPos.position);
                    //  var m2 = common.UIToScreenPointUI(objPao.pos_fire[0].transform.position);
                    var m2 = common.UIToScreenPointUI(objPao.transform.position);
                    Vector2 m3;
                    Vector2 m4;
                    RectTransformUtility.ScreenPointToLocalPointInRectangle(common2.NormalUICanvas.GetComponent<RectTransform>(), m, Root3D.Instance.UICamera, out m3);
                    RectTransformUtility.ScreenPointToLocalPointInRectangle(common2.NormalUICanvas.GetComponent<RectTransform>(), m2, Root3D.Instance.UICamera, out m4);

                    float distance = Vector3.Distance(m3, m4);
                    objPao.violentFire(distance / 640f);

                    //击中鱼****************
                    fish fs = common.listFish[nAutoFish];
                    //发送server
                    long netId = id;
                    if (nAutoFish <= 0)//不为锁定
                    {
                    }
                    else//是锁定
                    {
                        if (netId != -1)//子弹id不是复数
                        {
                            if (robotId == -1)//是自己
                            {

                                if (_pi.playerId == PlayerData.PlayerId)
                                {
                                    if (common.IDRoomFish == BY_SESSION.龙晶场)
                                    {
                                        NetMessage.Chanllenge.Req_FishingChallengeFireRequest(netId, nAutoFish, 0);
                                    }
                                    else
                                    {
                                        //common3._UIFishingInterface.Req_FishingFightFishRequest(netId, nAutoFish);
                                        NetMessage.BigAward.Req_FishingGrandPrixFireRequest(netId, nAutoFish, 0);
                                    }
                                   // NetMessage.Chanllenge.Req_FishingChallengeFireRequest(netId, nAutoFish, 0);
                                    common3._UIFishingInterface.Req_FishingFightFishRequest(netId, nAutoFish);

                                    if (fs != null)
                                    {
                                        fs.fishBeHitColor();
                                    }
                                }

                            }
                        }

                    }

                }
            }
            else
            {
                //炮口特效
                if (objPao != null)
                {
                    objPao.Fire();
                }
                if (_bBaoJi) //子弹变大
                {     //是自己播放声音
                    if (PlayerData.PlayerId == _pi.playerId)
                    {
                        SoundLoadPlay.PlaySound("fury1");
                    }
                    var vargo = commonLoad.GetOneBullet(nlocalPaoPerfab);
                    vargo.transform.SetParent(common2.BulletPos, false);
                    var go = vargo.GetComponent<playerBullet>();
                    go.base_paotai = objPao;
                    go.gameObject.SetActive(true);
                    go.transform.localScale = Vector3.one * 1.5f;
                    go.transform.position = objPao.pos_fire[0].position;
                    go.transform.localEulerAngles = new Vector3(0, 0, fAngle);
                    go.name = id.ToString();
                    go.bulletID = id;
                    go.nPower = paoLevel;
                    go.nPaoMute = 1;
                    //go.Init(nlocalPaoPerfab, nTmpPower, _pi.playerId, robotId);
                    go.Init(nlocalPaoPerfab, nTmpPower, _pi.playerId, robotId);
                    go.nTargetId = autoFishID;
                }
                else //普通子弹
                {     //是自己播放声音
                    if (PlayerData.PlayerId == _pi.playerId)
                    {
                        SoundLoadPlay.PlaySound("sd_t4_kaipao");
                    }
                    var vargo = commonLoad.GetOneBullet(nlocalPaoPerfab);
                    vargo.transform.SetParent(common2.BulletPos, false);
                    var go = vargo.GetComponent<playerBullet>();
                    go.base_paotai = objPao;
                    go.gameObject.SetActive(true);
                    go.transform.localScale = Vector3.one;
                    go.transform.position = objPao.pos_fire[0].position;
                    go.transform.localEulerAngles = new Vector3(0, 0, fAngle);
                    go.name = id.ToString();
                    go.bulletID = id;
                    go.nPower = paoLevel;
                    go.nPaoMute = 1;
                    //go.Init(nlocalPaoPerfab, nPaoView, _pi.playerId, robotId);
                    go.Init(nlocalPaoPerfab, nTmpPower, _pi.playerId, robotId);
                    go.nTargetId = autoFishID;
                }
            }
        }
        //闪电自动开炮范围
        bool CheckShandianAutoFishInScreen(long varDianAutoFish)
        {
            if (varDianAutoFish > 0)
            {
                if (common.listFish.ContainsKey(varDianAutoFish))
                {
                    VarCamrePosV3 = common.WordToUI(common.listFish[varDianAutoFish].nowLockPos.position);                  
                    if (VarCamrePosV3.x >= 0 && VarCamrePosV3.y >= 0 && VarCamrePosV3.x <= common.W && VarCamrePosV3.y <= common.H)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                return false;
            }
            else
                return false;
        }
        //机器人
        #region
        void SetPaotaiRobotAngle(ref float fAngle)
        {
            //Debug.Log(fAngle); 
            if (n_RotateSeat < 2)
            {
                fAngle = Mathf.Clamp(fAngle, -80, 80);
            }
            else
            {
                if (fAngle >= 90)
                {
                    fAngle = Mathf.Clamp(fAngle, 100, 260);
                }
                else
                {
                    fAngle = Mathf.Clamp(fAngle, -80, 80);
                }
            }
        }
        //普通场机器人开炮 
        public void S_C_NormalRobotDoFire(FishingRobotFireResponse fr)
        {
            ChangeGold(fr.restMoney);
            float fAngle = 90;
            //自动跟踪 炮台旋转
            if (common.listFish.ContainsKey(fr.fishId) && common.listFish[fr.fishId] != null)
            {
                //是否在屏幕内
                if (CheckShandianAutoFishInScreen(fr.fishId))
                {
                    Vector2 ptTouch = common.WordToUI(common.listFish[fr.fishId].nowLockPos.position);
                    ptTouch = Root3D.Instance.UICamera.ScreenToWorldPoint(ptTouch);
                    //if (posPaotai == Vector2.zero)
                    //    posPaotai = RectTransformUtility.WorldToScreenPoint(Root3D.Instance.UICamera, objPaotai.transform.position);
                    Vector2 dir = ptTouch - posPaotai;
                    //计算角度
                    fAngle = Vector2.Angle(Vector2.up, dir);
                    SetPaotaiRobotAngle(ref fAngle);
                    if (ptTouch.x > posPaotai.x)
                        fAngle = -fAngle;

                    RobotOldAngle = fAngle;
                    //其它情况不发射子弹
                    Fire(RobotOldAngle, fr.fireId, nPower, robotId: fr.robotId);
                }
            }
        }
        public void DoOverPlayGameState()
        {
            //状态结束
            canfireLittleGame = false;
            txt_GameSate.gameObject.SetActive(false);

        }
        public void DoPlayGameState(long state, long time)
        {
            if (state == 1)//小玛利
            {
                txt_GameSate.text = "小玛利中";
                txt_GameSate.gameObject.SetActive(true);
                canfireLittleGame = true;
            }
            else if (state == 2)//柯金
            {
                txt_GameSate.text = "珂晶中";
                txt_GameSate.gameObject.SetActive(true);
                canfireLittleGame = true;
            }
            else if (state == 3)//龙王宝藏
            {
                txt_GameSate.text = "龙王宝藏中";
                txt_GameSate.gameObject.SetActive(true);
                canfireLittleGame = true;
            }
            CancelInvoke("DoOverPlayGameState");
            Invoke("DoOverPlayGameState", 10f);
            //设置状态
        }
        //大奖赛机器人开炮 
        public void S_C_GrandPrixRobotDoFire(FishingGrandPrixRobotFireResponse fr)
        {
            //if (common.IDRoomFish == BY_SESSION.龙晶场)
            //{
            //    ChangeDragonScale(fr.restMoney);
            //}
            //else
            //{
            //    ChangeGold(fr.restMoney);
            //}
            //float fAngle = 90;
            //if (_pi.seat == 0 || _pi.seat == 1)
            //{
            //    fAngle = 0;
            //}
            //else
            //{
            //    fAngle = 180;
            //}
            ////自动跟踪 炮台旋转
            //if (common.listFish.ContainsKey(fr.fishId) && common.listFish[fr.fishId] != null)
            //{
            //    Vector2 ptTouch = common.WordToUI(common.listFish[fr.fishId].nowLockPos.position);
            //    ptTouch = Root3D.Instance.UICamera.ScreenToWorldPoint(ptTouch);
    
            //    Vector2 dir = ptTouch - posPaotai;
            //    //计算角度
            //    fAngle = Vector2.Angle(Vector2.up, dir);
            //    SetPaotaiRobotAngle(ref fAngle);
            //    if (ptTouch.x > posPaotai.x)
            //        fAngle = -fAngle;

            //    RobotOldAngle = fAngle;
            //    Fire(fAngle, fr.fireId, nPower, robotId: fr.robotId);
            //}
        }
        //挑战赛机器人开炮
        public void S_C_ChangeRobotDoFire(FishingChallengeRobotFireResponse fr)
        {
            //if (fr.restMoney==0)
            //{
            //    Debug.Log("restMoney");
            //}
            //Debug.Log("发射子弹robotId" + fr.robotId + "x" + fr.restMoney);
            ChangeDragonScale(fr.restMoney);
            if (fr.fishId > 0)
            {
                //跟踪鱼
                //Debug.Log(n_nameSeat+"发射角度fishid" + fr.fishId + "x" + fr.angle);
                //nAutoFish = 0;
                //if (common.listFish.ContainsKey(fr.fishId))
                //{
                //    Vector2 ptTouch = common.WordToUI(common.listFish[fr.fishId].nowLockPos.position);
                //    ptTouch = Root3D.Instance.UICamera.ScreenToWorldPoint(ptTouch);
                //    Vector2 dir = ptTouch - posPaotai;
                //    //计算角度
                //    fAngle = Vector2.Angle(Vector2.up, dir);
                //    SetPaotaiRobotAngle(ref fAngle);
                //    if (ptTouch.x > posPaotai.x)
                //        fAngle = -fAngle;

                //    RobotOldAngle = fAngle;
                //    Fire(fAngle, fr.fireId, nPower, robotId: fr.robotId);
                //}
                //else
                //{
                //    //用之前的角度
                //    Fire(RobotOldAngle, fr.fireId, nPower, robotId: fr.robotId);
                //}
            }
            else
            {
                //Debug.Log(n_nameSeat+"发射角度robotId" + fr.angle);
                nAutoFish = 0;
                fAngle = fr.angle;
                if (n_RotateSeat == n_nameSeat)//自己这方向没有变
                {
                    if (n_RotateSeat == 0 && n_nameSeat == 0)
                    {
                    }
                    else if (n_RotateSeat == 1 && n_nameSeat == 1)
                    {
                    }
                    else if (n_RotateSeat == 2 && n_nameSeat == 2)
                    {
                        fAngle = fAngle - 180f;
                    }
                    else if (n_RotateSeat == 3 && n_nameSeat == 3)
                    {
                        fAngle = fAngle - 180f;
                    }
                }
                else//自己这方旋转了
                {
                    //但如果自己旋转了 处于下方  但其他人和自己一样处于下方  则交换数据时 则不旋转
                    if (n_nameSeat == 0 || n_nameSeat == 1)
                    {

                    }
                    else
                    {
                        if (n_RotateSeat == 0 && n_nameSeat == 2)
                        {
                            fAngle = fAngle - 180f;
                        }
                        else if (n_RotateSeat == 1 && n_nameSeat == 3)
                        {
                            fAngle = fAngle - 180f;
                        }
                        else if (n_RotateSeat == 2 && n_nameSeat == 0)
                        {
                            fAngle = 180f - fAngle;
                        }
                        else if (n_RotateSeat == 3 && n_nameSeat == 1)
                        {
                            fAngle = 180f - fAngle;
                        }
                    }
                }
                ////计算角度
                //if (RobotOldAngle!= fAngle)
                //{
                //    Debug.Log(n_nameSeat + "发射角度RobotOldAngle" + RobotOldAngle);
                //}
                RobotOldAngle = fAngle;
                Fire(fAngle, fr.fireId, nPower, robotId: fr.robotId);
            }
        }
        #endregion

        //void SetPaotaiAngle(ref float fAngle, Action callback)
        //{
        //    if (this.name == "2" || this.name == "3")
        //    {
        //        if (fAngle < 0)
        //        {
        //            if (fAngle > -90)
        //                fAngle = -90;
        //        }
        //        else
        //        {
        //            if (fAngle < 90)
        //                fAngle = 90;
        //        }

        //    }
        //    else
        //    {
        //        fAngle = Mathf.Clamp(fAngle, -90, 90);
        //    }
        //    if (obj_Paotaizhuan == null)
        //    {
        //        try
        //        {
        //            obj_Paotaizhuan = objPaotai.transform.GetChild(1).Find("pao");
        //        }
        //        catch
        //        {
        //        }

        //    }
        //    if (obj_Paotaizhuan != null)
        //    {
        //        obj_Paotaizhuan.transform.localEulerAngles = new Vector3(obj_Paotaizhuan.localEulerAngles.x, obj_Paotaizhuan.localEulerAngles.y, fAngle);
        //        callback();
        //    }
        //}
        //void SetAngle(Vector2 ptTouch, float fangel = 0f)
        //{
        //    ////自动跟踪 炮台旋转
        //    if (nAutoFish > 0)
        //    {
        //        return;
        //    } 
        //    else
        //    {
        //        ptTouch = Root3D.Instance.UICamera.ScreenToWorldPoint(ptTouch);
        //        //posPaotai = objPaotai.transform.position;
        //    }

        //    Vector2 dir = ptTouch - posPaotai;

        //    //计算角度
        //    float fAngletmp = Vector2.Angle(Vector2.up, dir);

        //    if (ptTouch.x > posPaotai.x)
        //        fAngletmp = -fAngletmp;


        //    if (nAutoFish <= 0 && fangel != 0f)
        //    {
        //        fAngletmp = fangel;
        //    }

        //    SetPaotaiAngle(ref fAngletmp);
        //}
        void SetPaotaiAngle(ref float fAngle)
        {
            if (n_nameSeat == 2 || n_nameSeat == 3)
            {
                if (fAngle < 0)
                {
                    if (fAngle > -90)
                        fAngle = -90;
                }
                else
                {
                    if (fAngle < 90)
                        fAngle = 90;
                }
            }
            else
            {
                fAngle = Mathf.Clamp(fAngle, -90, 90);
            }
            try
            {
                if (obj_Paotaizhuan == null)
                {
                    obj_Paotaizhuan = objPaotai.transform.Find("playerpao/pao");
                }
                if (obj_Paotaizhuan != null)
                {
                    obj_Paotaizhuan.transform.localEulerAngles = new Vector3(obj_Paotaizhuan.localEulerAngles.x, obj_Paotaizhuan.localEulerAngles.y, fAngle);
                }
            }
            catch 
            {
                //Debug.Log("objPaotai为null");
            }
        }

        public void ClearThis() {
            imgDizuo.gameObject.SetActive(false);
            bgNameGold.gameObject.SetActive(false);
            bAutoFire = false;
            isInit = false;
            //关闭小游戏状态
            txt_GameSate.gameObject.SetActive(false);

            if (suodingEf != null)
            {
                Destroy(suodingEf);
                suodingEf = null;
            }
            if (baojiEf != null)
            {
                Destroy(baojiEf);
                baojiEf = null;
            }
            if (objAutoEffect!=null)
            {
                Destroy(objAutoEffect);
                objAutoEffect = null;
            }            
        }
        void SetAutoButtonShow() {
            

            EventManager.IsOnAuto?.Invoke(PlayerData._bZiDong);

            //if (PlayerData._bZiDong == true)
            //{
            //    //btn_Autofire.transform.GetComponent<Image>().color = Color.gray;
            //    btn_Autofire.transform.Find("State1").gameObject.SetActive(true);
            //    btn_Autofire.transform.Find("State2").gameObject.SetActive(false);
            //}
            //else
            //{
            //    btn_Autofire.transform.Find("State1").gameObject.SetActive(false);
            //    btn_Autofire.transform.Find("State2").gameObject.SetActive(true);
            //    //btn_Autofire.transform.GetComponent<Image>().color = Color.white;
            //}
        }
        public void InitPaoAndWin(int paoView, int wingIdex, int paoPower)
        {
            SetAutoButtonShow();
            //初始化
            isInit = true;
            imgDizuo.gameObject.SetActive(true);
            bgNameGold.gameObject.SetActive(true);
            IsInitThis();
     
            nPower = paoPower;
            InitPlayerInfo();
            
            //必须先更新炮台 再更新翅膀
            //UpdatePaotai(paoView);

            //UpdateWing(wingIdex);
            if (_pi.playerId==PlayerData.PlayerId)
            {
                PlayerData._isOpenShop = false;
            }
        }
        public void InitPaoAndWinOther(int paoView, int wingIdex, int paoPower) {
            SetAutoButtonShow();
            //必须先更新炮台 再更新翅膀
            //UpdatePaotai(paoView);
            //UpdateWing(wingIdex);
            nPower = paoPower;
        }
        public  void InitPlayerInfo()
        {
            ChangeNKuangBaoMult();
            canfireLittleGame = false;
            //玩家信息 
            if (_pi.name.Length > 2)
            {
                txt_Name.text = _pi.name.Substring(0, 2) + "****   ";
            }
            else
            {
                txt_Name.text = _pi.name + "****   ";
            }
            ChangeGold(_pi.money);
            ChangeDiamond(long.Parse(_pi.diamond));
            img_noFire.gameObject.SetActive(false);
            if (PlayerData.PlayerId == _pi.playerId)
            {
                if (ByData.nModule==51)
                {
                }
                else
                {
                    ChangeDiamond(PlayerData.Diamond);
                    ChangeDragonScale(PlayerData.DragonCrystal);
                }
             
                DsRecycleAll();
                
                //Debug.Log("炮倍nModule" + ByData.nModule);
                //Debug.Log("炮倍Minlevel" + common.dicPaoFwConfig[ByData.nModule].Minlevel);
                //Debug.Log("炮倍Maxlevel" + common.dicPaoFwConfig[ByData.nModule].Maxlevel);
                foreach (var item in common.dicPaoConfig)
                {
                    if (item.Key >= common.dicPaoFwConfig[ByData.nModule].Minlevel && item.Key <= common.dicPaoFwConfig[ByData.nModule].Maxlevel)
                    {
                        //Debug.Log("炮倍"+item.Key);
                        InitPaoBeiText(item.Key.ToString());
                    }
                }
                ScrollTo(nPower);
                btn_add.gameObject.SetActive(true);
                btn_Autofire.gameObject.SetActive(true);
            }
            else
            {
                btn_add.gameObject.SetActive(false);
                btn_Autofire.gameObject.SetActive(false);
            }
            _isChuShiHua = true;
        }
        public void ScrollTo(int tmp)
        {
            for (int i = 0; i < DstmpList.Count; i++)
            {
                if (DstmpList[i].transform.Find("Item Label").GetComponent<Text>().text == tmp.ToString())
                {
                    float m = i / (float)(DstmpList.Count - 1);
                    varScrollRect.verticalNormalizedPosition = 1f - m;
                    DstmpList[i].GetComponent<Toggle>().isOn = true;
                }
            }

        }
        public void DsRecycleAll()
        {
            for (int i = DstmpList.Count - 1; i >= 0; i--)
            {
                Dspool.Recycle(DstmpList[i]);
                DstmpList.RemoveAt(i);
            }
            DstmpList.Clear();
        }
        public void InitPaoBeiText(string text)
        {
            var go = Dspool.Get();
            DstmpList.Add(go);
            go.transform.SetParent(Dscontent);
            go.transform.Find("Item Label").GetComponent<Text>().text = text;
            go.transform.SetAsLastSibling();
            go.gameObject.SetActive(true);

            go.GetComponent<Toggle>().onValueChanged.RemoveAllListeners();
            if (nPower.ToString() == text)
            {
                DstmpList[DstmpList.Count - 1].GetComponent<Toggle>().isOn = true;
            }
            else
            {
                go.GetComponent<Toggle>().isOn = false;
            }
            go.GetComponent<Toggle>().onValueChanged.AddListener((arg) =>
            {
                if (common.IDRoomFish == BY_SESSION.普通场)
                {
                    NetMessage.OseeFishing.Req_FishingChangeBatteryLevelRequest(int.Parse(go.transform.Find("Item Label").GetComponent<Text>().text));
                }
                else if (common.IDRoomFish == BY_SESSION.龙晶场)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeChangeBatteryLevelRequest(int.Parse(go.transform.Find("Item Label").GetComponent<Text>().text));
                }
                else if (common.IDRoomFish == BY_SESSION.大奖赛)
                {
                }
                paoDropdown.gameObject.SetActive(false);

            });
            go.transform.localScale = Vector3.one;
        }
        bool CanJiaZaiPaotai;
        //public  void UpdatePaotai(int tmpPaoView)
        //{
        //    if (n_nameSeat==-1)
        //    {
        //        n_nameSeat = int.Parse(this.name);
        //    }
        //    if (tmpPaoView == -1)
        //    {
        //        return;
        //    }
        //    if (objPaotai == null)
        //    {
        //        //说明这时player可能都没生成
        //        return;
        //    }
        //    if (tmpPaoView == 0)
        //    {
        //        tmpPaoView = 70;
        //    }

        //    //为空 或者不相同 则更新
        //    if (objPao == null|| nPaoView != tmpPaoView)
        //    {
        //        if (objPao!=null)
        //        {
        //            objPao.gameObject.SetActive(false);
        //            DestroyImmediate(objPao.gameObject);
        //        }
        //        //获取本地ID
        //        nlocalPaoPerfab = GetlocalPaoPerfab(tmpPaoView); //common5.JsonCanShu["PaoName"][nPaoView][0].ToString();// nPaoView
        //        GameObject Go = common4.LoadPrefab("Paotai/pao_" + nlocalPaoPerfab);
        //        var varGo = UnityEngine.Object.Instantiate(Go, this.objPaotai);
        //        varGo.name = "playerpao";
        //        objPao = varGo.GetComponent<paotai>();
        //        obj_Paotaizhuan = objPaotai.transform.Find("playerpao/pao");
        //        objPao.transform.localPosition = Vector3.zero;
        //        objPao.transform.localEulerAngles = Vector3.zero;
        //        if (obj_Paotaizhuan != null)
        //        {
        //            posPaotaiScreen = common.UIToScreenPointUI(objPaotai.position);
        //            posPaotai = obj_Paotaizhuan.position;
        //        }
        //        if (n_nameSeat == 2 || n_nameSeat == 3)//如果在上方旋转
        //        {
        //            if (obj_Paotaizhuan != null)
        //            {
        //                obj_Paotaizhuan.transform.localEulerAngles = new Vector3(0, 0, 180);
        //                obj_Paotaizhuan.transform.localPosition = new Vector3(obj_Paotaizhuan.localPosition.x, -obj_Paotaizhuan.localPosition.y, obj_Paotaizhuan.localPosition.z);
        //            }
        //        }
        //        nPaoView = tmpPaoView;

        //        if (CanJiaZaiPaotai==false)
        //        {
        //            CanJiaZaiPaotai = true;

        //        }
        //        //更新炮台了也必须更新翅膀
        //        //UpdateWing(nWingView);
        //    }
        //    else
        //    {
        //    }

        //    if (objPao != null)
        //    {
        //        if (objPao.violent != null)
        //        {
        //            if (_pi.playerId == PlayerData.PlayerId)
        //            {
        //                objPao.violent.GetComponent<AudioSource>().enabled = true;
        //                EventManager.SoundYinXiaoUpdate?.Invoke(SoundHelper.GameVolume);
        //            }
        //            else
        //            {
        //                objPao.violent.GetComponent<AudioSource>().enabled = false;
        //            }
        //        }
        //    }
        //}


        public void UpdateBeiPaotai(int tmppPower) 
        {
            if (n_nameSeat == -1)
            {
                n_nameSeat = int.Parse(this.name);
            }
            if (objPaotai == null)
            {
                //说明这时player可能都没生成
                return;
            }
            //为空 或者不相同 则更新
            if (objPao == null || nTmpPower != tmppPower)
            {
                if (objPao != null)
                {
                    objPao.gameObject.SetActive(false);
                    DestroyImmediate(objPao.gameObject);
                }
                //获取本地ID
                nlocalPaoPerfab = GetlocalPaoPerfab(tmppPower); //common5.JsonCanShu["PaoName"][nPaoView][0].ToString();// nPaoView
                GameObject Go = common4.LoadPrefab("Paotai/pao_" + nlocalPaoPerfab);
                var varGo = UnityEngine.Object.Instantiate(Go, this.objPaotai);
                varGo.name = "playerpao";
                objPao = varGo.GetComponent<paotai>();
                obj_Paotaizhuan = objPaotai.transform.Find("playerpao/pao");
                objPao.transform.localPosition = Vector3.zero;
                objPao.transform.localEulerAngles = Vector3.zero;
                if (obj_Paotaizhuan != null)
                {
                    posPaotaiScreen = common.UIToScreenPointUI(objPaotai.position);
                    posPaotai = obj_Paotaizhuan.position;
                }
                if (n_nameSeat == 2 || n_nameSeat == 3)//如果在上方旋转
                {
                    if (obj_Paotaizhuan != null)
                    {
                        obj_Paotaizhuan.transform.localEulerAngles = new Vector3(0, 0, 180);
                        obj_Paotaizhuan.transform.localPosition = new Vector3(obj_Paotaizhuan.localPosition.x, -obj_Paotaizhuan.localPosition.y, obj_Paotaizhuan.localPosition.z);
                    }
                }
                nTmpPower = tmppPower;

                if (CanJiaZaiPaotai == false)
                {
                    CanJiaZaiPaotai = true;

                }
                //更新炮台了也必须更新翅膀
                //UpdateWing(nWingView);
            }
            else
            {
            }

            if (objPao != null)
            {
                if (objPao.violent != null)
                {
                    if (_pi.playerId == PlayerData.PlayerId)
                    {
                        objPao.violent.GetComponent<AudioSource>().enabled = true;
                        EventManager.SoundYinXiaoUpdate?.Invoke(SoundHelper.GameVolume);
                    }
                    else
                    {
                        objPao.violent.GetComponent<AudioSource>().enabled = false;
                    }
                }
            }
        }
        public void TestUpdatePaotai(int varPaoView)
        {
            //Debug.Log("varPaoView" + varPaoView);
            //if (varPaoView == 0)
            //{
            //    varPaoView = 70;
            //}

            //if (objPao != null)
            //{
            //    DestroyImmediate(objPao.gameObject);
            //}
            //if (objPaotai != null)
            //{
            //    //获取本地ID
            //    nlocalPaoPerfab = GetlocalPaoPerfab(varPaoView); //common5.JsonCanShu["PaoName"][nPaoView][0].ToString();// nPaoView
            //                                                     //生成
            //    GameObject Go = common4.LoadPrefab("Paotai/pao_" + nlocalPaoPerfab);
            //    var varGo = UnityEngine.Object.Instantiate(Go, this.objPaotai);
            //    objPao = varGo.GetComponent<paotai>();

            //    obj_Paotaizhuan = objPaotai.transform.GetChild(1).Find("pao");
            //    objPao.transform.localPosition = Vector3.zero;
            //    objPao.transform.localEulerAngles = Vector3.zero;

            //    if (n_nameSeat == 2 || n_nameSeat == 3)//如果在上方旋转
            //    {
            //        if (obj_Paotaizhuan != null)
            //        {
            //            obj_Paotaizhuan.transform.localEulerAngles = new Vector3(0, 0, 180);
            //            obj_Paotaizhuan.transform.localPosition = new Vector3(obj_Paotaizhuan.localPosition.x, -obj_Paotaizhuan.localPosition.y, obj_Paotaizhuan.localPosition.z);
            //        }
            //    }

            //}
        }
        ///// <summary>
        ///// 获取炮台和翅膀的本地ID
        ///// </summary>
        ///// <param name="needpd"></param>
        ///// <returns></returns>
        //int GetlocalPaoPerfab(int needpd) {
        //    if (common4.PaoWinConfig.ContainsKey(needpd))
        //    {
        //        return common4.PaoWinConfig[needpd].modelID;
        //    }
        //    Debug.LogError("needpd PaoName is null:"+ needpd);
        //    return 10000;
        //}
        int GetlocalPaoPerfab(int needpd)
        {
            if (PlayerData.PlayerId==_pi.playerId)
            {
                if (ByData.nModule == 11)
                {
                    if (needpd < 21)
                    {
                        return 10000;
                    }
                    else if (needpd < 51)
                    {
                        return 10001;
                    }
                    else
                    {
                        return 10002;
                    }
                }
                else if (ByData.nModule == 12)
                {
                    if (needpd < 1001)
                    {
                        return 10000;
                    }
                    else if (needpd < 5001)
                    {
                        return 10001;
                    }
                    else
                    {
                        return 10002;
                    }
                }
                else if (ByData.nModule == 13)
                {
                    if (needpd < 5001)
                    {
                        return 10000;
                    }
                    else if (needpd < 20001)
                    {
                        return 10001;
                    }
                    else
                    {
                        return 10002;
                    }
                }
                else
                {
                    return 10000;
                }
            }
            else
            {
                if (ByData.nModule == 11)
                {
                    if (needpd < 21)
                    {
                        return 20000;
                    }
                    else if (needpd < 51)
                    {
                        return 20001;
                    }
                    else
                    {
                        return 20002;
                    }
                }
                else if (ByData.nModule == 12)
                {
                    if (needpd < 1001)
                    {
                        return 20000;
                    }
                    else if (needpd < 5001)
                    {
                        return 20001;
                    }
                    else
                    {
                        return 20002;
                    }
                }
                else if (ByData.nModule == 13)
                {
                    if (needpd < 5001)
                    {
                        return 20000;
                    }
                    else if (needpd < 20001)
                    {
                        return 20001;
                    }
                    else
                    {
                        return 20002;
                    }
                }
                else
                {
                    return 20000;
                }
            }
        }
        int JsonWingIsOrNull(int needpd)
        {
           return common4.PaoWinConfig[needpd].modelID;
        }
        public void UpdateWing(int tmpWingView) 
        {
            if (n_nameSeat == -1)
            {
                n_nameSeat = int.Parse(this.name);
            }
            if (tmpWingView==-1)
            {
                return;
            }
            if (objPaotai==null)
            {
                //说明这时player可能都没生成
                return;
            }
            if (tmpWingView==0)
            {
                tmpWingView = 80;
            }
            //---------80代表卸载此翅膀 则卸载-----------------
            if (tmpWingView == 80)
            {
                //
                if (objPao != null)
                {
                    if (objPao.trasWing != null)
                    {
                        objPao.trasWing.gameObject.SetActive(false);
                        Destroy(objPao.trasWing);
                    }
                }
                return;
            }
            //----------判断翅膀及父物体是否存在----------------
            if (objPao != null)
            {
                if (objPao.trasWing == null|| nWingView != tmpWingView||objPao.trasWing.activeSelf==false)
                {
                    if (objPao.trasWing != null)
                    {
                        objPao.trasWing.gameObject.SetActive(false);
                        DestroyImmediate(objPao.trasWing);
                    }
                    //这时需更新
                    nlocalWingPerfab = JsonWingIsOrNull(tmpWingView);   //获取本地ID
                    GameObject Go = common4.LoadPrefab("Wing/wing_" + nlocalWingPerfab);
                    var varGo = UnityEngine.Object.Instantiate(Go, this.objPaotai.root.parent);
                    varGo.transform.SetParent(objPao.root.parent);
                    varGo.transform.localPosition = new Vector3(0,0,100f);
                    varGo.transform.localEulerAngles = Vector3.zero;
                    varGo.transform.localScale = Vector3.one;
                    objPao.trasWing = varGo;
                    nWingView = tmpWingView;
                }
                else
                {
                   
                }
            }
            else
            {
                //炮台没有无法更新
                return;
            }
        }
        public void ChangePaoMult()
        {
            DsRecycleAll();
            paoDropdown.gameObject.SetActive(false);

            //if (common.IDRoomFish == BY_SESSION.龙晶场)
            //{
            //    foreach (var item in common.dicPaoConfigLj)
            //    {
            //        if (item.Key >= 200 && item.Key <= 100000)
            //        {
            //            InitText(item.Key.ToString());
            //        }
            //    }
            //}
            foreach (var item in common.dicPaoConfig)
            {
                if (item.Key >= common.dicPaoFwConfig[ByData.nModule].Minlevel && item.Key <= common.dicPaoFwConfig[ByData.nModule].Maxlevel)
                {
                    InitPaoBeiText(item.Key.ToString());
                }
            }
            ScrollTo(nPower);
        }
        //public void ChangePaoView(int paoView)
        //{
        //    //Debug.Log("ChangePaoView(int paoView)" + paoView);
        //    //nPaoView = paoView;
        //    UpdatePaotai(paoView);
        //}
        public void ChangeWingView(int wingView)  
        {
            //nWingView = wingView;
            //UpdateWing(wingView);
        }
        #region 使用的技能函数
        GameObject suodingEf;
        /// <summary>
        /// S锁定
        /// </summary>
        public void Skill_Juemingzhuiji(float time, long varPlayerId)
        {
            if (suodingEf == null)
            {
                suodingEf = Instantiate<GameObject>(common4.LoadPrefab("Effect/skill/lock_common"), objPaotai.transform);
            }
            suodingEf.transform.SetParent(this.objPaotai.transform);
            suodingEf.transform.localScale = Vector3.one;
            suodingEf.transform.localPosition = Vector3.zero;
            if (time>0)
            {
                //技能打开
                _bGenzong = true;
                SetGengZhong(_bGenzong);
                if (suodingEf != null)
                {
                    suodingEf.gameObject.SetActive(true);
                }
            }
            else
            {
                //技能关闭
                _bGenzong = false;
                SetGengZhong(_bGenzong);
                if (suodingEf != null)
                {
                    suodingEf.gameObject.SetActive(false);
                }
            }
        }
        GameObject baojiEf = null;
        /// <summary>
        /// 暴击
        /// </summary>
        public void Skill_Baoji(float time)
        {
            if (baojiEf == null)
            {
                baojiEf = Instantiate<GameObject>(common4.LoadPrefab("Effect/skill/lock_violent"), objPaotai.transform);
            }
            baojiEf.transform.SetParent(objPaotai.transform);
            baojiEf.transform.localScale = Vector3.one;
            baojiEf.transform.localPosition = new Vector3(0f, 0f, 0f);
            baojiEf.transform.SetParent(this.transform);
            baojiEf.transform.SetAsFirstSibling();
            //ListEffect.Add(go);
            if (time > 0)
            {
                //打开
                _bBaoJi = true;
                ChangeNKuangBaoMult();
                if (baojiEf != null)
                {
                    baojiEf.gameObject.SetActive(true);
                }
            }
            else
            {
                //关闭
                _bBaoJi = false;
                ChangeNKuangBaoMult();
                if (baojiEf != null)
                {
                    baojiEf.gameObject.SetActive(false);
                }
            }
        }
        /// <summary>
        /// 电磁炮
        /// </summary>
        /// <param name="time"></param>
        public void Skill_Diancipao(float time)
        {
            if (time>0)
            {
                //打开
                _bDiancipao = true;
                ChangeNKuangBaoMult();
                SetGengZhong(_bDiancipao);
            }
            else
            {
                //关闭
                _bDiancipao = false;
                ChangeNKuangBaoMult();
                SetGengZhong(_bDiancipao);
            }
        }
        /// <summary>
        /// 倍数设置
        /// </summary>
        void ChangeNKuangBaoMult() {

            if (_bBaoJi&& _bDiancipao)
            {
                NKuangBaoMult = 4;
                return;
            }
            if (_bBaoJi)
            {
                NKuangBaoMult = 2;
                return;
            }
            if (_bDiancipao)
            {
                NKuangBaoMult = 2;
                return;
            }
            NKuangBaoMult = 1;
        }
      
        /// <summary>
        /// 跟踪设置
        /// </summary>
        /// <param name="state"></param>
        void SetGengZhong(bool state)
        {
            if (state)
            {
                _bAllGenzong = true;
            }
            else
            {
                int m = 0;
                if (_bGenzong)
                {
                    m++;
                }
                //if (_bBaoJi)
                //{
                //    m++;
                //}
                if (_bDiancipao)
                {
                    m++;
                }
                if (m <= 0)
                {
                    _bAllGenzong = false;
                    //关闭清理
                    ClearAutoFish();
                }
            }
        }
        #endregion
    }
}