package org.resign.backend.es;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.resign.backend.domain.Resource;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamoDbStreamReader implements RequestHandler<DynamodbEvent, Integer> {

    @Override
    public Integer handleRequest(DynamodbEvent event, Context context) {
    	
        context.getLogger().log("Received event: " + event);

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com", 443, "https")));
        ObjectMapper objectMapper = new ObjectMapper();
        
        for (DynamodbStreamRecord record : event.getRecords()) {
            
            AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
    				.withRegion(Regions.EU_WEST_3)
    				.build();
    		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
    		Resource r= mapper.marshallIntoObject(Resource.class, record.getDynamodb().getNewImage());

            Map<String, Object> documentMapper = objectMapper.convertValue(r, Map.class);
            for(String s: documentMapper.keySet()) {
            	context.getLogger().log(s + ": " + documentMapper.get(s));
            }
            
            context.getLogger().log("Creating indexing request");
            IndexRequest indexRequest = new IndexRequest("resources", "res", r.getUserId() + "-" + r.getTs())
                    .source(documentMapper);

            try {
				IndexResponse indexResponse = client.index(indexRequest);
			} catch (IOException e) {
				context.getLogger().log("ERROR: " + e.getMessage());
				e.printStackTrace();
			}
            
            context.getLogger().log("Indexing performed");
        }
        return event.getRecords().size();
    }
}