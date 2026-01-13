package com.jeesite.modules.osee.vo.game;

import lombok.Data;

import java.util.List;

/**
 * 轮盘概率传输实体类
 *
 * @author zjl
 */
@Data
public class LotteryVO {

    private List<Lottery> itemList; // 奖励集合

    @Data
    public static class Lottery {

        private int probability;
        private List<Reward> rewardList; // 一个奖项对应的奖励，一对多
    }

    @Data
    public static class Reward {

        private String reward;
        private int num;
    }

    public boolean checkSum100() {
        return itemList.stream().mapToInt(Lottery::getProbability).sum() == 100;
    }

}
