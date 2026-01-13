package com.jeesite.modules.osee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 项目配置
 *
 * @author Junlong
 */
@Configuration
public class ProjectConfig {

    @Value("${project.name}")
    private String name;

    @Value("${project.code}")
    private String code;

    @Value("${project.server}")
    private String server;

    @Value("${project.port}")
    private Integer port;

    public String getServer() {
        if (port == null || port == 80) {
            return server;
        } else {
            return server + ":" + port;
        }
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
