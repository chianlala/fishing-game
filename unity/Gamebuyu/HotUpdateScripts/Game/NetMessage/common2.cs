using com.maple.game.osee.proto;
using com.maple.game.osee.proto.fishing;
using com.maple.network.proto;
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
    public class common2
    {

        #region
        public static Transform TraUIRoot;
       
        public static Transform PopUpUICanvas;
        public static Transform FixedUICanvas;
        public static Transform NormalUICanvas;
        public static Transform TwoAttackCanvas;
        public static Transform BuyuUICanvas;

        public static Transform BulletPos;
        public static Transform base_BG;
      
        public static Transform GoldNormal;
        public static Transform ApperAnimation;
        public static Transform transICE;
        public static Transform RootBG;
        public static Transform LoginMask;
        //所有鱼父物体
        public static Transform YuSheFish;
        #endregion
    }
}