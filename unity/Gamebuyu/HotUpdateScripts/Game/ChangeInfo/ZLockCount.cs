
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class ZLockCount : MonoBehaviour
    { 
        public Text txtCount; 
        public GameObject txtMore;    
        void Awake() {
            txtCount = this.transform.GetComponent<Text>();
            txtMore = this.transform.Find("txtMore").gameObject;
            
            EventManager.LockCount += ChangeFastCount;
        }
        void OnEnable() {
            ChangeFastCount(PlayerData.LockCount);
        }
        void OnDestroy() {
            EventManager.LockCount -= ChangeFastCount;
        }
        void ChangeFastCount(long num) {
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