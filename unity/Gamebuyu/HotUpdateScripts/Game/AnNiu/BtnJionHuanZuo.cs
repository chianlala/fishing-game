using CoreGame;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class BtnJionHuanZuo : MonoBehaviour
    {
        public Button Btn_this;
        public OperateSkeletonGraphic Operate;
        private int jionindex = 4;
        void Awake()
        {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                //NetMessage.OseeFishing.Req_FishingJoinRoomRequest(5);
                UIMgr.ShowUI(UIPath.UIChangeSeat);
            });
            EventManager.PaoLevelUpdate += On_ChangePao;
        }
        void OnDestory()
        {
            EventManager.PaoLevelUpdate -= On_ChangePao;
        }
        void On_ChangePao(int v)
        {
            //if (Operate == null)
            //{
            //    Operate = this.GetComponent<OperateSkeletonGraphic>();
            //}
            //if (PlayerData.PaoLevel < common.dicPaoFwConfig[jionindex].Minlevel)
            //{
            //    Operate.SetAniName("dark");
            //    this.transform.Find("lock").gameObject.SetActive(true);
            //}
            //else
            //{
            //    Operate.SetAniName("start1");
            //    this.transform.Find("lock").gameObject.SetActive(false);
            //}
        }
    }
}