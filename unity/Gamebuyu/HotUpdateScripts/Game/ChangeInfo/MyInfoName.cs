
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class MyInfoName : MonoBehaviour
    {
        public Text txt_Name ;   
        void Awake() {
            txt_Name = this.transform.GetComponent<Text>();
            EventManager.NackNameUpdate += TmpChangHead;
        }
        void OnEnable() {
            txt_Name.text = PlayerData.NickName;
        }
        void OnDestroy() {
            EventManager.NackNameUpdate -= TmpChangHead;
        }
        void TmpChangHead(string NickName) {
            txt_Name.text = NickName;
        } 
    }
}