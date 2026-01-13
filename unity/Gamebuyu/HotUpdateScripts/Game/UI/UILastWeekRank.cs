using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using CoreGame;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

namespace Game.UI
{

    public class UILastWeekRank : MonoBehaviour
    {
        public Button btn_ok;
        public Transform[] transfrom_rank;
        void Awake()
        {
            btn_ok.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UILastWeekRank);
            });
        }
        public void ShowPanel(List<FirstWeekLoginMessage> RankList)
        {
            for (int i = 0; i < RankList.Count; i++)
            {
                int m = i;
                transfrom_rank[i].gameObject.SetActive(true);
                transfrom_rank[i].transform.Find("name").GetComponent<Text>().text = RankList[i].name;
                //头像
                if (RankList[i].headUrl == "")
                {
                    UIHelper.GetHeadImage(RankList[i].headIndex.ToString(), (sp) =>
                    {
                        transfrom_rank[m].transform.Find("mask/head").GetComponent<Image>().sprite = sp;
                        transfrom_rank[m].transform.Find("mask").gameObject.SetActive(true);
                    });
                }
                else
                {
                    UIHelper.GetHeadImage(RankList[i].headUrl, (sp) =>
                    {
                        transfrom_rank[m].transform.Find("mask/head").GetComponent<Image>().sprite = sp;
                        transfrom_rank[m].transform.Find("mask").gameObject.SetActive(true);
                    });
                }
                transfrom_rank[m].transform.Find("txt_weekScore").GetComponent<Text>().text = RankList[i].weekPoint.ToString();
                //奖励
                if (RankList[i].itemId > 0 && RankList[i].itemNum > 0)
                {
                    int varitemId = RankList[i].itemId;
                    int varitemNum = RankList[i].itemNum;
                    transfrom_rank[m].transform.Find("Award").gameObject.SetActive(true);
                    Image mImage = transfrom_rank[m].transform.Find("Award").GetComponent<Image>();
                    if (varitemId == 3)
                    {
                        mImage.sprite = common4.LoadSprite("item/jiangquan2");
                    }
                    else
                    {
                        mImage.sprite = common4.LoadSprite("item/" + varitemId);
                    }
                    // mImage.SetNativeSize();
                    transfrom_rank[m].transform.Find("Award/Num").GetComponent<Text>().text = "x" + varitemNum.ToString();
                    transfrom_rank[m].transform.Find("txt_weijinru").gameObject.SetActive(false);
                }
                else
                {
                    transfrom_rank[m].transform.Find("txt_weijinru").gameObject.SetActive(true);
                    transfrom_rank[m].transform.Find("Award").gameObject.SetActive(false);
                }
            }
            for (int i = RankList.Count; i < 3; i++)
            {
                transfrom_rank[i].gameObject.SetActive(false);
            }
        }
    }
}