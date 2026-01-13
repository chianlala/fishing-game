package com.jeesite.modules.api.agent.vo;

import java.util.Date;

public class RateFormVO extends BaseVO {

    private static final long serialVersionUID = 3921553166795579010L;


    private Date startTime;
    private Date endTime;
    private String dayTime;

    private String rate;

    private String arppu;

    private String arpu;

    private String newRate;

    private String newArppu;

    private String newArpu;

    private String newPayNum;

    private String loginNum;

    private String payNum;

    private String allMoney;

    private Long agentId;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getArppu() {
        return arppu;
    }

    public void setArppu(String arppu) {
        this.arppu = arppu;
    }

    public String getArpu() {
        return arpu;
    }

    public void setArpu(String arpu) {
        this.arpu = arpu;
    }

    public String getNewRate() {
        return newRate;
    }

    public void setNewRate(String newRate) {
        this.newRate = newRate;
    }

    public String getNewArppu() {
        return newArppu;
    }

    public void setNewArppu(String newArppu) {
        this.newArppu = newArppu;
    }

    public String getNewArpu() {
        return newArpu;
    }

    public void setNewArpu(String newArpu) {
        this.newArpu = newArpu;
    }

    public String getNewPayNum() {
        return newPayNum;
    }

    public void setNewPayNum(String newPayNum) {
        this.newPayNum = newPayNum;
    }

    public String getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(String loginNum) {
        this.loginNum = loginNum;
    }

    public String getPayNum() {
        return payNum;
    }

    public void setPayNum(String payNum) {
        this.payNum = payNum;
    }

    public String getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(String allMoney) {
        this.allMoney = allMoney;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
}
