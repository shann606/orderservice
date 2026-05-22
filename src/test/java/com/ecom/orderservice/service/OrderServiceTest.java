/**
 * 
 */
package com.ecom.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItems;
import com.ecom.orderservice.mapper.CustomMapper;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.saga.temporal.workflow.OrderWorkFlow;
import com.ecom.orderservice.saga.temporal.workflow.OrderWorkFlowImpl;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;

@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(partitions = 1, topics = "test-order")
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepo;
	@Mock
	private CustomMapper cMapper;
	@Mock
	private WorkflowClient workFlowClient;
	@InjectMocks
	private OrderService orderService;
	@Mock
	private OrderWorkFlow orderWorkFlow;

	private static OrderDTO ord;

	private static List<OrderDTO> allOrders;

	private static Order orderE;
	private static List<Order> allEOrders;

	private static UUID id;

	@BeforeAll
	static final void startTestServer() {

		TestWorkflowEnvironment testEnv = TestWorkflowEnvironment.newInstance();
		Worker worker = testEnv.newWorker("test-order-queue");
		worker.registerWorkflowImplementationTypes(OrderWorkFlowImpl.class);

		testEnv.start();
	}

	@BeforeAll
	static final void init() {

		id = UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b");

		List<OrderItemsDTO> orderItems = new ArrayList<>();
		allOrders = new ArrayList<>();

		OrderItemsDTO items = OrderItemsDTO.builder().addr1("Rvam street").addr2("Brooks villa ave").city("Manhattan")
				.country("USA").customerName("Clarke").deliveryDate(OffsetDateTime.parse("2026-05-14T10:15:30+00:00"))
				.build();

		orderItems.add(items);

		ord = OrderDTO.builder().id(id).orderNo(68686).orderPlaced(OffsetDateTime.parse("2026-05-14T10:15:30+00:00"))
				.paymentStatus(PaymentStatus.OPEN).totalAmount(new BigDecimal(120000)).orderItems(orderItems).build();
		allOrders.add(ord);

		List<OrderItems> Eitems = new ArrayList<>();
		allEOrders = new ArrayList<>();
		OrderItems oItems = OrderItems.builder().addr1("Rvam street").addr2("Brooks villa ave").city("Manhattan")
				.country("USA").customerName("Clarke").deliveryDate(OffsetDateTime.parse("2026-05-14T10:15:30+00:00"))
				.build();

		Eitems.add(oItems);

		orderE = Order.builder().id(id).orderNo(68686).orderPlaced(OffsetDateTime.parse("2026-05-14T10:15:30+00:00"))
				.paymentStatus(PaymentStatus.OPEN).totalAmount(new BigDecimal(120000)).orderItems(Eitems).build();
		allEOrders.add(orderE);

	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	void initiateEntity() {
		lenient().when(orderRepo.saveAndFlush(cMapper.toOrderEntity(any(OrderDTO.class)))).thenReturn(orderE);

		lenient().when(orderRepo.findById(any(UUID.class))).thenReturn(Optional.of(orderE));

		lenient().when(orderRepo.findByOrderNo(any(Long.class))).thenReturn(orderE);

		lenient().when(cMapper.toOrderDto(any(Order.class))).thenReturn(ord);

		lenient().when(cMapper.toOrdersList(any(List.class))).thenReturn(allOrders);

	}

	@Test
	void testplaceOrder() {

		lenient().when(workFlowClient.newWorkflowStub(OrderWorkFlow.class,
				WorkflowOptions.newBuilder().setTaskQueue("Order-Process-Queue")
						.setWorkflowId("order-workflow-" + orderE.getOrderNo()) // Unique workflow ID
						.build()))
				.thenReturn(orderWorkFlow);

		// action
		OrderDTO order = orderService.placeOrder(ord);

		assertEquals(68686, order.getOrderNo());
		assertNotNull(order);

		verify(orderRepo, times(1)).saveAndFlush(cMapper.toOrderEntity(ord));

	}

	@Test
	final void testfindByOrderNo() {

		// action
		OrderDTO order = orderService.findByOrderNo(68686);

		assertEquals(id, order.getId());
		assertNotNull(order);

		verify(orderRepo, times(1)).findByOrderNo(68686);

	}

	@Test
	final void testfindAllOrders() {

		List<OrderDTO> orders = orderService.findAllOrders();

		assertNotNull(orders);
		verify(orderRepo, times(1)).findAll();

	}

	@Test
	final void testfindByOrderId() throws Exception {

		OrderDTO order = orderService.findByOrderId(id);

		assertEquals(id, order.getId());
		assertNotNull(order);

		verify(orderRepo, times(1)).findById(id);

	}

}
