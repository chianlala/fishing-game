package com.jeesite.modules.osee.vo.game;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家数据传输实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KillBossVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -5591142220317370957L;
    private Date startTime;
    private Date endTime;
    private Long playerId;      // 玩家id
    private Integer roomIndex;
    private Integer userId;
    private String bossName; // boss名称
}

