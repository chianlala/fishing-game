package com.maple.game.osee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.model.dto.GameKillFishPageDTO;
import com.maple.game.osee.service.GmCommonService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GmCommonServiceImpl implements GmCommonService {

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        GmCommonServiceImpl.redissonClient = redissonClient;
    }

    /**
     * 分页排序查询：击杀鱼记录
     */
    @Override
    public void killFishListPage(Map<String, Object> paramMap, CommonResponse response) {

        GameKillFishPageDTO dto = BeanUtil.toBean(paramMap, GameKillFishPageDTO.class);

        StringBuilder condBuilder = new StringBuilder(" WHERE 1=1 ");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        long page = dto.getCurrent();
        long pageSize = dto.getPageSize();

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        if (dto.getUserId() != null) {
            condBuilder.append("AND user_id = ").append(dto.getUserId()).append(" ");
        }
        if (StrUtil.isNotBlank(dto.getUserName())) {
            condBuilder.append("AND user_name = '").append(dto.getUserName()).append("' ");
        }

        if (dto.getType() != null) {
            condBuilder.append("AND type = ").append(dto.getType()).append(" ");
        }
        if (dto.getGameState() != null) {
            condBuilder.append("AND game_state = ").append(dto.getGameState()).append(" ");
        }
        if (dto.getIndividualControlValueType() != null) {
            if (dto.getIndividualControlValueType() == 1) {
                condBuilder.append("AND individual_control_value = 0 ");
            } else {
                condBuilder.append("AND individual_control_value != 0 ");
            }
        }

        if (StrUtil.isNotBlank(dto.getKillFishName())) {
            condBuilder.append("AND kill_fish_name = '").append(dto.getKillFishName()).append("' ");
        }
        if (StrUtil.isNotBlank(dto.getCtBeginTime())) {
            condBuilder.append("AND create_ms >= ").append(DateUtil.parse(dto.getCtBeginTime()).getTime()).append(" ");
        }
        if (StrUtil.isNotBlank(dto.getCtEndTime())) {
            condBuilder.append("AND create_ms <= ").append(DateUtil.parse(dto.getCtEndTime()).getTime()).append(" ");
        }

        HashMap<Object, Object> data = MapUtil.newHashMap();

        response.setData(data);

    }


}
