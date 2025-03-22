package com.salon.booking.dto;

public class BookingDto {

	private int id;

	private String bookingId;

	private int userId;

	private String customerName;

	private String date;

	private String timeSlot;

	private String status;

	private int salonId;

	private String salonContact;

	private String salonEmail;

	private String salonImage;

	private String salonName;

	private String customerContact;

	private String totalAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSalonId() {
		return salonId;
	}

	public void setSalonId(int salonId) {
		this.salonId = salonId;
	}

	public String getSalonContact() {
		return salonContact;
	}

	public void setSalonContact(String salonContact) {
		this.salonContact = salonContact;
	}

	public String getSalonEmail() {
		return salonEmail;
	}

	public void setSalonEmail(String salonEmail) {
		this.salonEmail = salonEmail;
	}

	public String getSalonImage() {
		return salonImage;
	}

	public void setSalonImage(String salonImage) {
		this.salonImage = salonImage;
	}

	public String getSalonName() {
		return salonName;
	}

	public void setSalonName(String salonName) {
		this.salonName = salonName;
	}

	public String getCustomerContact() {
		return customerContact;
	}

	public void setCustomerContact(String customerContact) {
		this.customerContact = customerContact;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

}
