package com.maple.game.osee.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.maple.game.osee.pojo.fish.AbsFish;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class FishMultGenerateUtil {

    public static final String FISH_MULT_REPOSITORY = System.getProperty("user.dir") + "/FishMultRepository";

    /**
     * 鱼倍数，缓存 map
     */
    public static final ConcurrentHashMap<String, TreeMap<Long, byte[]>> FISH_MULT_CACHE_MAP =
            MapUtil.newConcurrentHashMap();

    /**
     * 通过 absFish，获取该鱼的所有倍数
     */
    @Nullable
    public static TreeMap<Long, byte[]> getTreeMapByAbsFish(AbsFish absFish) {

        String filePath = FISH_MULT_REPOSITORY + "/Fish" + absFish.getClass().getSimpleName() + ".json";

        return getTreeMapByFile(FileUtil.file(filePath));

    }

    @Nullable
    private static TreeMap<Long, byte[]> getTreeMapByFile(File file) {

        if (file.getName().startsWith("Fish") == false) {
            return null;
        }

        if (FileUtil.exist(file)) {

            try {

                TreeMap<Long, byte[]> resultTreeMap = FISH_MULT_CACHE_MAP.get(file.getName());

                if (resultTreeMap == null) {

                    resultTreeMap = JSONUtil.toBean(FileUtil.readUtf8String(file),
                            new TypeReference<TreeMap<Long, byte[]>>() {
                            }, false);

                    FISH_MULT_CACHE_MAP.put(file.getName(), resultTreeMap);

                }

                return resultTreeMap;

            } catch (Exception ignored) {

            }

        }

        return null;

    }

    /**
     * 通过 absFish，获取该鱼 最接近的倍数
     *
     * @return 最接近的倍数
     */
    @NotNull
    public static <T> Long getValueByTreeMap(TreeMap<Long, T> treeMap, Long randomMoney) {
        Long result = treeMap.floorKey(randomMoney);

        if (result == null) {

            result = treeMap.ceilingKey(randomMoney);

        }

        return result;

    }

}
