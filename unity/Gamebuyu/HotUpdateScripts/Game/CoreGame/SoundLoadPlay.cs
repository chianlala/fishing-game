using CoreGame;
using Game.UI;
using JEngine.Core;
using LitJson;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace CoreGame
{
    public static class SoundLoadPlay
    {
        //public static Dictionary<string, AudioClip> listAudioClip = new Dictionary<string, AudioClip>();
        //public static void Init()
        //{
        //    var result = common4.LoadAynsJson("Json/CanShu");
        //    common5.JsonCanShu = JsonMapper.ToObject(result.text); 
        //    Debug.Log(common5.JsonCanShu["soundPath"].ToJson());
        //    foreach (var itempath in common5.JsonCanShu["soundPath"].Keys)
        //    {
        //        if (listAudioClip.ContainsKey(itempath) == false)
        //        {
        //            listAudioClip.Add(itempath, null);
        //        }
        //    }
        //    //, (result) => {
        //    //    common5.JsonCanShu= JsonMapper.ToObject(result.text);
        //    //    Debug.Log(common5.JsonCanShu["soundPath"].ToJson());
        //    //    foreach (var itempath in common5.JsonCanShu["soundPath"].Keys)
        //    //    {
        //    //        if (listAudioClip.ContainsKey(itempath) == false)
        //    //        {
        //    //            listAudioClip.Add(itempath, null);
        //    //        }
        //    //    }
        //    //});
        //}
        //public static void PlaySound(string soundName)
        //{
        //    if (listAudioClip.ContainsKey(soundName))
        //    {
        //        if (listAudioClip[soundName] != null)
        //        {
        //            //播放
        //            SoundHelper.PlayClip(listAudioClip[soundName]);
        //        }
        //        else
        //        {
        //            var audio = common4.LoadSoundWav(common5.JsonCanShu["soundPath"][soundName].ToString());
        //            listAudioClip[soundName] = audio;
        //            //播放
        //            SoundHelper.PlayClip(listAudioClip[soundName]);
        //            //, (audio) =>
        //            //{
        //            //    //加载成功
        //            //    listAudioClip[soundName] = audio;
        //            //    //播放
        //            //    SoundHelper.PlayClip(listAudioClip[soundName]);
        //            //});
        //        }
        //    }
        //}
        public static Dictionary<string, AudioClip> listAudioClip = new Dictionary<string, AudioClip>();
        public static void PlaySound(string soundName) 
        { 
            //在Sound路径下面
            if (listAudioClip.ContainsKey(soundName))
            {
                if (listAudioClip[soundName] != null) 
                {
                    //播放
                    SoundHelper.PlayClip(listAudioClip[soundName]);
                }
                else
                {
                    var audio = common4.LoadNameSoundWav(soundName);
                    listAudioClip[soundName] = audio;
                    //播放
                    SoundHelper.PlayClip(listAudioClip[soundName]);
                }
            }
            else
            {
                var audio = common4.LoadNameSoundWav(soundName);
                listAudioClip.Add(soundName, audio);
                //播放
                SoundHelper.PlayClip(listAudioClip[soundName]);
            }
        }

        //只有自己能听见
        public static void MyPlaySound(long playerID,string soundName)
        {
            if (playerID!=PlayerData.PlayerId)
            {
                return;
            }
            //在Sound路径下面
            if (listAudioClip.ContainsKey(soundName))
            {
                if (listAudioClip[soundName] != null)
                {
                    //播放
                    SoundHelper.PlayClip(listAudioClip[soundName]);
                }
                else
                {
                    var audio = common4.LoadNameSoundWav(soundName);
                    listAudioClip[soundName] = audio;
                    //播放
                    SoundHelper.PlayClip(listAudioClip[soundName]);
                }
            }
            else
            {
                var audio = common4.LoadNameSoundWav(soundName);
                listAudioClip.Add(soundName, audio);
                //播放
                SoundHelper.PlayClip(listAudioClip[soundName]);
            }
        }
        public static void ChangeBgMusic(string soundName)
        {
            if (listAudioClip.ContainsKey(soundName))
            {
                if (listAudioClip[soundName] != null)
                {
                    //播放
                    SoundHelper.ChangeBgMusic(listAudioClip[soundName]);
                }
                else
                {
                    var audio = common4.LoadNameSoundWav(soundName);
                    listAudioClip[soundName] = audio;
                    //播放
                    SoundHelper.ChangeBgMusic(listAudioClip[soundName]);
                }
            }
            else
            {
                var audio = common4.LoadNameSoundWav(soundName);
                listAudioClip[soundName] = audio;
                //播放
                SoundHelper.ChangeBgMusic(listAudioClip[soundName]);
            }
        }
    }
}
