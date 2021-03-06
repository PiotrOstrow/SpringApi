package com.example.api.model;

public class City {

	private long id;
	private String city;
	private String country;

	public void setId(long id) {
		this.id = id;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getId() {
		return id;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}
}
