package com.jeesite.modules.osee.vo.game;

import com.jeesite.common.utils.excel.annotation.ExcelField;
import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * CDK传输实体类
 */
@Getter
@Setter
public class CdkVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1410560237622498789L;

    // JeeSite框架导出为Excel需要的注解
    @ExcelField(title = "CKDEY", attrName = "cdkey", fieldType = String.class)
    private String cdkey;       // CDK内容
    @ExcelField(title = "类型", attrName = "typeName", fieldType = String.class)
    private String typeName;    // CDK类型名
    @ExcelField(title = "CDK奖励", attrName = "rewards", fieldType = String.class)
    private String rewards;     // 奖励道具
    @ExcelField(title = "兑换者ID", attrName = "userId", fieldType = Long.class)
    private Long userId;        // 兑换者id
    @ExcelField(title = "兑换者昵称", attrName = "nickname", fieldType = String.class)
    private String nickname;    // 兑换者昵称

    private Long typeId;        // CDK类型id
    private Integer count;      // 添加的cdkey数量
    private Integer[] itemId;   // 添加的奖励物品id 1:金币 2:银行金币 3:奖券 4:钻石
    private Integer[] itemCount;// 添加的cdk数量

    private Integer used;       // 使用情况 0:不指定 1:未使用 2:已使用

    private Long userGameId;        // 兑换者游戏id

    private Long agentId; // 所属代理的用户 id

    private Long agentGameId; // 所属代理的游戏 id

    /**
     * cdk奖励物品的数据类型
     */
    public static class Reward {

        private Integer itemId;
        private Integer count;

        public Integer getItemId() {
            return itemId;
        }

        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}

