
using System.Linq;
using System.Text;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


public class GUIMainNetWorkInfo : MonoBehaviour
{
    bool isSocketWorkLose = false;
    bool isContent = false;
    float fCD;
    float fHeartCD;
    public static GUIMainNetWorkInfo instancse;

    public bool isStartJianChe;
    void Start()
    {
    }



    private void Update()
    {
  
        if (Application.internetReachability == NetworkReachability.NotReachable)
        {
            //网络已断开            
            //MessageBox.ShowConfirm("网络链接已断开，请检查网络链接是否正常 \n若网络正常点击“确定”按钮，点击取消退出游戏", null, () => { NetMgr.Instance.OnZhuxiaoAndLogin(); }, () => { Application.Quit(); });
        }
        else
        {
            if (NetMgr.Instance.pLoginState==2)
            {

                if (commonunity.bWaiting) //表示等待时间
                {
                    fCD += Time.deltaTime;
                }
                else
                {
                    fCD = 0;
                }
                if (fCD > 6f)//等待超过6秒重连
                {
                    commonunity.bWaiting = false;
                    fCD = 0f;
                    //MessageBox.ShowPopMessage("正在重新连接服务器");
                    NetMgr.Instance.OnConnectLogin();
                }

            }
        }
    }

}
