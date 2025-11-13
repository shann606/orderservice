package com.ecom.orderservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsDTO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 6280914681320246822L;
	
	private UUID id;
	private UUID orderId;
	private UUID customerId;
	private String customerName;
	private UUID shippingAddresId;
	private BigDecimal itemAmount;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String country;
	private ShippingStatus shippingStatus;
	private OffsetDateTime deliveryDate;
	private UUID productId;
	private String orderBy;
	private OffsetDateTime orderPlacedOn;
	
	
	
	
	
}