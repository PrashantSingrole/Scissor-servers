package com.salon.service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.SalonAddRequest;
import com.salon.service.dto.SalonAddResponse;
import com.salon.service.dto.SalonResponseDto;
import com.salon.service.entity.Salon;
import com.salon.service.entity.Location;
import com.salon.service.exception.SalonNotFoundException;
import com.salon.service.external.UserService;
import com.salon.service.service.SalonService;
import com.salon.service.service.LocationService;
import com.salon.service.utility.Constants.ResponseCode;
import com.salon.service.utility.StorageService;

@RestController
@RequestMapping("api/salon/")
//@CrossOrigin(origins = "http://localhost:3000")
public class SalonController {

	Logger LOG = LoggerFactory.getLogger(SalonController.class);

	@Autowired
	private SalonService salonService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private StorageService storageService;

	@Autowired
	private UserService userService;

	@PostMapping("add")
	public ResponseEntity<?> register(SalonAddRequest salonAddRequest) {
		LOG.info("Recieved request for Add Salon");

		CommanApiResponse response = new CommanApiResponse();

		if (salonAddRequest == null) {
			throw new SalonNotFoundException();
		}

		if (salonAddRequest.getLocationId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Salon Location is not selected");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (salonAddRequest.getUserId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Salon Admin is not selected");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		Salon salon = SalonAddRequest.toEntity(salonAddRequest);
		Location location = locationService.getLocationById(salonAddRequest.getLocationId());
		salon.setLocation(location);

		String image1 = storageService.store(salonAddRequest.getImage1());
		String image2 = storageService.store(salonAddRequest.getImage2());
		String image3 = storageService.store(salonAddRequest.getImage3());
		salon.setImage1(image1);
		salon.setImage2(image2);
		salon.setImage3(image3);
		Salon addedSalon = salonService.addSalon(salon);

		if (addedSalon != null) {

			CommanApiResponse updateResponse = this.userService.updateSalonAdmin(salonAddRequest.getUserId(),
					addedSalon.getId());

			if (updateResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			if (updateResponse.getResponseCode() != ResponseCode.SUCCESS.value()) {
				throw new RuntimeException("Failed to add the salon!!!");
			}

			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Salon Added Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to add Salon");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("id")
	public ResponseEntity<?> fetchSalon(@RequestParam("salonId") int salonId) {
		LOG.info("Recieved request for Fetch Salon using salon Id");

		SalonResponseDto response = new SalonResponseDto();

		Salon salon = salonService.fetchSalon(salonId);

		if (salon == null) {
			throw new SalonNotFoundException();
		}

		try {
			response.setSalon(salon);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Salon Fetched Successfully");
			return new ResponseEntity(response, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch Salon");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("fetch")
	public ResponseEntity<?> fetchAllSalons() {
		LOG.info("Recieved request for Fetch Salons");

		SalonAddResponse salonAddResponse = new SalonAddResponse();

		List<Salon> salons = salonService.fetchAllSalons();
		try {
			salonAddResponse.setSalons(salons);
			salonAddResponse.setResponseCode(ResponseCode.SUCCESS.value());
			salonAddResponse.setResponseMessage("Salons Fetched Successfully");
			return new ResponseEntity(salonAddResponse, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			salonAddResponse.setResponseCode(ResponseCode.FAILED.value());
			salonAddResponse.setResponseMessage("Failed to Fetch Salons");
			return new ResponseEntity(salonAddResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("location")
	public ResponseEntity<?> getProductsByCategories(@RequestParam("locationId") int locationId) {

		System.out.println("request came for getting all salons by locations");

		SalonAddResponse salonAddResponse = new SalonAddResponse();

		List<Salon> salons = new ArrayList<Salon>();

		Location location = locationService.getLocationById(locationId);

		salons = this.salonService.fetchSalonsByLocation(location);

		try {
			salonAddResponse.setSalons(salons);
			salonAddResponse.setResponseCode(ResponseCode.SUCCESS.value());
			salonAddResponse.setResponseMessage("Salons Fetched Successfully");
			return new ResponseEntity(salonAddResponse, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Exception Caught");
			salonAddResponse.setResponseCode(ResponseCode.FAILED.value());
			salonAddResponse.setResponseMessage("Failed to Fetch Salons");
			return new ResponseEntity(salonAddResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping(value = "/{salonImageName}", produces = "image/*")
	public void fetchProductImage(@PathVariable("salonImageName") String salonImageName, HttpServletResponse resp) {
		System.out.println("request came for fetching product pic");
		System.out.println("Loading file: " + salonImageName);
		Resource resource = storageService.load(salonImageName);
		if (resource != null) {
			try (InputStream in = resource.getInputStream()) {
				ServletOutputStream out = resp.getOutputStream();
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("response sent!");
	}

}
