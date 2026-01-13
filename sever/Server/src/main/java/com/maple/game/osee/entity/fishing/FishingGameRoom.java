package com.maple.game.osee.entity.fishing;

import com.google.protobuf.GeneratedMessage;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.fishing.OseeFishingMessage;
import com.maple.game.osee.util.MyRefreshFishingHelper;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 捕鱼游戏房间
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FishingGameRoom extends NewBaseFishingRoom {

    /**
     * 房间序号
     */
    private int roomIndex = 1;

    /**
     * 刷鱼的响应协议值
     */
    private int refreshFishResponseValue = OseeMessage.OseeMsgCode.S_C_OSEE_FISHING_REFRESH_FISHES_RESPONSE_VALUE;

    /**
     * 倍数鱼的响应协议值
     */
    private int fishMultipleResponseValue = OseeMessage.OseeMsgCode.S_C_FISHING_ROOM_FISH_MULTIPLE_RESPONSE_VALUE;

    /**
     * 使用boss号角的响应协议值
     */
    private int useBossBugleResponseValue = OseeMessage.OseeMsgCode.S_C_TTMY_USE_BOSS_BUGLE_RESPONSE_VALUE;

    @Override
    public GeneratedMessage.Builder<?> getRefreshFishMessageBuilder() {
        return OseeFishingMessage.FishingRefreshFishesResponse.newBuilder();
    }

    @Override
    public void addFishInfos(FishStruct fishStruct, GeneratedMessage.Builder<?> messageBuilder) {

        OseeFishingMessage.FishingRefreshFishesResponse.Builder builder =
            (OseeFishingMessage.FishingRefreshFishesResponse.Builder)messageBuilder;

        builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForGeneral(fishStruct));

    }

    // **************************************************

    @Override
    public int getGameId() {
        return 7;
    }

    /**
     * 需要的vip等级
     */
    private int vipLevel = 0;

}
