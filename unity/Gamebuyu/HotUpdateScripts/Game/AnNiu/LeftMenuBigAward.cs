
using UnityEngine;
using UnityEngine.UI;
using CoreGame;
using DG.Tweening;

namespace Game.UI
{
    public class LeftMenuBigAward : MonoBehaviour
    {
        public Button btnShowMenu;

        public Transform frame;
        public Button btnLeave; 
        public Button btnFishBook;
        public Button btnSetting;
        public Button btnmask; 
        void Awake() {
            frame = this.transform.Find("frame");
            btnFishBook = this.transform.Find("frame/btnFishBook").GetComponent<Button>();
            btnLeave = this.transform.Find("frame/btnLeave").GetComponent<Button>();
            btnSetting = this.transform.Find("frame/btnSetting").GetComponent<Button>();

            btnShowMenu = this.transform.Find("btnShowMenu").GetComponent<Button>();
            btnmask = this.transform.Find("btnmask").GetComponent<Button>();
            btnShowMenu.onClick.AddListener(() =>
            {
        
                frame.gameObject.SetActive(true);
                frame.transform.DOLocalMoveX(0, 1f).OnComplete(()=> { btnmask.gameObject.SetActive(true); });            
                btnShowMenu.gameObject.SetActive(false);
            });
            btnmask.onClick.AddListener(() =>
            {
                btnmask.gameObject.SetActive(false);
                frame.transform.DOLocalMoveX(-250, 1f).OnComplete(()=> {
                    frame.gameObject.SetActive(false);
                });
                btnShowMenu.gameObject.SetActive(true);
            });
            btnLeave.onClick.AddListener(() =>
            {
                NetMessage.BigAward.Req_FishingGrandPrixQuitRequest();
            });
            btnSetting.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UISetting);
            });
            btnFishBook.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIYuzhong);
            });
        }
        void OnEnable()
        {
            frame.gameObject.SetActive(false);
            btnShowMenu.gameObject.SetActive(true);
        }
    }
}