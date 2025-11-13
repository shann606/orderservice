package com.ecom.orderservice.events;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data

@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

	private long orderNo;
	private OffsetDateTime orderCreatedOn;
	private BigDecimal amount;

}
