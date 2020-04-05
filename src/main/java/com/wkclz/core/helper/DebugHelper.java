package com.wkclz.core.helper;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.entity.DebugInfo;
import com.wkclz.core.util.UniqueCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;

public class DebugHelper {

    private static final Logger logger = LoggerFactory.getLogger(DebugHelper.class);
    private final static ThreadLocal<DebugInfo> threadLocal = new ThreadLocal();

    public static void debug(){
        debug(null);
    }
    public static void debug(CharSequence msg, Object... args){
        DebugInfo debugInfo = threadLocal.get();

        long currentTimeMillis = System.currentTimeMillis();
        if (debugInfo == null){
            throw BizException.error("found any debug info!");
        }
        debugInfo.setUpperTime(debugInfo.getCurrentTime());
        debugInfo.setCurrentTime(currentTimeMillis);
        debugInfo.setStep(debugInfo.getStep() +1);
        long setpCost = debugInfo.getCurrentTime() - debugInfo.getUpperTime();
        long allCost = debugInfo.getCurrentTime() - debugInfo.getStartTime();

        String baseInfo = StrUtil.format("debug {} process @ {} with {} \nstep {} cost {}ms, all in {} ms",
            debugInfo.getDebugId(),
            DateUtil.format(new Date(currentTimeMillis), "yyyy-MM-dd HH:mm:ss.SSS"),
            debugInfo.getInfo(),
            debugInfo.getStep(),
            setpCost,
            allCost
        );

        if (StringUtils.isNotBlank(msg)){
            String userInfo = StrUtil.format(msg, args);
            baseInfo = baseInfo + "\n" + userInfo;
        }
        logger.info(baseInfo);
    }


    private static void debugStart(String info){
        DebugInfo debugInfo = threadLocal.get();
        long currentTimeMillis = System.currentTimeMillis();

        String debugId = MDC.get(LogTraceHelper.TRACE_ID);
        if (debugId == null){
            UniqueCodeUtil.getJavaUuid();
        }

        if (debugInfo == null){
            debugInfo = new DebugInfo();
            debugInfo.setInfo(info);
            debugInfo.setStartTime(currentTimeMillis);
            debugInfo.setUpperTime(currentTimeMillis);
            debugInfo.setCurrentTime(currentTimeMillis);
            debugInfo.setDebugId(debugId);
            debugInfo.setStep(0);
            threadLocal.set(debugInfo);
            logger.info("debug {} start @ {} with {}",
                debugInfo.getDebugId(),
                DateUtil.format(new Date(currentTimeMillis), "yyyy-MM-dd HH:mm:ss.SSS"),
                info
            );
        }
    }



    private static void debugEnd(){
        DebugInfo debugInfo = threadLocal.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (debugInfo == null){
            return;
        }
        debugInfo.setCurrentTime(currentTimeMillis);
        debugInfo.setEndTime(currentTimeMillis);
        debugInfo.setStep(debugInfo.getStep() +1);
        long setpCost = debugInfo.getCurrentTime() -  debugInfo.getUpperTime();
        long allCost = debugInfo.getCurrentTime() - debugInfo.getStartTime();

        logger.info("debug {} end @ {} with {} \nstep {} cost {}ms, all in {} ms",
            debugInfo.getDebugId(),
            DateUtil.format(new Date(currentTimeMillis), "yyyy-MM-dd HH:mm:ss.SSS"),
            debugInfo.getInfo(),
            debugInfo.getStep(),
            setpCost,
            allCost
        );
        threadLocal.remove();
    }

}
