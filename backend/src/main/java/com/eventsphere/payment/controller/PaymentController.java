package com.eventsphere.payment.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
			Booking book =bookingService.findByBookId(bookingId)
					.orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Not Available"));
			return this.paymentService.initiatePayment(book, amount);
			}
	
	@PostMapping("/webhook")
	public ResponseEntity<Payment> webhook(
	        @RequestBody String rawPayload,
	        @RequestHeader("X-Webhook-Signature") String signature
	) throws Exception {
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> body = mapper.readValue(rawPayload, Map.class);

	    String idempotencyKey = (String) body.get("idempotencyKey");
	    Long paymentId = Long.valueOf(body.get("paymentId").toString());
	    boolean success = (Boolean) body.get("success");

	    Payment payment = paymentService.handleWebhook(rawPayload, signature, idempotencyKey, paymentId, success);
	    return ResponseEntity.ok(payment);
	}
	// TEMPORARY - for testing signature generation only, delete once webhook flow is confirmed working
	@PostMapping("/debug-signature")
	public String debugSignature(@RequestBody String payload) {
	    return paymentService.computeSignature(payload);
	}
}
