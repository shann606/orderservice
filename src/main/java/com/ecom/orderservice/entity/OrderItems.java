package com.ecom.orderservice.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ecom.orderservice.dto.ShippingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "online_order_items") // Avoid using reserved names like "user"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItems {
	
	
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@Column(name = "order_id" , nullable = false, insertable = false, updatable = false)
	private UUID orderId;
	
	@Column(name = "customer_id")
	private UUID customerId;
	
	@Column(name = "customer_name")
	private String customerName;
	
	@Column(name = "shipping_addr_id")
	private UUID shippingAddresId;
	
	@Column(name="addr1")
	private String addr1;
	
	@Column(name="addr2")
	private String addr2;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="country")
	private String country;
	
	@Column(name = "shipping_status")
	@Enumerated(EnumType.STRING)
	private ShippingStatus shippingStatus;
	
	
	@Column(name = "deliver_date" , columnDefinition = "date")
	private OffsetDateTime deliveryDate;
	
	@Column(name = "product_id")
	private UUID productId;
	
	@Column(name = "order_by")
	private String orderBy;
	
	@Column(name = "order_placed_on" , columnDefinition = "date")
	private OffsetDateTime orderPlacedOn;
	
	@PrePersist
	private void generatedId() {
		if(id == null ) {
			id=UUID.randomUUID();
		}
	}

}
