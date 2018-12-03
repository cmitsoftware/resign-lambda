package org.resign.backend.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonInclude;

@DynamoDBDocument
public class Location {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String country;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String administrative_area_1;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String administrative_area_2;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double lat;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double lng;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAdministrative_area_1() {
		return administrative_area_1;
	}
	public void setAdministrative_area_1(String administrative_area_1) {
		this.administrative_area_1 = administrative_area_1;
	}
	public String getAdministrative_area_2() {
		return administrative_area_2;
	}
	public void setAdministrative_area_2(String administrative_area_2) {
		this.administrative_area_2 = administrative_area_2;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
}
