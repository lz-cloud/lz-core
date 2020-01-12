package com.wkclz.core.base;

import com.wkclz.core.base.annotation.Desc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: common mapper
 * Created: wangkaicun @ 2019-01-17 14:43
 * Updadte: wangkaicun @ 2019-12-31 23:01:47
 */
public interface BaseMapper<Model> {

    @Desc("统计")
    Integer count(Model model);

    @Desc("用ID查找")
    Model getById(@Param("id") Long id);

    @Desc("用 Model 查找")
    Model getByModel(Model model);

    @Desc("查询列表，不包含Blobs")
    List<Model> list(Model model);

    @Desc("(选择性)插入")
    Long insert(Model model);

    @Desc("全量批量插入")
    Integer insertBatch(@Param("list") List<Model> models);

    @Desc("更新(带乐观锁)")
    Integer updateAll(Model model);

    @Desc("选择性更新(带乐观锁)")
    Integer updateSelective(Model model);

    @Desc("批量更新(不带乐观锁)")
    Integer updateBatch(@Param("list") List<Model> model);

    @Desc("删除")
    Integer delete(Model model);

    @Desc("批量删除")
    Integer deleteBatch(Model model);

}
