using CoreGame;
using DG.Tweening;
using GameFramework;
using LitJson;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIChangePaoTai : MonoBehaviour
    {
        public Toggle tog_wing;
        public Toggle tog_cannon;
        public Transform trans_wing;
        public Transform trans_cannon; 
        public Button btn_close;
        //炮台id 对应的是服务器的道具ID
        public int nowSelect=70; 
        public Toggle[] tog_selectPaos = new Toggle[9]; 

        public Button btnEquip;
        public Transform transAlready; 
        public Button btnGet;
        public Transform wingParent;
        public Transform cannonParent;
        public Transform teshuxiaoguo;
        public Transform teshuxiaoguoNull; 
        public GameObject tmpGameObject;
        public Animator ani_pao;
        public paotai now_paotai; 
        public Text pao_name;
        public Dictionary<string,GameObject> dicTionary=new Dictionary<string, GameObject>();
        //翅膀
        public GameObject tmpWingGameObject; 
        public Toggle[] tog_selectWings = new Toggle[5];
        public int nowSelectWing; 
       
        public Transform transWingAlready;
        public Button btn_wingzbxz;
        public Text wing_name;
        public Text txt_wingzbxz;

        public Transform pop_get_material;
        //public Image icon;
        //public Text desc;
        public Button btn_getpaotai;
        private Button btnxiangqinClose;
        private void Awake()
        {
            //btnxiangqinClose = this.transform.Find("pop_get_material/anchor/bg/btnClose").GetComponent<Button>();
            btn_close = this.transform.Find("main/btnClose").GetComponent<Button>();
            pao_name = this.transform.Find("cannon/detail/title/name").GetComponent<Text>();
            //desc = this.transform.Find("pop_get_material/anchor/list/Viewport/Content/item/desc").GetComponent<Text>();
            //icon = this.transform.Find("pop_get_material/anchor/list/Viewport/Content/item/frame/icon").GetComponent<Image>();
            //btn_getpaotai = this.transform.Find("pop_get_material/anchor/list/Viewport/Content/item/btnGet").GetComponent<Button>();
            tog_selectPaos[0] = this.transform.Find("cannon/list/Viewport/Content/70").GetComponent<Toggle>();
            tog_selectPaos[1] = this.transform.Find("cannon/list/Viewport/Content/71").GetComponent<Toggle>();
            tog_selectPaos[2] = this.transform.Find("cannon/list/Viewport/Content/72").GetComponent<Toggle>();
            tog_selectPaos[3] = this.transform.Find("cannon/list/Viewport/Content/73").GetComponent<Toggle>();
            tog_selectPaos[4] = this.transform.Find("cannon/list/Viewport/Content/74").GetComponent<Toggle>();
            tog_selectPaos[5] = this.transform.Find("cannon/list/Viewport/Content/75").GetComponent<Toggle>();
            tog_selectPaos[6] = this.transform.Find("cannon/list/Viewport/Content/76").GetComponent<Toggle>();
            tog_selectPaos[7] = this.transform.Find("cannon/list/Viewport/Content/77").GetComponent<Toggle>();
            tog_selectPaos[8] = this.transform.Find("cannon/list/Viewport/Content/78").GetComponent<Toggle>();

            tog_selectWings[0] = this.transform.Find("wing/list/Viewport/Content/81").GetComponent<Toggle>();
            tog_selectWings[1] = this.transform.Find("wing/list/Viewport/Content/82").GetComponent<Toggle>();
            tog_selectWings[2] = this.transform.Find("wing/list/Viewport/Content/83").GetComponent<Toggle>();
            tog_selectWings[3] = this.transform.Find("wing/list/Viewport/Content/84").GetComponent<Toggle>();
            tog_selectWings[4] = this.transform.Find("wing/list/Viewport/Content/85").GetComponent<Toggle>();
            tog_wing = this.transform.Find("main/tabs/wing").GetComponent<Toggle>();
            tog_cannon = this.transform.Find("main/tabs/cannon").GetComponent<Toggle>();
            trans_wing = this.transform.Find("wing");
            trans_cannon = this.transform.Find("cannon");
            teshuxiaoguo = this.transform.Find("cannon/detail/effect/list/Viewport/Content/item");
            teshuxiaoguoNull= this.transform.Find("cannon/detail/effect/null");
            btnEquip = this.transform.Find("cannon/root/btnEquip").GetComponent<Button>();
            btnGet = this.transform.Find("cannon/root/btnGet").GetComponent<Button>();
            transAlready = this.transform.Find("cannon/root/transAlready");
            transWingAlready = this.transform.Find("wing/root/transAlready");
            wing_name = this.transform.Find("wing/detail/title/name").GetComponent<Text>();
  
            cannonParent = this.transform.Find("show/root/cannonParent");
            wingParent = this.transform.Find("show/root/wingParent");
            btn_wingzbxz = this.transform.Find("wing/btn_wingzbxz").GetComponent<Button>();
            pop_get_material = this.transform.Find("pop_get_material");
            txt_wingzbxz = this.transform.Find("wing/btn_wingzbxz/Text").GetComponent<Text>();
     
            tog_cannon.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    trans_wing.gameObject.SetActive(false);
                    trans_cannon.gameObject.SetActive(true);
                    wingParent.gameObject.SetActive(false);
                }
            });
            tog_wing.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    trans_wing.gameObject.SetActive(true);
                    trans_cannon.gameObject.SetActive(false);
                    wingParent.gameObject.SetActive(true);
                }
            });
            //btnxiangqinClose.onClick.AddListener(() => {
            //    pop_get_material.gameObject.SetActive(false);
            //});
            btn_close.onClick.AddListener(() => {
                Debug.Log("关闭btn_close"); 
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
                UIMgr.CloseUI(UIPath.UIChangePaoTai);
            });
            //btn_getpaotai.onClick.AddListener(() => {
            //    //获取此炮台
            //    var varm = UIMgr.ShowUISynchronize(UIPath.UIShop);
            //    varm.GetComponent<UIShop>().Setpanel(JieMain.道具商城);
            //    pop_get_material.gameObject.SetActive(false);
            //});
            btnGet.onClick.AddListener(() =>
            {
                if (nowSelect == 71 || nowSelect == 72 )
                {
                    pop_get_material.gameObject.SetActive(true);
                    for (int i = 0; i <= 8; i++)
                    {
                        if (tog_selectPaos[i].name == nowSelect.ToString())
                        {
                            //desc.text = common5.JsonCanShu["PaoName"][nowSelect][2].ToString();
                            //desc.text = common4.PaoWinConfig[nowSelect].info.ToString();
                            //icon.sprite = tog_selectPaos[i].transform.Find("Image").GetComponent<Image>().sprite;
                            btn_getpaotai.gameObject.SetActive(true);
                            return;
                        }
                    }
                }
                //if (nowSelect==76|| nowSelect == 77 || nowSelect == 78)
                //{
                //    pop_get_material.gameObject.SetActive(true);
                //    for (int i = 0; i <= 8; i++)
                //    {
                //        if (tog_selectPaos[i].name== nowSelect.ToString())
                //        {
                //            // desc.text = common5.JsonCanShu["PaoName"][nowSelect][2].ToString();
                //            desc.text = common4.PaoWinConfig[nowSelect].info;
                //            icon.sprite = tog_selectPaos[i].transform.Find("Image").GetComponent<Image>().sprite;
                //            btn_getpaotai.gameObject.SetActive(false);
                //            return;
                //        } 
                //    }
                //}
                Debug.Log("nowSelect"+nowSelect);
                //获取此炮台
                var varm = UIMgr.ShowUISynchronize(UIPath.UIShop);
                varm.GetComponent<UIShop>().Setpanel(JieMain.道具商城);
            });
            btnEquip.onClick.AddListener(() =>
            {
                //请求装备此炮台
                //切换炮台
                //if (common.IDRoomFish == BY_SESSION.普通场)
                //{
                    Debug.Log("请求"+ nowSelect);
                    NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(nowSelect);
                    NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
                //}
                //else if (common.IDRoomFish == BY_SESSION.龙晶场)
                //{
                //    NetMessage.Chanllenge.Req_FishingChallengeChangeBatteryViewRequest(nowSelect);
                //    NetMessage.Lobby.Req_PlayerStatusRequest(PlayerData.PlayerId);
                //}
                SoundLoadPlay.PlaySound("paochange");
            });
            btn_wingzbxz.onClick.AddListener(() =>
            { 
                if (txt_wingzbxz.text=="装备")
                {
                    //请求装备此翅膀
                    NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(nowSelectWing);
                }
                else if (txt_wingzbxz.text == "卸载")
                {    
                    //80卸载翅膀
                    NetMessage.OseeFishing.Req_FishingChangeBatteryViewRequest(80);                
                }
                else if (txt_wingzbxz.text == "获得")
                {
                    //请求购买此翅膀
                    var varm = UIMgr.ShowUISynchronize(UIPath.UIShop);
                    varm.GetComponent<UIShop>().Setpanel(JieMain.道具商城);
                    //if (nowSelectWing==81)
                    //{
                    //    for (int i = 0; i < 5; i++)
                    //    {
                    //        if (tog_selectWings[i].name == nowSelectWing.ToString())
                    //        {
                    //           // desc.text = common5.JsonCanShu["WingName"][nowSelectWing][2].ToString();
                    //            desc.text = common4.PaoWinConfig[nowSelectWing].info.ToString();
                    //            icon.sprite = tog_selectWings[i].transform.Find("Image").GetComponent<Image>().sprite;
                    //            btn_getpaotai.gameObject.SetActive(true);
                    //            pop_get_material.gameObject.SetActive(true);
                    //            return;
                    //        }
                    //    }
                    //}
                    //else
                    //{
                    //    //请求购买此翅膀
                    //    var varm = UIMgr.ShowUISynchronize(UIPath.UIShop);
                    //    varm.GetComponent<UIShop>().Setpanel(JieMain.道具商城);
                    //}

                }
            });
            for (int i = 0; i <= 8; i++)
            {
                int n = i;
                tog_selectPaos[n].onValueChanged.AddListener((arg) =>
                {
                    if (arg)
                    {
                        nowSelect =int.Parse(tog_selectPaos[n].name);
                        ChangePaoShow(int.Parse(tog_selectPaos[n].name));
                    }
                });
            }
            for (int i = 0; i < tog_selectWings.Length; i++)
            {
                int n = i;
                tog_selectWings[n].onValueChanged.AddListener((arg) =>
                {
                    if (arg)
                    {
                        nowSelectWing =int.Parse(tog_selectWings[n].name);
                        ChangeWingShow(nowSelectWing);
                    }
                }); 
            }
        }
     
        //更改炮台显示
        void ChangePaoShow(int paofwqID) {
            if (paofwqID==0)
            {
                paofwqID = 70;
            }
            if (common4.PaoWinConfig.ContainsKey(paofwqID) == false)
            {
                Debug.LogError("PaoName不存在" + paofwqID);
                return;
            }
     
            pao_name.text = common4.PaoWinConfig[paofwqID].name;

            if (paofwqID == PlayerData.PaoViewIndex)
            {
                //代表已经装备
                transAlready.gameObject.SetActive(true);
                btnEquip.gameObject.SetActive(false);
                btnGet.gameObject.SetActive(false);
            }
            else
            {
                transAlready.gameObject.SetActive(false);
                if (nowSelect == 70)
                {
                    //基础炮台
                    btnEquip.gameObject.SetActive(true);
                    btnGet.gameObject.SetActive(false);
                }
                else if (paofwqID == 71 || paofwqID == 72 || paofwqID == 73 || paofwqID == 74)
                {
                    //免费赠送的炮台
                    btnEquip.gameObject.SetActive(true);
                    btnGet.gameObject.SetActive(false);
                }
                else
                {
                    if (common4.PaoWinTime.ContainsKey(paofwqID))
                    {
                        long dqtime = common4.PaoWinTime[paofwqID];
                        if (dqtime > 0)
                        {
                            //永久或时间大于0 则已拥有
                            btnEquip.gameObject.SetActive(true);
                            btnGet.gameObject.SetActive(false);
                        }
                        else
                        {
                            btnEquip.gameObject.SetActive(false);
                            btnGet.gameObject.SetActive(true);
                        }

                    }
                    else
                    {
                        btnEquip.gameObject.SetActive(false);
                        btnGet.gameObject.SetActive(true);
                    }
                }
            }

            //加载炮台
            if (tmpGameObject != null)
            {
                DestroyImmediate(tmpGameObject);
            }
  
            string mpath = common4.PaoWinConfig[paofwqID].modelID.ToString();

            GameObject Go = common4.LoadPrefab("Paotai/pao_" + mpath);
            tmpGameObject = UnityEngine.Object.Instantiate(Go, cannonParent);
            //Canvas m = tmpGameObject.transform.Find("pao/root").gameObject.AddComponent<Canvas>();
            //m.overrideSorting = true;
            //m.sortingOrder = 25;

            ////激光
            //var mm = tmpGameObject.transform.Find("pao/violent");
            //if (mm != null)
            //{
            //    mm.gameObject.layer = 5;
            //    var vgrandFa = mm.GetComponentsInChildren<Transform>(false);
            //    foreach (Transform childvargo in vgrandFa)
            //    {
            //        childvargo.gameObject.layer = 5;
            //    }

            //    mm.gameObject.layer = 5;
            //    var vgrandFax = mm.GetComponentsInChildren<Renderer>(false);
            //    foreach (Renderer childvargo in vgrandFax)
            //    {
            //        childvargo.sortingLayerID = 5;
            //        //childvargo.sortingOrder = childvargo.sortingOrder + 5;
            //    }
            //}
            ChangeLockbulletWing();
        }
        int beforeInit = 81;

        //更新翅膀
        void ChangeWingShow(int wingfwqID) 
        {
            if (wingfwqID==0)//为0则为80
            {
                wingfwqID = 80;
            }
            if (wingfwqID==80)//代表不装翅膀
            {
                if (tmpWingGameObject != null)
                {
                    DestroyImmediate(tmpWingGameObject);
                } 
                transWingAlready.gameObject.SetActive(false);
                ChangeWingShow(beforeInit);
                return;
            }
            //本地ID
            string localPrefabID = common4.PaoWinConfig[wingfwqID].modelID.ToString();
            wing_name.text = common4.PaoWinConfig[wingfwqID].name;
            if (common4.PaoWinTime.ContainsKey(wingfwqID))
            {
                //time
                long dqtime = common4.PaoWinTime[wingfwqID];
                //时间大于0则可以使用
                if (dqtime > 0) 
                {
                    if (wingfwqID == PlayerData.WingIndex)
                    {
                        transWingAlready.gameObject.SetActive(true);
                        beforeInit = wingfwqID;
                        txt_wingzbxz.text = "卸载";
                    }
                    else
                    {
                        transWingAlready.gameObject.SetActive(false);
                        txt_wingzbxz.text = "装备";
                    }
                }
                else
                {
                    transWingAlready.gameObject.SetActive(false);
                    txt_wingzbxz.text = "获得";
                }
            }
            //加载prefab显示
            if (tmpWingGameObject != null)
            {
                DestroyImmediate(tmpWingGameObject);
            }
            GameObject Go = common4.LoadPrefab("Wing/wing_" + localPrefabID);
            //生成实例并更改层级
            tmpWingGameObject = UnityEngine.Object.Instantiate(Go, wingParent);
            tmpWingGameObject.transform.SetParent(wingParent);
            //tmpWingGameObject.layer = 5;
            //var vgrandFa = tmpWingGameObject.GetComponentsInChildren<Transform>();
            //foreach (Transform childvargo in vgrandFa)
            //{
            //    childvargo.gameObject.layer = 5;
            //}
            //tmpWingGameObject.layer = 5;

            //ParticleSystem[] vgParticleS = tmpWingGameObject.GetComponentsInChildren<ParticleSystem>();
            //foreach (ParticleSystem childvargo in vgParticleS) 
            //{ 
            //    childvargo.R = 5; 
            //}

            ////更改Sorting层级
            //Canvas m = tmpWingGameObject.AddComponent<Canvas>();
            //m.overrideSorting = true;
            //m.sortingOrder = 10;
            //tmpWingGameObject.layer = 5;
            //var vgrandFa = tmpWingGameObject.GetComponentsInChildren<Transform>();
            //foreach (Transform childvargo in vgrandFa)
            //{
            //    childvargo.gameObject.layer = 5;
            //}
            //tmpWingGameObject.layer = 5;
            ////更改Sorting层级
            //Canvas m = tmpWingGameObject.AddComponent<Canvas>();
            //m.overrideSorting = true;
            //m.sortingOrder = 10;
            ChangeLockbulletWing();



            //if (common5.PropCanShu.Keys.Contains(wingfwqID.ToString()))//已经解锁
            //{
            //    int dqtime = int.Parse(common5.PropCanShu[wingfwqID].ToString());
            //    Debug.Log("dqtime" + dqtime);
            //    Debug.Log("dqtime" + common5.PropCanShu[wingfwqID].ToString());
            //    if (dqtime > 0)
            //    {
            //        if (wingfwqID == PlayerData.WingIndex)
            //        {
            //            transWingAlready.gameObject.SetActive(true);
            //            beforeInit = wingfwqID;
            //            txt_wingzbxz.text = "卸载";
            //        }
            //        else
            //        {
            //            transWingAlready.gameObject.SetActive(false);
            //            txt_wingzbxz.text = "装备";
            //        }
            //    }
            //    else
            //    {
            //        transWingAlready.gameObject.SetActive(false);
            //        txt_wingzbxz.text = "获得";
            //    }
            //}
            //else
            //{
            //    transWingAlready.gameObject.SetActive(false);
            //    txt_wingzbxz.text = "获得";
            //}


        }
        //更改显示锁
        void ChangeLockbulletWing() {
            //更改炮台
            for (int i = 0; i < tog_selectPaos.Length; i++)
            {
                //判断是否装备
                if (PlayerData.PaoViewIndex.ToString()== tog_selectPaos[i].name)
                {
                    //代表已经装备
                    tog_selectPaos[i].transform.Find("check").gameObject.SetActive(true);
                }
                else
                {
                    tog_selectPaos[i].transform.Find("check").gameObject.SetActive(false);
                }

                //判断是否解锁
                if (tog_selectPaos[i].name=="70")
                {
                    //70是基础炮台
                    tog_selectPaos[i].transform.Find("lock").gameObject.SetActive(false);
                    tog_selectPaos[i].transform.Find("txtsysj").GetComponent<Text>().text = "";
                }
                else
                {
                    if (tog_selectPaos[i].name == "71" || tog_selectPaos[i].name == "72" || tog_selectPaos[i].name == "73" || tog_selectPaos[i].name == "74")
                    {
                        tog_selectPaos[i].transform.Find("lock").gameObject.SetActive(false);
                        tog_selectPaos[i].transform.Find("txtsysj").GetComponent<Text>().text = "永久";          
                    }
                    else
                    {
                        if (common4.PaoWinTime.ContainsKey(int.Parse(tog_selectPaos[i].name)))
                        {
                            long dqtime =common4.PaoWinTime[int.Parse(tog_selectPaos[i].name)];
                            if (dqtime > 0)
                            {
                                //存在并且时间大于0 代表解锁了
                                tog_selectPaos[i].transform.Find("lock").gameObject.SetActive(false);
                                long mdays = dqtime / 86400 + 1;
                                tog_selectPaos[i].transform.Find("txtsysj").GetComponent<Text>().text = mdays + "天";
                            }                   
                            else
                            {
                                //小于0 代表到期了
                                tog_selectPaos[i].transform.Find("lock").gameObject.SetActive(true);
                                tog_selectPaos[i].transform.Find("txtsysj").GetComponent<Text>().text = "";
                            }
                        }
                        else
                        {
                            tog_selectPaos[i].transform.Find("lock").gameObject.SetActive(true);
                            tog_selectPaos[i].transform.Find("txtsysj").GetComponent<Text>().text = "";
                        }
                    }
                  
                }
            }
            //更改翅膀
            for (int i = 0; i < tog_selectWings.Length; i++)
            {
                if (PlayerData.WingIndex.ToString() == tog_selectWings[i].name)
                {
                    //相等代表已经装备                                     
                    tog_selectWings[i].transform.Find("check").gameObject.SetActive(true);
                }
                else
                {
                    tog_selectWings[i].transform.Find("check").gameObject.SetActive(false);
                }


                if (common4.PaoWinTime.ContainsKey(int.Parse(tog_selectWings[i].name)))
                {
                    long dqtime = common4.PaoWinTime[int.Parse(tog_selectWings[i].name)];
                    if (dqtime > 0)
                    {
                        //存在并且时间大于0 代表解锁了
                        tog_selectWings[i].transform.Find("lock").gameObject.SetActive(false);
                        long mdays = dqtime / 86400 + 1;
                        tog_selectWings[i].transform.Find("txtsysj").GetComponent<Text>().text = mdays + "天";
                    }              
                    else
                    {
                        tog_selectWings[i].transform.Find("lock").gameObject.SetActive(true);
                        tog_selectWings[i].transform.Find("txtsysj").GetComponent<Text>().text = "";
                    }

                }
                else
                {
                    tog_selectWings[i].transform.Find("lock").gameObject.SetActive(true);
                    tog_selectWings[i].transform.Find("txtsysj").GetComponent<Text>().text = "";
                }
            }
        }
        //开火
        int firetimes = 0;
        void PlayFireOne() 
        {
            if (tmpGameObject!=null)
            {
                if (now_paotai==null)
                {
                    now_paotai = tmpGameObject.GetComponent<paotai>();
                }
                firetimes++;
                if (firetimes==2)
                {
                    firetimes = 0;
                }
                if (firetimes==0)
                {
                    Debug.Log("nowSelect" + nowSelect);
                
                    //发射子弹
                    string mpath = common4.PaoWinConfig[nowSelect].modelID.ToString();
                    foreach (var item in common4.PaoWinConfig)
                    {
                        Debug.Log("item.Key" + item.Key);
                        Debug.Log("item.Value" + item.Value);
                    }
                    Debug.Log("Pao_mpath" + common4.PaoWinConfig.Count);

                    string str = "bullet_" + mpath;
                    if (dicTionary.ContainsKey(str)&& dicTionary[str]!=null)
                    {
                        GameObject vargo= dicTionary[str];
                        vargo.transform.position = now_paotai.pos_fire[0].position;
                        now_paotai.Fire();
                        //移动子弹
                        DoMoveBullet(vargo, mpath);
                    }
                    else
                    {
                        if (dicTionary.ContainsKey(str) && dicTionary[str] == null)
                        {
                            dicTionary.Remove(str);
                        }
                        //生成实例并设置层级 使其为UI层级
                        GameObject mgo = common4.LoadPrefab("BulletShow/" + str);
                        GameObject vargo = Instantiate(mgo, cannonParent);
                        vargo.transform.SetParent(cannonParent);
                        //vargo.layer = 5;
                        //var vgrandFa = vargo.GetComponentsInChildren<Transform>();
                        //foreach (Transform childvargo in vgrandFa)
                        //{
                        //    childvargo.gameObject.layer = 5;
                        //}
                        //vargo.layer = 5;
                        vargo.name = "vargo1000";
                        vargo.gameObject.SetActive(true);
                        vargo.transform.position = now_paotai.pos_fire[0].position;
                        now_paotai.Fire();
                        //加入字典
                        dicTionary.Add(str, vargo);
                        //移动子弹
                        DoMoveBullet(vargo, mpath);
                    }
                }
                else
                {
                    if (now_paotai.violent!=null)
                    {
                        now_paotai.Fire();
                        now_paotai.violentFire(0.5f);
                    }
                    else
                    {
                        //发射子弹
                        //string mpath = common5.JsonCanShu["PaoName"][nowSelect][0].ToString(); 
                        string mpath = common4.PaoWinConfig[nowSelect].modelID.ToString();
                        string str = "bullet_" + mpath;
                        if (dicTionary.ContainsKey(str) && dicTionary[str] != null)
                        {
                            GameObject vargo = dicTionary[str];
                            vargo.transform.position = now_paotai.pos_fire[0].position;
                            now_paotai.Fire();
                            //移动子弹
                            DoMoveBullet(vargo, mpath);
                        }
                        else
                        {
                            if (dicTionary.ContainsKey(str) && dicTionary[str] == null)
                            {
                                dicTionary.Remove(str);
                            }
                            //生成实例并设置层级 使其为UI层级
                            GameObject mgo = common4.LoadPrefab("BulletShow/" + str);
                            GameObject vargo = Instantiate(mgo, cannonParent);
                            vargo.transform.SetParent(cannonParent);
                            //vargo.layer = 5;
                            //var vgrandFa = vargo.GetComponentsInChildren<Transform>();
                            //foreach (Transform childvargo in vgrandFa)
                            //{
                            //    childvargo.gameObject.layer = 5;
                            //}
                            //vargo.layer = 5;
                            vargo.gameObject.SetActive(true);
                            vargo.transform.position = now_paotai.pos_fire[0].position;
                            now_paotai.Fire();
                            //加入字典
                            dicTionary.Add(str, vargo);
                            //移动子弹
                            DoMoveBullet(vargo, mpath);
                        }
                    }
                }
            }
        }
        float tTime = 0f;
        void Update() {
            tTime = tTime+Time.deltaTime;
            if (tTime>3f)
            {
                tTime = 0f;
                PlayFireOne();
            }
        }
        void DoMoveBullet(GameObject tmpgo,string mpath) {
            tmpgo.SetActive(true);
            tmpgo.transform.DOLocalMove(new Vector3(0f, 200f, 0f), 0.2f).OnComplete(() => {
                var netstr = "net_"+ mpath;
                tmpgo.gameObject.SetActive(false);
                if (dicTionary.ContainsKey(netstr) && dicTionary[netstr] != null)
                {
                    GameObject netgo = dicTionary[netstr];
                    netgo.transform.position = tmpgo.transform.position;
                    netgo.SetActive(true);
                    //隐藏
                    DOVirtual.DelayedCall(0.5f, () => {
                        netgo.gameObject.SetActive(false);
                    });
                }
                else
                {
                    if (dicTionary.ContainsKey(netstr) && dicTionary[netstr] == null)
                    {
                        dicTionary.Remove(netstr);
                    }
                    //生成实例并设置层级
                    GameObject ccgo = common4.LoadPrefab("Wang/net_" + mpath);// commonLoad.GetOneWang(m, cannonParent);
                    GameObject netgo = Instantiate(ccgo, cannonParent);
                    netgo.transform.SetParent(cannonParent);
                    netgo.transform.position = tmpgo.transform.position;
                    //netgo.layer = 5;
                    //var grandFa = netgo.GetComponentsInChildren<Transform>();
                    //foreach (Transform child in grandFa)
                    //{
                    //    child.gameObject.layer = 5;
                    //}
                    //加入字典

                    dicTionary.Add(netstr, netgo);
                    netgo.gameObject.SetActive(true);
                    //隐藏
                    DOVirtual.DelayedCall(0.5f, () => {
                        netgo.gameObject.SetActive(false);
                    });
                }
            
            });
        }
        void On_PaoIndexUpdate(int paotai) {
            //更新 还是更新当前选择 因为选择的是这个
            ChangePaoShow(nowSelect);
        }
        void On_WingIndexUpdate(int wing) 
        {
            //更新 还是更新当前选择 因为选择的是这个
            ChangeWingShow(nowSelectWing);
        } 
        private void OnEnable()
        {
            for (int i = 0; i < tog_selectPaos.Length; i++)
            {
                //判断是否解锁
                if (tog_selectPaos[i].name == PlayerData.PaoViewIndex.ToString())
                {
                    tog_selectPaos[i].isOn = true;
                }
            }
            for (int i = 0; i < tog_selectWings.Length; i++)
            {
                //判断是否解锁
                if (tog_selectWings[i].name == PlayerData.WingIndex.ToString())
                {
                    tog_selectWings[i].isOn = true;
                }
            }
            //ChangeShow("70");//初始化炮台为70;
            ChangePaoShow(PlayerData.PaoViewIndex);//初始化炮台为70;
            ChangeWingShow(PlayerData.WingIndex);            
            ChangeLockbulletWing();
            //检查VIP等级 能使用的炮台
            int nPower = -1;
            EventManager.PaoIndexUpdate += On_PaoIndexUpdate;
            EventManager.WingIndexUpdate += On_WingIndexUpdate;
            EventManager.ChangeVipLevel += ChangeVipLevel;
            EventManager.ShopPaoWingUpdate += On_ShopPaoWingUpdate;
            UEventDispatcher.Instance.AddEventListener(UEventName.PlayerStatusResponse, On_PlayerStatusResponse);//玩家最新状态响应  
 
        }
        void On_ShopPaoWingUpdate()
        {
            ChangeLockbulletWing();
            ChangeWingShow(nowSelectWing);
            ChangePaoShow(nowSelect);
        }
        void ChangeVipLevel(int vip)
        {
            ChangeLockbulletWing();
        }
        private void On_PlayerStatusResponse(UEventContext obj)
        {
          
        }
        private void OnDisable() {
            EventManager.ChangeVipLevel -= ChangeVipLevel;
            EventManager.PaoIndexUpdate -= On_PaoIndexUpdate;
            EventManager.WingIndexUpdate -= On_WingIndexUpdate;
            EventManager.ShopPaoWingUpdate -= On_ShopPaoWingUpdate;
            UEventDispatcher.Instance.RemoveEventListener(UEventName.PlayerStatusResponse, On_PlayerStatusResponse);//玩家最新状态响应  
        }
    }
}