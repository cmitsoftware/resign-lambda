package org.resign.backend;

import java.util.HashMap;
import java.util.Map;

import org.resign.backend.domain.Resource;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RetrieveResourceHandler implements RequestHandler<Resource, Resource> {

	@Override
	public Resource handleRequest(Resource input, Context context) {

		context.getLogger().log("Input: " + input);
		Resource ret = null;

		try {

			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.EU_WEST_2)
					.build();
			HashMap<String,AttributeValue> fetchKey = new HashMap<String,AttributeValue>();

			fetchKey.put("userId", new AttributeValue(input.getUserId()));
			fetchKey.put("ts", new AttributeValue(input.getTs()));

			GetItemRequest getItemRequest = new GetItemRequest();
			getItemRequest.withKey(fetchKey);
			getItemRequest.withTableName(Constants.DYNAMODB_TABLE_RESOURCE);
			Map<String,AttributeValue> returnedItem = ddb.getItem(getItemRequest).getItem();
			if (returnedItem != null) {
				ret = Resource.buildFromMap(returnedItem);
				context.getLogger().log("Resource found: " + ret.toString());
			} else {
				context.getLogger().log("No item found with the key " + input.getUserId() + "-" + input.getTs());
			}
		} catch(Exception e) {
			context.getLogger().log(e.getMessage());
		}
		return ret;
	}

}
