package com.jeesite.modules.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.jeesite.modules.model.dto.JiaoYiLePayRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:
 * @author: Liny
 * @email: 2930251092@qq.com
 * @date: 2022/11/1 9:20
 */
@Slf4j
public class JiaoYiLePayUtils {

    @SneakyThrows
    public static String toSort(JiaoYiLePayRequest jiaoYiLePayRequest) {

        //时间
        Date date = new Date();
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        //时间戳必须是String类型
        String timestamp = df2.format(date);

        //biz_content(电脑网站支付为例）
        JSONObject obj = new JSONObject(); // 定义一个描述json的数据

        obj.put("subject", jiaoYiLePayRequest.getSubject());
        obj.put("out_trade_no", jiaoYiLePayRequest.getOut_trade_no());
        obj.put("total_amount", jiaoYiLePayRequest.getTotal_amount());
        obj.put("trade_type", "alipayApp");
        obj.put("notify_url",
            "http://" + MySettingUtil.SETTING.getStr("server.gm.host") + ":" + MySettingUtil.SETTING
                .getStr("server.gm.port") + "/ttmy_admin/api/pay/callback/jiaoYiLe");
        obj.put("client_ip", InetAddress.getLocalHost());

        String bizContent = obj.toString(); // 将保存的数据变为字符串

        //拼接
        //1、hashmap里面的元素是不按添加顺序的，乱序；
        //使用hashmap时，必须使用下方的Collections.sort(keys);
        //		Map <String,String> map= new HashMap <>();
        //2、LinkedHashMap里面的元素是按添加顺序的
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("biz_content", bizContent);
        map.put("charset", "utf-8");
        map.put("mn_type", 3);
        map.put("sign_type", AlipayConstants.SIGN_TYPE_RSA2);
        map.put("sub_mchid", "80016668582742870");
        map.put("timestamp", timestamp);
        map.put("version", "1.0");

        //将逗号换成&
        List<String> keys = new ArrayList<>(map.keySet());

        Collections.sort(keys);//不按首字母排序, 需要按首字母排序请打开
        StringBuilder preStrStringBuilder = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            Object o = map.get(key);

            //            String encode = URLEncoder.encode(map.get(key).toString(), "UTF-8");

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符

                preStrStringBuilder.append(key).append("=").append(o);

            } else {

                preStrStringBuilder.append(key).append("=").append(o).append("&");

            }

        }
        //        log.info("待签名字符串：" + preStrStringBuilder);

        //StringBuilder类转换为String类
        return preStrStringBuilder.toString();

    }

    public static String toSign(String content) throws AlipayApiException {

        return AlipaySignature.rsaSign(content,
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCAtioXU/B5WEFjWMlAtCej3xstT5kRCLGM/j25TAWcUjaEPqwY4RWF3paYGUImMboWaV8nWaNuqhxtdYReECKKleFCOFHibEZjT9jr56kqBahNm5tniV6FGiX5p1ub5HmcyFzqxeb+FgWpg4CNuLwZ8Bw6uxAqNsf8ZB8jJgrMJcdCDwZVYz3pdlt61qd7xoN7bJlDmMBqGa7eLv2ur8iJwqFKzqnA8STPEzwQrMmJ3lHUBwdbKHAlFbJ7ckNQLJRwfeTdRDzpSkspd+DF1jRhQnVtXFd1KEafErfMXiljj+AkITn7AOgUzn3SvxVAehRAKUfPwXXp3b7kPLSaNc69AgMBAAECggEAU/kFCwUrfa9zsaIqYvlmZ0ZWz7//qBpY5Bi3Il4TxwHoKW1OEElBJn6rF35bJtoIgzip6N1VgAyh0VP7UPcL4giY1wAxhByJ658bcgVI226neEKOKAV6UjJWFkP2w4VTRlN+M3l8E8dAWHC//TZKGk4JzNNgX3owDV0w51iZcMhD7En301HNLlIopFxqO9bqe8A4/yrOF0cagOcQtWLkIjpaxxaDVHIgjXYWA1jBj2oVKo2BmsyE0JJeVNmFzlIpP2HLoLAiDwvU7qrrnLtXTILlwRjcsEKwc0GX6OsZaIGVMh60gIhn8W29XY5o0ZErzlAW7pF5M/v4AJJ0cKo+IQKBgQC8U25IlgAIkpm2cnxR++7pdXjKOTFdrYoNiILRnEzeoxfDz9BZUavggPlZa3JnJVbTbuQux3/79D321Vu/5ffVISIJfDX1APc6zSqFtLbaFDcCn5vxZC12xCMb6iIOwKGLfZpHyuPhNKItuXFtpmn/jEcJx/3mUQDGGnb4cC9f1QKBgQCu9qndfZzvZQgnnnkCbHpxZTBCHuM+cN/HIGlyHWS7D75ZDmApm2sZyRta+d9LM+iJ7cfhMNMUdwTfPHLPW+iUQ40yzBGTT5CGH0rPN5E+USy/lEaKPCn+aDBLg0ghcL7LCJywM0HdL9qzbskkGKu7KMr5XqfPoPmXYI3u3r4PSQKBgFJol8u/h8g4jnbd5jTh74vNUcv79vQtKkn9wHEE2kZaS93tpBhRusqE5ZgRgoBMQJBI6CToO8L+/9ZxSrLQaQCkfQZ1ig7qG0Vp3f+gMpt/WQsW2OBqm9JqxYpDjB+0f3xQvCBKUimMJKOpkE8RETU4JyXKk/oi3BnUHtdcD/75AoGAU6u4TijdHQr23GOHwSX5ZDMc4fH9gIXgLk0MwDrYSjDcOaCLUjp1/G3VPCuUolUflp504PdFalsEpHE3An0Ue5rWcn1uxkDdf5aFYxVSyzdsq+2P0lm1R2mx9trAauWNCHGE5SqOC+Xvs7F+VVnK/oUilCC1JbD4y3CsT5iL0akCgYEAk2s3GY7Gu8rkPRIDjxI5F0TXKQRahlcd0VLF4iN0yrKHrFSbkbUM1q7rAHBpnq6RCxTcNlpHxvpZT27vIePeg5sJnAo0HtJpL+b7J79g5Zoo/Qj/UsJWPNmbkIYdXeRk8ylhQTgRV86GzEKBlhERkzBjN/RZcdeuhSiXulm2Gyg=",
            "utf-8", AlipayConstants.SIGN_TYPE_RSA2);

    }
}
