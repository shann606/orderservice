package com.ecom.orderservice.saga.temporal.Activity;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.saga.OrderDetails;

import io.temporal.activity.ActivityInterface;

@ActivityInterface(namePrefix = "process_")
public interface OrderActivity {

	void updateProductInventory(OrderDetails order);

	void processPayment(OrderDetails order);

	int updateOrderPayment(PaymentResponseEvent payment, OrderStatus status);

	void compensatateProductInventory(OrderDetails order);

	void compenstatePaymentProcess(OrderDetails order);

}
