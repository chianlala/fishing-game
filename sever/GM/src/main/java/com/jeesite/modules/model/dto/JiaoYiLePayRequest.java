package com.jeesite.modules.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 请求参数
 * @author: Liny
 * @email: 2930251092@qq.com
 * @date: 2022/10/24 15:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JiaoYiLePayRequest {

    /**
     * 订单号
     */
    private String out_trade_no;

    /**
     * 订单名称（标题）
     */
    private String subject;

    /**
     * 订单金额
     */
    private String total_amount;

}
