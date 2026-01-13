
using UnityEngine;
using UnityEngine.UI;
using CoreGame;
using DG.Tweening;
using com.maple.game.osee.proto.fishing;
using System.Collections.Generic;
using System;
using System.Reflection;
using Game.UI;
using System.Collections;
using static UnityEngine.UI.Dropdown;
using com.maple.game.osee.proto;

namespace Game.UI
{
    public class TestFishTwoAttack : MonoBehaviour
    {
        public Button twoAttack1;
        public Button twoAttack2;
        public Button twoAttack3;
        public Button twoAttack4;
        public Button twoAttack5;
        public Button twoAttack6;
        public Button twoAttack7;
        public Button twoAttack8;
        public Button twoAttack9;
        public Button twoAttack10;
        public Button twoAttack11;
        public Button twoAttack12; 
        public Button twoAttack13;
        public Button twoAttack14;
        public Button twoAttack15;
        public Button twoAttack16;
        public Button twoAttack17;


        public Button appear1;
       

    
        public Dropdown DropdownFish;
        public InputField PathID;
        public Transform tmp;

        public Button btnJionRoom;

        public InputField IFMoldeID;
        public Button OkZhYu;

        public InputField IFUITwoAttack; 
        public Button OkZhYuTA;
        public List<Button> ListPaoTai=new List<Button>();

