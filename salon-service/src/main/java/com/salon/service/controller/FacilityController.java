package com.salon.service.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.salon.service.dto.FacilityFetchResponse;
import com.salon.service.dto.SalonFacilityAddRequest;
import com.salon.service.entity.Facility;
import com.salon.service.entity.SalonFacility;
import com.salon.service.service.FacilityService;
import com.salon.service.service.SalonFacilityService;
import com.salon.service.service.SalonService;
import com.salon.service.utility.Constants.ResponseCode;

@RestController
@RequestMapping("api/salon/facility/")
//@CrossOrigin(origins = "http://localhost:3000")
public class FacilityController {

	Logger LOG = LoggerFactory.getLogger(FacilityController.class);

	@Autowired
	private FacilityService facilityService;

	@Autowired
	private SalonService salonService;

	@Autowired
	private SalonFacilityService salonFacilityService;

	@PostMapping("add")
	public ResponseEntity<?> register(@RequestBody Facility facility) {
		LOG.info("Recieved request for Add Facility");

		CommanApiResponse response = new CommanApiResponse();

		Facility addedFacility = facilityService.addFacility(facility);

		if (addedFacility != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Facility Added Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add Facility");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("fetch")
	public ResponseEntity<?> fetchAllFacilities() {
		LOG.info("Recieved request for Fetch Facility");

		FacilityFetchResponse facilityFetchResponse = new FacilityFetchResponse();

		Set<Facility> facilities = facilityService.fetchAllFacilities();

		try {
			facilityFetchResponse.setFacilities(facilities);
			facilityFetchResponse.setResponseCode(ResponseCode.SUCCESS.value());
			facilityFetchResponse.setResponseMessage("Facilities Fetched Successfully");
			return new ResponseEntity(facilityFetchResponse, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			facilityFetchResponse.setResponseCode(ResponseCode.FAILED.value());
			facilityFetchResponse.setResponseMessage("Failed to Fetch Facility");
			return new ResponseEntity(facilityFetchResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("salon")
	public ResponseEntity<?> fetchAllFacilitiesBySalonId(@RequestParam("salonId") int salonId) {
		LOG.info("Recieved request for Fetch Facility");

		FacilityFetchResponse facilityFetchResponse = new FacilityFetchResponse();

		List<SalonFacility> salonFacilities = this.salonFacilityService.getSalonFacilitiesBySalonId(salonId);

		Set<Facility> facilities = new HashSet<>();

		for (SalonFacility salonFacility : salonFacilities) {
			facilities.add(this.facilityService.getFacilityById(salonFacility.getFacilityId()));
		}

		try {
			facilityFetchResponse.setFacilities(facilities);
			facilityFetchResponse.setResponseCode(ResponseCode.SUCCESS.value());
			facilityFetchResponse.setResponseMessage("Facilities Fetched Successfully");

			System.out.println(facilityFetchResponse);

			return new ResponseEntity(facilityFetchResponse, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			facilityFetchResponse.setResponseCode(ResponseCode.FAILED.value());
			facilityFetchResponse.setResponseMessage("Failed to Fetch Facility");
			return new ResponseEntity(facilityFetchResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("salon/add")
	public ResponseEntity<?> addSalonFacility(@RequestBody SalonFacilityAddRequest addFacility) {
		LOG.info("Recieved request for Add Facility");

		CommanApiResponse response = new CommanApiResponse();

		Facility facility = facilityService.getFacilityById(addFacility.getFacilityId());

		if (facility == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Facility not found");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<SalonFacility> salonFacilities = this.salonFacilityService
				.getSalonFacilitiesBySalonId(addFacility.getSalonId());

		Set<Facility> facilities = new HashSet<>();

		for (SalonFacility salonFacility : salonFacilities) {
			if (salonFacility.getFacilityId() == facility.getId()) {
				response.setResponseCode(ResponseCode.FAILED.value());
				response.setResponseMessage("Facility already added");
				return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
			}
		}

		SalonFacility salonFacility = new SalonFacility();
		salonFacility.setSalonId(addFacility.getSalonId());
		salonFacility.setFacilityId(addFacility.getFacilityId());

		salonFacilities.add(salonFacility);

		SalonFacility addedSalonFacility = salonFacilityService.addFacility(salonFacility);

		if (addedSalonFacility != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Salon Facility Added Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add Salon Facility");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
