package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @GetMapping(value = "/error")
    public ResponseEntity<Result> error(HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>();
        map.put("status_code", request.getAttribute("javax.servlet.error.status_code"));
        map.put("servlet_name", request.getAttribute("javax.servlet.error.servlet_name"));
        map.put("request_uri", request.getAttribute("javax.servlet.error.request_uri"));
        map.put("request_uri", request.getAttribute("javax.servlet.forward.request_uri"));

        logger.error("err: {}", map);
        Result result = new Result();
        result.setData(map);
        result.setCode(-1);
        return new ResponseEntity<>(result, HttpStatus.BAD_GATEWAY);
    }
}