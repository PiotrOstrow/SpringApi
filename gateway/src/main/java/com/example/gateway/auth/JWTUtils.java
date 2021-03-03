package com.example.gateway.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JWTUtils {

	public Jws<Claims> getAllClaimsFromToken(String authToken) {
		return Jwts.parser()
				.setSigningKey("verysecure123".getBytes(StandardCharsets.UTF_8))
				.parseClaimsJws(authToken);
	}
}
