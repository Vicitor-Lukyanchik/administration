package com.crud.service.impl;

import com.crud.entity.Order;
import com.crud.entity.Product;
import com.crud.entity.ProductStatus;
import com.crud.repository.OrderRepository;
import com.crud.service.OrderService;
import com.crud.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;

    @Override
    public Long createOrder(Order order) {
        if(order.getProducts().isEmpty()){
            throw new ServiceException("Order without products");
        }
        for(Product product : order.getProducts()){
            if (product.getProductStatus().equals(ProductStatus.OUT_OF_STOCK)){
                throw new ServiceException("Product by id : " + product.getId() + " in status OUT_OF_STOCK");
            }
        }
        if (order.getUser_id() == 0){
            throw new ServiceException("user_id do not exist");
        }
        return orderRepository.save(order).getId();
    }
}
