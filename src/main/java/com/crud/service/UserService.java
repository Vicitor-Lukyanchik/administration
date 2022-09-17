package com.crud.service;

import com.crud.entity.User;

import javax.validation.Valid;

public interface UserService {

    User register(User user);

    User findByUsername(String username);

    User findById(Long id);
}
