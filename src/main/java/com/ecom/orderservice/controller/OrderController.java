package com.ecom.orderservice.controller;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.dto.PaymentStatus;
import com.ecom.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/order")
	public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orders) throws Exception {

		OrderDTO order = orderService.placeOrder(orders);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/order/{id}")
				.buildAndExpand(order.getId()).toUri();
		return ResponseEntity.created(location).body(order);

	}

	@GetMapping("/allorders")
	public ResponseEntity<List<OrderDTO>> getAllOrderByOrderNo(
			@RequestParam(name = "orderNO", required = true) String orderNo) throws Exception {

		List<OrderDTO> order = orderService.findAllByOrderNo(Integer.parseInt(orderNo));

		return ResponseEntity.ok(order);

	}

	@GetMapping("/testorders")
	List<OrderDTO> getOrder() {

		List<OrderDTO> order = new ArrayList<OrderDTO>();
		List<OrderItemsDTO> orderItems = new ArrayList<OrderItemsDTO>();

		OrderItemsDTO items = OrderItemsDTO.builder().addr1("Rvam street").addr2("Brooks villa ave").city("Manhattan")
				.country("USA").customerName("Clarke").deliveryDate(OffsetDateTime.now()).build();
		orderItems.add(items);
		OrderDTO ord = OrderDTO.builder().id(UUID.randomUUID()).orderNo(68686).orderPlaced(OffsetDateTime.now())
				.paymentStatus(PaymentStatus.confirmed).orderItems(orderItems).build();
		order.add(ord);

		return order;

	}

}
