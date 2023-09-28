package com.example.service;

import com.example.dto.OrderDto;
import com.example.jpa.OrderEntity;
import com.example.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());//수량 * 단가

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);

        orderRepository.save(orderEntity);

        OrderDto returnValue = mapper.map(orderEntity, OrderDto.class);

        return returnValue;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        final OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
        OrderDto dto = new ModelMapper().map(orderEntity, OrderDto.class);
        return dto;
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {

        return orderRepository.findByUserId(userId);
    }
}