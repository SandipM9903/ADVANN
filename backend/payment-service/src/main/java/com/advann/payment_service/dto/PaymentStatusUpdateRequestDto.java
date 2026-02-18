package com.advann.payment_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdateRequestDto {
    private String paymentStatus;
}