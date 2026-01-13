
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    //急速技能
    public class BtnFAST : MonoBehaviour
    {
        public Button Btn_this;   
        void Awake() {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                NetMessage.Chanllenge.Req_FishingChallengeUseSkillRequest((int)BY_SKILL.FAST);
            });
        }
    }
}