
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    //海螺（神灯技能）
    public class BtnICE : MonoBehaviour
    {
        public Button Btn_this;   
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.ICE);
            });
        }
    }
} 