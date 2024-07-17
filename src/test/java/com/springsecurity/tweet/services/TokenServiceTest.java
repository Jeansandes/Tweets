package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.dtos.LoginRequest;
import com.springsecurity.tweet.dtos.LoginResponse;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenServiceTest {

    private static final String USERNAMEBASIC = "jean";
    private static final String PASSWORDENCODER = "123";
    private static final String PASSWORDENCODER2 = "1223";
    private static final String EMAIL = "sandesjean@gmail.com";
    private static final UUID ID = UUID.randomUUID();
    @InjectMocks
    TokenService tokenService;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    private LoginRequest loginRequest;
    private  Role  basicRole= new Role();
    private UserModel userBasic;
    private String tokenValue = "mockedToken";
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        basicRole.setName(Role.Values.BASIC.name());
        userBasic = new UserModel(USERNAMEBASIC, EMAIL,PASSWORDENCODER2);
        userBasic.setUserId(ID);
        userBasic.setRoles(Set.of(basicRole));
        loginRequest = new LoginRequest(USERNAMEBASIC,PASSWORDENCODER);

    }

    @Test
    void whenCheckUserThenReturnLoginResponse(){
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(userBasic.getUserId().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300L))
                .claim("scope", "BASIC")
                .build();

        // Mocks
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(userBasic));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // Configuração do mock para jwtEncoder.encode
        Jwt jwtMock = mock(Jwt.class); // Mock de Jwt
        when(jwtMock.getTokenValue()).thenReturn("tokenValue"); // Define o comportamento esperado para getTokenValue()

        // Captura os parâmetros passados para jwtEncoder.encode e retorna o mock de Jwt diretamente
        when(jwtEncoder.encode(ArgumentMatchers.any())).thenAnswer(invocation -> {
            JwtEncoderParameters parameters = invocation.getArgument(0);
            // Aqui você pode validar os parâmetros se necessário
            return jwtMock;
        });

        // Chamada ao método checkUser do TokenService
        LoginResponse response = tokenService.checkUser(loginRequest);

        // Verificações
        assertNotNull(response);
        assertEquals("tokenValue", response.accessToken());
    }

    @Test
    void whenCheckUserThenReturnUserEmpty() {
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            tokenService.checkUser(loginRequest);
        });
    }
    @Test
    void whenCheckUserThenReturnPasswordInvalid() {
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(userBasic));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> {
            tokenService.checkUser(loginRequest);
        });
    }
}