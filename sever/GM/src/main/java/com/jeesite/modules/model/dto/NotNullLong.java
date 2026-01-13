package com.jeesite.modules.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotNullLong {

    /**
     * 值
     */
    @NotNull
    private Long value;

}
