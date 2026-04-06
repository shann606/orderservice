package com.ecom.orderservice.saga.temporal.workflow;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.saga.OrderDetails;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkFlow {
	
	@WorkflowMethod
	void processOrder(OrderDetails orderDetails);
	
	
	@SignalMethod
    void onKafkaEvent(PaymentResponseEvent payment, OrderStatus status);

}
