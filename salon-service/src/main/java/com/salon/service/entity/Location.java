package com.salon.service.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.beans.BeanUtils;

import com.salon.service.dto.LocationFetchResponse;

@Entity
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String city;

	private String description;

	@OneToMany
	private List<Salon> salons;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Salon> getSalons() {
		return salons;
	}

	public void setSalons(List<Salon> salons) {
		this.salons = salons;
	}

	public static LocationFetchResponse toLocationFetchResponse(Location location) {
		LocationFetchResponse locationFetchResponse = new LocationFetchResponse();
		BeanUtils.copyProperties(location, locationFetchResponse);
		return locationFetchResponse;
	}

}
