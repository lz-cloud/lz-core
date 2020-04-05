package com.wkclz.core.helper;


import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.entity.DebugInfo;
import com.wkclz.core.util.UniqueCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugHelper {

    private static final Logger logger = LoggerFactory.getLogger(DebugHelper.class);
    private final static ThreadLocal<DebugInfo> threadLocal = new ThreadLocal();

    public static void debugStart(){
        DebugInfo debugInfo = threadLocal.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (debugInfo == null){
            debugInfo = new DebugInfo();
            debugInfo.setStartTime(currentTimeMillis);
            debugInfo.setUpperTime(currentTimeMillis);
            debugInfo.setCurrentTime(currentTimeMillis);
            debugInfo.setDebugId(UniqueCodeUtil.getJavaUuid());
            debugInfo.setStep(0);
            threadLocal.set(debugInfo);
            logger.info("debug {} start @ {}", debugInfo.getDebugId(), debugInfo.getStartTime());
        }
    }


    public static void debug(){
        DebugInfo debugInfo = threadLocal.get();

        long currentTimeMillis = System.currentTimeMillis();
        if (debugInfo == null){
            throw BizException.error("found any debug info!");
        }
        debugInfo.setUpperTime(debugInfo.getCurrentTime());
        debugInfo.setCurrentTime(currentTimeMillis);
        debugInfo.setStep(debugInfo.getStep() +1);
        long setpCost = debugInfo.getUpperTime() - debugInfo.getCurrentTime();
        long allCost = debugInfo.getStartTime() - debugInfo.getCurrentTime();
        logger.info("debug {} process @ {}, {} steps, step cost {} ms, allCost {} ms",
            debugInfo.getDebugId(),
            debugInfo.getCurrentTime(),
            debugInfo.getStep(),
            setpCost,
            allCost
        );


    }
    public static void debugEnd(){
        DebugInfo debugInfo = threadLocal.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (debugInfo == null){
            return;
        }
        debugInfo.setCurrentTime(currentTimeMillis);
        debugInfo.setEndTime(currentTimeMillis);
        debugInfo.setStep(debugInfo.getStep() +1);
        long setpCost = debugInfo.getUpperTime() - debugInfo.getCurrentTime();
        long allCost = debugInfo.getStartTime() - debugInfo.getCurrentTime();
        logger.info("debug {} end @ {}, {} steps, step cost {} ms, allCost {} ms",
            debugInfo.getDebugId(),
            debugInfo.getEndTime(),
            debugInfo.getStep(),
            setpCost,
            allCost
        );

        threadLocal.remove();
    }

}
