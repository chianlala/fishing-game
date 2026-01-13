package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.KkPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;


@Service
public class KkPayServiceImpl implements KkPayService {

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
    public CommonResponse unifiedorder(String orderId, int isSao, ShopItem shopItem) {
        try {
            logger.debug("Kk支付订单下单开始");

            int money = new Double(shopItem.getPayMoney()).intValue();//注意金额是以元为单位。
            String url = "";
            if (isSao == 1) {//支付宝H5
                url = "http://api.kj-pay.com/alipay/scan_pay";
            } else if (isSao == 2) {
                url = "http://api.kj-pay.com/alipay/wap_pay";
            } else {
                url = "";
            }

            PrintWriter out = null;
            BufferedReader in = null;

            //拼装请求的字符串
            String param = "";
            //商户号
            String merchant_no = "2020540415";
            //密钥
            String key = "61a27b99db4722556eaf98943aa6b188";
            //签名
            String sign = "";
            //响应的结果(json格式)
            String result = "";

            Map<String, String> map = new HashMap<String, String>();
            map.put("merchant_no", merchant_no);

            map.put("merchant_order_no", orderId);
            map.put("notify_url", "http://139.155.248.244/ttmy_admin/api/pay/callback/kkpay");

            map.put("start_time", DateUtils.formatDate(new Date()));
            map.put("trade_amount", String.valueOf(money));
            map.put("pay_type", getEncoding("1"));

            //注：参数的值为中文必须得进行转码
            map.put("goods_name", getEncoding(shopItem.getShopName()));
            map.put("goods_desc", getEncoding(shopItem.getShopName()));

            map.put("user_ip", "127.0.0.1");
//            map.put("pay_sence", getEncoding("{\"type\":\"Wap\",\"wap_url\":\"http://www.kk30.com\",\"wap_name\":\"快快网络\"}"));
            map.put("sign_type", "1");

            param = buildSignStr(map);

            sign = "&sign=" + getSign(param, key);
            param = param + sign;

            //System.out.println(param);
            try {
                URL realUrl = new URL(url);
                //打开和URL之间的连接
                URLConnection conn = realUrl.openConnection();

                //设置通用的请求属性
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("Content-type",
                    "application/x-www-form-urlencoded;charset=UTF-8");

                //发送POST请求必须设置如下两行
                conn.setDoOutput(true);
                conn.setDoInput(true);

                //获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                //发送请求参数
                out.print(param);
                //flush输出流的缓冲
                out.flush();
                //定义BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } catch (Exception e) {
                System.out.println("发送POST请求出现异常！" + e);
                e.printStackTrace();
            }
            //使用finally块来关闭输出流、输入流
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            //返回的json 请您自行解析...
            System.out.println(result);

//            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//            params.add(new BasicNameValuePair("name", shopItem.getShopName()));
//            params.add(new BasicNameValuePair("total", String.valueOf(money)));
//            params.add(new BasicNameValuePair("orderNo", orderId));
//
//            params.add(new BasicNameValuePair("merchantNo", "8099209208852911"));
//            params.add(new BasicNameValuePair("notifyUrl", "http://139.155.248.244/ttmy_admin/api/pay/callback/hycloudpay"));
//
//            Res resultMap = HttpUtils.httpPostAddSign(url, params);
//            resultMap = HttpUtils.parseRes(resultMap);
            Map a = JSONObject.parseObject(result);
            if (Integer.valueOf(a.get("status").toString()) != 1) {
                logger.error("第三方支付下单返回错误信息：{}", a.get("info"));
                return new CommonResponse(PayConstants.PAY_ERROR, a.get("info").toString());
            }

            // 将下单数据发送过去
            logger.debug("第三方支付下单返回数据：{}", result);
            if (result == null || result.equals("")) {
                logger.error("第三方支付下单返回数据为空！");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误！");
            }
            // 封装返回数据
            JSONObject object = new JSONObject();
            Map b = (Map) a.get("data");
            object.put("code_url", b.get("pay_url"));
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
        logger.debug("-- 快快支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));
        }
        if (params.get("status").toString().equals("Success")) {
            String orderNo = (String) params.get("merchant_order_no");
            Double money = Double.parseDouble((String) params.get("amount"));
            // 发送通知回调给游戏服务器
            CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo,
                (double) money * 100, 1);
            if (!notifyResponse.getSuccess()) {
                logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                    notifyResponse.getErrMsg());
                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
            }
//            logger.debug( "订单号：" + orderNo + " --> 交易失败");
            // 回调处理成功
            logger.debug("第三方支付回调处理成功：{}", orderNo);
//            // 接口方定义的返回 ok 为商户处理回调成功
            return new CommonResponse("success");
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
