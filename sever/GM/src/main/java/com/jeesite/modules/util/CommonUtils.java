package com.jeesite.modules.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 通用工具类
 *
 * @author zjl
 */
public class CommonUtils {

    /**
     * @param length 随机字符串长度
     * @return 生成的随机字符串
     */
    public static String createNonceStr(int length) {
        if (length <= 0) {
            length = 16;
        }
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            str.append(chars.charAt(rd.nextInt(chars.length() - 1)));
        }
        return str.toString();
    }

    /**
     * @param nonceLength 附加的随机数字个数
     * @param attch       订单号内附加的文本
     * @return 生成的订单号
     */
    public static String createOrderId(int nonceLength, Object... attch) {

        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        StringBuilder orderIdStr = new StringBuilder();

        orderIdStr.append(timeStr);

        for (Object anAttch : attch) {

            orderIdStr.append(anAttch);

        }

        if (nonceLength > 0) {

            int range = (int) Math.pow(10, nonceLength);

            Random random = new Random();

            int endNonce = random.nextInt(range);

            // 位数不足前面补0
            orderIdStr.append(String.format("%0" + nonceLength + "d", endNonce));

        }

        return orderIdStr.toString();

    }

    private static String LocalIp = null;

    /**
     * @return 本机IP
     */
    public static String getLocalIp() {
        if (LocalIp == null || LocalIp.equals("")) {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                LocalIp = addr.getHostAddress();
            } catch (Exception e) {
                return "";
            }
        }
        return LocalIp;
    }

    /**
     * @param map 要转换的Map
     * @return 传入Map的Xml格式文本
     */
    public static String mapToXml(Map<?, ?> map) {
        Document document = DocumentHelper.createDocument();
        Element rootElmt = document.addElement("xml");
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            Element keyElmt = rootElmt.addElement(key);
            keyElmt.setText(value);
        }
        return document.asXML();
    }

    /**
     * @param xml 要转换的xml格式文本
     * @return 传入的xml文本转换成的Map
     */
    public static Map<String, Object> xmlToMap(String xml) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(xml)) {
            return map;
        }
        try {
            Document document = DocumentHelper.parseText(xml);
            Element rootElement = document.getRootElement();
            List elements = rootElement.elements();
            for (Object object : elements) {
                Element element = (Element) object;
                String name = element.getName();
                String text = element.getText();
                if (name == null || text == null) {
                    continue;
                }
                map.put(name, text);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将一个xml输入流转换为Map
     */
    public static Map<String, Object> streamXmlToMap(InputStream inputStream) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 获取根元素
            Element rootElement = document.getRootElement();
            // 得到所有子节点
            List elements = rootElement.elements();
            for (Object object : elements) {
                Element element = (Element) object;
                map.put(element.getName(), element.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return map;
    }

    /**
     * 从请求中获取服务器请求根地址
     */
    public static String getServerURIFromRequest(HttpServletRequest request) {
        String scheme = request.getScheme().trim();
        String serverName = request.getServerName().trim();
        int serverPort = request.getServerPort();
        String uri = scheme + "://" + serverName;
        if (serverPort != 80) {
            uri += ":" + serverPort;
        }
        return uri;
    }

    /**
     * @param length 随机字符串长度
     * @return 生成的随机字符串
     */
    public static String createRom(int length) {
        char[] letters =
            {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        boolean[] flags = new boolean[letters.length];
        char[] chs = new char[6];
        for (int i = 0; i < chs.length; i++) {
            int index;
            do {
                index = (int) (Math.random() * (letters.length));
            } while (flags[index]);// 判断生成的字符是否重复
            chs[i] = letters[index];
            flags[index] = true;
        }
        return chs.toString();
    }

}
