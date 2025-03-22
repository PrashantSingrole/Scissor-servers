package com.salon.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.salon.service.entity.Salon;
import com.salon.service.entity.Location;

@Repository
public interface SalonDao extends JpaRepository<Salon, Integer> {
	
	List<Salon> findByLocation(Location locationId);
	
}
