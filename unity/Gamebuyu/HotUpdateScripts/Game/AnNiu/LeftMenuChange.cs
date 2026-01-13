
using UnityEngine;
using UnityEngine.UI;
using CoreGame;
using DG.Tweening;

namespace Game.UI
{
    public class LeftMenuChange : MonoBehaviour
    {
        public Button btnShowMenu;

        public Transform frame;
        public Button btnLeave;
        public Button btnFishBook;
        public Button btnSetting;
        public Button btnmask;

        bool DianJiShowMenu=false;
        bool DianJiMask = false; 
        void Awake() {
            frame = this.transform.Find("frame");
            btnFishBook = this.transform.Find("frame/btnFishBook").GetComponent<Button>();
            btnLeave = this.transform.Find("frame/btnLeave").GetComponent<Button>();
            btnSetting = this.transform.Find("frame/btnSetting").GetComponent<Button>();

            btnShowMenu = this.transform.Find("btnShowMenu").GetComponent<Button>();
            btnmask = this.transform.Find("btnmask").GetComponent<Button>();

            btnShowMenu.onClick.AddListener(() =>
            {
                //if (DianJiShowMenu)
                //{
                //    return;
                //}
                //DianJiShowMenu = true;

                frame.gameObject.SetActive(true);
                frame.transform.DOLocalMoveX(0, 1f).OnComplete(()=> {

                    btnmask.gameObject.SetActive(true);
                    DianJiShowMenu = false;
                });            
                btnShowMenu.gameObject.SetActive(false);
            });
            btnmask.onClick.AddListener(() =>
            {
                //if (DianJiMask)
                //{
                //    return;
                //}
                //DianJiMask = true;

                btnmask.gameObject.SetActive(false);
                frame.transform.DOLocalMoveX(-250, 1f).OnComplete(()=> {

                    frame.gameObject.SetActive(false);
                    DianJiMask = false;

                    btnShowMenu.gameObject.SetActive(true);
                });
         
            });


            btnLeave.onClick.AddListener(() =>
            {
                if (ByData.nModule==51)
                {
                    MessageBox.Show("您正在体验场进行游戏，退出后体验分将会清零",null,()=> {
                        NetMessage.Chanllenge.Req_FishingChallengeExitRoomRequest();
                    });
                    
                }
                else
                {
                    NetMessage.Chanllenge.Req_FishingChallengeExitRoomRequest();
                }
                
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
        void OnEnable() {
            frame.gameObject.SetActive(false);            
            btnShowMenu.gameObject.SetActive(true);
        }
    }
}