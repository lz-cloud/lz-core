package com.wkclz.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BizException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(BizException.class);
    private static final String DEFAULT_ERR_MSG = "System error, please turn to Administrator";

    public BizException() {
        super();
    }


    public BizException(String msg) {
        super(msg);
    }

    private Integer code = -1;


    public static BizException remind(String msg){
        msg = StringUtils.isBlank(msg)?DEFAULT_ERR_MSG:msg;
        logger.warn(msg);
        BizException bizException = new BizException(msg);
        bizException.code = 0;
        return bizException;
    }

    public static BizException error(String msg){
        msg = StringUtils.isBlank(msg)?DEFAULT_ERR_MSG:msg;
        logger.error(msg);
        BizException bizException = new BizException(msg);
        bizException.code = -1;
        return bizException;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
