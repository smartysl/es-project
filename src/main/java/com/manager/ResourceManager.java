package com.manager;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class ResourceManager {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Value("${app.test}")
    private boolean test;

    private static final Logger log = LoggerFactory.getLogger(ResourceManager.class);

    public ClusterHealthStatus getClusterHealthyStatus() {
        if(test) {
            return ClusterHealthStatus.GREEN;
        }
        try {
            ClusterHealthResponse healthResponse = restHighLevelClient.cluster().health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
            return healthResponse.getStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doWriteEsDocument(EsOperator.OperatorType operatorType, String indexName, String documentId, Map<String, Object> data) throws IOException {
        log.info("[Es数据写入] 类型={}, indexName={}, docId={}, data={}", operatorType, indexName, documentId, data);
        if(test) {
            return;
        }
        switch (operatorType) {
            case INDEX:
                indexEsDocument(indexName, documentId, data);
            case CREATE:
                createEsDocument(indexName, documentId, data);
            case UPDATE:
                updateEsDocument(indexName, documentId, data);
            case DELETE:
                deleteEsDocument(indexName, documentId);
        }
    }

    private void indexEsDocument(String indexName, String documentId, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName).source(data);
        if(documentId != null) {
            indexRequest.id(documentId);
        }
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createEsDocument(String indexName, String documentId, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName).source(data).create(true);
        if(documentId != null) {
            indexRequest.id(documentId);
        }
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void updateEsDocument(String indexName, String documentId, Map<String, Object> data) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(indexName, documentId).doc(data);
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void deleteEsDocument(String indexName, String documentId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, documentId);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
