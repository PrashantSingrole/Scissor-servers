package com.salon.booking.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salon.booking.dao.BookingDao;
import com.salon.booking.dto.BookingDetailDto;
import com.salon.booking.dto.BookingDto;
import com.salon.booking.dto.CommanApiResponse;
import com.salon.booking.dto.Salon;
import com.salon.booking.dto.SalonResponseDto;
import com.salon.booking.dto.UpdateBookingStatusRequestDto;
import com.salon.booking.dto.User;
import com.salon.booking.dto.UsersResponseDto;
import com.salon.booking.entity.Booking;
import com.salon.booking.exception.BookingNotFoundException;
import com.salon.booking.external.SalonService;
import com.salon.booking.external.UserService;
import com.salon.booking.service.BookingService;
import com.salon.booking.utility.Constants.BookingStatus;
import com.salon.booking.utility.Constants.ResponseCode;
import com.salon.booking.utility.Helper;

@Transactional
@RestController
@RequestMapping("api/book/salon")
//@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

	Logger LOG = LoggerFactory.getLogger(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BookingDao bookingDao;

	@Autowired
	private SalonService salonService;

	@Autowired
	private UserService userService;

	@PostMapping("/validate")
	public ResponseEntity<?> validateCustomerBooking(@RequestBody Booking booking) {
		LOG.info("Recieved request for booking salon");

		System.out.println(booking);

		BookingDetailDto response = new BookingDetailDto();

		if (booking == null) {
//			response.setResponseCode(ResponseCode.FAILED.value());
//			response.setResponseMessage("Salon Booking Failed");
//			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);

			throw new BookingNotFoundException();
		}

		if (booking.getUserId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("User is not not looged in");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (booking.getSalonId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Salon not found to Book");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

		if (salonResponse == null) {
			throw new RuntimeException("salon service is down!!!");
		}

		Salon salon = salonResponse.getSalon();

		if (salon == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("No Salon present with this Id");
		}

		UsersResponseDto customerResponse = this.userService.fetchUser(booking.getUserId());

		if (customerResponse == null) {
			throw new RuntimeException("user service is down!!!");
		}

		User customer = customerResponse.getUser();

		if (customer == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("No Salon present with this Id");
		}

		booking.setSalonUserId(salon.getUserId());

		List<Booking> bookings = this.bookingDao.findByDateAndTimeSlotAndStatusAndSalonId(booking.getDate(),
				booking.getTimeSlot(), BookingStatus.APPROVED.value(), booking.getSalonId());

		if (!CollectionUtils.isEmpty(bookings)) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Slot already booked!!!");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		booking.setStatus(BookingStatus.PENDING.value());

		booking.setBookingId(Helper.getAlphaNumericId());
		booking.setPrice(salon.getPricePerDay());
		booking.setCustomerContact(customer.getContact());
		booking.setCustomerEmailId(customer.getEmailId());
		booking.setCustomerFirstName(customer.getFirstName());
		booking.setCustomerLastName(customer.getLastName());

		Booking bookedSalon = this.bookingService.bookSalon(booking);

		if (bookedSalon != null) {
			response.setBooking(bookedSalon);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Congratulations, Slot is Available, Loading the payment page...");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Book Salon");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/")
	public ResponseEntity<?> addCustomerBooking(@RequestBody Booking booking) {
		LOG.info("Recieved request for booking salon");

		CommanApiResponse response = new CommanApiResponse();

		if (booking == null) {
//			response.setResponseCode(ResponseCode.FAILED.value());
//			response.setResponseMessage("Salon Booking Failed");
//			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);

			throw new BookingNotFoundException();
		}

		if (booking.getUserId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("User is not not looged in");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (booking.getSalonId() == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Salon not found to Book");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

		if (salonResponse == null) {
			throw new RuntimeException("salon service is down!!!");
		}

		Salon salon = salonResponse.getSalon();

		if (salon == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("No Salon present with this Id");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		// in validation part we have already done this

//		List<Booking> bookings = this.bookingDao.findByDateAndTimeSlotAndStatusAndSalonId(booking.getDate(),
//				booking.getTimeSlot(), BookingStatus.APPROVED.value(), booking.getSalonId());
//
//		if (!CollectionUtils.isEmpty(bookings)) {
//			response.setResponseCode(ResponseCode.FAILED.value());
//			response.setResponseMessage("Slot already booked!!!");
//			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
//		}

		Booking pendingBooking = this.bookingDao.findById(booking.getId()).orElse(null);

		if (pendingBooking == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to book the Salon Slot!!!");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		pendingBooking.setStatus(BookingStatus.APPROVED.value());

		Booking bookedSalon = this.bookingService.bookSalon(pendingBooking);

		if (bookedSalon != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("Congratulions, you have booked the slot successful!!!");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Book Salon");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/fetch/all")
	public ResponseEntity<?> fetchAllSalonBooking() {
		LOG.info("Recieved request for fetch all booking");

		BookingDetailDto response = new BookingDetailDto();

		List<BookingDto> bookings = new ArrayList<>();

		List<Booking> allBookings = this.bookingService.getAllBookings();

		for (Booking booking : allBookings) {

			BookingDto dto = new BookingDto();

			dto.setBookingId(booking.getBookingId());
			dto.setDate(booking.getDate());
			dto.setTimeSlot(booking.getTimeSlot());

			UsersResponseDto customerResponse = this.userService.fetchUser(booking.getUserId());

			if (customerResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User customer = customerResponse.getUser();
			dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

			SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

			if (salonResponse == null) {
				throw new RuntimeException("salon service is down!!!");
			}

			Salon salon = salonResponse.getSalon();

			UsersResponseDto salonUserResponse = this.userService.fetchUser(booking.getUserId());

			if (salonUserResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User salonUser = salonUserResponse.getUser();

			dto.setSalonEmail(salon.getEmailId());
			dto.setSalonContact(salonUser.getContact());
			dto.setSalonId(salon.getId());
			dto.setStatus(booking.getStatus());
			dto.setUserId(customer.getId());
			dto.setSalonName(salon.getName());
			dto.setSalonImage(salon.getImage1());
			dto.setCustomerContact(customer.getContact());
			dto.setTotalAmount(String.valueOf(salon.getPricePerDay()));
			dto.setId(booking.getId());

			bookings.add(dto);
		}

		response.setBookings(bookings);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Booking Fetched Successfully");
		return new ResponseEntity(response, HttpStatus.OK);

	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchMyBooking(@RequestParam("userId") int userId) {
		LOG.info("Recieved request for fetch all booking");

		BookingDetailDto response = new BookingDetailDto();

		List<BookingDto> bookings = new ArrayList<>();

		List<Booking> allBookings = this.bookingService.getMyBookings(userId);

		for (Booking booking : allBookings) {

			BookingDto dto = new BookingDto();

			dto.setBookingId(booking.getBookingId());
			dto.setDate(booking.getDate());
			dto.setTimeSlot(booking.getTimeSlot());

			UsersResponseDto customerResponse = this.userService.fetchUser(booking.getUserId());

			if (customerResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User customer = customerResponse.getUser();
			dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

			SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

			if (salonResponse == null) {
				throw new RuntimeException("salon service is down!!!");
			}

			Salon salon = salonResponse.getSalon();

			UsersResponseDto salonUserResponse = this.userService.fetchUser(booking.getUserId());

			if (salonUserResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User salonUser = salonUserResponse.getUser();
			dto.setSalonEmail(salon.getEmailId());
			dto.setSalonContact(salonUser.getContact());
			dto.setSalonId(salon.getId());
			dto.setStatus(booking.getStatus());

			dto.setUserId(customer.getId());
			dto.setSalonName(salon.getName());
			dto.setSalonImage(salon.getImage1());
			dto.setCustomerContact(customer.getContact());
			dto.setTotalAmount(String.valueOf(salon.getPricePerDay()));
			dto.setId(booking.getId());

			bookings.add(dto);
		}

		response.setBookings(bookings);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Booking Fetched Successfully");
		return new ResponseEntity(response, HttpStatus.OK);

	}

	@GetMapping("/fetch/id")
	public ResponseEntity<?> fetchBookingById(@RequestParam("bookingId") int bookingId) {
		LOG.info("Recieved request for fetch booking by Id");

		Booking booking = this.bookingService.getBookingById(bookingId);

		if (booking == null) {
			throw new BookingNotFoundException();
		}

		BookingDto dto = new BookingDto();

		dto.setBookingId(booking.getBookingId());
		dto.setDate(booking.getDate());
		dto.setTimeSlot(booking.getTimeSlot());

		UsersResponseDto customerResponse = this.userService.fetchUser(booking.getUserId());

		if (customerResponse == null) {
			throw new RuntimeException("user service is down!!!");
		}

		User customer = customerResponse.getUser();
		dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

		SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

		if (salonResponse == null) {
			throw new RuntimeException("salon service is down!!!");
		}

		Salon salon = salonResponse.getSalon();

		UsersResponseDto salonUserResponse = this.userService.fetchUser(booking.getUserId());

		if (salonUserResponse == null) {
			throw new RuntimeException("user service is down!!!");
		}

		User salonUser = salonUserResponse.getUser();
		dto.setSalonEmail(salon.getEmailId());
		dto.setSalonContact(salonUser.getContact());
		dto.setSalonId(salon.getId());
		dto.setStatus(booking.getStatus());
		dto.setUserId(customer.getId());
		dto.setSalonName(salon.getName());
		dto.setSalonImage(salon.getImage1());
		dto.setCustomerContact(customer.getContact());
		dto.setTotalAmount(String.valueOf(salon.getPricePerDay()));
		dto.setId(booking.getId());

		return new ResponseEntity(dto, HttpStatus.OK);

	}

	@GetMapping("/fetch/bookings")
	public ResponseEntity<?> fetchMySalonBooking(@RequestParam("salonId") int salonId) {
		LOG.info("Recieved request for fetch all booking");

		BookingDetailDto response = new BookingDetailDto();

		List<BookingDto> bookings = new ArrayList<>();

		List<Booking> allBookings = this.bookingService.getMySalonBookings(salonId);

		for (Booking booking : allBookings) {

			BookingDto dto = new BookingDto();

			dto.setBookingId(booking.getBookingId());
			dto.setDate(booking.getDate());
			dto.setTimeSlot(booking.getTimeSlot());

			UsersResponseDto customerResponse = this.userService.fetchUser(booking.getUserId());

			if (customerResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User customer = customerResponse.getUser();
			dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

			SalonResponseDto salonResponse = this.salonService.fetchSalon(booking.getSalonId());

			if (salonResponse == null) {
				throw new RuntimeException("salon service is down!!!");
			}

			Salon salon = salonResponse.getSalon();

			UsersResponseDto salonUserResponse = this.userService.fetchUser(booking.getUserId());

			if (salonUserResponse == null) {
				throw new RuntimeException("user service is down!!!");
			}

			User salonUser = salonUserResponse.getUser();
			dto.setSalonEmail(salon.getEmailId());
			dto.setSalonContact(salonUser.getContact());
			dto.setSalonId(salon.getId());
			dto.setStatus(booking.getStatus());
			dto.setUserId(customer.getId());
			dto.setSalonName(salon.getName());
			dto.setSalonImage(salon.getImage1());
			dto.setCustomerContact(customer.getContact());
			dto.setTotalAmount(String.valueOf(salon.getPricePerDay()));
			dto.setId(booking.getId());

			bookings.add(dto);
		}

		response.setBookings(bookings);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Booking Fetched Successfully");
		return new ResponseEntity(response, HttpStatus.OK);

	}

	@GetMapping("/fetch/status")
	public ResponseEntity<?> fetchAllBookingStatus() {
		LOG.info("Recieved request for fetch all booking status");

		List<String> response = new ArrayList<>();

		for (BookingStatus status : BookingStatus.values()) {
			response.add(status.value());
		}

		return new ResponseEntity(response, HttpStatus.OK);

	}

	@PostMapping("/update/status")
	public ResponseEntity<?> updateSalonBookingStatus(@RequestBody UpdateBookingStatusRequestDto request) {

		LOG.info("Recieved request for updating the Salon Booking Status");

		CommanApiResponse response = new CommanApiResponse();

		Booking b = this.bookingService.getBookingById(request.getBookingId());

		if (b == null) {
			throw new BookingNotFoundException();
		}

		if (request.getStatus().equals("") || request.getStatus() == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Booking Status can not be empty");
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		b.setStatus(request.getStatus());
		this.bookingService.bookSalon(b);

		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Booking Status Updated");
		return new ResponseEntity(response, HttpStatus.OK);

	}

}
