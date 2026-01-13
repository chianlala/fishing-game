
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class DragonCrystalUpdate : MonoBehaviour
    {
        public Text thisTxt;    
        void Awake() {
            thisTxt = this.transform.GetComponent<Text>();
            EventManager.DragonCrystalUpdate += TmpChang;
        }
        void OnEnable() {
            thisTxt.text = PlayerData.DragonCrystal.ToString();
        }
        void OnDestroy() {
            EventManager.DragonCrystalUpdate -= TmpChang;
        }
        void TmpChang(long vaule) {
            thisTxt.text = vaule.ToString(); 
        } 
    }
}