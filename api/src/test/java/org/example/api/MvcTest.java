package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.controllers.Controller;
import org.example.api.model.City;
import org.example.api.model.CityDto;
import org.example.api.services.Service;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
		long id = 5;
		CityDto city = new CityDto(id, "A", "B");
		when(service.update(any(CityDto.class), anyLong())).thenReturn(city);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/city/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(city))
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	void putWithNewCityShouldSaveAndReturn200WithSavedCityAsJson() throws Exception {
		long id = 5;
		CityDto city = new CityDto(id, "A", "B");
		when(service.replace(any(CityDto.class), anyLong())).thenReturn(city);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/city/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(city))
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	void postWithNewCityShouldSaveAndReturnWithID() throws Exception {
		CityDto cityDto = new CityDto(0, "A", "B");
		CityDto cityResult = new CityDto(231253, cityDto.getCity(), cityDto.getCountry());
		when(service.create(cityDto)).thenReturn(cityResult);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/city")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(cityDto))).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.CREATED.value());
	}

	@Test
	void getWithNonExistingIdShouldReturn404() throws Exception {
		when(service.findOne(9)).thenReturn(Optional.empty());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city/9")).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
	}

	@Test
	void getWithIdShouldReturnOneAsJson() throws Exception {
		when(service.findOne(1)).thenReturn(Optional.of(new CityDto(1, "A", "B")));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city/1")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	void getAllShouldReturnAllAsJson() throws Exception {
		when(service.findAll()).thenReturn(List.of(
				new CityDto(1, "A", "B"),
				new CityDto(2, "C", "D")
		));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/city")
				.accept(MediaType.APPLICATION_JSON)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		assertEquals(result.getResponse().getContentType(), MediaType.APPLICATION_JSON_VALUE);
	}
}
