using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{
    public class UIRenzheng : MonoBehaviour
    {
        public Button btn_close;
        public InputField input_name, input_shenfenzheng;
        public Button btn_ok;

        //------------------view_renzhengOK----------
        public GameObject view_ok;
        public Button btn_okClose;
        public GameObject item;
        public Transform grid;
        GameObjectPool itemPool = new GameObjectPool();

        private void Awake()
        {
            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            input_name = this.transform.Find("bg/bg_name/input_name").GetComponent<InputField>();
            input_shenfenzheng = this.transform.Find("bg/bg_shenfenzheng/input_shenfenzheng").GetComponent<InputField>();
            btn_ok = this.transform.Find("bg/btn_ok").GetComponent<Button>();
            view_ok = this.transform.Find("view_ok").gameObject;
            btn_okClose = this.transform.Find("view_ok/bg/btn_ok").GetComponent<Button>();
            item = this.transform.Find("view_ok/bg/root_reward/bg_item").gameObject;
            grid = this.transform.Find("view_ok/bg/root_reward");

            itemPool.SetTemplete(item);
            itemPool.Recycle(item);

            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIRenzheng); });
            //btn_getYanzheng.onClick.AddListener(() => 
            //{
            //    if(input_phone.text.Trim()!="")
            //    {
            //        NetMessage.OseeLobby.Req_AuthenticatePhoneCheckRequest(input_phone.text.Trim());
            //    }
            //    else
            //    {
            //        MessageBox.ShowPopMessage("请输入手机号");
            //    }
            //});
            btn_ok.onClick.AddListener(() =>
            {
                if (input_name.text.Trim() != "" && input_shenfenzheng.text.Trim() != "")
                {
                    NetMessage.OseeLobby.Req_SubmitAuthenticateRequest(input_name.text, input_shenfenzheng.text);
                }
                else
                {
                    MessageBox.ShowPopMessage("请正确输入");
                }
            });
            btn_okClose.onClick.AddListener(() => { view_ok.SetActive(false); });
            UEventDispatcher.Instance.AddEventListener(UEventName.AuthenticatePhoneCheckResponse, On_AuthenticatePhoneCheckResponse);//实名认证手机验证请求
            UEventDispatcher.Instance.AddEventListener(UEventName.SubmitAuthenticateResponse, On_SubmitAuthenticateResponse);//提交实名认证信息返回
        }

        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.AuthenticatePhoneCheckResponse, On_AuthenticatePhoneCheckResponse);//实名认证手机验证请求
            UEventDispatcher.Instance.RemoveEventListener(UEventName.SubmitAuthenticateResponse, On_SubmitAuthenticateResponse);//提交实名认证信息返回
        }

        private void OnEnable()
        {
            view_ok.SetActive(false);
        }

        /// <summary>
        /// 实名认证手机验证请求
        /// <summary>
        private void On_AuthenticatePhoneCheckResponse(UEventContext obj)
        {
            var pack = obj.GetData<AuthenticatePhoneCheckResponse>();
            string str = pack.result ? "成功" : "失败";
            MessageBox.ShowPopMessage("发送验证码" + str);
        }
        /// <summary>
        /// 提交实名认证信息返回
        /// <summary>
        private void On_SubmitAuthenticateResponse(UEventContext obj)
        {
            var pack = obj.GetData<SubmitAuthenticateResponse>();
            view_ok.SetActive(true);
            //清空
            for (int i = 0; i < grid.childCount; i++)
            {
                var go = grid.GetChild(i);
                itemPool.Recycle(go.gameObject);
            }
            if (pack.rewards.Count > 0)
            {
                for (int i = 0; i < pack.rewards.Count; i++)
                {
                    var data = pack.rewards[i];
                    var go2 = itemPool.Get();
                    go2.SetActive(true);
                    go2.transform.SetParent(grid, false);
                    go2.transform.Find("img_item").GetComponent<Image>().sprite = common4.LoadSprite("item/" + data.itemId);
                    go2.transform.Find("txt_item").GetComponent<Text>().text = "x" + data.itemNum;
                }
            }
        }
    }
}