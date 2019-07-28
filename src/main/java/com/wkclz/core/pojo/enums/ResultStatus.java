package com.wkclz.core.pojo.enums;

import com.wkclz.core.base.annotation.Desc;

@Desc("结果状态")
public enum ResultStatus {

    TOKEN_UNLL(10001, "token 为空！"),
    TOKEN_ERROR(10002, "token 不正确或已失效！"),
    TOKEN_ILLEGAL_TRANSFER(10003, "非法传输 token！"),
    TOKEN_ILLEGAL_LENGTH(10004, "非法长度的 token！"),
    TOKEN_SIGN_FAILD(10005, "token 签名效验失败！"),

    CLIENT_CHANGE(20001, "用户登录环境改变！"),
    API_CORS(20002, "api url can not be cors"),
    ORIGIN_CORS(20003, "origin url can not be cors"),

    USERNAME_PASSWORD_ERROR(30001, "登录名或密码错误"),
    CAPTCHA_ERROR(30002, "图片验证码错误"),
    CAPTCHA_NEED(30003, "需要图片验证码"),
    MOBILE_CAPTCHA_ERROR(30004, "短信验证码错误"),

    ORDER_TIMEOUT(40001, "订单支付超时已自动取消，请重新下单！"),
    ORDER_PAYD(40002, "订单已完成支付，请不要重复支付！"),
    ORDER_ERROR(40003, "订单状态异常，不能支付！"),
    ;

    private Integer code;
    private String msg;

    ResultStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}