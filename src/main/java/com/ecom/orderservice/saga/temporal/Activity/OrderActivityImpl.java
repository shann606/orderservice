package com.ecom.orderservice.saga.temporal.Activity;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.events.OrderCreatedEvent;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.saga.OrderDetails;
import com.ecom.orderservice.service.OrderService;

import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;

@Component
@ActivityImpl(taskQueues = "Order-Process-Queue")
public class OrderActivityImpl implements OrderActivity {

	private static final Logger logger = Workflow.getLogger(OrderActivityImpl.class);

	private final RestTemplate restTemplate;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	private OrderService orderService;

	public OrderActivityImpl(RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate,
			OrderService orderService) {
		this.restTemplate = restTemplate;
		this.kafkaTemplate = kafkaTemplate;
		this.orderService = orderService;
	}

	@Override
	public void updateProductInventory(OrderDetails order) {
		logger.info("in update invertory method " + order.toString());

		Map<UUID, Integer> data = new HashMap<>();
		order.getOrderItems().forEach(x -> {
			data.put(x.getProductId(), x.getQuantity());

		});

		URI url = UriComponentsBuilder.fromUriString("http://productservice:8080/api/v1/categories/products/quantity")
				.queryParam("action", "order").build().toUri();
		HttpEntity<Map<UUID, Integer>> reqData = new HttpEntity<>(data);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, reqData, String.class);

		logger.error("Product update status -->" + response.getStatusCode());

		if (!response.getStatusCode().equals(HttpStatus.OK))
			throw new RuntimeException("Error occured at product service");

	}

	@Override
	public void processPayment(OrderDetails order) {
		OrderCreatedEvent orderEvent;
		logger.info("inprocessPayment Method " + order.getOrderNo());

		orderEvent = new OrderCreatedEvent(order.getOrderNo(), order.getOrderCreatedOn(), order.getAmount());
		logger.info("Order created and hitting kafka queue");

		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("order-created", orderEvent);
		future.whenComplete((result, ex) -> {

			if (ex == null) {
				logger.info("Order created details sent to kafka " + result.getRecordMetadata().offset());

			} else {
				logger.error("Exception occureing during message delivery :: " + ex);
				throw new RuntimeException(ex.getMessage());
			}

		});

		logger.info("Order details sent to payment gateway");

	}

	@Override
	public int updateOrderPayment(PaymentResponseEvent payment, OrderStatus status) {
		logger.info("getting call back from the payment update Service Original");

		return orderService.updatePaymentStatus(payment, status);

	}

	@Override
	public void compensatateProductInventory(OrderDetails order) {
		logger.info("in compensatateProductInventory method " + order.toString());

		// updating cancelled status to order and orderItems
		orderService.cancelOrder(order.getOrderId());

	}

	@Override
	public void compenstatePaymentProcess(OrderDetails order) {

		logger.info("need up cancel the payment and calling payment service for order " + order.getOrderNo());

		URI url = UriComponentsBuilder.fromUriString("http://paymentservice:8083/api/v1/payments/update")
				.queryParam("orderno", order.getOrderNo()).queryParam("status", PaymentStatus.CANCELLED)
				.queryParam("reason", "order has issue").build().toUri();

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

		logger.error("Payment update status -->" + response.getStatusCode());

		if (!response.getStatusCode().equals(HttpStatus.ACCEPTED))
			throw new RuntimeException("Error occured at Payment service");

	}

}
