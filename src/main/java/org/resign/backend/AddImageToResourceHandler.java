package org.resign.backend;

import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Base64;

public class AddImageToResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		context.getLogger().log("Request: " + request.toString());
		
		context.getLogger().log("Identity id: " + context.getIdentity().getIdentityId());
		
		String authorizationHeader = request.getHeaders().get("Authorization");
		String tokens[] = authorizationHeader.split(".");
		context.getLogger().log(new String(Base64.decode(tokens[1])));
		
		
//		ObjectMapper objectMapper = new ObjectMapper();
//
//    	String ts = Constants.ddbFullDateFormat.format(new Date());
//    	Resource resource = null;
//    	ApiGatewayProxyResponse response = null;
//    	if(request.getBody() != null) {
//    		try {
//				resource = objectMapper.readValue(request.getBody(), Resource.class);
//				resource.setTs(ts);
//			} catch (Exception e) {
//				context.getLogger().log("Error: " + e.getMessage());
//			}
//    	}
		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, context.getIdentity().getIdentityId());
    	return response;
	}
}
