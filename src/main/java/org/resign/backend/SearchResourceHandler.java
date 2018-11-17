package org.resign.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.resign.backend.domain.Resource;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {

    	//search: search token
    	//type: type of resource
    	//s: start index
    	//l: length
    	//o: order (default ts)
    	//d: order direction (default desc)
    	
    	context.getLogger().log("Request: " + request.toString());
    	
    	String search = null;
    	Integer type = null;
    	String address = null;
    	Integer start = null;
    	Integer length = null;
    	String order = null;
    	String dir = null;
    	if(request.getQueryStringParameters() != null) {
    		
    		search = request.getQueryStringParameters().get("search");
    		try {
    			String typeS = request.getQueryStringParameters().get("type");
    			if(!StringUtils.isNullOrEmpty(typeS)) {
    				type = Integer.parseInt(typeS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		try {
    			String startS = request.getQueryStringParameters().get("s");
    			if(!StringUtils.isNullOrEmpty(startS)) {
    				start = Integer.parseInt(startS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		try {
    			String lengthS = request.getQueryStringParameters().get("l");
    			if(!StringUtils.isNullOrEmpty(lengthS)) {
    				length = Integer.parseInt(lengthS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		order = request.getQueryStringParameters().get("o");
    		dir = request.getQueryStringParameters().get("d");
    	}
    	
    	AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
    			.withRegion(Regions.EU_WEST_2)
    			.build();
    	DynamoDBMapper mapper = new DynamoDBMapper(ddb);
    	
    	Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    	eav.put(":status", new AttributeValue().withN(String.valueOf(Resource.STATUS_CONFIRMED)));
    	if(!StringUtils.isNullOrEmpty(search)) {
    		eav.put(":name", new AttributeValue().withS(search));
    		eav.put(":surname", new AttributeValue().withS(search));
    		eav.put(":desc", new AttributeValue().withS(search));
    	}
    	
    	DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    	String filter = "resStatus = :status"; 
    	if(!StringUtils.isNullOrEmpty(search)) {
    		filter += " and (contains(name, :name) or contains(surname, :surname)) or contains(desc, :desc))";
    	}
    	scanExpression.withFilterExpression(filter.trim()).withExpressionAttributeValues(eav);
        List<Resource> searchResult = mapper.scan(Resource.class, scanExpression);

        ObjectMapper objectMapper = new ObjectMapper();
    	String reply = null;
		try {
			reply = objectMapper.writeValueAsString(searchResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
    }

}
