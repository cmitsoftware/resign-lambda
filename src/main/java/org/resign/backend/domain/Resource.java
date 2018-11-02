package org.resign.backend.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.resign.backend.Constants;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class Resource {

	private String uuid;
	private String userId;
	private Integer type;
	private String ts;
	private String name;
	private List<String> tags;
	
	public Resource() {}

	public HashMap<String, AttributeValue> toResourceMap() {
		
		HashMap<String, AttributeValue> resourceMap = new HashMap<String,AttributeValue>();
		
	    resourceMap.put("userId", new AttributeValue(this.getUserId()));
	    resourceMap.put("ts", new AttributeValue(this.getTs()));
		resourceMap.put("name", new AttributeValue(this.getName()));
		resourceMap.put("type", new AttributeValue(String.valueOf(this.getType())));
		resourceMap.put("tags", new AttributeValue(this.getTags()));
		
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
			r.setTags(tags);
		}
		return r;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Resource [uuid=" + uuid + ", userId=" + userId + ", type=" + type + ", ts=" + ts + ", name=" + name
				+ ", tags=" + tags + "]";
	}
	
}
