using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using System;
using UnityEngine.Scripting;

[Preserve]
public class Amazing : MonoBehaviour {
    public ArtNum num_gold;
    private long dieMoney;
    private int pos; 
    
    public void Show(Action callback=null)
    {
        //if (common.IDRoomFish==0)
        //{
        //    this.transform.DOScale(0.8f, 0.5f).SetEase(Ease.OutBack);
        //    this.transform.DOMove(UIByRoomMain.instance.objPlayer[pos].objPaotai.transform.position, 0.5f).SetDelay(3);
        //    this.transform.DOScale(0, 0.5f).SetDelay(3f).OnComplete(() =>
        //    {
        //        //Destroy(this.gameObject);
        //        UIByRoomMain.instance.itemPool_zhuanfanle.Recycle(this.gameObject);
        //        if (callback != null)
        //            callback();
        //    });
        //}
        //else
        //{
            this.transform.DOScale(0.8f, 0.5f).SetEase(Ease.OutBack);
            //this.transform.DOMove(UIByChange.instance.objPlayer[pos].objPaotai.transform.position, 0.5f).SetDelay(3);
            this.transform.DOScale(0, 0.5f).SetDelay(3f).OnComplete(() =>
            {
                //Destroy(this.gameObject);
                //UIByChange.instance.itemPool_DragonShui.Recycle(this.gameObject);
                if (callback != null)
                    callback();
            });
        //}
        
    }
//    public void OnEnable()
//    {
//#if UNITY_EDITOR
//        AssetBundle ab = AssetBundle.LoadFromFile(Application.dataPath + "/" + "HotAssets/AssetBundles/" + LuaConst.platform + "/" + "TxPacker/".ToLower() + "Number".ToLower() + ".ab");
//#else
//         AssetBundle ab = AssetBundle.LoadFromFile(LuaConst.localCommomPath + "/TxPacker/".ToLower() + "Number".ToLower() + ".ab");
//#endif

//        Sprite[] allsp = ab.LoadAllAssets<Sprite>();
//        for (int i = 0; i < allsp.Length; i++)
//        {
//            common.ListNum.Add(allsp[i].name, allsp[i]);
//        }
//      //  UIRoot.Instance.StartCoroutines(UIMgr.Unload(ab));
//        Init(12345999, 0);
//    }
    public void Init(long _dieMoney, int _pos, Action callback = null)
    {
        dieMoney = _dieMoney;
        pos = _pos;
        if(dieMoney>0)
            num_gold.Init("ziti", dieMoney, isShowAni: true);
        this.transform.localScale = Vector3.zero;
        Show(callback);
    }
}
