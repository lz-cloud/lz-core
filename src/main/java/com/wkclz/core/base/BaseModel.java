package com.wkclz.core.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wkclz.core.pojo.enums.DateRangeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午10:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseModel {

    protected static final String DEFAULE_ORDER_BY = "sort, id desc";

    private Integer id;
    private Integer orgId;
    private String mobile;
    private String email;
    private String code;

    private Integer userId;
    private Integer sort;

    private String comments;
    private Integer version;
    private Integer status;

    private Integer isPage;
    private Integer pageNo;
    private Integer pageSize;

    private String orderBy;
    private List<Integer> ids;

    private String keyword;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeTo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Integer createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastUpdateTime;
    private Integer lastUpdateBy;

    private DateRangeType dateRangeType;

    private String messageId;
    private Long count;

    private Integer debug;

    public void init() {
        if (this.pageNo == null || this.pageNo < 1) {
            this.pageNo = 1;
        }
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = 10;
        }
    }

}
