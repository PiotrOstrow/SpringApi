package org.example.cities.controllers;

import org.apache.commons.lang3.StringUtils;
import org.example.cities.model.CityDto;
import org.example.cities.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class Controller {

	private final Service service;

	public Controller(Service service){
		this.service = service;
	}

	@GetMapping("/city")
	public List<CityDto> get(@RequestParam(required = false) String search) {
		if(StringUtils.isBlank(search))
			return service.findAll();
		return service.search(search);
	}

	@GetMapping("/city/{id}")
	public CityDto getOne(@PathVariable long id) {
		return service.findOne(id).orElseThrow(() ->
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Id " + id + " does not exist"));
	}

	@GetMapping("/search/{term}")
	public List<CityDto> search(@PathVariable String term) {
		return service.search(term);
	}

	@PostMapping("/city")
	@ResponseStatus(HttpStatus.CREATED)
	public CityDto create(@RequestBody CityDto cityDto) {
		return service.create(cityDto);
	}

	@PutMapping("/city/{id}")
	public CityDto put(@RequestBody CityDto cityDto, @PathVariable long id) {
		return service.replace(cityDto, id);
	}

	@PatchMapping("/city/{id}")
	public CityDto patch(@RequestBody CityDto cityDto, @PathVariable long id) {
		return service.update(cityDto, id);
	}

	@DeleteMapping("/city/{id}")
	//@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void delete(@PathVariable long id) {
		//service.delete(id);
	}
}
