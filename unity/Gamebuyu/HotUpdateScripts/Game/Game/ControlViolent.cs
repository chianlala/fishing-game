using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;

namespace Game.UI
{
    public class ControlViolent : MonoBehaviour
    {
        private AudioSource thisAudio;
        public void Awake()
        {
            thisAudio = this.GetComponent<AudioSource>();
            EventManager.SoundYinXiaoUpdate += SetYinXiao;
        }
        void OnEnable() {
            thisAudio.volume = SoundHelper.GameVolume;
        }
        public void OnDestory()
        {
            EventManager.SoundYinXiaoUpdate -= SetYinXiao;
        }
        void SetYinXiao(float fvalue)
        {
            if (thisAudio != null)
            {
                thisAudio.volume = fvalue;
            }
        }
    }
}
