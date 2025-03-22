package com.salon.service.dto;

import com.salon.service.entity.Salon;

public class SalonResponseDto extends CommanApiResponse {
	
	private Salon salon;

	public Salon getSalon() {
		return salon;
	}

	public void setSalon(Salon salon) {
		this.salon = salon;
	}
	
	

}
