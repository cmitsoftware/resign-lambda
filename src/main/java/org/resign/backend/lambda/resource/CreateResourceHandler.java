package org.resign.backend.lambda.resource;

import java.util.Date;

import org.json.JSONObject;
import org.resign.backend.Constants;
import org.resign.backend.domain.Resource;
import org.resign.backend.domain.User;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;
import org.resign.backend.util.CognitoUtil;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
//		final Map<String, Object> requestContext = (Map<String, Object>) request.getRequestContext();
//	    final Map<String, Object> identity = (Map<String, Object>) requestContext.get("identity");
//	    final String userId = (String) identity.get("cognitoIdentityId");
		
		context.getLogger().log("Request: " + request.toString());
		Resource resource = null;
		ApiGatewayProxyResponse response;
		
		try {
			
			AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
					.withRegion(Regions.EU_WEST_3)
					.build();
			DynamoDBMapper mapper = new DynamoDBMapper(ddb);
			
			ObjectMapper objectMapper = new ObjectMapper();
	    	String ts = Constants.ddbFullDateFormat.format(new Date());
	    	if(request.getBody() != null) {
	    		try {
					resource = objectMapper.readValue(request.getBody(), Resource.class);
					resource.setTs(ts);
				} catch (Exception e) {
					context.getLogger().log("Error: " + e.getMessage());
				}
	    	}
	    	
	    	String checkUserAuth = System.getenv("CHECK_USER_AUTH");
			context.getLogger().log("Check user auth " + checkUserAuth);
			if("true".equals(checkUserAuth)) {
				try {
	
//					String authorizationHeader = request.getHeaders().get("Authorization");
//					String payload = new String(Base64.decode(authorizationHeader.split("\\.")[1]), "UTF-8");
//					context.getLogger().log("Payload: " + payload);
//					JSONObject json = new JSONObject(payload);
//					String sub = json.getString("sub");
					
					String sub = CognitoUtil.readSubFromAuthorization(request.getHeaders().get("Authorization"));
					context.getLogger().log("Sub: " + sub);
					
					/*
					 * Check if user exists, in case it doesn't create it
					 * Something is probably gone wrong in the cognito trigger 
					 */
		    		User user = mapper.load(User.class, sub);
		    		
		    		if(user == null) {
		    			
		    			context.getLogger().log("User " + sub + " not found. Will be created");
		    			
		    			user = new User();
		    			user.setUuid(sub);
		    			user.setTs(ts);
		    			mapper.save(user);
		    			
		    		}
					resource.setUserId(sub);
					
				} catch (Exception e) {
					e.printStackTrace();
					return new ApiGatewayProxyResponse(500, null, "An error occurred getting user id");
				}
				
			} else {
				
				/*
				 * Auth disabled, use the userId passed in the json 
				 * This works only if security is disabled on the api gateway
				 */
			}
    	
	    	if(resource != null) {
	    		try {
	    			
	    			
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
