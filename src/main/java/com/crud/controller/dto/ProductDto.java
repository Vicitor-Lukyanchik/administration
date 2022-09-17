package com.crud.controller.dto;

import com.crud.entity.ProductStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class ProductDto {

    private Long id;

    @NotBlank(message = "Name can't be empty")
    @Size(max = 30, message = "Name should be less than 30")
    private String name;

    @Column(name = "price")
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus productStatus;

    private String createdAt;
}
