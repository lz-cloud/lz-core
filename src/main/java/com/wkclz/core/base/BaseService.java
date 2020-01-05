package com.wkclz.core.base;

import com.wkclz.core.base.annotation.Desc;
import org.springframework.beans.factory.annotation.Autowired;

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
    public Integer count(Model model){
        return mapper.count(model);
    }

    @Desc("用ID查找")
    public Model get(Long id){
        return mapper.getById(id);
    }

    @Desc("用 Model 查找")
    public Model get(Model model){
        return mapper.getByModel(model);
    }

    @Desc("查询列表，不包含Blobs")
    public List<Model> list(Model model){
        return mapper.list(model);
    }

    @Desc("查询列分页，不包含Blobs")
    public PageData<Model> page(Model model){
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
    public Long insert(Model model){
        return mapper.insert(model);
    }

    @Desc("全量批量插入")
    public Integer insert(List<Model> models){
        return mapper.insertBatch(models);
    }

    @Desc("更新(带乐观锁)")
    public Integer updateAll(Model model){
        return mapper.updateAll(model);
    }

    @Desc("选择性更新(带乐观锁)")
    public Integer updateSelective(Model model){
        return mapper.updateSelective(model);
    }

    @Desc("批量更新(不带乐观锁)")
    public Integer update(List<Model> models){
        return mapper.updateBatch(models);
    }

    @Desc("保存(有ID则乐观锁更新,无则新增)")
    public Long save(Model model){
        if (model.getId() == null) {
            return mapper.insert(model);
        } else {
            Integer rt = mapper.updateSelective(model);
            if (rt == 1){
                return model.getId();
            }
            return 0L;
        }
    }

    @Desc("删除")
    public Integer delete(Long id){
        return mapper.delete(id);
    }

    @Desc("批量删除")
    public Integer delete(List<Long> ids){
        return mapper.deleteBatch(ids);
    }

}
