package com.ecom.orderservice.saga.temporal.workflow;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.ecom.orderservice.dto.OrderStatus;
import com.ecom.orderservice.events.PaymentResponseEvent;
import com.ecom.orderservice.saga.OrderDetails;
import com.ecom.orderservice.saga.temporal.Activity.OrderActivity;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = "Order-Process-Queue")
public class OrderWorkFlowImpl implements OrderWorkFlow {

	private static final Logger logger = Workflow.getLogger(OrderWorkFlowImpl.class);
	private static final String WITHDRAW = "Withdraw";
	private boolean goToNextProcess = true;
	private boolean paymentProcess = true;
	private boolean paymentReceived = false;

	// RetryOptions specify how to automatically handle retries when Activities fail
	private final RetryOptions retryoptions = RetryOptions.newBuilder().setInitialInterval(Duration.ofSeconds(1)) // Wait
			.setMaximumInterval(Duration.ofSeconds(20)) // Do not exceed 20 seconds between retries
			.setBackoffCoefficient(2) // Wait 1 second, then 2, then 4, etc
			.setMaximumAttempts(2) // Fail after 5000 attempts
			.build();

	// ActivityOptions specify the limits on how long an Activity can execute before
	// being interrupted by the Orchestration service
	private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder().setRetryOptions(retryoptions)
			.setStartToCloseTimeout(Duration.ofSeconds(2)) // Max execution time for single Activity
			.setScheduleToCloseTimeout(Duration.ofSeconds(5000)) // Entire duration from scheduling to completion
																	// including queue time
			.build();

	private final Map<String, ActivityOptions> perActivityMethodOptions = new HashMap<String, ActivityOptions>() {
		{
			put(WITHDRAW, ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(5)).build());
		}
	};

	// ActivityStubs enable calls to methods as if the Activity object is local but
	// actually perform an RPC invocation

	private final OrderActivity orderAcitivityStub = Workflow.newActivityStub(OrderActivity.class,
			defaultActivityOptions, perActivityMethodOptions);

	@Override
	public void processOrder(OrderDetails orderDetails) {

		try {
			logger.info("in Workflow implementation class first call update inventory");

			orderAcitivityStub.updateProductInventory(orderDetails);
		} catch (Exception e) {
			logger.error("Error occured in updating product inventory");

			e.printStackTrace();
			goToNextProcess = false;

		}

		try {
			logger.info("in Workflow implementation class first call process payment");

			orderAcitivityStub.processPayment(orderDetails);
		} catch (Exception e) {
			logger.error("Error occured in updating payment process");
			e.printStackTrace();
			paymentProcess = false;

		}

		try {
			logger.info("in the wait block");
			Workflow.await(() -> paymentReceived);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error occured in workflow awaiting " + e.toString());
		}

		try {
			logger.info("in Workflow implementation class first call refund part");
			if (!goToNextProcess || !paymentProcess) {
				orderAcitivityStub.compensatateProductInventory(orderDetails);

				orderAcitivityStub.compenstatePaymentProcess(orderDetails);
			}
		} catch (Exception e) {
			logger.error("Error occured in calling compensation service");
			e.printStackTrace();

		}

	}

	@Override
	public void onKafkaEvent(PaymentResponseEvent payment, OrderStatus status) {

		try {
			logger.info("coming here in the onKafkaEvent method");

			int x = orderAcitivityStub.updateOrderPayment(payment,status);
			paymentReceived = true;
			if (x == 1) {
				logger.info("Payment details are updated from paymentservice  order no" + payment.getOrderNo());
			}

		} catch (Exception e) {
			logger.error("Error occured in while getting back events from payment service");
			e.printStackTrace();
			goToNextProcess = false;
		}

	}

}
