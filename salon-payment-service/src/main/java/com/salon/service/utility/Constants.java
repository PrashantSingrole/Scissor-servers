package com.salon.service.utility;

public class Constants {

	public enum BookingStatus {
		APPROVED("Approved"), PENDING("Pending"), CANCEL("Cancel");

		private String status;

		private BookingStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum ResponseCode {
		SUCCESS(0), FAILED(1);

		private int code;

		private ResponseCode(int code) {
			this.code = code;
		}

		public int value() {
			return this.code;
		}
	}

	public enum PaymentGatewayTxnType {
		CREATE_ORDER("Create Order"), PAYMENT("Payment");

		private String type;

		private PaymentGatewayTxnType(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
	}

	public enum PaymentGatewayTxnStatus {
		SUCCESS("Success"), FAILED("Failed");

		private String type;

		private PaymentGatewayTxnStatus(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
	}

}
