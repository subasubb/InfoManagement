package com.example.Model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class User {
	
	private String userName;

	private String name;
	
	private String password;

	private String accountType;

	private String email;

	private Map<String, List<String>> privilege;
	
}