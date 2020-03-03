package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

//全局异常捕捉处理
@ControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e) {

        BizException bizException = getBizException(e);
        if (bizException != null){
            return Result.error(bizException);
        }

        String message = e.getMessage();
        if (message == null || "".equals(message.trim()) || "null".equals(message)){
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            message = out.toString();
        }
        return Result.error(message);
    }


    /**
     * Throwable 找 BizException，找二级原因
     * @param throwable
     * @return
     */
    private static BizException getBizException(Throwable throwable){
        if (throwable == null){
            return null;
        }
        if (throwable instanceof BizException){
            return (BizException) throwable;
        }
        Throwable cause = throwable.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        cause = cause.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        return null;
    }

}



