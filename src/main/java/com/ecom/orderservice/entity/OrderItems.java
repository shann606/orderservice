package com.ecom.orderservice.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ecom.orderservice.dto.ShippingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItems {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@Column(name = "order_id" , nullable = false , updatable = false)
	private UUID orderId;
	
	@Column(name = "customer_id")
	private UUID customerId;
	
	@Column(name = "customer_name")
	private String customerName;
	
	@Column(name = "shipping_addr_id")
	private UUID shippingAddresId;
	
	
	private String addr1;
	
	private String addr2;
	
	private String city;
	
	private String state;
	
	private String country;
	
	@Column(name = "shipping_status")
	private ShippingStatus shippingStatus;
	
	
	@Column(name = "deliver_date" , columnDefinition = "date")
	private OffsetDateTime deliveryDate;
	
	@Column(name = "product_id")
	private UUID productId;
	
	@Column(name = "order_by")
	private String orderBy;
	
	@Column(name = "order_placed_on" , columnDefinition = "date")
	private OffsetDateTime orderPlacedOn;

}
