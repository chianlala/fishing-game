package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼物赠送记录数据传输实体
 *
 * @author Junlong
 */
@Getter
@Setter
public class GiveVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -2932694888774626744L;

    private Long playerId;          // 礼物赠送，被赠玩家的ID

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    private Long agentId;

    private Long fromPlayerId;          // 赠送人ID
    private Long toPlayerId;          // 接收人ID
    private Long fromGameId;          // 赠送游戏ID
    private Long toGameId;          // 接收游戏ID

    private Integer mailState;          // 状态

    /**
     * 发送的物品数量，最小值
     */
    private Long propsCountMin;

    /**
     * 发送的物品数量，最大值
     */
    private Long propsCountMax;

}
