package com.salon.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.salon.service.dto.Booking;
import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.OrderRazorPayResponse;
import com.salon.service.pg.RazorPayPaymentResponse;
import com.salon.service.service.PaymentService;

@RestController
@RequestMapping("api/payment/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PutMapping("order/create")
	public ResponseEntity<OrderRazorPayResponse> createRazorPayOrder(@RequestBody Booking booking) throws RazorpayException {
		return this.paymentService.createRazorPayOrder(booking);
	}

	@PutMapping("razorpPay/response")
	public ResponseEntity<CommanApiResponse> updateUserWallet(@RequestBody RazorPayPaymentResponse razorPayResponse)
			throws RazorpayException {
		return this.paymentService.handleRazorPayPaymentResponse(razorPayResponse);
	}

}
