package org.resign.backend.domain;

import java.util.Map;

import org.resign.backend.Constants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "post-category")
public class PostCategory extends ApiResponse {

	public static final String UUID = "uuid";
	public static final String NAME = "name";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String uuid;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	
	public PostCategory() {}
	
	@DynamoDBHashKey
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@DynamoDBAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public static PostCategory buildFromMap(Map<String, AttributeValue> sourceMap) {
		PostCategory t = new PostCategory();
		t.setUuid(sourceMap.get(PostCategory.UUID).getS());
		if(sourceMap.containsKey(PostCategory.NAME)) {
			t.setName(sourceMap.get(PostCategory.NAME).getS());
		}
		return t;
	}
	
}
