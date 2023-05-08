package com.example.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.couchbase.client.java.json.JsonObject;
import com.example.Model.Account;
import com.example.Model.AccountTypeEnum;
import com.example.Model.RegisterUser;
import com.example.Model.User;
import com.example.repository.AccountRepository;
import com.example.repository.CountersRepository;
import com.example.security.ClientTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InfoServiceImpl {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	ClientTokenService clientTokenService;

	@Autowired
	private CountersRepository countersRepository;

	public ResponseEntity<String> save(Account account) {
		log.debug("Entering save method");

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmss");
		String datetime = ft.format(dNow);

		account.setId("ID_" + datetime + "_"
				+ countersRepository.incCounter("0", 0L));
		account.setCreatedDate(new Date());
		try {
			Account accountResponse = accountRepository.save(null, account);
			if (accountResponse != null) {
				log.debug("Exiting save method with success response");
				return new ResponseEntity<>("Account Created Successfully",
						HttpStatus.OK);
			} else {
				log.error("Exiting save method with error response");
				return new ResponseEntity<>("Error Occurred.. Please try again",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("Exiting save method with error response {}", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					ExceptionUtils.getStackTrace(e));
		}

	}

	public List<Account> get(String userName) {
		log.debug("Entering get method");

		SimpleDateFormat originalFormat = new SimpleDateFormat("yyMMddhhmmss");

		List<Account> accountList = new ArrayList<>();
		try {
			List<JsonObject> accountResponse = accountRepository.select(userName);
			for (JsonObject json : accountResponse) {
				JsonObject info = json.getObject("info");
				Account account = new Account();
				account.setId(String.valueOf(info.get("id")));
				account.setFirstName(String.valueOf(info.get("firstName")));
				account.setLastName(String.valueOf(info.get("lastName")));
				account.setPhoneNumber(String.valueOf(info.get("phoneNumber")));
				account.setEmail(String.valueOf(info.get("email")));
				account.setCreatedBy(String.valueOf(info.get("createdBy")));

				DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
				Instant instant = Instant.from(formatter.parse(String.valueOf(info.get("createdDate"))));
				account.setCreatedDate(Date.from(instant));
				accountList.add(account);
			}
		} catch (Exception e) {
			log.error("Exiting save method with error response {}", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					ExceptionUtils.getStackTrace(e));
		}
		log.debug("Exiting get method with response");
		return accountList;
	}

	public ResponseEntity<String> update(String accountId, Account account) {
		log.debug("Entering update method");

		account.setUpdatedDate(new Date());
		try {
			Account accountResponse = accountRepository.save(accountId,
					account);
			if (accountResponse != null) {
				log.debug("Exiting update method with success response");
				return new ResponseEntity<>("Account Updated Successfully",
						HttpStatus.OK);
			} else {
				log.error("Exiting update method with error response");
				return new ResponseEntity<>("Error Occurred.. Please try again",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("Exiting update method with error response {}", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					ExceptionUtils.getStackTrace(e));
		}
	}

	public ResponseEntity<String> delete(String accountId) {
		log.debug("Entering delete method");
		try {
			accountRepository.delete(accountId);
			log.debug("Exiting delete method with success response");
			return new ResponseEntity<>("Account Deleted Successfully",
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exiting delete method with error response {}", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					ExceptionUtils.getStackTrace(e));
		}
	}

	public Account getValuesUsingId(String accountId) throws ParseException {
		log.debug("Entering getValuesUsingId method");
		try {
			List<JsonObject> accountResponse = accountRepository
					.selectUsingId(accountId);
			Account account = new Account();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			for (JsonObject json : accountResponse) {
				JsonObject info = json.getObject("info");
				account.setId(String.valueOf(info.get("id")));
				account.setFirstName(String.valueOf(info.get("firstName")));
				account.setLastName(String.valueOf(info.get("lastName")));
				account.setPhoneNumber(String.valueOf(info.get("phoneNumber")));
				account.setEmail(String.valueOf(info.get("email")));
				account.setDesigination(
						String.valueOf(info.get("desigination")));
				account.setQualification(
						String.valueOf(info.get("qualification")));
				account.setGender(String.valueOf(info.get("gender")));
				account.setState(String.valueOf(info.get("state")));
				account.setCity(String.valueOf(info.get("city")));
				account.setCountry(String.valueOf(info.get("country")));
				account.setCreatedDate(
						format.parse(String.valueOf(info.get("createdDate"))));
			}
			log.debug("Exiting getValuesUsingId method with success response");
			return account;
		} catch (Exception e) {
			log.error("Exiting getValuesUsingId method with error response {}",
					e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					ExceptionUtils.getStackTrace(e));
		}
	}

	public ResponseEntity<String> registerUser(RegisterUser registerUser) {
		log.debug("Account register method Starts");
		AccountTypeEnum accountType = registerUser.getAccountType();
		switch (accountType) {
		case ADMIN:
			registerUser.setPrivilege("USER",
					Arrays.asList("READ", "CREATE", "UPDATE"));
			break;
		case L1USER:
			registerUser.setPrivilege("USER",
					Arrays.asList("CREATE", "READ", "UPDATE"));
			break;
		case L2USER:
			registerUser.setPrivilege("USER", Arrays.asList("CREATE", "READ"));
			break;
		default:
			registerUser.setPrivilege("USER", Arrays.asList("READ"));
			break;
		}
		try {
			String registerUserResponse = accountRepository
					.registerUser(registerUser);
			if (registerUserResponse != null) {
				log.debug("Account Created Successfully");
				return new ResponseEntity<String>(
						"Account Created Successfully", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						"Error Occurred.. Please try again",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			if ("Document with the given id already exists"
					.equals(e.getMessage())) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
						.body("{\"error\": \"" + e.getMessage() + "\",}");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"" + e.getMessage() + "\",}");
		}
	}

	public ResponseEntity<String> signUpUser(String userName, String password) {
		log.debug("User signed up method starts");
		try {
			Optional<User> userDetails = accountRepository.signUpUser(userName);
			if (!userDetails.isPresent()) {
				throw new UsernameNotFoundException(
						"User not found with username: " + userName);
			} else {
				User user = userDetails.get();

				if (user.getPassword().equals(password)) {
					String accessToken = clientTokenService.generateBearerToken(
							userName, user.getEmail(), user.getPrivilege(),
							user.getAccountType());
					log.debug("User signed up successfully: {}", userName);
					return new ResponseEntity<String>(accessToken,
							HttpStatus.OK);
				} else {
					log.error("Invalid password for user:{} ", userName);
					return new ResponseEntity<String>("false",
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		} catch (Exception e) {
			log.error("Error signing up user:{}, {} ", userName, e);
			return new ResponseEntity<String>("false",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}