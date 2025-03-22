package com.salon.service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.salon.service.dao.PgTransactionDao;
import com.salon.service.dto.Booking;
import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.OrderRazorPayResponse;
import com.salon.service.external.BookingService;
import com.salon.service.external.UserService;
import com.salon.service.model.PgTransaction;
import com.salon.service.pg.Notes;
import com.salon.service.pg.Prefill;
import com.salon.service.pg.RazorPayPaymentRequest;
import com.salon.service.pg.RazorPayPaymentResponse;
import com.salon.service.pg.Theme;
import com.salon.service.utility.Constants.PaymentGatewayTxnStatus;
import com.salon.service.utility.Constants.PaymentGatewayTxnType;
import com.salon.service.utility.Constants.ResponseCode;

@Service
public class PaymentService {

	Logger LOG = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private PgTransactionDao pgTransactionDao;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@Value("${com.salon.paymentGateway.razorpay.key}")
	private String razorPayKey;

	@Value("${com.salon.paymentGateway.razorpay.secret}")
	private String razorPaySecret;

	public ResponseEntity<OrderRazorPayResponse> createRazorPayOrder(Booking booking) throws RazorpayException {
		OrderRazorPayResponse response = new OrderRazorPayResponse();

		if (booking.getUserId() == 0) {
			response.setResponseMessage("bad request - user id is missing");
			response.setResponseCode(ResponseCode.FAILED.value());

			return new ResponseEntity<OrderRazorPayResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String requestTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		BigDecimal totalPrice = new BigDecimal(booking.getPrice());

		// write payment gateway code here
		String receiptId = generateUniqueRefId();

		RazorpayClient razorpay = new RazorpayClient(razorPayKey, razorPaySecret);

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", convertRupeesToPaisa(totalPrice));
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", receiptId);
		JSONObject notes = new JSONObject();
		notes.put("note", "Salon Slot Booking Payment - Salon Booking System");
		orderRequest.put("notes", notes);

		Order order = razorpay.orders.create(orderRequest);

		if (order == null) {
			LOG.error("Null Response from RazorPay for creation of Order");
			response.setResponseMessage("Failed to Order the Products");
			response.setResponseCode(ResponseCode.FAILED.value());
			return new ResponseEntity<OrderRazorPayResponse>(response, HttpStatus.BAD_REQUEST);
		}

		LOG.info(order.toString()); // printing the response which we got from RazorPay

		String orderId = order.get("id");

		PgTransaction createOrder = new PgTransaction();
		createOrder.setAmount(totalPrice);
		createOrder.setReceiptId(receiptId);
		createOrder.setRequestTime(requestTime);
		createOrder.setType(PaymentGatewayTxnType.CREATE_ORDER.value());
		createOrder.setUserId(booking.getUserId());
		createOrder.setOrderId(orderId); // fetching order id which is created at Razor Pay which we got in response
		createOrder.setBookingId(booking.getId());

		if (order.get("status").equals("created")) {
			createOrder.setStatus(PaymentGatewayTxnStatus.SUCCESS.value());
		} else {
			createOrder.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		}

		PgTransaction saveCreateOrderTxn = this.pgTransactionDao.save(createOrder);

		if (saveCreateOrderTxn == null) {
			LOG.error("Failed to save Payment Gateway CReate Order entry in DB");
		}

		PgTransaction payment = new PgTransaction();
		payment.setAmount(totalPrice);
		payment.setReceiptId(receiptId);
		payment.setRequestTime(requestTime);
		payment.setType(PaymentGatewayTxnType.PAYMENT.value());
		payment.setUserId(booking.getUserId());
		payment.setOrderId(orderId); // fetching order id which is created at Razor Pay which we got in response
		payment.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		payment.setBookingId(booking.getId());
		// from callback api we will actual response from RazorPay, initially keeping it
		// FAILED, once get success response from PG,
		// we will update it

		PgTransaction savePaymentTxn = this.pgTransactionDao.save(payment);

		if (savePaymentTxn == null) {
			LOG.error("Failed to save Payment Gateway Payment entry in DB");
		}

		// Creating RazorPayPaymentRequest to send to Frontend

		RazorPayPaymentRequest razorPayPaymentRequest = new RazorPayPaymentRequest();
		razorPayPaymentRequest.setAmount(convertRupeesToPaisa(totalPrice));
		// razorPayPaymentRequest.setCallbackUrl("http://localhost:8080/pg/razorPay/callBack/response");
		razorPayPaymentRequest.setCurrency("INR");
		razorPayPaymentRequest.setDescription("Salon Slot Booking Payment - Salon Booking System");
		razorPayPaymentRequest.setImage("https://thumbs.dreamstime.com/b/salon-concept-logo-26458280.jpg");
		razorPayPaymentRequest.setKey("rzp_test_9C5DF9gbJINYTA");
		razorPayPaymentRequest.setName("Salon Booking System");

		Notes note = new Notes();
		note.setAddress("Dummy Address");

		razorPayPaymentRequest.setNotes(note);
		razorPayPaymentRequest.setOrderId(orderId);

		Prefill prefill = new Prefill();
		prefill.setContact(booking.getCustomerContact());
		prefill.setEmail(booking.getCustomerEmailId());
		prefill.setName(booking.getCustomerFirstName() + " " + booking.getCustomerLastName());

		razorPayPaymentRequest.setPrefill(prefill);

		Theme theme = new Theme();
		theme.setColor("#D4AA00");

		razorPayPaymentRequest.setTheme(theme);

		try {
			String jsonRequest = objectMapper.writeValueAsString(razorPayPaymentRequest);
			System.out.println("*****************");
			System.out.println(jsonRequest);
			System.out.println("*****************");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//				customer.setWalletAmount(existingWalletAmount.add(request.getWalletAmount()));
		//
//				User updatedCustomer = this.userService.updateUser(customer);
		//
//				if (updatedCustomer == null) {
//					response.setResponseMessage("Failed to update the Wallet");
//					response.setSuccess(false);
//					return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
//				}

		response.setBooking(booking);
		response.setRazorPayRequest(razorPayPaymentRequest);
		response.setResponseMessage("Payment Order Created Successful!!!");
		response.setResponseCode(ResponseCode.SUCCESS.value());

		return new ResponseEntity<OrderRazorPayResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommanApiResponse> handleRazorPayPaymentResponse(RazorPayPaymentResponse razorPayResponse) {

		LOG.info("razor pay response came from frontend");

		try {
			LOG.info(objectMapper.writeValueAsString(razorPayResponse));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CommanApiResponse response = new CommanApiResponse();

		if (razorPayResponse == null || razorPayResponse.getRazorpayOrderId() == null) {
			response.setResponseMessage("Invalid Input response");
			response.setResponseCode(ResponseCode.FAILED.value());
			return new ResponseEntity<CommanApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		PgTransaction paymentTransaction = this.pgTransactionDao
				.findByTypeAndOrderId(PaymentGatewayTxnType.PAYMENT.value(), razorPayResponse.getRazorpayOrderId());

		if (paymentTransaction == null) {
			response.setResponseMessage("Failed to Order the Products");
			response.setResponseCode(ResponseCode.FAILED.value());
			return new ResponseEntity<CommanApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String razorPayRawResponse = "";
		try {
			razorPayRawResponse = objectMapper.writeValueAsString(razorPayResponse);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		paymentTransaction.setRawResponse(razorPayRawResponse);

		if (razorPayResponse.getError() == null) {
			paymentTransaction.setStatus(PaymentGatewayTxnStatus.SUCCESS.value());
		} else {
			paymentTransaction.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		}

		PgTransaction updatedTransaction = this.pgTransactionDao.save(paymentTransaction);

		if (updatedTransaction.getStatus().equals(PaymentGatewayTxnStatus.FAILED.value())) {
			response.setResponseMessage("Payment Failed!!!");
			response.setResponseCode(ResponseCode.FAILED.value());

			return new ResponseEntity<CommanApiResponse>(response, HttpStatus.OK);
		} else {

			// since razorpay payment successful, call user service to update the salon
			// wallet

			CommanApiResponse walletUpdateResponse = this.userService
					.updateSalonWallet(razorPayResponse.getBooking().getSalonUserId(), updatedTransaction.getAmount());

			if (walletUpdateResponse == null
					|| walletUpdateResponse.getResponseCode() != ResponseCode.SUCCESS.value()) {
				response.setResponseMessage("Slot Booking Failed, if Amount deducted shortly it will be refunded!!!");
				response.setResponseCode(ResponseCode.FAILED.value());

				return new ResponseEntity<CommanApiResponse>(response, HttpStatus.OK);
			}

			// call actual booking api
			CommanApiResponse bookingResponse = this.bookingService.addCustomerBooking(razorPayResponse.getBooking());

			if (bookingResponse == null || bookingResponse.getResponseCode() != ResponseCode.SUCCESS.value()) {
				response.setResponseMessage("Slot Booking Failed, if Amount deducted shortly it will be refunded!!!");
				response.setResponseCode(ResponseCode.FAILED.value());

				return new ResponseEntity<CommanApiResponse>(response, HttpStatus.OK);
			}
			response.setResponseMessage(bookingResponse.getResponseMessage());
			response.setResponseCode(ResponseCode.SUCCESS.value());

			return new ResponseEntity<CommanApiResponse>(response, HttpStatus.OK);

		}

	}

	private int convertRupeesToPaisa(BigDecimal rupees) {
		// Multiply the rupees by 100 to get the equivalent in paisa
		BigDecimal paisa = rupees.multiply(new BigDecimal(100));
		return paisa.intValue();
	}

	// for razor pay receipt id
	private String generateUniqueRefId() {
		// Get current timestamp in milliseconds
		long currentTimeMillis = System.currentTimeMillis();

		// Generate a 6-digit UUID (random number)
		String randomDigits = UUID.randomUUID().toString().substring(0, 6);

		// Concatenate timestamp and random digits
		String uniqueRefId = currentTimeMillis + "-" + randomDigits;

		return uniqueRefId;
	}

}
