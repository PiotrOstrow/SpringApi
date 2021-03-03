package com.example.gateway.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

	private final JWTUtils jwtUtils;

	public AuthenticationManager(JWTUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();

		try {
			Jws<Claims> claims = jwtUtils.getAllClaimsFromToken(authToken);
			if (claims == null)
				return Mono.empty();

			Date expires = claims.getBody().getExpiration();
			if (expires.before(new Date(System.currentTimeMillis())))
				return Mono.empty();

			ArrayList<String> perms = (ArrayList<String>) claims.getBody().get("authorities");
			List<SimpleGrantedAuthority> authorities = perms.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

			return Mono.just(new UsernamePasswordAuthenticationToken(claims.getBody().getSubject(), null, authorities));
		}catch (Exception e) {
			e.printStackTrace();
			return Mono.empty();
		}
	}
}
