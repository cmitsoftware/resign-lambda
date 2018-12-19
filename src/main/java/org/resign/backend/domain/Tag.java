package org.resign.backend.domain;

import java.util.Map;

import org.resign.backend.Constants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "tag")
public class Tag extends ApiResponse {

	public static final String UUID = "uuid";
	public static final String NAME = "name";
	public static final String CATEGORY = "category";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String uuid;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String category;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer tagStatus;
	
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
	@DynamoDBAttribute
	public Integer getTagStatus() {
		return tagStatus;
	}
	public void setTagStatus(Integer tagStatus) {
		this.tagStatus = tagStatus;
	}

	@Override
	public String toString() {
		return "Tag [uuid=" + uuid + ", category=" + category + ", name=" + name + "]";
	}

	public static Tag buildFromMap(Map<String, AttributeValue> sourceMap) {
		Tag t = new Tag();
		t.setUuid(sourceMap.get(Tag.UUID).getS());
		if(sourceMap.containsKey(Tag.NAME)) {
			t.setName(sourceMap.get(Tag.NAME).getS());
		}
		if(sourceMap.containsKey(Tag.CATEGORY)) {
			t.setCategory(sourceMap.get(Tag.CATEGORY).getS());
		}
		return t;
	}
	
}
