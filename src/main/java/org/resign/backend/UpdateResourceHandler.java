package org.resign.backend;

import java.util.Date;
import java.util.UUID;

import org.resign.backend.domain.Resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class UpdateResourceHandler implements RequestHandler<Resource, String> {

    @Override
    public String handleRequest(Resource input, Context context) {
    	context.getLogger().log("Input: " + input);

		AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_WEST_2)
				.build();
		try {
			
			ddb.putItem(Constants.DYNAMODB_TABLE_RESOURCE, input.toResourceMap());
		    return "";
		    
		} catch (ResourceNotFoundException e) {
		    System.err.format("Error: The table \"%s\" can't be found.\n", Constants.DYNAMODB_TABLE_RESOURCE);
		    System.err.println("Be sure that it exists and that you've typed its name correctly!");
		    System.exit(1);
		} catch (AmazonServiceException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		}
		return "";
    }
}
