package org.resign.backend;

import java.text.SimpleDateFormat;

public class Constants {

	public static String BETA = "beta";
	public static String DEV_TABLE_PREFIX = "dev-";
	public static String ENVIRONMENT_STAGE_VARIABLE = "environment"; 
	
	public static String DYNAMODB_TABLE_RESOURCE = "resource";
	public static String DYNAMODB_TABLE_TAG = "tag";
	public static SimpleDateFormat ddbFullDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	public static SimpleDateFormat esFullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
}
