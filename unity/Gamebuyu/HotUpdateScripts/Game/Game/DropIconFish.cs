using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using System;
using CoreGame;

namespace Game.UI
{

    public class DropIconFish : MonoBehaviour
    {
        public ArtNum num_gold;
        public Image img_fish;
        public Text txt_fishname;
        private long dieMoney;
        private long playerid;
        void Awake() {
            num_gold = this.transform.Find("Effect_tanban222/effect_Num/rootNum/ArtNum").GetComponent<ArtNum>();
            img_fish = this.transform.Find("Effect_tanban222/effect_dikuang/effect_icon/Image_icon").GetComponent<Image>();
            txt_fishname = this.transform.Find("Effect_tanban222/effect_sidai/effect_zi/Text").GetComponent<Text>();
        }
        public void Show(Action callback = null)
        {
            try
            {
                this.transform.DOScale(0.8f, 0.5f).SetEase(Ease.OutBack);
                // this.transform.DOMove(UIByRoomMain.instance.objPlayer[pos].objPaotai.transform.position, 0.5f).SetDelay(3);
                this.transform.DOMove(common3._UIFishingInterface.GetOnePlayer(playerid).objPaotai.transform.position, 0.5f).SetDelay(3);
            }
            catch
            {
                if (common3._UIFishingInterface.GetDropIconFish() == null)
                {
                    DestroyImmediate(this.gameObject);
                }
                else
                {
                    common3._UIFishingInterface.GetDropIconFish().Recycle(this.gameObject);
                }
                return;
            }


            this.transform.DOScale(0, 0.5f).SetDelay(3f).OnComplete(() =>
            {
            //Destroy(this.gameObject);
            //UIByRoomMain.instance.itemPool_SkillDie.Recycle(this.gameObject);
            try
                {
                    if (common3._UIFishingInterface.GetDropIconFish() == null)
                    {
                        DestroyImmediate(this.gameObject);
                    }
                    else
                    {
                        common3._UIFishingInterface.GetDropIconFish().Recycle(this.gameObject);
                    }

                    if (callback != null)
                    {
                        callback();
                    }
                    this.gameObject.SetActive(false);
                }
                catch
                {
                    DestroyImmediate(this.gameObject);
                }

            });
        }

        public void Init(long _dieMoney, long _playerid, string name, bool isShowZhuan = false)
        {
            dieMoney = _dieMoney;
            txt_fishname.text = name;
            playerid = _playerid;

            if (dieMoney > 0)
            {
                num_gold.Init("fish-caijin", dieMoney, isShowAni: true);
            }

            this.transform.localScale = Vector3.zero;

            if (isShowZhuan == true)
            {
                if (_dieMoney > 20)
                {
                    StartCoroutine(enumerator(_dieMoney));
                }
                else
                {
                    num_gold.Init("fish-caijin", _dieMoney);
                    Show();
                }
            }
            else
            {
                Show();
            }

        }
        private void Update()
        {

        }
        IEnumerator enumerator(long _dieMoney)
        {
            this.transform.DOScale(0.8f, 0.5f).SetEase(Ease.OutBack);

            //随机一个数
            long nMin = (long)Mathf.Pow(10, _dieMoney.ToString().Length - 2);

            long varTime = _dieMoney - nMin;
            num_gold.Init("fish-caijin", nMin);
            varTime = varTime / 15;
            yield return new WaitForSeconds(0.5f);
            for (int i = 0; i < 3; i++)
            {

                //赋值
                for (int j = 0; j < 5; j++)
                {
                    nMin = nMin + varTime;
                    if (i == 2 && j == 4)
                    {
                        num_gold.Init("fish-caijin", _dieMoney);
                    }
                    else
                    {
                        num_gold.Init("fish-caijin", nMin);
                    }

                    yield return new WaitForSeconds(0.05f);
                }

                if (i != 2)
                {
                    num_gold.transform.DOScale(1.5f, 0.2f).OnComplete(() =>
                    {
                        num_gold.transform.DOScale(1f, 0.2f);  //正常              
                    });//变大
                    this.transform.DOScale(1.2f, 0.2f).OnComplete(() =>
                    {
                        this.transform.DOScale(1f, 0.2f);  //正常              
                    });//变大
                    yield return new WaitForSeconds(0.4f);
                }

            }
            this.transform.DOScale(1f, 1.4f);
            num_gold.transform.DOScale(1.5f, 0.2f).OnComplete(() =>
            {
                num_gold.transform.DOScale(1f, 1.2f);  //正常    
            });//变大
            yield return new WaitForSeconds(1.6f);//再等0.2秒
            try
            {
                if (common3._UIFishingInterface.GetDropIconFish() == null)
                {
                    DestroyImmediate(this.gameObject);
                }
                else
                {
                    common3._UIFishingInterface.GetDropIconFish().Recycle(this.gameObject);
                }

                this.gameObject.SetActive(false);
            }
            catch
            {
                DestroyImmediate(this.gameObject);
            }
        }
        private void OnEnable()
        {
            //  ShanGold(10000);
        }
        public void ShanGold(long _dieMoney)
        {
            if (_dieMoney > 20)
            {
                StartCoroutine(enumerator(_dieMoney));
            }
            else
            {
                num_gold.Init("fish-caijin", _dieMoney);
            }
        }
    }

}