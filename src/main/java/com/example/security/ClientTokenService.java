package com.example.security;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class ClientTokenService {

	public String generateBearerToken(String username, String email,
			Map<String, List<String>> privileges, String accountType) {

		JwtBuilder builder = Jwts.builder();

		builder.setHeaderParam("typ", "JWT").setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + 864000000))
				.claim("username", username).claim("email", email)
				.claim("privilege", privileges)
				.claim("accountType", accountType).setIssuer("Admin_Service")
				.signWith(Keys.hmacShaKeyFor(
						"0jyt8U6FA9STBegoK3qs7xF/D/y9eTpZgMiZtGG+0ak="
								.getBytes()),
						SignatureAlgorithm.HS256);

		String token = builder.compact();

		return token;

	}

}
