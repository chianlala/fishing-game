package com.jeesite.modules.api.pay.util;

import java.math.BigDecimal;

/**
 * @author: zhangyingjie
 * @description:
 * @date: 2018/6/5 17:07
 */
public class SciCalUtils {

    /**
     * @param value 需要科学计算的数据
     * @param digit 保留的小数位
     * @return 功能：四舍六入五成双计算法
     */
    public static String sciCal(Double value, int digit) {
        String result = "-999";
        try {
            double ratio = Math.pow(10, digit);
            double _num = value * ratio;
            double mod = _num % 1;
            double integer = Math.floor(_num);
            double returnNum;
            if (mod > 0.5) {
                returnNum = (integer + 1) / ratio;
            } else if (mod < 0.5) {
                returnNum = integer / ratio;
            } else {
                returnNum = (integer % 2 == 0 ? integer : integer + 1) / ratio;
            }
            BigDecimal bg = new BigDecimal(returnNum);
            result = bg.setScale((int) digit, BigDecimal.ROUND_HALF_UP).toString();
        } catch (RuntimeException e) {
            throw e;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(sciCal(1.1250, 2));
        System.out.println(sciCal(1.1350, 2));
        System.out.println(sciCal(1.1349, 2));
        System.out.println(sciCal(1.125001, 2));
    }

}
