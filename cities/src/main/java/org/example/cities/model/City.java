package org.example.cities.model;

import javax.persistence.*;

@Entity
@Table(name="cities", schema = "dbo")
public class City {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String city;
	private String country;

	public City() {}

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
