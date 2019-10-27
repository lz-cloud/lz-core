package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.util.UniqueCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

public class TraceHelper {

    public static TraceInfo checkTraceInfo(HttpServletRequest req){

        TraceInfo traceInfo = new TraceInfo();

        // traceId
        String traceId = MDC.get("traceId");
        if (StringUtils.isBlank(traceId)){
            traceId = req.getHeader("traceId");
            if (StringUtils.isBlank(traceId)) {
                // 如果未生成，生成方法保持一致
                String uuid = UniqueCodeUtil.getJavaUuid();
                traceId = StringUtils.join(Sys.APPLICATION_GROUP.toLowerCase(), "_", uuid);
            }
            MDC.put("traceId", traceId);
        }

        // seq
        String seq = MDC.get("seq");
        if (StringUtils.isBlank(seq)){
            seq = req.getHeader("seq");
            if (StringUtils.isBlank(seq)) {
                seq = "0";
            }
        }
        Integer newSeq = Integer.valueOf(seq) + 1;
        MDC.put("seq", newSeq + "");

        traceInfo.setTraceId(traceId);
        traceInfo.setSeq(newSeq);
        return traceInfo;
    }
}
