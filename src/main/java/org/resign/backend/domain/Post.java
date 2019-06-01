package org.resign.backend.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.resign.backend.Constants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "post")
public class Post extends ApiResponse {
	
	public static final String STATUS_PUBLISHED = "p";
	public static final String STATUS_DRAFT = "d";
	
	public static final String USER_ID = "userId";
	public static final String TS = "ts";
	public static final String PS = "ps";
	public static final String TITLE = "title";
	public static final String TEXT = "text";
	public static final String SLUG = "slug";
	public static final String CATEGORY = "category";
	public static final String IMAGES = "images";
	public static final String VIEWS = "views";
	public static final String LIKES = "likes";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String userId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ts;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String ps;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String title;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String text;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String slug;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String category;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer views;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer likes;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> images;
	
//	@SuppressWarnings("unchecked")
//	public static Post buildFromMap(Map<String, Object> sourceMap) throws Exception {
//		
//		Post p = new Post();
//		p.setUserId((String)sourceMap.get(USER_ID));
//		p.setTs(((String)sourceMap.get(TS)));
//		if(sourceMap.containsKey(PS)) {
//			p.setPs(((String)sourceMap.get(PS)));
//		}
//		if(sourceMap.containsKey(TITLE)) {
//			p.setTitle((String)sourceMap.get(TITLE));
//		}
//		if(sourceMap.containsKey(TEXT)) {
//			p.setText((String)sourceMap.get(TEXT));
//		}
//		if(sourceMap.containsKey(VIEWS)) {
//			p.setViews((Integer)sourceMap.get(VIEWS));
//		}
//		if(sourceMap.containsKey(LIKES)) {
//			p.setLikes((Integer)sourceMap.get(LIKES));
//		}
//		if(sourceMap.containsKey(IMAGE)) {
//			p.setImage((String)sourceMap.get(IMAGE));
//		}
//		
//		return p;
//	}

	public static Post buildFromAttributeMap(Map<String, AttributeValue> sourceMap) throws Exception {
		
		Post p = new Post();
		p.setUserId(sourceMap.get(USER_ID).getS());
		p.setTs((String)sourceMap.get(TS).getS());
		if(sourceMap.containsKey(PS)) {
			p.setPs((sourceMap.get(PS)).getS());
		}
		if(sourceMap.containsKey(TITLE)) {
			p.setTitle((String)sourceMap.get(TITLE).getS());
		}
		if(sourceMap.containsKey(TEXT)) {
			p.setText(sourceMap.get(TEXT).getS());
		}
		if(sourceMap.containsKey(SLUG)) {
			p.setSlug(sourceMap.get(SLUG).getS());
		}
		if(sourceMap.containsKey(CATEGORY)) {
			p.setCategory(sourceMap.get(CATEGORY).getS());
		}
		if(sourceMap.containsKey(VIEWS)) {
			p.setViews(Integer.parseInt(sourceMap.get(VIEWS).getN()));
		}
		if(sourceMap.containsKey(LIKES)) {
			p.setLikes(Integer.parseInt(sourceMap.get(LIKES).getS()));
		}
		if(sourceMap.containsKey(IMAGES)) {
			List<String> images = new ArrayList<String>();
			for(AttributeValue v: sourceMap.get(IMAGES).getL()) {
				images.add(v.getS());
			}
			p.setImages(images);
		}
		
		return p;
	}
	
	@DynamoDBHashKey	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBAttribute
	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	@DynamoDBRangeKey
	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
	
	@DynamoDBAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@DynamoDBAttribute
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@DynamoDBAttribute
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	@DynamoDBAttribute
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@DynamoDBAttribute
	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@DynamoDBAttribute
	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	@DynamoDBAttribute
	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	
}
