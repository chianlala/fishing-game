package com.jeesite.modules.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SetSpecifyFishKillDTO {

    private List<Long> fishIdArr;

    private List<Integer> fishCountArr;

    private Long userId;

}
