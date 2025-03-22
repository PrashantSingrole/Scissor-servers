package com.salon.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salon.service.dao.SalonDao;
import com.salon.service.entity.Salon;
import com.salon.service.entity.Location;

@Service
public class SalonService {

	@Autowired
	private SalonDao salonDao;

	public Salon addSalon(Salon salon) {
		return salonDao.save(salon);
	}
	
	public List<Salon> fetchAllSalons() {
		return salonDao.findAll();
	}
	
	public List<Salon> fetchSalonsByLocation(Location locationId) {
		return salonDao.findByLocation(locationId);
	}
	
	public Salon fetchSalon(int salonId) {
		return salonDao.findById(salonId).get();
	}

}
