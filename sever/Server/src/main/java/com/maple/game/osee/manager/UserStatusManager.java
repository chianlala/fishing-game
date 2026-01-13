package com.maple.game.osee.manager;

import com.alibaba.fastjson.JSON;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.entity.UserStatus;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.proto.OseePublicData;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户状态管理
 */
@Component
@Slf4j
public class UserStatusManager {

    /**
     * 用户物品数据缓存((%s)用户ID Map->(标识,状态))
     */
    private static final String USER_ID_FLAG_STATUS = "G:U:STATUS:%s";

    private String getKey(Long userId, NewBaseFishingRoom room) {

        String result = String.format(USER_ID_FLAG_STATUS, userId);

        if (room == null) {

            return "-1:" + result;

        }

        return room.getConfigGameId() + ":" + result;

    }

    /**
     * 设置状态(可以优化为返回刚设置的数据用于 响应)
     */
    public void setUserStatus(UserStatus<Object> userStatus, NewBaseFishingRoom room) {

        final RMap<String, Object> rMap = RedisHelper.redissonClient.getMap(getKey(userStatus.getUserId(), room));

        rMap.put(userStatus.getFlag(), userStatus.getStatus());

    }

    /**
     * 获取所有状态缓存对象
     */
    public RMap<String, Object> getUserStatusMap(Long userId, NewBaseFishingRoom room) {

        return RedisHelper.redissonClient.getMap(getKey(userId, room));

    }

    /**
     * 获取多个状态Map
     */
    public Map<String, Object> getUserStatusMap(Long userId, Set<String> keys, NewBaseFishingRoom room) {
        return this.getUserStatusMap(userId, room).getAll(keys);
    }

    /**
     * 获取单个状态Map
     */
    public Object getUserStatusMap(Long userId, String key, NewBaseFishingRoom room) {

        return this.getUserStatusMap(userId, room).get(key);

    }

    /**
     * 获取状态
     */
    public OseePublicData.PlayerStatusResponse getUserStatusInfo(ServerUser user, Integer index,
        NewBaseFishingRoom room) {

        long userId = user.getId();

        if (index != null && index > 0) { // 表示查询别人
            // 通过房间查询该序号上的人
            userId = index;
        }

        final OseePublicData.PlayerStatusResponse.Builder builder = OseePublicData.PlayerStatusResponse.newBuilder();
        builder.setIndex((int)userId);
        builder.setDatetime(System.currentTimeMillis());
        builder.setData(JSON.toJSONString(this.getUserStatusMap(userId, room).readAllMap()));

        return builder.build();

    }

    /**
     * 获取状态
     */
    public OseePublicData.PlayerStatusResponse.Builder getUserStatusInfo(ServerUser user, List<String> keys,
        NewBaseFishingRoom room) {

        final OseePublicData.PlayerStatusResponse.Builder builder = OseePublicData.PlayerStatusResponse.newBuilder();

        builder.setIndex((int)user.getId());
        builder.setDatetime(System.currentTimeMillis());
        builder.setData(JSON.toJSONString(this.getUserStatusMap(user.getId(), new HashSet<>(keys), room)));

        return builder;

    }

}
