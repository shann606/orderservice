package com.ecom.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecom.orderservice.entity.Order;


@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	
	@Query(value = "SELECT o FROM Order o JOIN o.orderItems i WHERE o.orderNo = :orderNo and o.id=i.orderId" )
	 Order findByOrderNo(long orderNo); 
	
	@Query(nativeQuery = true , value = "select ONLINE_ORDER_NO_SEQ.nextval from dual")
	long getOrderNumber();

}
