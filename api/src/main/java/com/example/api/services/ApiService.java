package com.example.api.services;

import com.example.api.config.ApiConfiguration;
import com.example.api.model.City;
import com.example.api.util.CountryCodeMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableRetry
public class ApiService {

	/**
	 * Max number of requests made to the external api when search results in multiple results
	 */
	private static final int MAX_REQUESTS = 5;

	private final WebClient webClient;
	private final CountryCodeMap countryCodeMap;
	private final ObjectMapper objectMapper;
	private final WebClient.Builder loadBalancedWebClientBuilder;
	private final ApiConfiguration apiConfiguration;

	public ApiService(WebClient webClient, CountryCodeMap countryCodeMap, ObjectMapper objectMapper,
					  WebClient.Builder loadBalancedWebClientBuilder, ApiConfiguration apiConfiguration) {
		this.webClient = webClient;
		this.countryCodeMap = countryCodeMap;
		this.objectMapper = objectMapper;
		this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
		this.apiConfiguration = apiConfiguration;
	}

	public Object search(String searchTerm) {
		City[] cities = getCities(searchTerm);

		if(cities == null || cities.length == 0)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No city matching \"" + searchTerm + "\" found");

		if(cities.length > MAX_REQUESTS)
			cities = Arrays.copyOfRange(cities, 0, MAX_REQUESTS);

		int callCount = Math.min(MAX_REQUESTS, cities.length);

		Flux<ObjectNode> flux = Flux.fromArray(cities)
				.parallel(callCount)
				.runOn(Schedulers.boundedElastic())
				.flatMap(this::getWeatherData)
				.sequential();

		List<ObjectNode> weatherDataList = flux.collectList().block();
		weatherDataList.sort(Comparator.comparingInt(o -> o.get("dbID").asInt()));

		ArrayNode arrayNode = objectMapper.createArrayNode();

		for(ObjectNode weatherData : weatherDataList) {
			// find the corresponding city object
			City city = Arrays.stream(cities).filter(c -> c.getId() == weatherData.get("dbID").asInt()).findFirst().get();

			String countryCode = countryCodeMap.getCountryCode(city.getCountry());

			ObjectNode element = objectMapper.createObjectNode();
			element.put("id", city.getId());
			element.put("city", city.getCity());
			element.put("country", city.getCountry());
			element.put("country_code", countryCode);
			element.set("coord", weatherData.get("coord"));
			element.set("weather", constructWeatherNode(weatherData));

			arrayNode.add(element);
		}

		return objectMapper.createObjectNode().set("result", arrayNode);
	}

	private ObjectNode constructWeatherNode(ObjectNode weatherData) {
		JsonNode weather = weatherData.get("weather").get(0);
		JsonNode main = weatherData.get("main");

		ObjectNode weatherNode = objectMapper.createObjectNode();
		weatherNode.set("weather", weather.get("main"));
		weatherNode.set("description", weather.get("description"));
		weatherNode.set("temp", main.get("temp"));
		weatherNode.set("feels_like", main.get("feels_like"));
		weatherNode.set("temp_min", main.get("temp_min"));
		weatherNode.set("temp_max", main.get("temp_max"));
		weatherNode.set("pressure", main.get("pressure"));
		weatherNode.set("humidity", main.get("humidity"));
		weatherNode.set("visibility", weatherData.get("visibility"));
		weatherNode.set("wind", weatherData.get("wind"));
		weatherNode.set("sunrise", weatherData.get("sys").get("sunrise"));
		weatherNode.set("sunset", weatherData.get("sys").get("sunset"));

		return weatherNode;
	}

	private Mono<ObjectNode> getWeatherData(final City city) {
		String cityName = city.getCity();
		String countryCode = countryCodeMap.getCountryCode(city.getCountry());
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.scheme("http")
						.host("api.openweathermap.org")
						.path("/data/2.5/weather")
						.queryParam("q", "{cityName},{countryCode}")
						.queryParam("units", apiConfiguration.getUnits())
						.queryParam("appid", apiConfiguration.getKey())
						.build(cityName, countryCode))
				.retrieve()
				.onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND), clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.bodyToMono(ObjectNode.class)
				.map(jsonNode -> jsonNode.put("dbID", city.getId()));
	}

	private City[] getCities(String searchTerm) {
		return loadBalancedWebClientBuilder.build().get().uri("http://cities-api/search/" + searchTerm).retrieve().bodyToMono(City[].class).block();
	}
}
