package com.crud.service.impl;

import com.crud.entity.Product;
import com.crud.entity.ProductStatus;
import com.crud.repository.ProductRepository;
import com.crud.service.ProductService;
import lombok.RequiredArgsConstructor;
import com.crud.service.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        product.setProductStatus(ProductStatus.randomStatus());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()){
            throw new ServiceException("There is not product with id : " + id);
        }
        if (product.get().getProductStatus() == ProductStatus.IN_STOCK ||
        product.get().getProductStatus() == ProductStatus.RUNNING_LOW){
            throw new ServiceException("This product not in status out_of_stock");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).get();
    }


}
