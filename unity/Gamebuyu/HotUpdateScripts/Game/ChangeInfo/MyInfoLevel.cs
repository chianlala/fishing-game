
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class MyInfoLevel : MonoBehaviour
    {
        public Text txt_PlayerLevel ;   
        void Awake() {
            txt_PlayerLevel = this.transform.GetComponent<Text>();
            EventManager.PlayerLevelUpdate += TmpChangHead; 
        }
        void OnEnable() {
            txt_PlayerLevel.text = "Lv." + PlayerData.PlayerLevel.ToString();
        }
        void OnDestroy() {
            EventManager.PlayerLevelUpdate -= TmpChangHead;
        }
        void TmpChangHead(long palyerlevel) {
            txt_PlayerLevel.text = "Lv." + palyerlevel.ToString();
        } 
    }
}