        public List<Button> ListChibang = new List<Button>(); 
        void FindCompent() {
            tmp = this.transform.Find("tmp");
            IFMoldeID = this.transform.Find("tmp/InputFish/IFMoldeID").GetComponent<InputField>();
            
            DropdownFish = this.transform.Find("tmp/DropdownFish").GetComponent<Dropdown>();

            OkZhYu = this.transform.Find("tmp/InputFish/OkZhYu").GetComponent<Button>();

            IFUITwoAttack = this.transform.Find("tmp/TwoAttackEf/IFZsjn").GetComponent<InputField>();
            OkZhYuTA = this.transform.Find("tmp/TwoAttackEf/OkZsjn").GetComponent<Button>();
            PathID = this.transform.Find("tmp/PathID").GetComponent<InputField>();
            twoAttack1 = this.transform.Find("tmp/twoAttack/twoAttack1").GetComponent<Button>();
            twoAttack2 = this.transform.Find("tmp/twoAttack/twoAttack2").GetComponent<Button>();
            twoAttack3 = this.transform.Find("tmp/twoAttack/twoAttack3").GetComponent<Button>();
            twoAttack4 = this.transform.Find("tmp/twoAttack/twoAttack4").GetComponent<Button>();
            twoAttack5 = this.transform.Find("tmp/twoAttack/twoAttack5").GetComponent<Button>();
            twoAttack6 = this.transform.Find("tmp/twoAttack/twoAttack6").GetComponent<Button>();
            twoAttack7 = this.transform.Find("tmp/twoAttack/twoAttack7").GetComponent<Button>();
            twoAttack8 = this.transform.Find("tmp/twoAttack/twoAttack8").GetComponent<Button>();
            twoAttack9 = this.transform.Find("tmp/twoAttack/twoAttack9").GetComponent<Button>();
            twoAttack10 = this.transform.Find("tmp/twoAttack/twoAttack10").GetComponent<Button>();
            twoAttack11 = this.transform.Find("tmp/twoAttack/twoAttack11").GetComponent<Button>();
            twoAttack12 = this.transform.Find("tmp/twoAttack/twoAttack12").GetComponent<Button>();
            twoAttack13 = this.transform.Find("tmp/twoAttack/twoAttack13").GetComponent<Button>();
            twoAttack14 = this.transform.Find("tmp/twoAttack/twoAttack14").GetComponent<Button>();
            twoAttack15 = this.transform.Find("tmp/twoAttack/twoAttack15").GetComponent<Button>();
            twoAttack16 = this.transform.Find("tmp/twoAttack/twoAttack16").GetComponent<Button>();
            twoAttack17 = this.transform.Find("tmp/twoAttack/twoAttack17").GetComponent<Button>();
            appear1 = this.transform.Find("tmp/twoAttackAppaer/appear1").GetComponent<Button>();
  
            btnJionRoom = this.transform.Find("tmp/btnJionRoom").GetComponent<Button>();
            for (int i = 0; i < 9; i++)
            {            
                ListPaoTai.Add(this.transform.Find("tmp/paoTai/" + (71+i)).GetComponent<Button>());
            }
            for (int i = 0; i < 14; i++)
            {
                ListChibang.Add(this.transform.Find("tmp/chiBang/chiBang" + i).GetComponent<Button>());
            }
        }
        void Awake() {
            FindCompent();
            foreach (var item in common4.dicMoldConfig)
            {
                OptionData pd = new OptionData();
                pd.text = item.Key;
                DropdownFish.options.Add(pd);
            }
            DropdownFish.options.Reverse();
            DropdownFish.onValueChanged.AddListener((arg) =>
            {
                //Debug.Log("DropdownFish"+arg);
                //Debug.Log("DropdownFish" + DropdownFish.options[arg].text);
                //test_InitFish(DropdownFish.options[arg].text);

                test_InitFwqFish(DropdownFish.options[arg].text);
            });
            twoAttack16.onClick.AddListener(() =>
            {
         
            });
         
            appear1.onClick.AddListener(() =>
            {
                test_InitFish("天蓬元帅");
            });

            for (int i = 0; i < ListPaoTai.Count; i++)
            {
                int n = i;
                ListPaoTai[n].onClick.AddListener(() =>
                {

                    common3._UIFishingInterface.GetOnePlayer(PlayerData.PlayerId).TestUpdatePaotai(int.Parse(ListPaoTai[n].name));
                });
            }
            

            btnJionRoom.onClick.AddListener(() =>
            {
                JionRoom();
            });
        }
        //服务器生成鱼
        private void test_InitFwqFish(string fishName)
        {
            //fishName = "虎头大金锣";
            int mPath = 0;
            bool mTry = int.TryParse(PathID.text,out mPath);
            if (mTry)
            {
                FishInfo pack = new FishInfo();
                foreach (var item in common4.dicFishConfig)
                {
                    if (item.Value.name == fishName)
                    {
                        pack.ruleId = 221;// 116;
                        pack.configId = item.Key;
                        pack.routeId = mPath; //右边
                        pack.lifeTime = 128;
                        pack.safeTimes = 300;
                        pack.createTime =  ConvertDateTimeToUtc_13(DateTime.Now);
                        pack.fishType = item.Value.fishType;
                        pack.isFirst = false;
                        common.SendMessage((int)OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST, pack);
                        return;
                    }
                }
            }
            FishInfo pack2 = new FishInfo();
            foreach (var item in common4.dicFishConfig)
            {
                if (item.Value.name == fishName)
                {   
                    pack2.ruleId = 221;
                    pack2.configId = item.Key;
                    pack2.routeId = 1060;   
                    pack2.lifeTime = 30;
                    pack2.safeTimes = 300;
                    pack2.createTime = ConvertDateTimeToUtc_13(DateTime.Now);
                    pack2.fishType = item.Value.fishType;
                    pack2.isFirst = false;
                    common.SendMessage((int)OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST, pack2);
                    return;
                }
            }
            //FishInfo pack = new FishInfo();
            //foreach (var item in common4.dicFishConfig)
            //{
            //    if (item.Value.name == fishName)
            //    {
            //        //pack.id = 11;
            //        pack.ruleId = 116;

            //        pack.configId = item.Key;

            //        pack.routeId = 10421;
            //        pack.lifeTime = 100;
            //        pack.safeTimes = 300;

            //        pack.createTime = ConvertDateTimeToUtc_13(DateTime.Now);

            //        pack.fishType = item.Value.fishType;
            //        pack.isFirst = false;
            //        common.SendMessage((int)OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST, pack);
            //        break;
            //    }
            //}
            //NetMessage.OseeFishing.Req_FishInfo(fishName);
        }

