package org.resign.backend.domain;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "slug")
public class Slug extends ApiResponse {

	public static final String SLUG = "slug";
	public static final String HASH_KEY= "hashKey";
	public static final String RANGE_KEY= "rangeKey";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String slug;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String hashKey;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String rangeKey;
	
	public Slug() {}
	
	@DynamoDBHashKey
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	@DynamoDBAttribute
	public String getHashKey() {
		return hashKey;
	}

	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}

	@DynamoDBAttribute
	public String getRangeKey() {
		return rangeKey;
	}

	public void setRangeKey(String rangeKey) {
		this.rangeKey = rangeKey;
	}

	public static Slug buildFromMap(Map<String, AttributeValue> sourceMap) {
		Slug t = new Slug();
		t.setSlug(sourceMap.get(Slug.SLUG).getS());
		if(sourceMap.containsKey(Slug.HASH_KEY)) {
			t.setHashKey(sourceMap.get(Slug.HASH_KEY).getS());
		}
		if(sourceMap.containsKey(Slug.RANGE_KEY)) {
			t.setRangeKey(sourceMap.get(Slug.RANGE_KEY).getS());
		}
		return t;
	}
	
}
