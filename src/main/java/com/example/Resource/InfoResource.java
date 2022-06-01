package com.example.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Model.Account;
import com.example.service.InfoServiceImpl;

import net.minidev.json.JSONObject;

@Validated
@RestController
@RequestMapping("/api/v1")
public class InfoResource {

	@Autowired
	InfoServiceImpl infoServiceImpl;

	@PostMapping("/value/save")
	public ResponseEntity<JSONObject> saveValues(@RequestBody Account account) {
		return infoServiceImpl.save(account);
	}

}