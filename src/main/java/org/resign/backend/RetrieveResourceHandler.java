package org.resign.backend;

import org.resign.backend.domain.Resource;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrieveResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
    	
    	String userId = null;
    	String ts = null;
    	if(request.getQueryStringParameters() != null) {
    		userId = request.getQueryStringParameters().get("userId");
    		ts = request.getQueryStringParameters().get("ts");
    	}

    	Resource ret = null;
    	if(!StringUtils.isNullOrEmpty(userId) && !StringUtils.isNullOrEmpty(ts)) {
    		
    		AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
    				.withRegion(Regions.EU_WEST_2)
    				.build();
    		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
    		ret = mapper.load(Resource.class, userId, ts);
    		if(ret == null) {
    			ret = new Resource();
        		ret.setError("Resource not found");
    		}
    	} else {
    		ret = new Resource();
    		ret.setError("Missing input parameters");
    	}
		ObjectMapper objectMapper = new ObjectMapper();
    	String reply = null;
		try {
			reply = objectMapper.writeValueAsString(ret);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
    }

//	@Override
//	public String handleRequest(Resource input, Context context) {
//
//		context.getLogger().log("Input: " + input);
//		Resource ret = null;
//
//		try {
//
//			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
//					.withRegion(Regions.EU_WEST_2)
//					.build();
//			
////			HashMap<String,AttributeValue> fetchKey = new HashMap<String,AttributeValue>();
////
////			fetchKey.put("userId", new AttributeValue(input.getUserId()));
////			fetchKey.put("ts", new AttributeValue(input.getTs()));
////
////			GetItemRequest getItemRequest = new GetItemRequest();
////			getItemRequest.withKey(fetchKey);
////			getItemRequest.withTableName(Constants.DYNAMODB_TABLE_RESOURCE);
////			Map<String,AttributeValue> returnedItem = ddb.getItem(getItemRequest).getItem();
////			if (returnedItem != null) {
////				ret = Resource.buildFromMap(returnedItem);
////				context.getLogger().log("Resource found: " + ret.toString());
////			} else {
////				context.getLogger().log("No item found with the key " + input.getUserId() + "-" + input.getTs());
////			}
//			DynamoDBMapper mapper = new DynamoDBMapper(ddb);
//			ret = mapper.load(Resource.class, input.getUserId(), input.getTs());
//			
//		} catch(Exception e) {
//			context.getLogger().log(e.getMessage());
//		}
//		
////		return "{'isBase64Encoded':false,'statusCode':200,'headers':null,'body':'returned correctly from lambda'}";
//		return "{'statusCode':201,'headers':{'Access-Control-Allow-Origin': '*',},'body':'returned correctly from lambda'}";
////		{'Access-Control-Allow-Origin': '*',}
//	}

}