        void ADDInfo(FishBossMultipleInfoLine Pine, int id ,string data,int type) {

            FishBossMultipleInfo p = new FishBossMultipleInfo();
            p.id = id;
            p.data = data;
            p.type = type;
            Pine.info.Add(p);
        }
        void JionRoom() { 
            ////var pack = obj.GetData<FishingJoinRoomResponse>();
            //common.bySessionName = (BY_SESSIONNAME)3;
            //Debug.Log("pack.roomIndex" + 3);

            //ByData.nModule = 11;
            //Debug.Log("加入房间返回 UILoadingGame");
            //UIMgr.CloseAll();
            //UIMgr.CloseUI(UIPath.UIMainMenu);
            //UIMgr.CloseUI(UIPath.UIBuyuMenu);

            //UIMgr.DestroyUI(UIPath.UIByChange);
            //ControllerMgr.Instance.WaitForLoading = true;
            ////try
            ////{      //卸载场景
            ////    //SceneManager.UnloadSceneAsync("ZBuyuRoom");
            ////}
            ////catch
            ////{
            ////}
            ////清除玩家数据
            //common.listPlayer.Clear();
            //Debug.Log("开始加载UILoadingGame");
            //var Go = UIMgr.ShowUISynchronize(UIPath.UILoadingGame);
            //UILoadingGame uILoadingGame = Go.GetComponent<UILoadingGame>();// UIMgr.ShowUISynchronize(UIPath.UILoadingGame).GetComponent<UILoadingGame>();
            //Debug.Log("加载UILoadingGame成功");

            //uILoadingGame.StartLoad("ZBuyuRoom", UIPath.UIByRoomMain);
            //Root3D.Instance.ShowAllObject(true);
            //common.IDRoomFish = BY_SESSION.普通场;

            //StartCoroutine(Tmpp());
        }
        public int level { get; set; }

        public string diamond { get; set; }
     
