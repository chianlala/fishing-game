package com.maple.game.osee.pojo.fish;

import com.maple.game.osee.proto.fishing.FishBossMessage;
import lombok.Data;

@Data
public class FishMultipleHelperDTO {

    private int num;
    private int num1;
    private int num2;

    private String jsonStr; // json字符串

    private long randomMoney; // 普通的倍数

    private FishBossMessage.FishBossMultipleResponse.Builder message;

}
