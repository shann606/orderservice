package com.ecom.orderservice.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.dto.ShippingStatus;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.events.OrderCreatedEvent;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.mapper.CustomMappaer;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.saga.OrderDetails;
import com.ecom.orderservice.saga.OrderItemDetails;
import com.ecom.orderservice.saga.temporal.workflow.OrderWorkFlow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConfigurationProperties(prefix = "service.topic.order")
public class OrderService {

	private OrderRepository orderRepo;

	private CustomMappaer cMapper;

	private final WorkflowClient workFlowClient;
	private final String taskQueueName ="Order-Process-Queue";
	
	
	public OrderService(OrderRepository orderRepo, CustomMappaer cMapper, WorkflowClient workFlowClient) {
		this.orderRepo = orderRepo;
		this.cMapper = cMapper;
		this.workFlowClient = workFlowClient;

	}

	@Transactional(rollbackFor = Exception.class)
	public OrderDTO placeOrder(OrderDTO orders)  {
		Order order;

		
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

		
		// need to invoke temporal service to call workflow to process
		
		OrderDetails orderDetails = getOrderDetails(order);
		
		WorkflowOptions options = WorkflowOptions.newBuilder()
		        .setTaskQueue(taskQueueName)
		        .setWorkflowId("order-workflow-"+order.getOrderNo()) // Unique workflow ID
		        .build();
		
		OrderWorkFlow workFlow = workFlowClient.newWorkflowStub(OrderWorkFlow.class, options);

		// Start the workflow execution asynchronously using WorkflowClient.start()
		// This call returns immediately and the workflow runs in the background
		WorkflowClient.start(workFlow::processOrder, orderDetails);

      log.info("WorkFlow id :::: for the order no :::" + order.getOrderNo() + "" + options.getWorkflowId());

      order=orderRepo.findById(order.getId()).get();

		return cMapper.toOrderDto(order);
	}

	private OrderDetails getOrderDetails(Order orders) {

		return new OrderDetails(orders.getId(), orders.getOrderNo(), orders.getPaymentStatus(), orders.getOrderStatus(),
				orders.getOrderPlaced(), orders.getTotalAmount(),
				orders.getOrderItems().stream()
						.map(x -> new OrderItemDetails(x.getId(), x.getOrderId(), x.getProductId(), x.getQuantity()))
						.toList());

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
			log.error("update payment status error"+ e);
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
		log.error("update payment status error"+ e);
			
			throw e;
		}
		return i;

	}

	@Transactional(rollbackFor = Exception.class)
	public void cancelOrder(UUID orderId) {

		log.info("Cancelling the order " + orderId);

		Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order id not found"));
		order.setOrderStatus(OrderStatus.CANCELLED);
		order.setPaymentStatus(PaymentStatus.CANCELLED);
		order.getOrderItems().forEach(x -> {
			x.setShippingStatus(ShippingStatus.CANCELLED);
		});

	}

}
