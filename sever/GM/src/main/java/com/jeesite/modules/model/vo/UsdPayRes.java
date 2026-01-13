package com.jeesite.modules.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsdPayRes {
    private String status;
    private String status_mes;
    private String order_data;
    private UsdPayData usdPayData;
}
