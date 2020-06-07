package com.wkclz.core.pojo.entity;

import java.io.Serializable;

@Deprecated
public class RedisMessage implements Serializable {
    
    private String id;

    private String message;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String name) {
        this.message = name;
    }    

}