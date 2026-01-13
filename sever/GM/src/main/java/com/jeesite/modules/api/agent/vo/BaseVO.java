package com.jeesite.modules.api.agent.vo;

import java.io.Serializable;

public class BaseVO implements Serializable {

    private static final long serialVersionUID = -551341794436259217L;

    private Long id = 0L;       // 代理玩家id
    private String token = "";  // 登录token凭证

    // 分页数据
    private Integer page;
    private Integer pageSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
