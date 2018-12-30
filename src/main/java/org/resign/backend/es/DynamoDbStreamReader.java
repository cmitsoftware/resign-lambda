package org.resign.backend.es;

import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.resign.backend.domain.Resource;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamoDbStreamReader implements RequestHandler<DynamodbEvent, Integer> {

    @SuppressWarnings("unchecked")
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

			try {
    			 
				context.getLogger().log("Record: " + record);
//				Map<String, AttributeValue> attrs = record.getDynamodb().getNewImage();
//				for(String k: attrs.keySet()) {
//					context.getLogger().log(k + ": " + attrs.get(k).toString());
//				}
				
				if("REMOVE".equals(record.getEventName())){
					
					context.getLogger().log("Creating delete request");
					DeleteRequest deleteRequest = new DeleteRequest("resources", "res", 
							record.getDynamodb().getKeys().get(Resource.USER_ID).getS() + "-" + record.getDynamodb().getKeys().get(Resource.TS).getS());
					DeleteResponse deleteResponse = client.delete(deleteRequest);
					context.getLogger().log("Deleting performed: " + deleteResponse.toString());
				
				} else {
					
					Resource r= mapper.marshallIntoObject(Resource.class, record.getDynamodb().getNewImage());
					Map<String, Object> resourceMap = Resource.buildMap(r);
//					for(String s: resourceMap.keySet()) {
//						context.getLogger().log(s + ": " + resourceMap.get(s));
//					}
					
					context.getLogger().log("Creating index request");
					IndexRequest indexRequest = new IndexRequest("resources", "res", r.getUserId() + "-" + r.getTs())
							.source(resourceMap);
					
					IndexResponse indexResponse = client.index(indexRequest);
					context.getLogger().log("Indexing performed: " + indexResponse.toString());
				}
				
			} catch (Exception e) {
				context.getLogger().log("ERROR: " + e.getMessage());
				e.printStackTrace();
			}
        }
        return event.getRecords().size();
    }
}