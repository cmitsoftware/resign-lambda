package org.resign.backend.domain;

import java.util.List;

public class Resource {

	private String uuid;
	private String userId;
	private Integer type;
	private String ts;
	private String name;
	private List<String> tags;
	
	public Resource() {}
	
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
