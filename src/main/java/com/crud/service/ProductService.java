package com.crud.service;

import com.crud.entity.Product;

public interface ProductService {

    Product createProduct(Product product);
    Product updateProduct(Product product);

    void deleteProduct(Long id);

    Product findById(Long id);
}
