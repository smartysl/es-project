package com.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.TransactionManager;
import com.service.DataServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private TransactionManager transactionManager;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String transId = request.getParameter("transactionId");
        TransactionManager.TransactionState state = transactionManager.getTransactionState(transId);
        PrintWriter writer = response.getWriter();
        DataServiceResult serviceResult;
        if(state == null) {
            serviceResult = new DataServiceResult(1, "no such transaction");
        } else if(state != TransactionManager.TransactionState.UNCOMMITTED) {
            serviceResult = new DataServiceResult(2, "transaction is already been finished");
        } else {
            return true;
        }
        response.setHeader("Content-Type", "application/json");
        writer.write(mapper.writeValueAsString(serviceResult));
        return false;
    }
}
