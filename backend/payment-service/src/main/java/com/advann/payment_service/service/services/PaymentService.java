package com.advann.payment_service.service.services;

import com.advann.payment_service.dto.PaymentRequestDto;
import com.advann.payment_service.dto.PaymentVerifyRequestDto;
import com.advann.payment_service.dto.RazorpayOrderResponseDto;

public interface PaymentService {

    RazorpayOrderResponseDto createRazorpayOrder(PaymentRequestDto dto);

    String verifyPayment(PaymentVerifyRequestDto dto);
}