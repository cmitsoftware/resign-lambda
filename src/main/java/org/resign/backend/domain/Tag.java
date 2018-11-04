package org.resign.backend.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "tag")
public class Tag extends ApiResponse {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String uuid;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String category;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	
	public Tag() {}
	
	@DynamoDBHashKey
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@DynamoDBAttribute
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@DynamoDBAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Tag [uuid=" + uuid + ", category=" + category + ", name=" + name + "]";
	}
	
}
