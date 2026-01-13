
using System.Collections;

using DG.Tweening;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class UIPopMessage : MonoBehaviour
    {
        public GameObject item;
        GameObjectPool pool = new GameObjectPool();
        private int nCount = 0;

        void Awake()
        {
            item=this.transform.Find("Image").gameObject;
            pool.SetTemplete(item);
            pool.Recycle(item);
        }

        public void ShowMessage(string text)
        {
            var go = pool.Get();
            go.SetActive(true);
            go.transform.SetParent(this.transform, false);
            go.transform.localPosition = new Vector3(0, 50 * nCount, 0);
            go.transform.GetChild(0).GetComponent<Text>().text = text;
            go.name = nCount.ToString();
            ShowItem(go);
            nCount++;
        }

        void ShowItem(GameObject it)
        {

            it.transform.localScale = new Vector3(1, 0, 1);
            it.transform.DOScaleY(1.2f, 0.5f).SetEase(Ease.InOutCirc).OnComplete(() => { it.transform.DOScaleY(1f, 0.5f).SetEase(Ease.InOutCirc); });
            it.transform.GetChild(0).transform.localPosition = new Vector3(1280, 0, 0);
            it.transform.GetChild(0).transform.DOLocalMoveX(0, 1f);
            it.transform.DOScaleY(0, 0.5f).SetDelay(2);
            it.transform.GetChild(0).transform.DOLocalMoveX(-Screen.width, 1f).SetDelay(2f).OnComplete(() =>
            {
                nCount--;
                pool.Recycle(it);
                MoveDown();
            });
        }

        public void MoveDown()
        {
            //剩余物体下移
            for (int i = 0; i < this.transform.childCount; i++)
            {
                if (transform.GetChild(i).gameObject.activeSelf)
                {
                    int n = int.Parse(this.transform.GetChild(i).name);
                    this.transform.GetChild(i).name = (n - 1).ToString();
                    transform.GetChild(i).DOLocalMoveY((n - 1) * 50, 0.5f);
                }
            }
        }

    }

}