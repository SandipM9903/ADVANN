package com.advann.order_service.dto;

import com.advann.order_service.enums.PaymentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdateRequestDto {
    private PaymentStatus paymentStatus;
}