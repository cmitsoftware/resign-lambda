package org.resign.backend.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.resign.backend.Constants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBTable(tableName = "resource")
public class Resource extends ApiResponse{
	
	public static final String USER_ID = "userId";
	public static final String TS = "ts";
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String SURNAME = "surname";
	public static final String DESC = "desc";
	public static final String RES_STATUS = "resStatus";
	public static final String LOCATION = "location";
	public static final String COUNTRY = "country";
	public static final String ADMINISTRATIVE_AREA_1 = "administrative_area_1";
	public static final String ADMINISTRATIVE_AREA_2 = "administrative_area_2";
	public static final String GEO_LOCATION = "geoLocation";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String VISIBLE_FROM = "visibleFrom";
	public static final String VISIBLE_TO = "visibleTo";
	public static final String ACTIVATION = "activation";
	public static final String TAGS = "tags";
	public static final String TAG_UUID= "uuid";
	public static final String TAG_NAME = "name";
	public static final String IMAGES = "images";
	public static final String IMAGE_URL = "url";
	public static final String IMAGE_DESC = "desc";
	
	public static final Integer STATUS_DRAFT = 1;
	public static final Integer STATUS_CONFIRMED = 2;
	public static final Integer STATUS_REJECTED = 3;
	public static final Integer STATUS_DELETED = 4;

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
	private Integer resStatus;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String desc;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Location location;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String visibleFrom;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String visibleTo;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String activation;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Tag> tags;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Image> images;
	
	public Resource() {}
	
	@SuppressWarnings("unchecked")
	public static Resource buildFromMap(Map<String, Object> sourceMap) throws Exception {
		
		Resource r = new Resource();
		r.setUserId((String)sourceMap.get(Resource.USER_ID));
		r.setTs(Constants.ddbFullDateFormat.format(Constants.esFullDateFormat.parse((String)sourceMap.get(Resource.TS))));
		if(sourceMap.containsKey(NAME)) {
			r.setName((String)sourceMap.get(NAME));
		}
		if(sourceMap.containsKey(SURNAME)) {
			r.setSurname((String)sourceMap.get(SURNAME));
		}
		if(sourceMap.containsKey(DESC)) {
			r.setDesc((String)sourceMap.get(DESC));
		}
		if(sourceMap.containsKey(TYPE)) {
			r.setType((Integer)sourceMap.get(TYPE));
		}
		Location location = new Location();
		if(sourceMap.containsKey(LOCATION)) {
			HashMap<String, Object> locationMap = (HashMap<String, Object>)sourceMap.get(LOCATION);
			if(locationMap.containsKey(COUNTRY)) {
				location.setCountry((String)locationMap.get(COUNTRY));
			}
			if(locationMap.containsKey(ADMINISTRATIVE_AREA_1)) {
				location.setAdministrative_area_1((String)locationMap.get(ADMINISTRATIVE_AREA_1));
			}
			if(locationMap.containsKey(ADMINISTRATIVE_AREA_2)) {
				location.setAdministrative_area_2((String)locationMap.get(ADMINISTRATIVE_AREA_2));
			}
		}
		if(sourceMap.containsKey(GEO_LOCATION)) {
			HashMap<String, Object> geoLocationMap = (HashMap<String, Object>)sourceMap.get(GEO_LOCATION);
			if(geoLocationMap.containsKey(LAT) && geoLocationMap.containsKey(LON)) {
				location.setLat((Double)geoLocationMap.get(LAT));
				location.setLng((Double)geoLocationMap.get(LON));
			}
		}
		r.setLocation(location);
		
		List<Tag> tags = new ArrayList<Tag>();
		if(sourceMap.containsKey(TAGS)) {
			ArrayList<HashMap<String, Object>> tagList = (ArrayList<HashMap<String, Object>>)sourceMap.get(TAGS);
			for(HashMap<String, Object> t: tagList) {
				if(t.containsKey(TAG_UUID) && t.containsKey(TAG_NAME)) {
					Tag tag = new Tag();
					tag.setUuid((String)t.get(TAG_UUID));
					tag.setName((String)t.get(TAG_NAME));
					tags.add(tag);
				}
			}
		}
		r.setTags(tags);
		
		return r;
	}

//	public HashMap<String, AttributeValue> toResourceMap() {
//		
//		HashMap<String, AttributeValue> resourceMap = new HashMap<String,AttributeValue>();
//		
//	    resourceMap.put("userId", new AttributeValue(this.getUserId()));
//	    resourceMap.put("ts", new AttributeValue(this.getTs()));
//		resourceMap.put("name", new AttributeValue(this.getName()));
//		resourceMap.put("type", new AttributeValue(String.valueOf(this.getType())));
////		resourceMap.put("tags", new AttributeValue(this.getTags()));
//		
//		return resourceMap;
//	}
	
//	public static Resource buildFromMap(Map<String,AttributeValue> map) {
//		
//		Resource r = new Resource();
//		r.setUserId(map.get("userId").getS());
//		r.setTs(map.get("ts").getS());
//		if(map.containsKey("name")) {
//			r.setName(map.get("name").getS());
//		}
//		if(map.containsKey("type")) {
//			r.setType(Integer.parseInt(map.get("type").getS()));
//		}
//		if(map.containsKey("tags")) {
//			List<String> tags = new ArrayList<String>(); 
//			AttributeValue tagListAttribute = map.get("tags");
//			for(String av: tagListAttribute.getSS()) {
//				tags.add(av);
//			}
////			r.setTags(tags);
//		}
//		return r;
//	}

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
	public Integer getResStatus() {
		return resStatus;
	}

	public void setResStatus(Integer resStatus) {
		this.resStatus = resStatus;
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
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@DynamoDBAttribute
	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "Resource [userId=" + userId + ", type=" + type + ", ts=" + ts + ", name=" + name
				+ ", tags=" + tags + "]";
	}
	
}
