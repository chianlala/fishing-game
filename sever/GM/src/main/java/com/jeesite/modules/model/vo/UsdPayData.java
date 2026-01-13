package com.jeesite.modules.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsdPayData {
    private String mer_no;
    private String order_no;
    private String order_amount;

}
