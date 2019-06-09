package org.resign.backend.lambda.blog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;
import org.resign.backend.util.CognitoUtil;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatePostHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	Context context = null;
	String env = null;
	
	@Override
	public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
		
		this.context = context;
		this.env = request.getStageVariables().get(Constants.ENVIRONMENT_STAGE_VARIABLE);
		
		context.getLogger().log("Request: " + request.toString());
		Post post = null;
		ApiGatewayProxyResponse response;
		
		try {
			
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
	    	if(request.getBody() != null) {
	    		try {
					post = objectMapper.readValue(request.getBody(), Post.class);
					context.getLogger().log("Post: " + post.toString());
				} catch (Exception e) {
					context.getLogger().log("Error: " + e.getMessage());
				}
	    	}
	    	if(post == null) {
	    		response = new ApiGatewayProxyResponse(406, null, "{\"error\":\"" 
			    		+ "Missing input parameters" + "\"}");
	    	}
    		try {
    			
    			String checkUserAuth = System.getenv("CHECK_USER_AUTH");
    			context.getLogger().log("Check user auth " + checkUserAuth);
    			
    			if("true".equals(checkUserAuth)) {
    				
    				String sub = CognitoUtil.readSubFromAuthorization(request.getHeaders().get("Authorization"));
    				context.getLogger().log("Sub: " + sub);
    				post.setUserId(sub);
    				
    			} else {
    				/*
    				 * Auth disabled, use the userId passed in the json 
    				 * This works only if security is disabled on the api gateway
    				 */
    			}
    			if(StringUtils.isNullOrEmpty(post.getPs())) {
    				post.setPs(Post.STATUS_DRAFT);
    			}
    			if(StringUtils.isNullOrEmpty(post.getTs())) {
    				post.setTs(Constants.ddbFullDateFormat.format(new Date()));
    			}
    			
    			mapper.save(post);

    			if(post.getImages() == null) {
    				post.setImages(new ArrayList<String>());
    			}
    			if(post.getNewImages() != null) {
    				for(String newImage: post.getNewImages()) {
    					try {
    						post.getImages().add(uploadImageToS3(post, newImage));
    					} catch (Exception e) {
    						context.getLogger().log("Error: " + e.getMessage());
    					}
    				}
    				post.setNewImages(new ArrayList<String>());
    			}
    			mapper.save(post);
    			response = new ApiGatewayProxyResponse(200, null, objectMapper.writeValueAsString(post));
    			
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    			post = new Post();
    		    post.setError("An error occurred while creating the post");
    		    response = new ApiGatewayProxyResponse(500, null, objectMapper.writeValueAsString(post));
    		}
    	} catch (Exception e) {
    		context.getLogger().log("Error: " + e.getMessage());
		    response = new ApiGatewayProxyResponse(500, null, "{\"error\":\"" 
		    		+ "An error occurred while creating the post" + "\"}");
    	}
		return response;
    }

	private String uploadImageToS3(Post post, String base64Image) {
		
		String mimeType = extractMimeTypeFromBase64Image(base64Image);
		context.getLogger().log("Mime: " + mimeType);
		
		String imageS3Key = post.getUserId() + "/" + post.getTs() + "/" + UUID.randomUUID().toString();
		if(Constants.BETA.equals(env)) {
			imageS3Key = Constants.DEV_BUCKET_PREFIX + "/" + imageS3Key;
		} else {
			imageS3Key = Constants.PROD_BUCKET_PREFIX + "/" + imageS3Key;
		}
		context.getLogger().log("S3 key: " + imageS3Key);
		
		
		byte[] bas64ImageBytes = Base64.decodeBase64((base64Image.substring(base64Image.indexOf(",")+1)).getBytes());

		InputStream fis = new ByteArrayInputStream(bas64ImageBytes);

		String clientRegion = System.getenv("S3_BUCKET_REGION");
		context.getLogger().log("Bucket region: " + clientRegion);
		String bucketName = System.getenv("S3_BUCKET_NAME");
		context.getLogger().log("Bucket name: " + bucketName);
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(bas64ImageBytes.length);
		metadata.setContentType(mimeType);
		metadata.setCacheControl("public, max-age=31536000");
		s3Client.putObject(bucketName, imageS3Key, fis, metadata);
		s3Client.setObjectAcl(bucketName, imageS3Key, CannedAccessControlList.PublicRead);
		
		return imageS3Key;
	}

	private String extractMimeTypeFromBase64Image(String base64Image) {
		
		try {
			String base64Payload = base64Image.substring(0, base64Image.indexOf(",")+1);
			String mimeType = base64Payload.substring(0, base64Payload.indexOf(";")).substring(5);
			return mimeType;
		} catch (Exception e) {
			context.getLogger().log("Can't get mime type: " + e.getMessage());			
		}
		return "image/png";
	}
}
