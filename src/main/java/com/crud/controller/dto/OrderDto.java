package com.crud.controller.dto;

import lombok.Data;

@Data
public class OrderDto {

    private Long id;

    private Long userId;

    private String status;

    private String createdAt;

    private String products_id;
}
