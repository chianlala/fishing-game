using com.maple.game.osee.proto;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIBuLuo : MonoBehaviour
    {
        public Button btn_Close;
        public Button btn_BianJi;
        public Button btn_refresh;
        public Button btn_buluolibao;
        public Button btn_Recording;
        public Transform bg_ShouQuRecording;
        public Transform ShouQuitem;
        public Transform itemShouQuParent;
        public GameObjectPool itemShouQuPool;

        public Transform itemSouye;
        public Transform itemSouyeparent;
        public GameObjectPool itemSouyePool;

        //public Toggle[] tog_lfet;  
        public Transform[] tras_panel=new Transform[5];
        public Button[] btn_item=new Button[14];
        public Button[] btn_Caoliao=new Button[15];
        public Button btn_itemDiamond;
        //公会信息
        public Toggle tog_Souye;
        public Toggle tog_buluoxinxi;
        public Toggle tog_buluoquanxian;
        public Toggle tog_shenqing;
        public Toggle tog_buluoChangku;
        //公会信息
        public Text Txt_GhName;
        public Text Txt_Name;
        public Text Txt_Chengyuan;
        public Text Txt_GhID;
        public Text Txt_XuanYan;

        public Text txt_buluoname;
        public Text txt_vipxz;
        public Text txt_levelxz;
        public Text txt_JiaRuxz;
        //查找玩家
        public Transform SouSuoPanel;
        public Transform SouSuoparent;
        public InputField Input_FindPlayer;
        public Button btn_FindPlayer;
        //存入 
        public InputField Input_Num;
        public InputField Input_QCM;
        public Image ImageCrSp;
        public int NowID = 0;
        public Button btn_OkCrSp;
        public Transform CRPanel;
        public Button btn_CRClose;
        public Text Txt_SuLiang;

        public Transform itemCunRu;
        public Transform itemCunRuparent;
        public GameObjectPool itemCunRuPool;

        //取出
        public Image ImageQcSp;
        public Text ImageQcNum;
        public InputField Input_PassWorld;
        public Transform QuChuPanel;
        public Button btn_QuChuClose;
        public Button btn_QuChuOk;

        public InputField Input_FindBox;
        public Button btn_FindBox;
        //存取记录
        public Transform itemCQJL;
        public Transform itemCQJLparent;
        public GameObjectPool itemCQJLPool;

        //申请加入
        public Transform itemShengQin;
        public Transform itemShengQinparent;
        public GameObjectPool itemShengQinPool;
        //编辑宣言
        public Transform Trans_gonggao;
        public InputField Input_xuanyan;
        public Button btn_gonggaook;
        public Button btn_gonggaoColse;
        //编辑名字
        public Button btn_openChangeName;
        public Transform Trans_ChangeName;
        public InputField ChangeName;
        public Button btn_changeNameOk;
        public Button btn_changeNameClose;
        //部落权限
        public Transform Trans_quanxian;
        public Button btn_quanxianclose;
        //给副首领
        public Button btn_sqjrbl;
        public Button btn_tichubl;
        public Button btn_zhiwutiaozheng;
        //处理成员
        public Transform panel_bianjicy;
        public Text txt_wanjiaName;
        public Button btn_tichubuluo;
        public Button btn_shezhiwei;
        //public Button btn_shezhiweiputong;
        //public Button btn_shezhiweifusoulin;

        //部落信息
        public Button btn_jiarutiaojian;
        public Transform panel_quanxian;
        public Button btn_quanxianOk;
        public InputField Input_Vip;
        public InputField Input_dengji;
        public Dropdown Dropdown_verif;

        public long xzVip = 0;
        public long xzlevel = 0;
        public Button btn_addvip;
        public Button btn_recvip;
        public Button btn_addlevel;
        public Button btn_reclevel;
        // 首领 = 1,副首领 = 2,精英 = 3,成员 = 4,
        long intNowPostion;
        // Use this for initialization
       void FindCompent() {
            btn_Close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            btn_BianJi = this.transform.Find("bg/Btn_bianji").GetComponent<Button>();
            btn_refresh = this.transform.Find("bg/view_changku/btn_refresh").GetComponent<Button>();
            btn_buluolibao = this.transform.Find("bg/btn_lingqulibao").GetComponent<Button>();
            btn_Recording = this.transform.Find("bg/view_changku/btn_jilv").GetComponent<Button>();
            bg_ShouQuRecording = this.transform.Find("bg/panel_Recording");
            ShouQuitem = this.transform.Find("bg/panel_Recording/Viewport/Content/object");
            itemShouQuParent = this.transform.Find("bg/panel_Recording/Viewport/Content");
            itemSouye = this.transform.Find("bg/view_souye/Viewport/Content/item");
            itemSouyeparent = this.transform.Find("bg/view_souye/Viewport/Content");
            tras_panel[0] = this.transform.Find("bg/view_souye");
            tras_panel[1] = this.transform.Find("bg/view_buluoxinxi");
            tras_panel[2] = this.transform.Find("bg/view_quanxian");
            tras_panel[3] = this.transform.Find("bg/view_buluoxinxi");
            tras_panel[4] = this.transform.Find("bg/view_changku");
            btn_item[0] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/7").GetComponent<Button>();
            btn_item[1] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/8").GetComponent<Button>();
            btn_item[2] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/9").GetComponent<Button>();
            btn_item[3] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/10").GetComponent<Button>();
            btn_item[4] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/11").GetComponent<Button>();
            btn_item[5] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/13").GetComponent<Button>();
            btn_item[6] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/19").GetComponent<Button>();
            btn_item[7] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/50").GetComponent<Button>();
            btn_item[8] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/20").GetComponent<Button>();
            btn_item[9] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/21").GetComponent<Button>();
            btn_item[10] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/22").GetComponent<Button>();
            btn_item[11] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/51").GetComponent<Button>();
            btn_item[12] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/52").GetComponent<Button>();
            btn_item[13] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/64").GetComponent<Button>();
            btn_Caoliao[0] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image/23").GetComponent<Button>();
            btn_Caoliao[1] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (1)/24").GetComponent<Button>();
            btn_Caoliao[2] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (2)/25").GetComponent<Button>();
            btn_Caoliao[3] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (3)/26").GetComponent<Button>();
            btn_Caoliao[4] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (4)/27").GetComponent<Button>();
            btn_Caoliao[5] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (5)/28").GetComponent<Button>();
            btn_Caoliao[6] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (6)/29").GetComponent<Button>();
            btn_Caoliao[7] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (7)/30").GetComponent<Button>();
            btn_Caoliao[8] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (8)/31").GetComponent<Button>();
            btn_Caoliao[9] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (9)/32").GetComponent<Button>();
            btn_Caoliao[10] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (10)/33").GetComponent<Button>();
            btn_Caoliao[11] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (11)/34").GetComponent<Button>();
            btn_Caoliao[12] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (12)/35").GetComponent<Button>();
            btn_Caoliao[13] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (13)/36").GetComponent<Button>();
            btn_Caoliao[14] = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/Image (14)/37").GetComponent<Button>();
            btn_itemDiamond = this.transform.Find("bg/view_changku/view_beibao/Viewport/Content/4").GetComponent<Button>();
            tog_Souye = this.transform.Find("bg/root_tog/tog_souye").GetComponent<Toggle>();
            tog_buluoxinxi = this.transform.Find("bg/root_tog/tog_blxx").GetComponent<Toggle>();
            tog_buluoquanxian = this.transform.Find("bg/root_tog/tog_blqx").GetComponent<Toggle>();
            tog_shenqing = this.transform.Find("bg/root_tog/tog_sqlb").GetComponent<Toggle>();
            tog_buluoChangku = this.transform.Find("bg/root_tog/tog_blck").GetComponent<Toggle>();
            Txt_GhName = this.transform.Find("bg/mingzhi/ghName").GetComponent<Text>();
            Txt_Name = this.transform.Find("bg/mingzhi/name").GetComponent<Text>();
            Txt_Chengyuan = this.transform.Find("bg/mingzhi/txt_chengyuan").GetComponent<Text>();
            Txt_GhID = this.transform.Find("bg/mingzhi/txt_ghid").GetComponent<Text>();
            Txt_XuanYan = this.transform.Find("bg/gonggao/txt_gonggao").GetComponent<Text>();
            txt_buluoname = this.transform.Find("bg/view_buluoxinxi/Image/txt_buluoName").GetComponent<Text>();
            txt_vipxz = this.transform.Find("bg/view_buluoxinxi/txt_vipxz").GetComponent<Text>();
            txt_levelxz = this.transform.Find("bg/view_buluoxinxi/txt_levelxz").GetComponent<Text>();
            txt_JiaRuxz = this.transform.Find("bg/view_buluoxinxi/txt_xyglycnjiaru").GetComponent<Text>();
            SouSuoPanel = this.transform.Find("bg/view_SouSuo");
            SouSuoparent = this.transform.Find("bg/view_SouSuo/Viewport/Content");
            Input_FindPlayer = this.transform.Find("bg/view_souye/FindName").GetComponent<InputField>();
            btn_FindPlayer = this.transform.Find("bg/view_souye/btn_Find").GetComponent<Button>();
            Input_Num = this.transform.Find("bg/panel_ChunRuItem/bg/InputField").GetComponent<InputField>();
            Input_QCM = this.transform.Find("bg/panel_ChunRuItem/bg/InputField (1)").GetComponent<InputField>();
            btn_OkCrSp = this.transform.Find("bg/panel_ChunRuItem/bg/btn_ok").GetComponent<Button>();
            CRPanel = this.transform.Find("bg/panel_ChunRuItem");
            btn_CRClose = this.transform.Find("bg/panel_ChunRuItem/btn_Close").GetComponent<Button>();
            Txt_SuLiang = this.transform.Find("bg/panel_ChunRuItem/bg/7/txt_count").GetComponent<Text>();
            itemCunRu = this.transform.Find("bg/view_changku/view_changku/Viewport/Content/0/item");
            itemCunRuparent = this.transform.Find("bg/view_changku/view_changku/Viewport/Content");
            ImageQcNum = this.transform.Find("bg/panel_QuChuItem/bg/7/txt_count").GetComponent<Text>();
            Input_PassWorld = this.transform.Find("bg/panel_QuChuItem/bg/InputPassWorld").GetComponent<InputField>();
            QuChuPanel = this.transform.Find("bg/panel_QuChuItem");
            btn_QuChuClose = this.transform.Find("bg/panel_QuChuItem/btn_Close").GetComponent<Button>();
            btn_QuChuOk = this.transform.Find("bg/panel_QuChuItem/bg/btn_ok").GetComponent<Button>();
            Input_FindBox = this.transform.Find("bg/view_changku/FindBaoXiang").GetComponent<InputField>();
            btn_FindBox = this.transform.Find("bg/view_changku/btn_Find").GetComponent<Button>();
            itemCQJL = this.transform.Find("bg/panel_Recording/Viewport/Content/object");
            itemCQJLparent = this.transform;
        }
        private void Awake()
        {
            FindCompent();
            itemShouQuPool = new GameObjectPool();
            itemShouQuPool.SetTemplete(ShouQuitem.gameObject);
            itemShouQuPool.Recycle(ShouQuitem.gameObject);

            itemSouyePool = new GameObjectPool();
            itemSouyePool.SetTemplete(itemSouye.gameObject);
            itemSouyePool.Recycle(itemSouye.gameObject);

            itemCunRuPool = new GameObjectPool();
            itemCunRuPool.SetTemplete(itemCunRu.gameObject);
            itemCunRuPool.Recycle(itemCunRu.gameObject);


            itemShengQinPool = new GameObjectPool();
            itemShengQinPool.SetTemplete(itemShengQin.gameObject);
            itemShengQinPool.Recycle(itemShengQin.gameObject);

            itemCQJLPool = new GameObjectPool();
            itemCQJLPool.SetTemplete(itemCQJL.gameObject);
            itemCQJLPool.Recycle(itemCQJL.gameObject);

            btn_Recording.onClick.AddListener(() =>
            {
                NetMessage.OseeLobby.Req_GetInOrOutRequest(PlayerData.BuluoID, PlayerData.PlayerId);
                bg_ShouQuRecording.gameObject.SetActive(true);
            });
            btn_BianJi.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                Trans_gonggao.gameObject.SetActive(true);
                //编辑
            });
            btn_OkCrSp.onClick.AddListener(() =>
            {
                if (Input_Num.text == "")
                {
                    MessageBox.ShowPopOneMessage("数量不能为空");
                    return;
                }
                //if (Input_QCM.text == "")
                //{
                //    MessageBox.ShowPopOneMessage("取出码不能为空");
                //    return;
                //}
                if (int.Parse(Input_Num.text) > 10000)
                {
                    MessageBox.ShowPopOneMessage("单个仓库存入物品数量上限为10000");
                    return;
                }
                NetMessage.OseeLobby.Req_DepositTribeWareHouseRequest(PlayerData.BuluoID, PlayerData.PlayerId, NowID, int.Parse(Input_Num.text));
                CRPanel.gameObject.SetActive(false);
                Input_Num.text = "";
                Input_QCM.text = "";
            });
            btn_CRClose.onClick.AddListener(() =>
            {
                CRPanel.gameObject.SetActive(false);
            });
            btn_gonggaoColse.onClick.AddListener(() =>
            {
                Trans_gonggao.gameObject.SetActive(false);
            });
            tog_Souye.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    NetMessage.OseeLobby.Req_GetTribeAllUser(PlayerData.BuluoID);
                }
            });
            tog_buluoxinxi.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
                }
            });
            tog_buluoquanxian.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
                }
            });
            tog_shenqing.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
                }
            });
            tog_buluoChangku.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    //NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
                    NetMessage.OseeLobby.Req_GetAllTribeWareHouseRequest(PlayerData.BuluoID);
                    Input_FindBox.text = "";
                }
            });
            //btn_tichubuluo.onClick.AddListener(() => {
            //    NetMessage.OseeLobby.Req_KickOutTribeUserRequest(PlayerData.BuluoID,);
            //});
            //btn_shezhiweiputong.onClick.AddListener(() => {

            //});
            //btn_shezhiweifusoulin.onClick.AddListener(() => {

            //});
            btn_FindBox.onClick.AddListener(() =>
            {
                NetMessage.OseeLobby.Req_TribeSearchRequest(PlayerData.BuluoID, PlayerData.PlayerId, Input_FindBox.text);
            });
            Input_FindBox.onValueChanged.AddListener((arg) =>
            {
                if (arg == "")
                {
                    NetMessage.OseeLobby.Req_GetAllTribeWareHouseRequest(PlayerData.BuluoID);
                }
            });
            btn_changeNameClose.onClick.AddListener(() =>
            {
                Trans_ChangeName.gameObject.SetActive(false);
            });
            btn_openChangeName.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                Trans_ChangeName.gameObject.SetActive(true);
            });
            btn_gonggaook.onClick.AddListener(() =>
            {
                NetMessage.OseeLobby.Req_UpdateTribeContextRequest(PlayerData.BuluoID, PlayerData.PlayerId, Input_xuanyan.text);
            });
            btn_jiarutiaojian.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                panel_quanxian.gameObject.SetActive(true);
            });
            btn_changeNameOk.onClick.AddListener(() =>
            {
                Trans_ChangeName.gameObject.SetActive(false);
                NetMessage.OseeLobby.Req_UpdateTribeNameRequest(PlayerData.BuluoID, PlayerData.PlayerId, ChangeName.text);
            });
            btn_addvip.onClick.AddListener(() =>
            {
                xzVip++;
                if (xzVip > 9)
                {
                    xzVip = 0;
                }
                Input_Vip.text = xzVip.ToString();
            });
            btn_recvip.onClick.AddListener(() =>
            {
                xzVip--;
                if (xzVip <= -1)
                {
                    xzVip = 9;
                }
                Input_Vip.text = xzVip.ToString();
            });
            btn_addlevel.onClick.AddListener(() =>
            {
                xzlevel++;
                if (xzlevel > 100)
                {
                    xzlevel = 0;
                }

                Input_dengji.text = xzlevel.ToString();
            });
            btn_reclevel.onClick.AddListener(() =>
            {
                xzlevel--;
                if (xzlevel < 0)
                {
                    xzlevel = 100;
                }
                Input_dengji.text = xzlevel.ToString();
            });

            btn_FindPlayer.onClick.AddListener(() =>
            {
                if (Input_FindPlayer.text == "")
                {
                    MessageBox.ShowPopOneMessage("搜索不能为空");
                    return;
                }
                SouSuoPanel.gameObject.SetActive(true);
                foreach (Transform item in SouSuoparent)
                {
                    itemSouyePool.Recycle(item.gameObject);
                }

                for (int i = 0; i < AlltribeUser.Count; i++)
                {
                    if (Input_FindPlayer.text == AlltribeUser[i].userName || Input_FindPlayer.text == AlltribeUser[i].userId.ToString())
                    {
                        var go = itemSouyePool.Get(SouSuoparent);
                        go.SetActive(true);
                        go.transform.Find("txt_name").GetComponent<Text>().text = AlltribeUser[i].userName;
                        go.transform.Find("txt_level").GetComponent<Text>().text = AlltribeUser[i].userLevel.ToString();
                        go.transform.Find("txt_zhiwei").GetComponent<Text>().text = ((EnumZHIWEI)(AlltribeUser[i].position)).ToString();
                        go.transform.Find("txt_vip").GetComponent<Text>().text = "VIP" + AlltribeUser[i].vipLevel.ToString();
                        go.transform.Find("txt_Gold").GetComponent<Text>().text = AlltribeUser[i].money.ToString();

                        if (AlltribeUser[i].isOnline)
                        {
                            go.transform.Find("txt_state").GetComponent<Text>().text = "在线";
                        }
                        else
                        {
                            go.transform.Find("txt_state").GetComponent<Text>().text = "离线";
                        }
                    }
                }
            });
            Input_FindPlayer.onEndEdit.AddListener((arg) =>
            {
                if (arg == "")//恢复页面
                {
                    SouSuoPanel.gameObject.SetActive(false);
                }
            });
            btn_sqjrbl.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                if (quanxian4Result == 1)
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 4, 2);
                }
                else
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 4, 1);
                }
            });
            btn_tichubl.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                if (quanxian5Result == 1)
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 5, 2);
                }
                else
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 5, 1);
                }

            });
            btn_zhiwutiaozheng.onClick.AddListener(() =>
            {
                if (intNowPostion != 1)
                {
                    MessageBox.ShowPopOneMessage("只有首领才能更改");
                    return;
                }
                if (quanxian6Result == 1)
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 6, 2);
                }
                else
                {
                    NetMessage.OseeLobby.Req_UpdateTribeJurisDictionRequest(PlayerData.BuluoID, PlayerData.PlayerId, 6, 1);
                }
            });
            btn_quanxianOk.onClick.AddListener(() =>
            {
                int varvip = 0;
                int vardengji = 0;
                if (Input_Vip.text != "")
                {
                    varvip = int.Parse(Input_Vip.text);
                    if (varvip < 0 || varvip > 9)
                    {
                        MessageBox.Show("VIP等级只能在0-9内");
                    }
                }
                else
                {

                    MessageBox.Show("VIP等级不能为空");
                }
                if (Input_dengji.text != "")
                {
                    vardengji = int.Parse(Input_Vip.text);
                    if (vardengji < 0 || vardengji > 100)
                    {
                        MessageBox.Show("玩家等级只能在0-100内");
                    }
                }
                else
                {
                    MessageBox.Show("玩家等级不能为空");
                }
                NetMessage.OseeLobby.Req_UpdateTribeLevelRequest(PlayerData.BuluoID, PlayerData.PlayerId, int.Parse(Input_Vip.text), int.Parse(Input_dengji.text), Dropdown_verif.value + 1);
                panel_quanxian.gameObject.SetActive(false);
            });
            for (int i = 0; i < btn_item.Length; i++)
            {
                int n = i;
                btn_item[n].onClick.AddListener(() =>
                {
                    CRPanel.gameObject.SetActive(true);
                    ImageCrSp.sprite = btn_item[n].GetComponent<Image>().sprite;
                    NowID = int.Parse(btn_item[n].name);
                    Txt_SuLiang.text = btn_item[n].transform.GetChild(0).GetChild(0).GetComponent<Text>().text;
                });
            }
            for (int i = 0; i < btn_Caoliao.Length; i++)
            {
                int n = i;
                btn_Caoliao[n].onClick.AddListener(() =>
                {
                    CRPanel.gameObject.SetActive(true);
                    ImageCrSp.sprite = btn_Caoliao[n].GetComponent<Image>().sprite;
                    NowID = int.Parse(btn_Caoliao[n].name);
                    Txt_SuLiang.text = btn_Caoliao[n].transform.GetChild(0).GetChild(0).GetComponent<Text>().text;
                });
            }
            btn_itemDiamond.onClick.AddListener(() =>
            {
                CRPanel.gameObject.SetActive(true);
                ImageCrSp.sprite = btn_itemDiamond.GetComponent<Image>().sprite;
                NowID = int.Parse(btn_itemDiamond.name);
                Txt_SuLiang.text = btn_itemDiamond.transform.GetChild(0).GetChild(0).GetComponent<Text>().text;
            });
            UEventDispatcher.Instance.AddEventListener(UEventName.GetTribeAllUserResponse, On_GetTribeAllUserResponse);//获取部落所有成员返回       
            UEventDispatcher.Instance.AddEventListener(UEventName.GetAllTribeWareHouseResponse, On_GetAllTribeWareHouseResponse);//获取所有宝箱返回
            UEventDispatcher.Instance.AddEventListener(UEventName.GetAllTribeApplyResponse, On_GetAllTribeApplyResponse);//获取所有部落申请返回
            UEventDispatcher.Instance.AddEventListener(UEventName.GetOneTribeResponse, On_GetOneTribeResponse);//获取当前部落信息返回

            UEventDispatcher.Instance.AddEventListener(UEventName.SetTribeUserPositionResponse, On_SetTribeUserPositionResponse);//获取所有部落申请返回
            UEventDispatcher.Instance.AddEventListener(UEventName.KickOutTribeUserResponse, On_KickOutTribeUserResponse);//踢人出部落返回

            UEventDispatcher.Instance.AddEventListener(UEventName.GetInOrOutResponse, On_GetInOrOutResponse);//获取存取记录返回

            UEventDispatcher.Instance.AddEventListener(UEventName.IsTribeGetGiftResponse, On_IsTribeGetGiftResponse);//是否获取部落礼包返回
            UEventDispatcher.Instance.AddEventListener(UEventName.TribeSearchResponse, On_TribeSearchResponse);//部落宝箱搜索返回

            UEventDispatcher.Instance.AddEventListener(UEventName.GetTribeGiftResponse, On_GetTribeGiftResponse);//获取部落礼包返回


        }
        long quanxian4Result;
        long quanxian5Result;
        long quanxian6Result;
        void Start()
        {
            btn_Close.onClick.AddListener(() =>
            {
                SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close");
                UIMgr.CloseUI(UIPath.UIBuLuo);
            });
            btn_refresh.onClick.AddListener(() =>
            {
            // NetMessage.OseeLobby.Req_GetInOrOutRequest(PlayerData.BuluoID, PlayerData.PlayerId);
            //刷新
            NetMessage.OseeLobby.Req_GetAllTribeWareHouseRequest(PlayerData.BuluoID);
            });
            btn_buluolibao.onClick.AddListener(() =>
            {
                if (common.LoginState == 2) //游客登陆
            {
                    MessageBox.Show("你还未设置昵称！点击确定立即开始设置", null, () =>
                    {
                        UIAccount varUIAccount = UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>();
                        varUIAccount.SetPanel(UIAccountPanel.BangDingNackName);
                    });
                    return;
                }
                if (common.LoginState == 4) //微信登陆
            {
                    if (PlayerData.isWxSheZhiNickName)
                    {

                    }
                    else
                    {
                        MessageBox.Show("你还未设置昵称！点击确定立即开始设置", null, () =>
                        {
                            UIAccount varUIAccount = UIMgr.ShowUISynchronize(UIPath.UIAccount).GetComponent<UIAccount>();
                            varUIAccount.SetPanel(UIAccountPanel.BangDingNackName);
                        });
                        return;
                    }
                }
                NetMessage.OseeLobby.Req_GetTribeGiftRequest(PlayerData.PlayerId);
            });
        }
        void ChangeItem()
        {
            for (int i = 0; i < btn_item.Length - 3; i++)
            {
                btn_item[i].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myItem[i + 2].ToString();
            }
            btn_item[btn_item.Length - 3].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[51].ToString();
            btn_item[btn_item.Length - 2].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[52].ToString();
            btn_item[btn_item.Length - 1].transform.GetChild(0).GetChild(0).GetComponent<Text>().text = common.myCaiLiao[64].ToString();
            if (PlayerData.Diamond > 10000)
            {
                btn_itemDiamond.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = (PlayerData.Diamond / 10000) + "W";
            }
            else
            {
                btn_itemDiamond.transform.GetChild(0).GetChild(0).GetComponent<Text>().text = PlayerData.Diamond.ToString();
            }
            for (int i = 0; i < btn_Caoliao.Length; i++)
            {
                btn_Caoliao[i].transform.transform.Find("0/txt_count").GetComponent<Text>().text = common.myCaiLiao[int.Parse(btn_Caoliao[i].name)].ToString();
            }
        }
        private void OnEnable()
        {
            UIMgr.CloseUI(UIPath.UIBuLuoWai);
            UEventDispatcher.Instance.AddEventListener(UEventName.DealApplyTripeResponse, On_DealApplyTripeResponse);//处理申请部落返回

            //NetMessage.OseeFishing.Req_IsJoinTribeRequest(PlayerData.PlayerId);
            if (PlayerData.BuluoID == 0)
            {
                Debug.LogError("PlayerData.BuluoID==0");
                UIMgr.CloseUI(UIPath.UIBuLuo);
            }
            else
            {
                NetMessage.OseeLobby.Req_GetTribeAllUser(PlayerData.BuluoID);

                NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
                NetMessage.OseeLobby.Req_GetOneTribeRequest(PlayerData.BuluoID);
                //NetMessage.OseeLobby.Req_GetInOrOutRequest(PlayerData.BuluoID,PlayerData.PlayerId);  
                ChangeItem();
                EventManager.PropUpdate+=TestFunc;

            }
            NetMessage.OseeLobby.Req_IsTribeGetGiftRequest(PlayerData.PlayerId);
        }
        private void OnDisable()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.DealApplyTripeResponse, On_DealApplyTripeResponse);//处理申请部落返回


            EventManager.PropUpdate -= TestFunc;
  
        }
        void TestFunc()
        {
            ChangeItem();
        }
        // ZHIWEI
        enum EnumZHIWEI
        {
            首领 = 1,
            副首领 = 2,
            精英 = 3,
            成员 = 4,
        }
        List<TribeUser> AlltribeUser;
        /// <summary>
        /// 返回隐藏中间的字符串
        /// </summary>
        /// <param name="Input">输入</param>
        /// <returns>输出</returns>
        public static string GetxxxString(string Input)
        {
            string Output = "";
            switch (Input.Length)
            {
                case 1:
                    Output = "*";
                    break;
                case 2:
                    Output = Input[0] + "*";
                    break;
                case 0:
                    Output = "";
                    break;
                default:
                    Output = Input.Substring(0, 1);
                    for (int i = 0; i < Input.Length - 2; i++)
                    {
                        Output += "*";
                    }
                    Output += Input.Substring(Input.Length - 1, 1);
                    break;
            }
            return Output;
        }
        /// <summary>
        /// 获取部落所有成员返回
        /// <summary>
        private void On_GetTribeAllUserResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetTribeAllUserResponse>();
            foreach (Transform item in itemSouyeparent)
            {
                itemSouyePool.Recycle(item.gameObject);
            }
            AlltribeUser = pack.tribeUser;
            for (int i = 0; i < pack.tribeUser.Count; i++)
            {
                if (pack.tribeUser[i].userId == PlayerData.PlayerId)
                {
                    intNowPostion = pack.tribeUser[i].position;

                    if (intNowPostion == 1 || intNowPostion == 2)
                    {
                        tog_Souye.gameObject.SetActive(true);
                        tog_buluoxinxi.gameObject.SetActive(true);
                        tog_buluoquanxian.gameObject.SetActive(true);
                        tog_shenqing.gameObject.SetActive(true);
                        tog_buluoChangku.gameObject.SetActive(true);
                        btn_BianJi.gameObject.SetActive(true);
                        tog_Souye.isOn = true;
                        tras_panel[0].gameObject.SetActive(true);
                        tras_panel[1].gameObject.SetActive(false);
                        tras_panel[2].gameObject.SetActive(false);
                        tras_panel[3].gameObject.SetActive(false);
                        tras_panel[4].gameObject.SetActive(false);
                    }
                    else
                    {
                        tog_Souye.gameObject.SetActive(true);
                        tog_buluoxinxi.gameObject.SetActive(false);
                        tog_buluoquanxian.gameObject.SetActive(false);
                        tog_shenqing.gameObject.SetActive(false);
                        tog_buluoChangku.gameObject.SetActive(true);

                        tras_panel[0].gameObject.SetActive(true);
                        tras_panel[1].gameObject.SetActive(false);
                        tras_panel[2].gameObject.SetActive(false);
                        tras_panel[3].gameObject.SetActive(false);
                        tras_panel[4].gameObject.SetActive(false);

                        tog_Souye.isOn = true;
                        btn_BianJi.gameObject.SetActive(false);
                    }
                }
                var go = itemSouyePool.Get(itemSouyeparent);
                go.SetActive(true);
                go.name = pack.tribeUser[i].userId.ToString();
                go.transform.Find("txt_name").GetComponent<Text>().text = GetxxxString(pack.tribeUser[i].userName);
                go.transform.Find("txt_level").GetComponent<Text>().text = pack.tribeUser[i].userLevel.ToString();
                go.transform.Find("txt_zhiwei").GetComponent<Text>().text = ((EnumZHIWEI)(pack.tribeUser[i].position)).ToString();
                go.transform.Find("txt_vip").GetComponent<Text>().text = "VIP" + pack.tribeUser[i].vipLevel.ToString();
                go.transform.Find("txt_Gold").GetComponent<Text>().text = pack.tribeUser[i].money.ToString();
                UIHelper.GetHeadImage(pack.tribeUser[i].headIndex.ToString(), (sp) =>
                {
                    go.transform.Find("head").GetComponent<Image>().sprite = sp;
                });// pack.tribeUser[i].money.ToString(); 
                if (pack.tribeUser[i].isOnline)
                {
                    go.transform.Find("txt_state").GetComponent<Text>().text = "在线";
                }
                else
                {
                    go.transform.Find("txt_state").GetComponent<Text>().text = "离线";
                }
                //long varuserId = pack.tribeUser[i].userId;
                go.GetComponent<Button>().onClick.RemoveAllListeners();
                go.GetComponent<Button>().onClick.AddListener(() =>
                {

                    txt_wanjiaName.text = go.transform.Find("txt_name").GetComponent<Text>().text;

                    btn_tichubuluo.onClick.RemoveAllListeners();
                    btn_tichubuluo.onClick.AddListener(() =>
                    {
                        if (intNowPostion == 1)
                        {
                            NetMessage.OseeLobby.Req_KickOutTribeUserRequest(PlayerData.BuluoID, long.Parse(go.name));
                        }
                        else if (intNowPostion == 2)
                        {
                            if (quanxian5Result == 1)
                            {
                                NetMessage.OseeLobby.Req_KickOutTribeUserRequest(PlayerData.BuluoID, long.Parse(go.name));
                            }
                            else
                            {
                                MessageBox.ShowPopOneMessage("未开启权限");
                            }
                        }
                        else
                        {
                            MessageBox.ShowPopOneMessage("没有权限");
                        }
                    });

                    if (go.transform.Find("txt_zhiwei").GetComponent<Text>().text == "首领")
                    {
                    // NetMessage.OseeLobby.Req_KickOutTribeUserRequest(PlayerData.BuluoID, long.Parse(go.name));
                    btn_shezhiwei.onClick.RemoveAllListeners();
                        btn_shezhiwei.gameObject.SetActive(false);
                        MessageBox.ShowPopOneMessage("无法操作首领");
                    //panel_bianjicy.gameObject.SetActive(true);
                }
                    else if (go.transform.Find("txt_zhiwei").GetComponent<Text>().text == "副首领")
                    {
                        panel_bianjicy.gameObject.SetActive(true);
                        btn_shezhiwei.gameObject.SetActive(true);
                        btn_shezhiwei.onClick.RemoveAllListeners();
                        btn_shezhiwei.transform.GetChild(0).GetComponent<Text>().text = "设为成员";
                        btn_shezhiwei.onClick.AddListener(() =>
                        {
                            if (intNowPostion != 1)
                            {
                                MessageBox.ShowPopOneMessage("只有首领才能更改");
                                return;
                            }
                            NetMessage.OseeLobby.Req_SetTribeUserPositionRequest(PlayerData.BuluoID, long.Parse(go.name), 4);
                        });
                    }
                    else if (go.transform.Find("txt_zhiwei").GetComponent<Text>().text == "成员")
                    {
                        panel_bianjicy.gameObject.SetActive(true);
                        btn_shezhiwei.gameObject.SetActive(true);
                        btn_shezhiwei.transform.GetChild(0).GetComponent<Text>().text = "设为副首领";
                        btn_shezhiwei.onClick.RemoveAllListeners();
                        btn_shezhiwei.onClick.AddListener(() =>
                        {
                            if (intNowPostion != 1)
                            {
                                MessageBox.ShowPopOneMessage("只有首领才能更改");
                                return;
                            }
                            NetMessage.OseeLobby.Req_SetTribeUserPositionRequest(PlayerData.BuluoID, long.Parse(go.name), 2);
                        });
                    //NetMessage.OseeLobby.Req_KickOutTribeUserRequest(PlayerData.BuluoID, long.Parse(go.name));
                }
                });
                //});
            }
        }
        /// <summary>
        /// 获取所有宝箱返回
        /// <summary>
        private void On_GetAllTribeWareHouseResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetAllTribeWareHouseResponse>();
            foreach (Transform item in itemCunRuparent)
            {
                if (item.childCount != 0)
                {
                    for (int i = 0; i < item.childCount; i++)
                    {
                        itemCunRuPool.Recycle(item.GetChild(i).gameObject);
                    }
                }
            }
            for (int i = 0; i < pack.tribeItem.Count; i++)
            {
                if (i >= 30)
                {
                    return;
                }
                var go = itemCunRuPool.Get(itemCunRuparent.transform.GetChild(i));
                go.SetActive(true);
                go.name = pack.tribeItem[i].wareHouseId.ToString();

                if (pack.tribeItem[i].itemId == 3)
                {
                    go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/jiangquan2");
                }
                else
                {
                    if (pack.tribeItem[i].itemId == 5 || pack.tribeItem[i].itemId == 6 || pack.tribeItem[i].itemId == 7)
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/hedan03");
                    }
                    else if (pack.tribeItem[i].itemId == 21)
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/xiyouhedan");
                    }
                    else
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/" + pack.tribeItem[i].itemId);
                    }

                }
                try
                {
                   // go.transform.Find("name").GetComponent<Text>().text = ((BY_ItemLName)(pack.tribeItem[i].itemId)).ToString();
                }
                catch
                {

                }

                go.transform.Find("txt_count").GetComponent<Text>().text = pack.tribeItem[i].itemNum.ToString();

                go.transform.GetComponent<Button>().onClick.RemoveAllListeners();

                go.transform.GetComponent<Button>().onClick.AddListener(() =>
                {
                    QuChuPanel.gameObject.SetActive(true);
                    ImageQcSp.sprite = go.transform.GetComponent<Image>().sprite;
                    ImageQcNum.text = go.transform.Find("txt_count").GetComponent<Text>().text;

                    btn_QuChuOk.onClick.RemoveAllListeners();
                    btn_QuChuOk.onClick.AddListener(() =>
                    {

                        NetMessage.OseeLobby.Req_OutTribeWareHouseRequest(PlayerData.BuluoID, PlayerData.PlayerId, Input_PassWorld.text, long.Parse(go.name));
                        QuChuPanel.gameObject.SetActive(false);
                        Input_PassWorld.text = "";
                    });
                });


                //go.transform.GetComponent<Image>().sprite =int.Parse(pack.tribeItem[i].itemId);
                //go.transform.Find("txt_zhiwei").GetComponent<Text>().text = pack.tribeItem[i].wareHouseId.ToString();                                  
            }
        }
        /// <summary>
        /// 获取所有部落申请返回
        /// <summary>
        private void On_GetAllTribeApplyResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetAllTribeApplyResponse>();

            foreach (Transform item in itemShengQinparent)
            {
                itemShengQinPool.Recycle(item.gameObject);
            }
            for (int i = 0; i < pack.tribeApply.Count; i++)
            {
                var go = itemShengQinPool.Get(itemShengQinparent);
                go.SetActive(true);
                UIHelper.GetHeadImage(pack.tribeApply[i].headUrl, (sp) =>
                {
                    go.transform.Find("head").GetComponent<Image>().sprite = sp;
                });

                go.transform.Find("txt_name").GetComponent<Text>().text = pack.tribeApply[i].userName.ToString();
                go.transform.Find("txt_vip").GetComponent<Text>().text = "VIP" + pack.tribeApply[i].vipLevel.ToString();
                go.transform.Find("txt_level").GetComponent<Text>().text = pack.tribeApply[i].userLevel.ToString();
                go.name = pack.tribeApply[i].applyId.ToString();

                go.transform.Find("btn_ok").GetComponent<Button>().onClick.RemoveAllListeners();
                go.transform.Find("btn_ok").GetComponent<Button>().onClick.AddListener(() =>
                {
                    if (intNowPostion == 1)
                    {
                        NetMessage.OseeLobby.Req_DealApplyTribeRequest(long.Parse(go.name), 1, PlayerData.BuluoID);
                    }
                    else if (intNowPostion == 2)
                    {
                        if (quanxian4Result == 1)
                        {
                            NetMessage.OseeLobby.Req_DealApplyTribeRequest(long.Parse(go.name), 1, PlayerData.BuluoID);
                        }
                        else
                        {
                            MessageBox.ShowPopOneMessage("未开启权限");
                        }
                    }
                    else
                    {
                        MessageBox.ShowPopOneMessage("没有权限");
                    }
                });
                go.transform.Find("btn_cancel").GetComponent<Button>().onClick.RemoveAllListeners();
                go.transform.Find("btn_cancel").GetComponent<Button>().onClick.AddListener(() =>
                {
                    if (intNowPostion == 1)
                    {
                        NetMessage.OseeLobby.Req_DealApplyTribeRequest(long.Parse(go.name), 0, PlayerData.BuluoID);
                    }
                    else if (intNowPostion == 2)
                    {
                        if (quanxian4Result == 1)
                        {
                            NetMessage.OseeLobby.Req_DealApplyTribeRequest(long.Parse(go.name), 0, PlayerData.BuluoID);
                        }
                        else
                        {
                            MessageBox.ShowPopOneMessage("未开启权限");
                        }
                    }
                    else
                    {
                        MessageBox.ShowPopOneMessage("没有权限");
                    }
                });
            }
        }
        /// <summary>
        /// 设置玩家位置返回
        /// <summary>
        private void On_SetTribeUserPositionResponse(UEventContext obj)
        {
            var pack = obj.GetData<SetTribeUserPositionResponse>();
            MessageBox.ShowPopMessage("设置成功");
            panel_bianjicy.gameObject.SetActive(false);
            NetMessage.OseeLobby.Req_GetTribeAllUser(PlayerData.BuluoID);
        }
        /// <summary>
        /// 踢人出部落返回
        /// <summary>
        private void On_KickOutTribeUserResponse(UEventContext obj)
        {
            var pack = obj.GetData<KickOutTribeUserResponse>();
            MessageBox.ShowPopMessage("踢出成功");
            NetMessage.OseeLobby.Req_GetTribeAllUser(PlayerData.BuluoID);
            panel_bianjicy.gameObject.SetActive(false);
        }
        static void CopyString(string str)
        {
            TextEditor te = new TextEditor();
            te.text = str;
            te.SelectAll();
            te.Copy();
            MessageBox.ShowPopOneMessage("复制到剪切板成功!");
        }
        /// <summary>
        /// 获取存取记录返回
        /// <summary>
        private void On_GetInOrOutResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetInOrOutResponse>();
            foreach (Transform item in itemCQJLparent)
            {
                itemCQJLPool.Recycle(item.gameObject);
            }

            for (int i = 0; i < pack.tribeInOrOut.Count; i++)
            {

                var go = itemCQJLPool.Get(itemCQJLparent);
                go.SetActive(true);
                // go.name = pack.tribeInOrOut[i].userId.ToString();
                // go.transform.Find("outId").GetComponent<Text>().text = pack.tribeInOrOut[i].outUserId.ToString();
                //go.transform.Find("inId").GetComponent<Text>().text = pack.tribeInOrOut[i].inUserId.ToString();
                go.transform.Find("outname").GetComponent<Text>().text = pack.tribeInOrOut[i].outUserName.ToString();

                go.transform.Find("warehouse").GetComponent<Text>().text = pack.tribeInOrOut[i].wareHouseId.ToString();
                go.transform.Find("password").GetComponent<Text>().text = pack.tribeInOrOut[i].password.ToString();

                go.transform.Find("inname").GetComponent<Text>().text = pack.tribeInOrOut[i].inUserName.ToString();
                //go.transform.Find("prop").GetComponent<Text>().text = (((BY_ItemLName)(pack.tribeInOrOut[i].itemId)).ToString()) + "/" + pack.tribeInOrOut[i].itemNum.ToString();
                go.transform.Find("time").GetComponent<Text>().text = pack.tribeInOrOut[i].inTime.ToString();
                if (pack.tribeInOrOut[i].outTime == "")
                {
                    go.transform.Find("Outtime").GetComponent<Text>().text = "-";
                }
                else
                {
                    go.transform.Find("Outtime").GetComponent<Text>().text = pack.tribeInOrOut[i].outTime.ToString();
                }

                int n = i;
                if (pack.tribeInOrOut[i].isOut == 0)
                {
                    go.transform.Find("copy").gameObject.SetActive(true);
                    go.transform.Find("copy").GetComponent<Button>().onClick.AddListener(() =>
                    {
                        CopyString("我存储了一个宝箱：" + pack.tribeInOrOut[n].wareHouseId + "，密码：" + pack.tribeInOrOut[n].password + " 粘贴到搜索框即可直接打开！");
                    });
                }
                else if (pack.tribeInOrOut[i].isOut == 1)
                {
                    go.transform.Find("copy").gameObject.SetActive(false);
                }
                else
                {
                    go.transform.Find("copy").gameObject.SetActive(false);
                }

            }
        }

        /// <summary>
        /// 部落宝箱搜索返回
        /// <summary>
        private void On_TribeSearchResponse(UEventContext obj)
        {
            var pack = obj.GetData<TribeSearchResponse>();


            //  var pack = obj.GetData<GetAllTribeWareHouseResponse>();
            foreach (Transform item in itemCunRuparent)
            {
                if (item.childCount != 0)
                {
                    for (int i = 0; i < item.childCount; i++)
                    {
                        itemCunRuPool.Recycle(item.GetChild(i).gameObject);
                    }
                }
            }
            for (int i = 0; i < pack.tribeItem.Count; i++)
            {
                if (i >= 30)
                {
                    return;
                }
                var go = itemCunRuPool.Get(itemCunRuparent.transform.GetChild(i));
                go.SetActive(true);
                go.name = pack.tribeItem[i].wareHouseId.ToString();

                if (pack.tribeItem[i].itemId == 3)
                {
                    go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/jiangquan2");
                }
                else
                {
                    if (pack.tribeItem[i].itemId == 5 || pack.tribeItem[i].itemId == 6 || pack.tribeItem[i].itemId == 7)
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/hedan03");
                    }
                    else if (pack.tribeItem[i].itemId == 21)
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/xiyouhedan");
                    }
                    else
                    {
                        go.transform.GetComponent<Image>().sprite = common4.LoadSprite("item/" + pack.tribeItem[i].itemId);
                    }

                }
                try
                {
                   // go.transform.Find("name").GetComponent<Text>().text = ((BY_ItemLName)(pack.tribeItem[i].itemId)).ToString();
                }
                catch
                {

                }

                go.transform.Find("txt_count").GetComponent<Text>().text = pack.tribeItem[i].itemNum.ToString();

                go.transform.GetComponent<Button>().onClick.RemoveAllListeners();

                go.transform.GetComponent<Button>().onClick.AddListener(() =>
                {
                    QuChuPanel.gameObject.SetActive(true);
                    ImageQcSp.sprite = go.transform.GetComponent<Image>().sprite;
                    ImageQcNum.text = go.transform.Find("txt_count").GetComponent<Text>().text;

                    btn_QuChuOk.onClick.RemoveAllListeners();
                    btn_QuChuOk.onClick.AddListener(() =>
                    {

                        NetMessage.OseeLobby.Req_OutTribeWareHouseRequest(PlayerData.BuluoID, PlayerData.PlayerId, Input_PassWorld.text, long.Parse(go.name));
                        QuChuPanel.gameObject.SetActive(false);
                        Input_PassWorld.text = "";
                    });
                });


            }
        }

        /// <summary>
        /// 处理申请部落返回
        /// <summary>
        private void On_DealApplyTripeResponse(UEventContext obj)
        {
            var pack = obj.GetData<DealApplyTripeResponse>();
            NetMessage.OseeLobby.Req_GetAllTribeApplyRequest(PlayerData.BuluoID);
        }

        /// <summary>
        /// 获取部落礼包返回
        /// <summary>
        private void On_GetTribeGiftResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetTribeGiftResponse>();
            if (pack.isSuccess == 1)
            {
                //pack.tribeGiftItem
                UIMessageItemBox tmp = UIMgr.ShowUISynchronize(UIPath.UIMessageItemBox).GetComponent<UIMessageItemBox>();
                Dictionary<int, long> Dictmp = new Dictionary<int, long>();
                foreach (var item in pack.tribeGiftItem)
                {
                    Dictmp.Add(item.itemId, item.itemNum);
                }
                tmp.InitItem(Dictmp, -1, true);
                btn_buluolibao.gameObject.SetActive(false);
                NetMessage.OseeLobby.Req_VipLevelRequest();
            }

        }

        /// <summary>
        /// 是否获取部落礼包返回
        /// <summary>
        private void On_IsTribeGetGiftResponse(UEventContext obj)
        {
            var pack = obj.GetData<IsTribeGetGiftResponse>();
            if (pack.isGet == 0)//未领取
            {
                if (PlayerData.PlayerId == pack.userId)
                {
                    btn_buluolibao.gameObject.SetActive(true);
                }
            }
            else
            {
                if (PlayerData.PlayerId == pack.userId)
                {
                    btn_buluolibao.gameObject.SetActive(false);
                }
            }

        }

        /// <summary>
        /// 获取当前部落信息返回
        /// <summary>
        private void On_GetOneTribeResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetOneTribeResponse>();
            Txt_GhName.text = pack.tribe.name;
            txt_buluoname.text = pack.tribe.name;
            Txt_Name.text = pack.tribe.slName;
            Txt_GhID.text = pack.tribe.tribeId.ToString();
            Txt_Chengyuan.text = AlltribeUser.Count + "/" + pack.tribe.userNum;
            Txt_XuanYan.text = pack.tribe.context;

            xzVip = pack.tribe.vipRestrict;
            xzlevel = pack.tribe.levelRestrict;

            Input_Vip.text = pack.tribe.vipRestrict.ToString();
            Input_dengji.text = pack.tribe.levelRestrict.ToString();

            txt_vipxz.text = pack.tribe.vipRestrict.ToString();
            txt_levelxz.text = pack.tribe.levelRestrict.ToString();


            Dropdown_verif.value = (int)(pack.tribe.verificationRestrict + 1);
            if (pack.tribe.iJurisdiction == 1)
            {
                btn_sqjrbl.transform.GetChild(0).gameObject.SetActive(true);
            }
            else
            {
                btn_sqjrbl.transform.GetChild(0).gameObject.SetActive(false);
            }

            if (pack.tribe.wJurisdiction == 1)
            {
                btn_tichubl.transform.GetChild(0).gameObject.SetActive(true);
            }
            else
            {
                btn_tichubl.transform.GetChild(0).gameObject.SetActive(false);
            }
            if (pack.tribe.lJurisdiction == 1)
            {
                btn_zhiwutiaozheng.transform.GetChild(0).gameObject.SetActive(true);
            }
            else
            {
                btn_zhiwutiaozheng.transform.GetChild(0).gameObject.SetActive(false);
            }
            quanxian4Result = pack.tribe.iJurisdiction;
            quanxian5Result = pack.tribe.wJurisdiction;
            quanxian6Result = pack.tribe.lJurisdiction;

            if (pack.tribe.verificationRestrict == 1)
            {
                txt_JiaRuxz.text = "任何人都可以加入";
            }
            else
            {
                txt_JiaRuxz.text = "需管理员同意";
            }

        }

        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetTribeAllUserResponse, On_GetTribeAllUserResponse);//获取部落所有成员返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetAllTribeWareHouseResponse, On_GetAllTribeWareHouseResponse);//获取所有宝箱返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetAllTribeApplyResponse, On_GetAllTribeApplyResponse);//获取所有部落申请返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetOneTribeResponse, On_GetOneTribeResponse);//获取当前部落信息返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.KickOutTribeUserResponse, On_KickOutTribeUserResponse);//踢人出部落返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetInOrOutResponse, On_GetInOrOutResponse);//获取存取记录返回
                                                                                                                //UEventDispatcher.Instance.RemoveEventListener(UEventName.DealApplyTripeResponse, On_DealApplyTripeResponse);//处理申请部落返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.TribeSearchResponse, On_TribeSearchResponse);//部落宝箱搜索返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.IsTribeGetGiftResponse, On_IsTribeGetGiftResponse);//是否获取部落礼包返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetTribeGiftResponse, On_GetTribeGiftResponse);//获取部落礼包返回

        }
    }
}
