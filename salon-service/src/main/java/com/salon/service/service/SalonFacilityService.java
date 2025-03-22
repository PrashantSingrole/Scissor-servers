package com.salon.service.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.salon.service.dao.SalonFacilityDao;
import com.salon.service.entity.SalonFacility;

@Service
public class SalonFacilityService {
	
	@Autowired
	private SalonFacilityDao salonFacilityDao;
	
	public List<SalonFacility> getSalonFacilitiesBySalonId(int salonId) {
		return this.salonFacilityDao.findBySalonId(salonId);
	}
	
	public SalonFacility addFacility(SalonFacility salonFacility) {
	    return this.salonFacilityDao.save(salonFacility);
	}

}
