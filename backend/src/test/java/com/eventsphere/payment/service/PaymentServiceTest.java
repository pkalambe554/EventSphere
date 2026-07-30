package com.eventsphere.payment.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void sameSignatureForSamePayload() {
        String sig1 = paymentService.computeSignature("hello-world");
        String sig2 = paymentService.computeSignature("hello-world");
        assertEquals(sig1, sig2);
    }

    @Test
    void differentSignatureForDifferentPayload() {
        String sig1 = paymentService.computeSignature("payload-A");
        String sig2 = paymentService.computeSignature("payload-B");
        assertNotEquals(sig1, sig2);
    }
}