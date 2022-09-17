package com.crud.service.impl;

import com.crud.entity.Role;
import com.crud.entity.UserStatus;
import com.crud.entity.User;
import com.crud.repository.RoleRepository;
import com.crud.repository.UserRepository;
import com.crud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.crud.service.exception.ServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    /**
     * For work with role database
     */
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * For registration user and create profile
     * @param user to register user
     * @return user that have been registered
     */
    @Override
    @Transactional
    public User register(@Valid User user) {
        if (isUsernameUsed(user.getUsername())) {
            log.warn("IN register - haven't registered user with username : {}", user.getUsername());
            throw new ServiceException("Username is already exists");
        }

        User registeredUser = userRepository.save(buildUser(user));

        log.info("IN register - user successfully have been registered with username : {}", registeredUser.getUsername());
        return registeredUser;
    }

    private User buildUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> userRoles = roleRepository.findByName("ROLE_USER");
        user.setRoles(userRoles);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private boolean isUsernameUsed(String username) {
        try {
            findByUsername(username);
            return true;
        } catch (ServiceException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);

        if (result == null) {
            log.warn("IN findByUsername - haven't founded user by username: {}", username);
            throw new ServiceException("User haven't founded by username : " + username);
        }

        log.info("IN findByUsername - have founded user by username: {}", username);
        return result;
    }


    /**
     * For finding user by id
     * @param id to find user
     * @return user that have been founded
     */
    @Override
    @Transactional
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.warn("IN findById - haven't founded user by id: {}", id);
            throw new ServiceException("User haven't been founded by id : " + id);
        }
        User result = user.get();
        log.info("IN findById - have founded user by id: {}", result.getId());
        return result;
    }
}
