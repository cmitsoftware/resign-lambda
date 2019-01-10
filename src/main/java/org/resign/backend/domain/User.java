package org.resign.backend.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "resource")
public class User extends ApiResponse {
	
	private String userId;
	private String email;
	private String ts;
	private String name;
	private String surname;

}
