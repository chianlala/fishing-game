using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;

namespace Game.UI
{
    public class UIPopOneMsg : MonoBehaviour
    {
        public GameObject item;
        //GameObjectPool pool = new GameObjectPool();
        //private int nCount = 0;

        void Awake()
        {
            item = transform.Find("Image").gameObject;
            //pool.SetTemplete(item);
            //pool.Recycle(item);
        }

        public void ShowMessage(string text)
        {
            StopCoroutine(ShowThree(text));
            StartCoroutine(ShowThree(text));
        }


        IEnumerator ShowThree(string text)
        {
            item.SetActive(true);
            item.transform.GetChild(0).gameObject.SetActive(true);
            item.transform.GetChild(0).GetComponent<Text>().text = text;
            yield return new WaitForSeconds(2f);
            item.gameObject.SetActive(false);
        }

    }
}
