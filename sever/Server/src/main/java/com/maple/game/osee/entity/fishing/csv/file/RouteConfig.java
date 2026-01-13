package com.maple.game.osee.entity.fishing.csv.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteConfig {

    /**
     * 轨迹 id
     */
    private long routeId;

    /**
     * 轨迹时长（秒）
     */
    private float time;

}
