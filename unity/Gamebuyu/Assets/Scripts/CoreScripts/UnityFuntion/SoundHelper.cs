using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;

[Preserve]
public abstract class SoundHelper
{
    static AudioSource _bgmSource = null;
    static AudioSource _gameSource = null;
    static float _bgmVolume = 0.5f;
    static float _gameVolume = 0.5f;

    public static AudioSource BgmSource
    {
        get
        {
            if (_bgmSource == null)
                _bgmSource = SoundMgr.Instance.bgSource;
            if (_bgmSource == null)
                _bgmSource = Object.FindObjectOfType<AudioSource>();
            _bgmSource.loop = true;
            return _bgmSource;
        }
        set
        {
            _bgmSource = value;
        }
    }
    public static AudioSource GameSource
    {
        get
        {
            if (_gameSource == null)
                _gameSource = SoundMgr.Instance.gameSource;
            if (_gameSource == null)
                _gameSource = Object.FindObjectOfType<AudioSource>();

            return _gameSource;
        }
    }

    /// <summary>
    /// 背景音量
    /// </summary>
    public static float BgmVolume
    {
        get { return _bgmVolume; }
        set
        {
            _bgmVolume = value;
            _bgmVolume = Mathf.Clamp(_bgmVolume, 0, 1);
            if (BgmSource != null)
                BgmSource.volume = value;
        }
    }
    /// <summary>
    /// 游戏音效
    /// </summary>
    public static float GameVolume
    {
        get { return _gameVolume; }
        set
        {
            _gameVolume = value;
            _gameVolume = Mathf.Clamp(_gameVolume, 0, 1);
            if (GameSource != null)
                GameSource.volume = value;
        }
    }

    public static void PlayClip(AudioClip clip)
    {
        PlayClip(clip, GameVolume);
    }

    public static void PlayClip(AudioClip clip, float volume)
    {
        if (GameSource == null)
            return;
        if (clip == null)
            return;
        GameSource.PlayOneShot(clip, volume);
    }
    public static void ChangeBgMusic(AudioClip audioClip)
    {  
        if (BgmSource.clip == audioClip)
        {
        }
        else
        {
            BgmSource.clip = audioClip;
            BgmSource.Play();
        }
    }
}
