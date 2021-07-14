package com.wkclz.core.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wkclz.core.base.annotation.Desc;
import com.wkclz.core.pojo.enums.DateRangeType;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午10:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseModel {

    public static final String DEFAULE_ORDER_BY = "sort, id desc";

    @Desc("主键ID")
    private Long id;
    /*
    private String mobile;
    private String email;
    private String code;
    private String messageId;
    */

    @Desc("用户ID")
    private Long userId;
    @Desc("租户ID")
    private Long tenantId;

    /**
     * 查询辅助
     */
    @Desc("分页页码")
    private Integer pageNo;
    @Desc("分页大小")
    private Integer pageSize;
    private Integer offset;
    @Desc("查询排序规则")
    private String orderBy;
    @Desc("统计数")
    private Integer count;

    /**
     * 查询辅助
     */
    @Desc("主键ID数组")
    private List<Long> ids;
    private String keyword;
    @Desc("创建时间从")
    private Date timeFrom;
    @Desc("创建时间到")
    private Date timeTo;
    @Desc("创建时间范围:HOUR(时),YESTERDAY(天),WEEK(周),MONTH(月),QUATER(季),YEAR(年)")
    private DateRangeType dateRangeType;

    /**
     * 数据库规范字段
     */
    @Desc("排序号，越大越往后")
    private Integer sort;
    @Desc("创建时间")
    private Date createTime;
    @Desc("创建人ID")
    private Long createBy;
    @Desc("创建人昵称")
    private String createByName;
    @Desc("更新时间")
    private Date updateTime;
    @Desc("更新人ID")
    private Long updateBy;
    @Desc("更新人昵称")
    private String updateByName;
    @Desc("备注")
    private String comments;
    @Desc("数据版本")
    private Integer version;
    @Desc("数据状态:0(已删除),1(有效)")
    private Integer status;

    private Integer debug;

    public void init() {
        if (this.pageNo == null || this.pageNo < 1) {
            this.pageNo = 1;
        }
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = 10;
        }
        this.offset = (this.pageNo -1 ) * this.pageSize;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public DateRangeType getDateRangeType() {
        return dateRangeType;
    }

    public void setDateRangeType(DateRangeType dateRangeType) {
        this.dateRangeType = dateRangeType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDebug() {
        return debug;
    }

    public void setDebug(Integer debug) {
        this.debug = debug;
    }
}
