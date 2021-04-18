package com.service;

public class DataServiceResult {
    private Integer errorNo;
    private String errorMsg;

    public DataServiceResult(Integer errorNo, String errorMsg) {
        this.errorNo = errorNo;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(Integer errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
