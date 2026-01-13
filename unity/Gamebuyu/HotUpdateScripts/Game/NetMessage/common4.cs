using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
using DG.Tweening;
using Game.UI;
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
    public class common4
    {
        public static Dictionary<long, FishConfig> dicFishConfig = new Dictionary<long, FishConfig>();//鱼摆摆的配置信息
        public static Dictionary<string, FishJson> dicMoldConfig=new Dictionary<string, FishJson>(); //鱼模型配置信息

        public static Dictionary<int, PaoWinJson> PaoWinConfig = new Dictionary<int, PaoWinJson>(); //炮台翅膀配置信息 

        public static Dictionary<int, BagItemJson> BagItemConfig = new Dictionary<int, BagItemJson>(); //炮台翅膀配置信息  
        public static Dictionary<long, FishJson> _AllGameFish = new Dictionary<long, FishJson>(); //鱼模型配置信息  

        //int 为ID   Long为时间   Long小于等于0则炮台未拥用
        public static Dictionary<int, long> PaoWinTime = new Dictionary<int, long>(); //炮台翅膀配置信息  


        //public static Dictionary<string, FishModleConfig> dicMoldConfig = new Dictionary<string, FishModleConfig>();//鱼摆摆的配置信息
        public static GameObject LoadPrefab(string path)
        {
            if (!path.Contains(".prefab"))
            {
                path = new StringBuilder(path).Append(".prefab").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(GameObject));

            return (GameObject)_request.asset;
        }
        public static AssetRequest LoadAssetPrefab(string path)
        {
            if (!path.Contains(".prefab"))
            {
                path = new StringBuilder(path).Append(".prefab").ToString();
            } 
            AssetRequest _request = Assets.LoadAsset(path, typeof(GameObject));
            return _request;
        }
        //public static GameObject LoadAynsPrefab(string path)
        //{
        //    if (!path.Contains(".prefab"))
        //    {
        //        path = new StringBuilder(path).Append(".prefab").ToString();
        //    }
        //    AssetRequest _request = Assets.LoadAsset(path, typeof(GameObject));
       
        //    return (GameObject)_request.asset;
        //}
        public static AssetRequest LoadAynsPrefab(string path)
        {
            if (!path.Contains(".prefab"))
            {
                path = new StringBuilder(path).Append(".prefab").ToString();
            }
            AssetRequest _request = Assets.LoadAssetAsync(path, typeof(GameObject));
            return _request;
            //return (GameObject)_request.asset;
        }
        public static AudioClip LoadSoundWav(string path)
        {
            if (!path.Contains(".wav"))
            {
                path = new StringBuilder(path).Append(".wav").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(AudioClip));
            return (AudioClip)_request.asset;
        }
        public static AudioClip LoadNameSoundWav(string path)
        {
            if (!path.Contains(".wav")) 
            {
                path = new StringBuilder("Sound/").Append(path).Append(".wav").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(AudioClip));
            return (AudioClip)_request.asset;
        }
        public static int GetFishModleID(string name)
        {
            name = name.Trim();
            if (common4.dicMoldConfig.ContainsKey(name) == false)
            {
                Debug.Log("name" + name + "不存在");
                foreach (var item in common4.dicMoldConfig)
                {
                    Debug.LogError(item.Key);
                } 
                return 0;
            }
            //  Debug.Log("dicMoldConfig[name].modelId" + dicMoldConfig[name].modelId);
            return dicMoldConfig[name].modelID;
        }
        public static int GetFishModleID(long fishID)//也是monsterId
        {
            return dicMoldConfig[dicFishConfig[fishID].name].modelID;
        }
        public static FishJson GetFishCanShu(long fishID)//也是monsterId  
        {
            return dicMoldConfig[dicFishConfig[fishID].name];
        }
        public static FishJson GetFishCanShu(string fishname)//也是monsterId  
        {
            return dicMoldConfig[fishname];
        }
        public static TextAsset LoadText(string path)
        {
            if (!path.Contains(".txt"))
            {
                path = new StringBuilder(path).Append(".txt").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(TextAsset));

            return (TextAsset)_request.asset;
        }

        public static Sprite LoadSprite(string path)
        {
            if (!path.Contains(".png"))
            {
                path = new StringBuilder(path).Append(".png").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(Sprite));
            return (Sprite)_request.asset;
        }
        public static AudioClip LoadAudioClip(string path)
        {
            if (!path.Contains(".wav"))
            {
                path = new StringBuilder(path).Append(".wav").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(AudioClip));

            var m = (AudioClip)_request.asset;
            Debug.LogError("AudioClip" + m.name);

            return (AudioClip)_request.asset;
        }
        public static GameObject LoadPrefabs(string path)
        {
            if (!path.Contains(".prefab"))
            {
                path = new StringBuilder(path).Append(".prefab").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(GameObject));

            return (GameObject)_request.asset;
        }
        public static TextAsset LoadAynsText(string path)
        {
            if (!path.Contains(".txt"))
            {
                path = new StringBuilder(path).Append(".txt").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(TextAsset));
            //_request.completed += (AssetRequest request) =>
            //{
            //    //Loaded = true;
            //    //Instance = (TextAsset)request.asset;
            //    //ErrorMessage = _request.error;
            //    complete?.Invoke((TextAsset)request.asset);
            //};
            return (TextAsset)_request.asset;
        }
        public static TextAsset LoadAynsJson(string path)
        {
            if (!path.Contains(".txt"))
            {
                path = new StringBuilder(path).Append(".txt").ToString();
            }
            AssetRequest _request = Assets.LoadAsset(path, typeof(TextAsset));
            //_request.completed += (AssetRequest request) =>
            //{
            //    complete?.Invoke((TextAsset)request.asset);
            //};
            return (TextAsset)_request.asset;
        }
        public static string ChangeNumStr(long dragonscale)
        {
            if (dragonscale > 1000000000)
            {
                return dragonscale / 100000000 + "亿";
            }
            if (dragonscale > 10000000)
            {
                return dragonscale / 10000 + "万";
            }
            return dragonscale.ToString();
        }

        static Animator cam3DAnimator;
  
        //放大缩小
        public static void SetCamreRoomIN()
        {
            if (cam3DAnimator==null)
            {
                cam3DAnimator= Root3D.Instance.cam3D.GetComponent<Animator>();
            }
            cam3DAnimator.Play("enter_stand_camera");
        }
        //放大缩小
        public static void SetCamreShakerDrill()
        {
            if (cam3DAnimator == null)
            {
                cam3DAnimator = Root3D.Instance.cam3D.GetComponent<Animator>();
            }
            cam3DAnimator.Play("drill_shaker");
        }
    }
}