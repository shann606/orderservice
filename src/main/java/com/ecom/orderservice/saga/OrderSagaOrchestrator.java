package com.ecom.orderservice.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderSagaOrchestrator {

	private OrderService orderService;

	@Autowired
	public OrderSagaOrchestrator(OrderService orderService) {
		this.orderService = orderService;
	}

	@KafkaListener(topics = "payment-result", groupId = "order-service-group")
	public void getPaymentResult(PaymentResponseEvent paymentResult) {
		log.info("getting call back from the payment service");
		orderService.updatePaymentStatus(paymentResult);

	}

	@KafkaListener(topics = "payment-update", groupId = "order-service-group")
	public void getPaymentUpdate(PaymentResponseEvent paymentResult) {
		log.info("getting call back from the payment update Service");

		OrderStatus status;

		if (paymentResult.getPaymentStatus().equals(PaymentStatus.CANCELLED)
				|| paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)) {
			status = OrderStatus.CANCELLED;
		} else {
			status = OrderStatus.CONFIRMED;
		}

		orderService.updatePaymentStatus(paymentResult, status);

	}

}
