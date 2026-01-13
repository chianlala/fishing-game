
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class DiamondUpdate : MonoBehaviour
    {
        public Text thisTxt;     
        void Awake() {
            thisTxt = this.transform.GetComponent<Text>();
            EventManager.DiamondUpdate += TmpChang;
        }
        void OnEnable() {
            thisTxt.text = PlayerData.Diamond.ToString();
        }
        void OnDestroy() {
            EventManager.DiamondUpdate -= TmpChang;
        }
        void TmpChang(long vaule) {
            thisTxt.text = vaule.ToString(); 
        } 
    }
}