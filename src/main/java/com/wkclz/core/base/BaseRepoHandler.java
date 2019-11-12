package com.wkclz.core.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2018-01-21 下午7:46
 */
public class BaseRepoHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseRepoHandler.class);

    protected static final int INSERT_SIZE = 1200;

    private static Long getUserId() {
        Object userObj = ThreadLocals.get("user");
        if (userObj != null) {
            User user = (User) userObj;
            return user.getUserId();
        }
        return null;
    }


    /**
     * @param @param  obj
     * @param @param  req
     * @param @return 设定文件
     * @throws
     * @Title: 处理基本信息【for update】
     * @Description:
     * @author wangkaicun @ current time
     */
    protected static <T extends BaseModel> T setBaseInfo(T model) {

        Long userId = getUserId();
        if (userId != null) {
            model.setUpdateBy(userId);
            if (model.getId() == null && model.getCreateBy() == null) {
                model.setCreateBy(userId);
            }
        }
        // model.setUpdateTime(new Date()); 让数据库做更新
        return model;
    }


    /**
     * 分页预处理
     *
     * @param model
     */
    public static <T extends BaseModel> boolean pagePreHandle(T model) {

        BeanUtil.removeBlank(model);

        // 是否分页
        boolean isPage = model.getIsPage() == null || model.getIsPage() == 1;
        if (isPage) {
            model.init();
            PageHelper.startPage(model.getPageNo(), model.getPageSize());
        }

        String orderBy = model.getOrderBy();
        // 注入风险检测
        if (orderBy != null && !orderBy.equals(BaseModel.DEFAULE_ORDER_BY) && sqlInj(orderBy)) {
            throw new RuntimeException("orderBy 有注入风险，请谨慎操作！");
        }

        // 大小写处理
        model.setOrderBy(StringUtil.check2LowerCase(orderBy, "DESC"));
        model.setOrderBy(StringUtil.check2LowerCase(orderBy, "ASC"));
        // 驼峰处理
        model.setOrderBy(StringUtil.camelToUnderline(orderBy));

        // keyword 查询处理
        if (StringUtils.isNotBlank(model.getKeyword())) {
            model.setKeyword("%" + model.getKeyword() + "%");
        }
        // 时间范围查询处理
        DateUtil.formatDateRange(model);
        return isPage;
    }

    /**
     * 分页查询处理，返回类型为 Object
     *
     * @param model
     * @param list
     * @return
     */
    protected static <T extends BaseModel> PageData<T> pageSelect(T model, Object list) {

        // 是否分页
        boolean isPage = model.getIsPage() == null || model.getIsPage() == 1;

        // pageData 用于重新整合数据
        PageData<T> pageData = new PageData<>(model.getPageNo(), model.getPageSize());

        // 是否分页
        if (isPage) {
            // 此处强转是为了拿到 Total
            Page<T> listPage = (Page<T>) list;
            pageData.setTotalCount(listPage.getTotal());
        } else {
            pageData.setPageNo(null);
            pageData.setPageSize(null);
            pageData.setTotalPage(null);
        }

        pageData.setRows((List<T>) list);

        // 完成分页查询
        return pageData;
    }


    /**
     * 分页查询处理，返回类型为 Map
     *
     * @param model
     * @param list
     * @return
     */
    protected static <T> PageData<Map<String, T>> pageSelect4Map(BaseModel model, List<Map<String, T>> list) {

        // 是否分页
        boolean isPage = model.getIsPage() == null || model.getIsPage() == 1;

        // pageData 用于重新整合数据
        PageData<Map<String, T>> pageData = new PageData<>(model.getPageNo(), model.getPageSize());

        // 是否分页
        if (isPage) {
            // 此处强转是为了拿到 Total
            Page<Map<String, T>> listPage = (Page<Map<String, T>>) list;
            pageData.setTotalCount(listPage.getTotal());
        } else {
            pageData.setPageNo(null);
            pageData.setPageSize(null);
            pageData.setTotalPage(null);
        }

        // 驼峰转换
        pageData.setRows(MapUtil.toReplaceKeyLow(list));

        // 完成分页查询
        return pageData;
    }

    protected static <M, T extends BaseModel> PageData<T> ansyList2Page(T model, List<M> list) {
        boolean isPage = model.getIsPage() == null || model.getIsPage() == 1;
        PageData<T> pageData = new PageData<>(model.getPageNo(), model.getPageSize());
        // 是否分页
        if (isPage) {
            // 转换为pageHelper的分页对象来提取分页信息
            Page<M> listPage = (Page<M>) list;
            pageData.setTotalCount(listPage.getTotal());
        } else {
            pageData.setPageNo(null);
            pageData.setPageSize(null);
        }
        return pageData;
    }

    /**
     * Class 创建实例
     *
     * @param clazz
     * @param <Model>
     * @return
     */
    public static <Model> Model getNewInstance(Class clazz) {
        Model model = null;
        try {
            model = (Model) clazz.newInstance();
        } catch (InstantiationException e) {
            logger.error("InstantiationException", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        }
        return model;
    }


    /**
     * 获取Map 类型的 columns
     *
     * @param columns 列名
     * @return
     */
    protected static Map<String, String> getMapColumns(List<String> columns) {
        if (columns == null || columns.size() == 0) {
            return null;
        }
        Map<String, String> columnsMap = new HashMap<>();
        columns.forEach(column -> {
            String columnUnderLine = StringUtil.camelToUnderline(column);
            String columnCamel = StringUtil.underlineToCamel(columnUnderLine);
            columnsMap.put(columnCamel, columnCamel);
        });
        return columnsMap;
    }

    /**
     * 插入 ElasticSearch
     *
     * @param client
     * @param table
     * @param obj
     */
    /*
    protected static <T> void insertEs(RestHighLevelClient client, String table, T obj) {
        if (obj == null) {
            throw new RuntimeException("model is null");
        }
        IndexRequest indexRequest = new IndexRequest(table, "doc");
        String jsonString = JSONObject.toJSONString(obj);
        indexRequest.source(jsonString, XContentType.JSON);
        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // who care ?
        }
    }
    */


    /**
     * 从 jdbc 查询
     *
     * @param conn
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jdbcExecutor(Connection conn, String sql, Class<T> clazz) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Map> maps = ResultSetMapper.toMapList(results);
            List<T> list = MapUtil.map2ObjList(maps, clazz);
            return list;
        } catch (SQLException e) {
            logger.error("SQLException", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("SQLException", e);
                }
            }
        }
        return null;
    }

    public static List<Map> jdbcExecutor(Connection conn, String sql) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Map> maps = ResultSetMapper.toMapList(results);
            return maps;
        } catch (SQLException e) {
            logger.error("SQLException", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("SQLException", e);
                }
            }
        }
        return null;
    }


    /**
     * 注入风险检测。默认只作用在 orderBy 上。不能用于其他地方的注入检测
     * 注入风险：
     * 1、orderBy 中使用 ${}。可用此方法进行检测，不可使用其他字段传入
     * 2、MBG 的 noValue，singleValue，betweenValue，listValue 注入风险：Example 的 Criteria 产生，无注入风险
     * 3、MBG 的 like 注入风险：like 前的由 Example 控制，后的为 #{}, 无注入风险
     * 4、Custom 实现的 like 强制使用 AND column like concat("%",#{value},"%")
     *
     * @param str
     * @return
     */
    private static boolean sqlInj(String str) {
        str = str.toLowerCase();
        String injStr = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;| or |-|+";
        String[] injStra = injStr.split("\\|");
        for (int i = 0; i < injStra.length; i++) {
            String is = injStra[i];
            if (str.indexOf(is) >= 0) {
                return true;
            }
        }
        return false;
    }

}
