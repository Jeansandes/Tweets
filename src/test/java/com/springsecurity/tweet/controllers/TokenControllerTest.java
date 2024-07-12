package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.dtos.LoginRequest;
import com.springsecurity.tweet.dtos.LoginResponse;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenControllerTest {

    @InjectMocks
    private TokenController tokenController;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private JwtDecoder jwtDecoder;
    @Mock
    private TokenService tokenService;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
       loginRequest = new LoginRequest("jean","123");
       loginResponse = new LoginResponse("token", 300l);
    }

    @Test
    void whenLoginThenReturnOk() {
        when(tokenService.checkUser(loginRequest)).thenReturn(loginResponse);

        var response = tokenController.login(loginRequest);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        assertEquals(loginResponse.accessToken(),response.getBody().accessToken());
        assertEquals(loginResponse.expiresIn(),response.getBody().expiresIn());
        verify(tokenService, times(1)).checkUser(loginRequest);
        verifyNoMoreInteractions(tokenService);
    }
}