package org.resign.backend.lambda.tag;

import java.util.UUID;

import org.resign.backend.domain.Tag;
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
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateTagHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	Tag tag = null;
    	ApiGatewayProxyResponse response;
    	if(request.getBody() != null) {
    		try {
				tag = objectMapper.readValue(request.getBody(), Tag.class);
				tag.setUuid(UUID.randomUUID().toString());
			} catch (Exception e) {
				context.getLogger().log("Error: " + e.getMessage());
			}
    	}
    	
    	try {
	    	if(tag != null) {
	    		try {
	    			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
	    					.withRegion(Regions.EU_WEST_3)
	    					.build();
	    			DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withTableNameOverride(TableNameOverride.withTableNamePrefix("dev-")).build();
	    			DynamoDBMapper mapper = new DynamoDBMapper(ddb, config);
	    			mapper.save(tag);
	    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(tag));
	    			
	    		} catch (Exception e) {
	    			context.getLogger().log("Error: " + e.getMessage());
	    			tag = new Tag();
	    		    tag.setError("An error occurred while creating the tag");
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
		    tag.setError("An error occurred while creating the tag");
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the tag" + "\"}");
    	}
		return response;
	}

}
