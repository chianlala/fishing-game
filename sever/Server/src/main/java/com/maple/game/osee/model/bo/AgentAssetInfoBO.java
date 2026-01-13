package com.maple.game.osee.model.bo;

import lombok.Data;

@Data
public class AgentAssetInfoBO {

    long moneyAll;

    long diamondAll;

    long skillBlackHoleAll;

    long skillTorpedoAll;

    long skillBitAll;

    long lotteryAll;

    // long draAll;
    //
    // long goldAll;

    long goldAllOrig; // 弹头总数，原始值

    long draAllOrig; // 龙晶总数，原始值

    public void addMoneyAll(long moneyAll) {
        this.moneyAll = this.moneyAll + moneyAll;
    }

    public void addDiamondAll(long diamondAll) {
        this.diamondAll = this.diamondAll + diamondAll;
    }

    // public void addDraAll(long draAll) {
    // this.draAll = this.draAll + draAll;
    // }

}
