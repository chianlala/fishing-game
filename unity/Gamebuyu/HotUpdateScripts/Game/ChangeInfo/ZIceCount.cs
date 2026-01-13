
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class ZIceCount : MonoBehaviour
    { 
        public Text txtCount; 
        public GameObject txtMore;   
        void Awake() {
            txtCount = this.transform.GetComponent<Text>();
            txtMore = this.transform.Find("txtMore").gameObject;
            
            EventManager.IceCount += ChangeIceCount;
        }
        void OnEnable() {
            ChangeIceCount(PlayerData.IceCount);
        }
        void OnDestroy() {
            EventManager.IceCount -= ChangeIceCount;
        }
        void ChangeIceCount(long num) {
            if (num<=99)
            {
                txtCount.text = num.ToString();
                txtMore.gameObject.SetActive(false);
            }
            else
            {
                txtCount.text = "99";
                txtMore.gameObject.SetActive(true);
            }
            
        }
    }
}