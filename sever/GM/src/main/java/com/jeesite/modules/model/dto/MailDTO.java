package com.jeesite.modules.model.dto;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class MailDTO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -4490180356182504690L;
    private Long id;
    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)
    private String username;
    private String title;
    private Integer state;
    private String createName;

    private Boolean receive; // 是否领取

    private Long gameId; // 游戏ID

}
