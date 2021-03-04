package com.example.api.controllers;

import com.example.api.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
public class Controller {

	@Autowired
	private ApiService apiService;

	@GetMapping("/city/{searchTerm}")
	public Object get(@PathVariable String searchTerm) {
		return apiService.search(searchTerm);
	}

	@GetMapping("/city")
	public Object getWithParam(@RequestParam(required = false) String search) {
		return apiService.search(search);
	}
}
