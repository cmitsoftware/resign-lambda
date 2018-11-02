package org.resign.backend.domain;

public class Tag {

	private String uuid;
	private String category;
	private String name;
	
	public Tag() {}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
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
