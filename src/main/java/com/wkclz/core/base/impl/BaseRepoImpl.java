package com.wkclz.core.base.impl;

import com.wkclz.core.base.BaseMapper;
import com.wkclz.core.base.BaseModel;
import com.wkclz.core.base.BaseRepoHandler;
import com.wkclz.core.base.PageData;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.util.ClassUtil;
import com.wkclz.core.util.IntegerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseRepoImpl<Model extends BaseModel, Example> extends BaseRepoHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseRepoImpl.class);

    protected BaseMapper<Model, Example> mapper;
    // 用于在 Model 为 null 时的实例化
    protected Class modelClazz;

    public Model get(Long id) {
        if (id == null) {
            throw BizException.error("id is null");
        }
        Model model = mapper.selectByPrimaryKey(id);
        if (model == null || model.getStatus() == 0) {
            return null;
        }
        return model;
    }

    public Model get(Model model) {
        model.setIsPage(0);
        List<Model> list = list(model);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw BizException.error("查询结果不唯一，存在 {} 条，请确保数据完整性，或者换用 list 查询", list.size());
        }
        return list.get(0);
    }

    public Integer update(Model model) {
        if (model.getId() == null) {
            throw BizException.error("id is null");
        }
        if (model.getVersion() == null) {
            throw BizException.error("version is null");
        }
        model = setBaseInfo(model);
        Model target = mapper.selectByPrimaryKey(model.getId());
        if (target == null) {
            throw BizException.error("id is error");
        }
        if (!model.getVersion().equals(target.getVersion())) {
            throw BizException.error("data is not lastest, can not update more than one time without get new one");
        }

        try {
            Class clazz = model.getClass();
            Method copyIfNotNull = clazz.getDeclaredMethod("copyIfNotNull", new Class[]{clazz, clazz});
            copyIfNotNull.invoke(null, model, target);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException", e);
        }
        target.setVersion(target.getVersion() + 1);
        return mapper.updateByPrimaryKeySelective(target);
    }

    public Long insert(Model model) {
        model = setBaseInfo(model);
        mapper.insertSelective(model);
        return model.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer insert(List<Model> list, String[] columns) {
        return insert(list, Arrays.asList(columns));
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer insert(List<Model> list, List<String> columns) {
        if (list == null || list.size() == 0 || columns == null || columns.size() == 0) {
            return 0;
        }
        int size = list.size();
        int counter = 0;
        int seccuss = 0;
        List<Model> tmpList = new ArrayList<>();
        for (Model model : list) {
            counter++;
            model = setBaseInfo(model);
            tmpList.add(model);
            if (counter % INSERT_SIZE == 0 || counter == size) {
                seccuss += mapper.insertBatch(tmpList, getMapColumns(columns));
                tmpList = new ArrayList<>();
            }
        }
        return seccuss;
    }

    public long count() {
        return countByExample(null);
    }

    public long count(Model model) {
        return countByExample(model);
    }

    public Integer del(Long id) {
        return del(Arrays.asList(id));
    }

    public Integer del(String ids) {
        return del(IntegerUtil.str2LongList(ids));
    }

    public Integer del(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }

        Model model = getNewInstance(modelClazz);
        Example example = null;
        try {
            Method createDelExample = modelClazz.getDeclaredMethod("createDelExample", new Class[]{List.class});
            example = (Example) createDelExample.invoke(null, ids);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException", e);
        }


        model = setBaseInfo(model);
        model.setStatus(0);
        return mapper.updateByExampleSelective(model, example);
    }

    public List<Model> list() {
        return list(null);
    }

    public List<Model> list(Model model) {
        if (model == null) {
            model = getNewInstance(modelClazz);
        }
        model.setIsPage(0);
        return selectByExample(model).getRows();
    }

    public PageData<Model> page() {
        return selectByExample(null);
    }

    public PageData<Model> page(Model model) {
        return selectByExample(model);
    }


    /**
     * selectByExample
     *
     * @param model
     * @return
     */

    private PageData<Model> selectByExample(Model model) {
        // 是否分页【预处理】
        pagePreHandle(model);
        Example example = getExample(model);

        List<Model> list = mapper.selectByExample(example);
        PageData pageData = ansyList2Page(model, list);

        pageData.setRows(list);
        return pageData;
    }

    private long countByExample(Model model) {
        Example example = getExample(model);
        return mapper.countByExample(example);
    }

    // 获取 example
    private Example getExample(Model model) {
        Example example;
        try {
            Method createExample = ClassUtil.getModelMethod(model.getClass(), "createExample");
            example = (Example) createExample.invoke(null, model);
            return example;
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        }
        return null;
    }


}