package com.ecom.orderservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ecom.orderservice.dto.OrderDTO;
import com.ecom.orderservice.dto.OrderItemsDTO;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItems;

@Mapper
public interface CustomMappaer {

	OrderDTO toOrderDto(Order order);

	Order toOrderEntity(OrderDTO orderDto);
	
	OrderItemsDTO toOrderItemDTO(OrderItems items);

	List<OrderDTO> toOrdersList(List<Order> orders);

}
