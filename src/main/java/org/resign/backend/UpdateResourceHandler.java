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
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	Resource resource = null;
    	ApiGatewayProxyResponse response;
    	try {
    		if(request.getBody() != null) {
				resource = objectMapper.readValue(request.getBody(), Resource.class);
		    	if(!StringUtils.isNullOrEmpty(resource.getUserId()) 
		    			&& !StringUtils.isNullOrEmpty(resource.getTs())) {
		    		try {
		    			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
			    				.withRegion(Regions.EU_WEST_2)
			    				.build();
			    		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
			    		mapper.save(resource);
		    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(resource));
		    			
		    		} catch (Exception e) {
		    			context.getLogger().log("Error: " + e.getMessage());
		    			resource = new Resource();
		    		    resource.setError("An error occurred while creating the resource");
		    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(resource));
		    		}
		    		
		    	} else {
		    		resource = new Resource();
		    		resource.setError("Missing input parameters");
		    		 response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(resource));
		    	}
    		} else {
	    		resource = new Resource();
	    		resource.setError("Missing input parameters");
	    		 response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(resource));
	    	}
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
    		resource = new Resource();
		    resource.setError("An error occurred while creating the resource");
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the resource" + "\"}");
    	}
    	return response;
    }
}
