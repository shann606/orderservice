package com.ecom.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.orderservice.entity.OrderItems;

public interface OrderItemRepository extends JpaRepository<OrderItems, UUID> {

}
