using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;
using DG.Tweening;
using com.maple.game.osee.proto.fruit;
using com.maple.game.osee.proto;


namespace Game.UI
{
    public class UIXinYunShiKe : MonoBehaviour
    {
        public void OnEnable()
        {

        }
        public void XinYunShiKe(int m)
        {
            if (m == 1)
            {
                this.transform.Find("bg/xiaomali").gameObject.SetActive(true);
                this.transform.Find("bg/kejin").gameObject.SetActive(false);
                this.transform.Find("bg/lwbz").gameObject.SetActive(false);
            }
            else if (m == 2)
            {
                this.transform.Find("bg/xiaomali").gameObject.SetActive(false);
                this.transform.Find("bg/kejin").gameObject.SetActive(true);
                this.transform.Find("bg/lwbz").gameObject.SetActive(false);
            }
            else if (m == 3)
            {
                this.transform.Find("bg/xiaomali").gameObject.SetActive(false);
                this.transform.Find("bg/kejin").gameObject.SetActive(false);
                this.transform.Find("bg/lwbz").gameObject.SetActive(true);
            }
        }
    }
}