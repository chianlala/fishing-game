
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    //狂暴技能
    public class BtnFURY : MonoBehaviour
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
                if (State1.gameObject.activeSelf)
                {
                    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest(-1*(int)BY_SKILL.FURY);
                }
                else
                {
                    NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.FURY);
                }
            });
        }
        void OnEnable() {
            On_Fury(-1f);
            EventManager.UseFury += On_Fury;
        }
        void OnDisable() {
            EventManager.UseFury -= On_Fury;
        }
        void On_Fury(float time) {
            if (time>0)
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