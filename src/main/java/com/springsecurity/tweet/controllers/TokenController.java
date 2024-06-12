package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.dtos.LoginRequest;
import com.springsecurity.tweet.dtos.LoginResponse;
import com.springsecurity.tweet.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    private TokenService tokenService;

    public TokenController(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenService tokenService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var loginResponse = tokenService.checkUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

}
















