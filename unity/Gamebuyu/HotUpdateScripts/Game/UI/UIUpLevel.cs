using com.maple.game.osee.proto.fishing;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIUpLevel : MonoBehaviour
    {
        public Text num_Level;
        GameObjectPool AwardPool;
        public GameObject Awarditem; 
        public Transform goupconet;
        public Button btnGet;
        private float fTime = 2;//等待动画时间完成后才能关闭
        private void Awake()
        {
            Awarditem = this.transform.Find("bar/Awarditem").gameObject;
            num_Level = this.transform.Find("top/fntLevel").GetComponent<Text>();
            goupconet= this.transform.Find("bar/group");
            btnGet = this.transform.Find("btnGet").GetComponent<Button>();
            AwardPool = new GameObjectPool();
            AwardPool.SetTemplete(Awarditem);
        }
        public void Start()
        {
            UIEventListener.Get(this.gameObject).onClick = ((a, b) =>
            {
                if (fTime <= 0)
                    UIMgr.CloseUI(UIPath.UIUpLevel);
            });
            btnGet.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIUpLevel);
            });
        }
        public void Init(FishingLevelUpResponse pack)
        {
            num_Level.text = pack.level.ToString();
            foreach (Transform item in goupconet)
            {
                AwardPool.Recycle(item.gameObject);
            }
            for (int i = 0; i < pack.rewards.Count; i++)
            {
                GameObject tmp = AwardPool.Get(goupconet);
                tmp.transform.Find("icon").GetComponent<Image>().sprite = common4.LoadSprite("item/" + pack.rewards[i].itemId);
                tmp.transform.Find("icon").GetComponent<Image>().SetNativeSize();
                tmp.transform.Find("count").GetComponent<Text>().text = "x" + pack.rewards[i].itemNum;
            }

        }
        void OnEnable()
        {
            fTime = 2;
        }

        void Update()
        {
            if (fTime > -1)
                fTime -= Time.deltaTime;
            else
            {
                UIMgr.CloseUI(UIPath.UIUpLevel);
            }
        }

    }
}