using com.maple.game.osee.proto;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIBuLuoWai : MonoBehaviour
    {

        public Button btn_Close;
        public Transform item;
        public Transform itemparent;
        public GameObjectPool itemPool;
        public Transform Panel_SQ;
        public Transform Panel_CJ;

        //创建部落
        public Button btn_CJBuLuo;
        public Button btn_okCJ;
        public InputField Input_Name;
        //查找部落
        public Transform Panel_ChaZhao;
        public Button btn_ChaZhao;
        public InputField Input_buluoname;
        public Transform ChaZhaoParent;

        // public InputField InputFileContent; 
        // Use this for initialization
        void FindCompent()
        {
     
            btn_Close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            item = this.transform.Find("bg/view_souye/Viewport/Content/item");
            itemparent = this.transform.Find("bg/view_souye/Viewport/Content");
            Panel_SQ = this.transform.Find("bg_sqjr");
            Panel_CJ = this.transform.Find("bg_cjbl");
            btn_CJBuLuo = this.transform.Find("bg/btn_cjbl").GetComponent<Button>();
            btn_okCJ = this.transform.Find("bg_cjbl/btn_SQCJ").GetComponent<Button>();
            Input_Name = this.transform.Find("bg_cjbl/Input_create").GetComponent<InputField>();
            Panel_ChaZhao = this.transform.Find("bg/view_FindBuluo");
            btn_ChaZhao = this.transform.Find("bg/btn_czbl").GetComponent<Button>();
            Input_buluoname = this.transform.Find("bg/Input_buluoname").GetComponent<InputField>();
            ChaZhaoParent = this.transform.Find("bg/view_FindBuluo/Viewport/Content");
        }
        private void Awake()
        {
            FindCompent();
            itemPool = new GameObjectPool();
            itemPool.SetTemplete(item.gameObject);
            itemPool.Recycle(item.gameObject);
            btn_ChaZhao.onClick.AddListener(() =>
            {
                Chazha();
            });

            Input_buluoname.onValueChanged.AddListener((arg) =>
            {
                if (arg == "")
                {
                    Panel_ChaZhao.gameObject.SetActive(false);
                }
            });
            btn_CJBuLuo.onClick.AddListener(() =>
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

                //if (common.isShengFenZheng)
                //{
                //    UIAccount varUIAccount = UIMgr.ShowUI(UIPath.UIAccount).GetComponent<UIAccount>();
                //    varUIAccount.SetPanel(UIAccountPanel.SecurityCenter);
                //}
                //else
                //{
                //    // tras_AccountXuKe.gameObject.SetActive(true);
                //    UIMgr.ShowUI(UIPath.UI_ViewXuke);
                //}

                Panel_CJ.gameObject.SetActive(true);
            });
            btn_okCJ.onClick.AddListener(() =>
            {

                NetMessage.OseeLobby.Req_TribeEsTabLishRequest(PlayerData.PlayerId, Input_Name.text, "", 5, 2, 2, "1");
            });
            btn_Close.onClick.AddListener(() =>
            {
                UIMgr.CloseUI(UIPath.UIBuLuoWai);
            });

            UEventDispatcher.Instance.AddEventListener(UEventName.TribrEsTabLishResponse, On_TribrEsTabLishResponse);//创建部落返回
            UEventDispatcher.Instance.AddEventListener(UEventName.GetTribeResponse, On_GetTribeResponse);//获取部落列表返回        
            UEventDispatcher.Instance.AddEventListener(UEventName.ApplyTripeResponse, On_ApplyTripeResponse);//申请部落返回

            UEventDispatcher.Instance.AddEventListener(UEventName.UpdateTribeJurisDictionResponse, On_UpdateTribeJurisDictionResponse);//修改部落权限返回
            UEventDispatcher.Instance.AddEventListener(UEventName.DepositTribeWareHouseResponse, On_DepositTribeWareHouseResponse);//存入部落仓库返回
            UEventDispatcher.Instance.AddEventListener(UEventName.OutTribeWareHouseResponse, On_OutTribeWareHouseResponse);//取出部落仓库返回
            UEventDispatcher.Instance.AddEventListener(UEventName.UpdateTribeNameResponse, On_UpdateTribeNameResponse);//修改部落名称返回
            UEventDispatcher.Instance.AddEventListener(UEventName.UpdateTribeContextResponse, On_UpdateTribeContextResponse);//修改部落简介返回


        }
        private void OnEnable()
        {


            UEventDispatcher.Instance.AddEventListener(UEventName.DealApplyTripeResponse, On_DealApplyTripeResponse);//处理申请部落返回
            InitThis();


        }
        void InitThis()
        {
            Input_buluoname.text = "";
            Panel_CJ.gameObject.SetActive(false);
            Panel_SQ.gameObject.SetActive(false);
            NetMessage.OseeLobby.Req_GetTribeRequest();
        }
        void ShowMyListBuLuo(long TribeID)
        {
        }
        void Chazha()
        {
            if (Input_buluoname.text == "")
            {
                MessageBox.ShowPopOneMessage("输入不能为空");
                return;
            }
            Panel_ChaZhao.gameObject.SetActive(true);
            foreach (Transform item in ChaZhaoParent)
            {
                itemPool.Recycle(item.gameObject);
            }

            for (int i = 0; i < AllTribe.Count; i++)
            {
                if (AllTribe[i].name == Input_buluoname.text || AllTribe[i].tribeId.ToString() == Input_buluoname.text)
                {
                    if (PlayerData.Jion_BuluoList.Count > 0)
                    {
                        for (int xj = 0; xj < PlayerData.Jion_BuluoList.Count; xj++)
                        {
                            if (PlayerData.Jion_BuluoList[xj] == AllTribe[i].tribeId)
                            {
                                int n = i;
                                var go = itemPool.Get(ChaZhaoParent);
                                go.SetActive(true);
                                go.name = AllTribe[n].tribeId.ToString();
                                go.transform.Find("txt_blname").GetComponent<Text>().text = AllTribe[n].name;
                                go.transform.Find("txt_blslname").GetComponent<Text>().text = AllTribe[n].slName;
                                go.transform.Find("txt_renshu").GetComponent<Text>().text = AllTribe[n].realNum + "/" + AllTribe[n].userNum + "人";

                                go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = true;
                                go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "进入部落";

                                UIHelper.GetHeadImage(AllTribe[n].headUrl, (sp) =>
                                {
                                    go.transform.Find("head").GetComponent<Image>().sprite = sp;
                                });
                                go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.RemoveAllListeners();
                                go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.AddListener(() =>
                                {
                                    PlayerData.BuluoID = AllTribe[n].tribeId;
                                    UIMgr.CloseUI(UIPath.UIBuLuoWai);
                                    UIMgr.ShowUI(UIPath.UIBuLuo);
                                });
                                //如果有就断掉后面的
                                return;
                            }
                        }

                    }
                    if (AllTribe[i].tribeId == 10046 || AllTribe[i].tribeId == 10047 || AllTribe[i].tribeId == 10048 || AllTribe[i].tribeId == 10049 || AllTribe[i].tribeId == 10050)
                    {
                        var go = itemPool.Get(ChaZhaoParent);
                        go.SetActive(true);
                        go.name = AllTribe[i].tribeId.ToString();
                        go.transform.Find("txt_blname").GetComponent<Text>().text = AllTribe[i].name;
                        go.transform.Find("txt_blslname").GetComponent<Text>().text = AllTribe[i].slName;// "";
                        go.transform.Find("txt_renshu").GetComponent<Text>().text = AllTribe[i].userNum + "/" + AllTribe[i].userNum + "人";

                        if (AllTribe[i].isApply)
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = false;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请中";
                        }
                        else
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = true;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请";
                        }

                        UIHelper.GetHeadImage(AllTribe[i].headUrl, (sp) =>
                        {
                            go.transform.Find("head").GetComponent<Image>().sprite = sp;
                        });
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.RemoveAllListeners();
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.AddListener(() =>
                        {
                            NetMessage.OseeLobby.Req_ApplyTribeRequest(long.Parse(go.name), PlayerData.PlayerId);
                        });
                    }
                    else
                    {
                        var go = itemPool.Get(ChaZhaoParent);
                        go.SetActive(true);
                        go.name = AllTribe[i].tribeId.ToString();
                        go.transform.Find("txt_blname").GetComponent<Text>().text = AllTribe[i].name;
                        go.transform.Find("txt_blslname").GetComponent<Text>().text = AllTribe[i].slName;// "";
                        go.transform.Find("txt_renshu").GetComponent<Text>().text = AllTribe[i].realNum + "/" + AllTribe[i].userNum + "人";

                        if (AllTribe[i].isApply)
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = false;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请中";
                        }
                        else
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = true;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请";
                        }

                        UIHelper.GetHeadImage(AllTribe[i].headUrl, (sp) =>
                        {
                            go.transform.Find("head").GetComponent<Image>().sprite = sp;
                        });
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.RemoveAllListeners();
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.AddListener(() =>
                        {
                            NetMessage.OseeLobby.Req_ApplyTribeRequest(long.Parse(go.name), PlayerData.PlayerId);
                        });
                    }
                }

            }

            //查找部落
        }
       
        private void OnDestroy()
        {

            UEventDispatcher.Instance.RemoveEventListener(UEventName.TribrEsTabLishResponse, On_TribrEsTabLishResponse);//创建部落返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.GetTribeResponse, On_GetTribeResponse);//获取部落列表返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.ApplyTripeResponse, On_ApplyTripeResponse);//申请部落返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.UpdateTribeJurisDictionResponse, On_UpdateTribeJurisDictionResponse);//修改部落权限返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.DepositTribeWareHouseResponse, On_DepositTribeWareHouseResponse);//存入部落仓库返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.OutTribeWareHouseResponse, On_OutTribeWareHouseResponse);//取出部落仓库返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UpdateTribeNameResponse, On_UpdateTribeNameResponse);//修改部落名称返回
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UpdateTribeContextResponse, On_UpdateTribeContextResponse);//修改部落简介返回


        }
        private void OnDisable()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.DealApplyTripeResponse, On_DealApplyTripeResponse);//处理申请部落返回
        }
        /// <summary>
        /// 创建部落返回
        /// <summary>
        private void On_TribrEsTabLishResponse(UEventContext obj)
        {
            var pack = obj.GetData<TribrEsTabLishResponse>();
        }

        List<Tribe> AllTribe;
        /// <summary>
        /// 获取部落列表返回
        /// <summary>
        private void On_GetTribeResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetTribeResponse>();
            foreach (Transform item in itemparent)
            {
                itemPool.Recycle(item.gameObject);
            }

            AllTribe = pack.tribe;
            if (PlayerData.Jion_BuluoList.Count > 0)
            {
                for (int m = 0; m < pack.tribe.Count; m++)
                {
                    for (int i = 0; i < PlayerData.Jion_BuluoList.Count; i++)
                    {
                        if (PlayerData.Jion_BuluoList[i] == pack.tribe[m].tribeId)
                        {
                            int n = m;
                            var go = itemPool.Get(itemparent);
                            go.SetActive(true);
                            go.name = pack.tribe[n].tribeId.ToString();
                            go.transform.Find("txt_blname").GetComponent<Text>().text = pack.tribe[n].name;
                            go.transform.Find("txt_blslname").GetComponent<Text>().text = pack.tribe[n].slName;
                            go.transform.Find("txt_renshu").GetComponent<Text>().text = pack.tribe[n].realNum + "/" + pack.tribe[n].userNum + "人";

                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = true;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "进入部落";

                            UIHelper.GetHeadImage(pack.tribe[n].headUrl, (sp) =>
                            {
                                go.transform.Find("head").GetComponent<Image>().sprite = sp;
                            });
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.RemoveAllListeners();
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.AddListener(() =>
                            {
                                PlayerData.BuluoID = pack.tribe[n].tribeId;
                                UIMgr.CloseUI(UIPath.UIBuLuoWai);
                                UIMgr.ShowUI(UIPath.UIBuLuo);
                            });
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < pack.tribe.Count; i++)
                {
                    //if (pack.tribe[i].name == "海洋管家")
                    //{
                    //    Debug.Log(pack.tribe[i].tribeId);
                    //}
                    //else if (pack.tribe[i].name == "幸运")
                    //{
                    //    Debug.Log(pack.tribe[i].tribeId);
                    //}

                    //else if (pack.tribe[i].name == "爆发部落")

                    //{
                    //    Debug.Log(pack.tribe[i].tribeId);
                    //}
                    //else if (pack.tribe[i].name == "海底冒险队")

                    //{
                    //    Debug.Log(pack.tribe[i].tribeId);
                    //}
                    if (pack.tribe[i].tribeId == 10046 || pack.tribe[i].tribeId == 10047 || pack.tribe[i].tribeId == 10048 || pack.tribe[i].tribeId == 10049 || pack.tribe[i].tribeId == 10050)
                    {
                        var go = itemPool.Get(itemparent);
                        go.SetActive(true);
                        go.name = pack.tribe[i].tribeId.ToString();
                        go.transform.Find("txt_blname").GetComponent<Text>().text = pack.tribe[i].name;
                        go.transform.Find("txt_blslname").GetComponent<Text>().text = pack.tribe[i].slName;
                        go.transform.Find("txt_renshu").GetComponent<Text>().text = pack.tribe[i].userNum + "/" + pack.tribe[i].userNum + "人";

                        if (pack.tribe[i].isApply)
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = false;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请中";
                        }
                        else
                        {
                            go.transform.Find("kuang/btn_sq").GetComponent<Button>().interactable = true;
                            go.transform.Find("kuang/btn_sq/Text").GetComponent<Text>().text = "申请";
                        }

                        UIHelper.GetHeadImage(pack.tribe[i].headUrl, (sp) =>
                        {
                            go.transform.Find("head").GetComponent<Image>().sprite = sp;
                        });
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.RemoveAllListeners();
                        go.transform.Find("kuang/btn_sq").GetComponent<Button>().onClick.AddListener(() =>
                        {
                            NetMessage.OseeLobby.Req_ApplyTribeRequest(long.Parse(go.name), PlayerData.PlayerId);
                            NetMessage.OseeLobby.Req_GetTribeRequest();
                        });
                    }
                }
            }

        }
        /// <summary>
        /// 获取部落所有成员返回
        /// <summary>
        private void On_GetTribeAllUserResponse(UEventContext obj)
        {
            var pack = obj.GetData<GetTribeAllUserResponse>();
        }
        /// <summary>
        /// 申请部落返回
        /// <summary>
        private void On_ApplyTripeResponse(UEventContext obj)
        {
            var pack = obj.GetData<ApplyTripeResponse>();
            NetMessage.OseeLobby.Req_GetTribeRequest();
            NetMessage.OseeFishing.Req_IsJoinTribeRequest(PlayerData.PlayerId);


        }
        public void ChangeChazhao()
        {
            if (Input_buluoname.text != "")
            {
                Chazha();
            }
        }
        /// <summary>
        /// 处理申请部落返回
        /// <summary>
        private void On_DealApplyTripeResponse(UEventContext obj)
        {
            var pack = obj.GetData<DealApplyTripeResponse>();
            NetMessage.OseeFishing.Req_IsJoinTribeRequest(PlayerData.PlayerId);
        }
        /// <summary>
        /// 修改部落权限返回
        /// <summary>
        private void On_UpdateTribeJurisDictionResponse(UEventContext obj)
        {
            var pack = obj.GetData<UpdateTribeJurisDictionResponse>();
        }
        /// <summary>
        /// 存入部落仓库返回
        /// <summary>
        private void On_DepositTribeWareHouseResponse(UEventContext obj)
        {
            var pack = obj.GetData<DepositTribeWareHouseResponse>();
        }
        /// <summary>
        /// 取出部落仓库返回
        /// <summary>
        private void On_OutTribeWareHouseResponse(UEventContext obj)
        {
            var pack = obj.GetData<OutTribeWareHouseResponse>();
        }
        /// <summary>
        /// 修改部落名称返回
        /// <summary>
        private void On_UpdateTribeNameResponse(UEventContext obj)
        {
            var pack = obj.GetData<UpdateTribeNameResponse>();
        }
        /// <summary>
        /// 修改部落简介返回
        /// <summary>
        private void On_UpdateTribeContextResponse(UEventContext obj)
        {
            var pack = obj.GetData<UpdateTribeContextResponse>();
        }


    }
}
