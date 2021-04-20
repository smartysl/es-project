package com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShardingAlgorithm implements ComplexKeysShardingAlgorithm {

    private final static Map<String, String> esToDbMapper = new HashMap<>() {{
        put("LtSNmRQDTFyYoVngDpYxsA", "node1");
        put("hSsb39THTy-b1asxGYgYHQ", "node2");
    }};

    private final static boolean test = true;

    private Map<Map.Entry<String, String>, Collection<String>> cache = new ConcurrentHashMap<>();

    @Override
    public Collection<String> doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
        Map<String, List> shardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        String docId = (String) shardingValuesMap.get("doc_id").get(0);
        String indexName = (String) shardingValuesMap.get("index_name").get(0);
        return cache.computeIfAbsent(new AbstractMap.SimpleEntry<>(docId, indexName), entry -> {
            RestTemplate restTemplate = HttpClient.getRestTemplate();
            try {
                String esNodeName = getEsRoutedNodeName(restTemplate, entry.getKey(), entry.getValue());
                return Collections.singletonList(ShardingAlgorithm.esToDbMapper.get(esNodeName));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private String getEsRoutedNodeName(RestTemplate restTemplate, String routing, String indexName) throws JsonProcessingException {
        if(ShardingAlgorithm.test) {
            if(Integer.parseInt(routing) % 2 == 1) {
                return "LtSNmRQDTFyYoVngDpYxsA";
            } else {
                return "hSsb39THTy-b1asxGYgYHQ";
            }
        }

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
