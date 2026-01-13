using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
using Game.UI;
using JEngine.Core;
using libx;
using ProtoBuf;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;
using UnityEngine;

namespace Game.UI
{
    /// <summary>
    /// JEngine's UI
    /// </summary>
    public class commonLoad
    {
        public static GameObjectPool[] _AllShowBulletPool = new GameObjectPool[16]; 
        public static Dictionary<int, HotGameObjectPool> _AllBulletPool = new Dictionary<int, HotGameObjectPool>();

        public static Dictionary<int, HotGameObjectPool>  _AllFishPool = new Dictionary<int, HotGameObjectPool>();
      

        public static Dictionary<int, HotGameObjectPool> _AllWangPool = new Dictionary<int, HotGameObjectPool>();

        public static GameObjectPool _AllDieMoney = new GameObjectPool();
        public static GameObjectPool _AllDieMoneyOther = new GameObjectPool(); 

        public static GameObjectPool _BigFishDieMoney= new GameObjectPool(); 
        public static GameObjectPool[] _AllDieDragonScale = new GameObjectPool[2];


        public static Dictionary<string, GameObjectPool> _AllDicPool = new Dictionary<string, GameObjectPool>();
        public static List<GameObject> _AllDieMoneyPerfect = new List<GameObject>();

        public static void InitThis() {  
     
        }
     
        /// <summary>
        /// 子弹
        /// </summary>
        /// <param name="index"></param>
        /// <returns></returns>
        public static GameObject  GetOneBullet(int index) 
        {
            if (_AllBulletPool.ContainsKey(index))
            {
                if (_AllBulletPool[index] == null)
                {
                    _AllBulletPool[index] = new HotGameObjectPool();
                }
                if (_AllBulletPool[index].IsTemplete() == false)
                {
                    _AllBulletPool[index].SetTemplete(common4.LoadPrefab("Bullet/bullet_" + index.ToString()));
                }
            }
            else
            {
                _AllBulletPool.Add(index, new HotGameObjectPool());
                if (_AllBulletPool[index].IsTemplete() == false)
                {
                    _AllBulletPool[index].SetTemplete(common4.LoadPrefab("Bullet/bullet_" + index.ToString()));
                }
            }
       

            return _AllBulletPool[index].Get();
        }

