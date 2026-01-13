package com.maple.game.osee.manager;

import java.util.*;

import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.entity.UserProps;
import com.maple.game.osee.dao.data.mapper.GUserPropsMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;

/**
 * 用户物品管理
 */
@Component
public class UserPropsManager {

    @Autowired
    private GUserPropsMapper userPropsMapper;

    /**
     * 用户物品数据缓存((%s)用户ID Map->(物品id,物品数量))
     */
    private static final String USER_ID_PROPS_ID_USERPROPS = "G:U:PROPS:%s";

    /**
     * 获取用户的物品数量
     *
     * @param user 用户
     * @param propsId 物品Id
     */
    public Long getUserProopsNum(ServerUser user, Integer propsId) {

        return getUserProopsNum(user.getId(), propsId);

    }

    /**
     * 获取用户的物品数量
     *
     * @param propsId 物品Id
     */
    public Long getUserProopsNum(long userId, Integer propsId) {

        // 查询缓存
        final RMap<Integer, Number> userRMap =
            RedisHelper.redissonClient.getMap(String.format(USER_ID_PROPS_ID_USERPROPS, userId));

        Long quantity = Convert.toLong(userRMap.get(propsId));

        if (quantity == null) { // 没有缓存

            final UserProps userProps = userPropsMapper.getUserProps(userId, propsId);

            if (userProps == null) {
                quantity = 0L;
            } else {
                quantity = userProps.getQuantity();
            }

            userRMap.put(propsId, quantity);

            return quantity;

        }

        return quantity;

    }

    public UserProps getUserProps(Long userId, Integer propsId){
        UserProps userProps = userPropsMapper.getUserProps(userId, propsId);
        return userProps;
    }

    /**
     * 获取用户的物品信息
     *
     * @param user 用户
     * @return 物品id-数量
     */
    public Map<Integer, Long> getUserProps(ServerUser user) {
        // 查询缓存
        final RMap<Integer, Number> userRMap =
            RedisHelper.redissonClient.getMap(String.format(USER_ID_PROPS_ID_USERPROPS, user.getId()));
        final Map<Integer, Long> res = new HashMap<>();
        if (userRMap.isEmpty()) { // 没有缓存 数据库查询
            final List<UserProps> userProps = userPropsMapper.getUserPropsByUserId(user.getId());
            userProps.parallelStream().forEach(p -> {
                res.put(p.getPropsId(), p.getQuantity());
            });
            userRMap.putAll(res);
        } else {
            userRMap.readAllMap().forEach((k, v) -> {
                res.put(k, v.longValue());
            });
        }
        res.forEach((k, v) -> {
            if (TIME_PROPS.contains(k)) { // 时间物品 时间戳转剩余秒
                res.put(k, (v - System.currentTimeMillis()) / 1000);
            }
        });
        return res;
    }

