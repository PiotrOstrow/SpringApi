package org.example.cities.model;

public class CityDto {

	private long id;

	private String city;
	private String country;

	public CityDto() {}

	public CityDto(long id, String city, String country) {
		this.id = id;
		this.city = city;
		this.country = country;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
