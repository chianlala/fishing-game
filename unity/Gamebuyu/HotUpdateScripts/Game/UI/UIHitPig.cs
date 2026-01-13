using com.maple.game.osee.proto;
using com.maple.game.osee.proto.goldenpig;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

using DG.Tweening;
using Game.UI;
using CoreGame;

namespace Game.UI
{
    public class UIHitPig : MonoBehaviour
    {

        public Button btn_close;
       
        public Text txt_jiangquan;
    
        public Text txt_hjyulei;
        public Text txt_zuansi;
    
        public Animator animation;
        //public Image ShowImage;
   
        public Transform tars_Mask;

        public Text txt_times; 
        //public Text freeText;

        //  public ArtNum VipLevel;


        //public Sprite[] Cuizi;
        public Button[] btnHit=new Button[4];
        [Header("没有奖券")]
        //public GameObject prefabBoomNone;
        public GameObject prefabBoom;//龙珠爆炸
        public Transform TrasaudioSource;//音效 
        public AudioSource audioSource;//音效
        public DragonBones.UnityArmatureComponent armatureZhaDan; 

       // public Text[] txt_DanNum; 
        void FindCompent() {



            btn_close = this.transform.Find("btn_close").GetComponent<Button>();
            txt_jiangquan = this.transform.Find("title/bg_jiangquan/txt_jiangquan").GetComponent<Text>();
            txt_hjyulei = this.transform.Find("title/bg_hjyulei/txt_hjyulei").GetComponent<Text>();
            txt_zuansi = this.transform.Find("title/bg_zhuanshi/txt_zuansi").GetComponent<Text>();
            animation = this.transform.Find("MainNiu/HitMonster/monster").GetComponent<Animator>();


            tars_Mask = this.transform.Find("tars_Mask");
            txt_times = this.transform.Find("txt_times").GetComponent<Text>();
            TrasaudioSource = this.transform.Find("audioSource");
            audioSource = this.transform.Find("audioSource").GetComponent<AudioSource>();
            armatureZhaDan = this.transform.Find("armatureZhaDan").GetComponent<DragonBones.UnityArmatureComponent>();
            for (int i = 0; i < 4; i++)
            {
                btnHit[i]= this.transform.Find("right/"+i).GetComponent<Button>();
            }
        }
        void Awake()
        {
            FindCompent();
            btn_close.onClick.AddListener(CloseUI);
            tars_Mask.gameObject.SetActive(false);

            for (int i = 0; i < btnHit.Length; i++)
            {
                int n = i;
                btnHit[n].onClick.AddListener(() =>
                {
        
                    if (common.myItem[2] <= 0)
                    {
                        MessageBox.Show("核弹数量不足");
                        return;
                    }
                   
                    VarTmp = n;
                    NetMessage.OseeLobby.Req_GoldenPigBreakRequest(n);

                });
            }


            UEventDispatcher.Instance.AddEventListener(UEventName.GoldenPigBreakResponse, On_GoldenPigBreakResponse);//砸金猪响应
        }

        int VarTmp = 0;
        /// <summary>
        /// 砸金猪响应
        /// <summary>
        private void On_GoldenPigBreakResponse(UEventContext obj)
        {
            NetMessage.OseeFishing.Req_PlayerPropRequest();
            var pack = obj.GetData<GoldenPigBreakResponse>();
            rewards = pack.items;
            //NetMessage.OseeLobby.Req_GoldenPigHitLimitRequest();
            armatureZhaDan.gameObject.SetActive(true);
            armatureZhaDan.animation.Play("zhadan4", 1);
            //SoundHelper.PlayClip(SoundMgr.Instance.HitpigBoom);
            armatureZhaDan.transform.DOLocalMove(new Vector3(-200f, 0f, -250f), 0.5f).SetEase(Ease.InQuart).OnComplete(() =>
            {
                armatureZhaDan.gameObject.SetActive(false);
                //ve = new Vector3(-200f, 0f, -250f);
                if (VarTmp == 0)
                {
                    AnimalYuleiBoom();//没有奖券
                }
                else
                {
                    AnimalYuleiBoom();
                }
                TrasaudioSource.gameObject.SetActive(false);
                CancelInvoke("audioSourcePlay");

                // ShowImage.sprite = Cuizi[VarTmp];
                animation.Play("dead", 0, 0f);
                //SoundHelper.PlayClip(SoundMgr.Instance.MonsterDead);
                //SounLoadPlay.PlaySound("HitPig"); 
                animation.enabled = true;
                tars_Mask.gameObject.SetActive(true);
                CancelInvoke("CancelAnimal");
                Invoke("CancelAnimal", 1.1f);
                VarTmp = 0;
                ChangeHedanItem();
            });
        }
        ///// <summary>
        ///// 获取今日VIP可砸的次数上限响应
        ///// <summary>
        //private void On_GoldenPigHitLimitResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<GoldenPigHitLimitResponse>();
        //    TimesText.text = "今日可砸次数：（" + pack.restLimit + "/" + pack.totalLimit + "）";
        //}
        public void AnimalYuleiBoom()
        {
            GameObject go = Instantiate<GameObject>(prefabBoom, this.transform);
            go.transform.SetParent(this.transform);
            go.SetActive(true);
            go.transform.localPosition = new Vector3(-200f, 0f, -250f);
            //SoundHelper.PlayClip(Resources.Load<AudioClip>("BGM/Bomb"));
            SoundLoadPlay.PlaySound("boom");

        }
        //public void AnimalYuleiBoom2()
        //{
        //    GameObject go = Instantiate<GameObject>(prefabBoomNone, this.transform);
        //    go.transform.localPosition = new Vector3(-200f, 0f, -250f);
        //    //SoundHelper.PlayClip(Resources.Load<AudioClip>("BGM/Bomb"));
        //    SoundHelper.PlayBombClip();

