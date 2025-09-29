package com.ecom.orderservice.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "online_order") // Avoid using reserved names like "user"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "order_no", updatable = false)
	private long orderNo;

	@Column(name = "payment_status")
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column(name = "order_status")
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(name = "order_placed_on", columnDefinition = "date")
	private OffsetDateTime orderPlaced;

	@OneToMany(orphanRemoval = true , cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id", nullable = false)
	private List<OrderItems> orderItems;
	
	@PrePersist
	private void generatedId() {
		if(id == null || id.equals("")) {
			id=UUID.randomUUID();
		}
	}

}
