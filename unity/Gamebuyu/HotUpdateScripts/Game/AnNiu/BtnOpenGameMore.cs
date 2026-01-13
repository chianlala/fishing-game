
using UnityEngine;
using UnityEngine.UI;
using CoreGame;
using DG.Tweening;

namespace Game.UI
{
    public class BtnOpenGameMore : MonoBehaviour
    {
        public Transform TransParent; 
        public Button Btn_this;
        bool CanClick=true;
        void Awake() { 
            Btn_this = this.transform.GetComponent<Button>();
            TransParent = this.transform.parent;
            Btn_this.onClick.AddListener(() =>
            {
                if (CanClick)
                {
                    CanClick = false;
                    
                    if (TransParent.transform.localPosition.x>-50)
                    {
                        //this.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                        TransParent.transform.DOLocalMoveX(-100f, 1f).OnComplete(() => {
                            CanClick = true;
                        });
                    }
                    else
                    {
                        //this.transform.localEulerAngles = new Vector3(0f, 0f, 180f);
                        TransParent.transform.DOLocalMoveX(0f, 1f).OnComplete(() => {
                            CanClick = true;
                        });
                    }
              
                }
            }); 
        }
        void OnEnable() {
            if (TransParent!=null)
            {
                CanClick = true;
                //this.transform.localEulerAngles = new Vector3(0f, 0f, 0f);
                TransParent.transform.localPosition = new Vector3(0f, 0f, 0f);
            }
        }
    }
}