package org.resign.backend;

import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Base64;

public class TestVersioningHandler  implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		String ret = "cerri at work - beta update 3";
		context.getLogger().log(ret);
		
		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, ret);
    	return response;
	}

}
