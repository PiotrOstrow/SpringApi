package com.example.auth.controllers;

import com.example.auth.config.JwtConfig;
import com.example.auth.model.TokenResponse;
import com.example.auth.model.User;
import com.example.auth.model.UserCredentials;
import com.example.auth.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Controller {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtConfig jwtConfig;

	public Controller(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtConfig jwtConfig) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtConfig = jwtConfig;
	}

	@PostMapping("/signup")
	public void signUp(@RequestBody UserCredentials userCredentials) {
		if(userRepository.existsByUsername(userCredentials.getUsername()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid username");

		User user = new User();
		user.setUsername(userCredentials.getUsername());
		user.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
		user.setRoles("ROLE_USER");
		userRepository.save(user);
	}

	@PostMapping("/auth")
	public TokenResponse auth(@RequestBody UserCredentials userCredentials) {
		User user = userRepository.findByUsername(userCredentials.getUsername());
		if(user == null || !passwordEncoder.matches(userCredentials.getPassword(), user.getPassword()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

		//List<GrantedAuthority> grantedAuthorities = Arrays.stream(user.getRoles().split(","))
		//		.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles());

		long now = System.currentTimeMillis();
		String token = Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + jwtConfig.getExpiration() * 1000L))
				.claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
				.compact();

		TokenResponse tokenResponse = new TokenResponse();
		tokenResponse.access_token = token;
		tokenResponse.token_type = jwtConfig.getPrefix();
		tokenResponse.expires_in = jwtConfig.getExpiration();
		return tokenResponse;
	}
}
