package com.wkclz.core.base;

import com.wkclz.core.base.annotation.Desc;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.enums.ResultStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-01-17 15:22:38
 * Updadte: wangkaicun @ 2019-12-31 23:39:46
 */
public class BaseService<Model extends BaseModel, Mapper extends BaseMapper<Model>> {

    @Autowired
    protected Mapper mapper;

    @Desc("统计")
    public Integer count(@NotNull Model model){
        return mapper.count(model);
    }

    @Desc("用ID查找")
    public Model get(@NotNull Long id){
        return mapper.getById(id);
    }

    @Desc("用 Model 查找")
    public Model get(@NotNull Model model){
        return mapper.getByModel(model);
    }

    @Desc("查询列表，不包含Blobs")
    public List<Model> list(@NotNull Model model){
        return mapper.list(model);
    }

    @Desc("查询列分页，不包含Blobs")
    public PageData<Model> page(@NotNull Model model){
        model.init();
        Integer count = mapper.count(model);
        List<Model> list = null;
        if (count > 0){
            list = mapper.list(model);
        }
        if (list == null){
            list = new ArrayList<>();
        }
        PageData<Model> pageData = new PageData<>(model.getPageNo(), model.getPageSize(), count, list);
        return pageData;
    }

    @Desc("(选择性)插入")
    public Long insert(@NotNull Model model){
        mapper.insert(model);
        return model.getId();
    }

    @Desc("全量批量插入")
    public Integer insert(@NotNull List<Model> models){
        return mapper.insertBatch(models);
    }

    @Desc("更新(带乐观锁)")
    public Integer updateAll(@NotNull Model model){
        Integer update = mapper.updateAll(model);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(带乐观锁)")
    public Integer updateSelective(@NotNull Model model){
        Integer update = mapper.updateSelective(model);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("批量更新(不带乐观锁)")
    public Integer update(@NotNull List<Model> models){
        return mapper.updateBatch(models);
    }

    @Desc("删除")
    public Integer delete(@NotNull Long id){
        BaseModel baseModel = new BaseModel();
        baseModel.setId(id);
        Model model = (Model)baseModel;
        return mapper.delete(model);
    }

    @Desc("删除")
    public Integer delete(@NotNull List<Long> ids){
        BaseModel baseModel = new BaseModel();
        baseModel.setIds(ids);
        Model model = (Model)baseModel;
        return mapper.delete(model);
    }
    /*
    @Desc("批量删除")
    public Integer delete(@NotNull List<Model> models){
        if (CollectionUtils.isEmpty(models)){
            throw BizException.error("models can not be null");
        }
        List<Long> ids = new ArrayList<>();
        models.forEach(model -> {
            Long tmpId = model.getId();
            List<Long> tmpIds = model.getIds();
            if (tmpId != null){
                ids.add(tmpId);
            }
            if (CollectionUtils.isNotEmpty(tmpIds)){
                ids.addAll(tmpIds);
            }
        });
        Model model = models.get(0);
        model.setId(null);
        model.setIds(ids);
        return mapper.delete(model);
    }
    */
    @Desc("批量删除")
    public Integer delete(@NotNull Model model){
        List<Long> ids = new ArrayList<>();
        Long tmpId = model.getId();
        List<Long> tmpIds = model.getIds();
        if (tmpId != null){
            ids.add(tmpId);
        }
        if (CollectionUtils.isNotEmpty(tmpIds)){
            ids.addAll(tmpIds);
        }
        if (CollectionUtils.isEmpty(ids)){
            throw BizException.error("id or ids can not be null at the same time");
        }
        // id 和 ids 保证只有一个存在
        if (ids.size() == 1){
            model.setId(ids.get(0));
            model.setIds(null);
        } else {
            model.setId(null);
            model.setIds(ids);
        }
        Integer delete = mapper.delete(model);
        if (delete == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return delete;
    }

}
