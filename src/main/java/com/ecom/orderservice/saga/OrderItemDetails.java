package com.ecom.orderservice.saga;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6192907484070277900L;
	private UUID orderItemId;
	private UUID orderId;
	private UUID productId;
	private int quantity;

}
