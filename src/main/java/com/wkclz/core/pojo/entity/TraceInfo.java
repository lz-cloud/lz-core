package com.wkclz.core.pojo.entity;


public class TraceInfo {

    /**
     * 跟踪节点序号
     */
    private Integer seq;

    /**
     * 跟踪号
     */
    private String traceId;



    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
