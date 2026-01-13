package com.jeesite.modules.model.dto;

import cn.hutool.core.convert.Convert;
import com.jeesite.common.entity.Page;
import lombok.Data;

/**
 * 分页参数，查询所有：pageSize = -1，默认：current = 1，pageSize = 10
 */
@Data
public class MyPageDTO {

    /**
     * 第几页
     */
    private long current = 1;

    /**
     * 每页显示条数
     */
    private String pageSize = "10";

    /**
     * 排序字段
     */
    private MyOrderDTO order;

    public void setByPage(Page page) {
        setCurrent(page.getPageNo());
        setPageSize(Convert.toStr(page.getPageSize()));
    }

}
