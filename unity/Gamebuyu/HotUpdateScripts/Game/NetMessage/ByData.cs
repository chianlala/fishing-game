using com.maple.network.proto;
using ProtoBuf;
using System.Collections.Generic;
using System.Security.Cryptography;
using UnityEngine;

namespace CoreGame
{
    /// <summary>
    /// JEngine's UI
    /// </summary>
    public static class ByData
    {
        public static bool IsCanChange;
        public static int nModule=0;
        
        public static int nModuleBg = 0; 
        
        public static int NewRoomId = 0;
        public static string NewRoomPossword = "";

        public static int BychangeRoomType = 0;
        public static bool IsChangSet;

        public static bool vip;
        public static int RoomId;
        //解锁到了下一场次
        public static int NewNormalnModule;

        //大奖赛积分
        public static long nScore = 0;
        //大奖赛子弹
        public static int nBulet = 0; 
    }

}