package com.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
public class ResourceManager {

    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RestTemplate restTemplate;

    public Map<String, Object> getEsDocument(String indexName, String documentId) throws IOException {
        GetRequest getRequest = new GetRequest(indexName).id(documentId);
        try {
            GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Async("operatorPool")
    public void writeEsDocuments(List<Operator> operators) {
        Long timeStamp = System.currentTimeMillis();
        operators.forEach(operator -> {
            operator.getData().put("_timestamp", timeStamp);
            try {
                doWriteEsDocument(
                        operator.getOperatorType(),
                        operator.getIndexName(),
                        operator.getDocumentId(),
                        operator.getData()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void doWriteEsDocument(Operator.OperatorType operatorType, String indexName, String documentId, Map<String, Object> data) throws IOException {

        switch (operatorType) {
            case INDEX -> indexEsDocument(indexName, documentId, data);
            case CREATE -> createEsDocument(indexName, documentId, data);
            case UPDATE -> updateEsDocument(indexName, documentId, data);
            case DELETE -> deleteEsDocument(indexName, documentId);
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

    @Cacheable(cacheNames = {"route"})
    public String getEsRoutedNodeName(String indexName, String routing) throws JsonProcessingException {
        String url = String.format("http://localhost:9200/%s/_search_shards?routing=%s", indexName, routing);
        Object jsonResponse = restTemplate.getForObject(url, Object.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> metaData = mapper.readValue(mapper.writeValueAsString(jsonResponse), new TypeReference<Map<String, Object>>() {});
        List<List<Map<String, Object>>> shards = (List<List<Map<String, Object>>>) metaData.get("shards");
        for(Map<String, Object> shard: shards.get(0)){
            if((Boolean) shard.get("primary")) {
                return (String) shard.get("node");
            }
        }
        return null;
    }
}
