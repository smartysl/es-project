package com.ResponseTemplate;

public class TransactionResponseTemplate {

    protected Integer errorNo;
    protected String errorMsg;

    public TransactionResponseTemplate(Integer errorNo, String errorMsg) {
        this.errorNo = errorNo;
        this.errorMsg = errorMsg;
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

    public Integer getErrorNo() {
        return errorNo;
    }
}
