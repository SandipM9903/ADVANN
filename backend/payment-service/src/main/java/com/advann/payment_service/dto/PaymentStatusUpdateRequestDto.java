package com.advann.payment_service.dto;

import com.advann.payment_service.enums.PaymentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdateRequestDto {
    private PaymentStatus paymentStatus;
}