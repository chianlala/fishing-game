
using UnityEngine;
using UnityEngine.UI;
using CoreGame;


namespace Game.UI
{
    public class MyInfoHead : MonoBehaviour
    {
        public Button Btn_this;
        public Image img_head ;
        void Awake() {
            img_head = this.transform.GetComponent<Image>();
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIUserInfo);
            });
            EventManager.intHeadUrlUpdate += TmpChangHead;
        }
        void OnEnable() {
            UIHelper.GetHeadImage(PlayerData.HeadIndex.ToString(), (sp) =>
            {
                img_head.sprite = sp;
            });
        }
        void OnDestroy() {
            EventManager.intHeadUrlUpdate -= TmpChangHead;
        }
        void TmpChangHead(int num) {
            UIHelper.GetHeadImage(num.ToString(), (sp) =>
            {
                img_head.sprite = sp;
            });
        }
    }
}