package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.json.JsonObject;
import com.example.Model.Account;
import com.example.repository.AccountRepository;
import com.example.repository.CountersRepository;

import net.minidev.json.JSONObject;

@Service
public class InfoServiceImpl {

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	private CountersRepository countersRepository;

	public ResponseEntity<JSONObject> save(Account account) {
		JSONObject item = new JSONObject();
		account.setId("USER_"+countersRepository.incCounter("0",0L));
		Account accountResponse = accountRepository.save(account);
		long count = accountRepository.count();
		List<JsonObject> response = accountRepository.select();
//		item.put("accountResponse", accountResponse);
		item.put("count", count);
		item.put("response", response);
		return new ResponseEntity<>(item, HttpStatus.OK);
	}
}