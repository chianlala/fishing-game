using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using GameFramework;

public class ArtNumZeng : MonoBehaviour
{
    private GameObjectPool itemPool = new GameObjectPool();
    public GameObject item;
    public bool bConst = false;
    public long Num = 0;//当前显示数值
    public bool bBestFit = false;
    public int minSize = 2, maxSize = 100;

    private bool bAwake = false;
    private Vector2 fStartSize = Vector2.zero;
    private Sprite[] AllSprite;
    void Awake()
    {
        //fStartSize = this.GetComponent<RectTransform>().sizeDelta;
        this.GetComponent<ContentSizeFitter>().enabled = !bBestFit;

        if (bAwake)
        {
            return;
        }
        if (bConst)
            return;
        if (item == null)
            item = this.transform.GetChild(0).gameObject;
        itemPool.SetTemplete(item);
        itemPool.Recycle(item);
        bAwake = true;
    }

    void OnDestroy()
    {
        itemPool = null;
    }

    /// <summary>
    /// 初始化美术字 0-9 + -
    /// </summary>
    /// <param name="strType">路径</param>
    /// <param name="num">数字</param>
    /// <param name="bShowAddSub">是否显示加减符号</param>
    /// <param name="isShowAni">是否显示动画</param>
    /// <param name="bSetNativeSize">是否调整为数字像素大小</param>
    /// <param name="bFlash">是否闪烁数字</param>
    public void Init(string strType, long num, bool bShowAddSub = false, bool isShowAni = false, bool bSetNativeSize = false, bool bFlash = false)
    {
        if (AllSprite == null)
        {
            AllSprite = Resources.LoadAll<Sprite>(strType);
        }

        if (!bAwake)
        {
            Awake();
        }
        Num = num;
        //清空
        for (int i = 0; i < this.transform.childCount; i++)
        {
            var go = this.transform.GetChild(i);
            itemPool.Recycle(go.gameObject);
        }

        float cellX = 0;
        if (bBestFit)
        {
            int nWeishu = GetWeishu(num);
            if (bShowAddSub)
            {
                nWeishu++;
            }
            if (nWeishu * maxSize < fStartSize.x)
            {
                //没超过上限
                this.GetComponent<RectTransform>().sizeDelta = new Vector2(nWeishu * maxSize, fStartSize.y);
                cellX = maxSize;
            }
            else
            {
                this.GetComponent<RectTransform>().sizeDelta = fStartSize;
                //超过上限
                float w = fStartSize.x / nWeishu;
                cellX = Mathf.Clamp(w, minSize, maxSize);
            }
        }

        //是否显示加减符号
        if (bShowAddSub)
        {
            var go = itemPool.Get();
            go.SetActive(true);
            go.transform.SetParent(this.transform, false);
            go.transform.SetAsFirstSibling();
            if (num > 0)
            {
                using (zstring.Block())
                {
                    go.GetComponent<Image>().sprite = AllSprite[11];
                }
            }
            else
            {
                using (zstring.Block())
                {
                    go.GetComponent<Image>().sprite = AllSprite[12];
                }
            }
            if (bBestFit)
            {
                BestFitImageWith(cellX, go);
            }
            else if (bSetNativeSize)
                go.GetComponent<Image>().SetNativeSize();
        }

        if (num < 0)
            num = -num;

        if (num == 0)
        {
            var go = itemPool.Get();
            go.SetActive(true);
            go.transform.SetParent(this.transform, false);
            go.transform.SetSiblingIndex(bShowAddSub ? 1 : 0);
            using (zstring.Block())
            {
                go.GetComponent<Image>().sprite = AllSprite[0]; ;
            }
            if (bBestFit)
            {
                BestFitImageWith(cellX, go);
            }
            else if (bSetNativeSize)
                go.GetComponent<Image>().SetNativeSize();
        }
        else
        {
            while (num > 0)
            {
                var go = itemPool.Get();
                go.SetActive(true);
                go.transform.SetParent(this.transform, false);
                go.transform.SetSiblingIndex(bShowAddSub ? 1 : 0);
                using (zstring.Block())
                {
                    go.GetComponent<Image>().sprite = AllSprite[num % 10];
                }

                if (bBestFit)
                {
                    BestFitImageWith(cellX, go);
                }
                else if (bSetNativeSize)
                    go.GetComponent<Image>().SetNativeSize();

                num /= 10;
            }
        }
        //一个动画效果
        if (isShowAni)
        {
            this.transform.DOScale(2, 0.2f).OnComplete(() => { this.transform.DOScale(1, 0.5f); });
        }

        if (bFlash)
        {
            for (int i = 0; i < this.transform.childCount; i++)
            {
                int n = i;
                transform.GetChild(n).GetComponent<Image>().DOFade(0.6f, 0.25f).OnComplete(() =>
                {
                    transform.GetChild(n).GetComponent<Image>().DOFade(1, 0.25f);
                });
            }
        }
    }

    /// <summary>
    /// 获取传入整数的位数 
    /// </summary>
    /// <param name="num"></param>
    /// <returns>位数</returns>
    private int GetWeishu(long num)
    {
        if (num == 0)
            return 1;

        int nWei = 0;
        while (num > 0)
        {
            num /= 10;
            nWei++;
        }
        return nWei;
    }

    private void BestFitImageWith(float cellX, GameObject go)
    {
        if (go.GetComponent<Image>() != null && go.GetComponent<Image>().sprite != null)
        {
            float rate = (float)go.GetComponent<Image>().sprite.texture.width / go.GetComponent<Image>().sprite.texture.height;
            go.GetComponent<RectTransform>().sizeDelta = new Vector2(cellX, cellX / rate);
        }
    }
}