package com.maple.game.osee.entity.tribe;

import java.util.Date;

public class Tribe {

    private Long id;
    private Long userId;
    private Long userNum;
    private Long realNum;
    private String name;
    private String userName;
    private String context;
    private Long vipRestrict;
    private Long levelRestrict;
    private Long verificationRestrict;
    private Long fJurisdiction;
    private Long tJurisdiction;
    private Long sJurisdiction;
    private Long iJurisdiction;
    private Long wJurisdiction;
    private Long lJurisdiction;
    private String headUrl;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserNum() {
        return userNum;
    }

    public void setUserNum(Long userNum) {
        this.userNum = userNum;
    }

    public Long getRealNum() {
        return realNum;
    }

    public void setRealNum(Long realNum) {
        this.realNum = realNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getVipRestrict() {
        return vipRestrict;
    }

    public void setVipRestrict(Long vipRestrict) {
        this.vipRestrict = vipRestrict;
    }

    public Long getLevelRestrict() {
        return levelRestrict;
    }

    public void setLevelRestrict(Long levelRestrict) {
        this.levelRestrict = levelRestrict;
    }

    public Long getVerificationRestrict() {
        return verificationRestrict;
    }

    public void setVerificationRestrict(Long verificationRestrict) {
        this.verificationRestrict = verificationRestrict;
    }

    public Long getfJurisdiction() {
        return fJurisdiction;
    }

    public void setfJurisdiction(Long fJurisdiction) {
        this.fJurisdiction = fJurisdiction;
    }

    public Long gettJurisdiction() {
        return tJurisdiction;
    }

    public void settJurisdiction(Long tJurisdiction) {
        this.tJurisdiction = tJurisdiction;
    }

    public Long getsJurisdiction() {
        return sJurisdiction;
    }

    public void setsJurisdiction(Long sJurisdiction) {
        this.sJurisdiction = sJurisdiction;
    }

    public Long getiJurisdiction() {
        return iJurisdiction;
    }

    public void setiJurisdiction(Long iJurisdiction) {
        this.iJurisdiction = iJurisdiction;
    }

    public Long getwJurisdiction() {
        return wJurisdiction;
    }

    public void setwJurisdiction(Long wJurisdiction) {
        this.wJurisdiction = wJurisdiction;
    }

    public Long getlJurisdiction() {
        return lJurisdiction;
    }

    public void setlJurisdiction(Long lJurisdiction) {
        this.lJurisdiction = lJurisdiction;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
