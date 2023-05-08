package com.example.Resource;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Model.Account;
import com.example.Model.RegisterUser;
import com.example.service.InfoServiceImpl;

@Validated
@RestController
public class InfoResource {

	@Autowired
	InfoServiceImpl infoServiceImpl;

	@PreAuthorize("hasAuthority('USER.CREATE')")
	@PostMapping("/api/v1/create/account")
	public ResponseEntity<String> saveValues(@RequestBody Account account) {
		return infoServiceImpl.save(account);
	}

	@PreAuthorize("hasAuthority('USER.READ')")
	@GetMapping("/api/v1/get/account")
	public List<Account> getValues(@RequestParam(value = "userName") String userName) {
		return infoServiceImpl.get(userName);
	}

	@PreAuthorize("hasAuthority('USER.READ')")
	@GetMapping("/api/v1/get/account/{id}")
	public Account getValuesUsingId(
			@PathVariable(value = "id") String accountId)
			throws ParseException {
		return infoServiceImpl.getValuesUsingId(accountId);
	}

	@PreAuthorize("hasAuthority('USER.UPDATE')")
	@PutMapping("/api/v1/update/account/{id}")
	public ResponseEntity<String> updateValues(
			@PathVariable(value = "id") String accountId,
			@RequestBody Account account) {
		return infoServiceImpl.update(accountId, account);
	}

	@PreAuthorize("hasAuthority('USER.DELETE')")
	@DeleteMapping("/api/v1/delete/account/{id}")
	public ResponseEntity<String> deleteValues(
			@PathVariable(value = "id") String accountId) {
		return infoServiceImpl.delete(accountId);
	}

	@PostMapping("/api/v1/register/user")
	public ResponseEntity<String> registerUser(
			@RequestBody RegisterUser registerUser) {
		return infoServiceImpl.registerUser(registerUser);
	}

	@GetMapping("/api/v1/signUp/user")
	public ResponseEntity<String> signUpUser(
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "password") String password) {
		return infoServiceImpl.signUpUser(userName, password);
	}

}