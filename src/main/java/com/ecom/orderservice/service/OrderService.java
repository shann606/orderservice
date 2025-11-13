package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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
public class OrderService {

	private OrderRepository orderRepo;

	private CustomMappaer cMapper;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public OrderService(OrderRepository orderRepo, CustomMappaer cMapper, KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.orderRepo = orderRepo;
		this.cMapper = cMapper;
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

			kafkaTemplate.send("order-created", orderEvent);

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


	public void updatePaymentStatus(PaymentResponseEvent paymentResult) {
		log.info("updating the payment status in order table"+ paymentResult.toString());
		int i=orderRepo.updatePaymentStatus(paymentResult.getOrderNo(), paymentResult.getPaymentStatus(),
				paymentResult.getReason());
		if(i==1) {
		log.info("Payment status successfull updated in Order table");
		}

	}
	
	
	public void updatePaymentStatus(PaymentResponseEvent paymentResult,OrderStatus orderStatus) {
		log.info("updating the payment status in order table"+ paymentResult.toString());
		int i=orderRepo.updatePaymentStatus(paymentResult.getOrderNo(), paymentResult.getPaymentStatus(),
				paymentResult.getReason(),orderStatus);
		if(i==1) {
		log.info("Payment Update status successfull updated in Order table");
		}

	}

}
