package com.service;

import com.dao.mapper.EsMappingMapper;
import com.manager.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class GetService extends DataService{

    @Autowired
    ResourceManager resourceManager;
    @Autowired
    EsMappingMapper esMappingMapper;

    public GetServiceResult getDocument(String transId, String indexName, String documentId){
        GetServiceResult result = new GetServiceResult(0, "", null);
        try {
            //System.out.println(resourceManager.getEsRoutedNodeName(indexName, documentId));
            System.out.println(esMappingMapper.selectVersion("demo", "1"));
            Map<String, Object> data = resourceManager.getEsDocument(indexName, documentId);
            Long timeStamp = (Long) data.remove("_timeStamp");
            result.setData(data);
        } catch (IOException e) {
            e.printStackTrace();
            result.setErrorNo(3);
            result.setErrorMsg("disable to connect es server !!!");
        }
        return result;
    }
}
