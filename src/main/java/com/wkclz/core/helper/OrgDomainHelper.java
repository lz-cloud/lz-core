package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.base.ThreadLocals;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.enums.ResultStatus;
import com.wkclz.core.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class OrgDomainHelper extends BaseHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String NAME_SPACE = "_ORG_DOMAINS";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static Map<String, Object> ORG_DOMAINS = null;

    /**
     * 初始化 ORG_DOMAINS
     *
     * @param orgDomains
     */
    public synchronized void setOrgDomains(Map<String, Object> orgDomains) {
        if (orgDomains == null || orgDomains.size() == 0) {
            throw new BizException("orgDomains can not be null or empty!");
        }
        stringRedisTemplate.opsForValue().set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONObject.toJSONString(orgDomains));
        ORG_DOMAINS = orgDomains;
    }

    private synchronized Map<String, Object> getOrgDomains() {
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && ORG_DOMAINS != null) {
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) < 0) {
                return ORG_DOMAINS;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String orgDomainsStr = stringRedisTemplate.opsForValue().get(Sys.APPLICATION_GROUP + NAME_SPACE);
        Map orgDomains = JSONObject.parseObject(orgDomainsStr, Map.class);
        ORG_DOMAINS = orgDomains;
        return ORG_DOMAINS;
    }


    /**
     * 获取 orgId
     * @return
     */
    public Long getOrgId() {
        Object orgId = ThreadLocals.get("orgId");
        if (orgId == null){
            throw BizException.error("can not get OrgId: {}", RequestHelper.getRequestUrl());
        }
        return Long.valueOf(orgId.toString());
    }

    /**
     * 获取 orgId
     * @param req
     * @return
     */
    public Long getOrgId(HttpServletRequest req) {
        Object orgIdObj = ThreadLocals.get("orgId");
        if (orgIdObj != null){
            return Long.valueOf(orgIdObj.toString());
        }

        if (req == null) {
            return null;
        }
        String orgIdStr = req.getHeader("orgId");
        if (orgIdStr != null){
            return Long.valueOf(orgIdObj.toString());
        }

        String domain = UrlUtil.getDomain(req);

        if (StringUtils.isBlank(domain)) {
            throw BizException.error("Can not get domain from the request: {}", RequestHelper.getRequestUrl());
        }

        Map<String, Object> orgDomains = getOrgDomains();
        if (orgDomains == null || orgDomains.size() == 0) {
            throw BizException.error("orgDomains must be init after system start up: {}", RequestHelper.getRequestUrl());
        }

        orgIdObj = orgDomains.get(domain);
        if (orgIdObj == null) {
            throw BizException.error("can not get OrgId from request: {}", RequestHelper.getRequestUrl());
        }
        Long orgId = Long.valueOf(orgIdObj.toString());
        ThreadLocals.set("orgId", orgId);
        return orgId;
    }

    /**
     * 检查 orgId
     * @param req
     * @param rep
     * @return
     */
    public boolean checkOrgDomains(HttpServletRequest req, HttpServletResponse rep) {
        Long orgId = getOrgId(req);
        if (orgId != null){
            return true;
        }
        String url = req.getRequestURL().toString();

        logger.error("origin url can not be cors, url : {}, ip: {}", url, IpHelper.getIpAddr(req));
        Result result = new Result();
        result.setMoreError(ResultStatus.ORIGIN_CORS);
        return Result.responseError(rep, result);
    }


}
