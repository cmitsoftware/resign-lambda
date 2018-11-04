package org.resign.backend.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "resource")
public class Resource extends ApiResponse{

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String userId;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ts;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer type;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String surname;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer status;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String desc;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String visibleFrom;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String visibleTo;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String creation;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String activation;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Tag> tags;
	
	public Resource() {}

	public HashMap<String, AttributeValue> toResourceMap() {
		
		HashMap<String, AttributeValue> resourceMap = new HashMap<String,AttributeValue>();
		
	    resourceMap.put("userId", new AttributeValue(this.getUserId()));
	    resourceMap.put("ts", new AttributeValue(this.getTs()));
		resourceMap.put("name", new AttributeValue(this.getName()));
		resourceMap.put("type", new AttributeValue(String.valueOf(this.getType())));
//		resourceMap.put("tags", new AttributeValue(this.getTags()));
		
		return resourceMap;
	}
	
	public static Resource buildFromMap(Map<String,AttributeValue> map) {
		
		Resource r = new Resource();
		r.setUserId(map.get("userId").getS());
		r.setTs(map.get("ts").getS());
		if(map.containsKey("name")) {
			r.setName(map.get("name").getS());
		}
		if(map.containsKey("type")) {
			r.setType(Integer.parseInt(map.get("type").getS()));
		}
		if(map.containsKey("tags")) {
			List<String> tags = new ArrayList<String>(); 
			AttributeValue tagListAttribute = map.get("tags");
			for(String av: tagListAttribute.getSS()) {
				tags.add(av);
			}
//			r.setTags(tags);
		}
		return r;
	}

	@DynamoDBHashKey
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@DynamoDBRangeKey
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	@DynamoDBAttribute
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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

	@DynamoDBAttribute
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@DynamoDBAttribute
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@DynamoDBAttribute
	public String getVisibleFrom() {
		return visibleFrom;
	}

	public void setVisibleFrom(String visibleFrom) {
		this.visibleFrom = visibleFrom;
	}

	@DynamoDBAttribute
	public String getVisibleTo() {
		return visibleTo;
	}

	public void setVisibleTo(String visibleTo) {
		this.visibleTo = visibleTo;
	}

	@DynamoDBAttribute
	public String getCreation() {
		return creation;
	}

	public void setCreation(String creation) {
		this.creation = creation;
	}

	@DynamoDBAttribute
	public String getActivation() {
		return activation;
	}

	public void setActivation(String activation) {
		this.activation = activation;
	}

	@DynamoDBAttribute
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	@Override
	public String toString() {
		return "Resource [userId=" + userId + ", type=" + type + ", ts=" + ts + ", name=" + name
				+ ", tags=" + tags + "]";
	}
	
}
