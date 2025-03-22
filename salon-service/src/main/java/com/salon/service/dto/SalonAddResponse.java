package com.salon.service.dto;

import java.util.List;

import com.salon.service.entity.Salon;

public class SalonAddResponse extends CommanApiResponse {

	List<Salon> salons;

	public List<Salon> getSalons() {
		return salons;
	}

	public void setSalons(List<Salon> salons) {
		this.salons = salons;
	}
	
	
	
}
