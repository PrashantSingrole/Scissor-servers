package com.salon.service.dto;

public class SalonReviewDto {

	private String user;
	
	private int star;
	
	private String review;
	
	public SalonReviewDto(String user, int star, String review) {
		super();
		this.user = user;
		this.star = star;
		this.review = review;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}
	
	
	
}
