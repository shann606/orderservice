package com.ecom.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.entity.Order;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query(value = "SELECT o FROM Order o JOIN o.orderItems i WHERE o.orderNo = :orderNo and o.id=i.orderId")
	Order findByOrderNo(long orderNo);

	@Query(nativeQuery = true, value = "select ONLINE_ORDER_NO_SEQ.nextval from dual")
	long getOrderNumber();

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.paymentStatus = :paymentStatus, o.failedReason= :reason WHERE o.orderNo = :orderNo")
	int updatePaymentStatus(@Param("orderNo") long orderNo, @Param("paymentStatus") PaymentStatus paymentStatus,
			@Param("reason") String reason);

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.paymentStatus = :paymentStatus,o.orderStatus=:orderStatus , o.failedReason= :reason WHERE o.orderNo = :orderNo")
	int updatePaymentStatus(long orderNo, PaymentStatus paymentStatus, String reason, OrderStatus orderStatus);

}
