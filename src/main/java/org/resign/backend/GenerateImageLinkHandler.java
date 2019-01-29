	package org.resign.backend;
	
	import java.net.URL;
import java.util.Date;
	
	import org.resign.backend.domain.Resource;
	import org.resign.backend.gateway.ApiGatewayProxyResponse;
	import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
	import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
	
	public class GenerateImageLinkHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {
	
		@Override
		public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
	
			context.getLogger().log("Request: " + request.toString());
	
			String clientRegion = "eu-west-3";
			String bucketName = "resign-test-resource-images";
			String objectKey = request.getQueryStringParameters().get("key");
			context.getLogger().log("Generating presigned url for " + objectKey);
			
			try {
				AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(clientRegion)
//	                    .withCredentials(new ProfileCredentialsProvider())
	                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
	                    .build();
				
				// Set the presigned URL to expire after one hour.
				java.util.Date expiration = new java.util.Date();
				long expTimeMillis = expiration.getTime();
				expTimeMillis += 1000 * 60 * 10;
				expiration.setTime(expTimeMillis);
		
				// Generate the presigned URL.
				GeneratePresignedUrlRequest generatePresignedUrlRequest = 
						new GeneratePresignedUrlRequest(bucketName, objectKey)
//						.withContentType("multipart/form-data")
						.withContentType("application/pdf")
						.withMethod(HttpMethod.PUT)
						.withExpiration(expiration);
				URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		
				context.getLogger().log("Pre-Signed URL for put pdf doc: " + url.toString());
		
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
