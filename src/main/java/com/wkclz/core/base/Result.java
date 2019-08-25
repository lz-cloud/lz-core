package com.wkclz.core.base;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wkclz.core.pojo.enums.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午9:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private static final Logger logger = LoggerFactory.getLogger(Result.class);

    public static final String SYSTEM_ERROR = "System error, we have turned to the Admin of this website";
    public static final String REDIS_IS_DISABLED = "Redis config or Redis server is error! no Redis will be support!";

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
    private T data;

    public Result() {
    }

    public Result(T data) {
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
        this.error = concatStr(error);
        this.code = -1;
        return this;
    }

    public String getRemind() {
        return remind;
    }

    public Result setRemind(String... remind) {
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

    public Result setData(T data) {
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


    public static Result error(String... error) {
        Result result = new Result();
        result.error = concatStr(error);
        result.code = -1;
        return result;
    }

    public static Result remind(String... remind) {
        Result result = new Result();
        result.remind = concatStr(remind);
        result.code = 0;
        return result;
    }

    public static Result data(Object data) {
        Result result = new Result();
        result.data = data;
        result.code = 1;
        return result;
    }

    public static Result ok() {
        Result result = new Result();
        result.data = true;
        result.code = 1;
        return result;
    }


    public Result setOk() {
        return Result.ok();
    }

    public Result setMoreError(ResultStatus status) {
        this.code = status.getCode();
        this.error = status.getMsg();
        return this;
    }

    public Result setMoreRemind(ResultStatus status) {
        this.code = status.getCode();
        this.error = status.getMsg();
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
            logger.error("JsonProcessingException", e);
            return false;
        } catch (IOException e) {
            logger.error("IOException", e);
            return false;
        }
        return false;
    }


}
