package com.eventsphere.payment.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
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
import com.eventsphere.notification.NotificationProducer;
import com.eventsphere.payment.entity.Payment;
import com.eventsphere.payment.entity.PaymentStatus;
import com.eventsphere.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class PaymentService {
	@Value("${payment.webhook-secret}")
	private String webhookSecret;
	@Value("${razorpay.key-id}")
	private String razorpayKeyId;
	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;
	private final SeatRepository seatRepository;
	private final NotificationProducer notificationProducer;
	private final RazorpayClient razorpayClient;
	public PaymentService(PaymentRepository paymentRepository ,
			BookingRepository bookingRepository,
			SeatRepository seatRepository,
			NotificationProducer notificationProducer,
			RazorpayClient razorpayClient) {
		this.paymentRepository=paymentRepository;
		this.bookingRepository=bookingRepository;
		this.seatRepository=seatRepository;
		this.notificationProducer=notificationProducer;
		this.razorpayClient=razorpayClient;
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
	
	public Payment handleWebhook(String rawPayload, String receivedSignature, String razorpayOrderId, String razorpayPaymentId, boolean success) throws Exception {
	    String expectedSignature = computeSignature(rawPayload);
	    if (!expectedSignature.equals(receivedSignature)) {
	        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid webhook signature");
	    }

	    Optional<Payment> existing = this.paymentRepository.findByPaymentReference(razorpayOrderId);
	    Payment p = existing.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found for order " + razorpayOrderId));

	    // Idempotency: already processed (status already terminal) → return as-is, skip re-processing
	    if (p.getPaymentStatus() == PaymentStatus.SUCCESS || p.getPaymentStatus() == PaymentStatus.FAILED) {
	        return p;
	    }

	    p.setPaymentStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
	    this.paymentRepository.save(p);

	    Booking b = p.getBooking();
	    Seats s = b.getSeats();
	    if (success && b != null) {
	        b.setStatus(Status.CONFIRMED);
	        s.setSeatStatus(SeatStatus.BOOKED);
	        notificationProducer.publishBookingConfirmed(b.getBookId(), b.getUser().getEmail());
	    } else {
	        b.setStatus(Status.FAILED);
	        s.setSeatStatus(SeatStatus.AVAILABLE);
	    }
	    bookingRepository.save(b);
	    seatRepository.save(s);
	    return p;
	}
	
	public Map<String, Object> createRazorpayOrder(Long bookingId, BigDecimal amount) throws Exception {
	    Booking booking = bookingRepository.findById(bookingId)
	            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));

	    // Razorpay expects amount in the smallest currency unit - paise, not rupees
	    int amountInPaise = amount.multiply(BigDecimal.valueOf(100)).intValue();

	    JSONObject orderRequest = new JSONObject();
	    orderRequest.put("amount", amountInPaise);
	    orderRequest.put("currency", "INR");
	    orderRequest.put("receipt", "booking-" + bookingId);

	    Order order = razorpayClient.orders.create(orderRequest);

	    Payment payment = initiatePayment(booking, amount); // your existing method, reused as-is
	    payment.setPaymentReference(order.get("id")); // store Razorpay's order id against your Payment row
	    paymentRepository.save(payment);

	    Map<String, Object> response = new HashMap<>();
	    response.put("razorpayOrderId", order.get("id"));
	    response.put("razorpayKeyId", razorpayKeyId); // frontend needs this to open the checkout widget
	    response.put("paymentId", payment.getId());
	    response.put("amount", amountInPaise);
	    return response;
	}
}
