package com.tistory.jaimemin.orderservice.controller;

import com.tistory.jaimemin.orderservice.dto.OrderDto;
import com.tistory.jaimemin.orderservice.entity.OrderEntity;
import com.tistory.jaimemin.orderservice.messagequeue.KafkaProducer;
import com.tistory.jaimemin.orderservice.messagequeue.OrderProducer;
import com.tistory.jaimemin.orderservice.service.OrderService;
import com.tistory.jaimemin.orderservice.vo.RequestOrder;
import com.tistory.jaimemin.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

    private final Environment environment;

    private final OrderService orderService;

    private final KafkaProducer kafkaProducer;

    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId
            , @RequestBody RequestOrder order) {
        log.info("Before adding order data");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        ResponseOrder responseOrder = modelMapper.map(orderService.createOrder(orderDto), ResponseOrder.class);
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(order.getQuantity() * order.getUnitPrice());
//
//        // send this order to the kafka
//        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);
//
//        ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);
        log.info("After adding order data");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {
        log.info("Before retrieving orders data");
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        log.info("After retrieving orders data");

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
