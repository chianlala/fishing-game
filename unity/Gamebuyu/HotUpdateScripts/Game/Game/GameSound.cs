using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameSound{
    //音效
    //private string login = "Sound/login";
    private string hall = "Sound/hall";
    private string fish = "Sound/fish";
    private string reward = "Sound/reward";

    void InitSound() {

    }
    public static void PlayRoleWord(char index, string name)
    {
        

        var clip = LoadAsset<AudioClip>("RoleSound/"+ index.ToString()+"/"+name);
        SoundHelper.PlayClip(clip);
    }

    static T LoadAsset<T>(string path) where T : Object
    {
        T obj = Resources.Load<T>(path);

        if (obj == null)
            Debug.LogError(path + " asset is not exist...");

        return obj;
    }

    ///// <summary>
    ///// 获取常用语文字列表
    ///// </summary>
    ///// <param name="sex"></param>
    ///// <returns></returns>
    //public static string[] GetCommonUseWords(int sex)
    //{
    //    if (sex == 1)
    //        return commonWordMan;
    //    else
    //        return commonWordWoman;
    //}
    /// <summary>
    /// 播放常用语语音
    /// </summary>
    /// <param name="sex"></param>
    /// <param name="index"></param>
    public static void PlayCommonUseWordSound(int sex, int index)
    {
        string path = string.Empty;

        if (sex == 1)
            path += "Sound/word1/chat_m_" + index;
        else
            path += "Sound/word2/chat_f_" + index;

        var clip = LoadAsset<AudioClip>(path);
        SoundHelper.PlayClip(clip);
    }
    public static void PlayNiuCardTypeSound(int sex, int type)
    {
        string path = string.Empty;
        if (sex == 1)
            path = "Sound/niuniu1/card_type_sound_" + type;
        else
            path = "Sound/niuniu2/card_type_sound_" + type;

        var clip = LoadAsset<AudioClip>(path);
        SoundHelper.PlayClip(clip);
    }
}
