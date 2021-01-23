package com.ResponseTemplate;

public class TransactionCreateResponse extends TransactionResponseTemplate{

    private static class Data {
        private final String transactionId;
        Data(String id) {
            this.transactionId = id;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }

    private final Data data;

    public Data getData() {
        return data;
    }

    public TransactionCreateResponse(Integer errorNo, String errorMsg, String transactionId) {
        super(errorNo, errorMsg);
        this.data = new Data(transactionId);
    }
}
