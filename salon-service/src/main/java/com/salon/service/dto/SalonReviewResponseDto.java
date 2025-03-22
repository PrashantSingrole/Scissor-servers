package com.salon.service.dto;

import java.util.List;

public class SalonReviewResponseDto extends CommanApiResponse {
	
	private List<SalonReviewDto> salonReviews;

	public List<SalonReviewDto> getSalonReviews() {
		return salonReviews;
	}

	public void setSalonReviews(List<SalonReviewDto> salonReviews) {
		this.salonReviews = salonReviews;
	}
	
	

}
