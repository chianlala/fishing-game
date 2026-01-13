package com.maple.game.osee.util;

/**
 * 服务器返回对象
 *
 * @author cnmobi
 */
public class Res {

    public static String CODE = "code";
    public static String RESULT = "result";
    public static String MSG = "msg";

    /**
     * 返回码，参见章节5 code对照表
     */
    private int code;
    /**
     * 返回码说明，参见章节5 code对照表
     */
    private String msg;
    /**
     * 返回json结果
     */
    private Object result;

    public Res(int code, String msg, String result) {
        super();
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Res [code=" + code + ", msg=" + msg + ", result=" + result + "]";
    }

}
