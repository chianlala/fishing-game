package com.jeesite.modules.osee.vo.game;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告传输实体类
 */
public class NoticeVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -7080775449192830112L;

    private Long id;            // ID

    private String title;       // 标题
    private String content;     // 内容

    private Date startTime;     // 生效时间 (时间戳)
    private Date endTime;       // 失效时间 (时间戳)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
