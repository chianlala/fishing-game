package com.jeesite.modules.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: Liny
 * @email: 2930251092@qq.com
 * @date: 2022/11/1 19:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JiaoYiLeResponseData {

    private String mchid;

    private String out_trade_no;

    private String trade_no;

    private String total_amount;

    private String float_amount;

    private String payurl;

    private String payInfo;

    private String trade_type;

}
