using CoreGame;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace Game.UI
{
    public class UIBaoFu : MonoBehaviour
    {
        public Text Number;
        public DragonBones.UnityArmatureComponent BoomTexiao;
        // Use this for initialization
        void Start()
        {

        }

        // Update is called once per frame
        void Update()
        {

        }

        public void InitNum(string Num, int vartype)
        {
            Number.text = Num;
            if (vartype == 1)
            {

            }
            else if (vartype == 2)
            {

            }

            //SoundHelper.PlayClip(SoundMgr.Instance.bossbeiLvOver);
            BoomTexiao.animation.Play("mianban", 1);
            CancelInvoke("OnClose");
            Invoke("OnClose", 1.5f);
        }
        void OnClose()
        {
            UIMgr.CloseUI(UIPath.UIBaoFu);
        }
    }
}