using com.maple.game.osee.proto;
using com.maple.game.osee.proto.lobby;
using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class UIKefu : MonoBehaviour
    {
        public Button btn_close;
        public Button btn_ok;
        public Text txt_weixin;
        public Image img_2weima;
        public InputField inputMessage;

        Transform TransformFind(string str)
        {
            var tmp = this.transform.Find(str);
            if (tmp == null)
            {
                Debug.LogError("未找到路径：" + str);
                return null;
            }
            else
            {
                return tmp;
            }
        }
        private void Awake()
        {

            btn_close = TransformFind("bg/btn_close").GetComponent<Button>();
            btn_ok = TransformFind("bg/btn_ok").GetComponent<Button>();
            txt_weixin = TransformFind("bg/txt_weixin").GetComponent<Text>();
            img_2weima = TransformFind("bg/img_2weima").GetComponent<Image>();
            inputMessage = TransformFind("bg/inputMessage").GetComponent<InputField>();

            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIKefu); SoundLoadPlay.PlaySound("sd_t3_ui_cmn_close"); });
            btn_ok.onClick.AddListener(() =>
            {
                //SDK.onCopy(txt_weixin.text);
                //UIMgr.CloseUI(UIPath.UIKefu);
                if (inputMessage.text == "")
                {
                    MessageBox.Show("输入为空，无法发送");
                    return;
                }
                NetMessage.OseeLobby.Req_FeedBackRequest((int)PlayerData.PlayerId, inputMessage.text);
                inputMessage.text = "";
                UIMgr.CloseUI(UIPath.UIKefu);
            });
            inputMessage.onEndEdit.AddListener((arg) =>
            {

            });
            UEventDispatcher.Instance.AddEventListener(UEventName.ServiceWechatResponse, On_ServiceWechatResponse);//客服微信返回

            UEventDispatcher.Instance.AddEventListener(UEventName.FeedBackResponse, On_FeedBackResponse);//添加用户反馈响应

        }

        private void OnDestroy()
        {
            UEventDispatcher.Instance.RemoveEventListener(UEventName.ServiceWechatResponse, On_ServiceWechatResponse);//客服微信返回

            UEventDispatcher.Instance.RemoveEventListener(UEventName.FeedBackResponse, On_FeedBackResponse);//添加用户反馈响应

        }

        private void OnEnable()
        {
            NetMessage.OseeLobby.Req_ServiceWechatRequest();
        }

        /// <summary>
        /// 客服微信返回
        /// <summary>
        private void On_ServiceWechatResponse(UEventContext obj)
        {
            var pack = obj.GetData<ServiceWechatResponse>();
            //txt_weixin.text = pack.wechat;
            //UIHelper.GetHttpImage(pack.qrcode, (sp) => {
            //    img_2weima.sprite = sp;
            //    float rate = (float)img_2weima.sprite.texture.texelSize.x/ img_2weima.sprite.texture.texelSize.y;
            //    img_2weima.rectTransform.sizeDelta = new Vector2(img_2weima.rectTransform.sizeDelta.x, img_2weima.rectTransform.sizeDelta.x * rate);
            //});
        }
        // <summary>
        /// 添加用户反馈响应
        /// <summary>
        private void On_FeedBackResponse(UEventContext obj)
        {
            var pack = obj.GetData<FeedBackResponse>();
            if (pack.tool)
            {
                MessageBox.Show("反馈成功，感谢你的反馈");
            }
            else
            {
                MessageBox.Show("发送失败");
            }
        }
    }
}