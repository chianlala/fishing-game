package com.jeesite.modules.osee.vo.game;

import java.io.Serializable;

/**
 * cdk类型传输实体类
 */
public class CdkTypeVO implements Serializable {

    private static final long serialVersionUID = -1700815393274149399L;

    private String name;    // cdk类型名

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