        /// <summary>
        /// 异步加载鱼
        /// </summary>
        /// <param name="modleid"></param>
        /// <param name="Pos"></param>
        /// <param name="complete"></param>
        public static GameObject GetOneAynsFish(int modleid, Transform Pos)
        {

                if (_AllFishPool.ContainsKey(modleid))
                {
                    if (_AllFishPool[modleid] == null)
                    {
                        _AllFishPool[modleid] = new HotGameObjectPool();
                    }
                }
                else
                {
                    _AllFishPool.Add(modleid, new HotGameObjectPool());
                }
                if (_AllFishPool[modleid].IsTemplete() == false)
                {
                    var afish = common4.LoadPrefab("Fish/" + modleid);
                    _AllFishPool[modleid].SetTemplete(afish);
                    return _AllFishPool[modleid].Get(Pos);
                }
                else
                {
                    return _AllFishPool[modleid].Get(Pos);
                }
           
        }
        /// <summary>
        /// 鱼网
        /// </summary>
        /// <param name="index"></param>
        /// <param name="Pos"></param>
        /// <returns></returns>
        public static GameObject GetOneWang(int index, Transform Pos)
        {
            if (_AllWangPool.ContainsKey(index))
            {
                if (_AllWangPool[index] == null)
                {
                    _AllWangPool[index] = new HotGameObjectPool();
                }
                if (_AllWangPool[index].IsTemplete() == false)
                {
                    _AllWangPool[index].SetTemplete(common4.LoadPrefab("Wang/net_" + index.ToString()));
                }
            }
            else
            {
                _AllWangPool.Add(index, new HotGameObjectPool()); 
                if (_AllWangPool[index].IsTemplete() == false)
                {
                    _AllWangPool[index].SetTemplete(common4.LoadPrefab("Wang/net_" + index.ToString()));
                }
            }
            return _AllWangPool[index].Get(Pos);
        }
        /// <summary>
        /// 异步加载鱼
        /// </summary>
        /// <param name="modleid"></param>
        /// <param name="Pos"></param>
        /// <param name="complete"></param>
        public static void GetOneAnysFish(int modleid, long fwq_id, Transform Pos, TmpFishingFishInfoProto tmpFishInfo)
        {
            if (_AllFishPool.ContainsKey(modleid))
            {
                if (_AllFishPool[modleid] == null)
                {
                    _AllFishPool[modleid] = new HotGameObjectPool();
                }
            }
            else
            {
                _AllFishPool.Add(modleid, new HotGameObjectPool());
            }
            if (_AllFishPool[modleid].IsTemplete() == false)
            {
                var afish = common4.LoadAynsPrefab("Fish/" + modleid);
                float kuangbao_connter = Time.realtimeSinceStartup;
                afish.completed += ((targ) => {
                    if (targ.isDone == true)
                    {
                        _AllFishPool[modleid].SetTemplete((GameObject)afish.asset);
                        float mLoadTime = Time.realtimeSinceStartup - kuangbao_connter;
                        if (common.listFish.ContainsKey(fwq_id))
                        {
                            //判断是否存在此鱼 存在则不刷新
                            if (common.listFish[fwq_id] == null)
                            {
                                common.listFish.Remove(fwq_id);
                            }
                            else
                            {
                                common.listFish[fwq_id].gameObject.SetActive(false);
                                common.listFish[fwq_id].ThisOnlyRecycle();
                            }

                            var goFish = _AllFishPool[modleid].Get(Pos);
                            goFish.transform.localPosition = new Vector3(1000f, 1000f, 1000f);
                            goFish.SetActive(true);
                            var mf = goFish.GetComponent<fish>();
                            if (mf != null)
                            {
                                tmpFishInfo.clientLifeTime += mLoadTime;
                                mf.Init(tmpFishInfo);
                            }
                        }
                        else
                        {
                            var goFish = _AllFishPool[modleid].Get(Pos);
                            goFish.transform.localPosition = new Vector3(1000f, 1000f, 1000f);
                            goFish.SetActive(true);
                            var mf = goFish.GetComponent<fish>();
                            if (mf != null)
                            {
                                mf.Init(tmpFishInfo);
                            }
                        }
                    }
                });
            }
            else
            {
                var sgoFish = _AllFishPool[modleid].Get(Pos);
                sgoFish.SetActive(true);
                var mf = sgoFish.GetComponent<fish>();
                if (mf != null)
                {
                    mf.Init(tmpFishInfo);
                }
            }
        }
        public static GameObject GetOneDieMoney(Transform Pos) 
        {
            //if (_AllDieMoney.Length > index)
            //{
            //    if (_AllDieMoney[index] == null) 
            //    {
            //        _AllDieMoney[index] = new GameObjectPool();
            //    }
            //}
        
            if (_AllDieMoney.IsTemplete() == false)
            {
                _AllDieMoney.SetTemplete(common4.LoadPrefab("BuyuPrefabs/prefabDieMoney0"));
                // _AllDieMoney[index].SetTemplete(common4.LoadPrefab("BuyuPrefabs/prefabDieMoney" + index.ToString()));
            }
            return _AllDieMoney.Get(Pos);
        }
        public static GameObject GetOneDieOther(Transform Pos)
        { 
            if (_AllDieMoneyOther.IsTemplete() == false)
            {
                _AllDieMoneyOther.SetTemplete(common4.LoadPrefab("BuyuPrefabs/prefabDieMoney0"));
                // _AllDieMoney[index].SetTemplete(common4.LoadPrefab("BuyuPrefabs/prefabDieMoney" + index.ToString()));
            }
            return _AllDieMoneyOther.Get(Pos);
        }
        public static GameObject GetOneDicPool(string abPath, Transform Pos) 
        {             
            if (_AllDicPool.ContainsKey(abPath)) 
            {
                if (_AllDicPool[abPath].IsTemplete())
                {
                    return _AllDicPool[abPath].Get(Pos);
                }
                else
                {
                    _AllDicPool[abPath].SetTemplete(common4.LoadPrefab(abPath));
                    return _AllDicPool[abPath].Get(Pos);
                }
            }
            else
            {
                var mPool = new GameObjectPool();
                mPool.SetTemplete(common4.LoadPrefab(abPath));
                _AllDicPool.Add(abPath, mPool);
            }
            
            return _AllDicPool[abPath].Get(Pos);
        }
        public static void ReciveOneDic(string abPath, GameObject go)
        {
            _AllDicPool[abPath].Recycle(go);
        }
        public static GameObject GetOneDragonScale(int index, Transform Pos)
        {
            if (_AllDieDragonScale.Length > index)
            { 
                if (_AllDieDragonScale[index] == null)
                {
                    _AllDieDragonScale[index] = new GameObjectPool();
                }
            }
            if (_AllDieDragonScale[index].IsTemplete() == false)
            {
                _AllDieDragonScale[index].SetTemplete(common4.LoadPrefab("BuyuPrefabs/dropdragon" + index.ToString()));
            }

            return _AllDieDragonScale[index].Get(Pos);
        }
        public static void ReciveOneBullet(int index,GameObject go) 
        {
            _AllBulletPool[index].Recycle(go);
        }
        public static void ReciveOneFish(int index, GameObject go)
        {
            _AllFishPool[index].Recycle(go);
        }
        public static void ReciveOneWang(int index, GameObject go)
        {
            //Debug.Log("回收网");
            if (_AllWangPool.ContainsKey(index))
            {
                _AllWangPool[index].Recycle(go);

            }
            else
            {
                //Debug.Log("不存在"+ index);
            }
          
        }
        public static void ReciveDieMoney(int index, GameObject go) {
            if (go!=null)
            {
                go.gameObject.SetActive(false);
                _AllDieMoney.Recycle(go);
            }
        }
        public static void ReciveDieMoneyOther(GameObject go)
        {
            _AllDieMoneyOther.Recycle(go); 
        }
        public static void ReciveDragonScale(int index, GameObject go)
        {
            _AllDieDragonScale[index].Recycle(go);
        }



