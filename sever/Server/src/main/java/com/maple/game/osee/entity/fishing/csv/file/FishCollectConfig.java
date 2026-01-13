package com.maple.game.osee.entity.fishing.csv.file;

import cn.hutool.json.JSONUtil;
import com.maple.engine.anotation.AppData;
import com.maple.engine.container.DataContainer;
import com.maple.engine.data.BaseCsvData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@AppData(fileUrl = "data/fishing/cfg_fish_collect.csv")
@Data
public class FishCollectConfig extends BaseCsvData {

    private String groupIdListStr;

    /**
     * 鱼组 idList，代码里面用
     */
    private List<Long> groupIdList;

    public List<Long> getGroupIdList() {
        return JSONUtil.toList(getGroupIdListStr(), Long.class).stream()
            .map(it -> DataContainer.handleId(getGameId(), it)).collect(Collectors.toList());
    }

}
