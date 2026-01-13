package com.maple.game.osee.entity.fishing.challenge;

import com.google.protobuf.GeneratedMessage;
import com.maple.database.config.redis.RedisHelper;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.MyRefreshFishingHelper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 捕鱼挑战赛房间
 *
 * @author Junlong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class FishingChallengeRoom extends NewBaseFishingRoom {

    /**
     * 房间序号，就是配置表里面的：sessionId
     */
    private int roomIndex = 5;

    /**
     * 刷鱼的响应协议值
     */
    private int refreshFishResponseValue =
        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REFRESH_FISHES_RESPONSE_VALUE;

    /**
     * 倍数鱼的响应协议值
     */
    private int fishMultipleResponseValue =
        OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_ROOM_FISH_MULTIPLE_RESPONSE_VALUE;

    /**
     * 使用boss号角的响应协议值
     */
    private int useBossBugleResponseValue =
        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_BOSS_BUGLE_RESPONSE_VALUE;

    @Override
    public GeneratedMessage.Builder<?> getRefreshFishMessageBuilder() {
        return TtmyFishingChallengeMessage.FishingChallengeRefreshFishesResponse.newBuilder();
    }

    @Override
    public void addFishInfos(FishStruct fishStruct, GeneratedMessage.Builder<?> messageBuilder) {

        TtmyFishingChallengeMessage.FishingChallengeRefreshFishesResponse.Builder builder =
            (TtmyFishingChallengeMessage.FishingChallengeRefreshFishesResponse.Builder)messageBuilder;

        builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForChallenge(fishStruct, true));

    }

    @Override
    public boolean reset(boolean cleanFishMapFlag) {

        boolean resetFlag = super.reset(cleanFishMapFlag);

        // if (!cleanFishMapFlag) {
        //
        // Set<Long> removeKeySet = new HashSet<>();
        //
        // for (Map.Entry<Long, FishStruct> item : getFishMap().entrySet()) {
        //
        // // 不移除：世界 boss
        // if (!MyRefreshFishingHelper.WORLD_BOSS_ROOM_MAP.containsKey(item.getValue().getId())) {
        //
        // removeKeySet.add(item.getKey());
        //
        // } else {
        //
        // // log.info("不移除 boss鱼：{}", getCode());
        //
        // }
        //
        // }
        //
        // for (Long item : removeKeySet) {
        //
        // getFishMap().remove(item);
        //
        // }
        //
        // }

        if (resetFlag) {

            try {
                RedisHelper.set("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + this.getCode(), "0");
            } catch (Exception ignored) {
            }

        }

        return resetFlag;

    }

}
