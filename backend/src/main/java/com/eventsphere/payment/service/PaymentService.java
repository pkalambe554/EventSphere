package com.eventsphere.payment.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eventsphere.booking.entity.Booking;
import com.eventsphere.booking.entity.SeatStatus;
import com.eventsphere.booking.entity.Seats;
import com.eventsphere.booking.entity.Status;
import com.eventsphere.booking.repository.BookingRepository;
import com.eventsphere.booking.repository.SeatRepository;
import com.eventsphere.common.exception.ApiException;
import com.eventsphere.payment.entity.Payment;
import com.eventsphere.payment.entity.PaymentStatus;
import com.eventsphere.payment.repository.PaymentRepository;

@Service
public class PaymentService {
	@Value("${payment.webhook-secret}")
	private String webhookSecret;
	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;
	private final SeatRepository seatRepository;
	public PaymentService(PaymentRepository paymentRepository ,
			BookingRepository bookingRepository,
			SeatRepository seatRepository) {
		this.paymentRepository=paymentRepository;
		this.bookingRepository=bookingRepository;
		this.seatRepository=seatRepository;
	}

	
	public Payment initiatePayment(Booking booking, BigDecimal amount) {
		Payment payment= new Payment();
		payment.setBooking(booking);
		payment.setAmount(amount);
		payment.setPaymentReference(UUID.randomUUID().toString());
		payment.setPaymentStatus(PaymentStatus.PENDING);
		 return this.paymentRepository.save(payment);		
	}
	
	public String computeSignature(String payload) {
	    try {
	        Mac mac = Mac.getInstance("HmacSHA256");
	        SecretKeySpec keySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	        mac.init(keySpec);
	        byte[] hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
	        return HexFormat.of().formatHex(hashBytes);
	    } catch (Exception ex) {
	        throw new RuntimeException("Failed to compute webhook signature", ex);
	    }
	}
	
	public Payment handleWebhook(String rawPayload, String receivedSignature, String idempotencyKey ,Long paymentId, boolean success) throws Exception  {
		String expectedSignature = computeSignature(rawPayload);
	    if (!expectedSignature.equals(receivedSignature)) {
	        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid webhook signature");
	    }
		Optional<Payment> exsting=this.paymentRepository.findByIdempotencyKey(idempotencyKey);
		if(exsting.isPresent()) {
			return exsting.get();
		}
		Payment p = this.paymentRepository.findById(paymentId)
				.orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Payment time Expired"));
		p.setPaymentStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED );
		this.paymentRepository.save(p);
		
		
		Booking b =p.getBooking();
		
		Seats s = b.getSeats();
		if(success && b != null) {
			b.setStatus(Status.CONFIRMED);
		  	s.setSeatStatus(SeatStatus.BOOKED );		  	
		}else {
			b.setStatus(Status.FAILED);
	        s.setSeatStatus(SeatStatus.AVAILABLE); 
		}
		bookingRepository.save(b);
		seatRepository.save(s);
		return p;
		
	}
}
