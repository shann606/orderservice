package com.ecom.orderservice.service;

import java.util.List;

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

		Order order = orderRepo.saveAndFlush(cMapper.toOrderEntity(orders));

		return cMapper.toOrderDto(order);
	}

	public List<OrderDTO> findAllByOrderNo(int orderNo) {
		// TODO Auto-generated method stub
		
	   List<Order> orders =orderRepo.findAllOrdersByOrderNo(orderNo);
		return cMapper.toOrdersList(orders);
	}

}
