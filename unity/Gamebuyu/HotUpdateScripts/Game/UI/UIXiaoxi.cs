using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{

    public class UIXiaoxi : MonoBehaviour
    {
        public Button btn_close;
        public GameObject item;
        public GameObject itemRight;
        public Transform leftContent;
        public Transform rightRight;
        public Button btn_allLingqu;
        public Button btn_allDelet; 
        //详情界面
        public Text txt_title;
        public Text txt_content;
        public Text txt_date;
        public Button btn_delete;
        public Button btn_LingQu;      
        public Transform viewGiftRe; 
        public Button btn_viewGiftReOk;
        public Text txt_sendID;
        public Image img_item;
        public Text txt_itemcount;
        public Text txt_itemName;
        public Text txt_getID;
        public Text txt_sendDate;
        long _nCurIndex = -1;//当前邮件index
        GameObjectPool itemPool = new GameObjectPool();
        GameObjectPool itemPoolRight = new GameObjectPool();
        void FindCompent() {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            item = this.transform.Find("bg/bg1/leftScroll/Viewport/leftContent/item").gameObject;
            itemRight = this.transform.Find("bg/bg1/rightScroll/Viewport/Content/rightRight/itemRight").gameObject;
            leftContent = this.transform.Find("bg/bg1/leftScroll/Viewport/leftContent");
            rightRight = this.transform.Find("bg/bg1/rightScroll/Viewport/Content/rightRight");
            txt_title = this.transform.Find("bg/bg1/rightScroll/txt_title").GetComponent<Text>();
            txt_content = this.transform.Find("bg/bg1/rightScroll/Viewport/Content/txt_content").GetComponent<Text>();
            txt_date = this.transform.Find("bg/bg1/rightScroll/txt_date").GetComponent<Text>();
            btn_delete = this.transform.Find("bg/bg1/btn_delete").GetComponent<Button>();
            btn_LingQu = this.transform.Find("bg/bg1/btn_deleteLing").GetComponent<Button>();
            viewGiftRe = this.transform.Find("viewGiftRe");
            btn_viewGiftReOk = this.transform.Find("viewGiftRe/bg/btn_viewGiftReOk").GetComponent<Button>();
            txt_sendID = this.transform.Find("viewGiftRe/bg/bgSendID/txt_sendID").GetComponent<Text>();
            img_item = this.transform.Find("viewGiftRe/bg/img_item").GetComponent<Image>();
            txt_itemcount = this.transform.Find("viewGiftRe/bg/img_item/txt_itemcount").GetComponent<Text>();
            txt_itemName = this.transform.Find("viewGiftRe/bg/txt_itemName").GetComponent<Text>();
            txt_getID = this.transform.Find("viewGiftRe/bg/bgGetID/txt_getID").GetComponent<Text>();
            txt_sendDate = this.transform.Find("viewGiftRe/bg/bgSendDate/txt_sendDate").GetComponent<Text>();

            btn_allLingqu = this.transform.Find("bg/bg1/btn_allLingqu").GetComponent<Button>();
            btn_allDelet = this.transform.Find("bg/bg1/btn_allDelet").GetComponent<Button>();
        }
        public List<MessageInfoProto> allMessageInfo; 
        void Awake()
        {
            FindCompent();
     
            itemPool.SetTemplete(item);
            itemPool.Recycle(item);
            itemPoolRight.SetTemplete(itemRight);
            itemPoolRight.Recycle(itemRight);

            btn_close.onClick.AddListener(() => UIMgr.CloseUI(UIPath.UIXiaoxi));
            btn_delete.onClick.AddListener(OnDeleteMail);
            btn_LingQu.onClick.AddListener(OnLingQuMail);
            btn_viewGiftReOk.onClick.AddListener(() =>
            {
                viewGiftRe.gameObject.SetActive(false);
            });
            btn_allLingqu.onClick.AddListener(() =>
            {
                for (int i = 0; i < allMessageInfo.Count; i++)
                {
                    if (allMessageInfo[i].receive ==false)
                    {
                        long n = allMessageInfo[i].id;
                        NetMessage.OseeFishing.Req_ReceiveMessageItemsRequest(n);
                        NetMessage.OseeFishing.Req_ReadMessageRequest(n);
                    }
                }
                ClearLeftInfo();
                ClearRightInfo();
                NetMessage.OseeFishing.Req_MessageListRequest();
            });
            btn_allDelet.onClick.AddListener(() => 
            {
                for (int i = 0; i < allMessageInfo.Count; i++)
                {
                    if (allMessageInfo[i].receive == true)
                    {
                        long n = allMessageInfo[i].id;
                        NetMessage.OseeFishing.Req_DeleteMessageRequest(n);
                    }
                }
                ClearLeftInfo();
                ClearRightInfo();
                NetMessage.OseeFishing.Req_MessageListRequest();
            });
            UEventDispatcher.Instance.AddEventListener(UEventName.MessageListResponse, On_MessageListResponse);//玩家消息列表响应
            UEventDispatcher.Instance.AddEventListener(UEventName.UnreadMessageCountResponse, On_UnreadMessageCountResponse);//玩家未读消息数量响应
            UEventDispatcher.Instance.AddEventListener(UEventName.ReadMessageResponse, On_ReadMessageResponse);//读取消息响应
            UEventDispatcher.Instance.AddEventListener(UEventName.ReceiveMessageItemsResponse, On_ReceiveMessageItemsResponse);//领取消息附件/删除响应
            UEventDispatcher.Instance.AddEventListener(UEventName.DeleteMessageResponse, On_DeleteMessageResponse);//删除邮件响应
        }

        void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.MessageListResponse, On_MessageListResponse);//玩家消息列表响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.UnreadMessageCountResponse, On_UnreadMessageCountResponse);//玩家未读消息数量响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ReadMessageResponse, On_ReadMessageResponse);//读取消息响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ReceiveMessageItemsResponse, On_ReceiveMessageItemsResponse);//领取消息附件/删除响应
            UEventDispatcher.Instance.RemoveEventListener(UEventName.DeleteMessageResponse, On_DeleteMessageResponse);//删除邮件响应
        }

        void OnEnable()
        {
            //清空
            for (int i = 0; i < leftContent.childCount; i++)
            {
                var go = leftContent.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }

            ClearRightInfo();
            ClearLeftInfo();
            NetMessage.OseeFishing.Req_MessageListRequest();
        }

        private void OnDisable()
        {
            NetMessage.OseeFishing.Req_UnreadMessageCountRequest();
        }

        /// <summary>
        /// 玩家消息列表响应
        /// <summary>
        private void On_MessageListResponse(UEventContext obj)
        {
            var pack = obj.GetData<MessageListResponse>();
            allMessageInfo = pack.messageInfo;
            ClearLeftInfo();
            ClearRightInfo();
            //清空
            for (int i = 0; i < leftContent.childCount; i++)
            {
                var go = leftContent.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }
        

            int isReadAll = 0;
            for (int i = 0; i < pack.messageInfo.Count; i++)
            {
                int n = i;
                var data = pack.messageInfo[i];
                var go = itemPool.Get();
                go.SetActive(true);
                go.transform.SetParent(leftContent, false);
                GameObject goNew = go.transform.Find("imgNew").gameObject;
                GameObject goLook = go.transform.Find("txtLook").gameObject;
                Text txtName = go.transform.Find("txtName").GetComponent<Text>();
                Text txtDate = go.transform.Find("txtDate").GetComponent<Text>();
                GameObject goPrize = go.transform.Find("imgPrize").gameObject;

                if (data.items == null || data.items.Count == 0)
                {
                    goPrize.SetActive(false);     //附件
                }
                else
                {
                    goPrize.SetActive(true);     //附件
                }

                txtName.text = data.title;
                txtDate.text = GameHelper.ConvertJavaTime((double)data.time).ToString("yyyy-MM-dd");
                if (data.receive==true)
                {
                    isReadAll++;
                }
                goNew.SetActive(!data.read);
                goLook.SetActive(data.read);

                //点击获取消息详情
                go.name = data.title.ToString();
                go.name = data.id.ToString();
                go.GetComponent<Button>().onClick.RemoveAllListeners();
                go.GetComponent<Button>().onClick.AddListener(() =>
                {
                    int nIndex = int.Parse(go.name);
                    NetMessage.OseeFishing.Req_ReadMessageRequest(nIndex);
                });
            }
            if (allMessageInfo.Count > 0 && isReadAll< pack.messageInfo.Count)
            {
                btn_allLingqu.gameObject.SetActive(true);
                btn_allDelet.gameObject.SetActive(false);
            }
            else if (allMessageInfo.Count > 0 && isReadAll >= pack.messageInfo.Count)
            {
                btn_allLingqu.gameObject.SetActive(false);
                btn_allDelet.gameObject.SetActive(true);
            }
        }
        /// <summary>
        /// 玩家未读消息数量响应
        /// <summary>
        private void On_UnreadMessageCountResponse(UEventContext obj)
        {
            var pack = obj.GetData<UnreadMessageCountResponse>();
        }
        /// <summary>
        /// 读取消息响应
        /// <summary>
        private void On_ReadMessageResponse(UEventContext obj)
        {
            var pack = obj.GetData<ReadMessageResponse>();
            ClearRightInfo();
            txt_title.text = pack.message.title;
            txt_content.text = pack.message.content.ToString();
            txt_date.text = GameHelper.ConvertJavaTime((double)pack.message.time).ToString("yyyy-MM-dd");
            _nCurIndex = pack.message.id;
            if (pack.message.items != null && pack.message.items.Count > 0)
            {
                for (int i = 0; i < pack.message.items.Count; i++)
                {
                    var data = pack.message.items[i];
                    var go = itemPoolRight.Get();
                    go.SetActive(true);
                    go.transform.SetParent(rightRight, false);
                    go.GetComponent<Image>().sprite = common4.LoadSprite("item/" + data.itemId);//图标
                    go.transform.GetChild(0).GetComponent<Text>().text = data.itemNum.ToString();//个数
                }
                if (pack.message.receive)
                {
                    btn_LingQu.gameObject.SetActive(false);
                    btn_delete.gameObject.SetActive(true);
                }
                else
                {
                    btn_LingQu.gameObject.SetActive(true);
                    btn_delete.gameObject.SetActive(false);
                }
            }
            else
            {
                btn_LingQu.gameObject.SetActive(false);
                btn_delete.gameObject.SetActive(true);
            }
            ChangeLeftInfo();
        }
        /// <summary>
        /// 领取消息附件
        /// <summary>
        private void On_ReceiveMessageItemsResponse(UEventContext obj)
        {
            var pack = obj.GetData<ReceiveMessageItemsResponse>();
            if (pack.result)
            {
                //NetMessage.OseeFishing.Req_ReadMessageRequest(_nCurIndex);
                ClearRightInfo();
                ClearLeftInfo();
                NetMessage.OseeFishing.Req_MessageListRequest();
            }
        }
        private void On_DeleteMessageResponse(UEventContext obj)
        {
            var pack = obj.GetData<DeleteMessageResponse>();
        }
        void OnLingQuMail()
        {
            NetMessage.OseeFishing.Req_ReceiveMessageItemsRequest(_nCurIndex);
            //ClearLeftInfo();
            //ClearRightInfo();
        }
        void OnDeleteMail()
        {
            NetMessage.OseeFishing.Req_DeleteMessageRequest(_nCurIndex);
            ClearLeftInfo();
            ClearRightInfo();
        }

        //清除邮件详情
        void ClearRightInfo()
        {
            _nCurIndex = -1;
            txt_title.text = "";
            txt_content.text = "";
            txt_date.text = "";
            btn_delete.gameObject.SetActive(false);
            btn_LingQu.gameObject.SetActive(false);
            for (int i = 0; i < rightRight.childCount; i++)
            {
                var go = rightRight.GetChild(i);
                itemPoolRight.Recycle(go.gameObject);
            }
        }
        //清除邮件左边详情
        void ClearLeftInfo()
        {
            foreach (Transform item in leftContent)
            {
                if (item.name == _nCurIndex.ToString())
                {
                    itemPool.Recycle(item.gameObject);
                }
            }
        }
        //更改邮件左边详情
        void ChangeLeftInfo()
        {
            foreach (Transform item in leftContent)
            {
                if (item.name == _nCurIndex.ToString())
                {
                    item.transform.Find("txtLook").gameObject.SetActive(true);
                    item.transform.Find("imgNew").gameObject.SetActive(false);
                }
            }
        }

        ///// <summary>
        ///// 返回获取消息列表
        ///// <summary>
        //private void On_GetMessagesResponse(UEventContext obj)
        //{
        //    var pack = obj.GetData<GetMessagesResponse>();
        //    ClearRightInfo();
        //    //清空
        //    for (int i = 0; i < grid.childCount; i++)
        //    {
        //        var go = grid.GetChild(i);
        //        itemPool.Recycle(go);
        //    }


        //    for(int i=0;i<pack.msgs.Count;i++)
        //    {
        //        int n = i;
        //        var data = pack.msgs[i];
        //        var go = itemPool.Get();
        //        go.SetActive(true);
        //        go.transform.SetParent(grid, false);
        //        GameObject goNew = go.transform.Find("imgNew").gameObject;
        //        GameObject goLook = go.transform.Find("txtLook").gameObject;
        //        Text txtName = go.transform.Find("txtName").GetComponent<Text>();
        //        Text txtDate = go.transform.Find("txtDate").GetComponent<Text>();
        //        GameObject goPrize = go.transform.Find("imgPrize").gameObject;

        //        goPrize.SetActive(data.appendix);     //附件
        //        txtName.text = data.playerName;
        //        txtDate.text = GameHelper.ConvertJavaTime((double)data.time).ToString("yyyy-MM-dd");
        //        goNew.SetActive(!data.read);
        //        goLook.SetActive(data.read);

        //        //点击获取消息详情
        //        go.name = data.index.ToString();
        //        UIEventListener.Get(go).onClick = (a, b) => 
        //        {
        //            int nIndex = int.Parse(a.name);
        //            NetMessage.Game.Req_ReadMessageRequest(nIndex);
        //        };
        //    }
        //}
        ///// <summary>
        ///// 返回获取消息详情
        ///// <summary>
        //private void On_ReadMessageResponse(UEventContext obj)
        //{
        //    ClearRightInfo();
        //    var pack = obj.GetData<ReadMessageResponse>();
        //    //txt_title.text = pack.title;
        //    txt_content.text = (pack.title+"\n"+ pack.content).ToString();
        //    txt_date.text = GameHelper.ConvertJavaTime((double)pack.time).ToString("yyyy-MM-dd");
        //    _nCurIndex = pack.index;
        //    if(pack.items.Count>0)
        //    {
        //        for(int i=0;i<pack.items.Count;i++)
        //        {
        //            var data = pack.items[i];
        //            var go = itemPoolRight.Get();
        //            go.SetActive(true);
        //            go.transform.SetParent(gridRight, false);
        //            go.GetComponent<Image>().sprite = common4.LoadSprite("item/" + data.itemId);//图标
        //            go.transform.GetChild(0).GetComponent<Text>().text = data.count.ToString();//个数
        //        }
        //        btn_deleteLing.gameObject.SetActive(true);
        //        btn_delete.gameObject.SetActive(false);
        //    }
        //    else
        //    {
        //        btn_deleteLing.gameObject.SetActive(false);
        //        btn_delete.gameObject.SetActive(true);
        //    }  
        //}

    }
}