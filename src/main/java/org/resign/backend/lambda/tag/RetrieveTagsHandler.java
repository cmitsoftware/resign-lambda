package org.resign.backend.lambda.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.resign.backend.domain.ApiResponse;
import org.resign.backend.domain.Tag;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrieveTagsHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		ApiGatewayProxyResponse response= null;
		try {
			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.EU_WEST_3)
					.build();
			ScanRequest scanRequest = new ScanRequest()
				    .withTableName("tag");

			ScanResult result = ddb.scan(scanRequest);
			List<Tag> tags = new ArrayList<Tag>();
			for (Map<String, AttributeValue> item : result.getItems()){
				tags.add(Tag.buildFromMap(item));
			}
				
			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(tags));
			
		} catch (Exception e) {
			context.getLogger().log("Error: " + e.getMessage());
			ApiResponse errorResponse = new ApiResponse();
			errorResponse.setError("Error loading tags");
		    try {
				response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(errorResponse));
			} catch (JsonProcessingException e1) {
			}
		}
		return response;
	}

}
