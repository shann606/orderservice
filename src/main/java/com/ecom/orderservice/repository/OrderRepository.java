package com.ecom.orderservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	List<Order> findAllOrdersByOrderNo(int orderNo);

}
