package com.salon.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.salon.service.entity.SalonReview;

@Repository
public interface SalonReviewDao extends JpaRepository<SalonReview, Integer> {

	List<SalonReview> findBySalonId(int salonId);
	
}
