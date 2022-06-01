package com.example.Model;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class Account {
	
	String id;
	
	String name;
	
	String desigination;
	
	String location;
	
	Date createdDate;
	
//	public void setId() {
//		this.id = UUID.randomUUID().toString().replace("-", "");
//	}
//	
//	public void setCreatedDate() {
//		this.createdDate = new Date();
//	}

}
