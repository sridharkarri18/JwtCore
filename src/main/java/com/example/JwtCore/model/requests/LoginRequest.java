package com.example.JwtCore.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {


    @NotBlank(message = "email cannot be empty")
    @NotNull(message = "email cannot be null")
    private String email;

    @NotBlank(message = "password cannot be empty")
    @NotNull(message = "password cannot be null")
    private String password;
}
