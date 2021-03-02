package com.example.api.controllers;

import com.example.api.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Autowired
	private ApiService apiService;

	@GetMapping("/city/{searchTerm}")
	public Object get(@PathVariable String searchTerm) {
		return apiService.search(searchTerm);
	}
}
