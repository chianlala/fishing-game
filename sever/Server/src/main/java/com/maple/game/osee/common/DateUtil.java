package com.maple.game.osee.common;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    public static Instant tomorrowInstant() {
        return LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) // 同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
                {
                    timeDistance += 366;
                } else // 不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else // 不同年
        {
            return day2 - day1;
        }
    }

    public static String dayAddOne(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = sdf.parse(date);
        // 要实现日期+1 需要String转成Date类型

        Format f = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        // 利用Calendar 实现 Date日期+1天

        sDate = c.getTime();
        // 打印Date日期,显示成功+1天

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf1.format(sDate);
        return date;
    }

    public static String dayCutOne(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = sdf.parse(date);
        // 要实现日期+1 需要String转成Date类型

        Format f = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        c.add(Calendar.DAY_OF_MONTH, -1);
        // 利用Calendar 实现 Date日期-1天

        sDate = c.getTime();
        // 打印Date日期,显示成功-1天

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf1.format(sDate);
        return date;
    }

    public static String dayCutDate(String date, int day) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = sdf.parse(date);
        // 要实现日期+1 需要String转成Date类型

        Format f = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        c.add(Calendar.DAY_OF_MONTH, -day);
        // 利用Calendar 实现 Date日期-1天

        sDate = c.getTime();
        // 打印Date日期,显示成功-1天

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf1.format(sDate);
        return date;
    }

    public static void main(String[] args) {
        String dateStr = "2019-12-31 21:21:28";
        String dateStr2 = "2020-1-4";
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // try
        // {
        // Date date2 = new Date();
        // Date date = format.parse(dateStr);
        //
        // System.out.println("两个日期的差距：" + differentDays(date,date2));
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        try {
            System.out.println(dayAddOne(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
