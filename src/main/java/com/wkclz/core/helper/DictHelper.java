package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Sys;
import com.wkclz.core.config.SystemConfig;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import com.wkclz.core.pojo.entity.Dict;
import com.wkclz.core.pojo.entity.DictType;
import org.apache.commons.lang3.StringUtils;
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

        if (!Sys.getBean(SystemConfig.class).isCloud()){
            DICT_TYPES = dictTypes;
            return true;
        }
        RedisMsgBody body = new RedisMsgBody();
        body.setTag(DictHelper.class.getName());
        body.setMsg(dictTypes);

        String msg = JSONArray.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = Sys.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(Sys.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;
    }


    /**
     * 初始化 dictTypes 【仅给队列调用，不允许直接调用】
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
            return null;
        }
        if (DICT_TYPES == null){
            throw BizException.error("do not init dict yet, please wait!");
        }

        for (DictType type : DICT_TYPES) {
            if (dictType.equals(type.getDictType())){
                return type.getDicts();
            }
        }
        return null;
    }

    /**
     * 获取字典详情
     * @param dictType
     * @return
     */
    public static Dict get(String dictType, String dictKey){
        if (StringUtils.isBlank(dictKey)){
            return null;
        }
        List<Dict> dicts = get(dictType);
        if (CollectionUtils.isEmpty(dicts)) {
            return null;
        }

        for (Dict dict : dicts) {
            if (dictKey.equals(dict.getDictKey())){
                return dict;
            }
        }
        return null;
    }

    /**
     * 获取字典值
     * @param dictType
     * @param dictKey
     * @return
     */
    public static String getValue(String dictType, String dictKey){
        Dict dict = get(dictType, dictKey);
        if (dict == null){
            return null;
        }
        return dict.getDictValue();
    }
}











