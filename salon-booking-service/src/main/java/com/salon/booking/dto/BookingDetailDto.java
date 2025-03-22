package com.salon.booking.dto;

import java.util.List;

import com.salon.booking.entity.Booking;

public class BookingDetailDto extends CommanApiResponse {

	private List<BookingDto> bookings;

	private Booking booking;

	public List<BookingDto> getBookings() {
		return bookings;
	}

	public void setBookings(List<BookingDto> bookings) {
		this.bookings = bookings;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

}
