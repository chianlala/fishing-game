package com.maple.game.osee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.maple.database.data.entity.UserEntity;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.model.bo.ActiveConfigBO;
import com.maple.game.osee.proto.ActiveList;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.service.ActiveService;
import com.maple.game.osee.util.RandomUtil;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.maple.game.osee.proto.OseeMessage.OseeMsgCode.S_C_GET_HEAD_IMAGE_LIST_RESPONSE_VALUE;

@Service
@Slf4j
public class ActiveServiceImpl implements ActiveService {

    // 配置的上榜权重（优先级高于今日的上榜权重），key：用户 id，value：权重
    public static final String ACTIVE_CONFIG_KEY = "ACTIVE_CONFIG_KEY";

    // 今日的上榜权重，上线即有默认的 1权重，key：用户 id，value：权重
    public static final String ACTIVE_TODAY_KEY = "ACTIVE_TODAY_KEY";

    // // 现在的活跃榜，20分钟刷新
    // public static final String CURRENT_ACTIVE_LIST_KEY = "CURRENT_ACTIVE_LIST_KEY";

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        ActiveServiceImpl.redissonClient = redissonClient;
    }

    // 活跃榜：上榜的人数
    private static final int ACTIVE_LIST_SIZE = 20;

    /**
     * 获取：当前活跃榜的情况
     */
    @Override
    public void getActiveList(ActiveList.GetActiveListRequest request, ServerUser user) {

        ActiveList.GetActiveListResponse.Builder builder = ActiveList.GetActiveListResponse.newBuilder();

        // RSet<Long> set = redissonClient.getSet(CURRENT_ACTIVE_LIST_KEY, new JsonJacksonCodec());
        //
        // Set<Long> userIdSet = set.readAll();
        //
        // if (CollUtil.isEmpty(userIdSet)) { // 不存在，或者过期了
        // Set<Long> userIdSet = getActiveListGetUserIdSet(user); // 获取：用户id
        // }

        Set<Long> userIdSet = getActiveListGetUserIdSet(user); // 获取：用户id

        // 处理：userIdSet
        for (Long item : userIdSet) {

            if (builder.getListCount() == ACTIVE_LIST_SIZE) {
                break;
            }

            ServerUser serverUser = UserContainer.getUserById(item);

            if (serverUser == null) {
                continue;
            }

            ActiveList.GetActiveListItem.Builder itemBuilder = ActiveList.GetActiveListItem.newBuilder();
            itemBuilder.setUserId(serverUser.getId());
            itemBuilder.setGameId(serverUser.getEntity().getGameId());
            itemBuilder.setHeadUrl(serverUser.getEntity().getHeadUrl());
            itemBuilder.setNickname(serverUser.getEntity().getNickname());

            builder.addList(itemBuilder); // 添加元素

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GET_ACTIVE_LIST_RESPONSE_VALUE, builder, user);

    }

    /**
     * 获取：用户id
     */
    @NotNull
    private Set<Long> getActiveListGetUserIdSet(ServerUser user) {

        // Set<Long> userIdSet; // 获取：userIdSet

        // synchronized (CURRENT_ACTIVE_LIST_KEY) {

        // userIdSet = set.readAll(); // 再获取一次

        // if (CollUtil.isNotEmpty(userIdSet)) { // 如果：已经设置值了
        // return userIdSet;
        // }

        Set<Long> userIdSet = new LinkedHashSet<>();

        HashMap<Long, Integer> hashMap = new HashMap<>();

        RMap<Long, Integer> todayMap = redissonClient.getMap(ACTIVE_TODAY_KEY);

        Map<Long, Integer> todayReadAllMap = todayMap.readAllMap();

        for (Map.Entry<Long, Integer> item : todayReadAllMap.entrySet()) {

            hashMap.put(item.getKey(), item.getValue()); // 添加元素

        }

        RMap<Long, Integer> configMap = redissonClient.getMap(ACTIVE_CONFIG_KEY, new JsonJacksonCodec());

        Map<Long, Integer> configReadAllMap = configMap.readAllMap();

        for (Map.Entry<Long, Integer> item : configReadAllMap.entrySet()) {

            hashMap.put(item.getKey(), item.getValue()); // 添加元素

        }

        List<Long> sourceList = new ArrayList<>();
        List<Double> weightList = new ArrayList<>();

        for (Map.Entry<Long, Integer> item : hashMap.entrySet()) {

            sourceList.add(item.getKey());
            weightList.add((double)item.getValue());

        }

        if (sourceList.size() == 0) {

            sourceList.add(user.getId());
            weightList.add(1d);

        }

        // // 添加：选择的上榜用户
        // set.delete();
        //
        // set.addAll(userIdSet);
        //
        // set.expire(20, TimeUnit.MINUTES);// 设置：20分钟过期

        // }

        return userIdSet;

    }

    /**
     * 设置：今日的上榜权重
     */
    public static void activeTodayPut(ActiveConfigBO activeConfigBO) {

        // RMap<Long, Integer> map = redissonClient.getMap(ACTIVE_TODAY_KEY);
        //
        // int size = map.size();
        //
        // // 设置：值
        // map.put(activeConfigBO.getUserId(), activeConfigBO.getWeight());
        //
        // if (size == 0) {
        // map.expire(1, TimeUnit.DAYS); // 设置：一天过期
        // }

    }

    /**
     * 设置：活跃榜配置
     */
    @Override
    public void activeConfigPut(Map<String, Object> paramMap, CommonResponse response) {

        ActiveConfigBO activeConfigBO = BeanUtil.copyProperties(paramMap, ActiveConfigBO.class);

        if (activeConfigBO.getUserId() == null || activeConfigBO.getWeight() == null) {

            response.setSuccess(false);
            response.setErrMsg("参数不合法");
            return;

        }

        ServerUser serverUser = UserContainer.getUserById(activeConfigBO.getUserId());

        if (serverUser == null) {

            response.setSuccess(false);
            response.setErrMsg("用户不存在");
            return;

        }

        RMap<Long, Integer> map = redissonClient.getMap(ACTIVE_CONFIG_KEY, new JsonJacksonCodec());

        if (activeConfigBO.getWeight() < 0) {

            // 移除：值
            map.remove(activeConfigBO.getUserId());

        } else {

            // 设置：值
            map.put(activeConfigBO.getUserId(), activeConfigBO.getWeight());

        }

    }

    /**
     * 分页排序查询：活跃榜配置
     */
    @Override
    public void activeConfigPage(Map<String, Object> paramMap, CommonResponse response) {

        response.setSuccess(true);

        RMap<Long, Integer> map = redissonClient.getMap(ACTIVE_CONFIG_KEY, new JsonJacksonCodec());

        Map<Long, Integer> readAllMap = map.readAllMap();

        List<ActiveConfigBO> list = new ArrayList<>();

        for (Map.Entry<Long, Integer> item : readAllMap.entrySet()) {

            list.add(new ActiveConfigBO(item.getKey(), item.getValue()));

        }

        // 根据：权重，进行倒序排序
        list = list.stream().sorted(Comparator.comparing(ActiveConfigBO::getWeight, Comparator.reverseOrder()))
            .collect(Collectors.toList());

        HashMap<String, Object> dataMap = MapUtil.newHashMap();
        dataMap.put("data", list);
        dataMap.put("total", list.size());

        response.setData(dataMap);

    }

    /**
     * 获取：头像图片集合
     */
    @Override
    public void getHeadImageList(ActiveList.GetHeadImageListRequest request, ServerUser user) {

        List<Long> userIdSetList = request.getUserIdSetList();

        ActiveList.GetHeadImageListResponse.Builder builder = ActiveList.GetHeadImageListResponse.newBuilder();

        if (CollUtil.isEmpty(userIdSetList) || userIdSetList.size() > 30) {

            NetManager.sendMessage(S_C_GET_HEAD_IMAGE_LIST_RESPONSE_VALUE, builder, user);

            return;

        }

        HashSet<Long> userIdSet = new HashSet<>(userIdSetList);

        for (Long item : userIdSet) {

            ActiveList.GetHeadImageListItem.Builder itemBuilder = ActiveList.GetHeadImageListItem.newBuilder();

            itemBuilder.setUserId(item);

            builder.addList(itemBuilder); // 添加到返回值里

            ServerUser serverUser = UserContainer.getUserById(item);

            if (serverUser == null) {
                continue;
            }

            UserEntity userEntity = serverUser.getEntity();

            if (userEntity == null) {
                continue;
            }

            String headUrl = userEntity.getHeadUrl();

            if (StrUtil.isBlank(headUrl)) {

                itemBuilder.setHeadIndex(userEntity.getHeadIndex());
                continue;

            }

            String replaceHeadUrl = StrUtil.replace(headUrl, "/headImg.", "/headImg64x64.");

            if (FileUtil.exist(replaceHeadUrl)) { // 如果存在

                itemBuilder.setHeadImage(ImgUtil.toBase64(ImgUtil.read(FileUtil.file(replaceHeadUrl)), null));

            } else { // 如果不存在，看原文件是否存在，如果存在，则重新生成 64 * 64的文件

                if (FileUtil.exist(headUrl)) {

                    // 创建文件：64 * 64
                    File file64x64 = FileUtil.touch(replaceHeadUrl);

                    ImgUtil.scale(FileUtil.file(headUrl), file64x64, 64, 64, null);

                    itemBuilder.setHeadImage(ImgUtil.toBase64(ImgUtil.read(FileUtil.file(file64x64)), null));

                }

            }

        }

        NetManager.sendMessage(S_C_GET_HEAD_IMAGE_LIST_RESPONSE_VALUE, builder, user);

    }

}
