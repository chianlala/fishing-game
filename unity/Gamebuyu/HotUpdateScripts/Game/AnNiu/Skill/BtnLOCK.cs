
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    //海螺（神灯技能）
    public class BtnLOCK : MonoBehaviour
    {
        public Button Btn_this;
        public Transform State1; 
        public Transform State2;
        void Awake() {
            State1 = this.transform.Find("State1");
            State2 = this.transform.Find("State2");
            Btn_this = this.transform.Find("Button").GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.LOCK);
            });
        }
        void OnEnable()
        {
            On_Lock(-1f);
            EventManager.UseLock += On_Lock;
        }
        void OnDisable()
        {
            EventManager.UseLock -= On_Lock;
        }
        void On_Lock(float time)
        {
            if (time > 0)
            {
                State1.gameObject.SetActive(true);
                State2.gameObject.SetActive(false);
            }
            else
            {
                State1.gameObject.SetActive(false);
                State2.gameObject.SetActive(true);
            }
        }
    }
}