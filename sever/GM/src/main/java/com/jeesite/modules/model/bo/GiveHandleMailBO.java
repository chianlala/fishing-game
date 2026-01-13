package com.jeesite.modules.model.bo;

import lombok.Data;

@Data
public class GiveHandleMailBO {

    /**
     * 邮件主键 id
     */
    private Long mailId;

    /**
     * 操作码
     */
    private Integer code;

}
