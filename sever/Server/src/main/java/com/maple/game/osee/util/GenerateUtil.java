package com.maple.game.osee.util;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateUtil {

    /**
     * 获取：对应的倍数缓存
     */
    @Nullable
    public static TreeMap<Long, Set<byte[]>> getTreeMapByFile(File file,
        ConcurrentHashMap<String, TreeMap<Long, Set<byte[]>>> map) {

        if (FileUtil.exist(file)) {

            TreeMap<Long, Set<byte[]>> resultTreeMap = map.get(file.getName());

            if (resultTreeMap == null) {

                resultTreeMap = JSONUtil.toBean(FileUtil.readUtf8String(file),
                    new TypeReference<TreeMap<Long, Set<byte[]>>>() {}, false);

                map.put(file.getName(), resultTreeMap);

                // log.info("文件名：{}，最小值：{}，最大值：{}", file.getName(), resultTreeMap.higherKey(0L) / 10000d,
                // resultTreeMap.lastKey() / 10000d);

            }

            return resultTreeMap;

        }

        return null;

    }

    /**
     * 写入字符串，到文件里面
     */
    public static void writeUtf8StringToFile(String fileName, TreeMap<Long, Set<byte[]>> treeMap) {

        String filePath = FishMultGenerateUtil.FISH_MULT_REPOSITORY + "/" + fileName + ".json";

        if (FileUtil.exist(filePath)) { // 添加，已经存在的数据

            String readUtf8String = FileUtil.readUtf8String(filePath);

            HashMap<Long, Set<byte[]>> hashMap =
                JSONUtil.toBean(readUtf8String, new TypeReference<HashMap<Long, Set<byte[]>>>() {}, false);

            for (Map.Entry<Long, Set<byte[]>> item : hashMap.entrySet()) {

                Set<byte[]> byteArrSet = treeMap.computeIfAbsent(item.getKey(), k -> new HashSet<>());

                if (byteArrSet.size() > 100) {
                    continue;
                }

                byteArrSet.addAll(item.getValue());

            }

        }

        FileUtil.writeUtf8String(JSONUtil.toJsonStr(treeMap), filePath);

    }

    /**
     * 获取：一组数据
     */
    public static byte[] getByteArr(long multipleTemp, TreeMap<Long, Set<byte[]>> treeMap,
        CallBack<Integer> realMultipleCallBack) {

        long multiple = FishMultGenerateUtil.getValueByTreeMap(treeMap, multipleTemp);

        if (realMultipleCallBack != null) {

            realMultipleCallBack.setValue((int)multiple);

        }

        // if (multiple > multipleTemp) {

        // log.info("期望倍数：{}，实际倍数：{}", multipleTemp, multiple);

        // }

        Set<byte[]> byteArrSet = treeMap.get(multiple);

        return RandomUtil.randomEle(new ArrayList<>(byteArrSet));

    }

}
