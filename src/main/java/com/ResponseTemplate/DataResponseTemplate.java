package com.ResponseTemplate;

public class DataResponseTemplate {

    protected Integer errorNo;
    protected String errorMsg;
    protected String transactionState;

    public DataResponseTemplate(Integer errorNo, String errorMsg, String transactionState) {
        this.errorNo = errorNo;
        this.errorMsg = errorMsg;
        this.transactionState = transactionState;
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

    public String getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(String transactionState) {
        this.transactionState = transactionState;
    }
}
