using CoreGame;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class BtnJionRoom1 : MonoBehaviour
    {  
        public Button Btn_this;

        public bool isClick; 
        void Awake() 
        {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                if (isClick==false)
                {
                    isClick = true;
                    //NetMessage.Chanllenge.Req_FishingChallengeQuickJoinRequest(11);
                    NetMessage.Chanllenge.Req_FishingChallengeJoinRoomRequest(1101, 0, "");
                    Invoke("CanIsClick", 0.2f);
                }
            });
        }
        void CanIsClick() {
            isClick = false;
        }
        void OnEnable() {
            CanIsClick();
        }
    }
}