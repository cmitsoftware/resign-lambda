package org.resign.backend;

import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdatePostHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	Post post = null;
    	ApiGatewayProxyResponse response;
    	try {
    		if(request.getBody() != null) {
				post = objectMapper.readValue(request.getBody(), Post.class);
		    	if(!StringUtils.isNullOrEmpty(post.getUserId()) && !StringUtils.isNullOrEmpty(post.getTs())) {
		    		try {
		    			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
			    				.withRegion(Regions.EU_WEST_3)
			    				.build();
			    		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
			    		mapper.save(post);
		    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(post));
		    			
		    		} catch (Exception e) {
		    			context.getLogger().log("Error: " + e.getMessage());
		    			post = new Post();
		    		    post.setError("An error occurred while updating the post");
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
		    		+ "An error occurred while updating the tag" + "\"}");
    	}
    	return response;
    }
}
