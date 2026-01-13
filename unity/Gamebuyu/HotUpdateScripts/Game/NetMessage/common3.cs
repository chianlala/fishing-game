using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
using CoreGame;
using Game.UI;
using libx;
using ProtoBuf;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;
using UnityEngine;

namespace Game.UI
{
    /// <summary>
    /// JEngine's UI
    /// </summary> 
    public class common3
    {
        public static void CloseLoginZhuXiao()
        {

            UIMgr.ShowUI(UIPath.UILogin);
            UIMgr.CloseAllwithOutTwo(UIPath.UILogin, UIPath.UIMessageBox);
            NetMgr.Instance.OnZhuxiao();
        }
        public static UIFishingInterface _UIFishingInterface;
 
    }
}