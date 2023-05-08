package com.example.Model;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class Account {
	
	String id;
	
	String firstName;
	
	String lastName;
	
	String gender;
	
	String email;
	
	String qualification;
	
	String phoneNumber;
	
	String desigination;
	
	String city;
	
	String state;
	
	String country;
	
	Date createdDate;
	
	String createdBy;
	
	Date updatedDate;
	
	String updatedBy;
	
}
