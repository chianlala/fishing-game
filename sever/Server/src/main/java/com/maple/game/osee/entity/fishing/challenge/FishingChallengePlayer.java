package com.maple.game.osee.entity.fishing.challenge;

import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.PlayerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

import static com.maple.game.osee.controller.gm.GmCommonController.handleRoomIndexStr;

/**
 * 捕鱼挑战赛玩家
 *
 * @author Junlong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FishingChallengePlayer extends FishingGamePlayer {

    @Override
    public long getMoney() {
        return PlayerManager.getPlayerEntity(getUser()).getDragonCrystal();
    }

    @Override
    public void addMoney(long count) {
        PlayerManager.addItem(getUser(), ItemId.DRAGON_CRYSTAL, count, ItemChangeReason.FISHING_RESULT, false);
    }

    /**
     * 使用子弹
     *
     * @param count 备注：这个值是负数
     */
    @Override
    public void useBattery(long count, NewBaseFishingRoom room, RedissonClient redissonClient) {

        if (count == 0) {
            return;
        }

        this.addMoney(count);

        final OseePlayerEntity playerEntity = getUser().getExpertData(OseePlayerEntity.EntityId);

        long useCount = -count;

        playerEntity.setUseBattery(playerEntity.getUseBattery() + useCount);

        RBatch batch = redissonClient.createBatch();

        String roomIndexStr = String.valueOf(room.getRoomIndex());

        // 处理：roomIndexStr
        roomIndexStr = handleRoomIndexStr(getId(), room, roomIndexStr);

        // 处理：使用子弹
        PlayerUtil.handleUseBattery(redissonClient, useCount, batch, roomIndexStr, getId(), true);

        batch.execute(); // 执行：批量操作

    }

    private Map<ItemId, Long> joinItemCount = new HashMap<>();

    /**
     * 坐标位置
     */
    private TtmyFishingChallengeMessage.V3Proto.Builder v3ProtoBuilder = null;

}
