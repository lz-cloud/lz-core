package com.wkclz.core.base;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-01-17 15:22:38
 */
public interface BaseRepo<Model> {

    /**
     * id 查询数据对象
     *
     * @param id 数据主键
     * @return Model
     */
    Model get(Long id);

    /**
     * 条件查询数据对象
     * 当查询结果大于一条时，只抛出 RuntimeException
     *
     * @param model 查询条件
     * @return Model
     */
    Model get(Model model);

    /**
     * 对象更新数据【为空时不更新】
     *
     * @param model 数据对象
     * @return 更新状态
     */
    Integer update(Model model);

    /**
     * 插入数据
     *
     * @param model 数据对象
     * @return 数据ID
     */
    Long insert(Model model);

    /**
     * 插入批量数据
     *
     * @param models  数据对象
     * @param columns 指定列
     * @return 成功条数
     */
    Integer insert(List<Model> models, String[] columns);

    /**
     * 插入批量数据
     *
     * @param models  数据对象
     * @param columns 指定列
     * @return 成功条数
     */
    Integer insert(List<Model> models, List<String> columns);

    /**
     * 统计数据量
     *
     * @return 统计结果
     */
    long count();

    /**
     * 按条件统计数据量
     *
     * @param model 据对象
     * @return 统计结果
     */
    long count(Model model);

    /**
     * 使用主键删除数据
     *
     * @param id  主键ID
     * @return 删除状态
     */
    Integer del(Long id);

    /**
     * 使用主键【字符串】删除数据
     *
     * @param ids 主键ID字符串
     * @return 删除状态
     */
    Integer del(String ids);

    /**
     * 使用主键【列】删除数据
     *
     * @param ids 主键ID列
     * @return 删除状态
     */
    Integer del(List<Long> ids);

    /**
     * 查询所有数据【不分页，慎用】
     *
     * @return 数据列
     */
    List<Model> list();

    /**
     * 查询所有数据【不分页，慎用】
     *
     * @param model 据对象
     * @return 数据列
     */
    List<Model> list(Model model);

    /**
     * 分页查询数据，无条件
     *
     * @return 分页数据列
     */
    PageData<Model> page();

    /**
     * 分页查询数据，有条件
     *
     * @return 分页数据列
     */
    PageData<Model> page(Model model);


}
