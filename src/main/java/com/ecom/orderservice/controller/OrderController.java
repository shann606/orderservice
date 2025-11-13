package com.ecom.orderservice.controller;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order")
@Slf4j
public class OrderController {

	private OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/placeorder")
	public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orders) throws Exception {

		OrderDTO order = orderService.placeOrder(orders);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/order/{id}")
				.buildAndExpand(order.getId()).toUri();
		return ResponseEntity.created(location).body(order);

	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderDTO> getAllOrderByOrderNo(@PathVariable(name = "id") UUID id) throws Exception {

		return ResponseEntity.ok(orderService.findByOrderId(id));

	}

	@GetMapping("/searchorder")
	public ResponseEntity<OrderDTO> findByOrderNo(@RequestParam(name = "orderno", required = true) long orderno)
			throws Exception {

	
		return ResponseEntity.ok(orderService.findByOrderNo(orderno));

	}

	@GetMapping("/allorders")
	public ResponseEntity<List<OrderDTO>> getAllOrder() {

		return ResponseEntity.ok(orderService.findAllOrders());

	}

	@GetMapping("/testorders")
	List<OrderDTO> getOrder() {

		List<OrderDTO> order = new ArrayList<>();
		List<OrderItemsDTO> orderItems = new ArrayList<>();

		OrderItemsDTO items = OrderItemsDTO.builder().addr1("Rvam street").addr2("Brooks villa ave").city("Manhattan")
				.country("USA").customerName("Clarke").deliveryDate(OffsetDateTime.now()).build();
		orderItems.add(items);
		OrderDTO ord = OrderDTO.builder().id(UUID.randomUUID()).orderNo(68686).orderPlaced(OffsetDateTime.now())
				.paymentStatus(PaymentStatus.OPEN).orderItems(orderItems).build();
		order.add(ord);

		return order;

	}

}
