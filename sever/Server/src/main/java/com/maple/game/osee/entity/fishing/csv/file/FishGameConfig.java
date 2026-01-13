package com.maple.game.osee.entity.fishing.csv.file;

import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 游戏相关配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AppData(fileUrl = "data/fishing/cfg_fish_game_configuration.csv")
public class FishGameConfig extends BaseCsvData {

    /**
     * 赠送审核：1不需要审核；2需要审核；
     */
    private Integer giveAuditType;

    /**
     * 赠送限制：0无限制；1玩家之间不能赠送；2代理之间不能赠送；3.没有绑定关系的账号间不能赠送
     */
    private Integer giveLimitType;

    /**
     * 一级代理是否能开通：1一级代理可以在代理后台开通二级代理；2一级代理不能开通二级代理
     */
    private Integer createSecondAgent;

    /**
     * 代理后台是否生成下载链接：1是；2否
     */
    private Integer syncApk;

    /**
     * 代理后台是否只能一个账户登录：1 是 0 否
     */
    private Integer agentOnlyOne;

    /**
     * 是否发放救济金：0 否 1 是
     */
    private Integer reliefMoneyFlag = 0;

    /**
     * 是否启用 slots：0 不启用 1 启用
     */
    private Integer slotsFlag = 0;

    /**
     * 代理是否能加入房间进行游戏：0是，1否，默认：是
     */
    private Integer agentJoinGameFlag = 0;

    /**
     * 绑定代理时的类型：1 （默认）通过 gameId绑定 2 通过邀请码绑定
     */
    private Integer agentBindType = 1;

    /**
     * 代理层次数量，0 表示不能绑定代理 1 表示可以有一层代理关系，即一级代理绑定用户 2 表示可以有两层代理关系，即二级代理绑定用户 以此类推 最大值为 4，即四级代理绑定用户
     */
    private Integer agentHierarchyNumber = 1;

    public Integer getAgentHierarchyNumber() {
        return agentHierarchyNumber > 4 ? 4 : agentHierarchyNumber;
    }

}
