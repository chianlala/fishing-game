package com.jeesite.modules.osee.vo.shop;


import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;

import java.util.Date;

@Data
public class GoldenPigFormVO extends BaseVO {

    private static final long serialVersionUID = 3921553166795579010L;

    private Long type;

    private Date startTime;
    private Date endTime;

    private Long roomIndex;

    private Long userId;


    private String nickName;

}
