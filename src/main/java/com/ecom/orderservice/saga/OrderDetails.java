package com.ecom.orderservice.saga;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetails implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6418998221765778499L;
	private UUID orderId;
	private long orderNo;
	private PaymentStatus paymentStatus;
	private OrderStatus orderStatus;
	private OffsetDateTime orderCreatedOn;
	private BigDecimal amount;
	private List<OrderItemDetails> orderItems;

}
