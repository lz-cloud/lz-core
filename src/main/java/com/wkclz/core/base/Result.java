package com.wkclz.core.base;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wkclz.core.exception.BizException;
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

    /**
     * 代码执行状态
     * >1    【异常】有具体含意的异常
     * 1    正常
     * 0   【正常的】警告
     * -1  【中断的】错误
     */
    private Integer code = -1;

    private String msg = "success";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date requestTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date responeTime;

    private Long costTime;

    /**
     * 详情
     */
    private T data = (T)"error";

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result setError(String... error) {
        this.msg = concatStr(error);
        this.code = -1;
        return this;
    }

    public Result setRemind(String... remind) {
        this.msg = concatStr(remind);
        this.code = 0;
        return this;
    }


    public T getData() {
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
        result.msg = concatStr(error);
        result.code = -1;
        return result;
    }

    public static Result error(BizException e) {
        Result result = new Result();
        result.msg = e.getMessage();
        result.code = e.getCode();
        return result;
    }

    public static Result remind(String... remind) {
        Result result = new Result();
        result.msg = concatStr(remind);
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

    public boolean isSuccess(){
        if (this.code != null && this.code == 1){
            return true;
        }
        return false;
    }

    public Result setMoreError(ResultStatus status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
        return this;
    }

    public Result setMoreRemind(ResultStatus status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
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
            logger.error(e.getMessage(), e);
            return false;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }


}
