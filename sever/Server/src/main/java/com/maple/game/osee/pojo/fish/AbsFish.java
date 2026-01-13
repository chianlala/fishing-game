package com.maple.game.osee.pojo.fish;

import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.CacheFindHelper;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.proto.fishing.FishBossMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.CallBack;
import com.maple.gamebase.data.fishing.BaseFishingRoom;

import cn.hutool.core.text.StrBuilder;
import lombok.Data;

/**
 * 鱼的总类
 */
@Data
public abstract class AbsFish implements IFish {

    protected FishStruct fish;
    protected FishConfig config; // 注意：这个对象不要使用，不然会出现并发问题
    protected ServerUser user;
    protected String key;
    protected String bloodPoolKey; // 血池控制倍数的 redisKey
    protected long randomMoney; // 普通的倍数
    protected long bloodPoolRandomMoney; // 血池控制倍数
    protected Byte type; // 1 正常范围（默认） 2 回收范围 3 爆发范围
    protected NewBaseFishingRoom room;

    private Integer fishKillResponseValue; // 死鱼之后，回的 code

    public Integer getFishKillResponseValue() {
        return fishKillResponseValue;
    }

    protected FishBossMessage.FishBossMultipleResponse.Builder message;

    protected int computeMultipleType; // 计算倍数的类型：0 暂时没有 1 根据特殊规则生成倍数 2 从配置表中获取倍数 3 固定倍数 4 不需要重新取值

    public int getComputeMultipleType() {
        return computeMultipleType;
    }

    public void setComputeMultipleType(int computeMultipleType) {
        this.computeMultipleType = computeMultipleType;
    }

    // 使用：getBloodPoolMultiple 来获取
    private long getBloodPoolRandomMoney() {
        return bloodPoolRandomMoney;
    }

    // 最小的倍数
    private double minRandomMoney = 0d;

    // 最大的倍数
    private double maxRandomMoney = 0d;

    // 从倍数文件里面取倍数时，转换的值
    private int multValue = 1;

    private String jsonStr;

    public int getMultValue() {
        return multValue;
    }

    public boolean getSlotFlag() {
        return false;
    }

    public AbsFish(FishStruct fish, FishConfig config) {

        byte type = getType() == null ? (byte)1 : getType();

        setFish(fish);
        setConfig(config);
        setKey("USER:BOSS:MULT:" + config.getModelId() + ":" + type);
        setBloodPoolKey("USER:BLOOD_POOL:MULT:" + config.getModelId() + ":" + type);

    }

    public AbsFish(FishStruct fish, FishConfig config, ServerUser user) {

        this(fish, config);

        if (user != null) {

            setKey(getKey() + ":" + user.getId());

            setBloodPoolKey(getBloodPoolKey() + ":" + user.getId());

        }

        setUser(user);

    }

    public AbsFish(FishStruct fish, FishConfig config, ServerUser user, NewBaseFishingRoom room) {

        this(fish, config, user);
        setRoom(room);

    }

    @Override
    public abstract FishMultipleHelperDTO onlyComputeMultiple();

    @Override
    public abstract long computeMultiple(String key);

    @Override
    public long getMultiple() {

        // 获取鱼的倍数
        // 先从缓存中获取 缓存中不存 则执行computeMultiple方式计算倍数并进行缓存
        randomMoney = CacheFindHelper.find(key, 0L, this::computeMultiple);

        return randomMoney;

    }

    @Override
    public long getBloodPoolMultiple() {
        // 获取鱼的 血池控制倍数，备注：要想调用 computeMultiple时，要先设置：type值
        bloodPoolRandomMoney = CacheFindHelper.find(bloodPoolKey, 0L, this::computeMultiple);
        return bloodPoolRandomMoney;
    }

    @Override
    public void afterTheKill(BaseFishingRoom gameRoom, long winMoney, String key) {
        RedisHelper.remove(key);
    }

    /**
     * messageByteArr，击杀鱼之后的处理
     */
    public abstract void afterKillByMessageByteArr(BaseFishingRoom gameRoom, long winMoney, byte[] messageByteArr);

    public TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder getDoubleBuilder() {
        return TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
    }

}
