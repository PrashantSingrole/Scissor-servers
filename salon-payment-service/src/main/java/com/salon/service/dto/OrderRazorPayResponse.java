package com.salon.service.dto;

import com.salon.service.pg.RazorPayPaymentRequest;

public class OrderRazorPayResponse extends CommanApiResponse {

	private RazorPayPaymentRequest razorPayRequest;

	private Booking booking;

	public RazorPayPaymentRequest getRazorPayRequest() {
		return razorPayRequest;
	}

	public void setRazorPayRequest(RazorPayPaymentRequest razorPayRequest) {
		this.razorPayRequest = razorPayRequest;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

}
