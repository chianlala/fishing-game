package com.jeesite.modules.api.pay.util;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 功能：http请求工具类
 *
 * @author cnmobi_db
 */
public class HttpUtils {
//	private static Logger log = Logger.getLogger(HttpUtils.class);

    /**
     * 解析返回值
     * <p>
     * 返回map
     *
     * @return
     */
    public static Res parseRes(Res res) {
//		log.debug("服务器返回解析：" + res.toString());
        int code = res.getCode();
        String msg = res.getMsg();
        Object result = res.getResult();

        if (code == 1) {
            Gson gson = new Gson();

            Res response = gson.fromJson(result.toString(), Res.class);
//			log.debug(response);
            int resCode = response.getCode();
            String resMsg = response.getMsg();
            Object resResult = response.getResult();

            res.setMsg(resMsg);
            if (resCode == 1) {
                Map resultMap = (Map) resResult;
                // 验证签名
                if (HySignUtils.checkParam(resultMap, "977699200723")) {
                    res.setResult(resResult);
                } else {
//					log.debug("验证服务器返回签名不通过");
                    res.setCode(5);
                    res.setMsg("验证服务器返回签名不通过");
                }

            } else {
//				log.debug(result);
                res.setCode(resCode);
            }

        } else {
//			log.debug(result);
        }
//		log.debug("服务器返回解析后：" + res.toString());
        return res;

    }

    /**
     * http post请求并添加签名
     *
     * @param url    请求地址
     * @param params 请求参数集合,不包含timestamp和sign
     * @return
     */
    public static Res httpPostAddSign(String url, List<BasicNameValuePair> params) {
        params.add(new BasicNameValuePair("timestamp", Long.toString(System.currentTimeMillis())));
        params.add(
            new BasicNameValuePair("sign", HySignUtils.getSignature(params, "977699200723")));
        return httpPost(url, params);
    }

    /**
     * http post请求
     *
     * @param url    请求地址
     * @param params 请求参数集合
     * @return
     */
    public static Res httpPost(String url, List<BasicNameValuePair> params) {

        // 返回code，为1时正常，非1时不正常
        int code = 0;
        String msg = null;
        String result = null;

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            client = HttpClients.createDefault();
            response = client.execute(httpPost);
            if (response != null && response.getEntity() != null) {

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity httpEntity = response.getEntity();
                    code = 1;
                    result = EntityUtils.toString(httpEntity);// 取出应答字符串
                } else {
                    msg = new StringBuffer("服务器异常:statusCode=").append(statusCode).toString();
                }

            } else {
                msg = "服务器异常:无返回";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = new StringBuffer("系统异常:").append(e.getMessage()).toString();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new Res(code, msg, result);
    }

//	  /**
//     * 从request中获得参数Map，并返回可读的Map
//     *
//     * @param request
//     * @return
//     */
//    public static SortedMap getParameterMap(HttpServletRequest request) {
//        // 参数Map
//        Map properties = request.getParameterMap();
//        // 返回值Map
//        SortedMap returnMap = new TreeMap();
//        Iterator entries = properties.entrySet().iterator();
//        Map.Entry entry;
//        String name = "";
//        String value = "";
//        while (entries.hasNext()) {
//            entry = (Map.Entry) entries.next();
//            name = (String) entry.getKey();
//            Object valueObj = entry.getValue();
//            if(null == valueObj){
//                value = "";
//            }else if(valueObj instanceof String[]){
//                String[] values = (String[])valueObj;
//                for(int i=0;i<values.length;i++){
//                    value = values[i] + ",";
//                }
//                value = value.substring(0, value.length()-1);
//            }else{
//                value = valueObj.toString();
//            }
//            returnMap.put(name, value.trim());
//        }
//        returnMap.remove("method");
//        return returnMap;
//    }

}