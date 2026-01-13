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
    public interface UIFishingInterface
    {
        //void ChangebSuoDingAutoFire();
    
        //GameObject GetprefabYulei();
        void JinZhu(bool state);
        //List<player> GetobjPlayer();
        void DestroyThisRoomToLogin();
        player GetOnePlayer(long playerID);
        void Req_XmlEndRequest(long userId, long reword, int type);
        void Req_UseEleRequest(long fishId);
        void ChangeS_C_DoFire(FishingFireResponse vartmp, long fireId, long fishId, long fishId1, long fishId2, float angle);

        void ShanDianPao_DoFire(FishingFireResponse vartmp, long fireId, long fishId, float angle);
        void ShanDianPaoSyncLockThree_DoFire(long playerId, long fishId, long fishId1, long fishId2);
        //void ZhuanTou_Fire(FishingFireResponse vartmp, long fireId, long fishId, float angle);
        void SpecialFishRequest(List<long> fishIds, long specialFishId, long playerId);
        void TwoAttackFish(int vatype, long PlayerID);
        void TwoAttackNumFish(int vatype, long PlayerID, int num);
        void Req_FishingFightFishRequest(long fireId, long fishId);
        void Req_ChangePaoRequest(int paoIndex);
        void Req_FishingRobotFightFishRequest(long fireId, long fishId, long robotId);
        void AnimalNet(int localPaoPrefab, int nPaoView, Vector3 pos, GameObject tmpBullet);
       
        void tmpUIByBOOSAnimation(long fishId);
        bool Getb_SkillIce();
        void Req_DoubleKillRequest(long PlayerID,List<long> varListFish); 
        void Req_DoubleKillEndRequest(long AllGold, long PlayerID, long mult, string fishName);
        void ShowFishIconMoney(long playerId, string name, long fishID, long varGold);
        Dictionary<int, AudioClip> Getm_listLitFishDie();
        //AudioClip[] Getm_listLitGoldDrop();
        //GameObjectPool GetitemPool_zhuanfanle();
        //Transform Getroot_DropEffect();
        //Transform GetGoldNormal();
        void SyncLockRequest(long fishId);
        //void Req_BitFightFishRequest(List<long> varListFish);
        //Transform GetfishPos();
        //GameObjectPool GetitemPool_SkillDie();
        //void SetZiDong(bool state);
        void ReconnectionInRoom();
        void OpenReConnecting(bool state);

        void SetMyItem();
        void ClearDestory();
        //void ChangeGold();
        //void AnimalYuleiBoom(int itemId, long money, Vector3 pos, long playerPlayerID, float range = 0, bool bPlaySound = true);
        void Req_FishingReactiveRequest();
        GameObjectPool GetDropIconFish();
        //void RedImageShan(bool tmp);
        void isPlayBossCommingSound(bool varPlay);
        void ZhuanTouBoom(long playerID, Vector2 input);
    }
}