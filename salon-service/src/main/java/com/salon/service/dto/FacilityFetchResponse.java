package com.salon.service.dto;

import java.util.Set;

import com.salon.service.entity.Facility;

public class FacilityFetchResponse extends CommanApiResponse { 
	
	private Set<Facility> facilities;

	public Set<Facility> getFacilities() {
		return facilities;
	}

	public void setFacilities(Set<Facility> facilities) {
		this.facilities = facilities;
	}
	
	

}
