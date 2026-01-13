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
    //鱼信息封装
    public class TmpFishingFishInfoProto
    {
        public TmpFishingFishInfoProto(long varid, long varfishId, long varRouteId, float varclientLifeTime, long varcreateTime, long varlottery, bool varchuQiZhiShengFlag)
        { 
            this.id = varid;
            this.fishId = varfishId;
            this.routeId = varRouteId;
            this.clientLifeTime = varclientLifeTime;
            this.createTime = varcreateTime;
            this.lottery = varlottery;
            this.chuQiZhiShengFlag = varchuQiZhiShengFlag;
        } 
        public long id { get; set; }
        public long fishId { get; set; }
        public long routeId { get; set; }
        public float clientLifeTime { get; set; }
        public long createTime { get; set; }
        public long lottery { get; set; }

        public bool chuQiZhiShengFlag { get; set; }  
    }
}