package org.resign.backend.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "user")
public class User extends ApiResponse {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String userId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ts;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String surname;
	
	@DynamoDBHashKey
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBAttribute
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@DynamoDBAttribute
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	
	@DynamoDBAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
}
