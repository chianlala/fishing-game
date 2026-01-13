package com.maple.game.osee.model.bo;

import lombok.Data;

@Data
public class GiveHandleMailBO {

    /**
     * 邮件主键 id
     */
    private Long mailId;

    /**
     * 操作码：0 拒绝 1 同意
     */
    private Integer code;

}
