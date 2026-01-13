package com.jeesite.modules.osee.vo;

import com.jeesite.common.entity.Page;

import java.io.Serializable;

/**
 * 基础传输实体的共有属性
 */
public class BaseVO implements Serializable {

    private static final long serialVersionUID = 3281959010348324399L;

    private Integer page;           // 页码
    private Integer pageSize;       // 单页数量

    /**
     * 设置分页信息
     */
    public void setPageInfo(Page page) {
        if (page != null) {
            this.page = page.getPageNo();
            this.pageSize = page.getPageSize();
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
