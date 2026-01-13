package com.jeesite.modules.osee.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 1688 公告实体类
 */
public class GameNoticeEntity implements Serializable {

    private static final long serialVersionUID = 6454770930028030394L;

    /**
     * 数据id
     */
    protected long id = 100001;

    /**
     * 创建时间
     */
    protected Date createTime = new Date();

    /**
     * 序号
     */
    private int index;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 生效时间
     */
    private Date startTime;

    /**
     * 失效时间
     */
    private Date endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

}
