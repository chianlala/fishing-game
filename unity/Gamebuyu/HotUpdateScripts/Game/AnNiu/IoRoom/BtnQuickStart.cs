using CoreGame;
using UnityEngine;
using UnityEngine.UI;


namespace Game.UI
{
    public class BtnQuickStart : MonoBehaviour
    { 
        public Button Btn_this;
        void Awake()
        {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                SoundLoadPlay.PlaySound("btnclick");
                NetMessage.Chanllenge.Req_FishingChallengeQuickJoinRequest(0);
            });
        }
    }
}