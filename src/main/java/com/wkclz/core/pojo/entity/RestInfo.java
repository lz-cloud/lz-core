package com.wkclz.core.pojo.entity;

import org.springframework.web.bind.annotation.RequestMethod;

public class RestInfo {

    private RequestMethod requestMethod;

    private String uri;

    private String restName;

    private String functionPath;

    private String restDesc;

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getFunctionPath() {
        return functionPath;
    }

    public void setFunctionPath(String functionPath) {
        this.functionPath = functionPath;
    }

    public String getRestDesc() {
        return restDesc;
    }

    public void setRestDesc(String restDesc) {
        this.restDesc = restDesc;
    }
}
