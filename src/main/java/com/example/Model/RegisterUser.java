package com.example.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RegisterUser {

	String userName;
	
	String name;
	
	String password;
	
	AccountTypeEnum accountType;
	
	String email;
	
	Map<String, List<String>> privilege;
	
	public void setPrivilege(String key, List<String> value) {
        this.privilege = new HashMap<>();
        this.privilege.put(key, value);
    }
	
	
}
