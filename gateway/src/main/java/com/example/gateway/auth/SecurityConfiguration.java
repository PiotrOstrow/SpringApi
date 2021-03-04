package com.example.gateway.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;

	public SecurityConfiguration(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
		this.authenticationManager = authenticationManager;
		this.securityContextRepository = securityContextRepository;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
		return serverHttpSecurity.httpBasic().disable()
				.formLogin().disable()
				.csrf().disable()
				.logout().disable()
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.authenticationManager(authenticationManager)
				.securityContextRepository(securityContextRepository)
				.exceptionHandling()
				.authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() ->
						swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
				.accessDeniedHandler((swe, e) -> Mono.fromRunnable(() ->
						swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
				.and()
				.authorizeExchange()
				.pathMatchers("/auth/**").permitAll()
				.pathMatchers(HttpMethod.GET).authenticated()
				.anyExchange().hasRole("ADMIN") // all but GET  or /auth/** requires admin
				.and().build();
	}
}
