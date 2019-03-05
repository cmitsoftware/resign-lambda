package org.resign.backend.lambda.blog;

import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeletePostHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	Post post = null;
    	ApiGatewayProxyResponse response;
    	try {
    		if(request.getQueryStringParameters() != null) {
//				post = objectMapper.readValue(request.getBody(), Post.class);
    			String userId = request.getQueryStringParameters().get("userId");
    			String ts = request.getQueryStringParameters().get("ts");
		    	if(!StringUtils.isNullOrEmpty(userId) && !StringUtils.isNullOrEmpty(ts)) {
		    		try {
		    			
		    			String tablePrefix = "";
		    			String env = request.getStageVariables().get(Constants.ENVIRONMENT_STAGE_VARIABLE);
		    			if(Constants.BETA.equals(env)) {
		    				tablePrefix = Constants.DEV_TABLE_PREFIX;
		    			}
		    			DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withTableNameOverride(TableNameOverride.withTableNamePrefix(tablePrefix)).build();
		    			
		    			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
			    				.withRegion(Regions.EU_WEST_3)
			    				.build();
			    		DynamoDBMapper mapper = new DynamoDBMapper(ddb, config);
			    		post = mapper.load(Post.class, userId, ts);
			    		mapper.delete(post);
		    			response = new ApiGatewayProxyResponse(200, null, null);
		    			
		    		} catch (Exception e) {
		    			context.getLogger().log("Error: " + e.getMessage());
		    			post = new Post();
		    		    post.setError("An error occurred while deleting the post");
		    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(post));
		    		}
		    		
		    	} else {
		    		post = new Post();
		    		post.setError("Missing input parameters");
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
//		    post.setError("An error occurred while updating the tag");
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while deleting the tag" + "\"}");
    	}
    	return response;
    }

}
