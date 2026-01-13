package com.maple.game.osee.util;

import java.util.Random;
import java.util.function.Supplier;

import cn.hutool.core.util.NumberUtil;

public class MyMathUtil {

    public static void main(String[] args) {

        // mz 化成 分子为 1，然后直接取 分母，然后再取分母的正态分布

        // long l1 = System.currentTimeMillis();

        // List<Double> list = new ArrayList<>();
        // for (int i = 0; i < 10000; i++) {
        // list.add(MyMathUtil.normalDistribution(3, 3 * 3 / 9));
        // list.add(MyMathUtil.normalDistribution(10, 10 * Math.sqrt(10)));
        // }

        // for (int i = 0; i < 10000; i++) {
        // MyMathUtil.normalDistribution(3, 3 * 3 / 9);
        // }

        // 预览图：https://ambrosecdmeng.github.io/normalDistribution/index.html
        // System.out.println(CollUtil.join(list, ","));

        // System.out.println(list.stream().mapToDouble(it -> it).max());
        // System.out.println(list.stream().mapToDouble(it -> it).min());

        // System.out.println(System.currentTimeMillis() - l1);

        // List<Integer> list = new ArrayList<>();
        //
        // for (int i = 0; i < 10000; i++) {
        // list.add(floatKillNormalDistribution(0.01));
        // }
        //
        // System.out.println(CollUtil.join(list, ","));

        // for (int i = 0; i < 10; i++) {
        //
        // System.out.println(MyMathUtil.floatKillNormalDistribution(1.0 / 100));
        //
        // }

    }

    /**
     * 普通正态随机分布 参数 u 均值 参数 v 方差
     */
    public static double normalDistribution(double u, double v) {
        return Math.sqrt(v) * new Random().nextGaussian() + u;
    }

    /**
     * 获取：浮动概率击杀，正态分布的 四舍五入值
     */
    public static double floatKillNormalDistribution(double mz) {

        double u = 1 / mz;

        return handleNormalDistribution(() -> {

            return normalDistribution(u, u * u / 9);

        });

    }

    /**
     * 大于零的，正太分布次数
     */
    public static double handleNormalDistribution(Supplier<Double> supplier) {

        double resValue;

        int retryNumber = 0;

        do {

            resValue = NumberUtil.round(supplier.get(), 0).doubleValue();

            retryNumber++;

            if (retryNumber >= 10) {
                return 1;
            }

        } while (resValue <= 0);

        return resValue;

    }

}
