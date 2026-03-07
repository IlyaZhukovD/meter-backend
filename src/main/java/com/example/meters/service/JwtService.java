package com.example.meters.service;

import com.example.meters.entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractLogin(String token);
    boolean isValid(String token);
}
