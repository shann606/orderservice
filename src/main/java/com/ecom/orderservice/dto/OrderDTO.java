package com.ecom.orderservice.dto;

import java.io.Serializable;
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
	private int orderNo;
	private PaymentStatus paymentStatus;
	private OrderStatus orderStatus;
	private OffsetDateTime orderPlaced;
	private List<OrderItemsDTO> orderItems;

}
