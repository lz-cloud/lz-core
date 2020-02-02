package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.pojo.entity.RestInfo;
import com.wkclz.core.util.RestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Apis {

    @GetMapping(Routes.APIS)
    public Result apis(){
        List<RestInfo> mapping = RestUtil.getMapping();
        return Result.data(mapping);
    }
}
