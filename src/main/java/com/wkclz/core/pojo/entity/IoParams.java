package com.wkclz.core.pojo.entity;

import java.util.List;


/**
 * 报表入参出参
 *
 * @author wangkc
 * @date 2018-12-22 17:22:23
 */
public class IoParams {

    /**
     * 入参
     */
    private List<InParams> inParams;

    /**
     * 出参
     */
    private List<OutParams> outParams;


    public List<InParams> getInParams() {
        return inParams;
    }

    public void setInParams(List<InParams> inParams) {
        this.inParams = inParams;
    }

    public List<OutParams> getOutParams() {
        return outParams;
    }

    public void setOutParams(List<OutParams> outParams) {
        this.outParams = outParams;
    }
}
