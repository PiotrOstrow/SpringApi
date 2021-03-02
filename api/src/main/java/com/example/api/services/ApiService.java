package com.example.api.services;

import com.example.api.model.city.City;
import com.example.api.model.weather.Weather;
import com.example.api.model.weather.WeatherData;
import com.example.api.util.CountryCodeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {

	private final WebClient webClient;
	private final CountryCodeMap countryCodeMap;
	private final ObjectMapper objectMapper;

	public ApiService(WebClient webClient, CountryCodeMap countryCodeMap, ObjectMapper objectMapper) {
		this.webClient = webClient;
		this.countryCodeMap = countryCodeMap;
		this.objectMapper = objectMapper;
	}

	public Object search(String searchTerm) {
		City[] cities = getCities(searchTerm);

		if(cities == null || cities.length == 0)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No city matching \"" + searchTerm + "\" found");

		ArrayNode arrayNode = objectMapper.createArrayNode();
		List<ObjectNode> resultList = new ArrayList<>();

		for(int i = 0; i < Math.min(5, cities.length); ++i) {
			String cityName = cities[i].getCity();
			String countryCode = countryCodeMap.getCountryCode(cities[i].getCountry());

			WeatherData weatherData = getWeatherData(cityName, countryCode);

			ObjectNode element = objectMapper.createObjectNode();
			element.put("city", cityName);
			element.put("country", cities[i].getCountry());
			element.put("country_code", countryCode);

			element.set("coord", weatherData.getCoord());

			element.set("weather", constructWeatherNode(weatherData));
			arrayNode.add(element);
		}

		return objectMapper.createObjectNode().set("result", arrayNode);
	}

	private ObjectNode constructWeatherNode(WeatherData weatherData) {
		Weather weather = weatherData.getWeather()[0];

		ObjectNode weatherNode = objectMapper.createObjectNode();
		weatherNode.put("weather", weather.getMain());
		weatherNode.put("description", weather.getDescription());
		weatherNode.set("temp", weatherData.getMain().get("temp"));
		weatherNode.set("feels_like", weatherData.getMain().get("feels_like"));
		weatherNode.set("temp_min", weatherData.getMain().get("temp_min"));
		weatherNode.set("temp_max", weatherData.getMain().get("temp_max"));
		weatherNode.set("pressure", weatherData.getMain().get("pressure"));
		weatherNode.set("humidity", weatherData.getMain().get("humidity"));
		weatherNode.set("visibility", weatherData.getVisibility());
		weatherNode.set("wind", weatherData.getWind());
		weatherNode.set("sunrise", weatherData.getSys().get("sunrise"));
		weatherNode.set("sunset", weatherData.getSys().get("sunset"));

		return weatherNode;
	}

	private WeatherData getWeatherData(String cityName, String countryCode) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.scheme("http")
						.host("api.openweathermap.org")
						.path("/data/2.5/weather")
						.queryParam("q", "{cityName},{countryCode}")
						.queryParam("units", "metric")
						.queryParam("appid", "1727c443ffe3e768eb9fe74538f5def4")
						.build(cityName, countryCode))
				.retrieve()
				.onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND), clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.bodyToMono(WeatherData.class).block();
	}

	private City[] getCities(String searchTerm) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.scheme("http")
						.host("localhost")
						.port(80)
						.path("/search/")
						.path(searchTerm).build())
				.retrieve()
				.onStatus(httpStatus ->  httpStatus.equals(HttpStatus.NOT_FOUND), clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.bodyToMono(City[].class).block();
	}
}
