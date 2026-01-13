package com.jeesite.modules.osee.vo.money;

/**
 * 二维码支付请求参数
 *
 * @author 阿华
 * @date 2018-07-23
 */
public class JsPayRq {

    private String inst_no;//渠道号

    private String mch_no;//商户号

    private String shop_no;//门店号 非必传

    private String terminal_no;//终端号 内部使用 非必传

    private String pay_type;//支付类型

    private String pay_trace_no;//流水号

    private String pay_time;//支付时间

    private String total_amount;//金额

    private String open_id;//小程序openid

    private String appid;//小程序appid

    private String operator_id;//操作员号 内部使用

    private String order_body;//订单描述

    private String attach;//附加数据

    private String goods_detail;

    private String notify_url;//回调地址

    private String sign; //签名检验串


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getInst_no() {
        return inst_no;
    }

    public void setInst_no(String inst_no) {
        this.inst_no = inst_no;
    }

    public String getMch_no() {
        return mch_no;
    }

    public void setMch_no(String mch_no) {
        this.mch_no = mch_no;
    }

    public String getShop_no() {
        return shop_no;
    }

    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }

    public String getTerminal_no() {
        return terminal_no;
    }

    public void setTerminal_no(String terminal_no) {
        this.terminal_no = terminal_no;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }


    public String getPay_trace_no() {
        return pay_trace_no;
    }

    public void setPay_trace_no(String pay_trace_no) {
        this.pay_trace_no = pay_trace_no;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }


    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public String getOrder_body() {
        return order_body;
    }

    public void setOrder_body(String order_body) {
        this.order_body = order_body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getGoods_detail() {
        return goods_detail;
    }

    public void setGoods_detail(String goods_detail) {
        this.goods_detail = goods_detail;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


}
