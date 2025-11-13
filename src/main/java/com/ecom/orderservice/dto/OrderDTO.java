package com.ecom.orderservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1595612532622574461L;

	private UUID id;
	private long orderNo;
	private PaymentStatus paymentStatus;
	private OrderStatus orderStatus;
	private String failedReason;
	private OffsetDateTime orderPlaced;
	private BigDecimal totalAmount;
	private List<OrderItemsDTO> orderItems;


}
