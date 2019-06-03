package org.resign.backend.lambda.misc;

import org.resign.backend.Constants;
import org.resign.backend.domain.Slug;
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
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrieveSlugHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		context.getLogger().log("Identity id: " + context.getIdentity().getIdentityId());
    	
    	String slug = null;
    	if(request.getQueryStringParameters() != null) {
    		slug = request.getQueryStringParameters().get("slug");
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
		
    	if(!StringUtils.isNullOrEmpty(slug)) {
    		
    		context.getLogger().log("slug: " + slug);
    		
    		Slug ret = null;
    		ret = mapper.load(Slug.class, slug);
    		
    		if(ret == null) {
    			ret = new Slug();
        		ret.setError("Slug not found");
        		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(404, null, reply);
                return response;
    		}
    		
    		try {
    			reply = objectMapper.writeValueAsString(ret);
    		} catch (JsonProcessingException e) {
    			e.printStackTrace();
    		}

    		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
            return response;
    	}
    	
    	ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(406, null, reply);
        return response;
	}

}
