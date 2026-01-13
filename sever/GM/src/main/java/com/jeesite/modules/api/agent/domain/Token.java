package com.jeesite.modules.api.agent.domain;

import java.io.Serializable;

public class Token implements Serializable {

    private static final long serialVersionUID = -6514367565229948212L;

    private Long id;        // 渠道代理id
    private String token;   // 登录token

    private String nickname; // 代理昵称

    public Token() {
    }

    public Token(Long id, String token, String nickname) {
        this.id = id;
        this.token = token;
        this.nickname = nickname;
    }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
