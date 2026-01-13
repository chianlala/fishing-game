package com.maple.game.osee.entity.fishing.csv.file;

import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;

/**
 * 锻造/合成/强化表
 */
@AppData(fileUrl = "data/fishing/cfg_forging.csv")
public class ForgingConfig extends BaseCsvData {

    /**
     * 铸造名称
     */
    private String forgingName;

    /**
     * 锻造类型
     */
    private int forgingType;

    /**
     * 消耗组合
     */
    private String forgingGroup;

    public String getForgingName() {
        return forgingName;
    }

    public void setForgingName(String forgingName) {
        this.forgingName = forgingName;
    }

    public int getForgingType() {
        return forgingType;
    }

    public void setForgingType(int forgingType) {
        this.forgingType = forgingType;
    }

    public String getForgingGroup() {
        return forgingGroup;
    }

    public void setForgingGroup(String forgingGroup) {
        this.forgingGroup = forgingGroup;
    }
}
