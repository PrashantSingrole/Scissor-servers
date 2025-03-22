package com.salon.booking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.salon.booking.entity.Booking;

@Repository
public interface BookingDao extends JpaRepository<Booking, Integer> {
	
	List<Booking> findByUserId(int userId);
	List<Booking> findBySalonId(int salonId);
	List<Booking> findByDateAndTimeSlotAndStatusAndSalonId(String date, String timeSlot, String status, Integer salonId);
	
}
