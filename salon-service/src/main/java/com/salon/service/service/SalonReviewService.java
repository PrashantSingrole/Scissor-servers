package com.salon.service.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.salon.service.dao.SalonReviewDao;
import com.salon.service.entity.SalonReview;

@Service
public class SalonReviewService {
	
	@Autowired
	private SalonReviewDao salonReviewDao;
	
	public SalonReview addSalonReview(SalonReview review) {
		return salonReviewDao.save(review);
	}
	
	public List<SalonReview> fetchSalonReviews(int salonId) {
		return salonReviewDao.findBySalonId(salonId);
	}

}
