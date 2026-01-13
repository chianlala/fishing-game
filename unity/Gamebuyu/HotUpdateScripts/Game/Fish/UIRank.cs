using com.maple.game.osee.proto.fishing;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{

    public class UIRank : MonoBehaviour
    {
        public Button btn_close;
        //------------------------view_goldTorpedo--------------------------------- 
        public GameObject item_goldTorpedo;
        public Transform grid_goldTorpedo;
        GameObjectPool goldTorpedoPool = new GameObjectPool();
        Image[] ImgIndex=new Image[4]; 
        private void Awake()
        {
            ImgIndex[0] = transform.Find("Index/1").GetComponent<Image>();
            ImgIndex[1] = transform.Find("Index/2").GetComponent<Image>();
            ImgIndex[2] = transform.Find("Index/3").GetComponent<Image>();
            ImgIndex[3] = transform.Find("Index/4").GetComponent<Image>();
            btn_close = transform.Find("bg/btn_close").GetComponent<Button>();
            grid_goldTorpedo = this.transform.Find("bg/view_goldTorpedo/Viewport/Content");
            item_goldTorpedo = this.transform.Find("bg/item").gameObject;
            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIRank); });
            UEventDispatcher.Instance.AddEventListener(UEventName.GetRankingListResponse, On_GetRankingListResponse);//获取排行榜数据请求
            goldTorpedoPool.SetTemplete(item_goldTorpedo);
            goldTorpedoPool.Recycle(item_goldTorpedo);
        }
        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetRankingListResponse, On_GetRankingListResponse);//获取排行榜数据请求
        }
        /// <summary>
        /// 获取排行榜数据请求
        /// <summary>
        private void On_GetRankingListResponse(UEventContext obj)
        {

            var pack = obj.GetData<GetRankingListResponse>();
            if (pack.rankingType == 2)
            {

                for (int i = 0; i < grid_goldTorpedo.childCount; i++)
                {
                    var go = grid_goldTorpedo.GetChild(i);
                    goldTorpedoPool.Recycle(go.gameObject);
                }
                for (int i = 0; i < pack.rankingList.Count; i++)
                {
                    var data = pack.rankingList[i];
                    var go = goldTorpedoPool.Get();
                    go.SetActive(true);
                    go.transform.SetParent(grid_goldTorpedo, false);
           
                    SetItemGoldTorpedo(go, data, i + 1);
                }

            }
        }
        private void SetItemGoldTorpedo(GameObject go, RankingDataProto data, int nRank)
        {
            Image imgHead = go.transform.Find("img_head").GetComponent<Image>();
            Image imgRank = go.transform.Find("img_rank").GetComponent<Image>();
            Text txtRank = imgRank.transform.Find("txt_rank").GetComponent<Text>();
            //ArtNum numVip = go.transform.Find("bg_vip/num_vip").GetComponent<ArtNum>();
            Text txtName = go.transform.Find("txt_name").GetComponent<Text>();
            Text txtLevel = go.transform.Find("txt_level").GetComponent<Text>();
            Text txtGold = go.transform.Find("txt_gold").GetComponent<Text>();
            UIHelper.GetHeadImage(data.head, (sp) => { imgHead.sprite = sp; });
            if (nRank <= 3)//前3名不显示txt
            {
                txtRank.text = "";
                imgRank.sprite = ImgIndex[nRank-1].sprite;
            }
            else
            {
                txtRank.text = nRank.ToString();
                imgRank.sprite = ImgIndex[3].sprite;
            }
            //numVip.Init("vip", data.vipLevel, bSetNativeSize: true);
            txtName.text = data.nickname;
            txtLevel.text = "Lv" + data.level.ToString();
            txtGold.text = data.goldTorpedo.ToString();

            go.transform.SetSiblingIndex(nRank);
        }
        private void OnEnable()
        {
            //int nType = tog_gold.isOn ? 0 : 1;
            //NetMessage.OseeLobby.Req_GetRankingListRequest(nType);
            NetMessage.OseeLobby.Req_GetRankingListRequest(2);
        }
    }
}
