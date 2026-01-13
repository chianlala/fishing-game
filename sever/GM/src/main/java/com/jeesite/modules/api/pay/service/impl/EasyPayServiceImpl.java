package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.EasyPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


@Service
public class EasyPayServiceImpl implements EasyPayService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CloudPayConfig cloudPayConfig;

    @Autowired
    ProjectConfig projectConfig;

    @Autowired
    PayService payService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {
        try {
            logger.debug("Easy支付订单下单开始");
            double money = shopItem.getPayMoney();//注意金额是以分为单位。
            String url = "http://pay.yqhappy.cn:8081/Pay/GateWay";

            PrintWriter out = null;
            BufferedReader in = null;

            //拼装请求的字符串
            String param = "";
            //商户号
            String parter = "1808";
            //密钥
            String type = "1004";
            //签名
            String sign = "";
            //响应的结果(json格式)
            String result = "";
            //  Map<String, String> map = new HashMap<String, String>();
            // map.put("parter","1808");

            // map.put("type", "1007");
            // map.put("value", "0.02");

            //   map.put("orderid","parter"+System.currentTimeMillis());
            //  map.put("callbackurl","http://www.baidu.com");
            //  param = buildSignStr(map);
//        param = "parter={1808}&type={1007}&value={0.02}&orderid={jsdk_payment_" + System.currentTimeMillis()+"}&callbackurl={http://www.baidu.com}&device={wap}a2231095dad5470dadab517ea0623dca";
            long time = System.currentTimeMillis();
            param = "parter=1808&type=1007&value=" + money + "&orderid=" + orderId
                + "&callbackurl=http://146.56.226.120/ttmy_admin/api/pay/callback/easyPaya2231095dad5470dadab517ea0623dca";
            System.out.println(param);
            String param2 = param;
            // param+="a2231095dad5470dadab517ea0623dca";
            sign = "&sign=" + getSign1(param);
            String param1 = "parter=1808&type=1007&value=" + money + "&orderid=" + orderId
                + "&callbackurl=http://146.56.226.120/ttmy_admin/api/pay/callback/easyPay" + sign;
            //String param1=param2+sign;
            System.out.println(param1);
            url = "http://pay.yqhappy.cn:8081/Pay/GateWay?" + param1;
            System.out.println("url" + url);
            // 封装返回数据
            JSONObject object = new JSONObject();
            object.put("code_url", url);
            logger.debug(object.toJSONString());
            return new CommonResponse(object.toJSONString());
        } catch (Exception e) {
            logger.error("第三方支付下单出错：");
            e.printStackTrace();
            return new CommonResponse(PayConstants.PAY_ERROR, e.getMessage());
        }

    }

    /**
     * 对字符串进行UTF-8转码
     *
     * @return 字符串
     */
    public static String getEncoding(String str) {
        String res = "";
        try {
            res = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 对字符串进行UTF-8解码
     *
     * @return 字符串
     */
    public static String getDecoding(String str) {
        String res = "";
        try {
            res = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 对Map进行排序
     *
     * @param params Map
     * @return a=1&b=2... 这样的字符串
     */
    public static String buildSignStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        // 将参数以参数名的字典升序排序
        Map<String, String> sortParams = new TreeMap<String, String>(params);
        // 遍历排序的字典,并拼接"key=value"格式
        for (Map.Entry<String, String> entry : sortParams.entrySet()) {
            if (sb.length() != 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * 对字符串md5加密
     *
     * @param str 传入要加密的字符串
     * @return MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5 = new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:" + e.getMessage(), e);
        }
    }

    public static String fillMD5(String md5) {
        return md5.length() == 32 ? md5 : fillMD5("0" + md5);
    }

    /**
     * 快接签名算法
     *
     * @param str 传入排序后的字符串
     * @param key 密钥
     * @return MD5加密后的字符串
     */
    public static String getSign(String str, String key) {
        String sign = getMD5(getDecoding(str) + "&key=" + key);
        return sign;
    }

    public static String getSign1(String str) {
        String sign = getMD5(getDecoding(str));
        return sign;
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        logger.debug("-- easy支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));
        }
        if (Integer.valueOf(params.get("opstate").toString()) == 0) {
            String orderNo = (String) params.get("orderid");
            Double money = Double.parseDouble((String) params.get("orderamt")) * 100;
//            // 发送通知回调给游戏服务器
            CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo,
                (double) money, 1);
            if (!notifyResponse.getSuccess()) {
                logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                    notifyResponse.getErrMsg());
                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
            }
//            logger.debug( "订单号：" + orderNo + " --> 交易失败");
            // 回调处理成功
            logger.debug("第三方支付回调处理成功：{}", orderNo);
            // 接口方定义的返回 ok 为商户处理回调成功
            return new CommonResponse("ok");
        } else {
            logger.error("支付失败：{}", params.get("code"));
            return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
        }
    }

    public static void main(String[] args) {
        String url = "http://pay.yqhappy.cn:8081/Pay/GateWay";

        PrintWriter out = null;
        BufferedReader in = null;

        //拼装请求的字符串
        String param = "";
        //商户号
        String parter = "1808";
        //密钥
        String type = "1004";
        //签名
        String sign = "";
        //响应的结果(json格式)
        String result = "";
        //  Map<String, String> map = new HashMap<String, String>();
        // map.put("parter","1808");

        // map.put("type", "1007");
        // map.put("value", "0.02");

        //   map.put("orderid","parter"+System.currentTimeMillis());
        //  map.put("callbackurl","http://www.baidu.com");
        //  param = buildSignStr(map);
//        param = "parter={1808}&type={1007}&value={0.02}&orderid={jsdk_payment_" + System.currentTimeMillis()+"}&callbackurl={http://www.baidu.com}&device={wap}a2231095dad5470dadab517ea0623dca";
        long time = System.currentTimeMillis();
        param = "parter=1808&type=1007&value=0.02&orderid=jsdk_" + time
            + "&callbackurl=http://www.baidu.coma2231095dad5470dadab517ea0623dca";
        System.out.println(param);
        String param2 = param;
        // param+="a2231095dad5470dadab517ea0623dca";
        sign = "&sign=" + getSign1(param);
        String param1 = "parter=1808&type=1007&value=0.02&orderid=jsdk_" + time
            + "&callbackurl=http://www.baidu.com" + sign;
        //String param1=param2+sign;
        System.out.println(param1);
        url = "http://pay.yqhappy.cn:8081/Pay/GateWay?" + param1;
        System.out.println("url" + url);
    }
}
