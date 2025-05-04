package com.msa.yt.order_service.service;

import com.msa.yt.order_service.client.InventoryClient;
import com.msa.yt.order_service.dto.OrderRequest;
//import com.msa.yt.order_service.event.OrderPlacedEvent;
import com.msa.yt.order_service.event.OrderPlacedEventAvro;
import com.msa.yt.order_service.model.Order;
import com.msa.yt.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//@Slf4j
@Service
//@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
//    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
private final KafkaTemplate<String, OrderPlacedEventAvro> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

//    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
//        this.orderRepository = orderRepository;
//        this.inventoryClient = inventoryClient;
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate<String, OrderPlacedEventAvro> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void placeOrder(OrderRequest orderRequest) {
        // 1. Using Mockito
        // 2. Use Wiremock
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            // map OrderRequest to Order object
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            // save order to OrderRepository
            orderRepository.save(order);

            // Send the message to Kafka topic
//            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), orderRequest.userDetails().email());
            OrderPlacedEventAvro orderPlacedEvent = new OrderPlacedEventAvro();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            // orderNumber, email ---> Email
        }else {
            throw new RuntimeException("Product with SkuCode "+ orderRequest.skuCode() + " is not in stock");
        }
    }
}
