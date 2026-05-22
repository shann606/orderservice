package com.ecom.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	private MockMvc mockMvc;

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderController orderController;

	private static OrderDTO ord;

	private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

	static List<OrderDTO> allOrders;

	@BeforeAll
	static void init() {

		List<OrderItemsDTO> orderItems = new ArrayList<>();
		allOrders = new ArrayList<>();

		OrderItemsDTO items = OrderItemsDTO.builder().addr1("Rvam street").addr2("Brooks villa ave").city("Manhattan")
				.country("USA").customerName("Clarke").deliveryDate(OffsetDateTime.parse("2026-05-14T10:15:30+00:00"))
				.build();

		orderItems.add(items);

		ord = OrderDTO.builder().id(UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b")).orderNo(68686)
				.orderPlaced(OffsetDateTime.parse("2026-05-14T10:15:30+00:00")).paymentStatus(PaymentStatus.OPEN)
				.orderItems(orderItems).build();
		allOrders.add(ord);

	}

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
	}

	@Test
	void testplaceOrder() throws Exception {

		when(orderService.placeOrder(any())).thenReturn(ord);

		mockMvc.perform(post("/api/v1/orders/order").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ord))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderNo").value(68686));

		verify(orderService, times(1)).placeOrder(any());
	}

	@Test
	final void testGetAllOrderByOrderId() throws Exception {

		UUID id = UUID.fromString("b516f577-11da-424e-9ad0-bc23ab15df1b");
		when(orderService.findByOrderId(any(UUID.class))).thenReturn(ord);

		mockMvc.perform(get("/api/v1/orders/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(ord))).andExpect(status().isOk())
				.andExpect(jsonPath("$.orderNo").value(68686));

		verify(orderService, times(1)).findByOrderId(any());

	}

	@Test
	final void testFindByOrderNo() throws Exception {

		when(orderService.findByOrderNo(any(Long.class))).thenReturn(ord);

		mockMvc.perform(get("/api/v1/orders/byorderno").param("orderno", String.valueOf(68686l)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.orderNo").value(68686));

		verify(orderService, times(1)).findByOrderNo(68686l);

	}

	@Test
	final void testGetAllOrder() throws Exception {

		when(orderService.findAllOrders()).thenReturn(allOrders);

		mockMvc.perform(get("/api/v1/orders")).andExpect(status().isOk())
		.andReturn();

		verify(orderService, times(1)).findAllOrders();

	}

}
