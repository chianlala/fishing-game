using com.maple.game.osee.proto.fishing;
using CoreGame;
using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{

    public class UICqzsTA : MonoBehaviour
    {
        private long AllGold = 1723000000;
        //分割特效播放次数  
        private long OnefenS = 1; 
        //分割金币数次数 
        private long TwofenS = 10;
         
        //累计number
        public long LeiJiNum = 0;

        //显示累计number
        public Text txt_ShowAllNum;

        public Transform thisparent;

        public Transform parentUp;

        public GameObject item;

        //GameObjectPool pool = new GameObjectPool();

        public GameObject Boomitem;
        public Transform parentGold;
        public GameObject golditem;
        GameObjectPool Goldpool = new GameObjectPool();
        public GameObject longJiitem;
        GameObjectPool LongJipool = new GameObjectPool();

        public Transform tra_goldShow;
        public Transform tra_ShowAllNum;

        public Dictionary<int, List<long>> ListDic = new Dictionary<int, List<long>>();
   
        public GameObject gameObject_Skill;

        public GameObject[] AllSanDian=new GameObject[9];
        public ChainLightning[] AllChainLightning = new ChainLightning[9];
        //public Animator _AniEffect; 

        public long PlayerID;
       
        public string fishName;
        //该鱼倍数
        public long Mult;
   
        void FindCompent() {
            item = this.transform.Find("item").gameObject;
            parentUp = this.transform.Find("parentUp");
            thisparent = this.transform.Find("parent");
      

            golditem = common4.LoadPrefab("BuyuPrefabs/GoldItem");
            longJiitem = common4.LoadPrefab("BuyuPrefabs/LongjinItem");

            parentGold = this.transform.Find("parentGold");

            gameObject_Skill = this.transform.Find("gameObject_Skill").gameObject;
            tra_goldShow = this.transform.Find("goldShow");
            txt_ShowAllNum = this.transform.Find("goldShow/Text").GetComponent<Text>();
            tra_ShowAllNum = this.transform.Find("goldShow/Text");
            for (int i = 0; i < AllSanDian.Length; i++)
            {
                AllSanDian[i] = transform.Find("gameObject_Skill/"+i).gameObject;
                AllChainLightning[i] = AllSanDian[i].GetComponent<ChainLightning>();
            }
        }

        private void Awake()
        {
            FindCompent();
            
            Goldpool.SetTemplete(golditem);
            LongJipool.SetTemplete(longJiitem);
        }
        public void Init(Vector3 _fishPos, List<FishingChallengeFightFishRefFishItem> refFishList)
        {
            //测试雷电 
            for (int i = 0; i < refFishList.Count; i++)
            {
           
                //AllChainLightning[i].EndPostion.localPosition = common.WordToUI(common.listFish[varListFish[i]].transform.position);
                try
                {
                    AllChainLightning[i].StartPosition.localPosition = _fishPos - new Vector3(640f, 360f);
                    AllChainLightning[i].EndPostion.position = common.listFish[refFishList[i].fishId].transform.position;
                    AllSanDian[i].gameObject.SetActive(true);
                }
                catch
                {
                  
                }
            }
            StartCoroutine(overTwoAttack());
        }
        IEnumerator overTwoAttack()
        {
            yield return new WaitForSeconds(1f);
            this.gameObject.gameObject.SetActive(false);
            Destroy(this.gameObject);
        }
    }
}
