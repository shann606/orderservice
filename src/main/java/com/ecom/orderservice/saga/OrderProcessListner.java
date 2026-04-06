package com.ecom.orderservice.saga;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.saga.temporal.workflow.OrderWorkFlow;
import com.ecom.orderservice.service.OrderService;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderProcessListner {

	private OrderService orderService;
	private WorkflowClient workFlowClient;

	public OrderProcessListner(OrderService orderService, WorkflowClient workFlowClient) {
		this.orderService = orderService;
		this.workFlowClient = workFlowClient;

	}

	/***
	 * Not suing this one
	 * 
	 * @param paymentResult
	 * @param ack
	 */
	/*
	 * @KafkaListener(topics = "${service.topic.payment.result}", groupId =
	 * "order-service-group") public void getPaymentResult(PaymentResponseEvent
	 * paymentResult, Acknowledgment ack) {
	 * log.info("getting call back from the payment service");
	 * 
	 * int x = orderService.updatePaymentStatus(paymentResult);
	 * 
	 * if (x == 1) { ack.acknowledge(); // manual offset commit }
	 * 
	 * }
	 * 
	 */
	@KafkaListener(topics = "${service.topic.payment.result}", groupId = "order-service-group")
	public void getPaymentResult(PaymentResponseEvent paymentResult, Acknowledgment ack) {

		try {
			String workFlowId = "order-workflow-" + paymentResult.getOrderNo();
			OrderStatus status;

			if (paymentResult.getPaymentStatus().equals(PaymentStatus.CANCELLED)
					|| paymentResult.getPaymentStatus().equals(PaymentStatus.FAILED)) {
				status = OrderStatus.CANCELLED;
				paymentResult.setReason("Payment is Failed or Cancelled");
			} else if (paymentResult.getPaymentStatus().equals(PaymentStatus.INPROGRESS)) {
				status = OrderStatus.PENDING;
				paymentResult.setReason("order in progress");
			} else {
				status = OrderStatus.CONFIRMED;
				paymentResult.setReason("order is confirmed");
			}
           

			OrderWorkFlow workFlow = workFlowClient.newWorkflowStub(OrderWorkFlow.class, workFlowId);
			workFlow.onKafkaEvent(paymentResult,status);

			log.info("called signal method ::");

			ack.acknowledge(); // manual offset commit

		} catch (Exception e) {
			log.error("error occured in calling signal method ::" + e.getMessage());
			e.printStackTrace();
		}

	}

	@KafkaListener(topics = "${service.topic.payment.update}", groupId = "order-service-group")
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

	@KafkaListener(topics = "${service.topic.payment.update}", groupId = "order-duplicate-group")
	public void getPaymentUpdateDuplicate(PaymentResponseEvent paymentResult) {
		log.info("Getting callback from in duplicate group also ");

	}

}
