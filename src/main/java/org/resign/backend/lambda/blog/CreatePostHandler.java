package org.resign.backend.lambda.blog;

import java.util.Date;
import java.util.UUID;

import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;
import org.resign.backend.util.CognitoUtil;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatePostHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		Post post = null;
		ApiGatewayProxyResponse response;
		
		try {
			
			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.EU_WEST_3)
					.build();
			DynamoDBMapper mapper = new DynamoDBMapper(ddb);
			
			ObjectMapper objectMapper = new ObjectMapper();
	    	if(request.getBody() != null) {
	    		try {
					post = objectMapper.readValue(request.getBody(), Post.class);
				} catch (Exception e) {
					context.getLogger().log("Error: " + e.getMessage());
				}
	    	}
	    	
	    	if(post != null) {
	    		try {
	    			
	    			String checkUserAuth = System.getenv("CHECK_USER_AUTH");
	    			context.getLogger().log("Check user auth " + checkUserAuth);
	    			
	    			if("true".equals(checkUserAuth)) {
	    				
	    				String sub = CognitoUtil.readSubFromAuthorization(request.getHeaders().get("Authorization"));
	    				context.getLogger().log("Sub: " + sub);
	    				post.setUserId(sub);
	    				
	    			} else {
	    				/*
	    				 * Auth disabled, use the userId passed in the json 
	    				 * This works only if security is disabled on the api gateway
	    				 */
	    			}
	    			if(StringUtils.isNullOrEmpty(post.getPs())) {
	    				post.setPs(Post.STATUS_DRAFT);
	    			}
	    			if(StringUtils.isNullOrEmpty(post.getTs())) {
	    				post.setTs(Constants.ddbFullDateFormat.format(new Date()));
	    			}
	    			mapper.save(post);
	    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(post));
	    			
	    		} catch (Exception e) {
	    			context.getLogger().log("Error: " + e.getMessage());
	    			post = new Post();
	    		    post.setError("An error occurred while creating the post");
	    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(post));
	    		}
	    	} else {
	    		post = new Post();
			    post.setError("Missing input parameters");
			    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(post));
	    	}
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
//    		post = new Post();
//		    post.setError("An error occurred while creating the post");
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the post" + "\"}");
    	}
		return response;
    }

}
