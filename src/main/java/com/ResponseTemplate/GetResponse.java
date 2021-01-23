package com.ResponseTemplate;

import java.util.Map;

public class GetResponse extends DataResponseTemplate{

    private Map<String, Object> data;

    public GetResponse(Integer errorNo, String errorMsg, String transactionState, Map<String, Object> data) {
        super(errorNo, errorMsg, transactionState);
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
