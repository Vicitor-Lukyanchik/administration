package com.crud.controller.controllers;


import com.crud.controller.dto.ProductDto;
import com.crud.entity.Product;
import com.crud.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductService productService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss", Locale.getDefault());

    @PostMapping
    public Product createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = new Product();
        product.setCreatedAt(LocalDateTime.parse(productDto.getCreatedAt(), formatter));
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        return productService.createProduct(product);
    }

    @PutMapping
    public Product updateProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setCreatedAt(LocalDateTime.parse(productDto.getCreatedAt(), formatter));
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        return productService.updateProduct(product);
    }

    @DeleteMapping
    public Boolean deleteProduct(@RequestParam Long id){
        productService.deleteProduct(id);
        return true;
    }
}
