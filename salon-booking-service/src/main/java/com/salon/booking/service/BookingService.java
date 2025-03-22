package com.salon.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salon.booking.dao.BookingDao;
import com.salon.booking.entity.Booking;

@Service
public class BookingService {
	
	@Autowired
	private BookingDao bookingDao;
	
	public Booking bookSalon(Booking booking) {
		return bookingDao.save(booking);
	}

	public List<Booking> getAllBookings() {
		return bookingDao.findAll();
	}
	
	public List<Booking> getMyBookings(int userId) {
		return bookingDao.findByUserId(userId);
	}
	
	public List<Booking> getMySalonBookings(int salonId) {
		return bookingDao.findBySalonId(salonId);
	}
	
	public Booking getBookingById(int bookingId) {
		return bookingDao.findById(bookingId).get();
	}
	
	
}
