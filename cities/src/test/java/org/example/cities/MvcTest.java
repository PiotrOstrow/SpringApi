package org.example.cities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cities.controllers.Controller;
import org.example.cities.model.CityDto;
import org.example.cities.services.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(Controller.class)
public class MvcTest {

	@MockBean
	private Service service;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void putWithNoContentShouldReturn204() throws Exception {
		when(service.replace(any(CityDto.class), anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/city/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(new CityDto()))).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.NO_CONTENT.value());
	}

	@Test
	void patchWithNewCityShouldSaveAndReturn200WithSavedCityAsJson() throws Exception {
		CityDto city = sampleCityDto();
		when(service.update(any(CityDto.class), anyLong())).thenReturn(city);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/city/" + city.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(city))
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);

		CityDto resultBody = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CityDto.class);
		assertEquals(resultBody, city);
	}

	@Test
	void putWithNewCityShouldSaveAndReturn200WithSavedCityAsJson() throws Exception {
		CityDto city = sampleCityDto();
		when(service.replace(any(CityDto.class), anyLong())).thenReturn(city);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/city/" + city.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(city))
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);

		CityDto resultBody = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CityDto.class);
		assertEquals(resultBody, city);
	}

	@Test
	void postWithNewCityShouldSaveAndReturnWithID() throws Exception {
		CityDto cityDto = sampleCityDto();
		CityDto expectedResult = sampleCityDto();
		expectedResult.setId(cityDto.getId() + 20); // just so there's different id
		when(service.create(cityDto)).thenReturn(expectedResult);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/city")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(cityDto))).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.CREATED.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);

		CityDto resultBody = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CityDto.class);
		assertEquals(resultBody, expectedResult);
	}

	@Test
	void getWithNonExistingIdShouldReturn404() throws Exception {
		when(service.findOne(9)).thenReturn(Optional.empty());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city/9")).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
	}

	@Test
	void getWithIdShouldReturnOneAsJson() throws Exception {
		CityDto cityDto = sampleCityDto();
		when(service.findOne(1)).thenReturn(Optional.of(cityDto));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city/1")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);

		CityDto resultBody = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CityDto.class);
		assertEquals(resultBody, cityDto);
	}

	@Test
	void getAllShouldReturnAllAsJson() throws Exception {
		List<CityDto> cityDtoList = sampleCityDtoList();
		when(service.findAll()).thenReturn(cityDtoList);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);

		CityDto[] resultBody = objectMapper.readValue(result.getResponse().getContentAsByteArray(), CityDto[].class);

		assertEquals(cityDtoList.size(), resultBody.length);

		for(int i = 0; i < cityDtoList.size(); ++i)
			assertEquals(resultBody[i], cityDtoList.get(i));
	}

	private CityDto sampleCityDto() {
		return new CityDto(5, "Paris", "France");
	}

	private List<CityDto> sampleCityDtoList() {
		return List.of(
				new CityDto(5, "Paris", "France"),
				new CityDto(6, "Berlin", "Germany"),
				new CityDto(5, "Tokyo", "Japan")
		);
	}
}
