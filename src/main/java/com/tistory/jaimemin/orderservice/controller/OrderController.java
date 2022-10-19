package com.tistory.jaimemin.orderservice.controller;

import com.tistory.jaimemin.orderservice.dto.OrderDto;
import com.tistory.jaimemin.orderservice.entity.OrderEntity;
import com.tistory.jaimemin.orderservice.service.OrderService;
import com.tistory.jaimemin.orderservice.vo.RequestOrder;
import com.tistory.jaimemin.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    private final Environment environment;

    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId
            , @RequestBody RequestOrder order) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        ResponseOrder responseOrder = modelMapper.map(orderService.createOrder(orderDto), ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
