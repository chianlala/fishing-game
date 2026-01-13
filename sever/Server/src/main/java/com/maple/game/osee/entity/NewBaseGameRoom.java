package com.maple.game.osee.entity;

import java.util.concurrent.atomic.AtomicInteger;

import com.maple.gamebase.data.fishing.BaseFishingRoom;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class NewBaseGameRoom extends BaseFishingRoom {

    public abstract int getRoomIndex(); // 必须指定房间的 index

    public abstract void setRoomIndex(int roomIndex);

    private long addRobotTs = -1; // 下一次添加机器人的时间戳

    private int robotTotal = 0; // 机器人总数

    private long nextSetRobotTotalTs = -1; // 下一次设置机器人总数的时间，间隔时间一般为 5秒

    private AtomicInteger addRobotNum = new AtomicInteger(0); // 需要增加或者删除机器人的数量，正数：增加，负数：减少

}
