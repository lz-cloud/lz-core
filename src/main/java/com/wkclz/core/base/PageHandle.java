package com.wkclz.core.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.util.List;

public class PageHandle<T extends BaseModel> {

    private T model;

    public PageHandle(T model){
        model.init();
        this.model = model;
        PageHelper.startPage(model.getPageNo(), model.getPageSize());
    }

    public PageData page(List list){
        Page listPage = (Page) list;
        long total = listPage.getTotal();
        PageData pageData = new PageData<>(model);
        pageData.setTotalCount(Long.valueOf(total).intValue());
        pageData.setRows(list);
        return pageData;
    }

}
