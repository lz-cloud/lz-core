package com.wkclz.core.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-11-12 上午12:10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)//如果为空的属性，去除
public class PageData<T> {

    private List<T> rows;
    private Long totalCount;
    private Integer totalPage;
    private Integer pageNo = 1;
    private Integer pageSize = 10;

    public PageData() {
        init();
    }

    public PageData(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }


    /**
     * 自定义辅助类 分页
     * 使用查询数据源构造新分页参数
     */
    public PageData(PageData oldPageData, List<T> pageList) {
        this.pageNo = oldPageData.getPageNo();
        this.pageSize = oldPageData.getPageSize();
        this.totalCount = oldPageData.getTotalCount();
        this.totalPage = oldPageData.getTotalPage();
        this.rows = pageList;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> data) {
        this.rows = data;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        init(); // 只有设置了总数据数的时候才做分页处理
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    private void init() {
        if (this.pageNo == null || this.pageNo < 1) {
            this.pageNo = 1;
        }
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = 10;
        }
        if (this.totalCount == null) {
            this.totalCount = 0L;
        }

        this.totalPage = (int) Math.ceil((double) this.totalCount / (double) this.pageSize);
        if (this.totalPage == 0) {
            this.totalPage = 1;
        }
        this.pageNo = this.pageNo > this.totalPage ? this.totalPage : this.pageNo;
        /*
        this.url = "?pageNo="+pageNo+"&pageSize="+pageSize;
        this.prevUrl = "?pageNo="+(this.pageNo > 1 ? this.pageNo - 1 : 1)+"&pageSize="+pageSize;
        this.nextUrl = "?pageNo="+(this.pageNo.equals(this.totalPage) ? this.pageNo : this.pageNo + 1)+"&pageSize="+this.pageSize;
        */
    }
}
