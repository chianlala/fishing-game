package com.maple.game.osee.model.enums;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.maple.game.osee.util.MyRefreshFishingUtil;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountDetailGameStateEnum {

    public static final AccountDetailGameStateEnum OFFLINE = new AccountDetailGameStateEnum(0, "离线");

    public static final AccountDetailGameStateEnum GAME_HALL = new AccountDetailGameStateEnum(1, "游戏大厅");

    private final int value;
    private final String name;

    public static final Map<Integer, AccountDetailGameStateEnum> MAP = MapUtil.newHashMap();

    static {

        // List<AccountDetailGameStateEnum> accountDetailGameStateEnumList =
        // CollUtil.toList(JUNGLE_KINGDOMS, WILD_TURKEY, BEAST_LEGEND);
        //
        // for (AccountDetailGameStateEnum item : accountDetailGameStateEnumList) {
        //
        // MAP.put(item.getValue(), item);
        //
        // }

    }

    @NotNull
    public static AccountDetailGameStateEnum getByGameState(int roomIndex) {

        AccountDetailGameStateEnum accountDetailGameStateEnum = MAP.get(roomIndex);

        if (accountDetailGameStateEnum != null) {

            return accountDetailGameStateEnum;

        }

        String sceneName = MyRefreshFishingUtil.getSceneName(roomIndex);

        if (sceneName == null) {

            return OFFLINE;

        }

        accountDetailGameStateEnum = new AccountDetailGameStateEnum(roomIndex, sceneName);

        MAP.put(roomIndex, accountDetailGameStateEnum);

        return accountDetailGameStateEnum;

    }

}
