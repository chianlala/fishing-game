package com.maple.game.osee.manager.fishing.util;

import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.mapper.AgentMapper;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.OseeCutMoneyLogEntity;
import com.maple.game.osee.dao.log.mapper.OseeCutMoneyLogMapper;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.manager.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FishingUtil {

    @Autowired
    private OseeCutMoneyLogMapper cutMoneyLogMapper;

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private AgentMapper fishingRecordLogMapper;

    public static Long[] q0 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Double[] ap = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Long[] apt =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMax =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMin =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Double[] ap1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Long[] apt1 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMax1 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMin1 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Double[] ap2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Long[] apt2 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMax2 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMin2 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Double[] ap3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Long[] apt3 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMax3 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] recMin3 =
        {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L};

    public static Long[] pump = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] pumpNum = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceBuOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcBurstOne = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceBuTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcBurstTwo = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceBuThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcBurstThree = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceBuFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] gcBurstFour = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstOne1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recOne1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstTwo1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recTwo1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceOne1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceTwo1 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstOne2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recOne2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstTwo2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recTwo2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceOne2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceTwo2 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstOne3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recOne3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstTwo3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recTwo3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceOne3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceTwo3 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstOne4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recOne4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] burstTwo4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] recTwo4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceOne4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Long[] balanceTwo4 = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    public static Double[] gProd = {0D, 0D, 0D, 0D, 0D};

    public static Double[] lcf = {0D, 0D, 0D, 0D, 0D};

    public static Double[] dz = {0D, 0D, 0D, 0D, 0D};

    public static Double[] bfsMin = {0D, 0D, 0D, 0D, 0D};

    public static Double[] bfsMax = {0D, 0D, 0D, 0D, 0D};

    public static Double[] tp_c = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    public static Double[] tp_z = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    public static Double[] tp_g = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    public static Double[] tp_s = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] cxPercentage =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMax1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMin1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMaxNum1 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMinNum1 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMax2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMin2 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMaxNum2 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMinNum2 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMax3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMin3 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMaxNum3 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] peakMinNum3 =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropFreeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropRateGoldFreeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] bangTorpedoDropFreeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] bangTorpedoDropRateGoldFreeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] bossBugleRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropPerPayMoney =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropPerPayRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropPerExChangeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoDropPerExChangeRateMin =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoNotBangDropPerExChangeRateMin =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] rareTorpedoDropPerExChangeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] rareTorpedoDropPerExChangeRateMin =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] rareTorpedoNotBangDropPerExChangeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] rareTorpedoNotBangDropPerExChangeRateMin =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] torpedoNotBangDropPerExChangeRate =
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] FPMin = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] PXMin = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] FPMax = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] PXMax = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    public static Double[] Pdx = {10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0,
        10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0};

    public static Double[] Pdxp = {10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0,
        10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0};

    public static Double[] Xdx = {10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0,
        10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0};

    public static Double[] Xdxp = {10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0,
        10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0, 10000000.0};

    public static Double[] jcPercentage =
        {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};

    /**
     * 保存抽水记录
     *
     * @param player 玩家
     */
    public void saveCutProb(FishingGamePlayer player, int gameId) {
        ServerUser user = player.getUser();
        if (player.getCutMoney() != 0) {
            OseeCutMoneyLogEntity cutLog = new OseeCutMoneyLogEntity();
            cutLog.setUserId(user.getId());
            cutLog.setGame(gameId);
            cutLog.setCutMoney(player.getCutMoney());
            cutMoneyLogMapper.save(cutLog);
        }

        playerMapper.update(PlayerManager.getPlayerEntity(user));
    }

    public static double getQ0(int index) {
        return q0[index];
    }

    public static void setQ0(int index, long val) {
        q0[index] = val;
    }

    public static long getApt(int index) {
        return apt[index];
    }

    public static void setApt(int index, long val) {
        apt[index] = val;
    }

    public static double getAp(int index) {
        return ap[index];
    }

    public static void setAP(int index, double val) {
        ap[index] = val;
    }

}
