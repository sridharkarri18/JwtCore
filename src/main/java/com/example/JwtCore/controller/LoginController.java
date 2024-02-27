package com.example.JwtCore.controller;

import com.example.JwtCore.exceptions.UserDefinedException;
import com.example.JwtCore.model.requests.AccessTokenRequest;
import com.example.JwtCore.exceptions.CustomException;
import com.example.JwtCore.model.requests.LoginRequest;
import com.example.JwtCore.model.responses.LoginResponse;
import com.example.JwtCore.model.responses.RefreshTokenResponse;
import com.example.JwtCore.security.JwtService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public LoginResponse signUp(@Valid @RequestBody LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        if (authentication.isAuthenticated()) {
            String generatedToken = jwtService.generateToken(loginRequest.getEmail());
            return LoginResponse.builder()
                    .email(loginRequest.getEmail())
                    .token(generatedToken)
                    .refreshToken(jwtService.generateJwtRefreshToken(authentication))
                    .build();
        } else {
            throw new BadCredentialsException("Invalid username and password");
        }
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody AccessTokenRequest tokenRequest) throws UserDefinedException, CustomException {
        return jwtService.validateRefreshToken(tokenRequest);
    }

}

