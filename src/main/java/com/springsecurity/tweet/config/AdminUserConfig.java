package com.springsecurity.tweet.config;

import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.RoleRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import com.springsecurity.tweet.services.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {
   // private ModelMapper mapper;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;


    public AdminUserConfig(UserRepository userRepository,RoleRepository roleRepository,BCryptPasswordEncoder passwordEncoder,UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        int partition = 1;
        var role = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");
        userAdmin.ifPresentOrElse(
                user -> {
                    //userRepository.delete(user);
                    System.out.println("admin já existe!");
                },
                () -> {
                    var user = new UserModel();
                    user.setUsername("admin");
                    user.setEmail("sandesjean.sandes@gmail.com");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(role));

                    userRepository.save(user);
                    UserDto dto =new UserDto(user.getUsername(),user.getEmail(),user.getPassword());
                    userService.sendMessage(dto,partition);
                }
        );
    }
}













