package org.resign.backend.lambda.blog;

import java.util.ArrayList;

import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;
import org.resign.backend.lambda.ResignHandler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdatePostHandler extends ResignHandler { 

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
				post = objectMapper.readValue(request.getBody(), Post.class);
	    	}
    		if(post == null) {
	    		response = new ApiGatewayProxyResponse(406, null, "{\"error\":\"" 
			    		+ "Missing input parameters" + "\"}");
	    	}
    		if(StringUtils.isNullOrEmpty(post.getUserId()) || StringUtils.isNullOrEmpty(post.getTs())) {
    			response = new ApiGatewayProxyResponse(406, null, "{\"error\":\"" 
			    		+ "Missing input parameters" + "\"}");
	    	}
    		/*
    		 * TODO
    		 * Delete no more used images from s3 
    		 */
    		mapper.save(post);

    		if(post.getImages() == null) {
				post.setImages(new ArrayList<String>());
			}
			if(post.getNewImages() != null) {
				context.getLogger().log("Will upload " + post.getNewImages().size() + " new images");
				for(String newImage: post.getNewImages()) {
					try {
						String imageS3Key = uploadImageToS3(post, newImage);
						post.getImages().add(imageS3Key);
					} catch (Exception e) {
						e.printStackTrace();
						context.getLogger().log("Error: " + e.getMessage());
					}
				}
				post.setNewImages(new ArrayList<String>());
			}
			
			mapper.save(post);
    		response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(post));
    		
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while updating the post" + "\"}");
    	}
    	return response;
    }
}
