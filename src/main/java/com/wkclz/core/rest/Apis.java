package com.wkclz.core.rest;

import cn.hutool.core.util.StrUtil;
import com.wkclz.core.base.Result;
import com.wkclz.core.pojo.entity.RestInfo;
import com.wkclz.core.util.RestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Apis {

    @GetMapping(Routes.APIS_LIST)
    public Result apisList(){
        List<RestInfo> mapping = RestUtil.getMapping();
        return Result.data(mapping);
    }


    @GetMapping(Routes.APIS_CODE)
    public String apisCode(String router){

        if (router == null){
            router = "";
        }

        String lineSeparator = System.getProperty("line.separator");
        List<RestInfo> mappings = RestUtil.getMapping();

        StringBuilder sb = new StringBuilder();
        sb.append("import request from '@/utils/request'");
        sb.append(lineSeparator).append(lineSeparator);
        for (RestInfo mapping :mappings) {
            String funTemp;
            if (RequestMethod.GET.name().equals(mapping.getRequestMethod())) {
                funTemp = "export function {}(params) { return request({ url: '{}', method: 'get', params: params }) } // {}";
            } else {
                funTemp = "export function {}(data) { return request({ url: '{}', method: 'post', data: data }) } // {}";
            }
            String fun = StrUtil.format(funTemp, mapping.getRestName(), router + mapping.getUri(), mapping.getRestDesc());
            sb.append(fun).append(lineSeparator);
        }
        return sb.toString();
    }


}
