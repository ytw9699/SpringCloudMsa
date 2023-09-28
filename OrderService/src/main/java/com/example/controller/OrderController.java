package com.example.controller;

import com.example.dto.OrderDto;
import com.example.jpa.OrderEntity;
import com.example.service.OrderService;
import com.example.vo.RequestOrder;
import com.example.vo.ResponseOrder;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
@Slf4j
public class OrderController {

    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId, @RequestBody RequestOrder requestOrder) {

        ModelMapper mapper = new ModelMapper();
                    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
                 orderDto.setUserId(userId);

        OrderDto orderResult = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(orderResult, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrdersByUserId(@PathVariable String userId) throws Exception {

        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> results = new ArrayList<>();

        orderList.forEach(o -> {
            results.add(new ModelMapper().map(o, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResponseOrder> getOrderByOrderId(@PathVariable String orderId) {
        OrderDto order = orderService.getOrderByOrderId(orderId);
        ResponseOrder result = new ModelMapper().map(order, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}