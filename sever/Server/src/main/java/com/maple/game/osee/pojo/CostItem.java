package com.maple.game.osee.pojo;

import com.maple.game.osee.entity.ItemId;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * 价目表
 */
@AllArgsConstructor
public enum CostItem {
    // 弹头兑换钻石
    DT_ZS_1(30001, 1, ItemId.GOLD_TORPEDO, 10, ItemId.DIAMOND),
    DT_ZS_10(30002, 10, ItemId.GOLD_TORPEDO, 100, ItemId.DIAMOND),
    DT_ZS_100(30003, 100, ItemId.GOLD_TORPEDO, 1000, ItemId.DIAMOND),
    DT_ZS_1000(30004, 1000, ItemId.GOLD_TORPEDO, 10000, ItemId.DIAMOND),

    ;

    public long id;
    public int cost;
    public ItemId costType;
    public int num;
    public ItemId numType;

    public static CostItem getById(final long id) {
        return Arrays.stream(CostItem.values()).filter(i -> i.id == id).findFirst().orElse(null);
    }
}
