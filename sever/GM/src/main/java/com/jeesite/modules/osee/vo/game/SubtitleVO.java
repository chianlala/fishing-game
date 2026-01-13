package com.jeesite.modules.osee.vo.game;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 游走字幕传输实体类
 */
public class SubtitleVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 4433535301916305087L;

    private Long id;                // ID

    private String content;         // 内容
    private Integer intervalTime;   // 间隔时间(min)

    private Date effectiveTime;     // 生效时间(时间戳)
    private Date failureTime;       // 失效时间(时间戳)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }
}
