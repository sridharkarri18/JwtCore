package com.example.JwtCore.model.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String email;

    private String token;

    private String refreshToken;
}
