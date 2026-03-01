package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.events.OrderCreatedEvent;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.mapper.CustomMappaer;
import com.ecom.orderservice.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConfigurationProperties(prefix = "service.topic.order")
public class OrderService {

	private OrderRepository orderRepo;

	private CustomMappaer cMapper;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private String created;

	public OrderService(OrderRepository orderRepo, CustomMappaer cMapper, KafkaTemplate<String, Object> kafkaTemplate) {
		this.orderRepo = orderRepo;
		this.cMapper = cMapper;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Transactional(rollbackOn = Exception.class)
	public OrderDTO placeOrder(OrderDTO orders) throws Exception {
		Order order;

		try {
			OrderCreatedEvent orderEvent;

			log.info("order creation initiated");
			if (orders.getId() == null) {

				orders.setOrderNo(orderRepo.getOrderNumber());

			}

			orders.setOrderPlaced(OffsetDateTime.now());
			orders.getOrderItems().stream().forEach(s -> s.setOrderPlacedOn(OffsetDateTime.now())

			);
			BigDecimal totalAmount = orders.getOrderItems().stream().map(OrderItemsDTO::getItemAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			orders.getOrderItems().stream().forEach(s -> totalAmount.add(s.getItemAmount()));
			orders.setTotalAmount(totalAmount);

			order = orderRepo.saveAndFlush(cMapper.toOrderEntity(orders));

			orderEvent = new OrderCreatedEvent(order.getOrderNo(), order.getOrderPlaced(), order.getTotalAmount());
			log.info("Order created and hitting kafka queue");

			CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(created, orderEvent);
			future.whenComplete((result, ex) -> {

				if (ex == null) {
					log.info("Order created details sent to kafka ");

				} else {
					log.info("Excepion occured the details :" + ex.toString());
				}

			});

			log.info("Order details sent to payment gateway");

		} catch (Exception e) {
			log.info("Rollback initilized");
			throw e;
		}

		return cMapper.toOrderDto(order);
	}

	public OrderDTO findByOrderNo(long orderNo) {
		Order order = orderRepo.findByOrderNo(orderNo);

		return cMapper.toOrderDto(order);

	}

	public List<OrderDTO> findAllOrders() {

		return cMapper.toOrdersList(orderRepo.findAll());
	}

	public OrderDTO findByOrderId(UUID id) throws Exception {

		log.info("getting id or nor " + orderRepo.findById(id).get().toString());

		return cMapper.toOrderDto(orderRepo.findById(id).get());
	}

	public int updatePaymentStatus(PaymentResponseEvent paymentResult) {
		log.info("updating the payment status in order table" + paymentResult.toString());
		int i = 0;
		try {
			i = orderRepo.updatePaymentStatus(paymentResult.getPaymentStatus(), paymentResult.getReason(),
					paymentResult.getOrderNo());
			if (i == 1) {
				log.info("Payment status successfull updated in Order table");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return i;
	}

	public int updatePaymentStatus(PaymentResponseEvent paymentResult, OrderStatus orderStatus) {
		log.info("updating the payment status in order table" + paymentResult.toString());
		int i = 0;
		try {
			i = orderRepo.updatePaymentStatus(paymentResult.getPaymentStatus(), paymentResult.getReason(), orderStatus,
					paymentResult.getOrderNo());
			if (i == 1) {
				log.info("Payment Update status successfull updated in Order table");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return i;

	}

}
