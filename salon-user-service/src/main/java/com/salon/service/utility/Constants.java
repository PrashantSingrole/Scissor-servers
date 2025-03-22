package com.salon.service.utility;

public class Constants {
	
	public enum UserRole {
		ADMIN("Admin"),
		CUSTOMER("Customer"),
		SALON("Salon");
		
		
		private String role;

	    private UserRole(String role) {
	      this.role = role;
	    }

	    public String value() {
	      return this.role;
	    }    
	}
	
	public enum Sex {
		MALE("Male"),
		FEMALE("Female");
		
		
		private String sex;

	    private Sex(String sex) {
	      this.sex = sex;
	    }

	    public String value() {
	      return this.sex;
	    }    
	}
	
	public enum ResponseCode {
		SUCCESS(0),
		FAILED(1);
		
		
		private int code;

	    private ResponseCode(int code) {
	      this.code = code;
	    }

	    public int value() {
	      return this.code;
	    }    
	}
	
}