        public int wingIndex { get; set; }
        IEnumerator Tmpp() {
            yield return new WaitForSeconds(5f);
            FishingPlayerInfoResponse pack = new FishingPlayerInfoResponse();
            FishingPlayerInfoProto playerInfo = new FishingPlayerInfoProto();
            playerInfo.playerId = PlayerData.PlayerId;
            playerInfo.name = PlayerData.UserName;
            playerInfo.headIndex = 0;
            playerInfo.viewIndex = 70;
            playerInfo.batteryLevel = 10000;
            playerInfo.diamond = PlayerData.Diamond.ToString();
            playerInfo.money = PlayerData.DragonCrystal;
            playerInfo.seat = 0;
            playerInfo.batteryMult = 10000;
            Debug.Log("playerInfo" + playerInfo.playerId);
            pack.playerInfo = playerInfo;
            UEventDispatcher.Instance.DispatchEvent(UEventName.FishingPlayerInfoResponse, this, pack);
        }
        /// <summary>
        /// 用于测试生成鱼
        /// </summary>
        /// <param name="ms"></param>
        private void test_InitFish(string fishName)
        {
            foreach (var item in common4.dicFishConfig)
            {
                if (item.Value.name== fishName)
                {
                    FishingFishInfoProto ms = new FishingFishInfoProto();
                    ms.clientLifeTime = 0;
                    ms.id = UnityEngine.Random.Range(0,1000000);
                    ms.fishId = item.Value.monsterId;
                    foreach (var itempath in common.dicPathConfig)
                    {
                        if (itempath.Value.pathId == 1392)
                        {
                            ms.routeId = itempath.Value.id;
                        }
                    }
                  
                    ms.createTime = 999;
                    ms.isBossBulge = 0;//0代表不是boss号角生成的鱼

                    if (!common.listFish.ContainsKey(ms.id))
                    {
                        if (common4.dicFishConfig.ContainsKey(ms.fishId))
                        {
                            //根据名字获取模型ID
                            //Debug.Log("common4.dicFishConfig[ms.fishId].name"+common4.dicFishConfig[ms.fishId].name);
                            var goFish = commonLoad.GetOneAynsFish(common4.GetFishModleID(common4.dicFishConfig[ms.fishId].name), Root3D.Instance.rootFish);// (go) =>
                                                                                                                                                            //{

                            if (common4.GetFishCanShu(ms.fishId).isBoss == true || common4.GetFishCanShu(ms.fishId).xiaoboss == true || common4.GetFishCanShu(ms.fishId).isTwoAttack == true || common4.GetFishCanShu(ms.fishId).isSkill == true)
                            {
                                if (ms.clientLifeTime < 1) //时间小于1
                                {
                                    UIMgr.ShowCreatePerfab("BossComing/boss_coming_" + common4.GetFishModleID(ms.fishId), common2.ApperAnimation);
                                }
                                //if (ms.clientLifeTime < 1) //时间小于1
                                //{
                                //    UIMgr.ShowCreatePerfab("BossComing/boss_coming_" + common4.GetFishModleID(ms.fishId), common2.ApperAnimation);
                                //}
                                //int bgID = common4.GetFishCanShu(fishName).BgID;
                                //if (bgID > 0)
                                //{
                                //    for (int i = common2.base_BG.childCount - 1; i >= 0; i--)
                                //    {
                                //        try
                                //        {
                                //            DestroyImmediate(common2.base_BG.GetChild(i).gameObject);
                                //        }
                                //        catch
                                //        {
                                //        }
                                //    }

                                //    GameObject varUIGoBg = common4.LoadPrefab("SceneBg/bg_" + bgID);
                                //    var mm = Instantiate(varUIGoBg, common2.base_BG);
                                //    mm.transform.localScale = Vector3.one;
                                //    mm.transform.localPosition = Vector3.zero;
                                //}
                            }
                            goFish.SetActive(true);

                            goFish.GetComponent<fish>().Init(new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, ms.isBossBulge, false));
                            // });
                        }
                        else
                        {
                            Debug.LogError("鱼id不存在于dicFishConfig  " + ms.fishId);
                        }
                    }
                    else
                    {
                    }
                    return;
                }
            }
           
        }
        public static long ConvertDateTimeToUtc_13(DateTime _time)
        {
            TimeSpan timeSpan = _time.ToUniversalTime() - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(timeSpan.TotalMilliseconds);
        }
        int tmpIdex=0;
        private void test_InitFishModeID(int ModelID)
        {

            var goFish = commonLoad.GetOneAynsFish(ModelID, Root3D.Instance.rootFish);// (go) =>

            goFish.SetActive(true);

            FishingFishInfoProto ms = new FishingFishInfoProto();
            ms.clientLifeTime = 0;
            ms.createTime = ConvertDateTimeToUtc_13(DateTime.Now);//(DateTime.Now.ToUniversalTime().Ticks - 621355968000000000) / 10;
            ms.id = UnityEngine.Random.Range(0, 10000);
            ms.fishId = UnityEngine.Random.Range(0, 885);
            tmpIdex++;

            if (tmpIdex % 2 == 0)
            {
                foreach (var itempath in common.dicPathConfig)
                {
                    if (itempath.Value.pathId == 1249)
                    {
                        ms.routeId = itempath.Value.id;
                    }
                }
            }
            else
            {
                foreach (var itempath in common.dicPathConfig)
                {
                    if (itempath.Value.pathId == 1275)
                    {
                        ms.routeId = itempath.Value.id;
                    }
                }
            }
      
            goFish.GetComponent<fish>().Init(new TmpFishingFishInfoProto(UnityEngine.Random.Range(0, 1000000), ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, 0,false));

            //string fishName=common4.dicMoldConfig()
            //foreach (var item in common4.dicFishConfig)
            //{
            //    if (item.Value.name == fishName)
            //    {
            //        FishingFishInfoProto ms = new FishingFishInfoProto();
            //        ms.clientLifeTime = 0;
            //        ms.id = Random.Range(0, 1000000);
            //        ms.fishId = item.Value.monsterId;
            //        foreach (var itempath in common.dicPathConfig)
            //        {
            //            if (itempath.Value.pathId == 1392)
            //            {
            //                ms.routeId = itempath.Value.id;
            //            }
            //        }

            //        ms.createTime = 999;
            //        ms.isBossBulge = 0;//0代表不是boss号角生成的鱼

            //        if (!common.listFish.ContainsKey(ms.id))
            //        {
            //            if (common4.dicFishConfig.ContainsKey(ms.fishId))
            //            {
            //                //根据名字获取模型ID
            //                //Debug.Log("common4.dicFishConfig[ms.fishId].name"+common4.dicFishConfig[ms.fishId].name);
            //                var goFish = commonLoad.GetOneAynsFish(common4.GetFishModleID(common4.dicFishConfig[ms.fishId].name), Root3D.Instance.rootFish);// (go) =>
            //                //{
            //                if (common4.GetFishFishType(ms.fishId) == 30)
            //                {
            //                    if (ms.clientLifeTime < 1) //时间小于1
            //                    {
            //                        UIMgr.ShowCreatePerfab("BossComing/boss_coming_" + common4.GetFishModleID(ms.fishId), common2.ApperAnimation);
            //                    }
            //                }
            //                else if (common4.GetFishFishType(ms.fishId) == 100)
            //                {
            //                    if (ms.clientLifeTime < 1) //时间小于1
            //                    {
            //                        UIMgr.ShowCreatePerfab("BossComing/boss_coming_" + common4.GetFishModleID(ms.fishId), common2.ApperAnimation);
            //                    }
            //                }
            //                goFish.SetActive(true);

            //                goFish.GetComponent<fish>().Init(new TmpFishingFishInfoProto(ms.id, ms.fishId, ms.routeId, ms.clientLifeTime, ms.createTime, ms.isBossBulge));
            //                // });
            //            }
            //            else
            //            {
            //                Debug.LogError("鱼id不存在于dicFishConfig  " + ms.fishId);
            //            }
            //        }
            //        else
            //        {
            //        }
            //        return;
            //    }
            //}

        }
        void Update()
        {
            if (Root3D.Instance.GetClickKeyCodeP_DOWN())
            {
                if (tmp.gameObject.activeSelf)
                {
                    tmp.gameObject.SetActive(false);
                }
                else
                {
                    tmp.gameObject.SetActive(true);
                }
            }
        }
    }
}

