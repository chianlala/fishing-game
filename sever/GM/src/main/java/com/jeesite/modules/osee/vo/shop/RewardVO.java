package com.jeesite.modules.osee.vo.shop;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RewardVO {

    private int id;

    private long playerId;

    private int gold;

    private int diamond;

    private int lowerBall;

    private int middleBall;

    private int highBall;

    private int skillLock;

    private int skillFast;

    private int skillCrit;

    private int skillFrozen;

    private int bossBugle;

}
