package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.base.ThreadLocals;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.util.UniqueCodeUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 仅在应用入口处可用此功能
 */
public class TraceHelper {

    public static TraceInfo checkTraceInfo(HttpServletRequest req){

        TraceInfo traceInfo = new TraceInfo();

        // traceId
        String traceId = null;
        Object traceIdObj = ThreadLocals.get("traceId");
        if (traceIdObj != null){
            traceId = traceIdObj.toString();
        }
        if (traceId == null){
            traceId = req.getHeader("traceId");
        }
        if (traceId == null) {
            // 如果未生成，生成方法保持一致
            String uuid = UniqueCodeUtil.getJavaUuid();
            traceId = StringUtils.join(Sys.APPLICATION_GROUP.toLowerCase(), "_", uuid);
        }

        // seq
        String seq = null;
        Object seqObj = ThreadLocals.get("seq");
        if (seqObj != null){
            seq = seqObj.toString();
        }
        if (seq == null){
            seq = req.getHeader("seq");
        }
        if (seq == null) {
            seq = "0";
        }

        Integer newSeq = Integer.valueOf(seq) + 1;

        ThreadLocals.set("traceId", traceId);
        ThreadLocals.set("seq", newSeq + "");

        traceInfo.setTraceId(traceId);
        traceInfo.setSeq(newSeq);

        return traceInfo;
    }
}
