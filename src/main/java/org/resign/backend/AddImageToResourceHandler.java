package org.resign.backend;

import java.util.Date;

import org.resign.backend.domain.Resource;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddImageToResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		ObjectMapper objectMapper = new ObjectMapper();

    	String ts = Constants.ddbFullDateFormat.format(new Date());
    	Resource resource = null;
    	ApiGatewayProxyResponse response = null;
    	if(request.getBody() != null) {
    		try {
				resource = objectMapper.readValue(request.getBody(), Resource.class);
				resource.setTs(ts);
			} catch (Exception e) {
				context.getLogger().log("Error: " + e.getMessage());
			}
    	}
    	
    	return response;
	}
}
