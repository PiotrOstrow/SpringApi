package com.example.api.model.weather;

import com.fasterxml.jackson.databind.JsonNode;

public class WeatherData {
	private JsonNode coord;
	private Weather weather[];
	private JsonNode base;
	private JsonNode main, visibility, wind, coulds, dt, sys, timezone, id, name, cod;

	public JsonNode getCoord() {
		return coord;
	}

	public void setCoord(JsonNode coord) {
		this.coord = coord;
	}

	public Weather[] getWeather() {
		return weather;
	}

	public void setWeather(Weather[] weather) {
		this.weather = weather;
	}

	public JsonNode getBase() {
		return base;
	}

	public void setBase(JsonNode base) {
		this.base = base;
	}

	public JsonNode getMain() {
		return main;
	}

	public void setMain(JsonNode main) {
		this.main = main;
	}

	public JsonNode getVisibility() {
		return visibility;
	}

	public void setVisibility(JsonNode visibility) {
		this.visibility = visibility;
	}

	public JsonNode getWind() {
		return wind;
	}

	public void setWind(JsonNode wind) {
		this.wind = wind;
	}

	public JsonNode getCoulds() {
		return coulds;
	}

	public void setCoulds(JsonNode coulds) {
		this.coulds = coulds;
	}

	public JsonNode getDt() {
		return dt;
	}

	public void setDt(JsonNode dt) {
		this.dt = dt;
	}

	public JsonNode getSys() {
		return sys;
	}

	public void setSys(JsonNode sys) {
		this.sys = sys;
	}

	public JsonNode getTimezone() {
		return timezone;
	}

	public void setTimezone(JsonNode timezone) {
		this.timezone = timezone;
	}

	public JsonNode getId() {
		return id;
	}

	public void setId(JsonNode id) {
		this.id = id;
	}

	public JsonNode getName() {
		return name;
	}

	public void setName(JsonNode name) {
		this.name = name;
	}

	public JsonNode getCod() {
		return cod;
	}

	public void setCod(JsonNode cod) {
		this.cod = cod;
	}
}
