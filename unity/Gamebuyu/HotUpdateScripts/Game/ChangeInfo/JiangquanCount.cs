
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class JiangquanCount : MonoBehaviour
    {
        public Text txt_jq ;   
        void Awake() {
            txt_jq = this.transform.GetComponent<Text>();
            EventManager.JiangquanUpdate += TmJiangQuanCount;
        } 
        void OnEnable() {
            txt_jq.text = PlayerData.Jiangquan.ToString();
        }
        void OnDestroy() {
            EventManager.JiangquanUpdate -= TmJiangQuanCount;
        }
        void TmJiangQuanCount(long NickName) {
            txt_jq.text = NickName.ToString();
        } 
    }
}