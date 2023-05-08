package com.example.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.example.Model.User;
import com.example.repository.AccountRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final AccountRepository accountRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(JwtAuthenticationFilter.class);

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
			AccountRepository accountRepository) {
		super(authenticationManager);
		this.accountRepository = accountRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {
		log.debug("Entering filter: {}", getClass().getSimpleName());
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			Authentication authentication = null;
			try {
				authentication = getAuthentication(request);
			} catch (AccessDeniedException e) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("Acced denied exception");
				return;
			}
			if (authentication == null) {
				filterChain.doFilter(request, response);
				return;
			}

			SecurityContextHolder.getContext()
					.setAuthentication(authentication);
			filterChain.doFilter(request, response);

		}
	}

	private PreAuthenticatedAuthenticationToken getAuthentication(
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (StringUtils.isNotEmpty(token)) {
			try {
				log.debug("received Authorization header : {}", token);

				Jws<Claims> parsedToken = parseClaimsJws(token);
				String username = parsedToken.getBody().getSubject();
				Optional<User> userDetails = accountRepository
						.signUpUser(username);
				if (!userDetails.isPresent()) {
					throw new UsernameNotFoundException(
							"User not found with username: " + username);
				} else {
					User user = userDetails.get();
					List<SimpleGrantedAuthority> authorities = user
							.getPrivilege().entrySet().stream()
							.flatMap(e -> e.getValue().stream()
									.map(v -> new SimpleGrantedAuthority(
											e.getKey() + "." + v)))
							.collect(Collectors.toUnmodifiableList());
					return new PreAuthenticatedAuthenticationToken(
							user.getUserName(),
							request.getHeader("Authorization"), authorities);
				}
			} catch (ExpiredJwtException exception) {
				log.error("Request to parse expired JWT : {} failed : {}",
						token, exception.getMessage());
			} catch (UnsupportedJwtException exception) {
				log.error("Request to parse unsupported JWT : {} failed : {}",
						token, exception.getMessage());
			} catch (MalformedJwtException exception) {
				log.error("Request to parse invalid JWT : {} failed : {}",
						token, exception.getMessage());
			} catch (SignatureException exception) {
				log.error(
						"Request to parse JWT with invalid signature : {} failed : {}",
						token, exception.getMessage());
			} catch (IllegalArgumentException exception) {
				log.error("Request to parse empty or null JWT : {} failed : {}",
						token, exception.getMessage());
			}
		}
		return null;
	}

	private Jws<Claims> parseClaimsJws(String token) {
		return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(
				"0jyt8U6FA9STBegoK3qs7xF/D/y9eTpZgMiZtGG+0ak=".getBytes()))
				.parseClaimsJws(token);
	}
}