        ////音效
        //public static Sprite GetOneFishSprite(int index)
        //{

        //    return common4.LoadSprite("FishIcon/" + index);
        //}
        //public static AudioClip GetOneWavAudioClip(int index)
        //{

        //    return common4.LoadAudioClip("Sound/PaoFire/" + index);
        //}
        //public static GameObject GetOneBullet(int index,Transform parent)
        //{
        //    if (_AllBulletPool.Length > index)
        //    {
        //        if (_AllBulletPool[index] == null)
        //        {
        //            _AllBulletPool[index] = new GameObjectPool();
        //        }
        //    }
        //    if (_AllBulletPool[index].IsTemplete() == false)
        //    {
        //        _AllBulletPool[index].SetTemplete(common4.LoadPrefab("Bullet/bullet_" + (index + 10000).ToString()));
        //    }

        //    return _AllBulletPool[index].Get(parent);
        //}
        //public static GameObject GetShowOneBullet(int index, Transform parent)
        //{
        //    if (_AllShowBulletPool.Length > index)
        //    {
        //        if (_AllShowBulletPool[index] == null)
        //        {
        //            _AllShowBulletPool[index] = new GameObjectPool();
        //        }
        //    }
        //    if (_AllShowBulletPool[index].IsTemplete() == false)
        //    {
        //        _AllShowBulletPool[index].SetTemplete(common4.LoadPrefab("BulletShow/bullet_" + (index + 10000).ToString()));
        //    }

        //    return _AllShowBulletPool[index].Get(parent);
        //}

        //public static GameObject GetShowOneWang(int index, Transform Pos)
        //{
        //    if (_AllWangPool.Length > index)
        //    {
        //        if (_AllWangPool[index] == null)
        //        {
        //            _AllWangPool[index] = new GameObjectPool();
        //        }
        //    }
        //    if (_AllWangPool[index].IsTemplete() == false)
        //    {
        //        _AllWangPool[index].SetTemplete(common4.LoadPrefab("WangShow/net_" + (index + 10000)));
        //    }
        //    return _AllWangPool[index].Get(Pos);
        //}
        //public static GameObject GetOneBigFishDieMoney(Transform Pos)
        //{
        //    if (_BigFishDieMoney == null)
        //    {
        //        _BigFishDieMoney = new GameObjectPool();
        //    }
        //    if (_BigFishDieMoney.IsTemplete() == false)
        //    {
        //        _BigFishDieMoney.SetTemplete(common4.LoadPrefab("Effect/die_effect/BigFIshDieMoney"));
        //    }
        //    return _BigFishDieMoney.Get(Pos);
        //}
        //public static void ReciveBigFishDieMoney(GameObject go)
        //{
        //    _BigFishDieMoney.Recycle(go); 
        //}
        //public static void ReciveShowOneBullet(int index, GameObject go)
        //{
        //    _AllShowBulletPool[index].Recycle(go); 
        //}
    }
}