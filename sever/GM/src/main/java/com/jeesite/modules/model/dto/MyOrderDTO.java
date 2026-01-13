package com.jeesite.modules.model.dto;

import lombok.Data;

@Data
public class MyOrderDTO {

    /**
     * 排序的字段名
     */
    private String name;

    /**
     * ascend（升序，默认） descend（降序）
     */
    private String value;

}
