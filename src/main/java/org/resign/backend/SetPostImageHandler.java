package org.resign.backend;

import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class SetPostImageHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		Post post = null;
		ApiGatewayProxyResponse response = null;
		
		try {
			context.getLogger().log("Request: " + request.getBody());
		} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the post" + "\"}");
    	}
		return response;
    }

}
