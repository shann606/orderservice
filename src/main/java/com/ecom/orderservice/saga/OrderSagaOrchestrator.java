package com.ecom.orderservice.saga;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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

	public OrderSagaOrchestrator(OrderService orderService) {
		this.orderService = orderService;
	}

	@KafkaListener(topics = "payment-result", groupId = "order-service-group")
	public void getPaymentResult(PaymentResponseEvent paymentResult, Acknowledgment ack) {
		log.info("getting call back from the payment service");

		int x = orderService.updatePaymentStatus(paymentResult);

		if (x == 1) {
			ack.acknowledge(); // manual offset commit
		}

	}

	@KafkaListener(topics = "payment-update", groupId = "order-service-group")
	public void getPaymentUpdate(PaymentResponseEvent paymentResult, Acknowledgment ack) {
		log.info("getting call back from the payment update Service Original");

		OrderStatus status;

		if (paymentResult.getPaymentStatus().equals(PaymentStatus.CANCELLED)
				|| paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)) {
			status = OrderStatus.CANCELLED;
		} else if (paymentResult.getPaymentStatus().equals(PaymentStatus.INPROGRESS)) {
			status = OrderStatus.PENDING;
		} else {
			status = OrderStatus.CONFIRMED;
		}

		int x = orderService.updatePaymentStatus(paymentResult, status);

		if (x == 1) {
			ack.acknowledge(); // manual offset commit
		}

	}

	/**
	 * just to check how consumer group works when consumes same topic with
	 * different consumenr-group and same consumer group this is for under standing
	 */

	@KafkaListener(topics = "payment-update", groupId = "order-duplicate-group")
	public void getPaymentUpdateDuplicate(PaymentResponseEvent paymentResult) {
		log.info("Getting callback from in duplicate group also ");

	}

}