public class SkillTA<T>
{ 
    public T ShengCheng(string strname) 
    {
        int varid;
        string strclass = strname;

        UIContext varUIcon = new UIContext();
        varUIcon.name = "TwoAttack/" + strclass;
        varUIcon.uiType = UIType.NormalUICanvas;
        GameObject go = UIMgr.ShowCreateUI(varUIcon);

        
        //获取类型
        var tmp4 = go.GetComponent<T>();
        //Type t = tmp4.GetType();

        //Debug.Log(t.Name);//类名
        //Debug.Log(t.Namespace);//所属命名空间
        //Debug.Log(t.Assembly.ToString());//程序集信息

        //MethodInfo method = t.GetMethod("Init");//输入方法名,调用

        //object[] allcanshu = new object[7];
        //allcanshu[0] = PlayerData.PlayerId;
        //allcanshu[1] = Vector3.zero;
        //allcanshu[2] = "天蓬元帅";
        //allcanshu[3] = 1723000000;
        //allcanshu[4] = 3;
        //allcanshu[5] = 20;
        //allcanshu[6] = 10000;
        //method?.Invoke(this, allcanshu);
        //tmp4.Init(PlayerData.PlayerId, Vector3.zero, "天蓬元帅", 1723000000, 3, 20, 10000);
        return tmp4;
    }
}