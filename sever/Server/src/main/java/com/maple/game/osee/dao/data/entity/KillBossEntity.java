package com.maple.game.osee.dao.data.entity;

import java.util.Date;

import lombok.Data;

@Data
public class KillBossEntity {

    private long id;
    private long userId;
    private String nickName;
    private long batterLevel;
    private long mult;
    private String bossName;
    protected Date createTime;
    private Integer roomIndex;
    private String award;

    /**
     * 血池概率击杀时，取的鱼的倍数
     */
    private Integer bloodPoolFloatKillValue;

}
