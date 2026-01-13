package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值明细数据传输实体类
 *
 * @author zjl
 */
public class ChangeAllVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -4490180356182504690L;


    //    private Long id;
//    private Long loginNum;
//    private Long goldAll;
//    private Long bossAll;
//    private Long critAll;
//    private Long lockAll;
//    private Long magicAll;
//    private Long diamondAll;
//    private Long frozenAll;
//    private String moneyChange;
//    private String dragonChange;
    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
