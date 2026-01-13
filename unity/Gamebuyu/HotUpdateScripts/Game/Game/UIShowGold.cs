using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;

namespace Game.UI
{
    public class UIShowGold : MonoBehaviour
    {
        public GameObject item;
        GameObjectPool pool = new GameObjectPool();
        private int nCount = 0;
        private Transform[] itemShowGold=new Transform[4];
        void Awake()
        {
            item = transform.Find("Text").gameObject;
            for (int i = 0; i < 4; i++)
            {
                itemShowGold[i] = transform.Find(i.ToString());
            }
     
            pool.SetTemplete(item);
            pool.Recycle(item);

            startColor = item.GetComponent<Text>().color;
            endColor = new Color(startColor.r, startColor.g, startColor.b, 0f);
        }
        void OnEnable()
        {
            for (int i = 0; i < itemShowGold.Length; i++)
            {
                foreach (Transform item in itemShowGold[i].transform)
                {
                    pool.Recycle(item.gameObject);
                }
            }
        }
        Color endColor;
        Color startColor;

  
        public void ShowMessage(string vartext,int ngdseat)
        {
            var go = pool.Get();
            go.GetComponent<Text>().text = vartext;
            Text text = go.GetComponent<Text>();
            go.SetActive(true);
            
            go.transform.SetParent(itemShowGold[ngdseat], false);
            go.transform.transform.GetComponent<Text>().color = startColor;
            go.transform.position = itemShowGold[ngdseat].position;

            //上下 方向不同
            if (ngdseat < 2)
            { 
                //在下方
                go.transform.DOLocalMove(new Vector3(0, 50f, 0), 1f).SetEase(Ease.Flash).SetDelay(0.2f).OnComplete(() =>
                {
                    ShowItem(go);
                    text.DOColor(endColor, 1f);
                });
            }
            else 
            {
                go.transform.DOLocalMove(new Vector3(0, -50f, 0), 1f).SetEase(Ease.Flash).SetDelay(0.2f).OnComplete(() =>
                {
                    ShowItem(go);
                    text.DOColor(endColor, 1f);
                });
            }
        }

        void ShowItem(GameObject it)
        {
            //缩小并回收
            it.transform.DOScaleY(1, 0.5f).SetDelay(2).OnComplete(() =>
            {
                pool.Recycle(it);
            });
        }
    }
}
