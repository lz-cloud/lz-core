package com.wkclz.core.base;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description: common mapper
 * Created: wangkaicun @ 2019-01-17 14:43
 */
@Mapper
public interface BaseMapper<Model, Example> {

    long countByExample(Example example);

    /**
     * not recommended to use
     * 不建议使用
     */
    @Deprecated
    Long insert(Model record);

    Long insertSelective(Model record);

    /**
     * only can be use when table with BLOBs
     * not recommended to use
     * 只有表存在 BLOB 字段时可用
     * 不建议使用
     */
    @Deprecated
    List<Model> selectByExampleWithBLOBs(Example example);

    List<Model> selectByExample(Example example);

    Model selectByPrimaryKey(Long id);

    /**
     * not recommended to use
     * 不建议使用
     */
    int updateByExampleSelective(@Param("record") Model record, @Param("example") Example example);

    /**
     * only can be use when table with BLOBs
     * not recommended to use
     * 只有表存在 BLOB 字段时可用
     * 不建议使用
     */
    int updateByExampleWithBLOBs(@Param("record") Model record, @Param("example") Example example);

    /**
     * not recommended to use
     * 不建议使用
     */
    int updateByExample(@Param("record") Model record, @Param("example") Example example);

    int updateByPrimaryKeySelective(Model record);

    /**
     * not recommended to use
     * 不建议使用
     */
    @Deprecated
    int updateByPrimaryKey(Model record);

    /**
     * only can be use when table with BLOBs
     * not recommended to use
     * 只有表存在 BLOB 字段时可用
     * 不建议使用
     */
    @Deprecated
    int updateByPrimaryKeyWithBLOBs(Model record);

    int insertBatch(@Param("record") List<Model> record, @Param("columnsMap") Map<String, String> columnsMap);

}
