package com.salon.service.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.salon.service.entity.SalonFacility;

@Repository
public interface SalonFacilityDao extends JpaRepository<SalonFacility, Integer> {
	
	List<SalonFacility> findBySalonId(int salonId);

}
