
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class ZSummonCount : MonoBehaviour
    {  
        public Text txtCount; 
        public GameObject txtMore;    
        void Awake() {
            txtCount = this.transform.GetComponent<Text>();
            txtMore = this.transform.Find("txtMore").gameObject;
            
            EventManager.LockCount += ChangeSummonCount;
        }
        void OnEnable() {
            ChangeSummonCount(PlayerData.SummonCount);
        }
        void OnDestroy() {
            EventManager.SummonCount -= ChangeSummonCount;
        }
        void ChangeSummonCount(long num) {
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