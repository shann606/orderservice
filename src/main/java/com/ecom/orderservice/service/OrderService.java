package com.ecom.orderservice.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.mapper.CustomMappaer;
import com.ecom.orderservice.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private CustomMappaer cMapper;

	public OrderDTO placeOrder(OrderDTO orders) throws Exception {
		// TODO Auto-generated method stub

		log.info("order creation initiated");
		if (orders.getId() == null) {

			orders.setOrderNo(orderRepo.getOrderNumber());

		}
		
		orders.setOrderPlaced(OffsetDateTime.now());
		orders.getOrderItems().stream().forEach((s) -> {
			s.setOrderPlacedOn(OffsetDateTime.now());
			
		});
		

		Order order = orderRepo.saveAndFlush(cMapper.toOrderEntity(orders));

		return cMapper.toOrderDto(order);
	}

	public OrderDTO findByOrderNo(long orderNo) throws Exception { // TODO Auto-generated method
		Order order = orderRepo.findByOrderNo(orderNo);

		return cMapper.toOrderDto(order);

	}

	public List<OrderDTO> findAllOrders() throws Exception {
	
		return cMapper.toOrdersList(orderRepo.findAll());
	}

	public OrderDTO findByOrderId(UUID id) throws Exception{
		
		log.info("getting id or nor " + orderRepo.findById(id).get().toString());
		// TODO Auto-generated method stub
		return cMapper.toOrderDto(orderRepo.findById(id).get());
	}

}
