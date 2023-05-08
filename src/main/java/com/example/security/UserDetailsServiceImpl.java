package com.example.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.json.JsonObject;
import com.example.repository.AccountRepository;

@Service
@Scope("prototype")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

//	@Override
//	public UserDetails loadUserByUsername(String username)
//			throws UsernameNotFoundException {
//		AccountRepository accountRepository = new AccountRepository();
//		List<JsonObject> userList = accountRepository.signUpUser(username);
//		User user = null;
//		if (userList.isEmpty()) {
//			throw new UsernameNotFoundException(
//					"User not found with username: " + username);
//		}
//		for (JsonObject json : userList) {
//			JsonObject info = json.getObject("info");
//			Collection<GrantedAuthority> privileges = parseAuthorities(
//					String.valueOf(info.get("privilege")));
//			user = new User(username, username, privileges);
//		}
//		return new org.springframework.security.core.userdetails.User(
//				user.getUsername(), user.getPassword(), user.getAuthorities());
//	}

	private Collection<GrantedAuthority> parseAuthorities(
			String authoritiesAsString) {
		List<String> authorityStrings = List.of(authoritiesAsString.split(","));
		return authorityStrings.stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
