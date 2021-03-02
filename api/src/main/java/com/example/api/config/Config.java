package com.example.api.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

	@Bean
	WebClient webClient() {
		return WebClient.create();
	}

	@Bean
	@LoadBalanced
	WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

}
