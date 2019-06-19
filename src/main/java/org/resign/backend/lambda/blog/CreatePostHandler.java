package org.resign.backend.lambda.blog;

import java.util.ArrayList;
import java.util.Date;

import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;
import org.resign.backend.lambda.ResignHandler;
import org.resign.backend.util.CognitoUtil;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatePostHandler extends ResignHandler { 
	
	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		init(request, context);
		
		context.getLogger().log("Request: " + request.toString());
		Post post = null;
		ApiGatewayProxyResponse response;
		
		try {
			
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
	    	if(request.getBody() != null) {
	    		try {
					post = objectMapper.readValue(request.getBody(), Post.class);
					context.getLogger().log("Post: " + post.toString());
				} catch (Exception e) {
					context.getLogger().log("Error: " + e.getMessage());
				}
	    	}
	    	if(post == null) {
	    		response = new ApiGatewayProxyResponse(406, null, "{\"error\":\"" 
			    		+ "Missing input parameters" + "\"}");
	    	}
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

    			if(post.getImages() == null) {
    				post.setImages(new ArrayList<String>());
    			}
    			if(post.getNewImages() != null) {
    				for(String newImage: post.getNewImages()) {
    					try {
    						String imageS3Key = uploadImageToS3(post, newImage);
    						post.getImages().add(imageS3Key);
    					} catch (Exception e) {
    						context.getLogger().log("Error: " + e.getMessage());
    					}
    				}
    				post.setNewImages(new ArrayList<String>());
    			}
    			mapper.save(post);
    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(post));
    			
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    			post = new Post();
    		    post.setError("An error occurred while creating the post");
    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(post));
    		}
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the post" + "\"}");
    	}
		return response;
    }
}
