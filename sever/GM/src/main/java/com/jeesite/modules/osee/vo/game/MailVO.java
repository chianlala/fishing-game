package com.jeesite.modules.osee.vo.game;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮件发送数据传输实体
 *
 * @author Junlong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MailVO extends BaseVO {

    private static final long serialVersionUID = -4957860846068637516L;

    private Long receiverId; // 接收邮件的玩家ID 为空就是全服发送
    private String title; // 邮件标题
    private String content; // 邮件内容 最多500个字

    private Boolean sendCloseFlag; // 发送邮件之后，是否关闭页面

    private Long refId; // 关联的主键 id

    /**
     * 邮件类型 0 后台邮件（默认） 1 充值邮件 2 代理邮件 3 礼物赠送 4 大奖赛周榜 5 大奖赛日榜 6 击杀榜日榜 8 大奖赛月榜  9 击杀榜周榜 10 击杀榜月榜 11
     * 积分榜日榜 12 积分榜周榜 13 积分榜月榜 14 反馈回复
     */
    private Integer type;

    private Integer[] itemId;   // 添加的邮件物品id
    private Integer[] itemCount;// 添加的邮件物品数量

    private String mailType = "1"; // 1 游戏邮件 2 代理邮件

    private String toUserIds; // 代理ID，备注：为空则给全部代理发送，多个用英文逗号隔开

    private Long propsCount; // 发送金币数量

}
