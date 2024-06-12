package com.springsecurity.tweet.services;

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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String USERNAME = "admin";
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
    UserModel userAdmin;
    UserDto userDto;
    Role basicRole;
    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();

    }

    @Test
    void whenSaveThenReturnCreated() {
        when(roleRepository.findByName(any())).thenReturn(basicRole);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("passwordEncoder");

        userService.save(userDto);

        verify(userRepository, times(1)).findByUsername(userDto.username());
        verify(userRepository, times(1)).save(userAdmin);
        verify(userRepository, times(1)).save(argThat(user -> user.getPassword().equals(PASSWORDENCODER)));
        verify(userRepository, times(1)).save(argThat(userl -> userl.getRoles().contains(basicRole)));

    }

    @Test
    void findAll() {
    }


    private void startContent() {
        userAdmin = new UserModel(ID, USERNAME, PASSWORD, ROLEADMIN);
        userDto = new UserDto(USERNAME, PASSWORD);
        basicRole = new Role(2L,"basic");
    }
}





















