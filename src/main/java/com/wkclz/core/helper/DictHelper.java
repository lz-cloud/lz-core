package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import com.wkclz.core.pojo.entity.Dict;
import com.wkclz.core.pojo.entity.DictType;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class DictHelper extends BaseHelper {

    private static List<DictType> DICT_TYPES = null;

    /**
     * 更新全部缓存
     */
    public static boolean reflash() {
        return reflash(DICT_TYPES);
    }
    public static boolean reflash(List<DictType> dictTypes) {
        if (CollectionUtils.isEmpty(dictTypes)) {
            throw BizException.error("dictTypes can not be null or empty!");
        }
        RedisMsgBody body = new RedisMsgBody();
        body.setTag(DictHelper.class.getName());
        body.setMsg(dictTypes);

        String msg = JSONArray.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = Sys.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(RedisTopicConfig.CACHE_CONFIG_TOPIC, msg);
        return true;
    }


    /**
     * 初始化 accessUris 【仅给队列调用，不允许直接调用】
     */
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("dictTypes can not be null or empty!");
        }
        List<DictType> dictTypes = JSONArray.parseArray(msg.toString(), DictType.class);
        return setLocal(dictTypes);
    }

    public static boolean setLocal(List<DictType> dictTypes) {
        if (CollectionUtils.isEmpty(dictTypes)) {
            throw BizException.error("dictTypes can not be null or empty!");
        }
        DICT_TYPES = dictTypes;
        return true;
    }

    public static List<DictType> getLocal() {
        return DICT_TYPES;
    }


    /**
     * 获取字典列表
     * @param dictType
     * @return
     */
    public static List<Dict> get(String dictType){
        if (StringUtils.isBlank(dictType)){
            throw BizException.error("dictType can not be null!");
        }
        if (DICT_TYPES == null){
            throw BizException.error("do not init dict yet, please wait!");
        }

        for (DictType type : DICT_TYPES) {
            if (dictType.equals(type.getDictType())){
                return type.getDicts();
            }
        }
        throw BizException.error("dictType is not exist!");
    }

    /**
     * 获取字典详情
     * @param dictType
     * @return
     */
    public static Dict get(String dictType, String dictKey){
        if (StringUtils.isBlank(dictKey)){
            throw BizException.error("dictKey can not be null!");
        }
        List<Dict> dicts = get(dictType);

        for (Dict dict : dicts) {
            if (dictKey.equals(dict.getDictKey())){
                return dict;
            }
        }
        throw BizException.error("dictKey is not exist!");
    }

    /**
     * 获取字典值
     * @param dictType
     * @param dictKey
     * @return
     */
    public static String getValue(String dictType, String dictKey){
        Dict dict = get(dictType, dictKey);
        return dict.getDictValue();
    }
}











