package com.ecom.orderservice.events;

import com.ecom.orderservice.dto.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PaymentResponseEvent {
	private long orderNo;
	private PaymentStatus paymentStatus;
	private String reason;

}
