using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class UIGuangbo : MonoBehaviour
    {
        private List<string> m_listMsg= new List<string>();
        private bool bRoll = false;//是否正在滚动
        public Text txt_gonggao;

        void Awake()
        {
            txt_gonggao= this.transform.Find("Image_Notice/Viewport/Content/txt_gonggao").GetComponent<Text>();
            //m_listMsg = new List<string>();
            txt_gonggao.text = "";
        }

        public void Init(string strMsg, int level)
        {
            if (level == 0)
            {
                m_listMsg.Insert(0, strMsg);
            }
            else
            {
                m_listMsg.Add(strMsg);
            }


        }

        void Update()
        {
            if (bRoll)
            {
                txt_gonggao.rectTransform.anchoredPosition -= new Vector2(100 * Time.deltaTime, 0);
                if (txt_gonggao.rectTransform.anchoredPosition.x < -txt_gonggao.rectTransform.rect.width)
                {
                    bRoll = false;
                }

            }
            else
            {
                if (m_listMsg.Count > 0)
                {
                    if (!bRoll)
                    {
                        bRoll = true;
                        txt_gonggao.text = m_listMsg[0];
                        m_listMsg.RemoveAt(0);
                        txt_gonggao.rectTransform.anchoredPosition = new Vector2(txt_gonggao.transform.parent.parent.GetComponent<RectTransform>().rect.width, 0);
                    }
                }
                else
                {
                    UIMgr.CloseUI(UIPath.UIGuangbo);
                }
            }
        }
    }
}