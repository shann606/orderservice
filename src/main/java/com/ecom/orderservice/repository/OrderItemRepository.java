package com.ecom.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.orderservice.entity.OrderItems;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, UUID> {
  
	
	
}
