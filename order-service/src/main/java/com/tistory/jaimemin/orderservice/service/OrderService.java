package com.tistory.jaimemin.orderservice.service;

import com.tistory.jaimemin.orderservice.dto.OrderDto;
import com.tistory.jaimemin.orderservice.entity.OrderEntity;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderByOrderId(String orderId);

    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
