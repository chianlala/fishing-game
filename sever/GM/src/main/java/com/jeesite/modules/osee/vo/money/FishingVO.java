package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class FishingVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -687172645444732504L;

    private Date startTime;

    private Date endTime;

    private Long playerId;

    private Integer roomIndex; // 捕鱼场次

    private Integer gameType; // 游戏类型：1 fishing game 2 slots game

}
