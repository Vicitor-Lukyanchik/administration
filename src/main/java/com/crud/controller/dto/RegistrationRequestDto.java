package com.crud.controller.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RegistrationRequestDto {

    @Size(min = 8, max = 50, message = "Username must be from 8 to 50")
    private String username;

    @Size(min = 8, max = 50, message = "Password must be from 8 to 50")
    private String password;
}
