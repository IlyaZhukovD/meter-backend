package com.example.meters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    private String login;

    @NotBlank
    @Size(min = 8)
    private String password;
}
