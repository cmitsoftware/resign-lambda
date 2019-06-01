package org.resign.backend.lambda.blog;

import java.util.List;

import org.resign.backend.Constants;
import org.resign.backend.domain.PostCategory;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrievePostCategoriesHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		context.getLogger().log("Identity id: " + context.getIdentity().getIdentityId());
    	
    	String uuid = null;
    	if(request.getQueryStringParameters() != null) {
    		uuid = request.getQueryStringParameters().get("uuid");
    	}

    	String tablePrefix = "";
		String env = request.getStageVariables().get(Constants.ENVIRONMENT_STAGE_VARIABLE);
		if(Constants.BETA.equals(env)) {
			tablePrefix = Constants.DEV_TABLE_PREFIX + "-";
		}
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withTableNameOverride(TableNameOverride.withTableNamePrefix(tablePrefix)).build();
		
    	AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_WEST_3)
				.build();
    	DynamoDBMapper mapper = new DynamoDBMapper(ddb, config);
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	String reply = null;
		
    	if(!StringUtils.isNullOrEmpty(uuid)) {
    		
    		context.getLogger().log("uuid: " + uuid);
    		
    		PostCategory ret = null;
    		ret = mapper.load(PostCategory.class, uuid);
    		
    		if(ret == null) {
    			ret = new PostCategory();
        		ret.setError("Post category not found");
    		}
    		
    		try {
    			reply = objectMapper.writeValueAsString(ret);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}
    		
    	} else {
    		
    		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            List<PostCategory> posts = mapper.scan(PostCategory.class, scanExpression);
    		
    		try {
    			reply = objectMapper.writeValueAsString(posts);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
	}

}