    // 没有时间条件限制的时间类道具
    public static final List<Integer> TIME_PROPS_NO_TIME =
        Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.WING_VIEW_0.getId(), ItemId.BATTERY_VIEW_1.getId(),
            ItemId.WING_VIEW_2.getId(), ItemId.BATTERY_VIEW_10.getId(), ItemId.BATTERY_VIEW_11.getId());

    public static final List<Integer> TIME_PROPS_BV =
        Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.BATTERY_VIEW_1.getId(), ItemId.BATTERY_VIEW_2.getId(),
            ItemId.BATTERY_VIEW_3.getId(), ItemId.BATTERY_VIEW_4.getId(), ItemId.BATTERY_VIEW_5.getId(),
            ItemId.BATTERY_VIEW_6.getId(), ItemId.BATTERY_VIEW_7.getId(), ItemId.BATTERY_VIEW_8.getId(),
            ItemId.BATTERY_VIEW_9.getId(), ItemId.BATTERY_VIEW_12.getId(), ItemId.BATTERY_VIEW_13.getId());

    public static final List<Integer> TIME_PROPS_WV =
        Arrays.asList(ItemId.WING_VIEW_0.getId(), ItemId.WING_VIEW_1.getId(), ItemId.WING_VIEW_2.getId(),
            ItemId.WING_VIEW_3.getId(), ItemId.WING_VIEW_4.getId(), ItemId.WING_VIEW_5.getId());

    // 有时间条件限制的时间类道具
    public static final List<Integer> TIME_PROPS =
        CollUtil.addAllIfNotContains(CollUtil.addAllIfNotContains(new ArrayList<>(TIME_PROPS_BV), TIME_PROPS_WV),
            CollUtil.newArrayList(ItemId.SKILL_LEI_MING_PO.getId(), ItemId.SKILL_LEI_SHEN_BIAN.getId()
                    ,ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId()));

    /**
     * 用户添加物品
     *
     * @param user 用户
     * @param userProps 物品
     * @param reason 变动原因
     * @return
     */
    public void addUserProps(ServerUser user, UserProps userProps, ItemChangeReason reason) {
        this.addUserProps(user, Collections.singletonList(userProps), reason);
    }

    /**
     * 用户添加物品
     */
    public void addUserProps(ServerUser user, List<UserProps> userPropsList, ItemChangeReason reason) {

        final RMap<Integer, Number> userRMap =
            RedisHelper.redissonClient.getMap(String.format(USER_ID_PROPS_ID_USERPROPS, user.getId()));

        userPropsList.stream()
            .filter(userProps -> userProps.getUserId() != null && userProps.getUserId().equals(user.getId()))
            .forEach(userProps -> { // 写缓存

                if (TIME_PROPS.contains(userProps.getPropsId())) { // 时长道具按秒 延长时间

                    userProps.setQuantity(userProps.getQuantity() * 1000L);

                    final Number orDefault = userRMap.getOrDefault(userProps.getPropsId(), 0);

                    if (orDefault.longValue() <= System.currentTimeMillis()) { // 新购买或者道具已过期

                        userRMap.put(userProps.getPropsId(), System.currentTimeMillis() + userProps.getQuantity());
                        return;

                    }

                }


                userRMap.put(userProps.getPropsId(),
                    userRMap.getOrDefault(userProps.getPropsId(), 0).longValue() + userProps.getQuantity());

                // userRMap.addAndGet(userProps.getPropsId(), userProps.getQuantity());

            });

        // 标记该用户道具信息需要更新
        WAIT_UPDATE_DATA.put("userProps", user.getId());

    }

    /**
     * 待更新得数据
     */
    public static final HashMultimap<String, Object> WAIT_UPDATE_DATA = HashMultimap.create();

    /**
     * 定时同步数据
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
//    @Scheduled(fixedDelay = 1000)
    public void saveCache() {
        batchSaveUserProps();
    }

    /**
     * 批次更新道具信息
     */
    public void batchSaveUserProps() {

        final Set<Object> userProps = WAIT_UPDATE_DATA.get("userProps");

        if (userProps != null && userProps.size() > 0) {

            final List<Object> userIdList = new ArrayList<>(userProps);

            userProps.clear();

            final ArrayList<UserProps> userPropsList = new ArrayList<>();

            for (int i = 0; i < userIdList.size(); i++) {

                // 获取用户所有道具得缓存
                final Long userid = Long.parseLong("" + userIdList.get(i));

                final RMap<Integer, Number> rMap =
                    RedisHelper.redissonClient.getMap(String.format(USER_ID_PROPS_ID_USERPROPS, userid));

                rMap.forEach((k, v) -> {
                    if (k.equals(ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId())){ // 排除天神关羽，防止覆盖数据库写入
                        return;
                    }
                    userPropsList.add(new UserProps(userid, k, v.longValue()));
                });

                if (userPropsList.size() >= 1000) { // 批次提交

                    userPropsMapper.saveUserPropsList(userPropsList);
                    userPropsList.clear();

                }

            }

            if (userPropsList.size() > 0) { // 剩余数据

                userPropsMapper.saveUserPropsList(userPropsList);
                userPropsList.clear();

            }

            // System.out.println("缓存之后:" + WAIT_UPDATE_DATA.get("userProps").size());

        }

    }
}
