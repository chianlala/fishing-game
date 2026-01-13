package com.jeesite.modules.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRowHandler;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.lang.annotation.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * csv工具类
 */
public class MyCsvUtil {

    @Data
    public static class BaseCsv {

        private long id;

        private int gameId;

    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    public @interface CsvAnnotation {

        /**
         * 文件路径
         */
        String fileUrl();

        /**
         * 文件编码
         */
        String charSet() default "GBK";

    }

    public static final Map<Class<? extends BaseCsv>, Map<Long, BaseCsv>> MAP = new SafeConcurrentHashMap<>();

    /**
     * 获取：集合信息
     */
    public static <T extends BaseCsv> Map<Long, T> getCsvMap(Class<T> clazz) {

        Map<Long, BaseCsv> result = MAP.get(clazz);

        if (CollUtil.isEmpty(result)) {

            synchronized (clazz) {

                result = MAP.get(clazz); // 再获取一次

                if (CollUtil.isEmpty(result)) {

                    return (Map<Long, T>) initCsv(clazz);

                }

            }

        }

        return (Map<Long, T>) result;

    }

    /**
     * 初始化：csv数据
     */
    private static Map<Long, BaseCsv> initCsv(Class<? extends BaseCsv> clazz) {

        CsvAnnotation annotation = clazz.getAnnotation(CsvAnnotation.class);

        String mountPath = MySettingUtil.MOUNT_PATH == null ? "" : MySettingUtil.MOUNT_PATH;

        BufferedInputStream inputStream = FileUtil.getInputStream(mountPath + annotation.fileUrl());

        BufferedReader reader = IoUtil.getReader(inputStream,
            Charset.forName(annotation.charSet()));

        CsvReadConfig csvReadConfig = new CsvReadConfig();

        csvReadConfig.setHeaderLineNo(0); // 设置：头部为 0

        CsvReader csvReader = CsvUtil.getReader(reader, csvReadConfig);

        Map<Long, BaseCsv> secondMap = new HashMap<>();

        CsvRowHandler csvRowHandler = row -> {

            if (row.getOriginalLineNumber() == 1) { // 第二行的数据不要，因为这行是描述相关的内容
                return;
            }

            BaseCsv baseCsv = row.toBean(clazz);

            int gameId = baseCsv.getGameId();

            // 只添加，gameId相等的数据
            if (checkGameId(gameId)) {

                secondMap.put(baseCsv.getId(), baseCsv);

            }

        };

        csvReader.read(csvRowHandler); // 执行：读取

        MAP.put(clazz, secondMap); // 添加到：map里

        return secondMap;

    }

    /**
     * 检查：gameId
     */
    private static boolean checkGameId(int gameId) {

        if (gameId == 0) {
            return true;
        }

        String gameIds = MySettingUtil.SETTING.getStr("gameIds");

        if (StrUtil.isBlank(gameIds)) {
            return true;
        }

        List<String> gameIdList = StrUtil.splitTrim(gameIds, ",");

        return gameIdList.contains(String.valueOf(gameId));

    }

}
