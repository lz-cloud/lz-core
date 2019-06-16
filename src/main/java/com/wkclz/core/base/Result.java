package com.wkclz.core.base;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wkclz.core.pojo.enums.EnvType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午9:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {


    public static final String SYSTEM_ERROR = "System error, we have turned to the Admin of this website";
    public static final String REDIS_IS_DISABLED = "Redis config or Redis server is error! no Redis will be support!";

    public static final String[] TOKEN_UNLL = {"10001", "token 为空！"};
    public static final String[] TOKEN_ERROR = {"10002", "token 不正确或已失效！"};
    public static final String[] TOKEN_ILLEGAL_TRANSFER = {"10003", "非法传输 token！"};
    public static final String[] TOKEN_ILLEGAL_LENGTH = {"10004", "非法长度的 token！"};
    public static final String[] TOKEN_SIGN_FAILD = {"10005", "token 签名效验失败！"};

    public static final String[] CLIENT_CHANGE = {"20001", "用户登录环境改变！"};
    public static final String[] API_CORS = {"20002", "api url can not be cors"};
    public static final String[] ORIGIN_CORS = {"20003", "origin url can not be cors"};

    public static final String[] USERNAME_PASSWORD_ERROR = {"30001", "登录名或密码错误"};
    public static final String[] CAPTCHA_ERROR = {"30002", "图片验证码错误"};
    public static final String[] CAPTCHA_NEED = {"30003", "需要图片验证码"};
    public static final String[] MOBILE_CAPTCHA_ERROR = {"30004", "短信验证码错误"};

    public static final String[] ORDER_TIMEOUT = {"40001", "订单支付超时已自动取消，请重新下单！"};
    public static final String[] ORDER_PAYD = {"40002", "订单已完成支付，请不要重复支付！"};
    public static final String[] ORDER_ERROR = {"40003", "订单状态异常，不能支付！"};


    /**
     * 代码执行状态
     * >1    【异常】有具体含意的异常
     * 1    正常
     * 0   【正常的】警告
     * -1  【中断的】错误
     */
    private Integer code = -1;

    private String error;

    private String remind;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date requestTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date responeTime;

    private Long costTime;

    /**
     * 详情
     */
    private Object data;

    public Result() {
        if (Sys.CURRENT_ENV != EnvType.PROD) {
            this.requestTime = new Date();
        }
    }

    public Result(Object data) {
        this.code = 1;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public Result setError(String... error) {
        Result.calcCostTime(this);
        this.error = concatStr(error);
        this.code = -1;
        return this;
    }

    public String getRemind() {
        return remind;
    }

    public Result setRemind(String... remind) {
        Result.calcCostTime(this);
        this.remind = concatStr(remind);
        this.code = 0;
        return this;
    }


    public Object getData() {
        if (code > 0 && data != null) {
            return data;
        }
        return null;
    }

    public Result setData(Object data) {
        Result.calcCostTime(this);
        this.code = 1;
        this.data = data;
        return this;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponeTime() {
        return responeTime;
    }

    public void setResponeTime(Date responeTime) {
        this.responeTime = responeTime;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public Result setOk() {
        Result.calcCostTime(this);
        this.data = true;
        this.code = 1;
        return this;
    }


    public Result setMoreError(String[] error) {
        Result.calcCostTime(this);
        this.code = Integer.valueOf(error[0]);
        this.error = error[1];
        return this;
    }

    public Result setMoreRemind(String[] remind) {
        Result.calcCostTime(this);
        this.code = Integer.valueOf(remind[0]);
        this.remind = remind[1];
        return this;
    }

    /**
     * 错误信息连接处理
     *
     * @param msgs
     * @return
     */
    private static String concatStr(String[] msgs) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String rem : msgs) {
            stringBuffer.append(rem);
            stringBuffer.append(";");
        }
        String msg = stringBuffer.toString();
        if (msg.length() > 0) {
            msg = msg.substring(0, msg.length() - 1);
        }
        return msg;
    }



    /**
     * 错误信息返回
     *
     * @param rep
     * @return
     * @throws Exception
     */
    public static boolean responseError(HttpServletResponse rep, Result result) {
        try {
            result.setRequestTime(null);
            result.setResponeTime(null);
            result.setCostTime(null);
            String string = JSONObject.toJSONString(result);
            rep.setHeader("Content-Type", "application/json;charset=UTF-8");
            rep.getWriter().print(string);
            rep.getWriter().close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    
    private static void calcCostTime(Result result){
        if (Sys.CURRENT_ENV != EnvType.PROD) {
            result.setResponeTime(new Date());
            if(result.getRequestTime() != null){
                result.setCostTime(result.getResponeTime().getTime()-result.getRequestTime().getTime());
            }
        }
    }
}
