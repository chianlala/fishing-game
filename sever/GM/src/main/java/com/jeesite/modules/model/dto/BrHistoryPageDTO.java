package com.jeesite.modules.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class BrHistoryPageDTO extends MyPageDTO {

    private Long userId;

    private Long gameId;

    private Long roomIndex;

    private String nickname;

    /**
     * 创建时间范围：开始时间
     */
    private Date ctBeginTime;

    /**
     * 创建时间范围：结束时间
     */
    private Date ctEndTime;

}