        //}
        ///// <summary>
        ///// 获取今日砸金猪免费次数响应
        ///// <summary>
        //private void On_GoldenPigFreeTimesResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<GoldenPigFreeTimesResponse>();
        //   // PlayerData.FreeHitpig = pack.times;
        //  //  freeText.text = "免费：" + PlayerData.FreeHitpig + "次";
        //}

        void CancelAnimal()
        {
            //animation.speed = 0;
            //ShowImage.gameObject.SetActive(false);
            tars_Mask.gameObject.SetActive(false);
            UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
            Dictionary<int, long> Dictmp = new Dictionary<int, long>();
            //SoundHelper.PlayClip(SoundMgr.Instance.HitPigAward);

            foreach (var item in rewards)
            {
                Dictmp.Add(item.itemId, item.itemNum);
            }
            tmp.InitKuang(Dictmp, false, true, () => { RestartAni(); });

            //更改
            ChangeItem();
        }
        List<ItemDataProto> rewards = new List<ItemDataProto>();


        private void OnEnable()
        {
            EventManager.DiamondUpdate += On_ChangeDiamond;//.RegisterEvent(EventKey.ChangeDiamond, On_ChangeDiamond);

            RestartAni();
            ChangeItem();
            // ChangeItemNum();
            //NetMessage.OseeLobby.Req_GoldenPigHitLimitRequest();
            //VipLevel.Init("vip", PlayerData.vipLevel);
            // TimesText.text =  "今日可砸次数：（"+ PlayerData.vip.level + "/"+ PlayerData.vip.level + "）";
            //SoundMgr.Instance.SwitchHitPigMusic();

            EventManager.DiamondUpdate += On_ChangeDiamond;
            EventManager.PropUpdate += ChangeItem;

        }
        void On_ChangeDiamond(long v)
        {
            txt_zuansi.text = PlayerData.Diamond.ToString();
        }
        //重置动画到初始状态
        void RestartAni()
        {
            TrasaudioSource.gameObject.SetActive(true);
            //audioSource.Play();        
            InvokeRepeating("audioSourcePlay", 0.1f, 30f);
            animation.Play("walk");
            armatureZhaDan.gameObject.SetActive(false);
            armatureZhaDan.transform.localPosition = new Vector3(-200f, 530f, -250f);
            animation.Update(0);
            animation.enabled = true;
            //ShowImage.gameObject.SetActive(false);
            tars_Mask.gameObject.SetActive(false);
            // NetMessage.OseeLobby.Req_GoldenPigFreeTimesRequest();
            //freeText.text = "免费：" + PlayerData.FreeHitpig + "次";
        }
        void audioSourcePlay()
        {
            audioSource.volume = SoundHelper.GameVolume;
            audioSource.Play();
        }
        //改变参数
        public void ChangeItem()
        {
            txt_jiangquan.text = PlayerData.Jiangquan.ToString();
            //  txt_qtyulei.text = common.myItem[0].ToString();
            //   txt_byyulei.text = common.myItem[1].ToString();
            txt_hjyulei.text = common.myItem[2].ToString();
            txt_zuansi.text = PlayerData.Diamond.ToString();
        }
        public void ChangeHedanItem()
        {

            txt_hjyulei.text = common.myItem[2].ToString();
        }
        private void OnDisable()
        {
            System.GC.Collect();
            EventManager.DiamondUpdate -= On_ChangeDiamond;
            EventManager.PropUpdate -= ChangeItem;
        }


        private void CloseUI()
        {
            UIMgr.CloseUI(UIPath.UIHitPig);
            //UIMgr.ShowUI(UIPath.UIMainMenu);
        }
        private void OnDestroy()
        {
            //UEventDispatcher.Instance.RemoveEventListener(UEventName.GoldenPigHitLimitResponse, On_GoldenPigHitLimitResponse);//获取今日VIP可砸的次数上限响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GoldenPigBreakResponse, On_GoldenPigBreakResponse);//砸金猪响应
                                                                                                                        //UEventDispatcher.Instance.RemoveEventListener(UEventName.GoldenPigFreeTimesResponse, On_GoldenPigFreeTimesResponse);//获取今日砸金猪免费次数响应
        }
    }
}
