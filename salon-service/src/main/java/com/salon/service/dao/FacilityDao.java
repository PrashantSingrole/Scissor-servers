package com.salon.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.salon.service.entity.Facility;

@Repository
public interface FacilityDao extends JpaRepository<Facility, Integer> {

}
