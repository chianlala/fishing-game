package com.jeesite.modules.api.pay.vo;

public class UsdRequestVO {
    private String mer_no;  //分配给用户的编号
    private String order_no;  //订单号 在商户系统中必须唯一 (不能包含下划线)
    private String order_amount;  //交易金额
    private String payname;  //姓名
    private String payemail; //客户邮箱
    private String payphone;  //手机号
    private String currency;  //交易币种
    private String paytypecode;  //支付类型编码
    private String method;  //通信类型
    private String returnurl; //交易结果接收地址
    private String sign;  //数据签名

    public String getMer_no() {
        return mer_no;
    }

    public void setMer_no(String mer_no) {
        this.mer_no = mer_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getPayname() {
        return payname;
    }

    public void setPayname(String payname) {
        this.payname = payname;
    }

    public String getPayemail() {
        return payemail;
    }

    public void setPayemail(String payemail) {
        this.payemail = payemail;
    }

    public String getPayphone() {
        return payphone;
    }

    public void setPayphone(String payphone) {
        this.payphone = payphone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaytypecode() {
        return paytypecode;
    }

    public void setPaytypecode(String paytypecode) {
        this.paytypecode = paytypecode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReturnurl() {
        return returnurl;
    }

    public void setReturnurl(String returnurl) {
        this.returnurl = returnurl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
