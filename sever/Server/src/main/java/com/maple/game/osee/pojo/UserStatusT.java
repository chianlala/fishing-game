package com.maple.game.osee.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserStatusT {
    private StatusT status;
    private Integer t;
    /**
     * -1回收,0平衡,1爆发,
     */
    private Byte type;

    /**
     * 额外参数：控制类型：子分类：1 默认
     */
    private Byte staticType;

    public UserStatusT(StatusT status, Integer t, Byte type) {
        this.status = status;
        this.t = t;
        this.type = type;
        this.staticType = 1; // 1 默认
    }

    /**
     * T 状态枚举
     */
    @AllArgsConstructor
    @Getter
    public enum StatusT {
        SINGLE(0, "个控"), // 个控
        HISTORY(1, "历史控制"), // 历史控
        TODAY(2, "今日控制"), // 今日控
        SITE(3, "场控"), // 场控
        SERVICE(4, "全服控制"), // 全服控
        FLARE(5, "爆发控制"), // 爆发
        FLOAT(5, "概率控制"), // 概率浮动控制
        BLOOD_POOL(5, "血池控制"), // 血池控制：staticType说明：1 默认 2 消产概率击杀
        USUAL(5, "正常") // 正常取值
        ;

        private int priority;
        private String name;

    }

}
