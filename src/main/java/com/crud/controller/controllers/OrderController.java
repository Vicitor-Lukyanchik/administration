package com.crud.controller.controllers;

import com.crud.controller.dto.OrderDto;
import com.crud.entity.Order;
import com.crud.entity.Product;
import com.crud.service.OrderService;
import com.crud.service.ProductService;
import com.crud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/order")
public class OrderController {

    private final OrderService orderService;

    private final UserService userService;

    private final ProductService productService;

    @PostMapping
    public Long createOrder(@RequestBody OrderDto orderDto) {
        String[] productsId = orderDto.getProducts_id().split(" ");
        List<Product> products = new ArrayList<>();
        for (String productId : productsId){
            products.add(productService.findById(Long.parseLong(productId)));
        }
        Order order = new Order();
        order.setProducts(products);
        order.setUserId(orderDto.getUserId());
        order.setCreatedAt(orderDto.getCreatedAt());
        order.setStatus(orderDto.getStatus());
        return orderService.createOrder(order);
    }
}
