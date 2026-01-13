package com.jeesite.modules.api.pay.util;

import com.jeesite.modules.util.MD5Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.*;

/**
 * 签名工具
 *
 * @author zjl
 */
public class SignUtils {

    /**
     * 对传入参数进行MD5签名 默认utf-8、结果为大写
     */
    public static String createSign(SortedMap<String, Object> parameters, String key) {
        return createSign(parameters, key, true);
    }

    /**
     * 对传入参数进行MD5签名 可以设置是否对结果进行大小写转换
     */
    public static String createSign(SortedMap<String, Object> parameters, String key,
        boolean upperCase) {
        return createSign("utf-8", parameters, key, upperCase);
    }

    /**
     * 对传入参数进行MD5签名
     */
    public static String createSign(String characterEncoding, SortedMap<String, Object> parameters,
        String key, boolean upperCase) {
        StringBuilder sb = new StringBuilder();
        Set es = parameters.entrySet();
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                && !"key".equals(k)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        sb.append("key=").append(key);
        String md5Encode = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return upperCase ? md5Encode.toUpperCase() : md5Encode.toLowerCase();
    }

    /**
     * <一句话功能简述> <功能详细描述>验证返回参数
     *
     * @param params
     * @param key
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean checkParam(Map<String, String> params, String key) {
        boolean result = false;
        if (params.containsKey("sign")) {
            String sign = params.get("sign");
            params.remove("sign");
            StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
            SignUtils.buildPayParams(buf, params, false);
            String preStr = buf.toString();
            String signRecieve = MD5.sign(preStr, "&key=" + key, "utf-8");
            result = sign.equalsIgnoreCase(signRecieve);
        }
        return result;
    }

    /**
     * 过滤参数
     *
     * @param sArray
     * @return
     * @author
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>(sArray.size());
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * <一句话功能简述> <功能详细描述>将map转成String
     *
     * @param payParams
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String payParamsToString(Map<String, String> payParams) {
        return payParamsToString(payParams, false);
    }

    public static String payParamsToString(Map<String, String> payParams, boolean encoding) {
        return payParamsToString(new StringBuilder(), payParams, encoding);
    }

    /**
     * @param payParams
     * @return
     * @author
     */
    public static String payParamsToString(StringBuilder sb, Map<String, String> payParams,
        boolean encoding) {
        buildPayParams(sb, payParams, encoding);
        return sb.toString();
    }

    /**
     * @param payParams
     * @return
     * @author
     */
    public static void buildPayParams(StringBuilder sb, Map<String, String> payParams,
        boolean encoding) {
        List<String> keys = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            sb.append(key).append("=");
            if (encoding) {
                sb.append(urlEncode(payParams.get(key)));
            } else {
                sb.append(payParams.get(key));
            }
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Throwable e) {
            return str;
        }
    }


    public static Element readerXml(String body, String encode) throws DocumentException {
        SAXReader reader = new SAXReader(false);
        InputSource source = new InputSource(new StringReader(body));
        source.setEncoding(encode);
        Document doc = reader.read(source);
        Element element = doc.getRootElement();
        return element;
    }
}
