	package org.resign.backend;
	
	import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
	import org.resign.backend.gateway.ApiGatewayRequest;
	
	import com.amazonaws.AmazonServiceException;
	import com.amazonaws.HttpMethod;
	import com.amazonaws.SdkClientException;
	import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
	import com.amazonaws.services.lambda.runtime.Context;
	import com.amazonaws.services.lambda.runtime.RequestHandler;
	import com.amazonaws.services.s3.AmazonS3;
	import com.amazonaws.services.s3.AmazonS3ClientBuilder;
	import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
//	import com.amazonaws.util.Base64;

	public class GenerateImageLinkHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {
	
		@Override
		public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
	
			context.getLogger().log("Request: " + request.toString());
			
			String clientRegion = "eu-west-3";
			String bucketName = "resign-test-resource-images";
			String bucketFolderKey = request.getQueryStringParameters().get("key");
			String mimeType = request.getQueryStringParameters().get("mimeType");
			context.getLogger().log("Requested presigned url for " + bucketFolderKey);
			
			try {
				AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(clientRegion)
	                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
	                    .build();
			
				String checkUserAuth = System.getenv("CHECK_USER_AUTH");
				context.getLogger().log("Check user auth " + checkUserAuth);
				
				if("true".equals(checkUserAuth)) {
					try {
						
						String authorizationHeader = request.getHeaders().get("Authorization");
						context.getLogger().log("Authorization: " + authorizationHeader);
						String token = authorizationHeader.split("\\.")[1];
						context.getLogger().log("Token: " + token);
						String payload = new String(Base64.getDecoder().decode(token), "UTF-8");
						context.getLogger().log("Payload: " + payload);
						JSONObject json = new JSONObject(payload);
						String sub = json.getString("sub");
						context.getLogger().log("Sub: " + sub);
						JSONArray groups = json.optJSONArray("cognito:groups");
						List<String> roles = new ArrayList<String>();
						if(groups != null) {
							groups.forEach(item -> {
								context.getLogger().log("Group: " + (String)item);
								roles.add((String)item);
							});
						}
						
						String userBucketFolder = bucketFolderKey.split("\\/")[0];
						context.getLogger().log("User bucket folder: " + userBucketFolder);
						if(!userBucketFolder.equals(sub) && !roles.contains(System.getenv("COGNITO_ADMIN_GROUP"))) {
							return new ApiGatewayProxyResponse(403, null, "Not authorized");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						return new ApiGatewayProxyResponse(500, null, "An error occurred validating user permissions");
					}
				}
				
				java.util.Date expiration = new java.util.Date();
				long expTimeMillis = expiration.getTime();
				Integer linkValiditySeconds = Integer.parseInt(System.getenv("LINK_VALIDITY"));
				expTimeMillis += 1000 * linkValiditySeconds;
				expiration.setTime(expTimeMillis);
				
				String imageKey = bucketFolderKey + "/" + UUID.randomUUID().toString();
		
				// Generate the presigned URL.
				GeneratePresignedUrlRequest generatePresignedUrlRequest = 
						new GeneratePresignedUrlRequest(bucketName, imageKey)
						.withContentType(mimeType)
						.withMethod(HttpMethod.PUT)
						.withExpiration(expiration);
				URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		
				context.getLogger().log("Pre-Signed URL : " + url.toString());
		
				ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, url.toString());
				return response;
			}
			catch(AmazonServiceException e) {
				// The call was transmitted successfully, but Amazon S3 couldn't process 
				// it, so it returned an error response.
				e.printStackTrace();
			}
			catch(SdkClientException e) {
				// Amazon S3 couldn't be contacted for a response, or the client
				// couldn't parse the response from Amazon S3.
				e.printStackTrace();
			}
			return new ApiGatewayProxyResponse(500, null, "An error occurred");
		}
	}
