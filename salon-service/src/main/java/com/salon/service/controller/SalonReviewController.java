package com.salon.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.SalonReviewDto;
import com.salon.service.dto.SalonReviewResponseDto;
import com.salon.service.dto.User;
import com.salon.service.dto.UsersResponseDto;
import com.salon.service.entity.SalonReview;
import com.salon.service.external.UserService;
import com.salon.service.service.SalonReviewService;
import com.salon.service.utility.Constants.ResponseCode;

@RestController
@RequestMapping("api/salon/review")
//@CrossOrigin(origins = "http://localhost:3000")
public class SalonReviewController {
	
	Logger LOG = LoggerFactory.getLogger(SalonReviewController.class);

	@Autowired
	private SalonReviewService salonReviewService;
	
    @Autowired
    private UserService userService;
	
	@PostMapping("add")
	public ResponseEntity<?> register(@RequestBody SalonReview review) {
		LOG.info("Recieved request for Add Salon Review");

		CommanApiResponse response = new CommanApiResponse();

		if (review == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add review");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}
		
		SalonReview salonReview = salonReviewService.addSalonReview(review);
		
		if (salonReview != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Salon Review Added Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add Salon");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("fetch")
	public ResponseEntity<?> fetchSalonReview(@RequestParam("salonId") int salonId) {
		LOG.info("Recieved request for Fetch Salon Reviews for Salon Id : "+salonId);

		SalonReviewResponseDto response = new SalonReviewResponseDto();

		List<SalonReview> reviews = salonReviewService.fetchSalonReviews(salonId);
		
		List<SalonReviewDto> reviewDto = new ArrayList<>();
		
		for(SalonReview review : reviews) {
			
			UsersResponseDto userResponse = this.userService.fetchUser(review.getUserId());
			
			if(userResponse == null) {
				throw new RuntimeException("User service is down!!");
			}
			
			User user = userResponse.getUser();
			
			reviewDto.add(new SalonReviewDto(user.getFirstName(), review.getStar(), review.getReview()));
			
		}
		
		
		try {
			response.setSalonReviews(reviewDto);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Salon Reviews Fetched Successfully");
			return new ResponseEntity(response, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch Salon Reviews");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}
