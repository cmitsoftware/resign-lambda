package org.resign.backend.lambda;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.resign.backend.Constants;
import org.resign.backend.domain.Post;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

public abstract class ResignHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

	protected Context context = null;
	protected String env = null;
	
	protected String uploadImageToS3(Post post, String base64Image) {
		
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
