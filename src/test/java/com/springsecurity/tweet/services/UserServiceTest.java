package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.UserAlreadyExistsException;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.RoleRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String USERNAME = "admin";
    private static final String USERNAMEBASIC = "jean";
    private static final String PASSWORD = "123";
    private static final Role ROLEADMIN = new Role();
    private static final String PASSWORDENCODER = "passwordEncoder";
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    private UserModel userAdmin;
    private UserModel userBasic;
    private UserDto userDto;
    private Role basicRole = new Role();
    private Role adminRole = new Role();
    private List<UserModel> users;

    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    @Test
    void whenSaveThenReturnCreated() {
        when(roleRepository.findByName(any())).thenReturn(adminRole);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("passwordEncoder");

        userService.save(userDto);


       verify(userRepository, times(1)).findByUsername(userDto.username());
       verify(userRepository,times(1)).save(argThat(userModel -> userModel.getPassword().equals(PASSWORDENCODER)));
       verify(userRepository,times(1)).save(argThat(userModel -> userModel.getRoles().contains(adminRole)));


    }
    @Test
    void whenSaveThenRetrunUserAlreadyExistsException(){
        when(roleRepository.findByName(any())).thenReturn(basicRole);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userAdmin));
        try {
            userService.save(userDto);
        }catch (Exception ex){
            assertEquals(UserAlreadyExistsException.class, ex.getClass());
            assertEquals("usuário já existe!",ex.getMessage());
        }
    }

    @Test
    void whenFindAlThenReturnFindAlll() {
        when(userRepository.findAll()).thenReturn(users);
        List<UserModel> response = userService.findAll();

        assertEquals(2, response.size());
        assertEquals(USERNAME, response.get(0).getUsername());
        assertEquals(USERNAME, response.get(1).getUsername());
        assertEquals(PASSWORDENCODER, response.get(0).getPassword());
        assertEquals(PASSWORDENCODER, response.get(1).getPassword());
    }


    private void startContent() {
        basicRole.setName(Role.Values.BASIC.name());
        adminRole.setName(Role.Values.ADMIN.name());
        userAdmin = new UserModel(USERNAME, PASSWORDENCODER);
        userAdmin.setUserId(ID);
        userAdmin.setRoles(Set.of(basicRole));
        userBasic = new UserModel(USERNAME, PASSWORDENCODER);
        userBasic.setUserId(ID);
        userBasic.setRoles(Set.of(basicRole));
        userDto = new UserDto(USERNAME, PASSWORD);
        users = Arrays.asList(userAdmin,userBasic);
    }
}





















