package com.ecom.orderservice.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "online_order")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	
    @Generated(value = GenerationTime.INSERT)
	@Column(name = "order_no" ,columnDefinition = " NUMBER(19,0) DEFAULT ON NULL online_order_no_seq.nextval", insertable = false)
     private int orderNo;
	
	@Column(name = "payment_status")
	private PaymentStatus paymentStatus;
	
	
	@Column(name = "order_status")
	private OrderStatus orderStatus;
	
	
	@Column(name = "order_placed_on" , columnDefinition = "date")
	private OffsetDateTime orderPlaced;
	
	@OneToMany(cascade = CascadeType.ALL , orphanRemoval = true)
	@JoinColumn(name = "order_id")
	private List<OrderItems> orderItems;

}
