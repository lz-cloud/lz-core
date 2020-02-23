//package com.wkclz.core.rest;
//
//import com.wkclz.core.base.Result;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class ErrorHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
//
//    @GetMapping(value = "/error")
//    public Result error(HttpServletRequest request) {
//        Map<String, Object> map = new HashMap<>();
//        Enumeration<String> attributeNames = request.getAttributeNames();
//        while (attributeNames.hasMoreElements()){
//            String element = attributeNames.nextElement();
//            map.put(element, request.getAttribute(element));
//        }
//        logger.error("request error: {}", map);
//        Result result = new Result();
//        result.setData(map);
//        result.setCode(-1);
//        return result;
//    }
//}