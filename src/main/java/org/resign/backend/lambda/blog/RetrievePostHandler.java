package org.resign.backend.lambda.blog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrievePostHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
		context.getLogger().log("Identity id: " + context.getIdentity().getIdentityId());
    	
    	String userId = null;
    	String ts = null;
    	if(request.getQueryStringParameters() != null) {
    		userId = request.getQueryStringParameters().get("ps");
    		ts = request.getQueryStringParameters().get("ts");
    	}

    	String tablePrefix = "";
		String env = request.getStageVariables().get(Constants.ENVIRONMENT_STAGE_VARIABLE);
		if(Constants.BETA.equals(env)) {
			tablePrefix = Constants.DEV_TABLE_PREFIX + "-";
		}
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withTableNameOverride(TableNameOverride.withTableNamePrefix(tablePrefix)).build();
		
    	AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_WEST_3)
				.build();
    	DynamoDBMapper mapper = new DynamoDBMapper(ddb, config);
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	String reply = null;
    	if(!StringUtils.isNullOrEmpty(userId) && !StringUtils.isNullOrEmpty(ts)) {
    		
    		context.getLogger().log("userId: " + userId);
    		context.getLogger().log("ts: " + ts);
    		
    		Post ret = null;
    		ret = mapper.load(Post.class, userId, ts);
    		
    		if(ret == null) {
    			
    			ret = new Post();
        		ret.setError("Post not found");
        		
    		} else {

    			Integer views = ret.getViews();
    			if(views == null) {
    				views = 0;
    			}
    			views++;
    			ret.setViews(views);
    			mapper.save(ret);
    		}
    		
    		try {
    			reply = objectMapper.writeValueAsString(ret);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}
    		
    	} else if(!StringUtils.isNullOrEmpty(userId)) {
    		
//    		context.getLogger().log("ps: " + ps);
//    		
//            Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
//            expressionAttributeValues.put(":statusValue",new AttributeValue().withS(ps));
//            
//    		DynamoDBQueryExpression<Post> queryExpression = new DynamoDBQueryExpression<Post>()
//    				.withKeyConditionExpression("ps = :statusValue")
//                    .withExpressionAttributeValues(expressionAttributeValues);
//    		queryExpression.setScanIndexForward(false);
//    		
//    		List<Post> posts = mapper.query(Post.class, queryExpression);
//    		
//    		try {
//    			reply = objectMapper.writeValueAsString(posts);
//    		} catch (JsonProcessingException e) {
//    			e.printStackTrace();
//    		}
    		
    	} else {
    		
    		ScanRequest scanRequest = new ScanRequest()
    			    .withTableName("post");
    		ScanResult scanResult = ddb.scan(scanRequest);
    		List<Post> posts = new ArrayList<Post>();
    		for (Map<String, AttributeValue> item : scanResult.getItems()){
    			try {
					posts.add(Post.buildFromAttributeMap(item));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		Collections.reverse(posts);
    		
//    		List<Post> posts = new ArrayList<Post>();
//    		Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
//            expressionAttributeValues.put(":statusPublished",new AttributeValue().withS(Post.STATUS_PUBLISHED));
//    		DynamoDBQueryExpression<Post> queryExpression = new DynamoDBQueryExpression<Post>()
//    				.withKeyConditionExpression("ps = :statusPublished")
//                    .withExpressionAttributeValues(expressionAttributeValues);
//    		posts.addAll(mapper.query(Post.class, queryExpression));
//    		queryExpression.setScanIndexForward(false);
//    		
//    		expressionAttributeValues = new HashMap<>();
//            expressionAttributeValues.put(":statusDraft",new AttributeValue().withS(Post.STATUS_DRAFT));
//    		queryExpression = new DynamoDBQueryExpression<Post>()
//    				.withKeyConditionExpression("ps = :statusDraft")
//                    .withExpressionAttributeValues(expressionAttributeValues);
//    		queryExpression.setScanIndexForward(false);
//    		
//    		posts.addAll(mapper.query(Post.class, queryExpression));
//    		
    		try {
    			reply = objectMapper.writeValueAsString(posts);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}
    	}
		
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
    }
}
