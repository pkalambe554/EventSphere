package com.eventsphere.payment.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventsphere.booking.entity.Booking;
import com.eventsphere.booking.service.BookingService;
import com.eventsphere.common.exception.ApiException;
import com.eventsphere.payment.entity.Payment;
import com.eventsphere.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Milestone 4: Payment entity with unique idempotency_key, sandbox
 * gateway client, POST /webhook with signature verification.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	private final PaymentService paymentService;
	private final BookingService bookingService;
	
	public PaymentController (PaymentService paymentService, BookingService bookingService) {
		this.paymentService=paymentService;
		this.bookingService=bookingService;
	}
	
	@PostMapping("/initiate")
	public Payment initiate(@RequestParam Long bookingId, @RequestParam BigDecimal amount) {
		System.out.println("API-CALL: "+" /api/payments/initiate");
			Booking book =bookingService.findByBookId(bookingId)
					.orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Not Available"));
			return this.paymentService.initiatePayment(book, amount);
			}
	
	@PostMapping("/webhook")
	public ResponseEntity<Payment> webhook(
	        @RequestBody String rawPayload,
	        @RequestHeader("X-Razorpay-Signature") String signature
	) throws Exception {
	    System.out.println("API-CALL: /api/payments/webhook");
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> body = mapper.readValue(rawPayload, Map.class);

	    Map<String, Object> payload = (Map<String, Object>) body.get("payload");
	    Map<String, Object> paymentEntity = (Map<String, Object>) ((Map<String, Object>) payload.get("payment")).get("entity");

	    String razorpayPaymentId = (String) paymentEntity.get("id");
	    String razorpayOrderId = (String) paymentEntity.get("order_id");
	    String status = (String) paymentEntity.get("status");
	    boolean success = "captured".equals(status);

	    Payment payment = paymentService.handleWebhook(rawPayload, signature, razorpayOrderId,razorpayPaymentId, success);
	    return ResponseEntity.ok(payment);
	}
	// TEMPORARY - for testing signature generation only, delete once webhook flow is confirmed working
	@PostMapping("/debug-signature")
	public String debugSignature(@RequestBody String payload) {
	    return paymentService.computeSignature(payload);
	}
	
	// In PaymentController
	@PostMapping("/simulate-confirm/{paymentId}")
	public ResponseEntity<Payment> simulateConfirm(@PathVariable Long paymentId) throws Exception {
		System.out.println("API-CALL: "+" /api/payments/simulate-confirm/{paymentId}");

	    String idempotencyKey = "sim-" + paymentId + "-" + System.currentTimeMillis();
	    String rawPayload = "{\"idempotencyKey\":\"" + idempotencyKey + "\",\"paymentId\":" + paymentId + ",\"success\":true}";
	    String signature = paymentService.computeSignature(rawPayload);

	    Payment payment = paymentService.handleWebhook(rawPayload, signature, idempotencyKey, "", true);
	    return ResponseEntity.ok(payment);
	}
	@PostMapping("/create-order")
	public ResponseEntity<Map<String, Object>> createOrder(
	        @RequestParam Long bookingId,
	        @RequestParam BigDecimal amount
	) throws Exception {
	    return ResponseEntity.ok(paymentService.createRazorpayOrder(bookingId, amount));
	}
}
