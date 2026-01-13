package com.maple.game.osee.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RandomUtil {

    /**
     * 是否命中给定概率
     *
     * @param p 概率(百分制)
     * @return 是否命中
     */
    public static boolean isHappen(Double p) {

        if (p == null) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble(100) < p;

    }

    /**
     * 根据权重返回元素
     *
     * @param list
     * @param weight
     * @param <T>
     * @return
     */
    public static <T> T getRandom(List<T> list, List<Double> weight) {

        if (list == null || weight == null) {
            throw new RuntimeException("参数为NULL");
        }

        if (list.size() != weight.size()) {
            throw new RuntimeException("请给每个位置设置权重");
        }

        return list.get(getRandomByWeight(weight));

    }

    /**
     * 根据权重的值随机返回权重所在位置的下标
     *
     * @param weight 权重
     * @return 命中的权重下标
     */
    public static int getRandomByWeight(List<Double> weight) {

        final double sum = weight.stream().reduce(Double::sum).orElse(0d);

        if (sum <= 0) {
            throw new RuntimeException("请设置正确的权重");
        }

        final double random = getRandom(0d, sum);

        double tmp = 0;

        for (int i = 0; i < weight.size(); i++) {

            tmp += weight.get(i);

            if (random < tmp) {
                return i;
            }

        }

        return -1;

    }

    /**
     * 从数组中获得指定个数(不会重复获取)
     *
     * @param list 样本
     * @param num  采样个数
     * @param <T>  样本类型
     * @return 采样结果
     */
    public static <T> List<T> getRandom(List<T> list, int num) {
        if (list == null) {
            throw new RuntimeException("参数list为NULL");
        }
        if (num == 0 || list.size() < num) {
            throw new RuntimeException("参数num设置不正确");
        }

        Collections.shuffle(list);

        return list.stream().limit(num).collect(Collectors.toList());
    }

    /**
     * 从连续的范围内返回指定个数的值
     *
     * @param origin 连续范围的起点
     * @param bound  连续范围的终点
     * @param num    返回的个数
     * @return 数组
     */
    public static List<Integer> getRandom(int origin, int bound, int num) {

        if (num <= 0 || origin < 0 || bound <= 0) {
            throw new RuntimeException("参数num设置不正确");
        }

        final List<Integer> list = IntStream.range(origin, bound).boxed().collect(Collectors.toList());
        Collections.shuffle(list);

        return list.stream().limit(num).collect(Collectors.toList());

    }

    /**
     * 从数组中获得一个元素
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getRandom(List<T> list) {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("参数为NULL");
        }

        return list.get(getRandom(0, list.size()));
    }

    public static int getRandom(int origin, int bound) {
        if (origin == bound) {
            return origin;
        }

        return ThreadLocalRandom.current().nextInt(Math.min(origin, bound), Math.max(origin, bound));
    }

    public static long getRandom(long origin, long bound) {
        if (origin == bound) {
            return origin;
        }

        return ThreadLocalRandom.current().nextLong(Math.min(origin, bound), Math.max(origin, bound));
    }

    public static double getRandom(double origin, double bound) {
        if (origin == bound) {
            return origin;
        }

        return ThreadLocalRandom.current().nextDouble(Math.min(origin, bound), Math.max(origin, bound));
    }
}
