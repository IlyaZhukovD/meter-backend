package com.example.meters.service.impl;

import com.example.meters.entity.User;
import com.example.meters.service.JwtService;
import com.example.meters.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtUtil jwtUtil;

    @Override
    public String generateAccessToken(User user) {
        return jwtUtil.generateAccessToken(user.getId(), user.getLogin());
    }

    @Override
    public String generateRefreshToken(User user) {
        return jwtUtil.generateRefreshToken(user.getId(), user.getLogin());
    }

    @Override
    public String extractLogin(String token) {
        return jwtUtil.extractLogin(token);
    }

    @Override
    public boolean isValid(String token) {
        try {
            String login = jwtUtil.extractLogin(token);
            return login != null && jwtUtil.validateToken(token, login);
        } catch (Exception e) {
            return false;
        }
    }
}
