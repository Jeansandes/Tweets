package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.dtos.LoginRequest;
import com.springsecurity.tweet.dtos.LoginResponse;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class TokenServiceTest {

    private static final String USERNAMEBASIC = "jean";
    private static final String PASSWORDENCODER = "123";
    private static final String PASSWORDENCODER2 = "1223";
    private static final UUID ID = UUID.randomUUID();
    @InjectMocks
    TokenService tokenService;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private JwtDecoder jwtDecoder;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    private LoginRequest loginRequest;
    private LoginRequest loginRequest2;
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
        userBasic = new UserModel(USERNAMEBASIC, PASSWORDENCODER2);
        userBasic.setUserId(ID);
        userBasic.setRoles(Set.of(basicRole));
        loginRequest = new LoginRequest(USERNAMEBASIC,PASSWORDENCODER);
        loginRequest2 = new LoginRequest(USERNAMEBASIC,PASSWORDENCODER2);

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
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(userBasic));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        Map<String, Object> headers = Map.of("alg", "HS256");

        // Criando o objeto Jwt com cabeçalhos e claims válidos
        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plusSeconds(300), headers, claims.getClaims());
        when(jwtEncoder.encode(JwtEncoderParameters.from(claims))).thenReturn(jwt);
        System.out.println(jwt.getTokenValue());
        System.out.println(claims.getClaims());
      //  when(jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue()).thenReturn(tokenValue);

        LoginResponse response = tokenService.checkUser(loginRequest);

        System.out.println(response.accessToken());

        assertNotNull(response);
        assertEquals(tokenValue, response.accessToken());
       // assertEquals(300L, response.expiresIn());
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