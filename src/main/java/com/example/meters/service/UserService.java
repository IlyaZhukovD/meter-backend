package com.example.meters.service;

import com.example.meters.dto.RegisterRequest;
import com.example.meters.entity.User;
import java.util.Optional;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
    Optional<User> findByLogin(String login);
    User getUserById(Long id);
}
