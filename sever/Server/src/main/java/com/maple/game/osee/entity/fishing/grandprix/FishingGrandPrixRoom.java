package com.maple.game.osee.entity.fishing.grandprix;

import com.google.protobuf.GeneratedMessage;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.TtmyFishingGrandPrixMessage;
import com.maple.game.osee.util.MyRefreshFishingHelper;
import com.maple.game.osee.util.MyRefreshFishingUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 捕鱼大奖赛房间
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FishingGrandPrixRoom extends NewBaseFishingRoom {

    private int configGameId = getGameId();

    /**
     * 房间序号
     */
    private int roomIndex = getGameId();

    /**
     * 刷鱼的响应协议值
     */
    private int refreshFishResponseValue =
        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_REFRESH_FISHES_RESPONSE_VALUE;

    /**
     * 倍数鱼的响应协议值
     */
    private int fishMultipleResponseValue = -1;

    /**
     * 使用boss号角的响应协议值
     */
    private int useBossBugleResponseValue =
        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_BOSS_BUGLE_RESPONSE_VALUE;

    @Override
    public GeneratedMessage.Builder<?> getRefreshFishMessageBuilder() {
        return TtmyFishingGrandPrixMessage.FishingGrandPrixRefreshFishesResponse.newBuilder();
    }

    @Override
    public void addFishInfos(FishStruct fishStruct, GeneratedMessage.Builder<?> messageBuilder) {

        TtmyFishingGrandPrixMessage.FishingGrandPrixRefreshFishesResponse.Builder builder =
            (TtmyFishingGrandPrixMessage.FishingGrandPrixRefreshFishesResponse.Builder)messageBuilder;

        builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForGrandPrix(fishStruct));

    }

    // **************************************************

    @Override
    public int getGameId() {
        return MyRefreshFishingUtil.GRAND_PRIX_ROOM_INDEX;
    }

}
