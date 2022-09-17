package com.crud.controller.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorDetails {
    private HttpStatus status;
    private long timestamp;
    private List<String> errors;
}
