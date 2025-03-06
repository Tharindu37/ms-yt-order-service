package com.msa.yt.order_service.service;

import com.msa.yt.order_service.client.InventoryClient;
import com.msa.yt.order_service.dto.OrderRequest;
import com.msa.yt.order_service.model.Order;
import com.msa.yt.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
//@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
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
        }else {
            throw new RuntimeException("Product with SkuCode "+ orderRequest.skuCode() + " is not in stock");
        }
    }
}
