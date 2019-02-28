package org.resign.backend.lambda.tag;

import org.resign.backend.domain.Tag;
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

public class UpdateTagHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
    	
    	context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	Tag tag = null;
    	ApiGatewayProxyResponse response;
    	try {
    		if(request.getBody() != null) {
				tag = objectMapper.readValue(request.getBody(), Tag.class);
		    	if(!StringUtils.isNullOrEmpty(tag.getUuid())) {
		    		try {
		    			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
			    				.withRegion(Regions.EU_WEST_3)
			    				.build();
			    		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
			    		mapper.save(tag);
		    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(tag));
		    			
		    		} catch (Exception e) {
		    			context.getLogger().log("Error: " + e.getMessage());
		    			tag = new Tag();
		    		    tag.setError("An error occurred while updating the tag");
		    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(tag));
		    		}
		    		
		    	} else {
		    		tag = new Tag();
		    		tag.setError("Missing input parameters");
	    			response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(tag));
		    	}
    		} else {
	    		tag = new Tag();
	    		tag.setError("Missing input parameters");
	    		 response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(tag));
	    	}
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
    		tag = new Tag();
		    tag.setError("An error occurred while updating the tag");
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while updating the tag" + "\"}");
    	}
    	return response;
    }
}
