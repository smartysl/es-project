package com.service;

import java.util.Map;

public class GetServiceResult extends DataServiceResult{

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    GetServiceResult(Integer errorNo, String errorMsg, Map<String, Object> data) {
        super(errorNo, errorMsg);
        this.data = data;
    }
}
