package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.dtos.FeedItemDto;
import com.springsecurity.tweet.dtos.TweetRequest;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.dtos.UserNameDto;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.services.UserService;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Mock
    private JwtAuthenticationToken token;

    private UserDto userDto;
    private List<UserModel> users;
    private UserNameDto userNameDto;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        userNameDto = new UserNameDto("jean");
       userDto = new UserDto("jean","sandesjean@gmail.com","123");
       users = List.of(new UserModel("jean","sandesjean@gmail.com","123"));
    }
    @Test
    void whenCreateUserThenReturnOk() {
        doNothing().when(userService).save(userDto);

        var response = userController.createUser(userDto);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(userService, times(1)).save(userDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void whenListUsersThenReturnOk() {
        when(userService.findAll()).thenReturn(users);

        var response = userController.ListUsers();

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());
        assertEquals(users.size(),response.getBody().size());
        assertEquals(users.get(0).getUsername(),response.getBody().get(0).getUsername());
        verify(userService, times(1)).findAll();
        verifyNoMoreInteractions(userService);
    }
    @Test
    void whenDeleteUserThenReturnOk(){
        String msgTrue = "excluido com sucesso :" + userNameDto.username();
        when(userService.delete(userNameDto.username(), token)).thenReturn(true);

        var response = userController.deleteUser(userNameDto,token);

        assertEquals(msgTrue, response.getBody());
        assertEquals(ResponseEntity.ok().build().getStatusCode(),response.getStatusCode());
        verify(userService, times(1)).delete(userNameDto.username(), token);
        verifyNoMoreInteractions(userService);

    }
    @Test
    void whenDeleteUserThenReturnFORBIDEN(){
        String msgFalse = "VOCE NÃO TEM PERMISSÃO PARA EXCLUIR";
        when(userService.delete(userNameDto.username(), token)).thenReturn(false);

        var response = userController.deleteUser(userNameDto,token);

        assertEquals(msgFalse, response.getBody());
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());
        verify(userService, times(1)).delete(userNameDto.username(), token);
        verifyNoMoreInteractions(userService);

    }
}