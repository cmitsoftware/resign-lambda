package org.resign.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.resign.backend.domain.Resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CreateResourceHandler implements RequestHandler<Resource, String> {

	private String DYNAMODB_TABLE_RESOURCE = "resource";

	@Override
	public String handleRequest(Resource input, Context context) {
		
		context.getLogger().log("Input: " + input);

		String uuid = UUID.randomUUID().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		
		AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_WEST_2)
				.build();
		try {
			
		    HashMap<String, AttributeValue> resourceMap = new HashMap<String,AttributeValue>();
			
		    resourceMap.put("userId", new AttributeValue(input.getUserId()));
		    resourceMap.put("ts", new AttributeValue(sdf.format(new Date())));
			resourceMap.put("uuid", new AttributeValue(uuid));
			resourceMap.put("name", new AttributeValue(input.getName()));
			resourceMap.put("type", new AttributeValue(String.valueOf(input.getType())));
			resourceMap.put("tags", new AttributeValue(input.getTags()));

			ddb.putItem(DYNAMODB_TABLE_RESOURCE, resourceMap);
		    return uuid;
		    
		} catch (ResourceNotFoundException e) {
		    System.err.format("Error: The table \"%s\" can't be found.\n", DYNAMODB_TABLE_RESOURCE);
		    System.err.println("Be sure that it exists and that you've typed its name correctly!");
		    System.exit(1);
		} catch (AmazonServiceException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		}
		return "";
	}

}
