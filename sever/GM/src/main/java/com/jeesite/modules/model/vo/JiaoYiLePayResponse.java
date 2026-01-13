package com.jeesite.modules.model.vo;

/**
 * @description:
 * @author: Liny
 * @email: 2930251092@qq.com
 * @date: 2022/11/1 19:16
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright 2022 json.cn
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JiaoYiLePayResponse {

    private int code;

    private String msg;

    private JiaoYiLeResponseData data;


}
